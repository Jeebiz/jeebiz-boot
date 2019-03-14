/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.alibaba.fastjson.JSONObject;

public class ResultUtils {

	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_FAIL = "fail";
	public static final String STATUS_ERROR = "error";
	
	/**
	 * 包装处理结果状态，并返回JSON值
	 * @param code		： 状态值或状态码
	 * @param status	： 结果状态值（success、fail、error）
	 * @param message	： 提示信息
	 * @return 			： JSON格式的字符串
	 */
	public static String status(HttpStatus code, String status, String message) {
		return JSONObject.toJSONString(statusMap(code, status, message));
	}
	
	/**
	 * 包装处理结果状态，并返回Map对象
	 * @param code		： 状态值或状态码
	 * @param status	： 结果状态值（success、fail、error）
	 * @param message	： 提示信息
	 * @return 			： Map对象
	 */
	public static Map<String, Object> statusMap(HttpStatus code, String status, String message) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("code", code.value());
		rtMap.put("status", status);
		rtMap.put("reason", code.getReasonPhrase());
		rtMap.put("message", message);
		return rtMap;
	}
	
	/**
	 * 包装处理结果状态，并返回Map对象
	 * @param message	： 提示信息
	 * @return 			： Map对象
	 */
	public static Map<String, Object> success(String message) {
		return statusMap(HttpStatus.OK, STATUS_SUCCESS, message);
	}
	
	/**
	 * 包装处理结果状态，并返回Map对象
	 * @param code		： 状态值或状态码
	 * @param message	： 提示信息
	 * @return 			： Map对象
	 */
	public static Map<String, Object> success(HttpStatus code, String message) {
		return statusMap(code, STATUS_SUCCESS, message);
	}
	
	/**
	 * 包装处理结果状态，并返回Map对象
	 * @param message	： 提示信息
	 * @return 			： Map对象
	 */
	public static Map<String, Object> fail(String message) {
		return statusMap(HttpStatus.OK, STATUS_FAIL, message);
	}
	
	/**
	 * 包装处理结果状态，并返回Map对象
	 * @param code		： 状态值或状态码
	 * @param message	： 提示信息
	 * @return 			： Map对象
	 */
	public static Map<String, Object> fail(HttpStatus code, String message) {
		return statusMap(code, STATUS_FAIL, message);
	}
	
	/**
	 * 包装处理结果状态，并返回Map对象
	 * @param message	： 提示信息
	 * @return 			： Map对象
	 */
	public static Map<String, Object> error(String message) {
		return statusMap(HttpStatus.OK, STATUS_ERROR, message);
	}
	
	/**
	 * 包装处理结果状态，并返回Map对象
	 * @param code		： 状态值或状态码
	 * @param message	： 提示信息
	 * @return 			： Map对象
	 */
	public static Map<String, Object> error(HttpStatus code, String message) {
		return statusMap(code, STATUS_ERROR, message);
	}
	
	/**
	 * 包装处理结果状态，并返回JSON值
	 * @param status ： 状态值或状态码
	 * @param message ： 提示信息
	 * @return ： JSON格式的字符串
	 */
	public static String token(int code, String token) {
		return JSONObject.toJSONString(tokenMap(code, token));
	}

	public static Map<String, Object> tokenMap(int code, String token) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("code", code);
		rtMap.put("token", token);
		return rtMap;
	}
	
	public static String token(HttpStatus code, String token) {
		return JSONObject.toJSONString(tokenMap(code, token));
	}
	
	public static Map<String, Object> tokenMap(HttpStatus code, String token) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("code", code.value());
		rtMap.put("token", token);
		return rtMap;
	}
	
	/**
	 * 包装处理结果状态，并返回Map对象
	 * @param data	   	： 数据对象
	 * @return ： Map对象
	 */
	public static Map<String, Object> dataMap(Object data) {
		return dataMap(HttpStatus.OK.value(), data);
	}
	
	public static Map<String, Object> dataMap(HttpStatus code, Object data) {
		return dataMap(code.value(), data);
	}
	
	public static Map<String, Object> dataMap(HttpStatus code, String message, Object data) {
		return dataMap(code.value(), message, data);
	}
	
	public static Map<String, Object> dataMap(HttpStatus code, String status, String message, Object data) {
		return dataMap(code.value(), status, message, data);
	}
	
	public static Map<String, Object> dataMap(int code, Object data) {
		return dataMap(code, STATUS_SUCCESS, "", data);
	}
	
	public static Map<String, Object> dataMap(int code, String message, Object data) {
		return dataMap(code, STATUS_SUCCESS, message, data);
	}
	
	public static Map<String, Object> dataMap(int code, String status, String message, Object data) {
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("code", code);
		rtMap.put("status", status);
		rtMap.put("message", message);
		rtMap.put("data", data);
		return rtMap;
	}

}
