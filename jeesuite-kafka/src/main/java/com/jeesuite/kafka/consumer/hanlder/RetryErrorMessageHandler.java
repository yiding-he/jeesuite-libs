package com.jeesuite.kafka.consumer.hanlder;

import com.jeesuite.kafka.message.DefaultMessage;

/**
 * 消费错误消息处理接口(自动重试后失败则触发)
 *
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2017年11月27日
 */
public interface RetryErrorMessageHandler {

    public void process(String group, String topic, DefaultMessage message);

}
