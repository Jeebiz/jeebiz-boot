package net.jeebiz.boot.api.provider;

import java.util.Properties;

public interface PropertiesProvider {

	public Properties props();
	
	public boolean set(String key,String value);
	
	public void setProps(Properties props);
	
}
