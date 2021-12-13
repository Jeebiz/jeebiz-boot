package net.jeebiz.boot.extras.redis.setup;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.jeebiz.boot.api.exception.RedisOperationException;
import org.apache.commons.collections.MapUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.ReactiveListCommands;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveSubscription.Message;
import org.springframework.data.redis.connection.RedisZSetCommands.Aggregate;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.connection.RedisZSetCommands.Weights;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ReactiveRedisCallback;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import net.jeebiz.boot.api.exception.BizRuntimeException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Slf4j
public class ReactiveRedisOperationTemplate {

	private static final Long LOCK_SUCCESS = 1L;
    private static final Long LOCK_EXPIRED = -1L;

    private static final RedisScript<Long> LOCK_LUA_SCRIPT = RedisScript.of(RedisLua.LOCK_LUA_SCRIPT, Long.class );

    private static final RedisScript<Long> UNLOCK_LUA_SCRIPT = RedisScript.of(RedisLua.UNLOCK_LUA_SCRIPT, Long.class );

    public static final RedisScript<Long> INCR_SCRIPT = RedisScript.of(RedisLua.INCR_SCRIPT, Long.class);
    public static final RedisScript<Long> DECR_SCRIPT = RedisScript.of(RedisLua.DECR_SCRIPT, Long.class);

    public static final RedisScript<Object> INCR_BYFLOAT_SCRIPT = RedisScript.of(RedisLua.INCR_BYFLOAT_SCRIPT, Object.class);
    public static final RedisScript<Object> DECR_BYFLOAT_SCRIPT = RedisScript.of(RedisLua.DECR_BYFLOAT_SCRIPT, Object.class);

    public static final RedisScript<Long> HINCR_SCRIPT = RedisScript.of(RedisLua.HINCR_SCRIPT, Long.class);
    public static final RedisScript<Long> HDECR_SCRIPT = RedisScript.of(RedisLua.HDECR_SCRIPT, Long.class);

    public static final RedisScript<Object> HINCR_BYFLOAT_SCRIPT = RedisScript.of(RedisLua.HINCR_BYFLOAT_SCRIPT, Object.class);
    public static final RedisScript<Object> HDECR_BYFLOAT_SCRIPT = RedisScript.of(RedisLua.HDECR_BYFLOAT_SCRIPT, Object.class);


	public static final Function<Object, String> TO_STRING = member -> Objects.toString(member, null);

    public static final Function<Object, Double> TO_DOUBLE = member -> {
		if(Objects.isNull(member)) {
			return null;
		}
		return member instanceof Double ? (Double) member : new BigDecimal(member.toString()).doubleValue();
	};

	public static final Function<Object, Long> TO_LONG = member -> {
		if(Objects.isNull(member)) {
			return null;
		}
		return member instanceof Long ? (Long) member : new BigDecimal(member.toString()).longValue();
	};

	public static final Function<Object, Integer> TO_INTEGER = member -> {
		if(Objects.isNull(member)) {
			return null;
		}
		return member instanceof Integer ? (Integer) member : new BigDecimal(member.toString()).intValue();
	};

	private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

	public ReactiveRedisOperationTemplate(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
		this.reactiveRedisTemplate = reactiveRedisTemplate;
	}

	protected <T> Mono<T> monoError(Exception e){
		log.error(e.getMessage());
		return Mono.error(new RedisOperationException(e.getMessage()));
	}

	protected <T> Flux<T> fluxError(Exception e){
		log.error(e.getMessage());
		return Flux.error(new RedisOperationException(e.getMessage()));
	}

	// =============================Serializer============================

	public ByteBuffer getRawKey(String key) {
		return reactiveRedisTemplate.getSerializationContext().getStringSerializationPair().write(key);
	}

	public <V> ByteBuffer getRawValue(V value) {
		if (value instanceof ByteBuffer) {
			return (ByteBuffer) value;
		}
		return reactiveRedisTemplate.getSerializationContext().getValueSerializationPair().write(value);
	}

	public <V> List<ByteBuffer> getRawValues(Collection<V> values) {
		return values.stream().map(value -> reactiveRedisTemplate.getSerializationContext().getValueSerializationPair().write(value)).collect(Collectors.toList());
	}

	public <HK> ByteBuffer getRawHashKey(HK hashKey) {
		return reactiveRedisTemplate.getSerializationContext().getHashKeySerializationPair().write(hashKey);
	}

	public <HK> List<ByteBuffer> getRawHashKeys(HK... hashKeys) {
		return Stream.of(hashKeys).map(hashKey -> reactiveRedisTemplate.getSerializationContext().getHashKeySerializationPair().write(hashKey)).collect(Collectors.toList());
	}

	public <HV> ByteBuffer getRawHashValue(HV value) {
		return reactiveRedisTemplate.getSerializationContext().getHashValueSerializationPair().write(value);
	}

	// =============================Deserialize============================

	public Set<Object> getDeserializeValues(Set<ByteBuffer> rawValues) {
		return rawValues.stream().map(rawValue -> reactiveRedisTemplate.getSerializationContext().getValueSerializationPair().read(rawValue)).collect(Collectors.toSet());
	}

	public List<Object> getDeserializeValues(List<ByteBuffer> rawValues) {
		return rawValues.stream().map(rawValue -> reactiveRedisTemplate.getSerializationContext().getValueSerializationPair().read(rawValue)).collect(Collectors.toList());
	}

	public <T> Set<T> getDeserializeHashKeys(Set<ByteBuffer> rawKeys) {
		return rawKeys.stream().map(rawKey -> (T) reactiveRedisTemplate.getSerializationContext().getHashKeySerializationPair().read(rawKey)).collect(Collectors.toSet());
	}

	public <T> List<T> getDeserializeHashValues(List<ByteBuffer> rawValues) {
		return rawValues.stream().map(rawValue -> (T) reactiveRedisTemplate.getSerializationContext().getHashValueSerializationPair().read(rawValue)).collect(Collectors.toList());
	}

	public String getDeserializeKey(ByteBuffer rawKey) {
		return reactiveRedisTemplate.getSerializationContext().getStringSerializationPair().read(rawKey);
	}

	public Set<String> getDeserializeKeys(Set<ByteBuffer> rawKeys) {
		return rawKeys.stream().map(rawKey -> reactiveRedisTemplate.getSerializationContext().getStringSerializationPair().read(rawKey)).collect(Collectors.toSet());
	}

	public Object getDeserializeValue(ByteBuffer rawValue) {
		return reactiveRedisTemplate.getSerializationContext().getValueSerializationPair().read(rawValue);
	}

	public String getDeserializeString(ByteBuffer rawValue) {
		return reactiveRedisTemplate.getSerializationContext().getStringSerializationPair().read(rawValue);
	}

	public <HK> HK getDeserializeHashKey(ByteBuffer rawKey) {
		return (HK) reactiveRedisTemplate.getSerializationContext().getHashKeySerializationPair().read(rawKey);
	}

	public <HV> HV getDeserializeHashValue(ByteBuffer rawValue) {
		return (HV) reactiveRedisTemplate.getSerializationContext().getHashValueSerializationPair().read(rawValue);
	}

	public TypedTuple<Object> getDeserializeTuple(Tuple raw) {
		return new DefaultTypedTuple<>(getDeserializeValue(ByteBuffer.wrap(raw.getValue())), raw.getScore());
	}

	// =============================Keys============================

	/**
	 * 指定缓存失效时间
	 *
	 * @param key     键
	 * @param seconds 时间(秒)
	 * @return
	 */
	public Mono<Boolean> expire(String key, long seconds) {
		return this.expire(key, Duration.ofSeconds(seconds));
	}

