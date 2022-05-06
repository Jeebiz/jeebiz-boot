/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.api.provider;

public interface KeyValueProvider<T> {

	public T get(String key);
	
}
