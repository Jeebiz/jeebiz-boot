/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.exception;

import org.springframework.core.NestedCheckedException;

import net.jeebiz.boot.api.ApiCode;

@SuppressWarnings("serial")
public class BizCheckedException extends NestedCheckedException {

	/**
	 * 错误码
	 */
	private int code;
	/**
	 * 国际化Key
	 */
	private String i18n;

	public BizCheckedException(int code) {
		super("");
		this.code = code;
	}
	
	public BizCheckedException(String msg) {
		super(msg);
		this.code = Integer.parseInt(ApiCode.SC_RUNTIME_EXCEPTION.getCode());
	}

	public BizCheckedException(int code, String msg) {
		super(msg);
		this.code = code;
	}
	
	public BizCheckedException(int code, String i18n, String defMsg) {
		super(defMsg);
		this.code = code;
		this.i18n = i18n;
	}

	public BizCheckedException(int code, String msg, Throwable cause) {
		super(msg, cause);
		this.code = code;
	}
	
	public BizCheckedException(int code, String i18n, String defMsg, Throwable cause) {
		super(defMsg, cause);
		this.code = code;
		this.i18n = i18n;
	}

	public int getCode() {
		return code;
	}
	
	public String getI18n() {
		return i18n;
	}

}
