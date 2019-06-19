/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * model for interacting with client.
 */
@ApiModel(value = "ApiRestResponse", description = "接口响应对象")
public class ApiRestResponse<T> {

	private static final String RT_SUCCESS = "success";
	private static final String RT_FAIL = "fail";
	private static final String RT_ERROR = "error";
	
	@ApiModelProperty(name = "code", dataType = "String", value = "成功或异常编码")
    private final String code;
	
	@ApiModelProperty(name = "status", dataType = "String", value = "旧接口成功、失败或异常辅助判断标记:success、fail、error", allowableValues = "success,fail,error")
    private final String status;
	
	@ApiModelProperty(name = "message", dataType = "String", value = "成功或异常消息")
    private final String message;
    
	@ApiModelProperty(name = "data", dataType = "java.lang.Object", value = "成功或异常数据")
    private T data;
    
    protected ApiRestResponse(final String code, final String status, final String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
    
    protected ApiRestResponse(final String code, final String status, final String message, final T data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

	public static <T> ApiRestResponse<T> empty(final String message) {
		return of(ApiCode.SC_EMPTY, message);
	}
    
    public static <T> ApiRestResponse<T> success(final String message) {
        return of(ApiCode.SC_SUCCESS, message);
    }
    
    public static <T> ApiRestResponse<T> success(final int code, final String message) {
        return success(String.valueOf(code), message);
    }
    
    public static <T> ApiRestResponse<T> success(final String code, final String message) {
        return new ApiRestResponse<T>(code, RT_SUCCESS, message);
    }
    
    public static <T> ApiRestResponse<T> success(final T data) {
        return of(ApiCode.SC_SUCCESS, data);
    }
    
    public static <T> ApiRestResponse<T> fail(final String message) {
        return of(ApiCode.SC_FAIL, message);
    }
    
    public static <T> ApiRestResponse<T> fail(final int code, final String message) {
        return fail(String.valueOf(code), message);
    }
    
    public static <T> ApiRestResponse<T> fail(final String code, final String message) {
        return new ApiRestResponse<T>(code, RT_FAIL, message);
    }
    
    public static <T> ApiRestResponse<T> error(final String message) {
        return of(ApiCode.SC_INTERNAL_SERVER_ERROR, message);
    }
    
    public static <T> ApiRestResponse<T> error(final int code, final String message) {
        return error(String.valueOf(code), message);
    }
    
    public static <T> ApiRestResponse<T> error(final String code, final String message) {
        return new ApiRestResponse<T>(code, RT_ERROR,  message);
    }

    public static <T> ApiRestResponse<T> data(final T data) {
        return of(ApiCode.SC_SUCCESS.getCode(), ApiCode.SC_SUCCESS.getStatus(), ApiCode.SC_SUCCESS.getReason(), data);
    }
    
    public static <T> ApiRestResponse<T> of(final ApiCode code) {
        return of(code.getCode(), code.getStatus(), code.getReason());
    }
    
    public static <T> ApiRestResponse<T> of(final ApiCode code, final String message) {
        return of(code.getCode(), code.getStatus(), message);
    }
    
    public static <T> ApiRestResponse<T> of(final ApiCode code, final T data) {
        return of(code.getCode(), code.getStatus(), code.getReason(), data);
    }
    
    public static <T> ApiRestResponse<T> of(final int code, final String status, final String message) {
        return of(String.valueOf(code), status, message);
    }
    
    public static <T> ApiRestResponse<T> of(final String code, final String status, final String message) {
    	 return of(code, status, message, null);
    }
    
    public static <T> ApiRestResponse<T> of(final String code, final String status, final String message, final T data) {
        return new ApiRestResponse<T>(code, status, message, data);
    }

	public String getCode() {
		return code;
	}
	
	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public T getData() {
		return data;
	}
	
	public Map<String, Object> toMap(){
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("code", code);
		rtMap.put("status", status);
		rtMap.put("message", message);
		rtMap.put("data", data);
		return rtMap;
	}
    
}
