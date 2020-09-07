package net.jeebiz.boot.api.exception;

import net.jeebiz.boot.api.ApiCode;

public class PayException extends BizRuntimeException {

	public PayException(ApiCode code, String i18n) {
		super(code, i18n);
	}
	
	public PayException(int code, String msg, Throwable cause) {
		super(code, msg, cause);
	}

}
