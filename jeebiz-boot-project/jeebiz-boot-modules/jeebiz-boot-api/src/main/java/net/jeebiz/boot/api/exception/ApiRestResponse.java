/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.exception;

import java.util.ArrayList;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * model for interacting with client.
 */
@ApiModel(value = "ApiRestResponse", description = "接口响应对象")
public class ApiRestResponse {

	@ApiModelProperty(name = "code", dataType = "String", value = "编码")
    private final String code;
	
	@ApiModelProperty(name = "msg", dataType = "String", value = "消息")
    private final String msg;
    
	@ApiModelProperty(name = "data", dataType = "java.lang.Object", value = "数据")
    private final Object data;

    protected ApiRestResponse(final String code, final String msg) {
        this.code = code;
        this.msg = msg;
        this.data = new ArrayList<>();
    }
    
    protected ApiRestResponse(final String code, final String msg, final Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

	public static ApiRestResponse empty(final String msg) {
		return of(ApiCode.SC_EMPTY.getCode(), msg);
	}
    
    public static ApiRestResponse success(final String msg) {
        return of(ApiCode.SC_SUCCESS.getCode(), msg);
    }
    
    public static ApiRestResponse fail(final String msg) {
        return of(ApiCode.SC_FAIL.getCode(), msg);
    }
    
    public static ApiRestResponse error(final String msg) {
        return of(ApiCode.SC_INTERNAL_SERVER_ERROR.getCode(), msg);
    }
    
    public static ApiRestResponse of(final ApiCode code) {
        return of(code.getCode(), code.getReason());
    }
    
    public static ApiRestResponse of(final ApiCode code, final Object data) {
        return of(code.getCode(), code.getReason(), data);
    }
    
    public static ApiRestResponse of(final int code, final String msg) {
        return of(String.valueOf(code), msg);
    }
    
    public static ApiRestResponse of(final String code, final String msg) {
        return new ApiRestResponse(code, msg);
    }
    
    public static ApiRestResponse of(final String code, final String msg, final Object data) {
        return new ApiRestResponse(code, msg, data);
    }

	public String getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public Object getData() {
		return data;
	}
    
}
