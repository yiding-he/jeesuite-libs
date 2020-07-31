/**
 *
 */
package com.jeesuite.kafka;

/**
 * 常量定义
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年5月1日
 */
public class KafkaConst {

    public final static String HEARTBEAT_TOPIC = "_kafka_heartBeat";

    public final static String ZK_CONSUMER_PATH = "/consumers/";

    public final static String ZK_PRODUCER_STAT_PATH = "/producers/statistics";

    public final static String COMMAND_GET_STATISTICS = "get_statistics";

    public final static String PROP_TOPIC_LAT_THRESHOLD = "topic.lat.threshold";

    public final static String PROP_ENV_ROUTE = "topic.route.env";

    public static String ZK_PRODUCER_ACK_PATH = "/producers/watcher/";
}
