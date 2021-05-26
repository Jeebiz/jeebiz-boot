package net.jeebiz.boot.extras.redis.setup;

import java.io.UnsupportedEncodingException;

public class RedisKeyGenerator {

	private static String DELIMITER = ":";

	public static String getDELIMITER() {
		return DELIMITER;
	}
	
	public static String getUserLoginNum(String userId) {
		return getKeyStr(RedisConstant.USER_LOGIN_NUM, userId);
	}
	
	public static String getUserToken(String userId) {
		return getKeyStr(RedisConstant.USER_TOKEN, userId);
	}

	public static String getUserSso() {
		return getKeyStr(RedisConstant.USER_SSO_STATE);
	}
	
	public static String getUserInfoPrefix(String userId) {
		return getKeyStr(RedisConstant.USER_INFO_PREFIX, userId);
	}
	
	public static String getUserQueryPrefix(String userId, String timestamp ) {
		return getKeyStr(RedisConstant.USER_QUERY_PREFIX, userId, timestamp);
	}
	
	public static String getUserGeoLocation() {
		return getKeyStr(RedisConstant.USER_GEO_LOCATION);
	}
	
	public static String getUserSessionState() {
		return getKeyStr(RedisConstant.USER_SESSION_STATE);
	}
	
	public static String getUserGeoLocation(String userId) {
		return getKeyStr(RedisConstant.USER_GEO_LOCATION, userId);
	}

	public static String getUserInfoLock(String userId) {
		return getKeyStr(RedisConstant.USER_INFO_LOCK, userId);
	}
	
	public static String getUserInfoPreviewList(String userId) {
		return getKeyStr(RedisConstant.USER_INFO_PREVIEW, userId);
	}
	
	public static String getUserInfoPreviewList(String userId, String day) {
		return getKeyStr(RedisConstant.USER_INFO_PREVIEW, userId, day);
	}
	
	public static String getUserTaskList(String userId, String day) {
		return getKeyStr(RedisConstant.USER_TASK_LIST, userId, day);
	}
	
	public static String getUserCoinAmount(String userId) {
		return getKeyStr(RedisConstant.USER_COIN_AMOUNT, userId);
	}

	public static String getUserPearlAmount(String userId) {
		return getKeyStr(RedisConstant.USER_PEARL_AMOUNT, userId);
	}

	public static String getUserExpAmount(String userId) {
		return getKeyStr(RedisConstant.USER_EXP_AMOUNT, userId);
	}
	
	public static String getUserUnlock(String day) {
		return getKeyStr(RedisConstant.USER_INFO_UNLOCK, day);
	}
	
	public static String getUserUnlock(String day, String userId) {
		return getKeyStr(RedisConstant.USER_INFO_UNLOCK, day, userId);
	}
	
	public static String getVipInterest(String userId) {
		return getKeyStr(RedisConstant.USER_VIP_INTEREST, userId);
	}
 
	public static String getVipInterest(String month, String userId) {
		return getKeyStr(RedisConstant.USER_VIP_INTEREST, month, userId);
	}
	
	public static String getSmsBlacklist(String userId) {
		return getKeyStr(RedisConstant.SET_SMS_BLACK_LIST, userId);
	}
	
	public static String getSmsMobileTime(String day, Integer type, String mobile) {
		return getKeyStr(RedisConstant.STR_SMS_MOBILE_TIME, day, String.valueOf(type), mobile);
	}
	
	public static String getSmsDevTime(String day, String userId) {
		return getKeyStr(RedisConstant.STR_SMS_DEV_TIME, userId);
	}
	
	public static String getSmsLock(String mobile) {
		return RedisConstant.STR_SMS_LOCK_MOBILE.concat(mobile);
	}

	public static String getSmsCode(String userId) {
		return getKeyStr(RedisConstant.STR_SMS_CODE, userId);
	}
	
	public static String getSmsCode(Integer type, String mobile) {
		return getKeyStr(RedisConstant.STR_SMS_CODE, String.valueOf(type), mobile);
	}

	public static String getCatalog(String id) {
		return getKeyStr(RedisConstant.DBMATA_CATALOG, id);
	}
	
	public static String getCatalogLoding(String id) {
		return getKeyStr(RedisConstant.DBMATA_CATALOG_LODING, id);
	}
	

	/**
	 * 获取value的字节形式值
	 * 
	 * @param args
	 * @return
	 */
	public static byte[] getKeyByteArr(String prefix, String... args) {
		byte[] bytes = null;
		try {
			bytes = getKeyStr(prefix, args).getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// log.error(e.getMessage());
		}
		return bytes;
	}

	public static String getKeyStr(String prefix, String... args) {
		StringBuilder tempKey = new StringBuilder(prefix);
		for (String s : args) {
			if (s.length() <= 0) {
				continue;
			}
			tempKey.append(DELIMITER).append(s);
		}
		return tempKey.toString();
	}
	
	public static String getThreadKeyStr(String prefix, String... args) {
		StringBuilder tempKey = new StringBuilder(prefix).append(DELIMITER).append(Thread.currentThread().getId());
		for (String s : args) {
			if (s.length() <= 0) {
				continue;
			}
			tempKey.append(DELIMITER).append(s);
		}
		return tempKey.toString();
	}
 
}