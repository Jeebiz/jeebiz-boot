package net.jeebiz.boot.extras.redis.setup;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands.Tuple;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;
import net.jeebiz.boot.api.exception.BizRuntimeException;

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
    
    private static final RedisScript<Long> LOCK_LUA_SCRIPT = RedisScript.of(
        "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then return redis.call('pexpire', KEYS[1], ARGV[2]) else return -1 end",
         Long.class
    );
    
    private static final RedisScript<Long> UNLOCK_LUA_SCRIPT = RedisScript.of(
		"if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return -1 end",
        Long.class
    );
   
    public static final RedisScript<Long> INCR_SCRIPT = RedisScript.of(
    										" 	if redis.call('exists', KEYS[1]) == 1 then \n "
											+ "		local current = redis.call('incr', KEYS[1], ARGV[1]) \n"
											+ "		if current < 0 then \n"
											+ "			redis.call('decr', KEYS[1], ARGV[1]) \n"
											+ "			return 0 \n"
											+ "		else \n"
											+ "			return current \n"
											+ "		end \n"
											+ " else "
											+ "		redis.call('set', KEYS[1], 0) "
											+ "		return 0 "
											+ "	end",
									        Long.class);
	
	private static final RedisScript<Long> HINCR_SCRIPT = RedisScript.of(" if redis.call('hget', KEYS[1]) == 1 then  "
			+ "		local current = redis.call('incr', KEYS[1]); "
			+ "		if current < 0 then "
			+ "			redis.call('decr', KEYS[1]) "
			+ "			return 0 "
			+ "		else "
			+ "			return current "
			+ "		end "
			+ " else return 0 end",
	        Long.class);
	
	private final RedisTemplate<String, Object> redisTemplate;
	
	public RedisOperationTemplate(RedisTemplate<String, Object> redisTemplate) {
		super(redisTemplate);
		this.redisTemplate = redisTemplate;
	}

	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	// =============================Keys============================

	/**
	 * 指定缓存失效时间
	 * @param key  键
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
			return Boolean.FALSE;
		}
	}

	/**
	 * 指定缓存失效时间
	 * @param key  键
	 * @param timeout 时间
	 * @return
	 */
	public Boolean expire(String key, Duration timeout) {
		try {
			return getOperations().expire(key, timeout);
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}
	
	public Boolean expireAt(String key, Date date) {
		try {
			return getOperations().expireAt(key, date);
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}
	
	/**
	 * 根据key 获取过期时间
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
			return false;
		}
	}

	// 模糊匹配缓存中的key
	public Set<String> getKey(String pattern) {
		if (Objects.isNull(pattern)) {
			return new HashSet<>();
		}
		return getOperations().keys(pattern);
	}

	// 模糊匹配缓存中的key
	public Set<String> getVagueKey(String pattern) {
		return getOperations().keys("*" + pattern + "*");
	}

	public Set<String> getValueKeyByPrefix(String prefixPattern) {
		return getOperations().keys(prefixPattern + "*");
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
			getOperations().opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
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
	public boolean set(String key, Object value, long seconds) {
		try {
			if (seconds > 0) {
				getOperations().opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
				return true;
			} else {
				return set(key, value);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
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
	public boolean set(String key, Object value, Duration duration) {
		if (Objects.isNull(duration) || duration.isNegative()) {
			return false;
		}
		try {
			getOperations().opsForValue().set(key, value, duration);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}
	
	public boolean setNx(String key, String value) {
		try {
			//return getOperations().opsForValue().setIfAbsent(key, value);
			return this.execute((RedisCallback<Boolean>) redisConnection -> {
				byte[] serKey = rawString(key);
				byte[] serValue = rawString(value);
				return redisConnection.setNX(serKey, serValue);
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	public boolean setEx(String key, String value, long seconds) {
		try {
			//getOperations().opsForValue().set(key, value, Duration.ofMillis(seconds));
			//return true;
			return getOperations().execute((RedisCallback<Boolean>) redisConnection -> {
				return redisConnection.setEx(rawKey(key), seconds, rawValue(value));
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
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
			return !StringUtils.hasText(key) ? null : getOperations().opsForValue().get(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	public String getString(String key) {
		try {
			return !StringUtils.hasText(key) ? null : getOperations().execute((RedisCallback<String>) redisConnection -> {
				byte[] serValue = redisConnection.get(rawKey(key));
				return deserializeString(serValue);
			});
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 根据key表达式获取缓存
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
			return Lists.newArrayList();
		}
	}
	
	/**
	 * 批量获取缓存值
	 * @param keys 键集合
	 * @return 值
	 */
	public List<Object> mGet(Collection keys) {
		try {
			return CollectionUtils.isEmpty(keys) ? Lists.newArrayList() : getOperations().opsForValue().multiGet(keys);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Lists.newArrayList();
		}
	}
	
	public List<Object> mGet(Collection<Object> keys, String redisPrefix) {
		List<Object> result = getOperations().executePipelined((RedisConnection connection) -> {
			keys.stream().forEach(key -> {
				connection.get(rawKey(RedisKeyConstant.getKeyStr(redisPrefix, key.toString())));
			});
			return null;
		}, this.valueSerializer());
		return result;
	}
	
	/**
	 * 递增
	 * @param key   键
	 * @param delta 要增加几(>=0)
	 * @return
	 */
	public Long incr(String key, long delta) {
		return getOperations().opsForValue().increment(key, delta);
	}

	/**
	 * 递增
	 *
	 * @param key   键
	 * @param delta 要增加几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Long incr(String key, long delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Long increment = getOperations().opsForValue().increment(key, delta);
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
	public Double incr(String key, double delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		return getOperations().opsForValue().increment(key, delta);
	}
	
	/**
	 * 递增
	 *
	 * @param key   键
	 * @param delta 要增加几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Double incr(String key, double delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Double increment = getOperations().opsForValue().increment(key, delta);
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
	public Long decr(String key, long delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		return getOperations().opsForValue().increment(key, -delta);
	}

	/**
	 * 递减
	 *
	 * @param key   键
	 * @param delta 要减少几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Long decr(String key, long delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		Long increment = getOperations().opsForValue().increment(key, -delta);
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
	public Double decr(String key, double delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		return getOperations().opsForValue().increment(key, -delta);
	}
	
	/**
	 * 递减
	 *
	 * @param key   键
	 * @param delta 要减少几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Double decr(String key, double delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		Double increment = getOperations().opsForValue().increment(key, -delta);
		if (seconds > 0) {
			expire(key, seconds);
		}
		return increment;
	}
	
	/**
	 * 删除缓存
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
		}
	}
	
	public void delPattern(String pattern) {
		try {
			this.scan(pattern, (value) -> {
				getOperations().delete(deserializeKey(value));
			});
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 获取符合条件的key
	 * 
	 * @param pattern 表达式
	 * @return
	 */
	public List<String> keys(String pattern) {
		List<String> keys = Lists.newArrayList();
		this.scan(pattern, value -> {
			keys.add(deserializeString(value));
		});
		return keys;
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
				throw new BizRuntimeException(e.getMessage());
			}
		});
	}

	
	// ===============================List=================================


	/**
	 *    移除N个值为value
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
			return 0;
		}
	}

	/**
	 * List删除: ltrim
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
			return false;
		}
	}
	

	public List<String> lGet(String key, Integer count) {
		List<Object> result = getOperations().executePipelined((RedisConnection redisConnection) -> {
			byte[] serKey = rawString(key);
			redisConnection.lRange(serKey, 0, count - 1);
			redisConnection.lTrim(serKey, count, -1);
			return null;
		}, this.valueSerializer());
		
		return (List<String>) result.get(0);
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
			return getOperations().opsForList().range(key, start, end);
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
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
	public <T> List<T> lRange(String key, long start, long end, Class<T> clazz) {
		try {
			List<Object> range = getOperations().opsForList().range(key, start, end);
			List<T> result = range.stream().map(member -> clazz.cast(member)).collect(Collectors.toList());
			return result;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
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
			return null;
		}
	}

	public <V> Long lLeftPush(String key, V value) {
		if(value instanceof Collection) {
			return lLeftPush(key, (Collection) value);
		}
		try {
			return getOperations().opsForList().leftPush(key, value);
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
	}
	public <V> Long lLeftPush(String key, V value, long seconds) {
		if(value instanceof Collection) {
			return lLeftPush(key, (Collection) value, seconds);
		}
		try {
			Long rt = getOperations().opsForList().leftPush(key, value);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
	}
	
	public <V> Long lLeftPush(String key, V value, Duration duration) {
		if(value instanceof Collection) {
			return lLeftPush(key, (Collection) value, duration);
		}
		try {
			Long rt = getOperations().opsForList().leftPush(key, value);
			if(!duration.isNegative()) {
				expire(key, duration);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
	}
	
	public <V> Long lLeftPush(String key, Collection<V> values) {
		try {
			Long rt = getOperations().opsForList().leftPushAll(key, values);
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
	}
	
	public <V> Long lLeftPush(String key, Collection<V> values, long seconds) {
		try {
			Long rt = getOperations().opsForList().leftPushAll(key, values);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
	}
	
	public <V> Long lLeftPush(String key, Collection<V> values, Duration duration) {
		try {
			Long rt = getOperations().opsForList().leftPushAll(key, values);
			if(!duration.isNegative()) {
				expire(key, duration);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
	}
	
	/**
	 * 将list放入缓存
	 *
	 * @param key   键
	 * @param value 值
	 * @return
	 */
	public <V> Long lRightPush(String key, V value) {
		if(value instanceof Collection) {
			return lRightPush(key, (Collection) value);
		}
		try {
			Long rt = getOperations().opsForList().rightPush(key, value);
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
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
	public <V> Long lRightPush(String key, V value, long seconds) {
		if(value instanceof Collection) {
			return lRightPush(key, (Collection) value, seconds);
		}
		try {
			Long rt = getOperations().opsForList().rightPush(key, value);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
	}
	
	public <V> Long lRightPush(String key, V value, Duration duration) {
		if(value instanceof Collection) {
			return lRightPush(key, (Collection) value, duration);
		}
		try {
			Long rt = getOperations().opsForList().rightPush(key, value);
			if(!duration.isNegative()) {
				expire(key, duration);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
	}

	public <V> Long lRightPush(String key, Collection<V> values) {
		try {
			return getOperations().opsForList().rightPushAll(key, values);
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
	}
	
	public <V> Long lRightPush(String key, Collection<V> values, Duration duration) {
		try {
			Long rt = getOperations().opsForList().rightPushAll(key, values);
			if(!duration.isNegative()) {
				expire(key, duration);
			}
			return rt;
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
	}
	
	public Object lRightPop(String key) {
		try {
			return getOperations().opsForList().rightPop(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	public Object lLeftPop(String key) {
		try {
			return getOperations().opsForList().leftPop(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
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
			return false;
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
			return 0;
		}
	}


	// ================================Hash=================================
	

	/**
	 * hash递减
	 *
	 * @param key  键
	 * @param item 项
	 * @param delta   要减少记(小于0)
	 * @return
	 */
	public Long hDecr(String key, String item, int delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		return getOperations().opsForHash().increment(key, item, -delta);
	}
	
	/**
	 * hash递减
	 *
	 * @param key  键
	 * @param item 项
	 * @param delta   要减少记(>=0)
	 * @return
	 */
	public Long hDecr(String key, String item, long delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		return getOperations().opsForHash().increment(key, item, -delta);
	}

	/**
	 * hash递减
	 *
	 * @param key  键
	 * @param item 项
	 * @param delta 要减少记(>=0)
	 * @return
	 */
	public Double hDecr(String key, String item, double delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递减因子必须>=0");
		}
		return getOperations().opsForHash().increment(key, item, -delta);
	}

	/**
	 * 删除hash表中的值
	 *
	 * @param key  键 不能为null
	 * @param hashKeys 项 可以使多个 不能为null
	 */
	public void hDel(String key, Object... hashKeys) {
		getOperations().opsForHash().delete(key, hashKeys);
	}

	/**
	 * Hash删除: hscan + hdel
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
			throw new BizRuntimeException(e.getMessage());
		}
	}
	
	/**
	 * HashGet
	 *
	 * @param key  键 不能为null
	 * @param hashKey 项 不能为null
	 * @return 值
	 */
	public Object hGet(String key, String hashKey) {
		try {
			return getOperations().opsForHash().get(key, hashKey);
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**
	 * 获取hashKey对应的指定键值
	 * @param key 键
	 * @param hashKeys 要筛选项
	 * @return
	 */
    public List<Object> hGet(String key, Collection hashKeys) {
    	try {
			return getOperations().opsForHash().multiGet(key, hashKeys);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Lists.newArrayList();
		}
	}
   	
	public List<Object> hGet(Collection<Object> keys, String redisPrefix) {
		List<Object> result = getOperations().executePipelined((RedisConnection connection) -> {
			keys.stream().forEach(key -> {
				String hashKey = RedisKeyConstant.getKeyStr(redisPrefix, key.toString());
				connection.hGetAll(rawKey(hashKey));
			});
			return null;
		}, this.valueSerializer());
		return result;
	}
	
	public List<Object> hGet(Collection<Object> keys, String redisPrefix, String redisField) {
		List<Object> result = getOperations().executePipelined((RedisConnection connection) -> {
			keys.stream().forEach(key -> {
				String hashKey = RedisKeyConstant.getKeyStr(redisPrefix, key.toString());
				connection.hGet(rawKey(hashKey), rawHashKey(redisField));
			});
			return null;
		}, this.valueSerializer());
		return result;
	}
	
	/**
	 * 判断hash表中是否有该项的值
	 *
	 * @param key  键 不能为null
	 * @param item 项 不能为null
	 * @return true 存在 false不存在
	 */
	public boolean hHasKey(String key, String item) {
		return getOperations().opsForHash().hasKey(key, item);
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
			return Maps.newHashMap();
		}
	}
	/**
	 * 获取hashKey对应的指定键值
	 * @param key 键
	 * @param field hash键
	 * @return 对应的键值
	 */
	public Object hmGet(String key, String field) {
		try {
			HashOperations<String, String, Object> opsForHash = getOperations().opsForHash();
			return opsForHash.get(key, field);
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
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
			return false;
		}
	}

	/**
	 * HashSet 并设置时间
	 *
	 * @param key  键
	 * @param map  对应多个键值
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
			return false;
		}
	}
	
	public boolean hmSet(String key, Map<String, Object> map, Duration duration) {
		try {
			getOperations().opsForHash().putAll(key, map);
			if(!duration.isNegative()) {
				expire(key, duration);
			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}
	
	public void hScan(String bigHashKey, Consumer<Entry<byte[],byte[]>> consumer) {
		this.getOperations().execute((RedisConnection redisConnection) -> {
			try (Cursor<Entry<byte[], byte[]>> cursor = redisConnection.hScan(rawHashKey(bigHashKey), ScanOptions.scanOptions().count(Long.MAX_VALUE).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new BizRuntimeException(e.getMessage());
			}
		});
	}

	public void hScan(String bigHashKey, String pattern, Consumer<Entry<byte[],byte[]>> consumer) {
		this.getOperations().execute((RedisConnection redisConnection) -> {
			try (Cursor<Entry<byte[], byte[]>> cursor = redisConnection.hScan(rawHashKey(bigHashKey), ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new BizRuntimeException(e.getMessage());
			}
		});
	}
	
	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 *
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @return true 成功 false失败
	 */
	public boolean hSet(String key, String item, Object value) {
		try {
			getOperations().opsForHash().put(key, item, value);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 *
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
	 * @return true 成功 false失败
	 */
	public boolean hSet(String key, String item, Object value, long seconds) {
		try {
			getOperations().opsForHash().put(key, item, value);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}
	
	public boolean hSet(String key, String item, Object value, Duration duration) {
		try {
			getOperations().opsForHash().put(key, item, value);
			if(!duration.isNegative()) {
				expire(key, duration);
			}
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}

	public boolean hSetNX(String key, String hashKey, Object value) {
		try {
			return getOperations().opsForHash().putIfAbsent(key, hashKey, value);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Boolean.FALSE;
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
			return 0L;
		}
	}
	

	
	
	
	
	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key  键
	 * @param item 项
	 * @param delta  要增加几(>=0)
	 * @return
	 */
	public Long hIncr(String key, String item, int delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		return getOperations().opsForHash().increment(key, item, delta);
	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key  键
	 * @param item 项
	 * @param delta  要增加几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Long hIncr(String key, String item, int delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Long increment = getOperations().opsForHash().increment(key, item, delta);
		if (seconds > 0) {
			expire(key, seconds);
		}
		return increment;
	}
	
	public Long hIncr(String key, String item, int delta, Duration duration) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Long increment = getOperations().opsForHash().increment(key, item, delta);
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
	public Long hIncr(String key, String item, long delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		return getOperations().opsForHash().increment(key, item, delta);
	}
	
	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 *
	 * @param key  键
	 * @param item 项
	 * @param delta  要增加几(>=0)
	 * @param seconds 过期时长（秒）
	 * @return
	 */
	public Long hIncr(String key, String item, long delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Long increment = getOperations().opsForHash().increment(key, item, delta);
		if (seconds > 0) {
			expire(key, seconds);
		}
		return increment;
	}

	public Long hIncr(String key, String item, long delta, Duration duration) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Long increment = getOperations().opsForHash().increment(key, item, delta);
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
	 * @param delta   要增加几(>=0)
	 * @return
	 */
	public Double hIncr(String key, String item, double delta) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		return getOperations().opsForHash().increment(key, item, delta);
	}
	
	public Double hIncr(String key, String item, double delta, long seconds) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Double increment = getOperations().opsForHash().increment(key, item, delta);
		if (seconds > 0) {
			expire(key, seconds);
		}
		return increment;
	}
	
	public Double hIncr(String key, String item, double delta, Duration duration) {
		if (delta < 0) {
			throw new BizRuntimeException("递增因子必须>=0");
		}
		Double increment = getOperations().opsForHash().increment(key, item, delta);
		if(!duration.isNegative()) {
			expire(key, duration);
		}
		return increment;
	}

	public Set<Object> hKeys(String key) {
		return getOperations().opsForHash().keys(key);
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
			return 0L;
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
			return Boolean.FALSE;
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
			return getOperations().opsForSet().members(key);
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	/**
	 * 根据key获取Set中的所有值
	 *
	 * @param key 键
	 * @return
	 */
	public <T> Set<T> sGet(String key, Class<T> clazz) {
		try {
			Set<Object> members = getOperations().opsForSet().members(key);
			return members.stream().map(member -> clazz.cast(member)).collect(Collectors.toSet());
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	/**
	 * 获取两个key的不同value
	 * 
	 * @param key1 键
	 * @param key2 键
	 * @return 返回key1中和key2的不同数据
	 */
	public Set<Object> sDiff(String key1, String key2) {
		try {
			return getOperations().opsForSet().difference(key1, key2);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Sets.newHashSet();
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
	public Long sDiffStore(String key1, String key2, String key3) {
		try {
			return getOperations().opsForSet().differenceAndStore(key1, key2, key3);
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
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
			return getOperations().opsForSet().isMember(key, value);
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
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
		return getOperations().opsForSet().randomMembers(key, count);
	}

	/**
	 * 随机获取指定数量的元素,去重(同一个元素只能选择一次)
	 * 
	 * @param key
	 * @param count
	 * @return
	 */
	public Set<Object> sRandomSetDistinct(String key, long count) {
		return getOperations().opsForSet().distinctRandomMembers(key, count);
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
			Long count = getOperations().opsForSet().remove(key, values);
			return count;
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
	}

	public void sScan(String bigSetKey, Consumer<byte[]> consumer) {
		this.getOperations().execute((RedisConnection redisConnection) -> {
			try (Cursor<byte[]> cursor = redisConnection.sScan(rawKey(bigSetKey), ScanOptions.scanOptions().count(Long.MAX_VALUE).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new BizRuntimeException(e.getMessage());
			}
		});
	}
	
	public void sScan(String bigSetKey, String pattern, Consumer<byte[]> consumer) {
		this.getOperations().execute((RedisConnection redisConnection) -> {
			try (Cursor<byte[]> cursor = redisConnection.sScan(rawKey(bigSetKey), ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new BizRuntimeException(e.getMessage());
			}
		});
	}
	

	/**
	 * 将set数据放入缓存
	 *
	 * @param key    键
	 * @param seconds   过期时长(秒)
	 * @param values 值 可以是多个
	 * @return 成功个数
	 */
	public Long sSetAndTime(String key, long seconds, Object... values) {
		try {
			Long count = getOperations().opsForSet().add(key, values);
			if (seconds > 0) {
				expire(key, seconds);
			}
			return count;
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
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
			return 0L;
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
	public Boolean sUnionAndStore(String key1, String key2, String key3) {
		try {
			getOperations().opsForSet().unionAndStore(key1, key2, key3);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage());
			return false;
		}
	}
	
	// ===============================ZSet=================================
	
	public Boolean zAdd(String key, String value, double score) {
		return getOperations().opsForZSet().add(key, value, score);
	}

	public Long zAdd(String key, Set<TypedTuple<Object>> tuples) {
		return getOperations().opsForZSet().add(key, tuples);
	}

	public Long zCard(String key) {
		return getOperations().opsForZSet().zCard(key);
	}
	
	/**
	 * 通过分数返回有序集合指定区间内的成员个数
	 * 
	 * @param key
	 * @param min
	 * @param max
	 */
	public Long zCount(String key, double min, double max) {
		return getOperations().opsForZSet().count(key, min, max);
	}


	/**
	 * Set删除: sscan + srem
	 *
	 * @param bigZsetKey 键
	 * @return 
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
			return Boolean.FALSE;
		}
	}
	
	public Double zIncr(String key, String value, double delta) {
		return getOperations().opsForZSet().incrementScore(key, value, delta);
	}

	public Double zIncr(String key, String value, double delta, long seconds) {
		Double result = getOperations().opsForZSet().incrementScore(key, value, delta);
		if (seconds > 0) {
			expire(key, seconds);
		}
		return result;
	}

	
	/**
	 * 移除zset中的元素
	 * 
	 * @param key
	 * @param values
	 */
	public Long zRem(String key, Object... values) {
		return getOperations().opsForZSet().remove(key, values);
	}

	/**
	 * 移除分数区间内的元素
	 * 
	 * @param key
	 * @param min
	 * @param max
	 */
	public void zRemByScore(String key, double min, double max) {
		getOperations().opsForZSet().removeRangeByScore(key, min, max);
	}
	
	public Set<Object> zRange(String key, long start, long end) {
		return getOperations().opsForZSet().range(key, start, end);
	}
	
	public Set<Object> zRangeByScore(String key, double min, double max) {
		return getOperations().opsForZSet().rangeByScore(key, min, max);
	}
	
	public Set<TypedTuple<Object>> zRangeWithScores(String key, long start, long end) {
		return getOperations().opsForZSet().rangeWithScores(key, start, end);
	}
	
	/**
	 * 在min到max范围内倒序获取zset及对应的score
	 */
	public Set<TypedTuple<Object>> zRangeByScoreWithScores(String key, double min, double max) {
		return getOperations().opsForZSet().rangeByScoreWithScores(key, min, max);  
	}

	/**
	 * @param key   :
	 * @param start :
	 * @param end   :0 到-1表示查全部
	 * @return {@link Set< Object>}
	 */
	public Set<Object> zRevrange(String key, long start, long end) {
		return getOperations().opsForZSet().reverseRange(key, start, end);
	}

	/**
	 * @param key   :
	 * @param start :
	 * @param end   :0 到-1表示查全部
	 * @return {@link Set< Long>}
	 */
	public Set<Long> zRevrangeForLong(String key, long  start, long end) {
		Set<Object> objects = getOperations().opsForZSet().reverseRange(key, start, end);
		Set<Long> collect = objects.stream().map(object -> Long.valueOf(object.toString()))
				.collect(Collectors.toCollection(LinkedHashSet::new));
		return collect;
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
		return getOperations().opsForZSet().reverseRangeWithScores(key, start, end);
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
		return getOperations().opsForZSet().reverseRangeByScoreWithScores(key, min, max);
	}

	public Long zRevRank(String key, Object value) {
		return getOperations().opsForZSet().reverseRank(key, value);
	}
	
	public void zScan(String bigZsetKey, Consumer<Tuple> consumer) {
		this.getOperations().execute((RedisConnection redisConnection) -> {
			try (Cursor<Tuple> cursor = redisConnection.zScan(rawKey(bigZsetKey), ScanOptions.scanOptions().count(Long.MAX_VALUE).build())) {
				cursor.forEachRemaining(consumer);
				return null;
			} catch (Exception e) {
				log.error(e.getMessage());
				throw new BizRuntimeException(e.getMessage());
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
				throw new BizRuntimeException(e.getMessage());
			}
		});
	}

	public Double zScore(String key, Object value) {
		return getOperations().opsForZSet().score(key, value);
	}
	

	// ===============================HyperLogLog=================================
	
	public Long pfAdd(String key, Object... values) {
		return getOperations().opsForHyperLogLog().add(key, values);
	}

	public Boolean pfDel(String key) {
		try {
			getOperations().opsForHyperLogLog().delete(key);
			return Boolean.TRUE;
		} catch (Exception e) {
			log.error(e.getMessage());
			return Boolean.FALSE;
		}
	}
	
	public Long pfCount(String... keys) {
		try {
			return getOperations().opsForHyperLogLog().size(keys);
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
		
	}
	
	public Long pfMerge(String destination, String...  sourceKeys) {
		try {
			return getOperations().opsForHyperLogLog().union(destination, sourceKeys);
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0L;
		}
		
	}
	
	// ===============================BitMap=================================

	public Boolean setBit(String key, long offset, boolean value) {
		return getOperations().opsForValue().setBit(key, offset, value);
	}

	public Boolean getBit(String key, long offset) {
		try {
			return getOperations().opsForValue().getBit(key, offset);
		} catch (Exception e) {
			log.error(e.getMessage());
			return Boolean.FALSE;
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
		getOperations().convertAndSend(channel, message);
	}
	
	// ===============================Lock=================================
	
	public Long luaIncr(String lockKey, long amount) {
		Assert.hasLength(lockKey, "lockKey must not be empty");
		return this.executeLuaScript(INCR_SCRIPT, Collections.singletonList(lockKey), amount);
	}
	
	public boolean tryLock(String lockKey, String requestId, long timeout) {
		Assert.hasLength(lockKey, "lockKey must not be empty");
		Assert.hasLength(requestId, "requestId must not be empty");
		return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, Duration.ofSeconds(timeout));
	}
	
	public boolean tryLock(String lockKey, String requestId, long timeout, TimeUnit unit) {
		Assert.hasLength(lockKey, "lockKey must not be empty");
		Assert.hasLength(requestId, "requestId must not be empty");
		return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, timeout, unit);
	}

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
		}
       	return false;
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
        } catch (Throwable e) {
        	log.error("release lock occurred an exception", e);
        }
        return false;
    }
    
	// ===============================Pipeline=================================
	
	public List<Object> executePipelined(RedisCallback<?> action) {
		return getOperations().executePipelined(action);
	}
	
	public List<Object> executePipelined(RedisCallback<?> action, RedisSerializer<?> resultSerializer) {
		return getOperations().executePipelined(action, resultSerializer);
	}
	
	// ===============================RedisScript=================================
	
	/**
	 * 执行lua脚本
	 * 
	 * @param luaScript 脚本内容
	 * @param keys      redis键列表
	 * @param values    值列表
	 * @return
	 */
	public Object executeLuaScript(String luaScript, List<String> keys, Object... values) {
		RedisScript redisScript = RedisScript.of(luaScript);
		return getOperations().execute(redisScript, RedisSerializer.java(), RedisSerializer.java(), keys, values);
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
	public <T> T executeLuaScript(String luaScript, Class<T> resultType, List<String> keys, Object... values) {
		RedisScript redisScript = RedisScript.of(luaScript, resultType);
		return (T) getOperations().execute(redisScript, keys, values);
	}
	
	/**
	 * 执行lua脚本
	 * 
	 * @param luaScript  脚本内容
	 * @param keys       redis键列表
	 * @param values     值列表
	 * @return
	 */
	public <T> T executeLuaScript(RedisScript<T> luaScript, List<String> keys, Object... values) {
		return getOperations().execute(luaScript, keys, values);
	}
	
	/*
	 * @param luaScript 脚本内容
	 * @param keys      redis键列表
	 * @param values    值列表
	 * @return
	 */
	public Object executeLuaScript(Resource luaScript, List<String> keys, Object... values) {
		RedisScript redisScript = RedisScript.of(luaScript);
		return getOperations().execute(redisScript, keys, values);
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
	public <T> T executeLuaScript(Resource luaScript, Class<T> resultType, List<String> keys, Object... values) {
		RedisScript redisScript = RedisScript.of(luaScript, resultType);
		return (T) getOperations().execute(redisScript, keys, values);
	}
	
	// ===============================RedisCommand=================================
	
	/**
	 * 获取redis服务器时间 保证集群环境下时间一致
	 * @return Redis服务器时间戳
	 */
	public Long timeNow() {
		return getOperations().execute((RedisCallback<Long>) redisConnection -> {
			return redisConnection.time();
		});
	}

	/**
	 * 获取redis服务器时间 保证集群环境下时间一致
	 * @return Redis服务器时间戳
	 */
	public Long period(long expiration) {
		return getOperations().execute((RedisCallback<Long>) redisConnection -> {
			return expiration - redisConnection.time();
		});
	}
	
	public Long dbSize() {
		return getOperations().execute((RedisCallback<Long>) redisConnection -> {
			return redisConnection.dbSize();
		});
	}
	
	public Long lastSave() {
		return getOperations().execute((RedisCallback<Long>) redisConnection -> {
			return redisConnection.lastSave();
		});
	}

	public void bgReWriteAof() {
		getOperations().execute((RedisCallback<Void>) redisConnection -> {
			redisConnection.bgReWriteAof();
			return null;
		});
	}
	
	public void bgSave() {
		getOperations().execute((RedisCallback<Void>) redisConnection -> {
			redisConnection.bgSave();
			return null;
		});
	}
	
	public void save() {
		getOperations().execute((RedisCallback<Void>) redisConnection -> {
			redisConnection.save();
			return null;
		});
	}
	
	public void flushDb() {
		getOperations().execute((RedisCallback<Void>) redisConnection -> {
			redisConnection.flushDb();
			return null;
		});
	}
	
	public void flushAll() {
		getOperations().execute((RedisCallback<Void>) redisConnection -> {
			redisConnection.flushAll();
			return null;
		});
	}
	
	// ===============================batchGet=================================
	
	/**
   	 * 批量获取用户信息
   	 */
   	public List<Object> batchGetUserInfo(Collection<String> uids) {
   		List<Object> result = getOperations().executePipelined((RedisConnection connection) -> {
   			uids.stream().forEach(uid -> {
   				String hashKey = RedisKey.USER_INFO.getFunction().apply(uid);
   				connection.hGetAll(rawKey(hashKey));
   			});
   			return null;
   		}, this.valueSerializer());
   		return result;
   	}
	
}