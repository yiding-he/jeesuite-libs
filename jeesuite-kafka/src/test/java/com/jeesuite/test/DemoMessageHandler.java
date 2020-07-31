/**
 *
 */
package com.jeesuite.test;

import com.jeesuite.kafka.handler.MessageHandler;
import com.jeesuite.kafka.message.DefaultMessage;

import java.io.Serializable;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年6月25日
 */
public class DemoMessageHandler implements MessageHandler {


    @Override
    public void p1Process(DefaultMessage message) {
        //TODO
    }

    @Override
    public void p2Process(DefaultMessage message) {
        Serializable body = message.getBody();
        System.out.println(String.format("DemoMessageHandler process messageId:%s,body:%s", message.getMsgId(), body));
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
    }


    @Override
    public boolean onProcessError(DefaultMessage message) {
        System.out.println("ignore error message : " + message);
        return true;
    }

}
