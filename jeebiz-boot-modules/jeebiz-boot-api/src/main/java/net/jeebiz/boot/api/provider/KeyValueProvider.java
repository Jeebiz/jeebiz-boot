/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.provider;

public interface KeyValueProvider<T> {

	public T get(String key);
	
}
