package io.hiwepy.boot.demo.message;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 消息消费者.
 */
@Component
public class Consumer3 {

    @JmsListener(destination = "demo.topic")
    public void receiveQueue(String text) {

        System.out.println("Consumer3=" + text);

    }

}