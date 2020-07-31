package com.jeesuite.test.sch;

import com.jeesuite.spring.InstanceFactory;
import com.jeesuite.spring.SpringInstanceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;


public class TaskServerNode2 {

    private static Logger logger = LoggerFactory.getLogger(TaskServerNode2.class);

    public static void main(String[] args) throws InterruptedException {

        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("test-schduler.xml");

        InstanceFactory.setInstanceProvider(new SpringInstanceProvider(context));

        logger.info("TASK started....");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                logger.info("TASK Stoped....");
                context.close();
            }
        }));

        Scanner scan = new Scanner(System.in);
        String cmd = scan.next();
        if ("q".equals(cmd)) {
            scan.close();
            context.close();
        }

    }
}
