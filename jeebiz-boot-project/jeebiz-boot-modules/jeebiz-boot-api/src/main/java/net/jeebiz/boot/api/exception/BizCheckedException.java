/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.exception;

import org.springframework.core.NestedCheckedException;

@SuppressWarnings("serial")
public class BizCheckedException extends NestedCheckedException {

	public BizCheckedException(String msg) {
		super(msg);
	}
	
	public BizCheckedException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
