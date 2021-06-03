package net.jeebiz.boot.extras.redis.setup;

public interface RedisKeyConstant {
	
	String DELIMITER = ":";
	String YYYYMMDD = "yyyyMMdd";
	String YYYYMM = "yyyyMM";
	String YYYY = "yyyy";
	
	/**
	 * 用户token过期时间
	 */
	Long TOKEN_EXPIRE = 7 * 86400L;
	/**
	 * 锁过期时间5秒钟
	 */
	Long FIVE_SECONDS = 5 * 1000L;
	
	/**
	 * 短信发送次数最大值
	 */
	Integer SMS_TIME_MAX = 10;
	/**
	 * 手机/设备发送短信次数记录过期时间
	 */
	Long SMS_TIME_EXPIRE = 3500 * 6L;
	/**
	 * 验证码过期时间
	 */
	Long SMS_EXPIRE = 60 * 5L;

	/**
	 * 手机号黑名单
	 */
	String SET_SMS_BLACK_LIST = "set:sms:blacklist";
	/**
	 * 记录手机号发短信次数
	 */
	String STR_SMS_MOBILE_TIME = "str:sms:mobile:time:";
	/**
	 * 记录设备发短信次数
	 */
	String STR_SMS_DEV_TIME = "str:sms:dev:time:";
	/**
	 * 发送短信锁
	 */
	String STR_SMS_LOCK_MOBILE = "str:sms:lock:";
	/**
	 * 短信验证码 type + 手机号
	 */
	String STR_SMS_CODE = "str:sms:code:";
	/**
	 * redis 用户登陆次数
	 */
	String USER_LOGIN_AMOUNT_KEY = "user:{}:login:amount";
	/**
	 * redis 用户 token
	 */
	String USER_TOKEN_KEY = "user:{}:token";
	/**
	 * redis 用户黑名单缓存
	 */
	String USER_BLACKLIST_KEY = "user:{}:blacklist";
	/**
	 * redis 我关注的用户缓存
	 */
	String USER_FOLLOW_KEY = "user:{}:follow";
	/**
	 * redis 关注我的用户缓存
	 */
	String USER_FOLLOWERS_KEY = "user:{}:followers";
	/**
	 * redis 新关注我的用户缓存
	 */
	String USER_NEW_FOLLOWERS_KEY = "user:{}:new:followers";
	/**
	 * redis 用户信息前缀
	 */
	String USER_INFO_KEY = "user:{}:info";
	/**
	 * redis 用户单点登录状态
	 */
	String USER_SSO_STATE_KEY = "user:{}:sso:state";
	/**
	 * 用户坐标缓存
	 */
	String USER_GEO_LOCATION_KEY = "user:geo:location";
	/**
	 * 用户任务列表
	 */
	String USER_TASK_KEY = "user:{}:task";
	/**
	 * 用户金币增量缓存
	 */
	String USER_COIN_AMOUNT_KEY = "user:{}:coin:amount";
	/**
	 * 用户珍珠增量缓存
	 */
	String USER_PEARL_AMOUNT_KEY = "user:{}:pearl:amount";
	/**
	 * 用户经验增量缓存
	 */
	String USER_EXP_AMOUNT_KEY = "user:{}:exp:amount";
	/**
	 * 用户会员权益缓存
	 */
	String USER_RITHTS_KEY = "user:{}:rights";
	
	public static String getKeyStr(String... args) {
		StringBuilder tempKey = new StringBuilder("kding");
		for (String s : args) {
			if (s.length() <= 0) {
				continue;
			}
			tempKey.append(DELIMITER).append(s);
		}
		return tempKey.toString();
	}
	
}
