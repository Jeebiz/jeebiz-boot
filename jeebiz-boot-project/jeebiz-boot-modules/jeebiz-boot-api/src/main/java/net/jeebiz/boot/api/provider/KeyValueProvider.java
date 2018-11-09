package net.jeebiz.boot.api.provider;

public interface KeyValueProvider<T> {

	public T get(String key);
	
	public boolean set(String key,String value);
	
}
