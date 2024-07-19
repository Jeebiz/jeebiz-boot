/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure.weather;

import com.alibaba.fastjson2.JSONObject;
import com.github.benmanes.caffeine.cache.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 免费天气查询
 * https://www.sojson.com/api/weather.html
 */
@Slf4j
public class WeatherTemplate {

    //请求连接地址
    private final static String SOJSON_WEATHER_URL = "http://t.weather.sojson.com/api/weather/city";

    private OkHttpClient okhttp3Client;

    public WeatherTemplate(OkHttpClient okhttp3Client) {
        this.okhttp3Client = okhttp3Client;
    }

    private final LoadingCache<String, Optional<JSONObject>> WEATHER_DATA_CACHES = Caffeine.newBuilder()
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
                public void onRemoval(@Nullable String key, @Nullable Optional<JSONObject> value, @NonNull RemovalCause cause) {
                    log.info("{} was removed, cause is {}", key, cause);
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
