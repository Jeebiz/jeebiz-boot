package net.jeebiz.boot.extras.external;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.hiwepy.ip2region.spring.boot.IP2regionTemplate;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionAddress;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * 嵌套的地区解析模板
 */
@Component
@Slf4j
public class NestedRegionTemplate {

	protected static String NOT_MATCH = "未分配或者内网IP|0|0|0|0";

	@Autowired
    private IP2regionTemplate ip2RegionTemplate;
    @Autowired
    private WhoisIpTemplate whoisIpTemplate;
	
    public RegionEnum getRegion(String countryCode, String ipAddress) {
		RegionEnum regionEnum = RegionEnum.getByCode2(countryCode);
		if(regionEnum.compareTo(RegionEnum.UK) == 0) {
			regionEnum = this.getRegionByIp(ipAddress);
		}
		return regionEnum;
	}
    
	public RegionEnum getRegionByIp(String ipAddress) {
		RegionEnum regionEnum = getIp2RegionTemplate().getRegionByIp(ipAddress);
		if(regionEnum.compareTo(RegionEnum.UK) == 0) {
			try {
				regionEnum = getWhoisIpTemplate().getRegionByIp(ipAddress);
			} catch (Exception e) {
				log.error("太平洋网络IP地址查询失败！{}", e.getMessage());
			}
		}
		return regionEnum;
	}
	
    public RegionAddress getRegionAddress(String countryCode, String ipAddress) throws IOException {
		RegionEnum regionEnum = RegionEnum.getByCode2(countryCode);
		if(regionEnum.compareTo(RegionEnum.UK) == 0) {
			return this.getRegionAddress(ipAddress);
		}
		return new RegionAddress(regionEnum.getCname(), "", "", "", "");
	}
    
    public RegionAddress getRegionAddress(String ipAddress) throws IOException {
    	RegionAddress regionAddress = getIp2RegionTemplate().getRegionAddress(ipAddress);
    	if(NOT_MATCH.contains(regionAddress.getCountry())) {
			try {
				regionAddress = getWhoisIpTemplate().getRegionAddress(ipAddress);
			} catch (Exception e) {
				log.error("太平洋网络IP地址查询失败！{}", e.getMessage());
			}
		}
		return regionAddress;
	}
	
	public boolean isMainlandIp(String countryCode, String ipAddress) {
		RegionEnum regionEnum = RegionEnum.getByCode2(countryCode);
		if(regionEnum.compareTo(RegionEnum.UK) == 0) {
			return this.isMainlandIp(ipAddress);
		}
		return RegionEnum.CN.compareTo(regionEnum) == 0 && 
				RegionEnum.HK.compareTo(regionEnum) != 0 &&
				RegionEnum.MO.compareTo(regionEnum) != 0 &&
				RegionEnum.TW.compareTo(regionEnum) != 0;
	}
	
	public boolean isMainlandIp(String ipAddress) {
		RegionEnum regionEnum = getIp2RegionTemplate().getRegionByIp(ipAddress);
		if(regionEnum.compareTo(RegionEnum.UK) != 0) {
			return RegionEnum.CN.compareTo(regionEnum) == 0 && 
					RegionEnum.HK.compareTo(regionEnum) != 0 &&
					RegionEnum.MO.compareTo(regionEnum) != 0 &&
					RegionEnum.TW.compareTo(regionEnum) != 0;
		}
		try {
			return getWhoisIpTemplate().isMainlandIp(ipAddress);
		} catch (Exception e) {
			log.error("太平洋网络IP地址查询失败！{}", e.getMessage());
		}
		return true;
	}

	public IP2regionTemplate getIp2RegionTemplate() {
		return ip2RegionTemplate;
	}
	
	public WhoisIpTemplate getWhoisIpTemplate() {
		return whoisIpTemplate;
	}
}
