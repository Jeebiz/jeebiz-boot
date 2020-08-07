/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.jeebiz.boot.api.utils.Constants;

/**
 * model for interacting with client.
 */
@ApiModel(value = "ApiRestResponse", description = "接口响应对象")
public class ApiRestResponse<T> {

	@ApiModelProperty(name = "code", dataType = "String", value = "成功或异常编码")
    private final String code;
	
	@ApiModelProperty(name = "status", dataType = "String", value = "旧接口成功、失败或异常辅助判断标记:success、fail、error", allowableValues = "success,fail,error")
    private final String status;
	
	@ApiModelProperty(name = "msg", dataType = "String", value = "成功或异常消息")
    private final String msg;
    
	@ApiModelProperty(name = "data", dataType = "java.lang.Object", value = "成功或异常数据")
    private T data;

	public ApiRestResponse() {
		this.code = ApiCode.SC_SUCCESS.getCode();
		this.status = Constants.RT_SUCCESS;
		this.msg = ApiCode.SC_SUCCESS.getReason();
    }
    
	protected ApiRestResponse(final ApiCode code) {
		this.code = code.getCode();;
        this.status = code.getStatus();
        this.msg = code.getReason();
    }
	 
    protected ApiRestResponse(final ApiCode code, final T data) {
        this.code = code.getCode();;
        this.status = code.getStatus();
        this.msg = code.getReason();
        this.data = data;
    }
    
    protected ApiRestResponse(final ApiCode code, final String msg, final T data) {
        this.code = code.getCode();;
        this.status = code.getStatus();
        this.msg = msg;
        this.data = data;
    }

    protected ApiRestResponse(final String code, final String msg) {
        this(code, Constants.RT_SUCCESS, msg);
    }
    
    protected ApiRestResponse(final String code, final String status, final String msg) {
        this.code = code;
        this.status = status;
        this.msg = msg;
    }
    
    protected ApiRestResponse(final String code, final String msg, final T data) {
        this(code, Constants.RT_SUCCESS, msg, data);
    }
    
    protected ApiRestResponse(final String code, final String status, final String msg, final T data) {
        this.code = code;
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

	public static <T> ApiRestResponse<T> empty(final String msg) {
		return of(ApiCode.SC_EMPTY, msg, null);
	}
    
	// success -----------------------------------------------------------------
	
    public static <T> ApiRestResponse<T> success(final String msg) {
    	return of(ApiCode.SC_SUCCESS, msg, null);
    }
    
    public static <T> ApiRestResponse<T> success(final T data) {
    	return of(ApiCode.SC_SUCCESS, data);
    }
    
    public static <T> ApiRestResponse<T> success(final int code, final String msg) {
        return success(String.valueOf(code), msg);
    }
    
    public static <T> ApiRestResponse<T> success(final String code, final String msg) {
    	return of(code, Constants.RT_SUCCESS, msg);
    }
    
    // fail -----------------------------------------------------------------
    
    public static <T> ApiRestResponse<T> fail(final String msg) {
    	return of(ApiCode.SC_FAIL, msg, null);
    }
    
    public static <T> ApiRestResponse<T> fail(final T data) {
    	return of(ApiCode.SC_FAIL, data);
    }
    
    public static <T> ApiRestResponse<T> fail(final int code, final String msg) {
        return fail(String.valueOf(code), msg);
    }
    
    public static <T> ApiRestResponse<T> fail(final String code, final String msg) {
    	return of(code, Constants.RT_FAIL, msg);
    }
    
    // error -----------------------------------------------------------------
    
    public static <T> ApiRestResponse<T> error(final String msg) {
    	return of(ApiCode.SC_INTERNAL_SERVER_ERROR, msg, null);
    }
    
    public static <T> ApiRestResponse<T> error(final T data) {
        return of(ApiCode.SC_INTERNAL_SERVER_ERROR, data);
    }
    
    public static <T> ApiRestResponse<T> error(final int code, final String msg) {
        return error(String.valueOf(code), msg);
    }
    
    public static <T> ApiRestResponse<T> error(final String code, final String msg) {
        return of(code, Constants.RT_ERROR, msg);
    }
    
    // -----------------------------------------------------------------
    
    public static <T> ApiRestResponse<T> of(final ApiCode code) {
    	return new ApiRestResponse<T>(code);
    }
    
    public static <T> ApiRestResponse<T> of(final ApiCode code, final T data) {
    	return new ApiRestResponse<T>(code, data);
    }
    
    public static <T> ApiRestResponse<T> of(final ApiCode code, final String msg, final T data) {
    	return new ApiRestResponse<T>(code, msg, data);
    }
    
    public static <T> ApiRestResponse<T> of(final int code, final String msg) {
        return of(String.valueOf(code), msg);
    }
    
    public static <T> ApiRestResponse<T> of(final String code, final String msg) {
        return new ApiRestResponse<T>(code, msg);
    }
    
    public static <T> ApiRestResponse<T> of(final int code, final String status, final String msg) {
        return of(String.valueOf(code), status, msg, null);
    }
    
    public static <T> ApiRestResponse<T> of(final String code, final String status, final String msg) {
    	 return of(code, status, msg, null);
    }
    
    public static <T> ApiRestResponse<T> of(final String code, final String status, final String msg, final T data) {
        return new ApiRestResponse<T>(code, status, msg, data);
    }

	public String getCode() {
		return code;
	}
	
	public String getStatus() {
		return status;
	}

	public String getMsg() {
		return msg;
	}

	public T getData() {
		return data;
	}
	
	public Map<String, Object> toMap(){
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("code", code);
		rtMap.put("status", status);
		rtMap.put("msg", msg);
		rtMap.put("data", data);
		return rtMap;
	}
    
}
