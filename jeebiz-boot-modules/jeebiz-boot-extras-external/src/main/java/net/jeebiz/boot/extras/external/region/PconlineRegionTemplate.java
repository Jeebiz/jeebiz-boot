/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package net.jeebiz.boot.extras.external.region;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.nutz.plugins.ip2region.Util;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionAddress;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionEnum;
import com.github.hiwepy.ip2region.spring.boot.util.IpUtils;

import lombok.extern.slf4j.Slf4j;
import net.jeebiz.boot.api.utils.CalendarUtils;
import net.jeebiz.boot.extras.redis.setup.RedisKey;
import net.jeebiz.boot.extras.redis.setup.RedisOperationTemplate;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * IP地址解析
 * http://whois.pconline.com.cn/
 */
@Slf4j
public class PconlineRegionTemplate {

	private static final String GET_COUNTRY_BY_IP_URL = "https://whois.pconline.com.cn/ipJson.jsp";
	private static final String NOT_MATCH = "未分配或者内网IP|0|0|0|0";
	private static final RegionAddress NOT_MATCH_REGION_ADDRESS = new RegionAddress(NOT_MATCH.split("\\|"));
	/**
	 * 810000 香港， 820000 澳门 ，710000 台湾， 999999国外
 	 */
	private static final String[] SPECIAL_PROVINCE = new String[] { "810000", "820000", "710000", "999999" };
	private static final String CHINA = "中国";
	private static final String[] SPECIAL_REGION = new String[] { "香港", "澳门", "台湾" };
	private static Map<String, RegionEnum> SPECIAL_REGION_MAP;
	private static Set<String> SPECIAL_PROVINCE_SET;

	protected static String REGION_KEY = "region";
	protected static String COUNTRY_KEY = "country";
	protected static String PROVINCE_KEY = "province";
	protected static String CITY_KEY = "city";
	protected static String AREA_KEY = "area";
	protected static String ADDR_KEY = "addr";
	protected static String ISP_KEY = "isp";

	static {
		SPECIAL_REGION_MAP = new HashMap<>();
		SPECIAL_REGION_MAP.put(SPECIAL_REGION[0], RegionEnum.HK);
		SPECIAL_REGION_MAP.put(SPECIAL_REGION[1], RegionEnum.MO);
		SPECIAL_REGION_MAP.put(SPECIAL_REGION[2], RegionEnum.TW);
		SPECIAL_PROVINCE_SET = Arrays.stream(SPECIAL_PROVINCE).collect(Collectors.toSet());
	}

	private final OkHttpClient okhttp3Client;
	private RedisOperationTemplate redisOperation;

	public PconlineRegionTemplate(OkHttpClient okhttp3Client) {
		this.okhttp3Client = okhttp3Client;
	}

	public PconlineRegionTemplate(OkHttpClient okhttp3Client, RedisOperationTemplate redisOperation) {
		this.okhttp3Client = okhttp3Client;
		this.redisOperation = redisOperation;
	}

