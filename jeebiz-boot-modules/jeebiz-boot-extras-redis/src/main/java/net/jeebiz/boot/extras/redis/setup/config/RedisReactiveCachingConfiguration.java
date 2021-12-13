package net.jeebiz.boot.extras.redis.setup.config;

import net.jeebiz.boot.extras.redis.setup.ReactiveRedisOperationTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;


import reactor.core.publisher.Flux;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ ReactiveRedisConnectionFactory.class, ReactiveRedisTemplate.class, Flux.class })
@AutoConfigureAfter(RedisAutoConfiguration.class)
@AutoConfigureBefore(RedisReactiveAutoConfiguration.class)
public class RedisReactiveCachingConfiguration {

	@Bean(name = "reactiveRedisTemplate")
	public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
			ReactiveRedisConnectionFactory reactiveRedisConnectionFactory,
			Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer) {

		SerializationPair<String> keySerializationPair = SerializationPair.fromSerializer(new RedisSerializer<String>(){

			@Override
			public byte[] serialize(String t) throws SerializationException {
				return StringRedisSerializer.UTF_8.serialize(t.toString());
			}

			@Override
			public String deserialize(byte[] bytes) throws SerializationException {
				return StringRedisSerializer.UTF_8.deserialize(bytes);
			}}
		);

		RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
				.<String, Object>newSerializationContext()
				 // 设置value的序列化规则和 key的序列化规则
				.key(keySerializationPair)
				.value(jackson2JsonRedisSerializer)
				 // 设置hash key 和 hash value序列化模式
				.hashKey(keySerializationPair)
				.hashValue(jackson2JsonRedisSerializer)
				.build();

		return new ReactiveRedisTemplate<String, Object>(reactiveRedisConnectionFactory, serializationContext);

	}

	@Bean
	public ReactiveRedisOperationTemplate reactiveRedisOperationTemplate(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
		return new ReactiveRedisOperationTemplate(reactiveRedisTemplate);
	}

}
