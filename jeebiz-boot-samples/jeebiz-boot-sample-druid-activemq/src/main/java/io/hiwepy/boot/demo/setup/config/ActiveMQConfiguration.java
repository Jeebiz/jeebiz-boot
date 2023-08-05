package io.hiwepy.boot.demo.setup.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;
import javax.jms.Topic;

@Configuration
public class ActiveMQConfiguration {

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("demo.queue");
    }

    @Bean
    public Topic topic() {
        return new ActiveMQTopic("demo.topic");
    }

}
