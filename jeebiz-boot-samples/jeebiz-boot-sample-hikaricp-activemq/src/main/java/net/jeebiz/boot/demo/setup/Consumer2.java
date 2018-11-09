package net.jeebiz.boot.demo.setup;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 消息消费者.
 */
@Component
public class Consumer2 {

    @JmsListener(destination = "demo.topic")
    public void receiveQueue(String text) {

       System.out.println("Consumer2="+text);

    }

}