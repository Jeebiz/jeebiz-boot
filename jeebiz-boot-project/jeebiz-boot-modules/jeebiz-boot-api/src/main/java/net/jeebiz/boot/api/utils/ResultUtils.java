/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class ResultUtils {

	/**
	 * 包装处理结果状态，并返回JSON值
	 * @param status ： 状态值或状态码
	 * @param message ： 提示信息
	 * @return ： JSON格式的字符串
	 */
	public static String token(String status, String token) {
		return JSONObject.toJSONString(tokenMap(status, token));
	}

	/**
	 * 
	 * 包装处理结果状态，并返回Map对象
	 * @param status ： 状态值或状态码
	 * @param message ： 提示信息
	 * @return ： Map对象
	 */
	public static Map<String, Object> tokenMap(String status, String token) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("status", status);
		rtMap.put("token", token);
		return rtMap;
	}

	/**
	 * 
	 * 包装处理结果状态，并返回JSON值
	 * @param status ： 状态值或状态码
	 * @param message ： 提示信息
	 * @return ： JSON格式的字符串
	 */
	public static String status(String status, String message) {
		return JSONObject.toJSONString(statusMap(status, message));
	}

	/**
	 * 
	 * 包装处理结果状态，并返回Map对象
	 * @param status  ： 状态值或状态码
	 * @param message ： 提示信息
	 * @return ： Map对象
	 */
	public static Map<String, Object> statusMap(String status, String message) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("status", status);
		rtMap.put("message", message);
		return rtMap;
	}
	
	/**
	 * 包装处理结果状态，并返回Map对象
	 * @param status  ： 状态值或状态码
	 * @param data	      ： 数据对象
	 * @return ： Map对象
	 */
	public static Map<String, Object> dataMap(String status,  Object data) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("status", status);
		rtMap.put("data", data);
		return rtMap;
	}
	
	/**
	 * 包装处理结果状态，并返回Map对象
	 * @param status  ： 状态值或状态码
	 * @param message ： 提示信息
	 * @param data	      ： 数据对象
	 * @return ： Map对象
	 */
	public static Map<String, Object> dataMap(String status, String message, Object data) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("status", status);
		rtMap.put("message", message);
		rtMap.put("data", data);
		return rtMap;
	}

}
