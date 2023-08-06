package io.hiwepy.boot.sample.message;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class LogConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogConsumer.class);

    @JmsListener(destination = QueueName.LOG_QUEUE)
    public void receivedQueue(String msg) {
        LOGGER.info("Has received from " + QueueName.LOG_QUEUE + ", msg: " + msg);
    }
}
