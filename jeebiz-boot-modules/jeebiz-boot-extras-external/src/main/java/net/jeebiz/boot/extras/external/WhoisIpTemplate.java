/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package net.jeebiz.boot.extras.external;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionAddress;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionEnum;
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

/**
 * IP地址解析
 * http://whois.pconline.com.cn/
 */
@Component
@Slf4j
public class WhoisIpTemplate {

	protected static String NOT_MATCH = "未分配或者内网IP|0|0|0|0";

	@Autowired
	private OkHttpClient okhttp3Client;

	private final LoadingCache<String, Optional<JSONObject>> REGION_DATA_CACHES = CacheBuilder.newBuilder()
			// 设置并发级别为8，并发级别是指可以同时写缓存的线程数
			.concurrencyLevel(8)
			// 设置写缓存后2个小时过期
			.expireAfterWrite(2, TimeUnit.HOURS)
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
					System.out.println(notification.getKey() + " was removed, cause is " + notification.getCause());
				}
			})
			// build方法中可以指定CacheLoader，在缓存不存在时通过CacheLoader的实现自动加载缓存
			.build(new CacheLoader<String, Optional<JSONObject>>() {

				@Override
				public Optional<JSONObject> load(String ip) throws Exception {
					HttpUrl httpUrl = HttpUrl.parse(GET_COUNTRY_BY_IP_URL).newBuilder()
							.addQueryParameter("json", Boolean.TRUE.toString())
							.addQueryParameter("ip", ip).build();
					Request request = new Request.Builder().url(httpUrl).build();
					Response response = okhttp3Client.newCall(request).execute();
					if (response.isSuccessful()) {
						String bodyString = response.body().string();
						log.info(" IP : {} >> Region : {} ", ip, bodyString);
						JSONObject jsonObject = JSONObject.parseObject(bodyString);
						String addr = jsonObject.getString("addr");
						if (StringUtils.hasText(addr)) {
							return Optional.ofNullable(jsonObject);
						}
					}
					return Optional.empty();
				}
			});

	private static final String GET_COUNTRY_BY_IP_URL = "https://whois.pconline.com.cn/ipJson.jsp";
	// 810000 香港， 820000 澳门 ，710000 台湾， 999999国外
	private static final String[] SPECIAL_PROVINCE = new String[] { "810000", "820000", "710000", "999999" };
	private static final String CHINA = "中国";
	private static final String[] SPECIAL_REGION = new String[] { "香港", "澳门", "台湾" };
	private static Map<String, RegionEnum> SPECIAL_REGION_MAP;
	private static Set<String> SPECIAL_PROVINCE_SET;

	static {
		SPECIAL_REGION_MAP = new HashMap<>();
		SPECIAL_REGION_MAP.put(SPECIAL_REGION[0], RegionEnum.HK);
		SPECIAL_REGION_MAP.put(SPECIAL_REGION[1], RegionEnum.MO);
		SPECIAL_REGION_MAP.put(SPECIAL_REGION[2], RegionEnum.TW);
		SPECIAL_PROVINCE_SET = Arrays.stream(SPECIAL_PROVINCE).collect(Collectors.toSet());
	}


	/**
	 * IP地址解析
	 * @param ip
	 * @return {"ip":"110.137.48.237","pro":"","proCode":"999999","city":"","cityCode":"0","region":"","regionCode":"0","addr":" 印度尼西亚","regionNames":"","err":"noprovince"}
	 * @throws ExecutionException
	 */
	public JSONObject getRegionData(String ip) {
		try {
			Optional<JSONObject> optional = REGION_DATA_CACHES.get(ip);
			return optional.isPresent() ? optional.get() : null;
		} catch (ExecutionException e) {
			log.error("IP Region Query Error：{}", e.getMessage());
			return null;
		}
	}

	public RegionAddress getRegionAddress(String ip) throws IOException {
		try {
			Optional<JSONObject> optional = REGION_DATA_CACHES.get(ip);
			if(optional.isPresent()) {

				JSONObject regionData = optional.get();

				log.info(" IP : {} >> Region : {} ", ip, regionData.toJSONString());

				String province = regionData.getString("pro");
				String city = regionData.getString("city");
				String addr = StringUtils.trimWhitespace(regionData.getString("addr"));
				String country = addr;

				if(Stream.of(SPECIAL_REGION).anyMatch(region -> addr.contains(region))) {
					country = CHINA;
				}

				log.info(" IP : {} >> Country/Region : {} ", ip, country);

				return new RegionAddress(country, province, city, "", "");
			}
			return new RegionAddress(NOT_MATCH.split("\\|"));
		} catch (Exception e) {
			log.error("IP Region Parser Error：{}", e.getMessage());
			return new RegionAddress(NOT_MATCH.split("\\|"));
		}
	}

	public RegionEnum getRegionByIp(String ip) {
		try {

			Optional<JSONObject> optional = REGION_DATA_CACHES.get(ip);
			if(optional.isPresent()) {

				JSONObject regionData = optional.get();
				log.info(" IP : {} >> Region : {} ", ip, regionData.toJSONString());

				String addr = StringUtils.trimWhitespace(regionData.getString("addr"));
				String country = addr;

				Optional<String> regionOptional = Stream.of(SPECIAL_REGION).filter(region -> addr.contains(region)).findFirst();
				if(regionOptional.isPresent()) {
					log.info(" IP : {} >> Country/Region : {} ", ip, country);
					return SPECIAL_REGION_MAP.get(regionOptional.get());
				}

				log.info(" IP : {} >> Country/Region : {} ", ip, country);

				return RegionEnum.getByCnName(country);
			}
			return RegionEnum.UK;
		} catch (Exception e) {
			log.error("IP Region Parser Error：{}", e.getMessage());
			return RegionEnum.UK;
		}
	}

	public boolean isMainlandIp(String ip) {
		try {
			Optional<JSONObject> optional = REGION_DATA_CACHES.get(ip);
			if(optional.isPresent()) {

				JSONObject regionData = optional.get();
				log.info(" IP : {} >> Region : {} ", ip, regionData.toJSONString());

				String proCode = regionData.getString("proCode");
				if (!StringUtils.hasText(proCode) || SPECIAL_PROVINCE_SET.contains(proCode)) {
					return false;
				}

			}


			HttpUrl httpUrl = HttpUrl.parse(GET_COUNTRY_BY_IP_URL).newBuilder()
					.addQueryParameter("json", Boolean.TRUE.toString())
					.addQueryParameter("ip", ip).build();
			Request request = new Request.Builder().url(httpUrl).build();
			Response response = okhttp3Client.newCall(request).execute();
			if (response.isSuccessful()) {
				log.info("ip{}  对应地区  {}", ip, response);
				JSONObject jsonObject = JSONObject.parseObject(response.body().string());

			}

		} catch (Exception e) {
			log.error("IP Region Parser Error：{}", e.getMessage());
		}
		return true;
	}

}
