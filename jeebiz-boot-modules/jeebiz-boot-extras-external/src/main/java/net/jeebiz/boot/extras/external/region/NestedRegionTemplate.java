/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package net.jeebiz.boot.extras.external.region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisKey;
import org.springframework.data.redis.core.RedisOperationTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.hiwepy.ip2region.spring.boot.IP2regionTemplate;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionAddress;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionEnum;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;


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
	private RedisOperationTemplate redisOperation;
	@Autowired
    private IP2regionTemplate ip2RegionTemplate;
    @Autowired
    private PconlineRegionTemplate pconlineRegionTemplate;

    public RegionEnum getRegion(String regionCode, String ipAddress) {
		RegionEnum regionEnum = this.getRegionByCode(regionCode);
		if(!regionEnum.isValidRegion()) {
			regionEnum = this.getRegionByIp(ipAddress);
		}
		log.info("Get Final Region : {} By regionCode : {}, ipAddress : {}, is Valid : {} ", regionEnum.name(), regionCode, ipAddress, regionEnum.isValidRegion());
		return regionEnum;
	}

    public RegionEnum getRegionByCode(String regionCode) {
		RegionEnum regionEnum = RegionEnum.getByCode2(regionCode);
		log.debug("Get Region : {} By regionCode : {}, is Valid : {} ", regionEnum.name(), regionCode, regionEnum.isValidRegion());
		if(!regionEnum.isValidRegion()) {
			regionEnum = RegionEnum.getByCode3(regionCode);
			log.debug("Get Region : {} By regionCode : {}, is Valid : {} ", regionEnum.name(), regionCode, regionEnum.isValidRegion());
		}
		return regionEnum;
	}

	public RegionEnum getRegionByIp(String ipAddress) {
		// 1、去除参数两头空白
		ipAddress = StringUtils.trimWhitespace(ipAddress);
		// 2、优先从本地缓存获取数据
		String redisKey = RedisKey.IP_REGION_INFO.getKey(ipAddress);
		String regionCode = redisOperation.getString(redisKey);
		if(StringUtils.hasText(regionCode)){
			return RegionEnum.getByCode2(regionCode);
		}
		// 3、尝试使用ip2region的ip库进行IP解析
		RegionEnum regionEnum = getIp2RegionTemplate().getRegionByIp(ipAddress);
		log.debug("Get Region : {} By ipAddress: {} From Ip2Region, is Valid : {} ", regionEnum.name(), ipAddress, regionEnum.isValidRegion());
		if(!regionEnum.isValidRegion()) {
			try {
				// 3、尝试使用太平洋网络的ip库进行IP解析
				regionEnum = getPconlineRegionTemplate().getRegionByIp(ipAddress);
				log.debug("Get Region {} By ipAddress: {} From Pconline, is Valid : {} ", regionEnum.name(), ipAddress, regionEnum.isValidRegion());
			} catch (Exception e) {
				log.error("太平洋网络IP地址查询失败！{}", e.getMessage());
			}
		}
		if(regionEnum.isValidRegion()) {
			redisOperation.set(redisKey, regionEnum.getCode2(), Duration.ofMinutes(10));
			return regionEnum;
		}
		redisOperation.set(redisKey, RegionEnum.UK.getCode2(), Duration.ofMinutes(10));
		return RegionEnum.UK;
	}

    public RegionAddress getRegionAddress(String ipAddress) {
    	// 1、去除参数两头空白
		ipAddress = StringUtils.trimWhitespace(ipAddress);
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

	public boolean isMainlandIp(String regionCode, String ipAddress) {
		RegionEnum regionEnum = this.getRegionByCode(regionCode);
		if(!regionEnum.isValidRegion()) {
			return this.isMainlandIp(ipAddress);
		}
		return regionEnum.isChinaMainland();
	}

	public boolean isMainlandIp(String ipAddress) {
		// 1、去除参数两头空白
		ipAddress = StringUtils.trimWhitespace(ipAddress);
		// 2、尝试使用ip2region的ip库进行IP解析
		RegionEnum regionEnum = getIp2RegionTemplate().getRegionByIp(ipAddress);
		if(!regionEnum.isValidRegion()) {
			try {
				// 3、尝试使用太平洋网络的ip库进行IP解析
				regionEnum = getPconlineRegionTemplate().getRegionByIp(ipAddress);
			} catch (Exception e) {
				log.error("太平洋网络IP地址查询失败！{}", e.getMessage());
			}
		}
		return regionEnum.isChinaMainland();
	}

	public IP2regionTemplate getIp2RegionTemplate() {
		return ip2RegionTemplate;
	}

	public PconlineRegionTemplate getPconlineRegionTemplate() {
		return pconlineRegionTemplate;
	}

}
