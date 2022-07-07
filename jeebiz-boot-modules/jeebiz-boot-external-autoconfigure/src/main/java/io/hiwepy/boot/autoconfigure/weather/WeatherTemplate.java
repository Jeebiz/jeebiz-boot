/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure.weather;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
public class WeatherTemplate {

	//请求连接地址
	private final static String SOJSON_WEATHER_URL = "http://t.weather.sojson.com/api/weather/city";

	private OkHttpClient okhttp3Client;

	public WeatherTemplate(OkHttpClient okhttp3Client) {
		this.okhttp3Client = okhttp3Client;
	}

	private final LoadingCache<String, Optional<JSONObject>> WEATHER_DATA_CACHES = CacheBuilder.newBuilder()
			// 设置并发级别为8，并发级别是指可以同时写缓存的线程数
			.concurrencyLevel(8)
			// 设置写缓存后1个小时过期
			.expireAfterWrite(1, TimeUnit.HOURS)
			// 设置缓存容器的初始容量为10
			.initialCapacity(10)
			// 设置缓存最大容量为100，超过100之后就会按照LRU最近虽少使用算法来移除缓存项
			.maximumSize(100)
			// 设置要统计缓存的命中率
			.recordStats()
			// 设置缓存的移除通知
			.removalListener(new RemovalListener<String, Optional<JSONObject>>() {
				@Override
				public void onRemoval(RemovalNotification<String, Optional<JSONObject>> notification) {
					log.info("{} was removed, cause is {}", notification.getKey(), notification.getCause());
				}
			})
			// build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
			.build(new CacheLoader<String, Optional<JSONObject>>() {

				@Override
				public Optional<JSONObject> load(String city_code) throws Exception {

					HttpUrl httpUrl = HttpUrl.parse(SOJSON_WEATHER_URL).newBuilder()
							.addPathSegment(city_code)
							.build();
					Request request = new Request.Builder().url(httpUrl).build();
					Response response = okhttp3Client.newCall(request).execute();
					if (response.isSuccessful()) {
						String bodyString = response.body().string();
						log.info("city_code {} >> weather :  {}", city_code, bodyString);
						JSONObject jsonObject = JSONObject.parseObject(bodyString);
						return Optional.ofNullable(jsonObject);
					}
					log.error("Weather Query Error. Response Code >> {}, Body >> {}", response.code(), response.body().string());
					return Optional.empty();
				}
			});

	public JSONObject getWeather(String city_code) throws ExecutionException {
		Optional<JSONObject> opt = WEATHER_DATA_CACHES.get(city_code);
		return opt.isPresent() ? opt.get() : null;
	}

}
