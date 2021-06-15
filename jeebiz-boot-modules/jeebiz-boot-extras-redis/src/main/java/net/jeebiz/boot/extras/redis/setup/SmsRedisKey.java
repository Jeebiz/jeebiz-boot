package net.jeebiz.boot.extras.redis.setup;

import java.util.function.BiFunction;

import org.springframework.biz.utils.DateUtils;

public enum SmsRedisKey {

	/**
	 * redis 短信黑名单缓存
	 */
	SMS_BLACKLIST("短信黑名单", (mobile, type)->{
		return RedisKeyConstant.getKeyStr(RedisKeyConstant.SET_SMS_BLACK_LIST);
    }),
	/**
	 * redis 短信每分钟发送缓存
	 */
	SMS_LIMIT_MINUTE("短信每秒发送缓存", (mobile, type)->{
        return RedisKeyConstant.getKeyStr(RedisKeyConstant.STR_SMS_MOBILE_TIME, DateUtils.getDate("yyyyMMddHHmm"), type, mobile);
    }),
	/**
	 * redis 短信每小时发送缓存
	 */
	SMS_LIMIT_HOUR("短信每小时发送缓存", (mobile, type)->{
        return RedisKeyConstant.getKeyStr(RedisKeyConstant.STR_SMS_MOBILE_TIME, DateUtils.getDate("yyyyMMddHH"), type, mobile);
    }),
	/**
	 * redis 短信每天发送缓存
	 */
	SMS_LIMIT_DAY("短信每天发送缓存", (mobile, type)->{
        return RedisKeyConstant.getKeyStr(RedisKeyConstant.STR_SMS_MOBILE_TIME, DateUtils.getDate("yyyyMMdd"), type, mobile);
    }),
	/**
	 * redis 短信设备发送缓存
	 */
	SMS_LIMIT_DEVICE("短信设备发送缓存", (mobile, device)->{
        return RedisKeyConstant.getKeyStr(RedisKeyConstant.STR_SMS_DEV_TIME, mobile, device);
    }),
	/**
	 * redis 短信发送锁
	 */
	SMS_SEND_LOCK("短信发送锁", (mobile, type)->{
        return RedisKeyConstant.getKeyStr(RedisKeyConstant.STR_SMS_LOCK_MOBILE, mobile, type);
    }),
	/**
	 * redis 短信验证码缓存
	 */
	SMS_CODE("短信验证码缓存", (mobile, type)->{
        return RedisKeyConstant.getKeyStr(RedisKeyConstant.STR_SMS_CODE, mobile, type);
    });
	
	private String desc;
    private BiFunction<String, String, String> function;
    
    SmsRedisKey(String desc, BiFunction<String, String, String> function) {
        this.desc = desc;
        this.function = function;
    }
    
    public String getDesc() {
		return desc;
	}
    
    /**
     * 1、获取全名称key
     * @param key
     * @return
     */
    public String getKey() {
        return this.function.apply(null, null);
    }
    
    /**
     * 1、获取全名称key
     * @param key
     * @return
     */
    public String getKey(String key) {
        return this.function.apply(key, null);
    }
    
    /**
     * 2、获取全名称key
     * @param key
     * @return
     */
    public String getKey(String key, String key2) {
        return this.function.apply(key, key2);
    }

    public BiFunction<String, String, String> getFunction() {
        return function;
    }
	
}
