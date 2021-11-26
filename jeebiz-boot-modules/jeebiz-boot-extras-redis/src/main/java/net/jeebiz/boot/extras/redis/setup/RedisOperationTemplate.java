package net.jeebiz.boot.extras.redis.setup;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.MapUtils;
import org.springframework.core.io.Resource;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisZSetCommands.Aggregate;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.connection.RedisZSetCommands.Weights;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
import net.jeebiz.boot.api.exception.RedisOperationException;

/**
 * 1、基于RedisTemplate操作的二次封装
 * 2、参考：
 * https://blog.csdn.net/qq_24598601/article/details/105876432
 */
@SuppressWarnings({"unchecked","rawtypes"})
@Slf4j
public class RedisOperationTemplate extends AbstractOperations<String, Object> {

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

	private final RedisTemplate<String, Object> redisTemplate;

	public RedisOperationTemplate(RedisTemplate<String, Object> redisTemplate) {
		super(redisTemplate);
		this.redisTemplate = redisTemplate;
	}

	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	// =============================Serializer============================

	public byte[] getRawKey(Object key) {
		return rawKey(key);
	}

	public byte[] getRawString(String key) {
		return rawString(key);
	}

	public byte[] getRawValue(Object value) {
		return rawValue(value);
	}

	public <V> byte[][] getRawValues(Collection<V> values) {
		return rawValues(values);
	}

	public <HK> byte[] getRawHashKey(HK hashKey) {
		return rawHashKey(hashKey);
	}

	public <HK> byte[][] getRawHashKeys(HK... hashKeys) {
		return rawHashKeys(hashKeys);
	}

	public <HV> byte[] getRawHashValue(HV value) {
		return rawHashValue(value);
	}

	public byte[][] getRawKeys(String key, String otherKey) {
		return rawKeys(key, otherKey);
	}

	public byte[][] getRawKeys(Collection<String> keys) {
		return rawKeys(keys);
	}

	public byte[][] getRawKeys(String key, Collection<String> keys) {
		return rawKeys(key, keys);
	}

	// =============================Deserialize============================

	public Set<Object> getDeserializeValues(Set<byte[]> rawValues) {
		return deserializeValues(rawValues);
	}

	public Set<TypedTuple<Object>> getDeserializeTupleValues(Collection<Tuple> rawValues) {
		return deserializeTupleValues(rawValues);
	}

	public TypedTuple<Object> getDeserializeTuple(Tuple tuple) {
		return deserializeTuple(tuple);
	}

	public Set<Tuple> getRawTupleValues(Set<TypedTuple<Object>> values) {
		return rawTupleValues(values);
	}

	public List<Object> getDeserializeValues(List<byte[]> rawValues) {
		return deserializeValues(rawValues);
	}

	public <T> Set<T> getDeserializeHashKeys(Set<byte[]> rawKeys) {
		return deserializeHashKeys(rawKeys);
	}

	public <T> List<T> getDeserializeHashValues(List<byte[]> rawValues) {
		return deserializeHashValues(rawValues);
	}

	public <HK, HV> Map<HK, HV> getDeserializeHashMap(@Nullable Map<byte[], byte[]> entries) {
		return deserializeHashMap(entries);
	}

	public String getDeserializeKey(byte[] value) {
		return deserializeKey(value);
	}

	public Set<String> getDeserializeKeys(Set<byte[]> keys) {
		return deserializeKeys(keys);
	}

	public Object getDeserializeValue(byte[] value) {
		return deserializeValue(value);
	}

	public String getDeserializeString(byte[] value) {
		return deserializeString(value);
	}

	public <HK> HK getDeserializeHashKey(byte[] value) {
		return deserializeHashKey(value);
	}

	public <HV> HV getDeserializeHashValue(byte[] value) {
		return deserializeHashValue(value);
	}

	public GeoResults<GeoLocation<Object>> getDeserializeGeoResults(GeoResults<GeoLocation<byte[]>> source) {
		return deserializeGeoResults(source);
	}

	// =============================Keys============================

