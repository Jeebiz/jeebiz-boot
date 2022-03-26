package net.jeebiz.boot.extras.redis.setup.config;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import net.jeebiz.boot.extras.redis.annotation.RedisChannelTopic;
import net.jeebiz.boot.extras.redis.annotation.RedisPatternTopic;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.biz.utils.AnnotationUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.jeebiz.boot.api.annotation.RedisTopic;
import net.jeebiz.boot.extras.redis.setup.RedisKey;
import net.jeebiz.boot.extras.redis.setup.RedisOperationTemplate;
import net.jeebiz.boot.extras.redis.setup.geo.GeoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Reids 相关bean的配置
 * https://www.cnblogs.com/liuyp-ken/p/10538658.html
 * https://www.cnblogs.com/aoeiuv/p/6760798.html
 */

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisOperations.class)
@EnableCaching(proxyTargetClass = true)
@EnableConfigurationProperties(RedisExecutionProperties.class)
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
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);


		return jackson2JsonRedisSerializer;
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
	public GeoTemplate geoTemplate(RedisTemplate<String, Object> redisTemplate) {
		return new GeoTemplate(redisTemplate);
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
																	   ObjectProvider<MessageListenerAdapter> messageListenerProvider,
																	   RedisExecutionProperties redisExecutionProperties) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		// 订阅多个频道
		List<MessageListenerAdapter> messageListenerAdapters = messageListenerProvider.orderedStream().collect(Collectors.toList());
		if (!CollectionUtils.isEmpty(messageListenerAdapters)) {
			for (MessageListenerAdapter messageListener : messageListenerAdapters) {
				// 查找注解
				RedisChannelTopic channel = org.springframework.biz.utils.AnnotationUtils.findAnnotation(messageListener.getClass(), RedisChannelTopic.class);
				if (Objects.nonNull(channel) && org.springframework.util.StringUtils.hasText(channel.value())){
					container.addMessageListener(messageListener, new ChannelTopic(channel.value()));
					continue;
				}
				RedisPatternTopic pattern = AnnotationUtils.findAnnotation(messageListener.getClass(), RedisPatternTopic.class);
				if (Objects.nonNull(pattern) && StringUtils.hasText(pattern.value())){
					container.addMessageListener(messageListener, new PatternTopic(pattern.value()));
				}
			}
		}
		// 序列化对象（特别注意：发布的时候需要设置序列化；订阅方也需要设置序列化）
		container.setTopicSerializer(RedisSerializer.string());
		// 设置接收消息时用于运行消息侦听器的任务执行器
		container.setTaskExecutor(redisThreadPoolTaskExecutor(redisExecutionProperties.getListener()));
		// 设置Redis频道订阅的任务执行器
		container.setSubscriptionExecutor(redisThreadPoolTaskExecutor(redisExecutionProperties.getSubscription()));
		return container;
	}

	protected ThreadPoolTaskExecutor redisThreadPoolTaskExecutor(RedisExecutionProperties.Pool pool){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// 核心线程数
		executor.setCorePoolSize(pool.getCoreSize());
		// 最大线程数
		executor.setMaxPoolSize(pool.getMaxSize());
		// 任务队列的大小
		executor.setQueueCapacity(pool.getQueueCapacity());
		// 线程存活时间
		executor.setKeepAliveSeconds(Long.valueOf(pool.getKeepAlive().getSeconds()).intValue());
		// 线程前缀名
		executor.setThreadNamePrefix(pool.getThreadNamePrefix());
		/**
		 * 拒绝处理策略
		 * CallerRunsPolicy()：交由调用方线程运行，比如 main 线程。
		 * AbortPolicy()：直接抛出异常。
		 * DiscardPolicy()：直接丢弃。
		 * DiscardOldestPolicy()：丢弃队列中最老的任务。
		 */
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		// 线程初始化
		executor.initialize();
		return executor;
	}

}
