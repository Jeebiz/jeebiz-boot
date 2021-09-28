package net.jeebiz.boot.extras.redis.setup.config;

import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import net.jeebiz.boot.extras.redis.setup.RedissonOperationTemplate;

@Configuration
@EnableCaching(proxyTargetClass = true)
public class RedissonCachingConfiguration {
	
	@Bean
	@Order(1)
	public RedissonOperationTemplate redissonOperationTemplate(RedissonClient redissonClient) {
		return new RedissonOperationTemplate(redissonClient);
	}
	 
}