/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.extras.redis.setup;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import lombok.extern.slf4j.Slf4j;


/**
 * 1、基于RedissonClient操作的二次封装
 * 2、参考：
 * https://blog.csdn.net/qq_24598601/article/details/105876432
 */
@Slf4j
public class RedissonOperationTemplate {
	
	private RedissonClient redissonClient;
	
	public RedissonOperationTemplate(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
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
			boolean tryLock = fairLock.tryLock(1000, expire, TimeUnit.MILLISECONDS);
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
						tryLock = fairLock.tryLock(10, expire, TimeUnit.MILLISECONDS);
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
