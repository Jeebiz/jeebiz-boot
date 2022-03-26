package net.jeebiz.boot.extras.redis.setup.config;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

public interface MessageListenerAdapter extends MessageListener {

    void setMessageListenerContainer(RedisMessageListenerContainer container);

}
