/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure.region;

import com.github.hiwepy.ip2region.spring.boot.IP2regionTemplate;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionAddress;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionEnum;
import com.github.hiwepy.ip2region.spring.boot.ext.XdbSearcher;
import hitool.core.lang3.network.InetAddressUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisKey;
import org.springframework.data.redis.core.RedisOperationTemplate;
import org.springframework.util.StringUtils;

import java.time.Duration;


/**
 * 嵌套的地区解析模板
 */
@Slf4j
public class NestedRegionTemplate {

	private RedisOperationTemplate redisOperation;
    private IP2regionTemplate ip2RegionTemplate;
    private PconlineRegionTemplate pconlineRegionTemplate;

	public NestedRegionTemplate(RedisOperationTemplate redisOperation, IP2regionTemplate ip2RegionTemplate,
								PconlineRegionTemplate pconlineRegionTemplate){
		this.redisOperation = redisOperation;
		this.ip2RegionTemplate = ip2RegionTemplate;
		this.pconlineRegionTemplate = pconlineRegionTemplate;
	}


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
		try {
			// 1、去除参数两头空白
			ipAddress = StringUtils.trimWhitespace(ipAddress);
			if(InetAddressUtils.internalIp(ipAddress)){
				return RegionEnum.UK;
			}
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
				redisOperation.set(redisKey, regionEnum.getCode2(), Duration.ofMinutes(30));
				return regionEnum;
			}
			redisOperation.set(redisKey, RegionEnum.UK.getCode2(), Duration.ofMinutes(30));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return RegionEnum.UK;
	}

	public String getLocationByIp(String ipAddress) {
		try {
			// 1、去除参数两头空白
			ipAddress = StringUtils.trimWhitespace(ipAddress);
			if(InetAddressUtils.internalIp(ipAddress)){
				return XdbSearcher.NOT_MATCH;
			}
			// 2、优先从本地缓存获取数据
			String redisKey = RedisKey.IP_LOCATION_INFO.getKey(ipAddress);
			String regionAddress = redisOperation.getString(redisKey);
			if(StringUtils.hasText(regionAddress)){
				return regionAddress;
			}
			// 3、尝试使用ip2region的ip库进行IP解析
			regionAddress = getIp2RegionTemplate().getRegion(ipAddress);
			log.debug("Get Location : {} By ipAddress: {} From Ip2Region ", regionAddress, ipAddress);
			redisOperation.set(redisKey, regionAddress, Duration.ofMinutes(30));
			return regionAddress;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return XdbSearcher.NOT_MATCH;
	}

    public RegionAddress getRegionAddress(String ipAddress) {
		try {
			// 1、去除参数两头空白
			ipAddress = StringUtils.trimWhitespace(ipAddress);
			if(InetAddressUtils.internalIp(ipAddress)){
				return XdbSearcher.NOT_MATCH_REGION_ADDRESS;
			}
			// 2、尝试使用ip2region的ip库进行IP解析
			RegionAddress regionAddress = getIp2RegionTemplate().getRegionAddress(ipAddress);
			if(XdbSearcher.NOT_MATCH_REGION_ADDRESS.equals(regionAddress)) {
				try {
					// 3、尝试使用太平洋网络的ip库进行IP解析
					regionAddress = getPconlineRegionTemplate().getRegionAddress(ipAddress);
				} catch (Exception e) {
					log.error("太平洋网络IP地址查询失败！{}", e.getMessage());
				}
			}
			return regionAddress;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return XdbSearcher.NOT_MATCH_REGION_ADDRESS;
	}

	public boolean isMainlandIp(String regionCode, String ipAddress) {
		try {
			RegionEnum regionEnum = this.getRegionByCode(regionCode);
			if(!regionEnum.isValidRegion()) {
				return this.isMainlandIp(ipAddress);
			}
			return regionEnum.isChinaMainland();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return Boolean.FALSE;
	}

	public boolean isMainlandIp(String ipAddress) {
		// 1、去除参数两头空白
		ipAddress = StringUtils.trimWhitespace(ipAddress);
		if(InetAddressUtils.internalIp(ipAddress)){
			return true;
		}
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
