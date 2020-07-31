package com.jeesuite.kafka.spring;

import com.jeesuite.common.util.NodeNameHolder;
import com.jeesuite.common.util.ResourceUtils;
import com.jeesuite.kafka.KafkaConst;
import com.jeesuite.kafka.annotation.ConsumerHandler;
import com.jeesuite.kafka.consumer.*;
import com.jeesuite.kafka.consumer.hanlder.OffsetLogHanlder;
import com.jeesuite.kafka.consumer.hanlder.RetryErrorMessageHandler;
import com.jeesuite.kafka.handler.MessageHandler;
import com.jeesuite.kafka.serializer.KyroMessageDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 消息订阅者集成spring封装对象
 *
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年6月25日
 */
public class TopicConsumerSpringProvider implements InitializingBean, DisposableBean, ApplicationContextAware, PriorityOrdered {

    private final static Logger logger = LoggerFactory.getLogger(TopicConsumerSpringProvider.class);

    private ApplicationContext context;

    private TopicConsumer consumer;

    /**
     * 配置
     */
    private Properties configs;

    //是否独立进程
    private boolean independent;

    private boolean useNewAPI = true;

    private String scanPackages;

    private Map<String, MessageHandler> topicHandlers = new HashMap<>();

    private int processThreads = 200;

    private String groupId;

    private String consumerId;

    //标记状态（0：未运行，1：启动中，2：运行中，3：停止中，4：重启中）
    private AtomicInteger status = new AtomicInteger(0);

    //环境路由
    private String routeEnv;

    private OffsetLogHanlder offsetLogHanlder;

    private RetryErrorMessageHandler retryErrorMessageHandler;

