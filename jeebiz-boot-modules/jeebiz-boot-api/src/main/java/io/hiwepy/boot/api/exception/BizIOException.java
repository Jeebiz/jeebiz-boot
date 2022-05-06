/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.exception;

import io.hiwepy.boot.api.CustomApiCode;
import org.springframework.core.NestedIOException;

import io.hiwepy.boot.api.ApiCode;

@SuppressWarnings("serial")
public class BizIOException extends NestedIOException {

	/**
	 * 错误码
	 */
	private int code;
	/**
	 * 国际化Key
	 */
	private String i18n;

	public BizIOException(int code) {
		super("");
		this.code = code;
	}

	public BizIOException(String msg) {
		super(msg);
		this.code = ApiCode.SC_INTERNAL_SERVER_ERROR.getCode();
	}

	public BizIOException(int code, String msg) {
		super(msg);
		this.code = code;
	}

	public BizIOException(ApiCode code, String i18n) {
		super(code.getReason());
		this.code = code.getCode();
		this.i18n = i18n;
	}

	public BizIOException(int code, String i18n, String defMsg) {
		super(defMsg);
		this.code = code;
		this.i18n = i18n;
	}

	public BizIOException(int code, String msg, Throwable cause) {
		super(msg, cause);
		this.code = code;
	}

	public BizIOException(int code, String i18n, String defMsg, Throwable cause) {
		super(defMsg, cause);
		this.code = code;
		this.i18n = i18n;
	}

	public BizIOException(CustomApiCode code) {
		super(code.getReason());
		this.code = code.getCode();
	}

	public int getCode() {
		return code;
	}

	public String getI18n() {
		return i18n;
	}

}
