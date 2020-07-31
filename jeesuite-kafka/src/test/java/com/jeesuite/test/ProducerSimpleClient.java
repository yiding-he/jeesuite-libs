package com.jeesuite.test;

import com.jeesuite.common.json.JsonUtils;
import com.jeesuite.kafka.message.DefaultMessage;
import com.jeesuite.kafka.spring.TopicProducerSpringProvider;
import org.I0Itec.zkclient.ZkClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-kafka-producer.xml")
public class ProducerSimpleClient implements ApplicationContextAware {

    @Autowired
    private TopicProducerSpringProvider topicProducer;

    @Test
    public void testPublish() throws InterruptedException {
        //默认模式（异步/ ）发送
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setId(100 + i);
            user.setName("jack");
            topicProducer.publish("app-union-logs", new DefaultMessage(JsonUtils.toJson(user)).sendBodyOnly(true));
            //topicProducer.publish("demo-topic2", new DefaultMessage(user));
            //topicProducer.publishNoWrapperMessage("streams-plaintext-input", "hello " + i);
        }
//		
//		DefaultMessage msg = new DefaultMessage("hello,man")
//		            .header("headerkey1", "headerval1")//写入header信息
//		            .header("headerkey1", "headerval1")//写入header信息
//		            .partitionFactor(1000) //分区因子，譬如userId＝1000的将发送到同一分区、从而发送到消费者的同一节点(有状态)
//		            .consumerAck(true);// 已消费回执(未发布)
//		

//		User user = new User();
//		user.setAge(17);
//		user.setId(1);
//		user.setName("kafka");
//		//异步发送
//		topicProducer.publishNoWrapperMessage("demo-topic", JsonUtils.toJson(user),true);

        Thread.sleep(5000);

    }


    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
    }

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("127.0.0.1:2181");
        System.out.println(zkClient.exists("/aa"));
    }
}
