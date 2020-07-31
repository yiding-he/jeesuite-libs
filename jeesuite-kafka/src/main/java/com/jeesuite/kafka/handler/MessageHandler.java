package com.jeesuite.kafka.handler;

import com.jeesuite.kafka.message.DefaultMessage;

/**
 * 消息处理器接口
 *
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年6月15日
 */
public interface MessageHandler {

    /**
     * 第一阶段处理<br>
     * 第一阶段是同步处理，保证实时性（如：写缓存后保证实时读取）
     */
    void p1Process(DefaultMessage message);

    /**
     * 第二阶段处理<br>
     * 第二阶段为异步处理，为后台任务
     */
    void p2Process(DefaultMessage message);

    /**
     * 处理失败的消息
     *
     * @return true 表示业务系统自己已经做了处理，false 由底层框架执行重新处理
     */
    boolean onProcessError(DefaultMessage message);
}
