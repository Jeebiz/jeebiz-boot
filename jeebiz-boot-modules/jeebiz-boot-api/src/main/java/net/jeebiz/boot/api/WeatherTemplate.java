/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class WeatherTemplate {

	//请求连接地址
	private final static String SOJSON_WEATHER_URL = "http://t.weather.sojson.com/api/weather/city/{1}";
	private final RestTemplate restTemplate;
	
	public WeatherTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private final LoadingCache<String, Optional<JSONObject>> WEATHER_DATA_CACHES = CacheBuilder.newBuilder()
			// 设置并发级别为8，并发级别是指可以同时写缓存的线程数
			.concurrencyLevel(8)
			// 设置写缓存后2个小时过期
			.expireAfterWrite(2, TimeUnit.HOURS)
			// 设置缓存容器的初始容量为10
			.initialCapacity(2)
			// 设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
			.maximumSize(10)
			// 设置要统计缓存的命中率
			.recordStats()
			// 设置缓存的移除通知
			.removalListener(new RemovalListener<String, Optional<JSONObject>>() {
				@Override
				public void onRemoval(RemovalNotification<String, Optional<JSONObject>> notification) {
					System.out.println(notification.getKey() + " was removed, cause is " + notification.getCause());
				}
			})
			// build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
			.build(new CacheLoader<String, Optional<JSONObject>>() {

				@Override
				public Optional<JSONObject> load(String city_code) throws Exception {

					ResponseEntity<String> response = restTemplate.getForEntity(SOJSON_WEATHER_URL, String.class, city_code);
					if(response.getStatusCode().is2xxSuccessful()) {
						JSONObject jsonObject = JSONObject.parseObject(response.getBody());
						jsonObject.remove("message");
						jsonObject.remove("status");
						return Optional.fromNullable(jsonObject);
					}
					return Optional.fromNullable(null);
				}
			});
	
	public JSONObject getWeather(String city_code) throws ExecutionException {
		Optional<JSONObject> opt = WEATHER_DATA_CACHES.get(city_code);
		return opt.isPresent() ? opt.get() : null;
	}
	
}
