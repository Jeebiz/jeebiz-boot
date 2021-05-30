package net.jeebiz.boot.extras.redis.setup;

public interface RedisConstant {

	/**
	 * 手机号黑名单
	 */
	String SET_SMS_BLACK_LIST = "sms:blacklist";
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
	 * 记录手机号发短信次数
	 */
	String STR_SMS_MOBILE_TIME = "sms:mobile:time";
	/**
	 * 记录设备发短信次数
	 */
	String STR_SMS_DEV_TIME = "sms:dev:time";
	/**
	 * 发送短信锁
	 */
	String STR_SMS_LOCK_MOBILE = "sms:lock";
	/**
	 * 短信验证码 type + 手机号
	 */
	String STR_SMS_CODE = "sms:code";
	/**
	 * 用户登陆次数
	 */
	String USER_LOGIN_NUM = "user:login:num";
	/**
	 * 用户token过期时间
	 */
	Long USER_TOKEN_EXPIRE = 7 * 86400L;
	/**
	 * 用户token
	 */
	String USER_TOKEN = "user:token";
	/**
	 * redis 用户回话状态
	 */
	String USER_SESSION_STATE = "user:session.state";
	/**
	 * redis 用户信息前缀
	 */
	String USER_INFO_PREFIX = "user:info";
	/**
	 * redis 用户单点登录状态
	 */
	String USER_SSO_STATE = "user:sso.state";
	/**
	 * 用户查询前缀
	 */
	String USER_QUERY_PREFIX = "user:query";
	/**
	 * 用户坐标缓存
	 */
	String USER_GEO_LOCATION = "user:geo:location";
	/**
	 * 锁过期时间5秒钟
	 */
	Long FIVE_SECONDS = 5 * 1000L;
	/**
	 * 查询数据库用户信息时加锁
	 */
	String USER_INFO_LOCK = "user:info:lock";
	/**
	 * 用户信息解锁缓存
	 */
	String USER_INFO_UNLOCK = "user:info:unlock";
	/**
	 * 用户信息预览缓存
	 */
	String USER_INFO_PREVIEW = "user:info:preview";
	/**
	 * 用户任务列表
	 */
	String USER_TASK_LIST = "user:task:list";
	/**
	 * 用户金币增量缓存
	 */
	String USER_COIN_AMOUNT = "user:coin:amount";
	/**
	 * 用户珍珠增量缓存
	 */
	String USER_PEARL_AMOUNT = "user:pearl:amount";
	/**
	 * 用户经验增量缓存
	 */
	String USER_EXP_AMOUNT = "user:exp:amount";
	/**
	 * 用户会员类型缓存
	 */
	String USER_VIP_TYPES = "user:vip:types";
	/**
	 * 用户会员权益缓存
	 */
	String USER_VIP_INTEREST = "user:vip:interest";

	String USER_PERMS = "user:dbperms";
	String USER_PERMS_HASH = "user:dbperms:%s:%s";
		
	String SET_APPS = "user:apps";

	String DBMATA_CATALOG = "dbmata:catalog";
	String DBMATA_CATALOG_LODING = "dbmata:catalog:loding";
	

}
