/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.api.provider;

import java.util.Properties;

public interface PropertiesProvider {

	public Properties props();
	
	public boolean set(String key,String value);
	
	public void setProps(Properties props);
	
}
