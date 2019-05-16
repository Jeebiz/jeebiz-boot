/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * model for interacting with client.
 */
@ApiModel(value = "ApiRestResponse", description = "接口响应对象")
public class ApiRestResponse {
	
	@ApiModelProperty(name = "code", dataType = "String", value = "成功或异常编码")
    private final String code;
	
	@ApiModelProperty(name = "status", dataType = "String", value = "旧接口成功、失败或异常辅助判断标记:success、fail、error", allowableValues = "success,fail,error")
    private final String status;
	
	@ApiModelProperty(name = "msg", dataType = "String", value = "成功或异常消息")
    private final String msg;
    
	@ApiModelProperty(name = "data", dataType = "java.lang.Object", value = "成功或异常数据")
    private final Object data;
    
    protected ApiRestResponse(final String code, final String status, final String msg) {
        this.code = code;
        this.status = status;
        this.msg = msg;
        this.data = new ArrayList<>();
    }
    
    protected ApiRestResponse(final String code, final String status, final String msg, final Object data) {
        this.code = code;
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

	public static ApiRestResponse empty(final String msg) {
		return of(ApiCode.SC_EMPTY, msg);
	}
    
    public static ApiRestResponse success(final String msg) {
        return of(ApiCode.SC_SUCCESS, msg);
    }
    
    public static ApiRestResponse success(final int code, final String msg) {
        return success(String.valueOf(code), msg);
    }
    
    public static ApiRestResponse success(final String code, final String msg) {
        return new ApiRestResponse(code, "success", msg);
    }
    
    public static ApiRestResponse success(final Object data) {
        return of(ApiCode.SC_SUCCESS, data);
    }
    
    public static ApiRestResponse fail(final String msg) {
        return of(ApiCode.SC_FAIL, msg);
    }
    
    public static ApiRestResponse fail(final int code, final String msg) {
        return fail(String.valueOf(code), msg);
    }
    
    public static ApiRestResponse fail(final String code, final String msg) {
        return new ApiRestResponse(code, "fail", msg);
    }
    
    public static ApiRestResponse error(final String msg) {
        return of(ApiCode.SC_INTERNAL_SERVER_ERROR, msg);
    }
    
    public static ApiRestResponse error(final int code, final String msg) {
        return error(String.valueOf(code), msg);
    }
    
    public static ApiRestResponse error(final String code, final String msg) {
        return new ApiRestResponse(code, "error",  msg);
    }
    
    public static ApiRestResponse of(final ApiCode code) {
        return of(code.getCode(), code.getStatus(), code.getReason());
    }
    
    public static ApiRestResponse of(final ApiCode code, final String msg) {
        return of(code.getCode(), code.getStatus(), msg);
    }
    
    public static ApiRestResponse of(final ApiCode code, final Object data) {
        return of(code.getCode(), code.getStatus(), code.getReason(), data);
    }
    
    public static ApiRestResponse of(final int code, final String status, final String msg) {
        return of(String.valueOf(code), status, msg);
    }
    
    public static ApiRestResponse of(final String code, final String status, final String msg) {
    	 return of(code, status, msg, new ArrayList<>());
    }
    
    public static ApiRestResponse of(final String code, final String status, final String msg, final Object data) {
        return new ApiRestResponse(code, status, msg, data);
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

	public Object getData() {
		return data;
	}
	
	public Map<String, Object> toMap(){
		Map<String, Object> rtMap = new HashMap<String, Object>();
		rtMap.put("code", code);
		rtMap.put("status", status);
		rtMap.put("message", msg);
		rtMap.put("data", data);
		return rtMap;
	}
    
}
