package net.jeebiz.boot.extras.redis.setup;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.Function;

import org.slf4j.helpers.MessageFormatter;

public enum RedisKey {

	/**
	 * 用户任务列表
	 */
	USER_TASK_DAY("每日任务列表", (userId )->{
		String prefix = MessageFormatter.format(RedisKeyConstant.USER_TASK_KEY, userId).getMessage();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(RedisKeyConstant.YYYYMMDD);
        return RedisKeyConstant.getKeyStr(prefix, LocalDate.now().format(formatter));
    }),
	/**
	 * redis 用户登陆次数
	 */
	USER_LOGIN_AMOUNT("用户登陆次数", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_LOGIN_AMOUNT_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * redis 用户 token
	 */
	USER_TOKEN("用户 token", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_TOKEN_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * redis 用户黑名单缓存
	 */
	USER_BLACKLIST("用户黑名单", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_BLACKLIST_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * redis 我关注的用户 
	 */
	USER_FOLLOW("我关注的用户", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_FOLLOW_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * redis 关注我的用户
	 */
	USER_FOLLOWERS("关注我的用户", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_FOLLOWERS_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * redis 新关注我的用户
	 */
	USER_NEW_FOLLOWERS("新关注我的用户", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_NEW_FOLLOWERS_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * redis 用户信息
	 */
	USER_INFO("用户信息", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_INFO_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * redis 用户单点登录状态
	 */
	USER_SSO_STATE("用户单点登录状态", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_SSO_STATE_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * 用户会话列表
	 */
	USER_SESSIONS("用户会话列表", (p)->{
		return RedisKeyConstant.getKeyStr(RedisKeyConstant.USER_SESSIONS_KEY);
    }),
	/**
	 * 用户会话信息
	 */
	USER_SESSION("用户会话信息", (sessionId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_SESSION_KEY, sessionId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * 用户坐标缓存
	 */
	USER_GEO_LOCATION("用户坐标", (userId)->{
		return RedisKeyConstant.getKeyStr(RedisKeyConstant.USER_GEO_LOCATION_KEY);
    }),
	/**
	 * 用户坐标对应的地理位置缓存
	 */
	USER_GEO_INFO("用户坐标对应的地理位置缓存", (userId)->{
		return RedisKeyConstant.getKeyStr(RedisKeyConstant.USER_GEO_INFO_KEY);
    }),
	/**
	 * 用户坐标对应的地理位置缓存
	 */
	USER_GEO_IPV4("用户坐标对应的地理位置缓存", (userId)->{
		return RedisKeyConstant.getKeyStr(RedisKeyConstant.USER_GEO_INFO_KEY);
    }),
	/**
	 * 用户资产缓存
	 */
	USER_ASSETS_AMOUNT("用户资产", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_ASSETS_AMOUNT_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * 用户金币增量缓存
	 */
	USER_COIN_AMOUNT("用户珍珠", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_COIN_AMOUNT_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * 用户珍珠增量缓存
	 */
	USER_PEARL_AMOUNT("用户珍珠", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_PEARL_AMOUNT_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * 用户经验增量缓存
	 */
	USER_EXP_AMOUNT("用户经验", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_EXP_AMOUNT_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * 用户权益缓存
	 */
	USER_RITHTS("用户权益", (userId)->{
		String keyStr = MessageFormatter.format(RedisKeyConstant.USER_RITHTS_KEY, userId).getMessage();
		return RedisKeyConstant.getKeyStr(keyStr);
    }),
	/**
	 * 消息队列消息消费锁
	 */
	MQ_CONSUME_LOCK("消息队列消息消费锁", (msgKey)->{
		return RedisKeyConstant.getKeyStr(RedisKeyConstant.MQ_CONSUME_LOCK, Objects.toString(msgKey));
    })
	
	;

	private String desc;
    private Function<String, String> function;
    
    RedisKey(String desc, Function<String, String> function) {
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
    public String getKey(String key) {
        return this.function.apply(key);
    }
	
}
