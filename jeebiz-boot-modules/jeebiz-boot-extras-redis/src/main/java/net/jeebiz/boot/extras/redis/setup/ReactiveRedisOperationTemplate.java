package net.jeebiz.boot.extras.redis.setup;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisCallback;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import net.jeebiz.boot.api.exception.BizRuntimeException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Slf4j
public class ReactiveRedisOperationTemplate {

	private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
	private final ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate;

	public ReactiveRedisOperationTemplate(ReactiveRedisTemplate<Object, Object> reactiveRedisTemplate) {
		this.reactiveRedisTemplate = reactiveRedisTemplate;
	}

	// =============================common============================

	/**
	 * 指定缓存失效时间
	 * @param key  键
	 * @param time 时间(秒)
	 * @return
	 */
	public Mono<Boolean> expire(String key, long seconds) {
		if (seconds <= 0) {
			return Mono.just(Boolean.FALSE);
		}
		return this.expire(key, Duration.ofSeconds(seconds));
	}
	
	public Mono<Boolean> expire(String key, Duration duration) {
		if (Objects.isNull(duration) || duration.isNegative()) {
			return Mono.just(Boolean.FALSE);
		}
		try {
			return reactiveRedisTemplate.expire(key, duration);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}
	
	public Mono<Boolean> expireAt(String key, Instant expireAt) {
		if (Objects.isNull(expireAt)) {
			return Mono.just(Boolean.FALSE);
		}
		try {
			return reactiveRedisTemplate.expireAt(key, expireAt);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}

	/**
	 * 根据key 获取过期时间
	 * @param key 键 不能为null
	 * @return 时间(秒) 返回0代表为永久有效
	 */
	public Mono<Duration> getExpire(String key) {
		return reactiveRedisTemplate.getExpire(key);
	}

	/**
	 * 判断key是否存在
	 *
	 * @param key 键
	 * @return true 存在 false不存在
	 */
	public Mono<Boolean> hasKey(String key) {
		try {
			return reactiveRedisTemplate.hasKey(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}

	// 模糊匹配缓存中的key
	public Flux<Object> getKey(String pattern) {
		if (Objects.isNull(pattern)) {
			return Flux.empty();
		}
		return reactiveRedisTemplate.keys(pattern);
	}

	// 模糊匹配缓存中的key
	public Flux<Object> getVagueKey(String pattern) {
		return reactiveRedisTemplate.keys("*" + pattern + "*");
	}

	public Flux<Object> getValueKeyByPrefix(String prefixPattern) {
		return reactiveRedisTemplate.keys(prefixPattern + "*");
	}
	
	/**
	 * 删除缓存
	 * @param keys 可以传一个值 或多个
	 */
	public Mono<Long> delete(String... keys) {
		try {
			if (keys != null && keys.length > 0) {
				if (keys.length == 1) {
					return reactiveRedisTemplate.delete(keys[0]);
				} else {
					return reactiveRedisTemplate.delete(CollectionUtils.arrayToList(keys));
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return Mono.just(0L);
	}

	public <K> ByteBuffer rawKey(K key) {
		return reactiveRedisTemplate.getSerializationContext().getKeySerializationPair().write(key);
	}

	public <V> ByteBuffer rawValue(V value) {
		return reactiveRedisTemplate.getSerializationContext().getValueSerializationPair().write(value);
	}
	
	// ============================String=============================


	/**
	 * 普通缓存放入
	 *
	 * @param key   键
	 * @param value 值
	 * @return true成功 false失败
	 */
	public Mono<Boolean> set(String key, Object value) {
		try {
			return reactiveRedisTemplate.opsForValue().set(key, value);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}

	/**
	 * 普通缓存放入并设置时间
	 *
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒) time要>=0 如果time小于等于0 将设置无限期
	 * @return true成功 false 失败
	 */
	public Mono<Boolean> set(String key, Object value, long seconds) {
		try {
			if (seconds > 0) {
				return reactiveRedisTemplate.opsForValue().set(key, value, Duration.ofSeconds(seconds));
			} else {
				return set(key, value);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}
	
	/**
	 * 普通缓存放入并设置时间
	 *
	 * @param key   键
	 * @param value 值
	 * @param duration  时间
	 * @return true成功 false 失败
	 */
	public Mono<Boolean> set(String key, Object value, Duration duration) {
		if (Objects.isNull(duration) || duration.isNegative()) {
			return Mono.just(Boolean.FALSE);
		}
		try {
			return reactiveRedisTemplate.opsForValue().set(key, value, duration);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}

	public Mono<Boolean> setNX(String key, String value) {
		try {
			// reactiveRedisTemplate.opsForValue().setIfAbsent(key, value);
			return reactiveRedisTemplate.createMono((ReactiveRedisCallback<Boolean>) redisConnection -> {
				return redisConnection.stringCommands().setNX(rawKey(key), rawValue(value));
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}

	public Mono<Boolean> setEx(String key, String value, long seconds) {
		try {
			//return reactiveRedisTemplate.opsForValue().set(key, value, Duration.ofMillis(seconds));
			return reactiveRedisTemplate.createMono((ReactiveRedisCallback<Boolean>) redisConnection -> {
				return redisConnection.stringCommands().setEX(rawKey(key), rawValue(value), Expiration.seconds(seconds));
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}

	/**
	 * 普通缓存获取
	 * @param key 键
	 * @return 值
	 */
	public Mono<Object> get(String key) {
		try {
			return !StringUtils.hasText(key) ? Mono.empty() : reactiveRedisTemplate.opsForValue().get(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.empty();
		}
	}
	
	/**
	 * 根据key表达式获取缓存
	 * @param pattern 键表达式
	 * @return 值
	 */
	public Mono<List<Object>> multiGet(String pattern) {
		try {
			if (!StringUtils.hasText(pattern)) {
				return Mono.empty();
			}
			List<Object> keys = reactiveRedisTemplate.keys(pattern).collectList().block();
			return reactiveRedisTemplate.opsForValue().multiGet(keys);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.empty();
		}
	}
	
	/**
	 * 批量获取缓存值
	 * @param keys 键集合
	 * @return 值
	 */
	public Mono<List<Object>> multiGet(Collection keys) {
		try {
			return CollectionUtils.isEmpty(keys) ? Mono.empty() : reactiveRedisTemplate.opsForValue().multiGet(keys);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.empty();
		}
	}
	
	/**
	 * 递增
	 *
	 * @param key   键
	 * @param delta 要增加几(>=0)
	 * @return
	 */
	public Mono<Long> incr(String key, long delta) {
		return reactiveRedisTemplate.opsForValue().increment(key, delta);
	}
	
	/**
	 * 递增
	 *
	 * @param key   键
	 * @param delta 要增加几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Mono<Long> incr(String key, long delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Mono<Long> increment = reactiveRedisTemplate.opsForValue().increment(key, delta);
		if (seconds > 0) {
			expire(key, seconds);
		}
		return increment;
	}

	/**
	 * 递增
	 *
	 * @param key   键
	 * @param delta 要增加几(>=0)
	 * @return
	 */
	public Mono<Double> incr(String key, double delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		return reactiveRedisTemplate.opsForValue().increment(key, delta);
	}

	/**
	 * 递增
	 *
	 * @param key   键
	 * @param delta 要增加几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Mono<Double> incr(String key, double delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Mono<Double> increment = reactiveRedisTemplate.opsForValue().increment(key, delta);
		if (seconds > 0) {
			expire(key, seconds);
		}
		return increment;
	}

	/**
	 * 递减
	 *
	 * @param key   键
	 * @param delta 要减少几(>=0)
	 * @return
	 */
	public Mono<Long> decr(String key, long delta) {
		return reactiveRedisTemplate.opsForValue().increment(key, -delta);
	}
	
	/**
	 * 递减
	 *
	 * @param key   键
	 * @param delta 要减少几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Mono<Long> decr(String key, long delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		Mono<Long> increment = reactiveRedisTemplate.opsForValue().increment(key, -delta);
		if (seconds > 0) {
			expire(key, seconds);
		}
		return increment;
	}
	
	/**
	 * 递减
	 *
	 * @param key   键
	 * @param delta 要减少几(>=0)
	 * @return
	 */
	public Mono<Double> decr(String key, double delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		return reactiveRedisTemplate.opsForValue().increment(key, -delta);
	}

	/**
	 * 递减
	 *
	 * @param key   键
	 * @param delta 要减少几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Mono<Double> decr(String key, double delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		Mono<Double> increment = reactiveRedisTemplate.opsForValue().increment(key, -delta);
		if (seconds > 0) {
			expire(key, seconds);
		}
		return increment;
	}
	
	// ================================Map=================================

	/**
	 * HashGet
	 *
	 * @param key  键 不能为null
	 * @param item 项 不能为null
	 * @return 值
	 */
	public Mono<Object> hget(String key, String hashKey) {
		try {
			return reactiveRedisTemplate.opsForHash().get(key, hashKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.empty();
		}
	}
	
	/**
	 * 获取hashKey对应的指定键值
	 * @param key 键
	 * @param hashKeys 要筛选项
	 * @return
	 */
	public Mono<List<Object>> hmultiGet(String key, Collection hashKeys) {
		try {
			return reactiveRedisTemplate.opsForHash().multiGet(key, hashKeys);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.empty();
		}
	}

	/**
	 * 获取hashKey对应的所有键值
	 *
	 * @param key 键
	 * @return 对应的多个键值
	 */
	public Flux<Entry<Object, Object>> hmget(String key) {
		try {
			return StringUtils.hasText(key) ? reactiveRedisTemplate.opsForHash().entries(key) : Flux.empty();
		} catch (Exception e) {
			log.error(e.getMessage());
			return Flux.empty();
		}
	}

	/**
	 * 获取hashKey对应的所有键值
	 *
	 * @param key 键
	 * @return 对应的多个键值
	 */
	public Flux<Entry<Object, Object>> hmget2(String key) {
		try {
			ReactiveHashOperations<Object, Object, Object> opsForHash = reactiveRedisTemplate.opsForHash();
			return opsForHash.entries(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Flux.empty();
		}
		
	}

	/**
	 * HashSet
	 *
	 * @param key 键
	 * @param map 对应多个键值
	 * @return true 成功 false 失败
	 */
	public Mono<Boolean> hmset(String key, Map<String, Object> map) {
		try {
			return reactiveRedisTemplate.opsForHash().putAll(key, map);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}

	/**
	 * HashSet 并设置时间
	 *
	 * @param key  键
	 * @param map  对应多个键值
	 * @param time 时间(秒)
	 * @return true成功 false失败
	 */
	public Mono<Boolean> hmset(String key, Map<String, Object> map, long seconds) {
		try {
			Mono<Boolean> mono = reactiveRedisTemplate.opsForHash().putAll(key, map);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return mono;
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}

	public Mono<Boolean> hmset(String key, Map<String, Object> map, Duration duration) {
		try {
			Mono<Boolean> mono = reactiveRedisTemplate.opsForHash().putAll(key, map);
			if(!duration.isNegative()) {
				expire(key, duration);
			}
			return mono;
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}
	
	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 *
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @return true 成功 false失败
	 */
	public Mono<Boolean> hset(String key, String hashKey, Object hashValue) {
		try {
			ReactiveHashOperations<Object, Object, Object> opsForHash = reactiveRedisTemplate.opsForHash();
			return reactiveRedisTemplate.opsForHash().put(key, hashKey, hashValue);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 *
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @param seconds  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
	 * @return true 成功 false失败
	 */
	public Mono<Boolean> hset(String key, String item, Object value, long seconds) {
		try {
			Mono<Boolean> mono = reactiveRedisTemplate.opsForHash().put(key, item, value);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return mono;
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}

	/**
	 * hash的大小
	 * 
	 * @param key
	 * @return
	 */
	public Mono<Long> hsize(String key) {
		try {
			return reactiveRedisTemplate.opsForHash().size(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(0L);
		}
	}

	/**
	 * 删除hash表中的值
	 *
	 * @param key  键 不能为null
	 * @param hashKeys 项 可以使多个 不能为null
	 */
	public void hRemove(String key, Object... hashKeys) {
		reactiveRedisTemplate.opsForHash().remove(key, hashKeys);
	}
	 
	/**
	 * 判断hash表中是否有该项的值
	 *
	 * @param key  键 不能为null
	 * @param item 项 不能为null
	 * @return true 存在 false不存在
	 */
	public Mono<Boolean> hHasKey(String key, String item) {
		return reactiveRedisTemplate.opsForHash().hasKey(key, item);
	}

	public Flux<Object> hKeys(String key) {
		return reactiveRedisTemplate.opsForHash().keys(key);
	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key  键
	 * @param item 项
	 * @param delta  要增加几(>=0)
	 * @return
	 */
	public Mono<Long> hincr(String key, String item, int delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		return reactiveRedisTemplate.opsForHash().increment(key, item, delta);
	}

	public Mono<Long> hincr(String key, String item, int delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Mono<Long> increment = reactiveRedisTemplate.opsForHash().increment(key, item, delta);
		if (seconds > 0) {
			expire(key, seconds);
		}
		return increment;
	}
	
	public Mono<Long> hincr(String key, String item, int delta, Duration duration) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Mono<Long> increment = reactiveRedisTemplate.opsForHash().increment(key, item, delta);
		if(!duration.isNegative()) {
			expire(key, duration);
		}
		return increment;
	}
	
	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key  键
	 * @param item 项
	 * @param delta  要增加几(>=0)
	 * @return
	 */
	public Mono<Long> hincr(String key, String item, long delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		return reactiveRedisTemplate.opsForHash().increment(key, item, delta);
	}

	public Mono<Long> hincr(String key, String item, long delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Mono<Long> increment = reactiveRedisTemplate.opsForHash().increment(key, item, delta);
		if (seconds > 0) {
			expire(key, seconds);
		}
		return increment;
	}
	
	public Mono<Long> hincr(String key, String item, long delta, Duration duration) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Mono<Long> increment = reactiveRedisTemplate.opsForHash().increment(key, item, delta);
		if(!duration.isNegative()) {
			expire(key, duration);
		}
		return increment;
	}
	
	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key  键
	 * @param item 项
	 * @param delta 要增加几(>=0)
	 * @return
	 */
	public Mono<Double> hincr(String key, String item, double delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		return reactiveRedisTemplate.opsForHash().increment(key, item, delta);
	}

	public Mono<Double> hincr(String key, String item, double delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Mono<Double> increment = reactiveRedisTemplate.opsForHash().increment(key, item, delta);
		if (seconds > 0) {
			expire(key, seconds);
		}
		return increment;
	}
	
	public Mono<Double> hincr(String key, String item, double delta, Duration duration) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Mono<Double> increment = reactiveRedisTemplate.opsForHash().increment(key, item, delta);
		if(!duration.isNegative()) {
			expire(key, duration);
		}
		return increment;
	}

	/**
	 * hash递减
	 *
	 * @param key  键
	 * @param item 项
	 * @param delta 要减少记(>=0)
	 * @return
	 */
	public Mono<Long> hdecr(String key, String item, int delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		return reactiveRedisTemplate.opsForHash().increment(key, item, -delta);
	}
	
	/**
	 * hash递减
	 *
	 * @param key  键
	 * @param item 项
	 * @param delta  要增加几(>=0)
	 * @return
	 */
	public Mono<Long> hdecr(String key, String item, long delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		return reactiveRedisTemplate.opsForHash().increment(key, item, -delta);
	}

	/**
	 * hash递减
	 *
	 * @param key  键
	 * @param item 项
	 * @param delta 要减少记(>=0)
	 * @return
	 */
	public Mono<Double> hdecr(String key, String item, double delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		return reactiveRedisTemplate.opsForHash().increment(key, item, -delta);
	}
	
	// ============================set=============================

	/**
	 * 根据key获取Set中的所有值
	 *
	 * @param key 键
	 * @return
	 */
	public Flux<Object> sGet(String key) {
		try {
			return reactiveRedisTemplate.opsForSet().members(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Flux.empty();
		}
	}

	/**
	 * 根据key获取Set中的所有值
	 *
	 * @param key 键
	 * @return
	 */
	public Flux<Long> sGetLong(String key) {
		try {
			Flux<Object> members = reactiveRedisTemplate.opsForSet().members(key);
			Flux<Long> result = members.map(object -> Long.valueOf(object.toString()));
			return result;
		} catch (Exception e) {
			log.error(e.getMessage());
			return Flux.empty();
		}
	}

	/**
	 * 根据value从一个set中查询,是否存在
	 *
	 * @param key   键
	 * @param value 值
	 * @return true 存在 false不存在
	 */
	public Mono<Boolean> sHasKey(String key, Object value) {
		try {
			return reactiveRedisTemplate.opsForSet().isMember(key, value);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(Boolean.FALSE);
		}
	}

	/**
	 * 将数据放入set缓存
	 *
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 成功个数
	 */
	public Mono<Long> sSet(String key, Object... values) {
		try {
			return reactiveRedisTemplate.opsForSet().add(key, values);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(0L);
		}
	}

	/**
	 * 将set数据放入缓存
	 *
	 * @param key    键
	 * @param seconds   过期时长(秒)
	 * @param values 值 可以是多个
	 * @return 成功个数
	 */
	public Mono<Long> sSetAndTime(String key, long seconds, Object... values) {
		try {
			Mono<Long> count = reactiveRedisTemplate.opsForSet().add(key, values);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return count;
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(0L);
		}
	}

	/**
	 * 获取set缓存的长度
	 *
	 * @param key 键
	 * @return
	 */
	public Mono<Long> sGetSetSize(String key) {
		try {
			return reactiveRedisTemplate.opsForSet().size(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(0L);
		}
	}

	/**
	 * 移除值为value的
	 *
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 移除的个数
	 */
	public Mono<Long> sRemove(String key, Object... values) {
		try {
			Mono<Long> count = reactiveRedisTemplate.opsForSet().remove(key, values);
			return count;
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(0L);
		}
	}

	/**
	 * 获取两个key的不同value
	 * 
	 * @param key1 键
	 * @param key2 键
	 * @return 返回key1中和key2的不同数据
	 */
	public Flux<Object> sDiff(String key1, String key2) {
		try {
			return reactiveRedisTemplate.opsForSet().difference(key1, key2);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Flux.empty();
		}
	}

	/**
	 * 获取两个key的不同数据，放到key3中
	 * 
	 * @param key1 键
	 * @param key2 键
	 * @param key3 键
	 * @return 返回成功数据
	 */
	public Mono<Long> sDifferenceAndStore(String key1, String key2, String key3) {
		try {
			return reactiveRedisTemplate.opsForSet().differenceAndStore(key1, key2, key3);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.empty();
		}
	}

	/**
	 * 获取两个key的所有数据，放到key3中
	 * 
	 * @param key1 键
	 * @param key2 键
	 * @param key3 键
	 * @return 返回成功数据
	 */
	public Mono<Long> sUnionAndStore(String key1, String key2, String key3) {
		try {
			return reactiveRedisTemplate.opsForSet().unionAndStore(key1, key2, key3);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.empty();
		}
	}

	/**
	 * 随机获取指定数量的元素,同一个元素可能会选中两次
	 * 
	 * @param key
	 * @param count
	 * @return
	 */
	public Flux<Object> sRandomSet(String key, long count) {
		return reactiveRedisTemplate.opsForSet().randomMembers(key, count);
	}

	/**
	 * 随机获取指定数量的元素,去重(同一个元素只能选择一次)
	 * 
	 * @param key
	 * @param count
	 * @return
	 */
	public Flux<Object> sRandomSetDistinct(String key, long count) {
		return reactiveRedisTemplate.opsForSet().distinctRandomMembers(key, count);
	}

	// ===============================list=================================

	/**
	 * 获取list缓存的内容
	 *
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @return
	 */
	public Flux<Object> lGet(String key, long start, long end) {
		try {
			return reactiveRedisTemplate.opsForList().range(key, start, end);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Flux.empty();
		}
	}

	/**
	 * 获取list缓存的内容
	 *
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @return
	 */
	public Flux<Long> lGetLong(String key, long start, long end) {
		try {
			Flux<Object> range = reactiveRedisTemplate.opsForList().range(key, start, end);
			Flux<Long> result = range.map(object -> Long.valueOf(object.toString()));
			return result;
		} catch (Exception e) {
			log.error(e.getMessage());
			return Flux.empty();
		}
	}

	/**
	 * 获取list缓存的长度
	 *
	 * @param key 键
	 * @return
	 */
	public Mono<Long> lGetListSize(String key) {
		try {
			return reactiveRedisTemplate.opsForList().size(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(0L);
		}
	}

	/**
	 * 通过索引 获取list中的值
	 *
	 * @param key   键
	 * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
	 * @return
	 */
	public Mono<Object> lGetIndex(String key, long index) {
		try {
			return reactiveRedisTemplate.opsForList().index(key, index);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.empty();
		}
	}

	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public Mono<Long> lRightPush(String key, Object value) {
		try {
			return reactiveRedisTemplate.opsForList().rightPush(key, value);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.empty();
		}
	}
	
	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @param seconds  过期时长(秒)
	 * @return
	 */
	public Mono<Long> lRightPush(String key, Object value, long seconds) {
		try {
			Mono<Long> mono = reactiveRedisTemplate.opsForList().rightPush(key, value);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return mono;
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.empty();
		}
	}
	

	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param values 值
	 * @return
	 */
	public Mono<Long> lRightPush(String key, List<Object> values) {
		try {
			return reactiveRedisTemplate.opsForList().rightPushAll(key, values);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(0L);
		}
	}
	
	/**
	 * 将元素放到list左边
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public Mono<Long> lLeftPush(String key, Object value) {
		try {
			return reactiveRedisTemplate.opsForList().leftPush(key, value);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(0L);
		}
	}

	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @param seconds  时间(秒)
	 * @return
	 */
	public Mono<Long> lLeftPush(String key, List<Object> value, long seconds) {
		try {
			Mono<Long> mono = reactiveRedisTemplate.opsForList().rightPushAll(key, value);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return mono;
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(0L);
		}
	}

	public Mono<Long> lLeftPush(String key, List<Object> values, Duration duration) {
		try {
			Mono<Long> mono = reactiveRedisTemplate.opsForList().rightPushAll(key, values);
			if(!duration.isNegative()) {
				expire(key, duration);
			}
			return mono;
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(0L);
		}
	}
	
	public Mono<Object> lLeftPop(String key) {
		try {
			return reactiveRedisTemplate.opsForList().leftPop(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.empty();
		}
	}

	/**
	 * 根据索引修改list中的某条数据
	 *
	 * @param key   键
	 * @param index 索引
	 * @param value 值
	 * @return
	 */
	public boolean lUpdateIndex(String key, long index, Object value) {
		try {
			reactiveRedisTemplate.opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	/**
	 * 移除N个值为value
	 *
	 * @param key   键
	 * @param count 移除多少个
	 * @param value 值
	 * @return 移除的个数
	 */
	public Mono<Long> lRemove(String key, long count, Object value) {
		try {
			return reactiveRedisTemplate.opsForList().remove(key, count, value);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Mono.just(0L);
		}
	}

	public boolean lRemoveAll(String key, int start, int end) {
		try {
			reactiveRedisTemplate.opsForList().trim(key, start, end);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	// ===============================ZSet=================================

	/**
	 * Zset添加元素
	 * 
	 * @param key
	 * @param value
	 * @param score
	 */
	public Mono<Double> zincr(String key, String value, double score) {
		return reactiveRedisTemplate.opsForZSet().incrementScore(key, value, score);
	}

	public Mono<Double> zincr(String key, String value, double score, long time) {
		Mono<Double> result = reactiveRedisTemplate.opsForZSet().incrementScore(key, value, score);
		expire(key, time);
		return result;
	}

	public Mono<Boolean> updateZsetScore(String key, String value, double score) {
		return reactiveRedisTemplate.opsForZSet().add(key, value, score);
	}

	public Mono<Long> addZsetScore(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
		return reactiveRedisTemplate.opsForZSet().addAll(key, tuples);
	}

	/**
	 * 获取指定key的scores正序，指定start-end位置的元素
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Flux<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String key, long start, long end) {
		return reactiveRedisTemplate.opsForZSet().reverseRangeWithScores(key, Range.open(start, end));
	}

	/**
	 * 通过分数返回有序集合指定区间内的成员个数
	 * 
	 * @param key
	 * @param min
	 * @param max
	 */
	public Mono<Long> zsetCountByscores(String key, double min, double max) {
		return reactiveRedisTemplate.opsForZSet().count(key, Range.open(min, max));
	}

	/**
	 * 移除zset中的元素
	 * 
	 * @param key
	 * @param value
	 */
	public Mono<Long> removeZsetValue(String key, Object... value) {
		return reactiveRedisTemplate.opsForZSet().remove(key, value);
	}

	/**
	 * 移除分数区间内的元素
	 * 
	 * @param key
	 * @param min
	 * @param max
	 */
	public void removeZetRangeByScore(String key, double min, double max) {
		reactiveRedisTemplate.opsForZSet().removeRangeByScore(key,
				org.springframework.data.domain.Range.open(min, max));
	}

	public Mono<Double> getZscore(String key, Object value) {
		return reactiveRedisTemplate.opsForZSet().score(key, value);
	}

	public Mono<Long> reverseRank(String key, Object value) {
		return reactiveRedisTemplate.opsForZSet().reverseRank(key, value);
	}

	public Mono<Long> zCard(String key) {
		return reactiveRedisTemplate.createMono((ReactiveRedisCallback<Long>) redisConnection -> {
			return redisConnection.zSetCommands().zCard(rawKey(key));
		});
	}

	/**
	 * @param key   :
	 * @param from :
	 * @param to   :0 到-1表示查全部
	 * @return {@link Set< Object>}
	 */
	public Flux<Object> zReverseRange(String key, Long from, Long to) {
		return reactiveRedisTemplate.opsForZSet().reverseRange(key, Range.open(from, to));
	}

	/**
	 * @Date: 2020/6/7
	 * @param key   :
	 * @param from :
	 * @param to   :0 到-1表示查全部
	 * @return {@link Set< Long>}
	 */
	public Flux<Long> zReverseLongRange(String key, Long from, Long to) {
		return reactiveRedisTemplate.opsForZSet()
					.reverseRange(key, Range.open(from, to))
					.map(obj -> Long.valueOf(obj.toString()))
					.distinct();
	}

	public Flux<Object> zRangeByScore(String key, double min, double max) {
		return reactiveRedisTemplate.opsForZSet().rangeByScore(key, Range.open(min, max));
	}

	/**
	 * 在min到max范围内倒序获取zset及对应的score
	 */
	public Flux<TypedTuple<Object>> getZetRangeByScoreWithScores(String key, double min, double max) {
		return reactiveRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, Range.open(min, max));
	}
	
	/**
	 * 批量获取hash 某个值
	 * 
	 * @param redisKey
	 * @param redisField
	 */
	/**public List<Object> batchGetHashKeyField(Collection<Long> ids, String redisKey, String redisField) {
		List<Object> list = reactiveRedisTemplate.executePipelined(new RedisCallback<Long>() {
			@Nullable
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				connection.openPipeline();
				ids.stream().forEach(id -> {
					String key = RedisUtil.getKeyStr(redisKey, id.toString());
					connection.hGet(key.getBytes(), redisField.getBytes());
				});
				return null;
			}
		}, reactiveRedisTemplate.getValueSerializer());
		return list;
	}

	
	
	public List<Object> batchGetUserInfo(Collection<Long> userIds) {
		List<Object> list = reactiveRedisTemplate.executePipelined(new RedisCallback<Map>() {
			@Nullable

			public Map doInRedis(RedisConnection connection) throws DataAccessException {
				connection.openPipeline();
				userIds.stream().forEach(userId -> {
					String key = getKeyStr(RedisConstant.USER_INFO_PREFIX, String.valueOf(userId));
					connection.hGetAll(key.getBytes());
				});
				return null;
			}
		}, reactiveRedisTemplate.getValueSerializer());
		return list;
	}

	public List<String> batchGetList(String key, Integer count) {
		List<Object> result = reactiveRedisTemplate.executePipelined(new RedisCallback<Map>() {
			@Nullable

			public Map doInRedis(RedisConnection connection) throws DataAccessException {
				connection.openPipeline();
				connection.lRange(key.getBytes(), 0, count - 1);
				connection.lTrim(key.getBytes(), count, -1);
				return null;
			}
		}, reactiveRedisTemplate.getValueSerializer());
		return (List<String>) result.get(0);
	}

	public List<Object> batchGetCapitalList(Collection<Long> userIds, String redisPrefix) {
		List<Object> list = reactiveRedisTemplate.executePipelined(new RedisCallback<Map>() {
			@Nullable

			public Map doInRedis(RedisConnection connection) throws DataAccessException {
				connection.openPipeline();
				userIds.stream().forEach(userId -> {
					String key = RedisUtil.getKeyStr(redisPrefix, userId.toString());
					connection.get(key.getBytes());
				});
				return null;
			}
		}, reactiveRedisTemplate.getValueSerializer());
		return list;
	}
 */
	/**
	 * scan 实现
	 * 
	 * @param pattern  表达式
	 * @param consumer 对迭代到的key进行操作
	 
	public void scan(String pattern, Consumer<byte[]> consumer) {
		this.reactiveRedisTemplate.execute((ReactiveRedisCallback) connection -> {
			try (Cursor<byte[]> cursor = connection
					.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (IOException e) {
				log.error(e.getMessage());
				throw new BizRuntimeException(e.getMessage());
			}
		});
	}*/

	/**
	 * 获取符合条件的key
	 * 
	 * @param pattern 表达式
	 * @return
	 
	public List<String> keys(String pattern) {
		List<String> keys = new ArrayList<>();
		this.scan(pattern, item -> {
			// 符合条件的key
			String key = new String(item, StandardCharsets.UTF_8);
			keys.add(key);
		});
		return keys;
	}
*/
	
	

	// ===============================Pipeline=================================
	
	//public List<Object> executePipelined(ReactiveRedisCallback<List<Object>> action) {
	//	return reactiveRedisTemplate.executePipelined(action);
	//}

	/**
	 * 执行lua脚本
	 * 
	 * @param luaScript 脚本内容
	 * @param keys      redis键列表
	 * @param values    值列表
	 * @return
	 */

	public Flux<Object> executeLuaScript(String luaScript, List<Object> keys, List<String> values) {
		RedisScript redisScript = RedisScript.of(luaScript);
		return reactiveRedisTemplate.execute(redisScript, keys, values);
	}

	/**
	 * 执行lua脚本
	 * 
	 * @param luaScript  脚本内容
	 * @param keys       redis键列表
	 * @param values     值列表
	 * @param resultType 返回值类型
	 * @return
	 */
	public <T> Flux<T> executeLuaScript(String luaScript, List<Object> keys, List<String> values, Class<T> resultType) {
		RedisScript redisScript = RedisScript.of(luaScript, resultType);
		return reactiveRedisTemplate.execute(redisScript, keys, values);
	}

	// ===============================Lock=================================

	/**
	 * 获取分布式锁
	 * 
	 * @param lockKey
	 * @param requestId
	 * @param expireTime
	 * @return
	 */
	public Mono<Boolean> tryLock(String lockKey, String requestId, long expireTime) {
		Assert.hasLength(lockKey, "lockKey must not be empty");
		Assert.hasLength(requestId, "requestId must not be empty");
		return reactiveRedisTemplate.opsForValue().setIfAbsent(lockKey, requestId, Duration.ofSeconds(expireTime));
	}

	/**
	 * 释放分布式锁
	 * 
	 * @param lockKey
	 * @param requestId
	 * @return
	 */
	public Mono<Boolean> unlock(String lockKey, String requestId) {
		Assert.hasLength(lockKey, "lockKey must not be empty");
		Assert.hasLength(requestId, "requestId must not be empty");
		return this.executeLuaScript(RELEASE_LOCK_SCRIPT, Lists.newArrayList(lockKey), Lists.newArrayList(requestId), Long.class)
				.next().map(count -> count == 1)
				.doOnError(t -> log.error("release lockkey: [{}] failure", lockKey, t))
				.onErrorResume(e -> Mono.just(false));
	}

	
	/**
	 * redis分布式加锁
	 * 
	 * @param lockKey   锁的key值
	 * @param lockValue 锁的value
	 * @param lockTime  锁时间(毫秒)
	 * @return 是否成功加锁
	 
	public boolean tryLock(String lockKey, long lockValue, long lockTime) {

		boolean isLock = false;

		// 循环10次获得锁
		for (int i = 0; i < 10; i++) {
			long redisTime = currtTimeFromRedis().block();
			long realLockTime = redisTime + lockTime;// 超时时间
			reactiveRedisTemplate.createMono((ReactiveRedisCallback<Boolean>) redisConnection -> {
				byte[] serValue = StringRedisSerializer.UTF_8.serialize(String.valueOf(lockValue));
				return redisConnection.stringCommands().setNX(ByteBuffer.wrap(lockKey.getBytes()), ByteBuffer.wrap(serValue));
			}).doOnSuccess((s) -> {
				reactiveRedisTemplate.expire(lockKey, Duration.ofMillis(realLockTime));
				isLock = true;
			}).doOnError((ex) -> {
				Long curlockTime = (Long) reactiveRedisTemplate.opsForValue().get(lockKey).block();
				if (null != curlockTime && redisTime > curlockTime) {
					Long oldLockTime = (Long) reactiveRedisTemplate.opsForValue().getAndSet(lockKey, realLockTime).block();
					if (null != oldLockTime && oldLockTime.equals(curlockTime)) {
						reactiveRedisTemplate.expire(lockKey, Duration.ofMillis(realLockTime));
						isLock = true;
					}
				}
			});
			try {
				TimeUnit.MILLISECONDS.sleep(100); // 睡眠100毫秒
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}
		}

		return isLock;
	}*/
	
	/**
	public Mono<Boolean> lock(String lockKey, long expireMillis) {
		return reactiveRedisTemplate.createMono((ReactiveRedisCallback<Boolean>) redisConnection -> {
        	ByteBuffer serLockKey = rawKey(lockKey);
            // 1、获取时间毫秒值
            long expireAt = System.currentTimeMillis() + expireMillis + 1;
            // 2、获取锁
            Mono<Boolean> acquire = redisConnection.stringCommands().setNX(serLockKey, rawValue(expireAt));
            if (acquire) {
                return true;
            } else {
                Mono<ByteBuffer> bytes = redisConnection.stringCommands().get(serLockKey);
                // 3、非空判断
                if (Objects.nonNull(bytes) && bytes.length > 0) {
                    long expireTime = Long.parseLong(new String(bytes));
                    // 4、如果锁已经过期
                    if (expireTime < System.currentTimeMillis()) {
                        // 5、重新加锁，防止死锁
                        Mono<ByteBuffer> set = redisConnection.stringCommands().getSet(serLockKey, rawValue(System.currentTimeMillis() + expireMillis + 1));
                        return Long.parseLong(new String(set)) < System.currentTimeMillis();
                    }
                }
            }
            return acquire;
        });
    }**/

	/**
	 * 分布式锁解锁 加锁者可解锁，非加锁者等待过期 不可解锁
	 * 
	 * @param lockKey
	 * @param lockValue
	 */
	public void unlock(String lockKey, Object lockValue) {
		reactiveRedisTemplate.opsForValue().get(lockKey).doOnSuccess((oldLockValue) -> {
			if (!Objects.isNull(oldLockValue) && lockValue == oldLockValue) {
				delete(lockKey);
			}
		});
	}

	// ===============================Message=================================

	/**
	 * 发送消息
	 *
	 * @param channel
	 * @param message
	 */
	public void sendMessage(String channel, String message) {
		reactiveRedisTemplate.convertAndSend(channel, message);
	}

	
	// ===============================Command=================================

	/**
	 * 获取redis服务器时间 保证集群环境下时间一致
	 * @return
	 */
	public Mono<Long> currtTimeFromRedis() {
		return reactiveRedisTemplate.createMono((ReactiveRedisCallback<Long>) redisConnection -> {
			return redisConnection.serverCommands().time();
		});
	}

	public Mono<Long> dbSize() {
		return reactiveRedisTemplate.createMono((ReactiveRedisCallback<Long>) redisConnection -> {
			return redisConnection.serverCommands().dbSize();
		});
	}
	
	public Mono<Long> lastSave() {
		return reactiveRedisTemplate.createMono((ReactiveRedisCallback<Long>) redisConnection -> {
			return redisConnection.serverCommands().lastSave();
		});
	}

	public Mono<String> bgReWriteAof() {
		return reactiveRedisTemplate.createMono((ReactiveRedisCallback<String>) redisConnection -> {
			return redisConnection.serverCommands().bgReWriteAof();
		});
	}
	
	public Mono<String> bgSave() {
		return reactiveRedisTemplate.createMono((ReactiveRedisCallback<String>) redisConnection -> {
			return redisConnection.serverCommands().bgSave();
		});
	}
	
	public Mono<String> save() {
		return reactiveRedisTemplate.createMono((ReactiveRedisCallback<String>) redisConnection -> {
			return redisConnection.serverCommands().save();
		});
	}
	
	public Mono<String> flushDb() {
		return reactiveRedisTemplate.createMono((ReactiveRedisCallback<String>) redisConnection -> {
			return redisConnection.serverCommands().flushDb();
		});
	}
	
	public Mono<String> flushAll() {
		return reactiveRedisTemplate.createMono((ReactiveRedisCallback<String>) redisConnection -> {
			return redisConnection.serverCommands().flushAll();
		});
	}

}