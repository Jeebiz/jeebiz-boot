package net.jeebiz.boot.extras.redis.setup;

import org.slf4j.helpers.MessageFormatter;
import org.springframework.data.redis.core.RedisKey;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Function;

public enum BizRedisKey {

	/**
	 * 用户任务列表
	 */
	USER_TASK_DAY("每日任务列表", (userId )->{
		String prefix = MessageFormatter.format(BizRedisKeyConstant.USER_TASK_KEY, userId).getMessage();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(BizRedisKeyConstant.YYYYMMDD);
        return RedisKey.getKeyStr(prefix, LocalDate.now().format(formatter));
    }),
	/**
	 * redis 用户登陆次数
	 */
	USER_LOGIN_AMOUNT("用户登陆次数", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_LOGIN_AMOUNT_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * redis 用户 token
	 */
	USER_TOKEN("用户 token", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_TOKEN_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * redis 用户黑名单缓存
	 */
	USER_BLACKLIST("用户黑名单", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_BLACKLIST_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * redis 我关注的用户
	 */
	USER_FOLLOW("我关注的用户", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_FOLLOW_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * redis 关注我的用户
	 */
	USER_FOLLOWERS("关注我的用户", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_FOLLOWERS_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * redis 新关注我的用户
	 */
	USER_NEW_FOLLOWERS("新关注我的用户", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_NEW_FOLLOWERS_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * redis 用户信息
	 */
	USER_INFO("用户信息", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_INFO_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * redis 用户单点登录状态
	 */
	USER_SSO_STATE("用户单点登录状态", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_SSO_STATE_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * 用户会话列表
	 */
	USER_SESSIONS("用户会话列表", (p)->{
		return RedisKey.getKeyStr(BizRedisKeyConstant.USER_SESSIONS_KEY);
    }),
	/**
	 * 用户会话信息
	 */
	USER_SESSION("用户会话信息", (sessionId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_SESSION_KEY, sessionId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * 用户坐标缓存
	 */
	USER_GEO_LOCATION("用户坐标", (userId)->{
		return RedisKey.getKeyStr(BizRedisKeyConstant.USER_GEO_LOCATION_KEY);
    }),
	/**
	 * 用户坐标对应的地理位置缓存
	 */
	USER_GEO_INFO("用户坐标对应的地理位置缓存", (userId)->{
		return RedisKey.getKeyStr(BizRedisKeyConstant.USER_GEO_INFO_KEY);
    }),
	/**
	 * 用户资产缓存

	USER_ASSETS_AMOUNT("用户资产", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_ASSETS_AMOUNT_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }), */
	/**
	 * 用户资产换算临时缓存
	 */
	USER_EXCHANGE_AMOUNT("用户资产换算临时缓存", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_EXCHANGE_AMOUNT, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * 用户金币增量缓存
	 */
	USER_COIN_AMOUNT("用户金币", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_COIN_AMOUNT_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * 用户珍珠增量缓存
	*/
	USER_PEARL_AMOUNT("用户珍珠", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_PEARL_AMOUNT_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * 用户经验增量缓存
	 */
	USER_EXP_AMOUNT("用户经验", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_EXP_AMOUNT_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * 用户权益缓存
	 */
	USER_RITHTS("用户权益", (userId)->{
		String keyStr = MessageFormatter.format(BizRedisKeyConstant.USER_RITHTS_KEY, userId).getMessage();
		return RedisKey.getKeyStr(keyStr);
    }),
	/**
	 * 消息队列消息消费锁
	 */
	MQ_CONSUME_LOCK("消息队列消息消费锁", (msgKey)->{
		return RedisKey.getKeyStr(BizRedisKeyConstant.MQ_CONSUME_LOCK, Objects.toString(msgKey));
    }),
	/**
	 * IP地区编码缓存
	 */
	IP_REGION_INFO("用户坐标对应的地区编码缓存", (ip)->{
		return RedisKey.getKeyStr(BizRedisKeyConstant.IP_REGION_KEY, ip);
	}),
	/**
	 * IP坐标缓存
	 */
	IP_LOCATION_INFO("用户坐标对应的地理位置缓存", (ip)->{
		return RedisKey.getKeyStr(BizRedisKeyConstant.IP_LOCATION_KEY, ip);
    }),
	/**
	 * IP坐标缓存（百度服务缓存）
	 */
	IP_LOCATION_BAIDU_INFO("IP坐标缓存（百度服务缓存）", (ip)->{
		return RedisKey.getKeyStr(BizRedisKeyConstant.IP_BAIDU_LOCATION_KEY, ip);
    }),
	/**
	 * IP坐标缓存（太平洋网络）
	 */
	IP_LOCATION_PCONLINE_INFO("IP坐标缓存（太平洋网络）", (ip)->{
		return RedisKey.getKeyStr(BizRedisKeyConstant.IP_PCONLINE_LOCATION_KEY, ip);
    }),
	/**
	 * 接口幂等缓存（Token模式）
	 */
	IDEMPOTENT_TOKEN_KEY("接口幂等缓存（Token模式）", (token)->{
		return RedisKey.getKeyStr(BizRedisKeyConstant.IDEMPOTENT_TOKEN_KEY, token);
	}),
	/**
	 * 接口幂等缓存（参数模式）
	 */
	IDEMPOTENT_ARGS_KEY("接口幂等缓存（参数模式）", (args)->{
		return RedisKey.getKeyStr(BizRedisKeyConstant.IDEMPOTENT_ARGS_KEY, args);
	})

	;

	private String desc;
    private Function<Object, String> function;

    BizRedisKey(String desc, Function<Object, String> function) {
        this.desc = desc;
        this.function = function;
    }

    public String getDesc() {
		return desc;
	}

    /**
     * 1、获取全名称key
     * @return
     */
    public String getKey() {
        return this.function.apply(null);
    }

    /**
     * 1、获取全名称key
     * @param key
     * @return
     */
    public String getKey(Object key) {
        return this.function.apply(key);
    }

}