	/**
	 * IP地址解析：http://whois.pconline.com.cn/ipJson.jsp?json=true&ip=183.128.136.82
	 * @param ip
	 * @return {"ip":"110.137.48.237","pro":"","proCode":"999999","city":"","cityCode":"0","region":"","regionCode":"0","addr":" 印度尼西亚","regionNames":"","err":"noprovince"}
	 * @throws ExecutionException
	 */
	public Optional<JSONObject> getLocationByIp(String ip) {
		// 1、检查ip有效性
		if (Objects.isNull(ip)) {
			throw new NullPointerException("ip can not empty");
		}
		if(!IpUtils.isIpv4(ip)){
			throw new IllegalArgumentException("Invalid IPv4 address");
		}
		// 2、优先从本地缓存获取数据
		String redisKey = RedisKey.IP_LOCATION_PCONLINE_INFO.getKey(String.valueOf(Util.ip2long(ip)));
		if(Objects.nonNull(redisOperation)) {
			String redisValue = redisOperation.getString(redisKey);
			if(Objects.nonNull(redisValue)) {
				log.info(" IP : {} >> Location From Redis Cache : {} ", ip, redisValue);
				JSONObject jsonObject = JSONObject.parseObject(redisValue);
				return Optional.ofNullable(jsonObject);
			}
		}
		// 3、调用三方接口解析IP信息
		try {
			HttpUrl httpUrl = HttpUrl.parse(GET_COUNTRY_BY_IP_URL).newBuilder()
					.addQueryParameter("json", Boolean.TRUE.toString())
					.addQueryParameter("ip", ip).build();
			Request request = new Request.Builder().url(httpUrl).build();
			Response response = okhttp3Client.newCall(request).execute();
			if (response.isSuccessful()) {
				String bodyString = response.body().string();
				log.info(" IP : {} >> Location : {} ", ip, bodyString);
				JSONObject jsonObject = JSONObject.parseObject(bodyString);
				String addr = jsonObject.getString(ADDR_KEY);
				if (StringUtils.hasText(addr)) {
					if(Objects.nonNull(redisOperation)) {
						redisOperation.set(redisKey, bodyString, CalendarUtils.getSecondsNextEarlyMorning());
					}
					return Optional.ofNullable(jsonObject);
				}
			}
			log.error("IP : {} >> Location Query Error. Response Code >> {}, Body >> {}", response.code(), response.body().string());
		} catch (Exception e) {
			log.error("IP : {} >> Location Query Error：{}", e.getMessage());
		}
		return Optional.empty();
	}

	public RegionAddress getRegionAddress(String ip) {
		try {
			Optional<JSONObject> optional = this.getLocationByIp(ip);
			if(optional.isPresent()) {

				JSONObject regionData = optional.get();

				log.info(" IP : {} >> Region : {} ", ip, regionData.toJSONString());

				String province = regionData.getString("pro");
				String city = regionData.getString("city");
				String addr = StringUtils.trimWhitespace(regionData.getString(ADDR_KEY));
				String country = addr;

				if(Stream.of(SPECIAL_REGION).anyMatch(region -> addr.contains(region))) {
					country = CHINA;
				}

				log.info(" IP : {} >> Country/Region : {} ", ip, country);

				return new RegionAddress(country, province, city, "", "");
			}
			return NOT_MATCH_REGION_ADDRESS;
		} catch (Exception e) {
			log.error("IP : {} >> Country/Region Parser Error：{}", ip, e.getMessage());
			return NOT_MATCH_REGION_ADDRESS;
		}
	}

	public RegionEnum getRegionByIp(String ip) {
		try {
			if(!IpUtils.isIpv4(ip)){
				return RegionEnum.UK;
			}
			Optional<JSONObject> optional = this.getLocationByIp(ip);
			if(optional.isPresent()) {

				JSONObject regionData = optional.get();
				log.info(" IP : {} >> Region : {} ", ip, regionData.toJSONString());

				String addr = StringUtils.trimWhitespace(regionData.getString(ADDR_KEY));
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
			log.error("IP : {} >> Country/Region Parser Error：{}", ip, e.getMessage());
			return RegionEnum.UK;
		}
	}

	public RegionEnum getRegionByGeo(double longitude, double latitude){
		return RegionEnum.UK;
	}

	public boolean isMainlandIp(String ip) {
		try {
			Optional<JSONObject> optional = this.getLocationByIp(ip);
			if(optional.isPresent()) {

				JSONObject regionData = optional.get();
				log.info(" IP : {} >> Region : {} ", ip, regionData.toJSONString());
				String proCode = regionData.getString("proCode");
				ProvinceEnum proEnum = ProvinceEnum.getByCode(proCode);
				
				
				
				
				if (!StringUtils.hasText(proCode) || SPECIAL_PROVINCE_SET.contains(proCode)) {
					return false;
				}

			}
		} catch (Exception e) {
			log.error("IP : {} >> Country/Region Parser Error：{}", ip, e.getMessage());
		}
		return true;
	}


	public static void main(String[] args) throws IOException {

		PconlineRegionTemplate template = new PconlineRegionTemplate(new OkHttpClient.Builder().build());

		Optional<JSONObject> mapLL2 = template.getLocationByIp("183.128.136.82"); // lng：116.86380647644208  lat：38.297615350325717
		System.out.println(mapLL2.get().toJSONString());
	}

}
