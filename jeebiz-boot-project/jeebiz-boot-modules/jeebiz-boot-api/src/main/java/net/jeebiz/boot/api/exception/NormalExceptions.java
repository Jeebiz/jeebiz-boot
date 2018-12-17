/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.exception;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import net.jeebiz.boot.api.dao.entities.ErrorPair;

public enum NormalExceptions {
    
	/**
	 * RuntimeException					       500 (Internal Server Error)
	 */
	SC_RUNTIME_EXCEPTION( HttpStatus.INTERNAL_SERVER_ERROR, "服务器运行时异常" ),
	/**
	 * NullPointerException				       500 (Internal Server Error)
	 */
	SC_NULL_POINTER_EXCEPTION( HttpStatus.INTERNAL_SERVER_ERROR, "服务器空值异常"),
	/**
	 * ClassCastException				       500 (Internal Server Error)
	 */
	SC_CLASS_CAST_EXCEPTION( HttpStatus.INTERNAL_SERVER_ERROR, "服务器数据类型转换异常"),
	/**
	 * IOException						       500 (Internal Server Error)
	 */
	SC_IO_EXCEPTION( HttpStatus.INTERNAL_SERVER_ERROR, "服务器IO异常"),
	/**
	 * NoSuchMethodException			       500 (Internal Server Error)
	 */
	SC_NO_SUCH_METHOD_EXCEPTION( HttpStatus.INTERNAL_SERVER_ERROR,  "服务器未知方法异常"),
	/**
	 * IndexOutOfBoundsException		       500 (Internal Server Error)
	 */
	SC_INDEX_OUT_OF_BOUNDS_EXCEPTION( HttpStatus.INTERNAL_SERVER_ERROR,  "服务器数组越界异常"),
	/**
	 * NetworkException					       500 (Internal Server Error)
	 */
	SC_NETWORK_EXCEPTION( HttpStatus.INTERNAL_SERVER_ERROR, "服务器网络异常");
	
	private final String code;

	private final String reasonPhrase;
	
	private final HttpStatus httpStatus;

	public static final String STATUS_ERROR = "error";

	NormalExceptions(HttpStatus httpStatus, String reasonPhrase) {
		this.httpStatus = httpStatus;
		this.code = httpStatus.toString();
		this.reasonPhrase = reasonPhrase;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * Return the reason phrase of this status code.
	 */
	public String getReasonPhrase() {
		return this.reasonPhrase;
	}
	
	/**
	 * Return the http Status of this status code.
	 */
	public HttpStatus getHttpStatus() {
		return this.httpStatus;
	}
	
	/**
	 * Return a string representation of this status code.
	 */
	@Override
	public String toString() {
		return this.code;
	}
	
	/**
	 * Return the enum constant of this type with the specified numeric value.
	 * @param exCode the numeric value of the enum to be returned
	 * @return the enum constant with the specified numeric value
	 * @throws IllegalArgumentException if this enum has no constant for the specified numeric value
	 */
	public static NormalExceptions valueOfIgnoreCase(String exCode) {
		for (NormalExceptions status : values()) {
			if (status.code.equalsIgnoreCase(exCode)) {
				return status;
			}
		}
		throw new IllegalArgumentException("No matching constant for [" + exCode + "]");
	}
	
	public static List<ErrorPair> errors() {
		List<ErrorPair> errorList = new LinkedList<ErrorPair>();
		for (NormalExceptions ex : NormalExceptions.values()) {
			errorList.add(new ErrorPair(ex.getCode(), ex.getReasonPhrase()));
		}
		return errorList;
	}
	
	public <T extends Throwable>  ResponseEntity<Map<String, Object>> toResponseEntity(T ex) {
		return this.toResponseEntity(ex, null);
	}
	
	public <T extends Throwable>  ResponseEntity<Map<String, Object>> toResponseEntity(T ex, Map<String, Object> detail) {
		
		Map<String, Object> rtMap = new HashMap<String, Object>();
		
		rtMap.put("code", getCode());
		rtMap.put("status", STATUS_ERROR);
		rtMap.put("reason", getReasonPhrase());
		if(detail != null && !detail.isEmpty()) {
			rtMap.put("detail", detail);
		}
		
		return new ResponseEntity<Map<String, Object>>(rtMap, this.getHttpStatus());
	}

}
