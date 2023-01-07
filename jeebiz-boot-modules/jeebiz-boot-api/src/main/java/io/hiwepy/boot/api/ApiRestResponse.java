/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * model for interacting with client.
 */
@ApiModel(value = "ApiRestResponse", description = "接口响应对象")
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiRestResponse<T extends Object> {

	@ApiModelProperty(name = "code", dataType = "String", value = "成功或异常编码")
    private final int code;

	@ApiModelProperty(name = "status", dataType = "String", value = "旧接口成功、失败或异常辅助判断标记:success、fail、error", allowableValues = "success,fail,error")
    private final String status;

	@ApiModelProperty(name = "message", dataType = "String", value = "成功或异常消息")
    private final String message;

	@ApiModelProperty(name = "data", dataType = "java.lang.Object", value = "成功或异常数据")
    private T data;

	@ApiModelProperty(name = "error", dataType = "java.util.List<Map<String, String>>", value = "校验失败信息")
    private List<Map<String,String>> error;

	public ApiRestResponse() {
		this.code = ApiCode.SC_SUCCESS.getCode();
		this.status = Constants.RT_SUCCESS;
		this.message = ApiCode.SC_SUCCESS.getReason();
    }

	protected ApiRestResponse(final ApiCode code) {
		this.code = code.getCode();;
        this.status = code.getStatus();
        this.message = code.getReason();
    }

    protected ApiRestResponse(final ApiCode code, final T data) {
        this.code = code.getCode();;
        this.status = code.getStatus();
        this.message = code.getReason();
        this.data = data;
    }

    protected ApiRestResponse(final CustomApiCode code) {
		this.code = code.getCode();;
        this.status = code.getStatus();
        this.message = code.getReason();
    }

    protected ApiRestResponse(final CustomApiCode code, final T data) {
        this.code = code.getCode();;
        this.status = code.getStatus();
        this.message = code.getReason();
        this.data = data;
    }

    protected ApiRestResponse(final ApiCode code, final String message, final T data) {
        this.code = code.getCode();;
        this.status = code.getStatus();
        this.message = message;
        this.data = data;
    }

    protected ApiRestResponse(final ApiCode code, final String message, final T data, List<Map<String,String>> error) {
        this.code = code.getCode();;
        this.status = code.getStatus();
        this.message = message;
        this.data = data;
        this.error = error;
    }

    protected ApiRestResponse(final int code, final String message) {
        this(code, Constants.RT_SUCCESS, message);
    }

    protected ApiRestResponse(final int code, final String status, final String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    protected ApiRestResponse(final int code, final String message, final T data) {
        this(code, Constants.RT_SUCCESS, message, data);
    }

    protected ApiRestResponse(final int code, final String status, final String message, final T data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
    }

	// success -----------------------------------------------------------------

    public static <T> ApiRestResponse<T> success(final String message) {
    	return of(ApiCode.SC_SUCCESS, message, null);
    }

    public static <T> ApiRestResponse<T> success(final T data) {
    	return of(ApiCode.SC_SUCCESS, data);
    }

    public static <T> ApiRestResponse<T> success(final ApiCode code,final T data) {
    	return of(code, data);
    }

    public static <T> ApiRestResponse<T> success(final int code, final String message) {
    	return of(code, Constants.RT_SUCCESS, message);
    }

    public static <T> ApiRestResponse<T> success(final ApiCode code, final String message) {
    	 return of(code, message, null);
    }

    // fail -----------------------------------------------------------------

    public static <T> ApiRestResponse<T> fail(final String message) {
    	return of(ApiCode.SC_FAIL, message, null);
    }

    public static <T> ApiRestResponse<T> fail(final T data) {
    	return of(ApiCode.SC_FAIL, data);
    }

    public static <T> ApiRestResponse<T> fail(final ApiCode code, final T data) {
    	return of(code, data);
    }

    public static <T> ApiRestResponse<T> fail(final int code, final String message) {
    	return of(code, Constants.RT_FAIL, message);
    }

    public static <T> ApiRestResponse<T> fail(final ApiCode code, final String message) {
    	 return of(code, message, null);
    }

    // error -----------------------------------------------------------------

    public static <T> ApiRestResponse<T> error(final String message) {
    	return of(ApiCode.SC_INTERNAL_SERVER_ERROR, message, null);
    }

    public static <T> ApiRestResponse<T> error(final T data) {
        return of(ApiCode.SC_INTERNAL_SERVER_ERROR, data);
    }

    public static <T> ApiRestResponse<T> error(final ApiCode code, final T data) {
        return of(code, data);
    }

    public static <T> ApiRestResponse<T> error(final int code, final String message) {
        return of(code, Constants.RT_ERROR, message);
    }

    public static <T> ApiRestResponse<T> error(final ApiCode code, final String message) {
        return of(code, message, null);
    }

    public static <T> ApiRestResponse<T> error(final ApiCode code, final String message, List<Map<String,String>> error) {
        return of(code, message, null, error);
    }

    // -----------------------------------------------------------------

    public static <T> ApiRestResponse<T> of(final ApiCode code) {
    	return new ApiRestResponse<T>(code);
    }

    public static <T> ApiRestResponse<T> of(final ApiCode code, final T data) {
    	return new ApiRestResponse<T>(code, data);
    }

    public static <T> ApiRestResponse<T> of(final ApiCode code, final String message, final T data) {
    	return new ApiRestResponse<T>(code, message, data);
    }

    public static <T> ApiRestResponse<T> of(final ApiCode code, final String message, final T data, List<Map<String,String>> error) {
    	return new ApiRestResponse<T>(code, message, data, error);
    }

    public static <T> ApiRestResponse<T> of(final String code, final String message) {
        return new ApiRestResponse<T>(Integer.parseInt(code), message);
    }

    public static <T> ApiRestResponse<T> of(final int code, final String message) {
        return new ApiRestResponse<T>(code, message);
    }

    public static <T> ApiRestResponse<T> of(final String code, final String status, final String message) {
   	 	return of(Integer.parseInt(code), status, message, null);
    }

    public static <T> ApiRestResponse<T> of(final int code, final String status, final String message) {
    	 return of(code, status, message, null);
    }

    public static <T> ApiRestResponse<T> of(final int code, final String status, final String message, final T data) {
        return new ApiRestResponse<T>(code, status, message, data);
    }

    public static <T> ApiRestResponse<T> of(final CustomApiCode code) {
    	return new ApiRestResponse<T>(code);
    }

    public static <T> ApiRestResponse<T> of(final CustomApiCode code, final T data) {
    	return new ApiRestResponse<T>(code, data);
    }


	public int getCode() {
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

	@JsonIgnore
	public List<Map<String, String>> getError() {
		return error;
	}

	@JsonIgnore
	public boolean isSuccess() {
		return status == Constants.RT_SUCCESS || code == ApiCodeValue.SC_SUCCESS;
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
