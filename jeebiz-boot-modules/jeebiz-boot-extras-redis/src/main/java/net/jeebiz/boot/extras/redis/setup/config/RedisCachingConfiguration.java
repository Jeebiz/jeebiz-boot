package net.jeebiz.boot.extras.redis.setup.config;

import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.jeebiz.boot.api.annotation.RedisTopic;
import net.jeebiz.boot.api.utils.CollectionUtils;
import net.jeebiz.boot.api.utils.StringUtils;
import net.jeebiz.boot.extras.redis.setup.RedisKey;
import net.jeebiz.boot.extras.redis.setup.RedisOperationTemplate;
import net.jeebiz.boot.extras.redis.setup.RedissonOperationTemplate;
import net.jeebiz.boot.extras.redis.setup.geo.GeoTemplate;

/**
 * Reids 相关bean的配置 
 * https://www.cnblogs.com/liuyp-ken/p/10538658.html
 * https://www.cnblogs.com/aoeiuv/p/6760798.html
 */
@Configuration
@EnableCaching(proxyTargetClass = true)
public class RedisCachingConfiguration extends CachingConfigurerSupport {
	
	public final static String MESSAGE_TOPIC = "message.topic";
	
	@Bean
	public Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
		// 使用Jackson2JsonRedisSerialize 替换默认序列化
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
				Object.class);

		ObjectMapper objectMapper = new ObjectMapper();
		// 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		// 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

		return jackson2JsonRedisSerializer;
	}
	
	@Bean(name = "reactiveRedisTemplate")
	@ConditionalOnBean(ReactiveRedisConnectionFactory.class)
	public ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate(
			ReactiveRedisConnectionFactory reactiveRedisConnectionFactory,
			Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer) {
        
		RedisSerializationContext<Object, Object> serializationContext = RedisSerializationContext
				.newSerializationContext()
				 // 设置value的序列化规则和 key的序列化规则
				.key(jackson2JsonRedisSerializer)
				.value(jackson2JsonRedisSerializer)
				 // 设置hash key 和 hash value序列化模式
				.hashKey(jackson2JsonRedisSerializer)
				.hashValue(jackson2JsonRedisSerializer)
				.build();
		
		return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
	}
 
	/**
	 * redisTemplate 序列化使用的jdkSerializeable, 存储二进制字节码, 所以自定义序列化类
	 * 
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
			Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer) throws UnknownHostException {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);

		// 设置value的序列化规则和 key的序列化规则

		// 使用StringRedisSerializer来序列化和反序列化redis的key值
		redisTemplate.setKeySerializer(RedisSerializer.string());
		// 值采用json序列化
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

		// 设置hash key 和value序列化模式
		redisTemplate.setHashKeySerializer(RedisSerializer.string());
		redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
		redisTemplate.afterPropertiesSet();

		return redisTemplate;
	}

	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory)
			throws UnknownHostException {
		StringRedisTemplate redisTemplate = new StringRedisTemplate();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setEnableTransactionSupport(true);
		return redisTemplate;
	}
	
	@Bean
	@Order(1)
	public RedisOperationTemplate redisOperationTemplate(RedisTemplate<String, Object> redisTemplate) {
		return new RedisOperationTemplate(redisTemplate);
	}
	
	@Bean
	@Order(1)
	public RedissonOperationTemplate redissonOperationTemplate(RedissonClient redissonClient) {
		return new RedissonOperationTemplate(redissonClient);
	}
	
	@Bean
	public GeoTemplate geoTemplate(RedisTemplate<String, Object> redisTemplate) {
		return new GeoTemplate(redisTemplate, RedisKey.USER_GEO_LOCATION.getFunction().apply(null));
	}

	/**
	 * redis消息监听器容器 可以添加多个监听不同话题的redis监听器，只需要把消息监听器和相应的消息订阅处理器绑定，该消息监听器
	 * 通过反射技术调用消息订阅处理器的相关方法进行一些业务处理
	 * 
	 * @param connectionFactory
	 * @param messageListenerProvider
	 * @return
	 */
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
			ObjectProvider<MessageListener> messageListenerProvider,
			Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		// 订阅多个频道
		List<MessageListener> messageListeners = messageListenerProvider.orderedStream().collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(messageListeners)) {
			for (MessageListener messageListener : messageListeners) {
				// 查找注解
				RedisTopic topic = AnnotationUtils.findAnnotation(messageListener.getClass(), RedisTopic.class);
				if (topic != null) {
					if (StringUtils.hasText(topic.channel())) {
						container.addMessageListener(messageListener, new ChannelTopic(topic.channel()));
					} else if (StringUtils.hasText(topic.pattern())) {
						container.addMessageListener(messageListener, new PatternTopic(topic.pattern()));
					}
				}
			}
		}
		// 序列化对象（特别注意：发布的时候需要设置序列化；订阅方也需要设置序列化）
		container.setTopicSerializer(RedisSerializer.string());

		return container;
	}
}