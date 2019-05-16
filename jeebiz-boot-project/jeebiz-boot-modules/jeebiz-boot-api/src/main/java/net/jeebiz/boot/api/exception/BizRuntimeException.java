/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.exception;

import org.springframework.core.NestedRuntimeException;

@SuppressWarnings("serial")
public class BizRuntimeException extends NestedRuntimeException {

	public BizRuntimeException(String msg) {
		super(msg);
	}
	
	public BizRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