    @Override
    public void afterPropertiesSet() throws Exception {

        if (StringUtils.isNotBlank(scanPackages)) {
            String[] packages = org.springframework.util.StringUtils.tokenizeToStringArray(this.scanPackages, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            scanAndRegisterAnnotationTopics(packages);
        }

        Validate.isTrue(topicHandlers != null && topicHandlers.size() > 0, "at latest one topic");
        //当前状态
        if (status.get() > 0) return;

        routeEnv = StringUtils.trimToNull(ResourceUtils.getProperty(KafkaConst.PROP_ENV_ROUTE));

        if (routeEnv != null) {
            logger.info("current route Env value is:", routeEnv);
            Map<String, MessageHandler> newTopicHandlers = new HashMap<>();
            for (String origTopicName : topicHandlers.keySet()) {
                newTopicHandlers.put(routeEnv + "." + origTopicName, topicHandlers.get(origTopicName));
            }
            topicHandlers = newTopicHandlers;
        }

        //make sure that rebalance.max.retries * rebalance.backoff.ms > zookeeper.session.timeout.ms.
        configs.put("rebalance.max.retries", "5");
        configs.put("rebalance.backoff.ms", "1205");
        configs.put("zookeeper.session.timeout.ms", "6000");

        configs.put("key.deserializer", StringDeserializer.class.getName());

        if (!configs.containsKey("value.deserializer")) {
            configs.put("value.deserializer", KyroMessageDeserializer.class.getName());
        }

        if (useNewAPI) {
            if ("smallest".equals(configs.getProperty("auto.offset.reset"))) {
                configs.put("auto.offset.reset", "earliest");
            } else if ("largest".equals(configs.getProperty("auto.offset.reset"))) {
                configs.put("auto.offset.reset", "latest");
            }
        } else {
            //强制自动提交
            configs.put("enable.auto.commit", "true");
        }

        //同步节点信息
        groupId = configs.get(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG).toString();

        logger.info("\n===============KAFKA Consumer group[{}] begin start=================\n", groupId);

        consumerId = NodeNameHolder.getNodeId();
        //
        configs.put("consumer.id", consumerId);

        //kafka 内部处理 consumerId ＝ groupId + "_" + consumerId
        consumerId = groupId + "_" + consumerId;
        //
        if (!configs.containsKey("client.id")) {
            configs.put("client.id", consumerId);
        }
        //
        start();

        logger.info("\n===============KAFKA Consumer group[{}],consumerId[{}] start finished!!=================\n", groupId, consumerId);
    }


    /**
     * 启动
     */
    private void start() {
        if (independent) {
            logger.info("KAFKA 启动模式[independent]");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    registerKafkaSubscriber();
                }
            }).start();
        } else {
            registerKafkaSubscriber();
        }
    }


    /**
     *
     */
    @SuppressWarnings("rawtypes")
    private void registerKafkaSubscriber() {
        //状态：启动中
        status.set(1);

        Validate.notEmpty(this.configs, "configs is required");
        Validate.notEmpty(this.configs.getProperty("group.id"), "kafka configs[group.id] is required");
        Validate.notEmpty(this.configs.getProperty("bootstrap.servers"), "kafka configs[bootstrap.servers] is required");

        StringBuffer sb = new StringBuffer();
        Iterator itr = this.configs.entrySet().iterator();
        while (itr.hasNext()) {
            Entry e = (Entry) itr.next();
            sb.append(e.getKey()).append("  =  ").append(e.getValue()).append("\n");
        }
        logger.info("\n============kafka.Consumer.Config============\n" + sb.toString() + "\n");

        ConsumerContext consumerContext = ConsumerContext.getInstance();
        int retryNums = ResourceUtils.getInt("message.consumer.retry.counts", 3);
        consumerContext.propertiesSetIfAbsent(configs, groupId, consumerId, topicHandlers, processThreads, offsetLogHanlder, new ErrorMessageProcessor(1, 10, retryNums, retryErrorMessageHandler));

        if (useNewAPI) {
            consumer = new NewApiTopicConsumer(consumerContext);
        } else {
            consumer = new OldApiTopicConsumer(consumerContext);
        }

        //TODO 确保spring全部加载完成再start
        consumer.start();
        //状态：运行中
        status.set(2);
    }


    /**
     * kafka 配置
     *
     * @param configs kafka 配置
     */
    public void setConfigs(Properties configs) {
        this.configs = configs;
    }

    public void setTopicHandlers(Map<String, MessageHandler> topicHandlers) {
        this.topicHandlers = topicHandlers;
    }

    public void setIndependent(boolean independent) {
        this.independent = independent;
    }

    public void setProcessThreads(int processThreads) {
        this.processThreads = processThreads;
    }

    public void setUseNewAPI(boolean useNewAPI) {
        this.useNewAPI = useNewAPI;
    }

    public void setOffsetLogHanlder(OffsetLogHanlder offsetLogHanlder) {
        this.offsetLogHanlder = offsetLogHanlder;
    }

    public void setScanPackages(String scanPackages) {
        this.scanPackages = scanPackages;
    }

    public void setRetryErrorMessageHandler(RetryErrorMessageHandler retryErrorMessageHandler) {
        this.retryErrorMessageHandler = retryErrorMessageHandler;
    }

    @Override
    public void destroy() throws Exception {
        consumer.close();
    }

    private void scanAndRegisterAnnotationTopics(String[] scanBasePackages) {
        String RESOURCE_PATTERN = "/**/*.class";

        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        for (String scanBasePackage : scanBasePackages) {
            logger.info(">>begin scan package [{}] with Annotation[ConsumerHandler] MessageHanlder ", scanBasePackage);
            try {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(scanBasePackage)
                        + RESOURCE_PATTERN;
                org.springframework.core.io.Resource[] resources = resourcePatternResolver.getResources(pattern);
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                for (org.springframework.core.io.Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader reader = readerFactory.getMetadataReader(resource);
                        String className = reader.getClassMetadata().getClassName();
                        Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(ConsumerHandler.class)) {
                            ConsumerHandler annotation = clazz.getAnnotation(ConsumerHandler.class);
                            MessageHandler hander = (MessageHandler) context.getBean(clazz);
                            if (!topicHandlers.containsKey(annotation.topic())) {
                                topicHandlers.put(annotation.topic(), hander);
                                logger.info("register new MessageHandler:{}-{}", annotation.topic(), clazz.getName());
                            }
                        }
                    }
                }
                logger.info("<<scan package[" + scanBasePackage + "] finished!");
            } catch (Exception e) {
                if (e instanceof org.springframework.beans.factory.NoSuchBeanDefinitionException) {
                    throw (org.springframework.beans.factory.NoSuchBeanDefinitionException) e;
                }
                logger.error("<<scan package[" + scanBasePackage + "] error", e);
            }
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE + 1;
    }

}
