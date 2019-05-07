/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Error model for interacting with client.
 */
@ApiModel(value = "ErrorResponse", description = "错误信息响应对象")
public class ErrorResponse {
	
	// HTTP Response Status Code
	@ApiModelProperty(name = "status", dataType = "org.springframework.http.HttpStatus", value = "HTTP响应状态码")
    private final HttpStatus status;

    // General Error message
	@ApiModelProperty(name = "message", dataType = "String", value = "异常原因")
    private final String message;

    // Error code
	@ApiModelProperty(name = "status", dataType = "String", value = "异常编码")
    private final String code;
    
	@ApiModelProperty(name = "timestamp", dataType = "java.util.Date", value = "异常发生时间")
    private final Date timestamp;

    protected ErrorResponse(final String message, final String code, HttpStatus status) {
        this.message = message;
        this.code = code;
        this.status = status;
        this.timestamp = new java.util.Date();
    }
    
    public static ErrorResponse empty(final String message) {
		return new ErrorResponse(message, ErrorCode.EMPTY.getCode(), HttpStatus.OK);
	}
    
    public static ErrorResponse success(final String message) {
        return new ErrorResponse(message, ErrorCode.SUCCESS.getCode(), HttpStatus.OK);
    }
    
    public static ErrorResponse fail(final String message) {
        return new ErrorResponse(message, ErrorCode.FAIL.getCode(), HttpStatus.OK);
    }
    
    public static ErrorResponse fail(final String message, final int code) {
        return new ErrorResponse(message, String.valueOf(code), HttpStatus.OK);
    }
    
    public static ErrorResponse fail(final String message, final String code) {
        return new ErrorResponse(message, code, HttpStatus.OK);
    }
    
    public static ErrorResponse fail(final String message, HttpStatus status) {
        return new ErrorResponse(message, ErrorCode.FAIL.getCode(), status);
    }
    
    public static ErrorResponse error(final String message) {
        return new ErrorResponse(message, ErrorCode.ERROR.getCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    public static ErrorResponse error(final String message, final int code) {
        return new ErrorResponse(message, String.valueOf(code), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    public static ErrorResponse error(final String message, final String code) {
        return new ErrorResponse(message, code, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    public static ErrorResponse error(final String message, HttpStatus status) {
        return new ErrorResponse(message, ErrorCode.ERROR.getCode(), status);
    }
    
    public static ErrorResponse of(final String message, final int code, HttpStatus status) {
        return new ErrorResponse(message, String.valueOf(code), status);
    }
    
    public static ErrorResponse of(final String message, final String code, HttpStatus status) {
        return new ErrorResponse(message, code, status);
    }

    public Integer getStatus() {
        return status.value();
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
