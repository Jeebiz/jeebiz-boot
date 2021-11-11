/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package net.jeebiz.boot.extras.external.region;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.hiwepy.ip2region.spring.boot.IP2regionTemplate;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionAddress;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionEnum;

import lombok.extern.slf4j.Slf4j;
import net.jeebiz.boot.extras.redis.setup.RedisKey;
import net.jeebiz.boot.extras.redis.setup.RedisOperationTemplate;



/**
 * 嵌套的地区解析模板
 */
@Component
@Slf4j
public class NestedRegionTemplate {

	private static final String NOT_MATCH = "未分配或者内网IP|0|0|0|0";
	
	protected static String REGION_KEY = "region";
	protected static String COUNTRY_KEY = "country";
	protected static String PROVINCE_KEY = "province";
	protected static String CITY_KEY = "city";
	protected static String AREA_KEY = "area";
	protected static String ISP_KEY = "isp";
	
	@Autowired
    private IP2regionTemplate ip2RegionTemplate;
    @Autowired
    private PconlineRegionTemplate pconlineRegionTemplate;
	@Autowired
	private RedisOperationTemplate redisOperation;
    
    public RegionEnum getRegion(String countryCode, String ipAddress) {
		RegionEnum regionEnum = RegionEnum.getByCode2(countryCode);
		log.info("Get Region {} By countryCode : {} ", regionEnum.name(), countryCode);
		if(regionEnum.compareTo(RegionEnum.UK) == 0 || regionEnum.compareTo(RegionEnum.TS) == 0) {
			regionEnum = RegionEnum.getByCode3(countryCode);
			log.info("Get Region {} By countryCode : {} ", regionEnum.name(), countryCode);
		}
		if(regionEnum.compareTo(RegionEnum.UK) == 0 || regionEnum.compareTo(RegionEnum.TS) == 0) {
			regionEnum = this.getRegionByIp(ipAddress);
		}
		log.info("Get Final Region {} By countryCode : {}, ipAddress : {} ", regionEnum.name(), countryCode, ipAddress);
		return regionEnum;
	}
    
	public RegionEnum getRegionByIp(String ipAddress) {
		// 1、尝试从缓存中获取ip对应的信息
		String redisKey = RedisKey.IP_LOCATION_INFO.getKey(ipAddress);
		Map<String, Object> locationMap = redisOperation.hmGet(redisKey);
		String region = MapUtils.getString(locationMap, REGION_KEY);
		if(StringUtils.hasText(region)) {
			// 1.1、如果解析到了region的属性，说明IP已经解析过地区信息，则直接返回
			RegionEnum regionEnum = RegionEnum.valueOf(region);
			log.info("Get Region {} By ipAddress: {} From Cache ", regionEnum.name(), ipAddress);
			return regionEnum;
		}
		// 2、尝试使用ip2region的ip库进行IP解析
		RegionEnum regionEnum = getIp2RegionTemplate().getRegionByIp(ipAddress);
		log.info("Get Region {} By ipAddress: {} From Ip2Region ", regionEnum.name(), ipAddress);
		if(regionEnum.compareTo(RegionEnum.UK) == 0 || regionEnum.compareTo(RegionEnum.TS) == 0) {
			try {
				// 3、尝试使用太平洋网络的ip库进行IP解析
				regionEnum = getPconlineRegionTemplate().getRegionByIp(ipAddress);
				log.info("Get Region {} By ipAddress: {} From Pconline ", regionEnum.name(), ipAddress);
			} catch (Exception e) {
				log.error("太平洋网络IP地址查询失败！{}", e.getMessage());
			}
		}
		// 4、更新缓存中的数据
		redisOperation.hSet(redisKey, REGION_KEY, regionEnum.getCode2());
		return regionEnum;
	}
	
    public RegionAddress getRegionAddress(String countryCode, String ipAddress) throws IOException {
		RegionEnum regionEnum = RegionEnum.getByCode2(countryCode);
		if(regionEnum.compareTo(RegionEnum.UK) == 0 || regionEnum.compareTo(RegionEnum.TS) == 0) {
			return this.getRegionAddress(ipAddress);
		}
		return new RegionAddress(regionEnum.getCname(), "", "", "", "");
	}
    
