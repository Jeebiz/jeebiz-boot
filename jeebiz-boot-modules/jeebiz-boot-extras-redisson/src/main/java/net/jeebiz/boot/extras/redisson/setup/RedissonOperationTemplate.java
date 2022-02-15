/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.extras.redisson.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RScript;
import org.redisson.api.RScript.Mode;
import org.redisson.api.RScript.ReturnType;
import org.redisson.api.RedissonClient;
import org.springframework.util.Assert;

import lombok.extern.slf4j.Slf4j;


/**
 * 1、基于RedissonClient操作的二次封装
 * 2、参考：
 * https://github.com/redisson/redisson
 * https://blog.csdn.net/qq_24598601/article/details/105876432
 */
@Slf4j
public class RedissonOperationTemplate {
	
	private RedissonClient redissonClient;
	public RedissonOperationTemplate(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	// ===============================RedisScript=================================

	public RAtomicLong luaIncr(String lockKey, long amount) {
		Assert.hasLength(lockKey, "lockKey must not be empty");
		RScript script = redissonClient.getScript();
		List<Object> keys = new ArrayList<>();
		keys.add(lockKey);
		String re = script.eval(
		        RScript.Mode.READ_WRITE,
		        "return redis.call('set',KEYS[1],ARGV[1]);",
		        RScript.ReturnType.VALUE,
		        keys, amount );
		return redissonClient.getAtomicLong(lockKey);
	}
	
	/**
	 * 扣取现金账本
	 * @param actid 账户id
	 * @return
	 * @throws InterruptedException
	 */
	public boolean deductCashAccount(String actid, int amount) throws InterruptedException {
		/** 扣余额直接操作redis缓存数据库，key由账户ID，-字符，字符串balance组成 */
		long start = System.currentTimeMillis();
		String key = "CASH_" + actid;
		String lock_point = "LOCK_CASH_" + actid;
		RLock lock = redissonClient.getLock(lock_point);// 获取账户锁对象
		log.info("get lock " + lock_point);
		try {
			boolean locked = lock.tryLock(10, 60, TimeUnit.SECONDS); // 尝试锁住账户对象,waitTime第一个参数获取锁超时时间30毫秒,leaseTime第二参数,锁自动释放时间
			if (!locked) {
				log.info("cann't get lock ,id=" + actid);
				return false;
			}
			log.info("get lock " + lock_point + " ok");
			RBucket<Integer> atomicbalance = redissonClient.<Integer>getBucket(key); // 获取原子余量
			boolean result_flag = true;
			if (atomicbalance.get() == 0) {
				log.error(" error ,balance less than  or equal to 0");

				result_flag = false;
			} else {
				atomicbalance.set(atomicbalance.get().intValue() - amount);// 扣除余量
				log.info("balance is  " + atomicbalance.get());
				result_flag = true;
			}
			log.info("debut cash , cost time:" + (System.currentTimeMillis() - start));
			return result_flag;
		} finally {
			lock.unlock(); // 解锁
		}

	}

	/**
	 * 家族lua脚本，返回sha值
	 * 
	 * @param luaScript 脚本内容
	 * @return
	 */
	public String loadLuaScript(String luaScript) {
		return redissonClient.getScript().scriptLoad(luaScript);
	}
	
	/**
	 * 执行lua脚本
	 * 
	 * @param luaScript  脚本内容
	 * @param resultType 返回值类型
	 * @param keys       redis键列表
	 * @param values     值列表
	 * @return
	 */
	public <R> R executeLuaScript(String luaScript, ReturnType returnType, List<Object> keys, Object... values) {
		RScript script = redissonClient.getScript();
		return script.eval(Mode.READ_WRITE, luaScript, returnType, keys, values);
	}
	
	/**
	 * 执行lua脚本
	 * 
	 * @param mode  	   执行模式
	 * @param luaScript  脚本内容
	 * @param resultType 返回值类型
	 * @param keys       redis键列表
	 * @param values     值列表
	 * @return
	 */
	public <R> R executeLuaScript(Mode mode, String luaScript, ReturnType returnType, List<Object> keys, Object... values) {
		RScript script = redissonClient.getScript();
		return script.eval(mode, luaScript, returnType, keys, values);
	}
	
	/**
	 * 执行lua脚本
	 * 
	 * @param luaScript  脚本内容
	 * @param resultType 返回值类型
	 * @return
	 */
	public <R> R executeLuaScript(String luaScript, ReturnType returnType) {
		RScript script = redissonClient.getScript();
		return script.eval(Mode.READ_WRITE, luaScript, returnType);
	}
	
	/**
	 * 执行lua脚本
	 * 
	 * @param mode  	   执行模式
	 * @param luaScript  脚本内容
	 * @param resultType 返回值类型
	 * @return
	 */
	public <R> R executeLuaScript(Mode mode, String luaScript, ReturnType returnType) {
		RScript script = redissonClient.getScript();
		return script.eval(mode, luaScript, returnType);
	}
 
	/**
	 * 加锁
	 * 
	 * @param lockKey       锁的 key
	 * @param expire        key 的过期时间，单位 ms
	 * @param retryTimes    重试次数，即加锁失败之后的重试次数
	 * @param retryInterval 重试时间间隔，单位 ms
	 * @return 加锁 true 成功
	 */
	public RLock tryLock(String lockKey, long expire, int retryTimes, long retryInterval) {
		log.info("locking... redisK = {}", lockKey);
		RLock fairLock = redissonClient.getFairLock(lockKey);
		try {
			boolean tryLock = fairLock.tryLock(10000, expire, TimeUnit.MILLISECONDS);
			if (tryLock) {
				log.info("locked... redisK = {}", lockKey);
				return fairLock;
			} else {
				// 重试获取锁
				log.info("retry to acquire lock: [redisK = {}]", lockKey);
				int count = 0;
				while (count < retryTimes) {
					try {
						Thread.sleep(retryInterval);
						tryLock = fairLock.tryLock(10000, expire, TimeUnit.MILLISECONDS);
						if (tryLock) {
							log.info("locked... redisK = {}", lockKey);
							return fairLock;
						}
						log.warn("{} times try to acquire lock", count + 1);
						count++;
					} catch (Exception e) {
						log.error("acquire redis occurred an exception", e);
						break;
					}
				}

				log.info("fail to acquire lock {}", lockKey);
			}
		} catch (Throwable e1) {
			log.error("acquire redis occurred an exception", e1);
		}

		return fairLock;
	}

	/**
	 * 释放KEY
	 * 
	 * @param fairLock 分布式公平锁
	 * @return 释放锁 true 成功
	 */
	public boolean unlock(RLock fairLock) {
		try {
			// 如果这里抛异常，后续锁无法释放
			if (fairLock.isLocked() && fairLock.isHeldByCurrentThread()) {
				fairLock.unlock();
				log.info("release lock success");
				return true;
			}
		} catch (Throwable e) {
			log.error("release lock occurred an exception", e);
		}
		return false;
	}
	
}