	/**
	 * 指定缓存失效时间
	 *
	 * @param key     键
	 * @param duration 时间
	 * @return
	 */
	public Mono<Boolean> expire(String key, Duration duration) {
		if (Objects.isNull(duration)) {
			return Mono.just(Boolean.FALSE);
		}
		try {
			return reactiveRedisTemplate.expire(key, duration);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Boolean> expireAt(String key, Instant expireAt) {
		if (Objects.isNull(expireAt)) {
			return Mono.just(Boolean.FALSE);
		}
		try {
			return reactiveRedisTemplate.expireAt(key, expireAt);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 根据key 获取过期时间
	 * @param key 键 不能为null
	 * @return 时间(秒) 返回0代表为永久有效
	 */
	public Mono<Duration> getExpire(String key) {
		try {
			return reactiveRedisTemplate.getExpire(key);
		} catch (Exception e) {
			return monoError(e);
		}
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
			return monoError(e);
		}
	}

	// 模糊匹配缓存中的key
	public Flux<String> getKey(String pattern) {
		try {
			if (Objects.isNull(pattern)) {
				return Flux.empty();
			}
			return reactiveRedisTemplate.keys(pattern);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	// 模糊匹配缓存中的key
	public Flux<String> getVagueKey(String pattern) {
		try {
			return reactiveRedisTemplate.keys("*" + pattern + "*");
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<String> getValueKeyByPrefix(String prefixPattern) {
		try {
			return reactiveRedisTemplate.keys(prefixPattern + "*");
		} catch (Exception e) {
			return fluxError(e);
		}
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
			return monoError(e);
		}
	}

	/**
	 * 普通缓存放入并设置时间
	 *
	 * @param key   键
	 * @param value 值
	 * @param seconds  时间(秒) time要>=0 如果time小于等于0 将设置无限期
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
			return monoError(e);
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
			return monoError(e);
		}
	}

	public Mono<Boolean> setNX(String key, Object value) {
		try {
			Assert.hasLength(key, "key must not be empty");
			return reactiveRedisTemplate.opsForValue().setIfAbsent(key, value);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 1、仅可用于低并发功能，高并发严禁使用此方法
	 *
	 * @param key     并发锁
	 * @param value   锁key（务必能区别不同线程的请求）
	 * @param milliseconds 锁过期时间（单位：毫秒）
	 * @return
	 */
	public Mono<Boolean> setNx(String key, Object value, long milliseconds) {
		try {
			return reactiveRedisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(milliseconds));
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 1、仅可用于低并发功能，高并发严禁使用此方法
	 *
	 * @param key     并发锁
	 * @param value   锁key（务必能区别不同线程的请求）
	 * @param timeout 锁过期时间
	 * @return
	 */
	public Mono<Boolean> setNx(String key, Object value, Duration timeout) {
		try {
			return reactiveRedisTemplate.opsForValue().setIfAbsent(key, value, timeout);
		} catch (Exception e) {
			return monoError(e);
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
			return monoError(e);
		}
	}

	public Mono<Double> getDouble(String key) {
		return getFor(key, TO_DOUBLE);
	}

	public Mono<Long> getLong(String key) {
		return getFor(key, TO_LONG);
	}

	public Mono<Integer> getInteger(String key) {
		return getFor(key, TO_INTEGER);
	}

	public Mono<String> getString(String key) {
		return getFor(key, TO_STRING);
	}

	public <T> Mono<T> getFor(String key, Class<T> clazz) {
		return getFor(key, member -> clazz.cast(member));
	}

	/**
	 * 根据key获取值，并按Function函数进行转换
	 *
	 * @param key    键
	 * @param mapper 对象转换函数
	 * @return xx
	 */
	public <T> Mono<T> getFor(String key, Function<Object, T> mapper) {
		Mono<Object> obj = this.get(key);
		return obj.map(mapper);
	}

	/**
	 * 根据key表达式获取缓存
	 * @param pattern 键表达式
	 * @return 值
	 */
	public Mono<List<Object>> mGet(String pattern) {
		try {
			if (!StringUtils.hasText(pattern)) {
				return Mono.empty();
			}
			Collection<String> keys = reactiveRedisTemplate.keys(pattern).collectList().block();
			return reactiveRedisTemplate.opsForValue().multiGet(keys);
		} catch (Exception e) {
			return monoError(e);
		}
	}


	public Mono<List<Double>> mGetDouble(Collection keys) {
		return mGetFor(keys, TO_DOUBLE);
	}

	public Mono<List<Long>> mGetLong(Collection keys) {
		return mGetFor(keys, TO_LONG);
	}

	public Mono<List<Integer>> mGetInteger(Collection keys) {
		return mGetFor(keys, TO_INTEGER);
	}

	public Mono<List<String>> mGetString(Collection keys) {
		return mGetFor(keys, TO_STRING);
	}

	public <T> Mono<List<T>> mGetFor(Collection keys, Class<T> clazz) {
		return mGetFor(keys, member -> clazz.cast(member));
	}

	public <T> Mono<List<T>> mGetFor(Collection keys, Function<Object, T> mapper) {
		return this.mGet(keys).map(members -> members.stream().map(mapper).collect(Collectors.toList()));
	}

	/**
	 * 批量获取缓存值
	 * @param keys 键集合
	 * @return 值
	 */
	public Mono<List<Object>> mGet(Collection keys) {
		try {
			if(CollectionUtils.isEmpty(keys)) {
				return Mono.empty();
			}
			return reactiveRedisTemplate.opsForValue().multiGet(keys);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<List<Object>> mGet(Collection<Object> keys, String redisPrefix) {
		try {
			if(CollectionUtils.isEmpty(keys)) {
				return Mono.empty();
			}
			Collection newKeys = keys.stream().map(key -> RedisKeyConstant.getKeyStr(redisPrefix, key.toString())).collect(Collectors.toList());
			return reactiveRedisTemplate.opsForValue().multiGet(newKeys);
		} catch (Exception e) {
			return monoError(e);
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
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			return reactiveRedisTemplate.opsForValue().increment(key, delta);
		} catch (Exception e) {
			return monoError(e);
		}
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
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			Mono<Long> increment = reactiveRedisTemplate.opsForValue().increment(key, delta);
			return increment.doOnSuccess(newDelta -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> incr(String key, long delta, Duration timeout) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			Mono<Long> increment = reactiveRedisTemplate.opsForValue().increment(key, delta);
			return increment.doOnSuccess(newDelta -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
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
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			return reactiveRedisTemplate.opsForValue().increment(key, delta);
		} catch (Exception e) {
			return monoError(e);
		}
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
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			Mono<Double> increment = reactiveRedisTemplate.opsForValue().increment(key, delta);
			return increment.doOnSuccess(newDelta -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Double> incr(String key, double delta, Duration timeout) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			Mono<Double> increment = reactiveRedisTemplate.opsForValue().increment(key, delta);
			return increment.doOnSuccess(newDelta -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 递减
	 *
	 * @param key   键
	 * @param delta 要减少几(>=0)
	 * @return
	 */
	public Mono<Long> decr(String key, long delta) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递减因子必须>=0"));
		}
		try {
			return reactiveRedisTemplate.opsForValue().increment(key, -delta);
		} catch (Exception e) {
			return monoError(e);
		}
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
			return Mono.error(new RedisOperationException("递减因子必须>=0"));
		}
		try {
			Mono<Long> increment = reactiveRedisTemplate.opsForValue().increment(key, -delta);
			return increment.doOnSuccess(newDelta -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> decr(String key, long delta, Duration timeout) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递减因子必须>=0"));
		}
		try {
			Mono<Long> increment = reactiveRedisTemplate.opsForValue().increment(key, -delta);
			return increment.doOnSuccess(newDelta -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
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
			return Mono.error(new RedisOperationException("递减因子必须>=0"));
		}
		try {
			return reactiveRedisTemplate.opsForValue().increment(key, -delta);
		} catch (Exception e) {
			return monoError(e);
		}
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
			return Mono.error(new RedisOperationException("递减因子必须>=0"));
		}
		try {
			Mono<Double> increment = reactiveRedisTemplate.opsForValue().increment(key, -delta);
			return increment.doOnSuccess(newDelta -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Double> decr(String key, double delta, Duration timeout) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递减因子必须>=0"));
		}
		try {
			Mono<Double> increment = reactiveRedisTemplate.opsForValue().increment(key, -delta);
			return increment.doOnSuccess(newDelta -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}


	/**
	 * 删除缓存
	 * @param keys 可以传一个值 或多个
	 */
	public Mono<Long> del(String... keys) {
		try {
			if (keys != null && keys.length > 0) {
				if (keys.length == 1) {
					return reactiveRedisTemplate.delete(keys[0]);
				} else {
					return reactiveRedisTemplate.delete(keys);
				}
			}
		} catch (Exception e) {
			return monoError(e);
		}
		return Mono.just(0L);
	}

	// ===============================List=================================

	/**
	 * 获取list缓存的内容
	 *
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @return
	 */
	public Flux<Object> lRange(String key, long start, long end) {
		try {
			return reactiveRedisTemplate.opsForList().range(key, start, end);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<String> lRangeString(String key, long start, long end) {
		return lRangeFor(key, start, end, TO_STRING);
	}

	public Flux<Double> lRangeDouble(String key, long start, long end) {
		return lRangeFor(key, start, end, TO_DOUBLE);
	}

	public Flux<Long> lRangeLong(String key, long start, long end) {
		return lRangeFor(key, start, end, TO_LONG);
	}

	public Flux<Integer> lRangeInteger(String key, long start, long end) {
		return lRangeFor(key, start, end, TO_INTEGER);
	}

	/**
	 * 获取list缓存的内容
	 *
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @return
	 */
	public <T> Flux<T> lRangeFor(String key, long start, long end, Class<T> clazz) {
		return lRangeFor(key, start, end, member -> clazz.cast(member));
	}

	/**
	 * @param key    :
	 * @param start  :
	 * @param end    :0 到-1表示查全部
	 * @param mapper 对象转换函数
	 * @return {@link Set< Long>}
	 */
	public <T> Flux<T> lRangeFor(String key, long start, long end, Function<Object, T> mapper) {
		Flux<T> members = this.lRange(key, start, end).map(mapper);
		return members;
	}

	/**
	 * 通过索引 获取list中的值
	 *
	 * @param key   键
	 * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
	 * @return
	 */
	public Mono<Object> lIndex(String key, long index) {
		try {
			return reactiveRedisTemplate.opsForList().index(key, index);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lLeftPushDistinct(String key, V value) {
		try {
			return reactiveRedisTemplate.createMono((ReactiveRedisConnection connection) -> {
				ByteBuffer rawKey = getRawKey(key);
				ByteBuffer rawValue = getRawValue(value);
				ReactiveListCommands listCommands = connection.listCommands();
				return listCommands.lRem(rawKey, 0L, rawValue).then(listCommands.lPush(rawKey, Arrays.asList(rawValue)));
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}


	public <V> Mono<Long> lLeftPush(String key, V value) {
		return this.lLeftPush(key, value, 0);
	}

	public <V> Mono<Long> lLeftPush(String key, V value, long seconds) {
		if (value instanceof Collection) {
			return lLeftPushAll(key, (Collection) value, seconds);
		}
		try {
			return reactiveRedisTemplate.opsForList().leftPush(key, value).doOnSuccess(newDelta -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lLeftPush(String key, V value, Duration timeout) {
		if (value instanceof Collection) {
			return lLeftPushAll(key, (Collection) value, timeout);
		}
		try {
			return reactiveRedisTemplate.opsForList().leftPush(key, value).doOnSuccess(newDelta -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lLeftPushAll(String key, Collection<V> values) {
		try {
			return reactiveRedisTemplate.opsForList().leftPushAll(key, values.toArray());
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lLeftPushAll(String key, Collection<V> values, long seconds) {
		try {
			return reactiveRedisTemplate.opsForList().leftPushAll(key, values.toArray()).doOnSuccess(newDelta -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lLeftPushAll(String key, Collection<V> values, Duration timeout) {
		try {
			return reactiveRedisTemplate.opsForList().leftPushAll(key, values.toArray()).doOnSuccess(newDelta -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lLeftPushx(String key, V value) {
		return this.lLeftPushx(key, value, 0);
	}

	public <V> Mono<Long> lLeftPushx(String key, V value, long seconds) {
		if (value instanceof Collection) {
			return lLeftPushxAll(key, (Collection) value, seconds);
		}
		try {
			return reactiveRedisTemplate.opsForList().leftPushIfPresent(key, value).doOnSuccess(newDelta -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lLeftPushx(String key, V value, Duration timeout) {
		if (value instanceof Collection) {
			return lLeftPushxAll(key, (Collection) value, timeout);
		}
		try {
			return reactiveRedisTemplate.opsForList().leftPushIfPresent(key, value).doOnSuccess(newDelta -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lLeftPushxAll(String key, Collection<V> values, long seconds) {
		try {
			return Flux.fromIterable(values).flatMap(value -> {
				return reactiveRedisTemplate.opsForList().leftPushIfPresent(key, value);
			}).count().doOnSuccess(newDelta -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lLeftPushxAll(String key, Collection<V> values, Duration timeout) {
		try {
			return Flux.fromIterable(values).flatMap(value -> {
				return reactiveRedisTemplate.opsForList().leftPushIfPresent(key, value);
			}).count().doOnSuccess(newDelta -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Object> lLeftPop(String key) {
		try {
			return reactiveRedisTemplate.opsForList().leftPop(key);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Object> lLeftPopAndLrem(String key) {
		try {
			return reactiveRedisTemplate.createMono((ReactiveRedisConnection connection) -> {
				ByteBuffer rawKey = getRawKey(key);
				ReactiveListCommands listCommands = connection.listCommands();
				return listCommands.lPop(rawKey).doOnSuccess(rawValue -> listCommands.lRem(rawKey, 0L, rawValue)).map(rawValue -> getDeserializeValue(rawValue));
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Object> lLeftPop(String key, Duration timeout) {
		try {
			return reactiveRedisTemplate.opsForList().leftPop(key, timeout);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 从list左侧取count个元素并移除已经取出的元素
	 *
	 * @param key
	 * @param count
	 * @return
	 */
	public Flux<Object> lLeftPop(String key, Integer count) {
		try {
			return reactiveRedisTemplate.createFlux((ReactiveRedisConnection connection) -> {
				ByteBuffer rawKey = getRawKey(key);
				ReactiveListCommands listCommands = connection.listCommands();
				return listCommands.lRange(rawKey, 0, count - 1)
						.doOnNext(rawValue -> listCommands.lTrim(rawKey, count, -1))
						.map(rawValue -> getDeserializeValue(rawValue));
			});
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public <T> Flux<T> lLeftPop(String key, Integer count, Class<T> clazz) {
		try {
			Flux<Object> range = this.lLeftPop(key, count);
			return range.map(member -> clazz.cast(member));
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public <V> Mono<Long> lRightPushDistinct(String key, V value) {
		try {
			return reactiveRedisTemplate.createMono((ReactiveRedisConnection connection) -> {
				ByteBuffer rawKey = getRawKey(key);
				ByteBuffer rawValue = getRawValue(value);

				ReactiveListCommands listCommands = connection.listCommands();
				return listCommands.lRem(rawKey, 0L, rawValue)
						.then(listCommands.rPush(rawKey, Arrays.asList(rawValue)));
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 将对象放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public <V> Mono<Long> lRightPush(String key, V value) {
		return this.lRightPush(key, value, 0);
	}

	/**
	 * 将对象放入缓存
	 *
	 * @param key     键
	 * @param value   值
	 * @param seconds 时间(秒)
	 * @return
	 */
	public <V> Mono<Long> lRightPush(String key, V value, long seconds) {
		if (value instanceof Collection) {
			return lRightPushAll(key, (Collection) value, seconds);
		}
		try {
			Mono<Long> rt = reactiveRedisTemplate.opsForList().rightPush(key, value);
			return rt.doOnSuccess(c -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lRightPush(String key, V value, Duration timeout) {
		if (value instanceof Collection) {
			return lRightPushAll(key, (Collection) value, timeout);
		}
		try {
			Mono<Long> rt = reactiveRedisTemplate.opsForList().rightPush(key, value);
			return rt.doOnSuccess(c -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lRightPushAll(String key, Collection<V> values) {
		try {
			return reactiveRedisTemplate.opsForList().rightPushAll(key, values.toArray());
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lRightPushAll(String key, Collection<V> values, long seconds) {
		try {
			Mono<Long> rt = reactiveRedisTemplate.opsForList().rightPushAll(key, values.toArray());
			return rt.doOnSuccess(c -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lRightPushAll(String key, Collection<V> values, Duration timeout) {
		try {
			Mono<Long> rt = reactiveRedisTemplate.opsForList().rightPushAll(key, values.toArray());
			return rt.doOnSuccess(c -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 将对象放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public <V> Mono<Long> lRightPushx(String key, V value) {
		return this.lRightPushx(key, value, 0);
	}

	/**
	 * 将对象放入缓存
	 *
	 * @param key     键
	 * @param value   值
	 * @param seconds 时间(秒)
	 * @return
	 */
	public <V> Mono<Long> lRightPushx(String key, V value, long seconds) {
		if (value instanceof Collection) {
			return lRightPushxAll(key, (Collection) value, seconds);
		}
		try {
			Mono<Long> rt = reactiveRedisTemplate.opsForList().rightPushIfPresent(key, value);
			return rt.doOnSuccess(c -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lRightPushx(String key, V value, Duration timeout) {
		if (value instanceof Collection) {
			return lRightPushxAll(key, (Collection) value, timeout);
		}
		try {
			Mono<Long> rt = reactiveRedisTemplate.opsForList().rightPushIfPresent(key, value);
			return rt.doOnSuccess(c -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lRightPushxAll(String key, Collection<V> values, long seconds) {
		try {
			return Flux.fromIterable(values).flatMap(value -> {
				return reactiveRedisTemplate.opsForList().rightPushIfPresent(key, value);
			}).next().doOnSuccess(newDelta -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Long> lRightPushxAll(String key, Collection<V> values, Duration timeout) {
		try {
			return Flux.fromIterable(values).flatMap(value -> {
				return reactiveRedisTemplate.opsForList().rightPushIfPresent(key, value);
			}).next().doOnSuccess(newDelta -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Object> lRightPop(String key) {
		try {
			return reactiveRedisTemplate.opsForList().rightPop(key);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public <V> Mono<Object> lRightPopAndLrem(String key) {
		try {
			return reactiveRedisTemplate.createMono((ReactiveRedisConnection connection) -> {
				ByteBuffer rawKey = getRawKey(key);
				ReactiveListCommands listCommands = connection.listCommands();
				return listCommands.rPop(rawKey).doOnSuccess(rawValue -> listCommands.lRem(rawKey, 0L, rawValue))
						.map(rawValue -> getDeserializeValue(rawValue));
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Object> lRightPop(String key, Duration timeout) {
		try {
			return reactiveRedisTemplate.opsForList().rightPop(key, timeout);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 从list右侧取count个元素并移除已经去除的元素
	 *	1、Redis Ltrim 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
	 *  2、下标 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
	 * @param key
	 * @param count
	 * @return
	 */
	public Flux<Object> lRightPop(String key, Integer count) {
		try {
			return reactiveRedisTemplate.createFlux((ReactiveRedisConnection connection) -> {
				ByteBuffer rawKey = getRawKey(key);
				ReactiveListCommands listCommands = connection.listCommands();
				return listCommands.lRange(rawKey, -(count - 1), -1)
						.map(rawValue -> getDeserializeValue(rawValue))
						.doOnNext(rawValue -> listCommands.lTrim(rawKey, 0, -(count - 1)));
			});
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Mono<Object> lRightPopAndLeftPush(String sourceKey, String destinationKey) {
		try {
			return reactiveRedisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Object> lRightPopAndLeftPush(String sourceKey, String destinationKey, Duration timeout) {
		try {
			return reactiveRedisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout);
		} catch (Exception e) {
			return monoError(e);
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
	public Mono<Boolean> lSet(String key, long index, Object value) {
		try {
			return reactiveRedisTemplate.opsForList().set(key, index, value);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 获取list缓存的长度
	 *
	 * @param key 键
	 * @return
	 */
	public Mono<Long> lSize(String key) {
		try {
			return reactiveRedisTemplate.opsForList().size(key);
		} catch (Exception e) {
			return monoError(e);
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
	public Mono<Long> lRem(String key, long count, Object value) {
		try {
			return reactiveRedisTemplate.opsForList().remove(key, count, value);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * List删除: ltrim
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Mono<Boolean> lTrim(String key, long start, long end) {
		try {
			return reactiveRedisTemplate.opsForList().trim(key, start, end);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	// ================================Hash=================================


	/**
	 * hash递减
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   要减少记(小于0)
	 * @return
	 */
	public Mono<Long> hDecr(String key, String hashKey, int delta) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递减因子必须>=0"));
		}
		try {
			return reactiveRedisTemplate.opsForHash().increment(key, hashKey, -delta);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * hash递减
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   要减少记(>=0)
	 * @return
	 */
	public Mono<Long> hDecr(String key, String hashKey, long delta) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递减因子必须>=0"));
		}
		try {
			return reactiveRedisTemplate.opsForHash().increment(key, hashKey, -delta);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * hash递减
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   要减少记(>=0)
	 * @return
	 */
	public Mono<Double> hDecr(String key, String hashKey, double delta) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递减因子必须>=0"));
		}
		try {
			return reactiveRedisTemplate.opsForHash().increment(key, hashKey, -delta);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 删除hash表中的值
	 *
	 * @param key      键 不能为null
	 * @param hashKeys 项 可以使多个 不能为null
	 * @return
	 */
	public Mono<Long> hDel(String key, Object... hashKeys) {
		try {
			return reactiveRedisTemplate.opsForHash().remove(key, hashKeys);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * Hash删除: hscan + hdel
	 *
	 * @param bigHashKey
	 */
	public void hDel(String bigHashKey) {
		try {
			this.hScan(bigHashKey, (entry) -> {
				this.hDel(bigHashKey, entry.get().getKey());
			});
			this.del(bigHashKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 获取hashKey对应的指定键值
	 *
	 * @param key     键
	 * @param hashKey hash键
	 * @return 对应的键值
	 */
	public Mono<Object> hGet(String key, String hashKey) {
		try {
			return reactiveRedisTemplate.opsForHash().get(key, hashKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Object> hGet(String key, String hashKey, Object defaultVal) {
		try {
			Mono<Object> rtVal = reactiveRedisTemplate.opsForHash().get(key, hashKey);
			return rtVal.defaultIfEmpty(defaultVal);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<String> hGetString(String key, String hashKey) {
		return hGetFor(key, hashKey, TO_STRING);
	}

	public Mono<String> hGetString(String key, String hashKey, String defaultVal) {
		Mono<String> rtVal = hGetString(key, hashKey);
		return rtVal.defaultIfEmpty(defaultVal);
	}

	public Mono<Double> hGetDouble(String key, String hashKey) {
		return hGetFor(key, hashKey, TO_DOUBLE);
	}

	public Mono<Double> hGetDouble(String key, String hashKey, double defaultVal) {
		Mono<Double> rtVal = hGetDouble(key, hashKey);
		return rtVal.defaultIfEmpty(defaultVal);
	}

	public Mono<Long> hGetLong(String key, String hashKey) {
		return hGetFor(key, hashKey, TO_LONG);
	}

	public Mono<Long> hGetLong(String key, String hashKey, long defaultVal) {
		Mono<Long> rtVal = hGetLong(key, hashKey);
		return rtVal.defaultIfEmpty(defaultVal);
	}

	public Mono<Integer> hGetInteger(String key, String hashKey) {
		return hGetFor(key, hashKey, TO_INTEGER);
	}

	public Mono<Integer> hGetInteger(String key, String hashKey, int defaultVal) {
		Mono<Integer> rtVal = hGetInteger(key, hashKey);
		return rtVal.defaultIfEmpty(defaultVal);
	}

	public <T> Mono<T> hGetFor(String key, String hashKey, Class<T> clazz) {
		return hGetFor(key, hashKey, member -> clazz.cast(member));
	}

	public <T> Mono<T> hGetFor(String key, String hashKey, Function<Object, T> mapper) {
		Mono<Object> rt = this.hGet(key, hashKey);
		return rt.map(mapper);
	}

	public Flux<String> hGetString(Collection<String> keys, String hashKey) {
		return hGetFor(keys, hashKey, TO_STRING);
	}

	public Flux<Double> hGetDouble(Collection<String> keys, String hashKey) {
		return hGetFor(keys, hashKey, TO_DOUBLE);
	}

	public Flux<Long> hGetLong(Collection<String> keys, String hashKey) {
		return hGetFor(keys, hashKey, TO_LONG);
	}

	public Flux<Integer> hGetInteger(Collection<String> keys, String hashKey) {
		return hGetFor(keys, hashKey, TO_INTEGER);
	}

	public <T> Flux<T> hGetFor(Collection<String> keys, String hashKey, Class<T> clazz) {
		return hGetFor(keys, hashKey, member -> clazz.cast(member));
	}

	public <T> Flux<T> hGetFor(Collection<String> keys, String hashKey, Function<Object, T> mapper) {
		Flux<Object> members = this.hGet(keys, hashKey);
		return members.map(mapper);
	}

	public Flux<Object> hGet(Collection<String> keys, String hashKey) {
		try {
			return Flux.fromIterable(keys).flatMap(key -> {
				return reactiveRedisTemplate.opsForHash().get(key, hashKey);
			});
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<Object> hGet(Collection<String> keys, String redisPrefix, String hashKey) {
		try {
			return Flux.fromIterable(keys).flatMap(key -> {
				String nkey = RedisKeyConstant.getKeyStr(redisPrefix, String.valueOf(key));
				return reactiveRedisTemplate.opsForHash().get(nkey, hashKey);
			});
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	/**
	 * 判断hash表中是否有该项的值
	 *
	 * @param key     键 不能为null
	 * @param hashKey 项 不能为null
	 * @return true 存在 false不存在
	 */
	public Mono<Boolean> hHasKey(String key, String hashKey) {
		try {
			return reactiveRedisTemplate.opsForHash().hasKey(key, hashKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 获取hashKey对应的所有键值
	 *
	 * @param key 键
	 * @return 对应的多个键值
	 */
	public Mono<Map.Entry<Object, Object>> hmGet(String key) {
		try {
			return reactiveRedisTemplate.opsForHash().entries(key).last();
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Flux<Map.Entry<Object, Object>> hmGet(Collection<String> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return Flux.empty();
		}
		return Flux.fromIterable(keys).flatMap(key -> {
			return reactiveRedisTemplate.opsForHash().entries(key);
		});
	}

	public Flux<Map.Entry<Object, Object>> hmGet(Collection<String> keys, String redisPrefix) {
		if (CollectionUtils.isEmpty(keys)) {
			return Flux.empty();
		}
		return Flux.fromIterable(keys).flatMap(key -> {
			String nkey = RedisKeyConstant.getKeyStr(redisPrefix, key);
			return reactiveRedisTemplate.opsForHash().entries(nkey);
		});
	}

	public Mono<List<Object>> hMultiGet(String key, Collection<Object> hashKeys) {
		try {
			return reactiveRedisTemplate.opsForHash().multiGet(key, hashKeys);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Map<String, Object>> hmMultiGet(String key, Collection<Object> hashKeys) {
		try {
			Mono<List<Object>> rt = reactiveRedisTemplate.opsForHash().multiGet(key, hashKeys);
			return rt.map(list -> {
				Map<String, Object> ans = new HashMap<>(hashKeys.size());
				int index = 0;
				for (Object hashKey : hashKeys) {
					ans.put(hashKey.toString(), list.get(index));
					index++;
				}
				return ans;
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Flux<Map<String, Object>> hmMultiGet(Collection<String> keys, Collection<Object> hashKeys) {
		if (CollectionUtils.isEmpty(keys) || CollectionUtils.isEmpty(hashKeys)) {
			return Flux.empty();
		}
		return Flux.fromIterable(keys).flatMap(key -> {
			return this.hmMultiGet(key, hashKeys);
		});
	}

	public Mono<Map<String, Map<String, Object>>> hmMultiGet(Collection<String> keys, String identityHashKey,
			Collection<Object> hashKeys) {
		if (CollectionUtils.isEmpty(keys) || CollectionUtils.isEmpty(hashKeys)) {
			return Mono.empty();
		}
		return Flux.fromIterable(keys).flatMap(key -> {
			return this.hmMultiGet(key, hashKeys);
		}).collect(Collectors.toMap(kv -> MapUtils.getString(kv, identityHashKey), Function.identity()));
	}

	public Flux<Map.Entry<Object, Object>> hmMultiGetAll(Collection<String> keys) {
		try {
			if (CollectionUtils.isEmpty(keys)) {
				return Flux.empty();
			}
			return Flux.fromIterable(keys).flatMap(key -> {
				return reactiveRedisTemplate.opsForHash().entries(key);
			});
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<Map.Entry<Object, Object>> hmMultiGetAll(Collection<String> keys, String redisPrefix) {
		try {
			if (CollectionUtils.isEmpty(keys)) {
				return Flux.empty();
			}
			return Flux.fromIterable(keys).flatMap(key -> {
				String nkey = RedisKeyConstant.getKeyStr(redisPrefix, String.valueOf(key));
				return reactiveRedisTemplate.opsForHash().entries(nkey);
			});
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Mono<Boolean> hmMultiSet(String key, Collection<String> hashKeys, Object value) {
		if (CollectionUtils.isEmpty(hashKeys) || !StringUtils.hasText(key)) {
			return Mono.just(false);
		}
		try {
			return Flux.fromIterable(hashKeys).flatMap(hashKey -> {
				return reactiveRedisTemplate.opsForHash().put(key, hashKeys, value);
			}).all(rt -> rt == true);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * HashSet
	 *
	 * @param key 键
	 * @param map 对应多个键值
	 * @return true 成功 false 失败
	 */
	public Mono<Boolean> hmSet(String key, Map<String, Object> map) {
		try {
			return reactiveRedisTemplate.opsForHash().putAll(key, map);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * HashSet 并设置时间
	 *
	 * @param key     键
	 * @param map     对应多个键值
	 * @param seconds 时间(秒)
	 * @return true成功 false失败
	 */
	public Mono<Boolean> hmSet(String key, Map<String, Object> map, long seconds) {
		try {
			return reactiveRedisTemplate.opsForHash().putAll(key, map).doOnSuccess(nvalue -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Boolean> hmSet(String key, Map<String, Object> map, Duration timeout) {
		try {
			return reactiveRedisTemplate.opsForHash().putAll(key, map).doOnSuccess(nvalue -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public void hScan(String bigHashKey, Consumer<? super Signal<Entry<Object, Object>>> consumer) {
		reactiveRedisTemplate.opsForHash()
			.scan(bigHashKey, ScanOptions.scanOptions().count(Long.MAX_VALUE).build())
			.doOnEach(consumer);
	}

	public void hScan(String bigHashKey, String pattern, Consumer<? super Signal<Entry<Object, Object>>> consumer) {
		reactiveRedisTemplate.opsForHash()
			.scan(bigHashKey, ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())
			.doOnEach(consumer);
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param value   值
	 * @return true 成功 false失败
	 */
	public Mono<Boolean> hSet(String key, String hashKey, Object value) {
		try {
			return reactiveRedisTemplate.opsForHash().put(key, hashKey, value);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param value   值
	 * @param seconds    时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
	 * @return true 成功 false失败
	 */
	public Mono<Boolean> hSet(String key, String hashKey, Object value, long seconds) {
		try {
			Mono<Boolean> rt = reactiveRedisTemplate.opsForHash().put(key, hashKey, value);
			return rt.doOnSuccess(nvalue -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Boolean> hSet(String key, String hashKey, Object value, Duration timeout) {
		try {
			Mono<Boolean> rt = reactiveRedisTemplate.opsForHash().put(key, hashKey, value);
			return rt.doOnSuccess(nvalue -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Boolean> hSetNX(String key, String hashKey, Object value) {
		try {
			return reactiveRedisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * hash的大小
	 *
	 * @param key
	 * @return
	 */
	public Mono<Long> hSize(String key) {
		try {
			return reactiveRedisTemplate.opsForHash().size(key);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   要增加几(>=0)
	 * @return
	 */
	public Mono<Long> hIncr(String key, String hashKey, int delta) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			return reactiveRedisTemplate.opsForHash().increment(key, hashKey, delta);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   要增加几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Mono<Long> hIncr(String key, String hashKey, int delta, long seconds) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			Mono<Long> increment = reactiveRedisTemplate.opsForHash().increment(key, hashKey, delta);
			return increment.doOnSuccess(value -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> hIncr(String key, String hashKey, int delta, Duration timeout) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			Mono<Long> increment = reactiveRedisTemplate.opsForHash().increment(key, hashKey, delta);
			return increment.doOnSuccess(value -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   要增加几(>=0)
	 * @return
	 */
	public Mono<Long> hIncr(String key, String hashKey, long delta) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			return reactiveRedisTemplate.opsForHash().increment(key, hashKey, delta);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   要增加几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Mono<Long> hIncr(String key, String hashKey, long delta, long seconds) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			Mono<Long> increment = reactiveRedisTemplate.opsForHash().increment(key, hashKey, delta);
			return increment.doOnSuccess(value -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> hIncr(String key, String hashKey, long delta, Duration timeout) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			Mono<Long> increment = reactiveRedisTemplate.opsForHash().increment(key, hashKey, delta);
			return increment.doOnSuccess(value -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param delta   要增加几(>=0)
	 * @return
	 */
	public Mono<Double> hIncr(String key, String hashKey, double delta) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			return reactiveRedisTemplate.opsForHash().increment(key, hashKey, delta);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Double> hIncr(String key, String hashKey, double delta, long seconds) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			Mono<Double> increment = reactiveRedisTemplate.opsForHash().increment(key, hashKey, delta);
			return increment.doOnSuccess(value -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Double> hIncr(String key, String hashKey, double delta, Duration timeout) {
		if (delta < 0) {
			return Mono.error(new RedisOperationException("递增因子必须>=0"));
		}
		try {
			Mono<Double> increment = reactiveRedisTemplate.opsForHash().increment(key, hashKey, delta);
			return increment.doOnSuccess(value -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Flux<Object> hKeys(String key) {
		try {
			return reactiveRedisTemplate.opsForHash().keys(key);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	// ============================Set=============================

	/**
	 * 将数据放入set缓存
	 *
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 成功个数
	 */
	public Mono<Long> sAdd(String key, Object... values) {
		try {
			return reactiveRedisTemplate.opsForSet().add(key, values);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> sAddAndExpire(String key, long seconds, Object... values) {
		try {
			Mono<Long> rt = reactiveRedisTemplate.opsForSet().add(key, values);
			return rt.doOnSuccess(value -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> sAddAndExpire(String key, Duration timeout, Object... values) {
		try {
			Mono<Long> rt = reactiveRedisTemplate.opsForSet().add(key, values);
			return rt.doOnSuccess(value -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * Set删除: sscan + srem
	 *
	 * @param bigSetKey 键
	 * @return
	 * @return
	 */
	public Mono<Boolean> sDel(String bigSetKey) {
		try {
			this.sScan(bigSetKey, (value) -> {
				reactiveRedisTemplate.opsForSet().remove(bigSetKey, getDeserializeValue(value.get()));
			});
			return reactiveRedisTemplate.delete(bigSetKey).map(ct -> ct > 0);
		} catch (Exception e) {
			return monoError(e);
		}
	}

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
			return fluxError(e);
		}
	}

	public Flux<String> sGetString(String key) {
		return sGetFor(key, TO_STRING);
	}

	public Flux<Double> sGetDouble(String key) {
		return sGetFor(key, TO_DOUBLE);
	}

	public Flux<Long> sGetLong(String key) {
		return sGetFor(key, TO_LONG);
	}

	public Flux<Integer> sGetInteger(String key) {
		return sGetFor(key, TO_INTEGER);
	}

	/**
	 * 根据key获取Set中的所有值
	 *
	 * @param key   键
	 * @param clazz 值的类型
	 * @return 类型处理后的Set
	 */
	public <T> Flux<T> sGetFor(String key, Class<T> clazz) {
		return sGetFor(key, member -> clazz.cast(member));
	}

	/**
	 * 根据key获取Set中的所有值，并按Function函数进行转换
	 *
	 * @param key    键
	 * @param mapper 对象转换函数
	 * @return 类型处理后的Set
	 */
	public <T> Flux<T> sGetFor(String key, Function<Object, T> mapper) {
		Flux<Object> members = this.sGet(key);
		return members.map(mapper);
	}

	/**
	 * 获取两个key的不同value
	 *
	 * @param key      键
	 * @param otherKey 键
	 * @return 返回key中和otherKey的不同数据
	 */
	public Flux<Object> sDiff(String key, String otherKey) {
		try {
			return reactiveRedisTemplate.opsForSet().difference(key, otherKey);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	/**
	 * 获取两个key的不同数据，存储到destKey中
	 *
	 * @param key      键
	 * @param otherKey 键
	 * @param destKey  键
	 * @return 返回成功数据
	 */
	public Mono<Long> sDiffAndStore(String key, String otherKey, String destKey) {
		try {
			return reactiveRedisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 获取key和keys的不同数据，存储到destKey中
	 *
	 * @param key     键
	 * @param keys    键集合
	 * @param destKey 键
	 * @return 返回成功数据
	 */
	public Mono<Long> sDiffAndStore(String key, Collection<String> keys, String destKey) {
		try {
			return reactiveRedisTemplate.opsForSet().differenceAndStore(key, keys, destKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 获取多个keys的不同数据，存储到destKey中
	 *
	 * @param keys    键集合
	 * @param destKey 键
	 * @return 返回成功数据
	 */
	public Mono<Long> sDiffAndStore(Collection<String> keys, String destKey) {
		try {
			return reactiveRedisTemplate.opsForSet().differenceAndStore(keys, destKey);
		} catch (Exception e) {
			return monoError(e);
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
			return monoError(e);
		}
	}

	public Flux<Object> sIntersect(String key, String otherKey) {
		try {
			return reactiveRedisTemplate.opsForSet().intersect(key, otherKey);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<Object> sIntersect(String key, Collection<String> otherKeys) {
		try {
			return reactiveRedisTemplate.opsForSet().intersect(key, otherKeys);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<Object> sIntersect(Collection<String> otherKeys) {
		try {
			return reactiveRedisTemplate.opsForSet().intersect(otherKeys);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Mono<Long> sIntersectAndStore(String key, String otherKey, String destKey) {
		try {
			return reactiveRedisTemplate.opsForSet().intersectAndStore(key, otherKey, destKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> sIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
		try {
			return reactiveRedisTemplate.opsForSet().intersectAndStore(key, otherKeys, destKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> sIntersectAndStore(Collection<String> otherKeys, String destKey) {
		try {
			return reactiveRedisTemplate.opsForSet().intersectAndStore(otherKeys, destKey);
		} catch (Exception e) {
			return monoError(e);
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
		try {
			return reactiveRedisTemplate.opsForSet().randomMembers(key, count);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	/**
	 * 随机获取指定数量的元素,去重(同一个元素只能选择一次)
	 *
	 * @param key
	 * @param count
	 * @return
	 */
	public Flux<Object> sRandomSetDistinct(String key, long count) {
		try {
			return reactiveRedisTemplate.opsForSet().distinctRandomMembers(key, count);
		} catch (Exception e) {
			return fluxError(e);
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
			return reactiveRedisTemplate.opsForSet().remove(key, values);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public void sScan(String bigSetKey, Consumer<? super Signal<ByteBuffer>> consumer) {
		reactiveRedisTemplate.execute((ReactiveRedisConnection redisConnection) -> {
			return redisConnection.setCommands()
					.sScan(getRawKey(bigSetKey), ScanOptions.scanOptions().count(Long.MAX_VALUE).build())
					.doOnEach(consumer);
		});
	}

	public void sScan(String bigSetKey, String pattern, Consumer<? super Signal<ByteBuffer>> consumer) {
		reactiveRedisTemplate.execute((ReactiveRedisConnection redisConnection) -> {
			return redisConnection.setCommands()
					.sScan(getRawKey(bigSetKey), ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())
					.doOnEach(consumer);
		});
	}

	/**
	 * 将set数据放入缓存
	 *
	 * @param key     键
	 * @param seconds 过期时长(秒)
	 * @param values  值 可以是多个
	 * @return 成功个数
	 */
	public Mono<Long> sSetAndTime(String key, long seconds, Object... values) {
		try {
			Mono<Long> rt = reactiveRedisTemplate.opsForSet().add(key, values);
			return rt.doOnSuccess(newDelta -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 获取set缓存的长度
	 *
	 * @param key 键
	 * @return
	 */
	public Mono<Long> sSize(String key) {
		try {
			return reactiveRedisTemplate.opsForSet().size(key);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Flux<Object> sUnion(String key, String otherKey) {
		try {
			return reactiveRedisTemplate.opsForSet().union(key, otherKey);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<Object> sUnion(String key, Collection<String> keys) {
		try {
			return reactiveRedisTemplate.opsForSet().union(key, keys);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	/**
	 * 合并所有指定keys的数据
	 *
	 * @param keys 键集合
	 * @return 返回成功数据
	 */
	public Flux<Object> sUnion(Collection<String> keys) {
		try {
			return reactiveRedisTemplate.opsForSet().union(keys);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Mono<Long> sUnionAndStore(String key, String otherKey, String destKey) {
		try {
			return reactiveRedisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> sUnionAndStore(String key, Collection<String> keys, String destKey) {
		try {
			return reactiveRedisTemplate.opsForSet().unionAndStore(key, keys, destKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 合并所有指定keys的数据，存储到destKey中
	 *
	 * @param keys    键集合
	 * @param destKey 键
	 * @return 返回成功数据
	 */
	public Mono<Long> sUnionAndStore(Collection<String> keys, String destKey) {
		try {
			return reactiveRedisTemplate.opsForSet().unionAndStore(keys, destKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	// ===============================ZSet=================================

	public Mono<Boolean> zAdd(String key, Object value, double score) {
		try {
			return reactiveRedisTemplate.opsForZSet().add(key, value, score);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> zAdd(String key, Set<TypedTuple<Object>> tuples) {
		try {
			return reactiveRedisTemplate.opsForZSet().addAll(key, tuples);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> zCard(String key) {
		try {
			return reactiveRedisTemplate.opsForZSet().size(key);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Boolean> zHas(String key, Object value) {
		try {
			return reactiveRedisTemplate.opsForZSet().score(key, value).flatMap(score -> Mono.just(Objects.nonNull(score)));
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 通过分数返回有序集合指定区间内的成员个数
	 *
	 * @param key
	 * @param range
	 */
	public Mono<Long> zCount(String key, Range<Double> range) {
		try {
			return reactiveRedisTemplate.opsForZSet().count(key, range);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * Set删除: sscan + srem
	 *
	 * @param bigZsetKey 键
	 * @return
	 */
	public Mono<Boolean> zDel(String bigZsetKey) {
		try {
			this.zScan(bigZsetKey, (tuple) -> {
				this.zRem(bigZsetKey, tuple.get().getValue());
			});
			return reactiveRedisTemplate.delete(bigZsetKey).map(ct -> ct > 0);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Double> zIncr(String key, Object value, double delta) {
		try {
			return reactiveRedisTemplate.opsForZSet().incrementScore(key, value, delta);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Double> zIncr(String key, Object value, double delta, long seconds) {
		try {
			Mono<Double> rt = reactiveRedisTemplate.opsForZSet().incrementScore(key, value, delta);
			return rt.doOnSuccess(newDelta -> {
				if (seconds > 0) {
					expire(key, seconds);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Double> zIncr(String key, Object value, double delta, Duration timeout) {
		try {
			Mono<Double> rt = reactiveRedisTemplate.opsForZSet().incrementScore(key, value, delta);
			return rt.doOnSuccess(newDelta -> {
				if (!timeout.isNegative()) {
					expire(key, timeout);
				}
			});
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> zIntersectAndStore(String key, String otherKey, String destKey) {
		try {
			return reactiveRedisTemplate.opsForZSet().intersectAndStore(key, otherKey, destKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> zIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
		try {
			return reactiveRedisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> zIntersectAndStore(String key, Collection<String> otherKeys, String destKey, Aggregate aggregate) {
		try {
			return reactiveRedisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey, aggregate);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> zIntersectAndStore(String key, Collection<String> otherKeys, String destKey, Aggregate aggregate,
			Weights weights) {
		try {
			return reactiveRedisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey, aggregate, weights);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 移除zset中的元素
	 *
	 * @param key
	 * @param values
	 */
	public Mono<Long> zRem(String key, Object... values) {
		try {
			return reactiveRedisTemplate.opsForZSet().remove(key, values);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 移除分数区间内的元素
	 *
	 * @param key
	 * @param range
	 */
	public Mono<Long> zRemByScore(String key, Range<Double> range) {
		try {
			return reactiveRedisTemplate.opsForZSet().removeRangeByScore(key, range);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Flux<Object> zRange(String key, Range<Long> range) {
		try {
			return reactiveRedisTemplate.opsForZSet().range(key, range);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<String> zRangeString(String key, Range<Long> range) {
		return zRangeFor(key, range, TO_STRING);
	}

	public Flux<Double> zRangeDouble(String key, Range<Long> range) {
		return zRangeFor(key, range, TO_DOUBLE);
	}

	public Flux<Long> zRangeLong(String key, Range<Long> range) {
		return zRangeFor(key, range, TO_LONG);
	}

	public Flux<Integer> zRangeInteger(String key, Range<Long> range) {
		return zRangeFor(key, range, TO_INTEGER);
	}

	public <T> Flux<T> zRangeFor(String key, Range<Long> range, Class<T> clazz) {
		return zRangeFor(key, range, member -> clazz.cast(member));
	}

	/**
	 * @param key   :
	 * @param range
	 * @param mapper 对象转换函数
	 * @return {@link Set<T>}
	 */
	public <T> Flux<T> zRangeFor(String key, Range<Long> range, Function<Object, T> mapper) {
		Flux<Object> members = this.zRange(key, range);
		return members.map(mapper);
	}

	public Flux<Object> zRangeByScore(String key, Range<Double> range) {
		try {
			return reactiveRedisTemplate.opsForZSet().rangeByScore(key, range);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<String> zRangeStringByScore(String key, Range<Double> range) {
		return zRangeByScoreFor(key, range, TO_STRING);
	}

	public Flux<Double> zRangeDoubleByScore(String key, Range<Double> range) {
		return zRangeByScoreFor(key, range, TO_DOUBLE);
	}

	public Flux<Long> zRangeLongByScore(String key, Range<Double> range) {
		return zRangeByScoreFor(key, range, TO_LONG);
	}

	public Flux<Integer> zRangeIntegerByScore(String key, Range<Double> range) {
		return zRangeByScoreFor(key, range, TO_INTEGER);
	}

	public <T> Flux<T> zRangeByScoreFor(String key, Range<Double> range, Class<T> clazz) {
		return zRangeByScoreFor(key, range, member -> clazz.cast(member));
	}

	public <T> Flux<T> zRangeByScoreFor(String key, Range<Double> range, Function<Object, T> mapper) {
		Flux<Object> members = this.zRangeByScore(key, range);
		return members.map(mapper);
	}

	public Flux<TypedTuple<Object>> zRangeWithScores(String key, Range<Long> range) {
		try {
			return reactiveRedisTemplate.opsForZSet().rangeWithScores(key, range);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	/**
	 * 在min到max范围内倒序获取zset及对应的score
	 */
	public Flux<TypedTuple<Object>> zRangeByScoreWithScores(String key, Range<Double> range) {
		try {
			return reactiveRedisTemplate.opsForZSet().rangeByScoreWithScores(key, range);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<Object> zRangeByLex(String key, Range range) {
		try {
			return reactiveRedisTemplate.opsForZSet().rangeByLex(key, range);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<Object> zRangeByLex(String key, Range range, Limit limit) {
		try {
			return reactiveRedisTemplate.opsForZSet().rangeByLex(key, range, limit);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	/**
	 * @param key   :
	 * @param range :0 到-1表示查全部
	 * @return {@link Set< Object>}
	 */
	public Flux<Object> zRevrange(String key, Range<Long> range) {
		try {
			return reactiveRedisTemplate.opsForZSet().reverseRange(key, range);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<String> zRevrangeString(String key, Range<Long> range) {
		return zRevrangeFor(key, range, TO_STRING);
	}

	public Flux<Double> zRevrangeDouble(String key, Range<Long> range) {
		return zRevrangeFor(key, range, TO_DOUBLE);
	}

	public Flux<Long> zRevrangeLong(String key, Range<Long> range) {
		return zRevrangeFor(key, range, TO_LONG);
	}

	public Flux<Integer> zRevrangeInteger(String key, Range<Long> range) {
		return zRevrangeFor(key, range, TO_INTEGER);
	}

	public <T> Flux<Object> zRevrangeFor(String key, Range<Long> range, Class<T> clazz) {
		return zRevrangeFor(key, range, member -> clazz.cast(member));
	}

	public <T> Flux<T> zRevrangeFor(String key, Range<Long> range, Function<Object, T> mapper) {
		Flux<Object> members = this.zRevrange(key, range);
		return members.map(mapper);
	}

	/**
	 * 获取指定key的scores正序，指定start-end位置的元素
	 *
	 * @param key
	 * @param range
	 * @return
	 */
	public Flux<TypedTuple<Object>> zRevrangeWithScores(String key, Range<Long> range) {
		try {
			return reactiveRedisTemplate.opsForZSet().reverseRangeWithScores(key, range);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	/**
	 * 获取指定key的scores正序，指定start-end位置的元素
	 *
	 * @param key
	 * @param range
	 * @return
	 */
	public Flux<Object> zRevrangeByScore(String key, Range<Double> range) {
		try {
			return reactiveRedisTemplate.opsForZSet().reverseRangeByScore(key, range);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Flux<String> zRevrangeStringByScore(String key, Range<Double> range) {
		return zRevrangeForByScore(key, range, TO_STRING);
	}

	public Flux<Double> zRevrangeDoubleByScore(String key, Range<Double> range) {
		return zRevrangeForByScore(key, range, TO_DOUBLE);
	}

	public Flux<Long> zRevrangeLongByScore(String key, Range<Double> range) {
		return zRevrangeForByScore(key, range, TO_LONG);
	}

	public Flux<Integer> zRevrangeIntegerByScore(String key, Range<Double> range) {
		return zRevrangeForByScore(key, range, TO_INTEGER);
	}

	public <T> Flux<T> zRevrangeForByScore(String key, Range<Double> range, Class<T> clazz) {
		return zRevrangeForByScore(key, range, member -> clazz.cast(member));
	}

	public <T> Flux<T> zRevrangeForByScore(String key, Range<Double> range, Function<Object, T> mapper) {
		Flux<Object> members = this.zRevrangeByScore(key, range);
		return members.map(mapper);
	}

	/**
	 * 获取指定key的scores正序，指定start-end位置的元素
	 *
	 * @param key
	 * @param range
	 * @return
	 */
	public Flux<TypedTuple<Object>> zRevrangeByScoreWithScores(String key, Range<Double> range) {
		try {
			return reactiveRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, range);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	public Mono<Long> zRevRank(String key, Object value) {
		try {
			return reactiveRedisTemplate.opsForZSet().reverseRank(key, value);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public void zScan(String bigZsetKey, Consumer<? super Signal<TypedTuple<Object>>> consumer) {
		reactiveRedisTemplate.opsForZSet()
			.scan(bigZsetKey, ScanOptions.scanOptions().count(Long.MAX_VALUE).build())
			.doOnEach(consumer);
	}

	public void zScan(String bigZsetKey, String pattern, Consumer<? super Signal<TypedTuple<Object>>> consumer) {
		reactiveRedisTemplate.opsForZSet()
		.scan(bigZsetKey, ScanOptions.scanOptions().match(pattern).count(Long.MAX_VALUE).build())
		.doOnEach(consumer);
	}

	public Mono<Double> zScore(String key, Object value) {
		try {
			return reactiveRedisTemplate.opsForZSet().score(key, value);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> zUnionAndStore(String key, String otherKey, String destKey) {
		try {
			return reactiveRedisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> zUnionAndStore(String key, Collection<String> keys, String destKey) {
		try {
			return reactiveRedisTemplate.opsForZSet().unionAndStore(key, keys, destKey);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> zUnionAndStore(String key, Collection<String> keys, String destKey, Aggregate aggregate) {
		try {
			return reactiveRedisTemplate.opsForZSet().unionAndStore(key, keys, destKey, aggregate);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> zUnionAndStore(String key, Collection<String> keys, String destKey, Aggregate aggregate, Weights weights) {
		try {
			return reactiveRedisTemplate.opsForZSet().unionAndStore(key, keys, destKey, aggregate, weights);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	// ===============================HyperLogLog=================================

	public Mono<Long> pfAdd(String key, Object... values) {
		try {
			return reactiveRedisTemplate.opsForHyperLogLog().add(key, values);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Boolean> pfDel(String key) {
		try {
			return reactiveRedisTemplate.opsForHyperLogLog().delete(key);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Long> pfCount(String... keys) {
		try {
			return reactiveRedisTemplate.opsForHyperLogLog().size(keys);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Boolean> pfMerge(String destination, String... sourceKeys) {
		try {
			return reactiveRedisTemplate.opsForHyperLogLog().union(destination, sourceKeys);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	// ===============================BitMap=================================

	public Mono<Boolean> setBit(String key, long offset, boolean value) {
		try {
			return reactiveRedisTemplate.opsForValue().setBit(key, offset, value);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Mono<Boolean> getBit(String key, long offset) {
		try {
			return reactiveRedisTemplate.opsForValue().getBit(key, offset);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	// ===============================Message=================================

	/**
	 * 发送消息
	 *
	 * @param channel
	 * @param message
	 * @return
	 */
	public Mono<Long> sendMessage(String channel, String message) {
		try {
			return reactiveRedisTemplate.convertAndSend(channel, message);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	public Flux<? extends Message<String, Object>> listenTo(Topic... topics) {
		try {
			return reactiveRedisTemplate.listenTo(topics);
		} catch (Exception e) {
			return fluxError(e);
		}
	}

	// ===============================Lock=================================

	/**
	 * 1、对指定key来进行加锁逻辑（此锁是分布式阻塞锁）
	 * https://www.jianshu.com/p/6dbc44defd94
	 * @param lockKey  锁 key
	 * @param seconds  最大阻塞时间(秒)，超过时间将不再等待拿锁
	 * @return 获取锁成功/失败
	 */
	public Mono<Boolean> tryBlockLock(String lockKey, int seconds) {
        try {
			return reactiveRedisTemplate.createMono((ReactiveRedisCallback<Boolean>) redisConnection -> {
			    // 1、获取时间毫秒值
			    long expireAt = redisConnection.serverCommands().time().block() + seconds * 1000 + 1;
			    // 2、第一次请求, 锁标识不存在的情况，直接拿到锁
			    Boolean acquire = redisConnection.stringCommands().setNX(getRawKey(lockKey), ByteBuffer.wrap(String.valueOf(expireAt).getBytes())).block();
			    if (acquire) {
			    	return Mono.just(true);
			    } else {
			    	// 3、非第一次请求，阻塞等待拿到锁
			    	redisConnection.listCommands().brPop(Arrays.asList(getRawKey(lockKey + ":list")), Duration.ofSeconds(seconds));
			    }
			    return Mono.just(false);
			});
        } catch (Exception e) {
			log.error("acquire redis occurred an exception", e);
			return monoError(e);
		}
    }

	/**
	 * 2、删除指定key来进行完成解锁逻辑
	 * @param lockKey  锁key
	 * @param requestId  锁值
	 * @return 释放锁成功/失败
	 */
    public Mono<Boolean> unBlockLock(String lockKey, String requestId) {
		try {
			return reactiveRedisTemplate.opsForValue().delete(lockKey)
					.then(reactiveRedisTemplate.opsForList().rightPush(lockKey + ":list", requestId)).map(rt -> rt > 0);
		} catch (Exception e) {
			return monoError(e);
		}
	}

    public Mono<Boolean> tryLock(String lockKey, Duration timeout) {
		return tryLock( lockKey, timeout.toMillis());
	}

	/**
	 * 1、对指定key来进行加锁逻辑（此锁是全局性的）
	 * @param lockKey  锁key
	 * @return
	 */
	public Mono<Boolean> tryLock(String lockKey, long expireMillis) {
        try {
			return reactiveRedisTemplate.createMono((ReactiveRedisCallback<Boolean>) redisConnection -> {
				ByteBuffer serLockKey = getRawKey(lockKey);
			    // 1、获取时间毫秒值
			    long expireAt = redisConnection.serverCommands().time().block() + expireMillis + 1;
			    // 2、获取锁
			    Boolean acquire = redisConnection.stringCommands().setNX(serLockKey, ByteBuffer.wrap(String.valueOf(expireAt).getBytes())).block();
			    if (acquire) {
			        return Mono.just(true);
			    } else {
			    	ByteBuffer buffer = redisConnection.stringCommands().get(serLockKey).block();
			        // 3、非空判断
			        if (Objects.nonNull(buffer) && buffer.hasArray()) {
			            long expireTime = Long.parseLong(new String(buffer.array()));
			            // 4、如果锁已经过期
			            if (expireTime < redisConnection.serverCommands().time().block()) {
			                // 5、重新加锁，防止死锁
			            	ByteBuffer set = redisConnection.stringCommands().getSet(serLockKey, ByteBuffer.wrap(String.valueOf(redisConnection.serverCommands().time().block() + expireMillis + 1).getBytes())).block();
			            	 return Mono.just(Long.parseLong(new String(set.array())) < redisConnection.serverCommands().time().block());
			            }
			        }
			    }
		        return Mono.just(false);
			});
        } catch (Exception e) {
			log.error("acquire redis occurred an exception", e);
		}
        return Mono.just(false);
    }

	/**
	 * 2、删除指定key来进行完成解锁逻辑
	 * @param lockKey  锁key
	 * @return
	 */
    public Mono<Boolean> unlock(String lockKey) {
    	try {
	        return reactiveRedisTemplate.delete(lockKey).map(ct -> ct > 0);
        } catch (Exception e) {
			log.error("acquire redis occurred an exception", e);
			throw new RedisOperationException(e.getMessage());
		}
	}

    public Mono<Boolean> tryLock(String lockKey, String requestId, Duration timeout, int retryTimes, long retryInterval) {
    	return tryLock(lockKey, requestId, timeout.toMillis(), retryTimes, retryInterval);
    }

    /**
	 * 1、lua脚本加锁
	 * @param lockKey       锁的 key
	 * @param requestId     锁的 value
	 * @param expire        key 的过期时间，单位 ms
	 * @param retryTimes    重试次数，即加锁失败之后的重试次数
	 * @param retryInterval 重试时间间隔，单位 ms
	 * @return 加锁 true 成功
	 */
	public Mono<Boolean> tryLock(String lockKey, String requestId, long expire, int retryTimes, long retryInterval) {
       try {
			return reactiveRedisTemplate.createMono((ReactiveRedisCallback<Boolean>) redisConnection -> {
				// 1、执行lua脚本
				Long result =  this.executeLuaScript(LOCK_LUA_SCRIPT, Collections.singletonList(lockKey), requestId, expire).block();
				if(LOCK_SUCCESS.equals(result)) {
				    log.info("locked... redisK = {}", lockKey);
				    return Mono.just(true);
				} else {
					// 2、重试获取锁
			        int count = 0;
			        while(count < retryTimes) {
			            try {
			                Thread.sleep(retryInterval);
			                result = this.executeLuaScript(LOCK_LUA_SCRIPT, Collections.singletonList(lockKey), requestId, expire).block();
			                if(LOCK_SUCCESS.equals(result)) {
			                	log.info("locked... redisK = {}", lockKey);
			                	return Mono.just(true);
			                }
			                log.warn("{} times try to acquire lock", count + 1);
			                count++;
			            } catch (Exception e) {
			            	log.error("acquire redis occurred an exception", e);
			            }
			        }
			        log.info("fail to acquire lock {}", lockKey);
			        return Mono.just(false);
				}
			});
		} catch (Exception e) {
			log.error("acquire redis occurred an exception", e);
		}
       	return Mono.just(false);
	}

	/**
	 * 2、lua脚本释放KEY
	 * @param lockKey 释放本请求对应的锁的key
	 * @param requestId   释放本请求对应的锁的value
	 * @return 释放锁 true 成功
	 */
    public Mono<Boolean> unlock(String lockKey, String requestId) {
        log.info("unlock... redisK = {}", lockKey);
        try {
            // 使用lua脚本删除redis中匹配value的key
        	Long result = this.executeLuaScript(UNLOCK_LUA_SCRIPT, Collections.singletonList(lockKey), requestId).block();
            //如果这里抛异常，后续锁无法释放
            if (LOCK_SUCCESS.equals(result)) {
            	log.info("release lock success. redisK = {}", lockKey);
                return Mono.just(true);
            } else if (LOCK_EXPIRED.equals(result)) {
            	log.warn("release lock exception, key has expired or released");
            } else {
                //其他情况，一般是删除KEY失败，返回0
            	log.error("release lock failed");
            }
        } catch (Exception e) {
        	log.error("release lock occurred an exception", e);
			throw new RedisOperationException(e.getMessage());
        }
        return Mono.just(false);
    }

	// ===============================RedisScript=================================

	/**
     * 库存增加
     * @param key   库存key
	 * @param delta 增加数量
     * @return
     * -4:代表库存传进来的值是负数（非法值）
     * -3:库存未初始化
     * 大于等于0:剩余库存（新增之后剩余的库存）
     */
	public Mono<Long> luaIncr(String key, long delta) {
		Assert.hasLength(key, "key must not be empty");
		try {
			return this.executeLuaScript(INCR_SCRIPT, Lists.newArrayList(key), delta);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
     * 库存增加
     * @param key   库存key
	 * @param delta 增加数量
     * @return
     * -4:代表库存传进来的值是负数（非法值）
     * -3:库存未初始化
     * 大于等于0:剩余库存（新增之后剩余的库存）
     */
	public Mono<Double> luaIncr(String key, double delta) {
		Assert.hasLength(key, "key must not be empty");
		try {
			Mono<Object> rst = this.executeLuaScript(INCR_BYFLOAT_SCRIPT, Lists.newArrayList(key), delta);
			return rst.map(TO_DOUBLE);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
     * 库存扣减
	 * @param key   库存key
	 * @param delta 扣减数量
	 * @return
     * -4:代表库存传进来的值是负数（非法值）
     * -3:库存未初始化
     * -2:库存不足
     * -1:库存为0
     * 大于等于0:剩余库存（扣减之后剩余的库存）
	 */
	public Mono<Long> luaDecr(String key, long delta) {
		Assert.hasLength(key, "key must not be empty");
		try {
			return this.executeLuaScript(DECR_SCRIPT, Lists.newArrayList(key), delta);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
     * 库存扣减
	 * @param key   库存key
	 * @param delta 扣减数量
	 * @return
     * -4:代表库存传进来的值是负数（非法值）
     * -3:库存未初始化
     * -2:库存不足
     * -1:库存为0
     * 大于等于0:剩余库存（扣减之后剩余的库存）
	 */
	public Mono<Double> luaDecr(String key, double delta) {
		Assert.hasLength(key, "key must not be empty");
		try {
			Mono<Object> rst = this.executeLuaScript(DECR_BYFLOAT_SCRIPT, Lists.newArrayList(key), delta);
			return rst.map(TO_DOUBLE);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
     * 库存增加
     * @param key   库存key
	 * @param hashKey Hash键
	 * @param delta 增加数量
     * @return
     * -4:代表库存传进来的值是负数（非法值）
     * -3:库存未初始化
     * 大于等于0:剩余库存（新增之后剩余的库存）
     */
	public Mono<Long> luaHincr(String key, String hashKey, long delta) {
		Assert.hasLength(key, "key must not be empty");
		try {

			SerializationPair<Long> serializationPair = reactiveRedisTemplate.getSerializationContext().getHashValueSerializationPair();

			Flux<Long> rst = reactiveRedisTemplate.execute(HINCR_SCRIPT, Lists.newArrayList(key, hashKey),
					Arrays.asList(delta), serializationPair.getWriter(), serializationPair.getReader());

			return rst.last().map(TO_LONG);

		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
     * 库存增加
     * @param key   库存key
	 * @param hashKey Hash键
	 * @param delta 增加数量
     * @return
     * -4:代表库存传进来的值是负数（非法值）
     * -3:库存未初始化
     * 大于等于0:剩余库存（新增之后剩余的库存）
     */
	public Mono<Double> luaHincr(String key, String hashKey, double delta) {
		Assert.hasLength(key, "key must not be empty");
		try {

			SerializationPair<Object> serializationPair = reactiveRedisTemplate.getSerializationContext().getHashValueSerializationPair();

			Flux<Object> rst = reactiveRedisTemplate.execute(HINCR_BYFLOAT_SCRIPT, Lists.newArrayList(key, hashKey),
					Arrays.asList(delta), serializationPair.getWriter(),
					serializationPair.getReader());
			;

			return rst.last().map(TO_DOUBLE);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
     * 库存扣减
	 * @param key   库存key
	 * @param hashKey Hash键
	 * @param delta 扣减数量
	 * @return
     * -4:代表库存传进来的值是负数（非法值）
     * -3:库存未初始化
     * -2:库存不足
     * -1:库存为0
     * 大于等于0:剩余库存（扣减之后剩余的库存）
	 */
	public Mono<Long> luaHdecr(String key, String hashKey, long delta) {
		Assert.hasLength(key, "key must not be empty");
		try {

			SerializationPair<Long> serializationPair = reactiveRedisTemplate.getSerializationContext().getHashValueSerializationPair();

			Flux<Long> rst = reactiveRedisTemplate.execute(HDECR_SCRIPT, Lists.newArrayList(key, hashKey),
					Arrays.asList(delta), serializationPair.getWriter(), serializationPair.getReader());

			return rst.last().map(TO_LONG);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
     * 库存扣减
	 * @param key   库存key
	 * @param hashKey Hash键
	 * @param delta 扣减数量
	 * @return
     * -4:代表库存传进来的值是负数（非法值）
     * -3:库存未初始化
     * -2:库存不足
     * -1:库存为0
     * 大于等于0:剩余库存（扣减之后剩余的库存）
	 */
	public Mono<Double> luaHdecr(String key, String hashKey, double delta) {
		Assert.hasLength(key, "key must not be empty");
		try {

			SerializationPair<Object> serializationPair = reactiveRedisTemplate.getSerializationContext().getHashValueSerializationPair();

			Flux<Object> rst = reactiveRedisTemplate.execute(HDECR_BYFLOAT_SCRIPT, Lists.newArrayList(key, hashKey),
					Arrays.asList(delta), serializationPair.getWriter(),
					serializationPair.getReader());
			;

			return rst.last().map(TO_DOUBLE);
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 执行lua脚本
	 *
	 * @param luaScript  脚本内容
	 * @param keys       redis键列表
	 * @param values     值列表
	 * @param returnType 返回值类型
	 * @return
	 */
	public <T> Mono<T> executeLuaScript(String luaScript, Class<T> returnType, List<String> keys, Object... values) {
		try {
			RedisScript redisScript = RedisScript.of(luaScript, returnType);
			return reactiveRedisTemplate.execute(redisScript, keys, Arrays.asList(values)).last();
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 执行lua脚本
	 *
	 * @param luaScript 脚本内容
	 * @param keys      redis键列表
	 * @param values    值列表
	 * @return
	 */
	public <T> Mono<T> executeLuaScript(RedisScript<T> luaScript, List<String> keys, Object... values) {
		try {
			return reactiveRedisTemplate.execute(luaScript, keys, Arrays.asList(values)).last();
		} catch (Exception e) {
			return monoError(e);
		}
	}

	/**
	 * 执行lua脚本
	 *
	 * @param luaScript  脚本内容
	 * @param keys       redis键列表
	 * @param values     值列表
	 * @param returnType 返回值类型
	 * @return
	 */
	public <T> Mono<T> executeLuaScript(Resource luaScript, Class<T> returnType, List<String> keys, Object... values) {
		try {
			RedisScript redisScript = RedisScript.of(luaScript, returnType);
			return reactiveRedisTemplate.execute(redisScript, keys, Arrays.asList(values)).last();
		} catch (Exception e) {
			return monoError(e);
		}
	}

	// ===============================RedisCommand=================================

	/**
	 * 获取redis服务器时间 保证集群环境下时间一致
	 * @return
	 */
	public Mono<Long> timeNow() {
		return reactiveRedisTemplate.createMono((ReactiveRedisCallback<Long>) redisConnection -> {
			return redisConnection.serverCommands().time();
		});
	}

	/**
	 * 获取redis服务器时间 保证集群环境下时间一致
	 * @return Redis服务器时间戳
	 */
	public Mono<Long> period(long expiration) {
		return reactiveRedisTemplate.createMono((ReactiveRedisCallback<Long>) redisConnection -> {
			return redisConnection.serverCommands().time().map(time -> expiration - time);
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

	// ===============================batchGet=================================

	public <K> Flux<Entry<Object, Object>> batchGetUserInfo(Collection<K> uids) {
		List<String> uKeys = uids.stream().map(uid -> {
			return RedisKey.USER_INFO.getKey(Objects.toString(uid));
		}).collect(Collectors.toList());
		return this.hmMultiGetAll(uKeys);
	}

	public <K> Mono<Map<String, Object>> batchGetUserFields(K uid, Collection<Object> hashKeys) {
		String userKey = RedisKey.USER_INFO.getKey(Objects.toString(uid));
		return this.hmMultiGet(userKey, hashKeys);
	}

	public <K> Mono<Map<String, Object>> batchGetUserFields(K uid, String... hashKeys) {
		String userKey = RedisKey.USER_INFO.getKey(Objects.toString(uid));
		return this.hmMultiGet(userKey, Stream.of(hashKeys).collect(Collectors.toList()));
	}

	public <K> Flux<Map<String, Object>> batchGetUserFields(Collection<K> uids, String... hashKeys) {
		List<String> uKeys = uids.stream().map(uid -> {
			return RedisKey.USER_INFO.getKey(Objects.toString(uid));
		}).collect(Collectors.toList());
		return this.hmMultiGet(uKeys, Stream.of(hashKeys).collect(Collectors.toList()));
	}

	public <K> Flux<Map<String, Object>> batchGetUserFields(Collection<K> uids, Collection<Object> hashKeys) {
		List<String> uKeys = uids.stream().map(uid -> {
			return RedisKey.USER_INFO.getKey(Objects.toString(uid));
		}).collect(Collectors.toList());
		return this.hmMultiGet(uKeys, hashKeys);
	}

	public <K> Mono<Map<String, Map<String, Object>>> batchGetUserFields(Collection<K> uids, String identityField,
			Collection<Object> hashKeys) {
		List<String> uKeys = uids.stream().map(uid -> {
			return RedisKey.USER_INFO.getKey(Objects.toString(uid));
		}).collect(Collectors.toList());
        return this.hmMultiGet(uKeys, identityField, hashKeys);
    }

}