	/**
	 * 指定缓存失效时间
	 *
	 * @param key     键
	 * @param seconds 时间(秒)
	 * @return
	 */
	public Boolean expire(String key, long seconds) {
		if (seconds <= 0) {
			return Boolean.FALSE;
		}
		try {
			return getOperations().expire(key, seconds, TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 指定缓存失效时间
	 *
	 * @param key     键
	 * @param timeout 时间
	 * @return
	 */
	public Boolean expire(String key, Duration timeout) {
		try {
			return getOperations().expire(key, timeout);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Boolean expireAt(String key, Date date) {
		try {
			return getOperations().expireAt(key, date);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 根据key 获取过期时间
	 *
	 * @param key 键 不能为null
	 * @return 时间(秒) 返回0代表为永久有效
	 */
	public Long getExpire(String key) {
		return getOperations().getExpire(key, TimeUnit.SECONDS);
	}

	/**
	 * 判断key是否存在
	 *
	 * @param key 键
	 * @return true 存在 false不存在
	 */
	public Boolean hasKey(String key) {
		try {
			return getOperations().hasKey(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	// 模糊匹配缓存中的key
	public Set<String> getKey(String pattern) {
		try {
			if (Objects.isNull(pattern)) {
				return null;
			}
			return getOperations().keys(pattern);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	// 模糊匹配缓存中的key
	public Set<String> getVagueKey(String pattern) {
		try {
			if (Objects.isNull(pattern)) {
				return null;
			}
			return getOperations().keys("*" + pattern + "*");
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<String> getValueKeyByPrefix(String prefixPattern) {
		try {
			if (Objects.isNull(prefixPattern)) {
				return null;
			}
			return getOperations().keys(prefixPattern + "*");
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public boolean set(String key, Object value) {
		try {
			getOperations().boundValueOps(key).set(value);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 普通缓存放入并设置时间
	 *
	 * @param key     键
	 * @param value   值
	 * @param seconds 时间(秒) time要>=0 如果time小于等于0 将设置无限期
	 * @return true成功 false 失败
	 */
	public boolean set(String key, Object value, long seconds) {
		try {
			if (seconds > 0) {
				getOperations().boundValueOps(key).set(value, seconds, TimeUnit.SECONDS);
				return true;
			} else {
				return set(key, value);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 普通缓存放入并设置时间
	 *
	 * @param key     键
	 * @param value   值
	 * @param timeout 时间
	 * @return true成功 false 失败
	 */
	public boolean set(String key, Object value, Duration timeout) {
		if (Objects.isNull(timeout) || timeout.isNegative()) {
			return false;
		}
		try {
			getOperations().boundValueOps(key).set(value, timeout);
			;
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public boolean setNx(String key, String value) {
		try {
			// return getOperations().opsForValue().setIfAbsent(key, value);
			return this.execute((RedisCallback<Boolean>) redisConnection -> {
				byte[] rawKey = rawString(key);
				byte[] serValue = rawString(value);
				return redisConnection.setNX(rawKey, serValue);
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 1、仅可用于低并发功能，高并发严禁使用此方法
	 *
	 * @param key     并发锁
	 * @param value   锁key（务必能区别不同线程的请求）
	 * @param timeout 锁过期时间（单位：毫秒）
	 * @return
	 */
	public boolean setNx(String key, String value, long timeout) {
		Assert.hasLength(key, "key must not be empty");
		Assert.hasLength(value, "value must not be empty");
		try {
			return getOperations().boundValueOps(key).setIfAbsent(value, Duration.ofMillis(timeout));
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public boolean setNx(String key, String value, Duration timeout) {
		Assert.hasLength(key, "key must not be empty");
		Assert.hasLength(value, "value must not be empty");
		try {
			return getOperations().boundValueOps(key).setIfAbsent(value, timeout);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 2、仅可用于低并发功能，高并发严禁使用此方法
	 *
	 * @param key     并发锁
	 * @param value   锁key（务必能区别不同线程的请求）
	 * @param timeout 锁过期时间
	 * @param unit    锁过期时间单位
	 * @return
	 */
	public boolean setNx(String key, String value, long timeout, TimeUnit unit) {
		Assert.hasLength(key, "key must not be empty");
		Assert.hasLength(value, "value must not be empty");
		try {
			return getOperations().boundValueOps(key).setIfAbsent(value, timeout, unit);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public boolean setEx(String key, String value, long seconds) {
		try {
			// getOperations().opsForValue().set(key, value, Duration.ofMillis(seconds));
			// return true;
			return getOperations().execute((RedisCallback<Boolean>) redisConnection -> {
				return redisConnection.setEx(rawKey(key), seconds, rawValue(value));
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 普通缓存获取
	 *
	 * @param key 键
	 * @return 值
	 */
	public Object get(String key) {
		try {
			return getOperations().boundValueOps(key).get();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Double getDouble(String key) {
		return getFor(key, TO_DOUBLE);
	}

	public Long getLong(String key) {
		return getFor(key, TO_LONG);
	}

	public Integer getInteger(String key) {
		return getFor(key, TO_INTEGER);
	}

	public String getString(String key) {
		return getFor(key, TO_STRING);
	}

	public <T> T getFor(String key, Class<T> clazz) {
		return getFor(key, member -> clazz.cast(member));
	}

	/**
	 * 根据key获取值，并按Function函数进行转换
	 *
	 * @param key    键
	 * @param mapper 对象转换函数
	 * @return xx
	 */
	public <T> T getFor(String key, Function<Object, T> mapper) {
		Object obj = this.get(key);
		if (Objects.nonNull(obj)) {
			return mapper.apply(obj);
		}
		return null;
	}

	/**
	 * 根据key表达式获取缓存
	 *
	 * @param pattern 键表达式
	 * @return 值
	 */
	public List<Object> mGet(String pattern) {
		try {
			if (!StringUtils.hasText(pattern)) {
				return Lists.newArrayList();
			}
			Set<String> keys = getOperations().keys(pattern);
			return getOperations().opsForValue().multiGet(keys);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public List<Double> mGetDouble(Collection keys) {
		return mGetFor(keys, TO_DOUBLE);
	}

	public List<Long> mGetLong(Collection keys) {
		return mGetFor(keys, TO_LONG);
	}

	public List<Integer> mGetInteger(Collection keys) {
		return mGetFor(keys, TO_INTEGER);
	}

	public List<String> mGetString(Collection keys) {
		return mGetFor(keys, TO_STRING);
	}

	public <T> List<T> mGetFor(Collection keys, Class<T> clazz) {
		return mGetFor(keys, member -> clazz.cast(member));
	}

	public <T> List<T> mGetFor(Collection keys, Function<Object, T> mapper) {
		List<Object> members = this.mGet(keys);
		if (Objects.nonNull(members)) {
			return members.stream().map(mapper).collect(Collectors.toList());
		}
		return null;
	}

	/**
	 * 批量获取缓存值
	 *
	 * @param keys 键集合
	 * @return 值
	 */
	public List<Object> mGet(Collection keys) {
		try {
			return CollectionUtils.isEmpty(keys) ? Lists.newArrayList() : getOperations().opsForValue().multiGet(keys);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public List<Object> mGet(Collection<Object> keys, String redisPrefix) {
		try {
			List<Object> result = getOperations().executePipelined((RedisConnection connection) -> {
				keys.stream().forEach(key -> {
					connection.get(rawKey(RedisKeyConstant.getKeyStr(redisPrefix, key.toString())));
				});
				return null;
			}, this.valueSerializer());
			return result;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 递增
	 *
	 * @param key   键
	 * @param delta 要增加几(>=0)
	 * @return
	 */
	public Long incr(String key, long delta) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			return getOperations().opsForValue().increment(key, delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 递增
	 *
	 * @param key     键
	 * @param delta   要增加几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Long incr(String key, long delta, long seconds) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			Long increment = getOperations().opsForValue().increment(key, delta);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long incr(String key, long delta, Duration timeout) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			Long increment = getOperations().opsForValue().increment(key, delta);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 递增
	 *
	 * @param key   键
	 * @param delta 要增加几(>=0)
	 * @return
	 */
	public Double incr(String key, double delta) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			return getOperations().opsForValue().increment(key, delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 递增
	 *
	 * @param key     键
	 * @param delta   要增加几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Double incr(String key, double delta, long seconds) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			Double increment = getOperations().opsForValue().increment(key, delta);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Double incr(String key, double delta, Duration timeout) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			Double increment = getOperations().opsForValue().increment(key, delta);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 递减
	 *
	 * @param key   键
	 * @param delta 要减少几(>=0)
	 * @return
	 */
	public Long decr(String key, long delta) {
		if (delta < 0) {
			throw new RedisOperationException("递减因子必须>=0");
		}
		try {
			return getOperations().opsForValue().increment(key, -delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 递减
	 *
	 * @param key     键
	 * @param delta   要减少几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Long decr(String key, long delta, long seconds) {
		if (delta < 0) {
			throw new RedisOperationException("递减因子必须>=0");
		}
		try {
			Long increment = getOperations().opsForValue().increment(key, -delta);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long decr(String key, long delta, Duration timeout) {
		if (delta < 0) {
			throw new RedisOperationException("递减因子必须>=0");
		}
		try {
			Long increment = getOperations().opsForValue().increment(key, -delta);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 递减
	 *
	 * @param key   键
	 * @param delta 要减少几(>=0)
	 * @return
	 */
	public Double decr(String key, double delta) {
		if (delta < 0) {
			throw new RedisOperationException("递减因子必须>=0");
		}
		try {
			return getOperations().opsForValue().increment(key, -delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 递减
	 *
	 * @param key     键
	 * @param delta   要减少几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Double decr(String key, double delta, long seconds) {
		if (delta < 0) {
			throw new RedisOperationException("递减因子必须>=0");
		}
		try {
			Double increment = getOperations().opsForValue().increment(key, -delta);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Double decr(String key, double delta, Duration timeout) {
		if (delta < 0) {
			throw new RedisOperationException("递减因子必须>=0");
		}
		try {
			Double increment = getOperations().opsForValue().increment(key, -delta);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 删除缓存
	 *
	 * @param keys 可以传一个值 或多个
	 */
	public void del(String... keys) {
		try {
			if (keys != null && keys.length > 0) {
				if (keys.length == 1) {
					getOperations().delete(keys[0]);
				} else {
					getOperations().delete(Stream.of(keys).collect(Collectors.toList()));
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public void delPattern(String pattern) {
		try {
			this.scan(pattern, (value) -> {
				getOperations().delete(deserializeKey(value));
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 获取符合条件的key
	 *
	 * @param pattern 表达式
	 * @return
	 */
	public List<String> keys(String pattern) {
		try {
			List<String> keys = Lists.newArrayList();
			this.scan(pattern, value -> {
				keys.add(deserializeString(value));
			});
			return keys;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * scan 实现
	 *
	 * @param pattern  表达式
	 * @param consumer 对迭代到的key进行操作
	 */
	public void scan(String pattern, Consumer<byte[]> consumer) {
		this.getOperations().execute((RedisConnection redisConnection) -> {
			try (Cursor<byte[]> cursor = redisConnection.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new RedisOperationException(e.getMessage());
			}
		});
	}

	// ===============================List=================================

	/**
	 * 移除N个值为value
	 *
	 * @param key   键
	 * @param count 移除多少个
	 * @param value 值
	 * @return 移除的个数
	 */
	public long lDel(String key, long count, Object value) {
		try {
			Long remove = getOperations().opsForList().remove(key, count, value);
			return remove;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public boolean lDel(String key, int start, int end) {
		try {
			getOperations().opsForList().trim(key, start, end);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public List<Object> lRange(String key, long start, long end) {
		try {
			return getOperations().boundListOps(key).range(start, end);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public List<String> lRangeString(String key, long start, long end) {
		return lRangeFor(key, start, end, TO_STRING);
	}

	public List<Double> lRangeDouble(String key, long start, long end) {
		return lRangeFor(key, start, end, TO_DOUBLE);
	}

	public List<Long> lRangeLong(String key, long start, long end) {
		return lRangeFor(key, start, end, TO_LONG);
	}

	public List<Integer> lRangeInteger(String key, long start, long end) {
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
	public <T> List<T> lRangeFor(String key, long start, long end, Class<T> clazz) {
		return lRangeFor(key, start, end, member -> clazz.cast(member));
	}

	/**
	 * @param key    :
	 * @param start  :
	 * @param end    :0 到-1表示查全部
	 * @param mapper 对象转换函数
	 * @return {@link Set< Long>}
	 */
	public <T> List<T> lRangeFor(String key, long start, long end, Function<Object, T> mapper) {
		List<Object> members = this.lRange(key, start, end);
		if (Objects.nonNull(members)) {
			return members.stream().map(mapper).collect(Collectors.toList());
		}
		return null;
	}

	/**
	 * 通过索引 获取list中的值
	 *
	 * @param key   键
	 * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
	 * @return
	 */
	public Object lIndex(String key, long index) {
		try {
			return getOperations().opsForList().index(key, index);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lLeftPushDistinct(String key, V value) {
		try {
			List<Object> result = getOperations().executePipelined((RedisConnection redisConnection) -> {
				byte[] rawKey = rawKey(key);
				byte[] rawValue = rawValue(value);
				redisConnection.lRem(rawKey, 0, rawValue);
				redisConnection.lPush(rawKey, rawValue);
				return null;
			}, this.valueSerializer());
			return (Long) result.get(1);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lLeftPush(String key, V value) {
		return this.lLeftPush(key, value, 0);
	}

	public <V> Long lLeftPush(String key, V value, long seconds) {
		if (value instanceof Collection) {
			return lLeftPushAll(key, (Collection) value, seconds);
		}
		try {
			Long rt = getOperations().opsForList().leftPush(key, value);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lLeftPush(String key, V value, Duration timeout) {
		if (value instanceof Collection) {
			return lLeftPushAll(key, (Collection) value, timeout);
		}
		try {
			Long rt = getOperations().opsForList().leftPush(key, value);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lLeftPushAll(String key, Collection<V> values) {
		try {
			Long rt = getOperations().opsForList().leftPushAll(key, values.toArray());
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lLeftPushAll(String key, Collection<V> values, long seconds) {
		try {
			Long rt = getOperations().opsForList().leftPushAll(key, values.toArray());
			if (seconds > 0) {
				expire(key, seconds);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lLeftPushAll(String key, Collection<V> values, Duration timeout) {
		try {
			Long rt = getOperations().opsForList().leftPushAll(key, values.toArray());
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lLeftPushx(String key, V value) {
		return this.lLeftPushx(key, value, 0);
	}

	public <V> Long lLeftPushx(String key, V value, long seconds) {
		if (value instanceof Collection) {
			return lLeftPushxAll(key, (Collection) value, seconds);
		}
		try {
			Long rt = getOperations().opsForList().leftPushIfPresent(key, value);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lLeftPushx(String key, V value, Duration timeout) {
		if (value instanceof Collection) {
			return lLeftPushxAll(key, (Collection) value, timeout);
		}
		try {
			Long rt = getOperations().opsForList().leftPushIfPresent(key, value);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lLeftPushxAll(String key, Collection<V> values, long seconds) {
		try {
			long rt = 0L;
			for (V value : values) {
				rt += getOperations().opsForList().leftPushIfPresent(key, value);
			}
			if (seconds > 0) {
				expire(key, seconds);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lLeftPushxAll(String key, Collection<V> values, Duration timeout) {
		try {
			long rt = 0L;
			for (V value : values) {
				rt += getOperations().opsForList().leftPushIfPresent(key, value);
			}
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Object lLeftPop(String key) {
		try {
			return getOperations().opsForList().leftPop(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Object lLeftPopAndLrem(String key) {
		try {
			return getOperations().execute((RedisConnection redisConnection) -> {
				byte[] rawKey = rawKey(key);
				byte[] rawValue = redisConnection.lPop(rawKey);
				redisConnection.lRem(rawKey, 0, rawValue);
				return deserializeValue(rawValue);
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Object lLeftPop(String key, long timeout, TimeUnit unit) {
		try {
			return getOperations().opsForList().leftPop(key, timeout, unit);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Object lLeftPop(String key, Duration timeout) {
		try {
			return getOperations().opsForList().leftPop(key, timeout);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 从list左侧取count个元素并移除已经去除的元素
	 *
	 * @param key
	 * @param count
	 * @return
	 */
	public List<Object> lLeftPop(String key, Integer count) {
		try {
			List<Object> result = getOperations().executePipelined((RedisConnection redisConnection) -> {
				byte[] rawKey = rawKey(key);
				redisConnection.lRange(rawKey, 0, count - 1);
				redisConnection.lTrim(rawKey, count, -1);
				return null;
			}, this.valueSerializer());
			return (List<Object>) result.get(0);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <T> List<T> lLeftPop(String key, Integer count, Class<T> clazz) {
		try {
			List<Object> range = this.lLeftPop(key, count);
			List<T> result = range.stream().map(member -> clazz.cast(member)).collect(Collectors.toList());
			return result;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lRightPushDistinct(String key, V value) {
		try {
			List<Object> result = getOperations().executePipelined((RedisConnection redisConnection) -> {
				byte[] rawKey = rawKey(key);
				byte[] rawValue = rawValue(value);
				redisConnection.lRem(rawKey, 0, rawValue);
				redisConnection.rPush(rawKey, rawValue);
				return null;
			}, this.valueSerializer());
			return (Long) result.get(1);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 将对象放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public <V> Long lRightPush(String key, V value) {
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
	public <V> Long lRightPush(String key, V value, long seconds) {
		if (value instanceof Collection) {
			return lRightPushAll(key, (Collection) value, seconds);
		}
		try {
			Long rt = getOperations().opsForList().rightPush(key, value);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lRightPush(String key, V value, Duration timeout) {
		if (value instanceof Collection) {
			return lRightPushAll(key, (Collection) value, timeout);
		}
		try {
			Long rt = getOperations().opsForList().rightPush(key, value);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lRightPushAll(String key, Collection<V> values) {
		try {
			return getOperations().opsForList().rightPushAll(key, values.toArray());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lRightPushAll(String key, Collection<V> values, long seconds) {
		try {
			Long rt = getOperations().opsForList().rightPushAll(key, values.toArray());
			if (seconds > 0) {
				expire(key, seconds);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lRightPushAll(String key, Collection<V> values, Duration timeout) {
		try {
			Long rt = getOperations().opsForList().rightPushAll(key, values.toArray());
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 将对象放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public <V> Long lRightPushx(String key, V value) {
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
	public <V> Long lRightPushx(String key, V value, long seconds) {
		if (value instanceof Collection) {
			return lRightPushxAll(key, (Collection) value, seconds);
		}
		try {
			Long rt = getOperations().opsForList().rightPushIfPresent(key, value);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lRightPushx(String key, V value, Duration timeout) {
		if (value instanceof Collection) {
			return lRightPushxAll(key, (Collection) value, timeout);
		}
		try {
			Long rt = getOperations().opsForList().rightPushIfPresent(key, value);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lRightPushxAll(String key, Collection<V> values, long seconds) {
		try {
			long rt = 0L;
			for (V value : values) {
				rt += getOperations().opsForList().rightPushIfPresent(key, value);
			}
			if (seconds > 0) {
				expire(key, seconds);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Long lRightPushxAll(String key, Collection<V> values, Duration timeout) {
		try {
			long rt = 0L;
			for (V value : values) {
				rt += getOperations().opsForList().rightPushIfPresent(key, value);
			}
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Object lRightPop(String key) {
		try {
			return getOperations().opsForList().rightPop(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public <V> Object lRightPopAndLrem(String key) {
		try {
			return getOperations().execute((RedisConnection redisConnection) -> {
				byte[] rawKey = rawKey(key);
				byte[] rawValue = redisConnection.rPop(rawKey);
				redisConnection.lRem(rawKey, 0, rawValue);
				return deserializeValue(rawValue);
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Object lRightPop(String key, long timeout, TimeUnit unit) {
		try {
			return getOperations().opsForList().rightPop(key, timeout, unit);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Object lRightPop(String key, Duration timeout) {
		try {
			return getOperations().opsForList().rightPop(key, timeout);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public List<Object> lRightPop(String key, Integer count) {
		try {
			List<Object> result = getOperations().executePipelined((RedisConnection redisConnection) -> {
				byte[] rawKey = rawKey(key);
				redisConnection.lRange(rawKey, -(count - 1), -1);
				redisConnection.lTrim(rawKey, 0, -(count - 1));
				return null;
			}, this.valueSerializer());
			return (List<Object>) result.get(0);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Object lRightPopAndLeftPush(String sourceKey, String destinationKey) {
		try {
			return getOperations().opsForList().rightPopAndLeftPush(sourceKey, destinationKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Object lRightPopAndLeftPush(String sourceKey, String destinationKey, long timeout, TimeUnit unit) {
		try {
			return getOperations().opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Object lRightPopAndLeftPush(String sourceKey, String destinationKey, Duration timeout) {
		try {
			return getOperations().opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public boolean lSet(String key, long index, Object value) {
		try {
			getOperations().opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 获取list缓存的长度
	 *
	 * @param key 键
	 * @return
	 */
	public long lSize(String key) {
		try {
			return getOperations().opsForList().size(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public boolean lTrim(String key, long start, long end) {
		try {
			getOperations().opsForList().trim(key, start, end);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Long hDecr(String key, String hashKey, int delta) {
		if (delta < 0) {
			throw new RedisOperationException("递减因子必须>=0");
		}
		try {
			return getOperations().opsForHash().increment(key, hashKey, -delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Long hDecr(String key, String hashKey, long delta) {
		if (delta < 0) {
			throw new RedisOperationException("递减因子必须>=0");
		}
		try {
			return getOperations().opsForHash().increment(key, hashKey, -delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Double hDecr(String key, String hashKey, double delta) {
		if (delta < 0) {
			throw new RedisOperationException("递减因子必须>=0");
		}
		try {
			return getOperations().opsForHash().increment(key, hashKey, -delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 删除hash表中的值
	 *
	 * @param key      键 不能为null
	 * @param hashKeys 项 可以使多个 不能为null
	 */
	public void hDel(String key, Object... hashKeys) {
		try {
			getOperations().opsForHash().delete(key, hashKeys);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
				this.hDel(bigHashKey, deserializeHashKey(entry.getKey()));
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
	public <V> V hGet(String key, String hashKey) {
		return this.hGet(key, hashKey, null);
	}

	public <V> V hGet(String key, String hashKey, V defaultVal) {
		try {
			HashOperations<String, String, Object> opsForHash = getOperations().opsForHash();
			Object rtVal = opsForHash.get(key, hashKey);
			return Objects.nonNull(rtVal) ? (V) rtVal : defaultVal;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public String hGetString(String key, String hashKey) {
		return hGetFor(key, hashKey, TO_STRING);
	}

	public Double hGetDouble(String key, String hashKey) {
		return hGetFor(key, hashKey, TO_DOUBLE);
	}

	public Double hGetDouble(String key, String hashKey, double defaultVal) {
		Double rtVal = hGetDouble(key, hashKey);
		return Objects.nonNull(rtVal) ? rtVal : defaultVal;
	}

	public Long hGetLong(String key, String hashKey) {
		return hGetFor(key, hashKey, TO_LONG);
	}

	public Long hGetLong(String key, String hashKey, long defaultVal) {
		Long rtVal = hGetLong(key, hashKey);
		return Objects.nonNull(rtVal) ? rtVal : defaultVal;
	}

	public Integer hGetInteger(String key, String hashKey) {
		return hGetFor(key, hashKey, TO_INTEGER);
	}

	public Integer hGetInteger(String key, String hashKey, int defaultVal) {
		Integer rtVal = hGetInteger(key, hashKey);
		return Objects.nonNull(rtVal) ? rtVal : defaultVal;
	}

	public <T> T hGetFor(String key, String hashKey, Class<T> clazz) {
		return hGetFor(key, hashKey, member -> clazz.cast(member));
	}

	public <T> T hGetFor(String key, String hashKey, Function<Object, T> mapper) {
		Object rt = this.hGet(key, hashKey);
		return Objects.nonNull(rt) ? mapper.apply(rt) : null;
	}

	public List<String> hGetString(Collection<Object> keys, String hashKey) {
		return hGetFor(keys, hashKey, TO_STRING);
	}

	public List<Double> hGetDouble(Collection<Object> keys, String hashKey) {
		return hGetFor(keys, hashKey, TO_DOUBLE);
	}

	public List<Long> hGetLong(Collection<Object> keys, String hashKey) {
		return hGetFor(keys, hashKey, TO_LONG);
	}

	public List<Integer> hGetInteger(Collection<Object> keys, String hashKey) {
		return hGetFor(keys, hashKey, TO_INTEGER);
	}

	public <T> List<T> hGetFor(Collection<Object> keys, String hashKey, Class<T> clazz) {
		return hGetFor(keys, hashKey, member -> clazz.cast(member));
	}

	public <T> List<T> hGetFor(Collection<Object> keys, String hashKey, Function<Object, T> mapper) {
		List<Object> members = this.hGet(keys, hashKey);
		if (Objects.nonNull(members)) {
			return members.stream().map(mapper).collect(Collectors.toList());
		}
		return null;
	}

	public List<Object> hGet(Collection<Object> keys, String hashKey) {
		try {
			List<Object> result = getOperations().executePipelined((RedisConnection connection) -> {
				keys.stream().forEach(key -> {
					connection.hGet(rawKey(key), rawHashKey(hashKey));
				});
				return null;
			}, this.valueSerializer());
			return result;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public List<Object> hGet(Collection<Object> keys, String redisPrefix, String hashKey) {
		try {
			List<Object> result = getOperations().executePipelined((RedisConnection connection) -> {
				keys.stream().forEach(key -> {
					byte[] rawKey = rawKey(RedisKeyConstant.getKeyStr(redisPrefix, String.valueOf(key)));
					connection.hGet(rawKey, rawHashKey(hashKey));
				});
				return null;
			}, this.valueSerializer());
			return result;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 判断hash表中是否有该项的值
	 *
	 * @param key     键 不能为null
	 * @param hashKey 项 不能为null
	 * @return true 存在 false不存在
	 */
	public boolean hHasKey(String key, String hashKey) {
		try {
			return getOperations().opsForHash().hasKey(key, hashKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 获取hashKey对应的所有键值
	 *
	 * @param key 键
	 * @return 对应的多个键值
	 */
	public Map<String, Object> hmGet(String key) {
		try {
			HashOperations<String, String, Object> opsForHash = getOperations().opsForHash();
			return opsForHash.entries(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public List<Map<String, Object>> hmGet(Collection<String> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return Lists.newArrayList();
		}
		return keys.parallelStream().map(key -> {
			return this.hmGet(key);
		}).collect(Collectors.toList());
	}

	public List<Map<String, Object>> hmGet(Collection<String> keys, String redisPrefix) {
		if (CollectionUtils.isEmpty(keys)) {
			return Lists.newArrayList();
		}
		return keys.parallelStream().map(key -> {
			return this.hmGet(RedisKeyConstant.getKeyStr(redisPrefix, key));
		}).collect(Collectors.toList());
	}

	public List<Object> hMultiGet(String key, Collection<Object> hashKeys) {
		try {
			return getOperations().opsForHash().multiGet(key, hashKeys);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Map<String, Object> hmMultiGet(String key, Collection<Object> hashKeys) {
		try {
			List<Object> result = getOperations().opsForHash().multiGet(key, hashKeys);
			Map<String, Object> ans = new HashMap<>(hashKeys.size());
			int index = 0;
			for (Object hashKey : hashKeys) {
				ans.put(hashKey.toString(), result.get(index));
				index++;
			}
			return ans;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public List<Map<String, Object>> hmMultiGet(Collection<String> keys, Collection<Object> hashKeys) {
		if (CollectionUtils.isEmpty(keys) || CollectionUtils.isEmpty(hashKeys)) {
			return Lists.newArrayList();
		}
		return keys.parallelStream().map(key -> {
			return this.hmMultiGet(key, hashKeys);
		}).collect(Collectors.toList());
	}

	public Map<String, Map<String, Object>> hmMultiGet(Collection<String> keys, String identityHashKey,
			Collection<Object> hashKeys) {
		if (CollectionUtils.isEmpty(keys) || CollectionUtils.isEmpty(hashKeys)) {
			return Maps.newHashMap();
		}
		return keys.parallelStream().map(key -> {
			return this.hmMultiGet(key, hashKeys);
		}).collect(Collectors.toMap(kv -> MapUtils.getString(kv, identityHashKey), Function.identity()));
	}

	public List<Map<String, Object>> hmMultiGetAll(Collection<Object> keys) {
		try {
			List<Object> result = getOperations().executePipelined((RedisConnection connection) -> {
				keys.stream().forEach(key -> {
					byte[] rawKey = rawKey(String.valueOf(key));
					connection.hGetAll(rawKey);
				});
				return null;
			}, this.valueSerializer());
			return result.stream().map(mapper -> (Map<String, Object>) mapper).collect(Collectors.toList());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public List<Map<String, Object>> hmMultiGetAll(Collection<Object> keys, String redisPrefix) {
		try {
			List<Object> result = getOperations().executePipelined((RedisConnection connection) -> {
				keys.stream().forEach(key -> {
					byte[] rawKey = rawKey(RedisKeyConstant.getKeyStr(redisPrefix, String.valueOf(key)));
					connection.hGetAll(rawKey);
				});
				return null;
			}, this.valueSerializer());
			return result.stream().map(mapper -> (Map<String, Object>) mapper).collect(Collectors.toList());
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public boolean hmMultiSet(String key, Collection<Object> hashKeys, Object value) {
		if (CollectionUtils.isEmpty(hashKeys) || !StringUtils.hasText(key)) {
			return false;
		}
		try {
			getOperations().executePipelined((RedisConnection connection) -> {
				byte[] rawKey = rawKey(key);
				byte[] rawHashValue = rawHashValue(value);
				for (Object hashKey : hashKeys) {
					connection.hSet(rawKey, rawHashKey(hashKey), rawHashValue);
				}
				return null;
			});
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * HashSet
	 *
	 * @param key 键
	 * @param map 对应多个键值
	 * @return true 成功 false 失败
	 */
	public boolean hmSet(String key, Map<String, Object> map) {
		try {
			getOperations().opsForHash().putAll(key, map);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public boolean hmSet(String key, Map<String, Object> map, long seconds) {
		try {
			getOperations().opsForHash().putAll(key, map);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public boolean hmSet(String key, Map<String, Object> map, Duration timeout) {
		try {
			getOperations().opsForHash().putAll(key, map);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public void hScan(String bigHashKey, Consumer<Entry<byte[], byte[]>> consumer) {
		this.getOperations().execute((RedisConnection redisConnection) -> {
			try (Cursor<Entry<byte[], byte[]>> cursor = redisConnection.hScan(rawHashKey(bigHashKey),
					ScanOptions.scanOptions().count(Long.MAX_VALUE).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new RedisOperationException(e.getMessage());
			}
		});
	}

	public void hScan(String bigHashKey, String pattern, Consumer<Entry<byte[], byte[]>> consumer) {
		this.getOperations().execute((RedisConnection redisConnection) -> {
			try (Cursor<Entry<byte[], byte[]>> cursor = redisConnection.hScan(rawHashKey(bigHashKey),
					ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new RedisOperationException(e.getMessage());
			}
		});
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 *
	 * @param key     键
	 * @param hashKey 项
	 * @param value   值
	 * @return true 成功 false失败
	 */
	public boolean hSet(String key, String hashKey, Object value) {
		try {
			getOperations().opsForHash().put(key, hashKey, value);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public boolean hSet(String key, String hashKey, Object value, long seconds) {
		try {
			getOperations().opsForHash().put(key, hashKey, value);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public boolean hSet(String key, String hashKey, Object value, Duration timeout) {
		try {
			getOperations().opsForHash().put(key, hashKey, value);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public boolean hSetNX(String key, String hashKey, Object value) {
		try {
			return getOperations().opsForHash().putIfAbsent(key, hashKey, value);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * hash的大小
	 *
	 * @param key
	 * @return
	 */
	public Long hSize(String key) {
		try {
			return getOperations().opsForHash().size(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Long hIncr(String key, String hashKey, int delta) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			return getOperations().opsForHash().increment(key, hashKey, delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Long hIncr(String key, String hashKey, int delta, long seconds) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			Long increment = getOperations().opsForHash().increment(key, hashKey, delta);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long hIncr(String key, String hashKey, int delta, Duration timeout) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			Long increment = getOperations().opsForHash().increment(key, hashKey, delta);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Long hIncr(String key, String hashKey, long delta) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			return getOperations().opsForHash().increment(key, hashKey, delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Long hIncr(String key, String hashKey, long delta, long seconds) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			Long increment = getOperations().opsForHash().increment(key, hashKey, delta);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long hIncr(String key, String hashKey, long delta, Duration timeout) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			Long increment = getOperations().opsForHash().increment(key, hashKey, delta);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Double hIncr(String key, String hashKey, double delta) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			return getOperations().opsForHash().increment(key, hashKey, delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Double hIncr(String key, String hashKey, double delta, long seconds) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			Double increment = getOperations().opsForHash().increment(key, hashKey, delta);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Double hIncr(String key, String hashKey, double delta, Duration timeout) {
		if (delta < 0) {
			throw new RedisOperationException("递增因子必须>=0");
		}
		try {
			Double increment = getOperations().opsForHash().increment(key, hashKey, delta);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return increment;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<Object> hKeys(String key) {
		try {
			return getOperations().opsForHash().keys(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Long sAdd(String key, Object... values) {
		try {
			return getOperations().opsForSet().add(key, values);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long sAddAndExpire(String key, long seconds, Object... values) {
		try {
			Long rt = getOperations().opsForSet().add(key, values);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long sAddAndExpire(String key, Duration timeout, Object... values) {
		try {
			Long rt = getOperations().opsForSet().add(key, values);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * Set删除: sscan + srem
	 *
	 * @param bigSetKey 键
	 * @return
	 * @return
	 */
	public Boolean sDel(String bigSetKey) {
		try {
			this.sScan(bigSetKey, (hashKey) -> {
				getOperations().opsForHash().delete(bigSetKey, deserializeHashKey(hashKey));
			});
			return getOperations().delete(bigSetKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 根据key获取Set中的所有值
	 *
	 * @param key 键
	 * @return
	 */
	public Set<Object> sGet(String key) {
		try {
			return getOperations().boundSetOps(key).members();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<String> sGetString(String key) {
		return sGetFor(key, TO_STRING);
	}

	public Set<Double> sGetDouble(String key) {
		return sGetFor(key, TO_DOUBLE);
	}

	public Set<Long> sGetLong(String key) {
		return sGetFor(key, TO_LONG);
	}

	public Set<Integer> sGetInteger(String key) {
		return sGetFor(key, TO_INTEGER);
	}

	/**
	 * 根据key获取Set中的所有值
	 *
	 * @param key   键
	 * @param clazz 值的类型
	 * @return 类型处理后的Set
	 */
	public <T> Set<T> sGetFor(String key, Class<T> clazz) {
		return sGetFor(key, member -> clazz.cast(member));
	}

	/**
	 * 根据key获取Set中的所有值，并按Function函数进行转换
	 *
	 * @param key    键
	 * @param mapper 对象转换函数
	 * @return 类型处理后的Set
	 */
	public <T> Set<T> sGetFor(String key, Function<Object, T> mapper) {
		Set<Object> members = this.sGet(key);
		if (Objects.nonNull(members)) {
			return members.stream().map(mapper).collect(Collectors.toCollection(LinkedHashSet::new));
		}
		return null;
	}

	/**
	 * 获取两个key的不同value
	 *
	 * @param key      键
	 * @param otherKey 键
	 * @return 返回key中和otherKey的不同数据
	 */
	public Set<Object> sDiff(String key, String otherKey) {
		try {
			return getOperations().opsForSet().difference(key, otherKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Long sDiffAndStore(String key, String otherKey, String destKey) {
		try {
			return getOperations().opsForSet().differenceAndStore(key, otherKey, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Long sDiffAndStore(String key, Collection<String> keys, String destKey) {
		try {
			return getOperations().opsForSet().differenceAndStore(key, keys, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 获取多个keys的不同数据，存储到destKey中
	 *
	 * @param keys    键集合
	 * @param destKey 键
	 * @return 返回成功数据
	 */
	public Long sDiffAndStore(Collection<String> keys, String destKey) {
		try {
			return getOperations().opsForSet().differenceAndStore(keys, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 根据value从一个set中查询,是否存在
	 *
	 * @param key   键
	 * @param value 值
	 * @return true 存在 false不存在
	 */
	public boolean sHasKey(String key, Object value) {
		try {
			return getOperations().boundSetOps(key).isMember(value);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<Object> sIntersect(String key, String otherKey) {
		try {
			return getOperations().opsForSet().intersect(key, otherKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<Object> sIntersect(String key, Collection<String> otherKeys) {
		try {
			return getOperations().opsForSet().intersect(key, otherKeys);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<Object> sIntersect(Collection<String> otherKeys) {
		try {
			return getOperations().opsForSet().intersect(otherKeys);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long sIntersectAndStore(String key, String otherKey, String destKey) {
		try {
			return getOperations().opsForSet().intersectAndStore(key, otherKey, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long sIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
		try {
			return getOperations().opsForSet().intersectAndStore(key, otherKeys, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long sIntersectAndStore(Collection<String> otherKeys, String destKey) {
		try {
			return getOperations().opsForSet().intersectAndStore(otherKeys, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 随机获取指定数量的元素,同一个元素可能会选中两次
	 *
	 * @param key
	 * @param count
	 * @return
	 */
	public List<Object> sRandomSet(String key, long count) {
		try {
			return getOperations().boundSetOps(key).randomMembers(count);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 随机获取指定数量的元素,去重(同一个元素只能选择一次)
	 *
	 * @param key
	 * @param count
	 * @return
	 */
	public Set<Object> sRandomSetDistinct(String key, long count) {
		try {
			return getOperations().boundSetOps(key).distinctRandomMembers(count);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 移除值为value的
	 *
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 移除的个数
	 */
	public Long sRemove(String key, Object... values) {
		try {
			Long count = getOperations().boundSetOps(key).remove(values);
			return count;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public void sScan(String bigSetKey, Consumer<byte[]> consumer) {
		this.getOperations().execute((RedisConnection redisConnection) -> {
			try (Cursor<byte[]> cursor = redisConnection.sScan(rawKey(bigSetKey),
					ScanOptions.scanOptions().count(Long.MAX_VALUE).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new RedisOperationException(e.getMessage());
			}
		});
	}

	public void sScan(String bigSetKey, String pattern, Consumer<byte[]> consumer) {
		this.getOperations().execute((RedisConnection redisConnection) -> {
			try (Cursor<byte[]> cursor = redisConnection.sScan(rawKey(bigSetKey),
					ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new RedisOperationException(e.getMessage());
			}
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
	public Long sSetAndTime(String key, long seconds, Object... values) {
		try {
			Long count = getOperations().boundSetOps(key).add(values);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return count;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 获取set缓存的长度
	 *
	 * @param key 键
	 * @return
	 */
	public Long sSize(String key) {
		try {
			return getOperations().opsForSet().size(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<Object> sUnion(String key, String otherKey) {
		try {
			return getOperations().opsForSet().union(key, otherKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<Object> sUnion(String key, Collection<String> keys) {
		try {
			return getOperations().opsForSet().union(key, keys);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 合并所有指定keys的数据
	 *
	 * @param keys 键集合
	 * @return 返回成功数据
	 */
	public Set<Object> sUnion(Collection<String> keys) {
		try {
			return getOperations().opsForSet().union(keys);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long sUnionAndStore(String key, String otherKey, String destKey) {
		try {
			return getOperations().opsForSet().unionAndStore(key, otherKey, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long sUnionAndStore(String key, Collection<String> keys, String destKey) {
		try {
			return getOperations().opsForSet().unionAndStore(key, keys, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 合并所有指定keys的数据，存储到destKey中
	 *
	 * @param keys    键集合
	 * @param destKey 键
	 * @return 返回成功数据
	 */
	public Long sUnionAndStore(Collection<String> keys, String destKey) {
		try {
			return getOperations().opsForSet().unionAndStore(keys, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	// ===============================ZSet=================================

	public Boolean zAdd(String key, Object value, double score) {
		try {
			return getOperations().boundZSetOps(key).add(value, score);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long zAdd(String key, Set<TypedTuple<Object>> tuples) {
		try {
			return getOperations().boundZSetOps(key).add(tuples);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long zCard(String key) {
		try {
			return getOperations().boundZSetOps(key).zCard();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Boolean zHas(String key, Object value) {
		try {
			return getOperations().boundZSetOps(key).score(value) != null;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 通过分数返回有序集合指定区间内的成员个数
	 *
	 * @param key
	 * @param min
	 * @param max
	 */
	public Long zCount(String key, double min, double max) {
		try {
			return getOperations().boundZSetOps(key).count(min, max);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * Set删除: sscan + srem
	 *
	 * @param bigZsetKey 键
	 * @return
	 */
	public Boolean zDel(String bigZsetKey) {
		try {
			this.zScan(bigZsetKey, (tuple) -> {
				this.zRem(bigZsetKey, deserializeTuple(tuple).getValue());
			});
			return getOperations().delete(bigZsetKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Double zIncr(String key, Object value, double delta) {
		try {
			return getOperations().boundZSetOps(key).incrementScore(value, delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Double zIncr(String key, Object value, double delta, long seconds) {
		try {
			Double result = getOperations().boundZSetOps(key).incrementScore(value, delta);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return result;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Double zIncr(String key, Object value, double delta, Duration timeout) {
		try {
			Double result = getOperations().boundZSetOps(key).incrementScore(value, delta);
			if (!timeout.isNegative()) {
				expire(key, timeout);
			}
			return result;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	// zset指定元素增加值，并监听指定区域的顺序变化，如果指定区域元素发送变化，则返回true
	public Boolean zIncrAndWatch(String key, Object value, double delta, long start, long end) {
		try {
			byte[] rawKey = rawKey(key);
			byte[] rawValue = rawValue(value);
			return this.execute(connection -> {
				// 1、增加score之前查询指定区域的元素对象
				Set<TypedTuple<Object>> zset1 = deserializeTupleValues(connection.zRevRangeWithScores(rawKey, start, end));
				// 2、增加score
				connection.zIncrBy(rawKey, delta, rawValue);
				// 3、增加score之后查询指定区域的元素对象
				Set<TypedTuple<Object>> zset2 = deserializeTupleValues(connection.zRevRangeWithScores(rawKey, start, end));
				// 4、如果同一key两次取值有一个为空，表示元素发生了新增或移除，那两个元素一定有变化了
				if(CollectionUtils.isEmpty(zset1) && !CollectionUtils.isEmpty(zset2) || !CollectionUtils.isEmpty(zset1) && CollectionUtils.isEmpty(zset2)) {
					return Boolean.TRUE;
				}
				// 5、如果两个元素都不为空，但是长度不相同，表示元素一定有变化了
				if(zset1.size() != zset2.size()) {
					return Boolean.TRUE;
				}
				// 6、 两个set都不为空，且长度相同，则对key进行提取，并比较keyList与keyList2,一旦遇到相同位置处的值不一样，表示顺序发生了变化
				List<String> keyList1 = Objects.isNull(zset1) ? Lists.newArrayList() : zset1.stream().map(item -> item.getValue().toString()).collect(Collectors.toList());
				List<String> keyList2 = Objects.isNull(zset2) ? Lists.newArrayList() : zset2.stream().map(item -> item.getValue().toString()).collect(Collectors.toList());
				for (int i = 0; i < keyList1.size(); i++) {
					if(!Objects.equals(keyList1.get(i), keyList2.get(i))) {
						return Boolean.TRUE;
					}
				}
				return Boolean.FALSE;
			}, true);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long zIntersectAndStore(String key, String otherKey, String destKey) {
		try {
			return getOperations().opsForZSet().intersectAndStore(key, otherKey, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey) {
		try {
			return getOperations().opsForZSet().intersectAndStore(key, otherKeys, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey, Aggregate aggregate) {
		try {
			return getOperations().opsForZSet().intersectAndStore(key, otherKeys, destKey, aggregate);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long zIntersectAndStore(String key, Collection<String> otherKeys, String destKey, Aggregate aggregate,
			Weights weights) {
		try {
			return getOperations().opsForZSet().intersectAndStore(key, otherKeys, destKey, aggregate, weights);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 移除zset中的元素
	 *
	 * @param key
	 * @param values
	 */
	public Long zRem(String key, Object... values) {
		try {
			return getOperations().boundZSetOps(key).remove(values);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 移除分数区间内的元素
	 *
	 * @param key
	 * @param min
	 * @param max
	 */
	public Long zRemByScore(String key, double min, double max) {
		try {
			return getOperations().boundZSetOps(key).removeRangeByScore(min, max);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<Object> zRange(String key, long start, long end) {
		try {
			return getOperations().boundZSetOps(key).range(start, end);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<String> zRangeString(String key, long start, long end) {
		return zRangeFor(key, start, end, TO_STRING);
	}

	public Set<Double> zRangeDouble(String key, long  start, long end) {
		return zRangeFor(key, start, end, TO_DOUBLE);
	}

	public Set<Long> zRangeLong(String key, long  start, long end) {
		return zRangeFor(key, start, end, TO_LONG);
	}

	public Set<Integer> zRangeInteger(String key, long  start, long end) {
		return zRangeFor(key, start, end, TO_INTEGER);
	}

	public <T> Set<T> zRangeFor(String key, long start, long end, Class<T> clazz) {
		return zRangeFor(key, start, end, member -> clazz.cast(member));
	}

	/**
	 * @param key   :
	 * @param start :
	 * @param end   :0 到-1表示查全部
	 * @param mapper 对象转换函数
	 * @return {@link Set<T>}
	 */
	public <T> Set<T> zRangeFor(String key, long  start, long end, Function<Object, T> mapper) {
		Set<Object> members = this.zRange(key, start, end);
		if(Objects.nonNull(members)) {
			return members.stream().map(mapper)
					.collect(Collectors.toCollection(LinkedHashSet::new));
		}
		return null;
	}

	public Set<Object> zRangeByScore(String key, double min, double max) {
		try {
			return getOperations().boundZSetOps(key).rangeByScore(min, max);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<String> zRangeStringByScore(String key, long  min, long max) {
		return zRangeByScoreFor(key, min, max, TO_STRING);
	}

	public Set<Double> zRangeDoubleByScore(String key, long  min, long max) {
		return zRangeByScoreFor(key, min, max, TO_DOUBLE);
	}

	public Set<Long> zRangeLongByScore(String key, long min, long max) {
		return zRangeByScoreFor(key, min, max, TO_LONG);
	}

	public Set<Integer> zRangeIntegerByScore(String key, long min, long max) {
		return zRangeByScoreFor(key, min, max, TO_INTEGER);
	}

	public <T> Set<T> zRangeByScoreFor(String key, long min, long max, Class<T> clazz) {
		return zRangeByScoreFor(key, min, max, member -> clazz.cast(member));
	}

	public <T> Set<T> zRangeByScoreFor(String key, long min, long max, Function<Object, T> mapper) {
		Set<Object> members = this.zRangeByScore(key, min, max);
		if(Objects.nonNull(members)) {
			return members.stream().map(mapper)
					.collect(Collectors.toCollection(LinkedHashSet::new));
		}
		return null;
	}

	public Set<TypedTuple<Object>> zRangeWithScores(String key, long start, long end) {
		try {
			return getOperations().boundZSetOps(key).rangeWithScores(start, end);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 在min到max范围内倒序获取zset及对应的score
	 */
	public Set<TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max) {
		try {
			return getOperations().boundZSetOps(key).rangeByScoreWithScores(min, max);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<Object> zRangeByLex(String key, Range range) {
		try {
			return getOperations().boundZSetOps(key).rangeByLex(range);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<Object> zRangeByLex(String key, Range range, Limit limit) {
		try {
			return getOperations().boundZSetOps(key).rangeByLex(range, limit);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * @param key   :
	 * @param start :
	 * @param end   :0 到-1表示查全部
	 * @return {@link Set< Object>}
	 */
	public Set<Object> zRevrange(String key, long start, long end) {
		try {
			return getOperations().boundZSetOps(key).reverseRange(start, end);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Set<String> zRevrangeString(String key, long  start, long end) {
		return zRevrangeFor(key, start, end, TO_STRING);
	}

	public Set<Double> zRevrangeDouble(String key, long  start, long end) {
		return zRevrangeFor(key, start, end, TO_DOUBLE);
	}

	/**
	 * @param key   :
	 * @param start :
	 * @param end   :0 到-1表示查全部
	 * @return {@link Set< Long>}
	 */
	public Set<Long> zRevrangeLong(String key, long  start, long end) {
		return zRevrangeFor(key, start, end, TO_LONG);
	}

	public Set<Integer> zRevrangeInteger(String key, long  start, long end) {
		return zRevrangeFor(key, start, end, TO_INTEGER);
	}

	/**
	 * 获取list缓存的内容
	 *
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @return
	 */
	public <T> Set<T> zRevrangeFor(String key, long start, long end, Class<T> clazz) {
		return zRevrangeFor(key, start, end, member -> clazz.cast(member));
	}

	public <T> Set<T> zRevrangeFor(String key, long  start, long end, Function<Object, T> mapper) {
		Set<Object> members = this.zRevrange(key, start, end);
		if(Objects.nonNull(members)) {
			return members.stream().map(mapper).collect(Collectors.toCollection(LinkedHashSet::new));
		}
		return null;
	}

	/**
	 * 获取指定key的scores正序，指定start-end位置的元素
	 *
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public Set<TypedTuple<Object>> zRevrangeWithScores(String key, long start, long end) {
		try {
			return getOperations().boundZSetOps(key).reverseRangeWithScores(start, end);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 获取指定key的scores正序，指定start-end位置的元素
	 *
	 * @param key
	 * @param min
	 * @param max
	 * @return
	 */
	public Set<TypedTuple<Object>> zRevrangeByScoreWithScores(String key, double min, double max) {
		try {
			return getOperations().boundZSetOps(key).reverseRangeByScoreWithScores(min, max);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long zRevRank(String key, Object value) {
		try {
			return getOperations().boundZSetOps(key).reverseRank(value);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public void zScan(String bigZsetKey, Consumer<Tuple> consumer) {
		this.getOperations().execute((RedisConnection redisConnection) -> {
			try (Cursor<Tuple> cursor = redisConnection.zScan(rawKey(bigZsetKey), ScanOptions.scanOptions().count(Long.MAX_VALUE).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new RedisOperationException(e.getMessage());
			}
		});
	}

	public void zScan(String bigZsetKey, String pattern, Consumer<Tuple> consumer) {
		this.getOperations().execute((RedisConnection redisConnection) -> {
			try (Cursor<Tuple> cursor = redisConnection.zScan(rawKey(bigZsetKey), ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new RedisOperationException(e.getMessage());
			}
		});
	}

	public Double zScore(String key, Object value) {
		try {
			return getOperations().boundZSetOps(key).score(value);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long zUnionAndStore(String key, String otherKey, String destKey) {
		try {
			return getOperations().opsForZSet().unionAndStore(key, otherKey, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long zUnionAndStore(String key, Collection<String> keys, String destKey) {
		try {
			return getOperations().opsForZSet().unionAndStore(key, keys, destKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long zUnionAndStore(String key, Collection<String> keys, String destKey, Aggregate aggregate) {
		try {
			return getOperations().opsForZSet().unionAndStore(key, keys, destKey, aggregate);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long zUnionAndStore(String key, Collection<String> keys, String destKey, Aggregate aggregate, Weights weights) {
		try {
			return getOperations().opsForZSet().unionAndStore(key, keys, destKey, aggregate, weights);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	// ===============================HyperLogLog=================================

	public Long pfAdd(String key, Object... values) {
		try {
			return getOperations().opsForHyperLogLog().add(key, values);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Boolean pfDel(String key) {
		try {
			getOperations().opsForHyperLogLog().delete(key);
			return Boolean.TRUE;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long pfCount(String... keys) {
		try {
			return getOperations().opsForHyperLogLog().size(keys);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long pfMerge(String destination, String... sourceKeys) {
		try {
			return getOperations().opsForHyperLogLog().union(destination, sourceKeys);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	// ===============================BitMap=================================

	public Boolean setBit(String key, long offset, boolean value) {
		try {
			return getOperations().opsForValue().setBit(key, offset, value);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Boolean getBit(String key, long offset) {
		try {
			return getOperations().opsForValue().getBit(key, offset);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	// ===============================Message=================================

	/**
	 * 发送消息
	 *
	 * @param channel
	 * @param message
	 */
	public void sendMessage(String channel, String message) {
		try {
			getOperations().convertAndSend(channel, message);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public boolean tryBlockLock(String lockKey, int seconds) {
        try {
			return redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
			    // 1、获取时间毫秒值
			    long expireAt = redisConnection.time() + seconds * 1000 + 1;
			    // 2、第一次请求, 锁标识不存在的情况，直接拿到锁
			    Boolean acquire = redisConnection.setNX(rawKey(lockKey), String.valueOf(expireAt).getBytes());
			    if (acquire) {
			        return true;
			    } else {
			    	// 3、非第一次请求，阻塞等待拿到锁
			    	redisConnection.bRPop(seconds, rawKey(lockKey + ":list"));
			    }
			    return false;
			});
        } catch (Exception e) {
			log.error("acquire redis occurred an exception", e);
		}
       	return false;
    }

	/**
	 * 2、删除指定key来进行完成解锁逻辑
	 * @param lockKey  锁key
	 * @param requestId  锁值
	 * @return 释放锁成功/失败
	 */
    public boolean unBlockLock(String lockKey, String requestId) {
    	try {
    		return redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
    			redisConnection.del(rawKey(lockKey));
    			byte[] rawKey = rawKey(lockKey + ":list");
    			byte[] rawValue = rawValue(requestId);
    			redisConnection.rPush(rawKey, rawValue);
    		    return true;
    		}, true);
        } catch (Exception e) {
			log.error("acquire redis occurred an exception", e);
			throw new RedisOperationException(e.getMessage());
		}
	}

	public boolean tryLock(String lockKey, Duration timeout) {
		return tryLock( lockKey, timeout.toMillis());
	}

	/**
	 * 1、对指定key来进行加锁逻辑（此锁是全局性的）
	 * @param lockKey  锁key
	 * @return
	 */
	public boolean tryLock(String lockKey, long expireMillis) {
        try {
			return redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
				byte[] serLockKey = rawString(lockKey);
			    // 1、获取时间毫秒值
			    long expireAt = redisConnection.time() + expireMillis + 1;
			    // 2、获取锁
			    Boolean acquire = redisConnection.setNX(serLockKey, String.valueOf(expireAt).getBytes());
			    if (acquire) {
			        return true;
			    } else {
			        byte[] bytes = redisConnection.get(serLockKey);
			        // 3、非空判断
			        if (Objects.nonNull(bytes) && bytes.length > 0) {
			            long expireTime = Long.parseLong(new String(bytes));
			            // 4、如果锁已经过期
			            if (expireTime < redisConnection.time()) {
			                // 5、重新加锁，防止死锁
			                byte[] set = redisConnection.getSet(serLockKey, String.valueOf(redisConnection.time() + expireMillis + 1).getBytes());
			                return Long.parseLong(new String(set)) < redisConnection.time();
			            }
			        }
			    }
			    return false;
			});
        } catch (Exception e) {
			log.error("acquire redis occurred an exception", e);
		}
       	return false;
    }

	/**
	 * 2、删除指定key来进行完成解锁逻辑
	 * @param lockKey  锁key
	 * @return
	 */
    public boolean unlock(String lockKey) {
    	try {
	        return getOperations().delete(lockKey);
        } catch (Exception e) {
			log.error("acquire redis occurred an exception", e);
			throw new RedisOperationException(e.getMessage());
		}
	}

    public boolean tryLock(String lockKey, String requestId, Duration timeout, int retryTimes, long retryInterval) {
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
	public boolean tryLock(String lockKey, String requestId, long expire, int retryTimes, long retryInterval) {
       try {
			return redisTemplate.execute((RedisCallback<Boolean>) redisConnection -> {
				// 1、执行lua脚本
				Long result =  this.executeLuaScript(LOCK_LUA_SCRIPT, Collections.singletonList(lockKey), requestId, expire);
				if(LOCK_SUCCESS.equals(result)) {
				    log.info("locked... redisK = {}", lockKey);
				    return true;
				} else {
					// 2、重试获取锁
			        int count = 0;
			        while(count < retryTimes) {
			            try {
			                Thread.sleep(retryInterval);
			                result = this.executeLuaScript(LOCK_LUA_SCRIPT, Collections.singletonList(lockKey), requestId, expire);
			                if(LOCK_SUCCESS.equals(result)) {
			                	log.info("locked... redisK = {}", lockKey);
			                    return true;
			                }
			                log.warn("{} times try to acquire lock", count + 1);
			                count++;
			            } catch (Exception e) {
			            	log.error("acquire redis occurred an exception", e);
			            }
			        }
			        log.info("fail to acquire lock {}", lockKey);
			        return false;
				}
			});
		} catch (Exception e) {
			log.error("acquire redis occurred an exception", e);
		}
       	return false;
	}

	/**
	 * 2、lua脚本释放KEY
	 * @param lockKey 释放本请求对应的锁的key
	 * @param requestId   释放本请求对应的锁的value
	 * @return 释放锁 true 成功
	 */
    public boolean unlock(String lockKey, String requestId) {
        log.info("unlock... redisK = {}", lockKey);
        try {
            // 使用lua脚本删除redis中匹配value的key
            Long result = this.executeLuaScript(UNLOCK_LUA_SCRIPT, Collections.singletonList(lockKey), requestId);
            //如果这里抛异常，后续锁无法释放
            if (LOCK_SUCCESS.equals(result)) {
            	log.info("release lock success. redisK = {}", lockKey);
                return true;
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
        return false;
    }

	// ===============================Pipeline=================================

	public List<Object> executePipelined(RedisCallback<?> action) {
		try {
			return getOperations().executePipelined(action);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public List<Object> executePipelined(RedisCallback<?> action, RedisSerializer<?> resultSerializer) {
		try {
			return getOperations().executePipelined(action, resultSerializer);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
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
	public Long luaIncr(String key, long delta) {
		Assert.hasLength(key, "key must not be empty");
		return this.executeLuaScript(INCR_SCRIPT, Lists.newArrayList(key), delta);
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
	public Double luaIncr(String key, double delta) {
		Assert.hasLength(key, "key must not be empty");
		Object rst = this.executeLuaScript(INCR_BYFLOAT_SCRIPT, Lists.newArrayList(key), delta);
		return new BigDecimal(rst.toString()).doubleValue();
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
	public Long luaDecr(String key, long delta) {
		Assert.hasLength(key, "key must not be empty");
		return this.executeLuaScript(DECR_SCRIPT, Lists.newArrayList(key), delta);
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
	public Double luaDecr(String key, double delta) {
		Assert.hasLength(key, "key must not be empty");
		Object rst = this.executeLuaScript(DECR_BYFLOAT_SCRIPT, Lists.newArrayList(key), delta);
		return new BigDecimal(rst.toString()).doubleValue();
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
	public Long luaHincr(String key, String hashKey, long delta) {
		Assert.hasLength(key, "key must not be empty");
		try {
			return getOperations().execute(HINCR_SCRIPT, this.hashValueSerializer(), this.hashValueSerializer(),
					Lists.newArrayList(key, hashKey), delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Double luaHincr(String key, String hashKey, double delta) {
		Assert.hasLength(key, "key must not be empty");
		try {
			Object rst = getOperations().execute(HINCR_BYFLOAT_SCRIPT, this.hashValueSerializer(),
					this.hashValueSerializer(), Lists.newArrayList(key, hashKey), delta);
			return new BigDecimal(rst.toString()).doubleValue();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Long luaHdecr(String key, String hashKey, long delta) {
		Assert.hasLength(key, "key must not be empty");
		try {
			return getOperations().execute(HDECR_SCRIPT, this.hashValueSerializer(), this.hashValueSerializer(),
					Lists.newArrayList(key, hashKey), delta);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public Double luaHdecr(String key, String hashKey, double delta) {
		Assert.hasLength(key, "key must not be empty");
		try {
			Object rst = getOperations().execute(HDECR_BYFLOAT_SCRIPT, this.hashValueSerializer(),
					this.hashValueSerializer(), Lists.newArrayList(key, hashKey), delta);
			;
			return new BigDecimal(rst.toString()).doubleValue();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public <R> R executeLuaScript(String luaScript, Class<R> returnType, List<String> keys, Object... values) {
		try {
			RedisScript redisScript = RedisScript.of(luaScript, returnType);
			return (R) getOperations().execute(redisScript, keys, values);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public <R> R executeLuaScript(RedisScript<R> luaScript, List<String> keys, Object... values) {
		try {
			return (R) getOperations().execute(luaScript, keys, values);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
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
	public <R> R executeLuaScript(Resource luaScript, Class<R> returnType, List<String> keys, Object... values) {
		try {
			RedisScript redisScript = RedisScript.of(luaScript, returnType);
			return (R) getOperations().execute(redisScript, keys, values);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	// ===============================RedisCommand=================================

	/**
	 * 获取redis服务器时间 保证集群环境下时间一致
	 * @return Redis服务器时间戳
	 */
	public Long timeNow() {
		try {
			return getOperations().execute((RedisCallback<Long>) redisConnection -> {
				return redisConnection.time();
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	/**
	 * 获取redis服务器时间 保证集群环境下时间一致
	 * @return Redis服务器时间戳
	 */
	public Long period(long expiration) {
		try {
			return getOperations().execute((RedisCallback<Long>) redisConnection -> {
				return expiration - redisConnection.time();
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long dbSize() {
		try {
			return getOperations().execute((RedisCallback<Long>) redisConnection -> {
				return redisConnection.dbSize();
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public Long lastSave() {
		try {
			return getOperations().execute((RedisCallback<Long>) redisConnection -> {
				return redisConnection.lastSave();
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public void bgReWriteAof() {
		try {
			getOperations().execute((RedisCallback<Void>) redisConnection -> {
				redisConnection.bgReWriteAof();
				return null;
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public void bgSave() {
		try {
			getOperations().execute((RedisCallback<Void>) redisConnection -> {
				redisConnection.bgSave();
				return null;
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public void save() {
		try {
			getOperations().execute((RedisCallback<Void>) redisConnection -> {
				redisConnection.save();
				return null;
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public void flushDb() {
		try {
			getOperations().execute((RedisCallback<Void>) redisConnection -> {
				redisConnection.flushDb();
				return null;
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	public void flushAll() {
		try {
			getOperations().execute((RedisCallback<Void>) redisConnection -> {
				redisConnection.flushAll();
				return null;
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RedisOperationException(e.getMessage());
		}
	}

	// ===============================batchGet=================================

   	public <K> List<Map<String, Object>> batchGetUserInfo(Collection<K> uids) {
   		Collection<Object> uKeys = uids.stream().map(uid -> {
			return RedisKey.USER_INFO.getKey(Objects.toString(uid));
		}).collect(Collectors.toList());
		return this.hmMultiGetAll(uKeys);
	}

	public <K> Map<String, Object> batchGetUserFields(K uid, Collection<Object> hashKeys) {
		String userKey = RedisKey.USER_INFO.getKey(Objects.toString(uid));
		return this.hmMultiGet(userKey, hashKeys);
	}

	public <K> Map<String, Object> batchGetUserFields(K uid, String... hashKeys) {
		String userKey = RedisKey.USER_INFO.getKey(Objects.toString(uid));
		return this.hmMultiGet(userKey, Stream.of(hashKeys).collect(Collectors.toList()));
	}

	public <K> List<Map<String, Object>> batchGetUserFields(Collection<K> uids, String... hashKeys) {
		List<String> uKeys = uids.stream().map(uid -> {
			return RedisKey.USER_INFO.getKey(Objects.toString(uid));
		}).collect(Collectors.toList());
		return this.hmMultiGet(uKeys, Stream.of(hashKeys).collect(Collectors.toList()));
	}

	public <K> List<Map<String, Object>> batchGetUserFields(Collection<K> uids, Collection<Object> hashKeys) {
		List<String> uKeys = uids.stream().map(uid -> {
			return RedisKey.USER_INFO.getKey(Objects.toString(uid));
		}).collect(Collectors.toList());
		return this.hmMultiGet(uKeys, hashKeys);
	}

	public <K> Map<String, Map<String, Object>> batchGetUserFields(Collection<K> uids, String identityField,
			Collection<Object> hashKeys) {
		List<String> uKeys = uids.stream().map(uid -> {
			return RedisKey.USER_INFO.getKey(Objects.toString(uid));
		}).collect(Collectors.toList());
        return this.hmMultiGet(uKeys, identityField, hashKeys);
    }

}
