package net.jeebiz.boot.extras.redis.setup;

import java.util.Objects;

public abstract class RedisKeyConstant {
	
	public final static String DELIMITER = ":";
	public final static String YYYYMMDD = "yyyyMMdd";
	public final static String YYYYMM = "yyyyMM";
	public final static String YYYY = "yyyy";

	/**
	 * 用户token过期时间
	 */
	public final static Long TOKEN_EXPIRE = 7 * 86400L;
	/**
	 * 锁过期时间5秒钟
	 */
	public final static Long FIVE_SECONDS = 5 * 1000L;

	/**
	 * 应用黑名单缓存
	 */
	public final static String APP_BLACKLIST_PREFIX = "app:blacklist";
	
	/**
	 * redis 用户登陆次数
	 */
	public final static String USER_LOGIN_AMOUNT_KEY = "user:login:amount:{}";
	/**
	 * redis 用户 token
	 */
	public final static String USER_TOKEN_KEY = "user:token:{}";
	/**
	 * redis 用户黑名单缓存
	 */
	public final static String USER_BLACKLIST_KEY = "user:blacklist:{}";
	/**
	 * redis 我关注的用户缓存
	 */
	public final static String USER_FOLLOW_KEY = "user:follow:{}";
	/**
	 * redis 关注我的用户缓存
	 */
	public final static String USER_FOLLOWERS_KEY = "user:followers:{}";
	/**
	 * redis 新关注我的用户缓存
	 */
	public final static String USER_NEW_FOLLOWERS_KEY = "user:new:followers:{}";
	/**
	 * redis 用户信息前缀
	 */
	public final static String USER_INFO_KEY = "user:info:{}";
	/**
	 * redis 用户单点登录状态
	 */
	public final static String USER_SSO_STATE_KEY = "user:sso:{}";
	/**
	 * 用户坐标缓存
	 */
	public final static String USER_GEO_LOCATION_KEY = "user:geo:location";
	/**
	 * 用户任务列表
	 */
	public final static String USER_TASK_KEY = "user:task:{}";
	/**
	 * 用户资产缓存
	 */
	public final static String USER_ASSETS_AMOUNT_KEY = "user:assets:{}";
	/**
	 * 用户金币增量缓存
	 */
	public static String USER_COIN_AMOUNT_KEY = "user:coin:amount:{}";
	/**
	 * 用户珍珠增量缓存
	 */
	public static String USER_PEARL_AMOUNT_KEY = "user:pearl:amount:{}";
	/**
	 * 用户经验增量缓存
	 */
	public static String USER_EXP_AMOUNT_KEY = "user:exp:amount:{}";
	/**
	 * 用户会员权益缓存
	 */
	public final static String USER_RITHTS_KEY = "user:rights:{}";
	/**
	 * 消息队列消息消费锁
	 */
	public final static String MQ_CONSUME_LOCK = "mq:consume:lock";

	public static String REDIS_PREFIX = "rds";
	
	public static String getKeyStr(String... args) {
		StringBuilder tempKey = new StringBuilder(REDIS_PREFIX);
		for (String s : args) {
			if (Objects.isNull(s) || s.length() <= 0) {
				continue;
			}
			tempKey.append(DELIMITER).append(s);
		}
		return tempKey.toString();
	}

}
