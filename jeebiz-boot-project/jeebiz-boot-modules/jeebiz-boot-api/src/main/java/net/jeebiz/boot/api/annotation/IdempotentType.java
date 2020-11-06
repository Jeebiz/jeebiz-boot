/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.annotation;

/**
 * 幂等方式
 */
public enum IdempotentType {

	/**
	 * 通过请求参数中的token值实现幂等
	 */
	TOKEN,
	/**
	 * 通过参数值
	 */
	ARGS;
	
}