    public RegionAddress getRegionAddress(String ipAddress) {
    	// 1、尝试从缓存中获取ip对应的信息
		String redisKey = RedisKey.IP_LOCATION_INFO.getKey(ipAddress);
		Map<String, Object> locationMap = redisOperation.hmGet(redisKey);
		String country = MapUtils.getString(locationMap, COUNTRY_KEY);
		if(StringUtils.hasText(country)) {
			String province = MapUtils.getString(locationMap, PROVINCE_KEY);
			String city = MapUtils.getString(locationMap, CITY_KEY, "");
			String area = MapUtils.getString(locationMap, AREA_KEY, "");
			String isp = MapUtils.getString(locationMap, ISP_KEY, "");
			// 1.1、如果解析到了country的属性，说明IP已经解析过地区信息，则直接返回
			return new RegionAddress(country, province, city, area, isp);
		}
		// 2、尝试使用ip2region的ip库进行IP解析
    	RegionAddress regionAddress = getIp2RegionTemplate().getRegionAddress(ipAddress);
    	if(NOT_MATCH.contains(regionAddress.getCountry())) {
			try {
				// 3、尝试使用太平洋网络的ip库进行IP解析
				regionAddress = getPconlineRegionTemplate().getRegionAddress(ipAddress);
			} catch (Exception e) {
				log.error("太平洋网络IP地址查询失败！{}", e.getMessage());
			}
		}
		return regionAddress;
	}
	
	public boolean isMainlandIp(String countryCode, String ipAddress) {
		RegionEnum regionEnum = RegionEnum.getByCode2(countryCode);
		if(regionEnum.compareTo(RegionEnum.UK) == 0) {
			regionEnum = RegionEnum.getByCode3(countryCode);
		}
		if(regionEnum.compareTo(RegionEnum.UK) != 0 && regionEnum.compareTo(RegionEnum.TS) != 0) {
			return this.isMainlandIp(ipAddress);
		}
		return RegionEnum.CN.compareTo(regionEnum) == 0 && 
				RegionEnum.HK.compareTo(regionEnum) != 0 &&
				RegionEnum.MO.compareTo(regionEnum) != 0 &&
				RegionEnum.TW.compareTo(regionEnum) != 0;
	}
	
	public boolean isMainlandIp(String ipAddress) {
		// 1、尝试从缓存中获取ip对应的信息
		String redisKey = RedisKey.IP_LOCATION_INFO.getKey(ipAddress);
		Map<String, Object> locationMap = redisOperation.hmGet(redisKey);
		String region = MapUtils.getString(locationMap, REGION_KEY);
		if(StringUtils.hasText(region)) {
			// 1.1、如果解析到了region的属性，说明IP已经解析过地区信息，则直接返回
			RegionEnum regionEnum = RegionEnum.valueOf(region);
			return RegionEnum.CN.compareTo(regionEnum) == 0 && 
					RegionEnum.HK.compareTo(regionEnum) != 0 &&
					RegionEnum.MO.compareTo(regionEnum) != 0 &&
					RegionEnum.TW.compareTo(regionEnum) != 0;
		}
		// 2、尝试使用ip2region的ip库进行IP解析
		RegionEnum regionEnum = getIp2RegionTemplate().getRegionByIp(ipAddress);
		if(regionEnum.compareTo(RegionEnum.UK) == 0 || regionEnum.compareTo(RegionEnum.TS) == 0) {
			try {
				// 3、尝试使用太平洋网络的ip库进行IP解析
				regionEnum = getPconlineRegionTemplate().getRegionByIp(ipAddress);
			} catch (Exception e) {
				log.error("太平洋网络IP地址查询失败！{}", e.getMessage());
			}
		}
		// 4、更新缓存中的数据
		redisOperation.hSet(redisKey, REGION_KEY, regionEnum.getCode2());
		return RegionEnum.CN.compareTo(regionEnum) == 0 && 
				RegionEnum.HK.compareTo(regionEnum) != 0 &&
				RegionEnum.MO.compareTo(regionEnum) != 0 &&
				RegionEnum.TW.compareTo(regionEnum) != 0; 
	}

	public IP2regionTemplate getIp2RegionTemplate() {
		return ip2RegionTemplate;
	}
	
	public PconlineRegionTemplate getPconlineRegionTemplate() {
		return pconlineRegionTemplate;
	}
	
}
