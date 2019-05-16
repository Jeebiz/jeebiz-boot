/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.exception;

import org.springframework.core.NestedIOException;

@SuppressWarnings("serial")
public class BizIOException extends NestedIOException {

	public BizIOException(String msg) {
		super(msg);
	}

}
