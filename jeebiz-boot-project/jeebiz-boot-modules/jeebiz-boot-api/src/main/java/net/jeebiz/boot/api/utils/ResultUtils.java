/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

public class ResultUtils {

	public static final String STATUS_SUCCESS = "success";
	public static final String STATUS_FAIL = "fail";
	public static final String STATUS_ERROR = "error";
	
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
