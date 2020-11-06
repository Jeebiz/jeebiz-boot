/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.annotation;

import java.util.NoSuchElementException;

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
	
	public boolean equals(IdempotentType type) {
		return this.compareTo(type) == 0;
	}

	public boolean equals(String type) {
		return this.compareTo(IdempotentType.valueOfIgnoreCase(type)) == 0;
	}

	public static IdempotentType valueOfIgnoreCase(String type) {
		
		for (IdempotentType typeEnum : IdempotentType.values()) {
			if (typeEnum.name().equals(type)) {
				return typeEnum;
			}
		}
		throw new NoSuchElementException("Cannot found AliIdempotentType with type '" + type + "'.");
	}
	
}
