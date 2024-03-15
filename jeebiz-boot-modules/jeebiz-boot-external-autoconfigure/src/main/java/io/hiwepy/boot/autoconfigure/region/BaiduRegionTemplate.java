/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure.region;

import com.alibaba.fastjson2.JSONObject;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionAddress;
import com.github.hiwepy.ip2region.spring.boot.ext.RegionEnum;
import com.github.hiwepy.ip2region.spring.boot.ext.XdbSearcher;
import com.github.hiwepy.ip2region.spring.boot.util.IpUtils;
import com.github.hiwepy.ip2region.spring.boot.util.Util;
import hitool.core.lang3.time.CalendarUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.data.redis.core.RedisKey;
import org.springframework.data.redis.core.RedisOperationTemplate;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 *  IP获取经纬度：   http://lbsyun.baidu.com/index.php?title=webapi/ip-api
 *  https://blog.csdn.net/Li_Chunxiao_/article/details/107082921
 */
@Slf4j
public class BaiduRegionTemplate {

	private static final String GET_LOCATION_BY_IP_URL = "https://api.map.baidu.com/location/ip?ak=%s&ip=%s&coor=bd09ll";
	private final String ak;
	private final OkHttpClient okhttp3Client;
	private RedisOperationTemplate redisOperation;

	public BaiduRegionTemplate(String ak, OkHttpClient okhttp3Client) {
		this.ak = ak;
		this.okhttp3Client = okhttp3Client;
	}

	public BaiduRegionTemplate(String ak, OkHttpClient okhttp3Client, RedisOperationTemplate redisOperation) {
		this.ak = ak;
		this.okhttp3Client = okhttp3Client;
		this.redisOperation = redisOperation;
	}

    /**
	* 获取指定IP对应的经纬度（为空返回当前机器经纬度）
	 *
	 * {
		    address: "CN|北京|北京|None|CHINANET|1|None",    #详细地址信息
		    content:    #结构信息
		    {
		        address: "北京市",    #简要地址信息
		        address_detail:    #结构化地址信息
		        {
		            city: "北京市",    #城市
		            city_code: 131,    #百度城市代码
		            district: "",    #区县
		            province: "北京市",    #省份
		            street: "",    #街道
		            street_number: ""    #门牌号
		        },
		        point:    #当前城市中心点
		        {
		            x: "116.39564504",    #当前城市中心点经度
		            y: "39.92998578"    #当前城市中心点纬度
		        }
		    },
		    status: 0    #结果状态返回码
		}
	*
	* @param ip
	* @return
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
		String redisKey = RedisKey.IP_LOCATION_BAIDU_INFO.getKey(Util.ip2long(ip));
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
			String url = String.format(GET_LOCATION_BY_IP_URL, this.ak, ip);
			Request request = new Request.Builder().url(url).build();
			Response response = okhttp3Client.newCall(request).execute();
			if (response.isSuccessful()) {
				String bodyString = response.body().string();
				log.info(" IP : {} >> Location : {} ", ip, bodyString);
				JSONObject jsonObject = JSONObject.parseObject(bodyString);
				if (jsonObject.getInteger("status") != 0) {
					throw new IOException(jsonObject.getString("message"));
				}
				if(Objects.nonNull(redisOperation)) {
					redisOperation.set(redisKey, bodyString, CalendarUtils.getSecondsNextEarlyMorning());
				}
				return Optional.ofNullable(jsonObject);
			}
			log.error("IP : {} >> Location Query Error. Response Code >> {}, Body >> {}", response.code(), response.body().string());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("IP : {} >> Country/Region Parser Error：{}", ip, e.getMessage());
		}
		return Optional.empty();
	}

	public RegionAddress getRegionAddress(String ip) {
		try {
			Optional<JSONObject> optional = this.getLocationByIp(ip);
			if(optional.isPresent()) {

				JSONObject regionData = optional.get();

				if(regionData.getIntValue("status") == 0) {

					// CN|浙江省|杭州市|None|None|99|99
					String address = regionData.getString("address");

					String[] addrArr = StringUtils.tokenizeToStringArray(address, "|");

					RegionEnum region = RegionEnum.getByCode2(addrArr[0]);

					log.info(" IP : {} >> Country/Region : {} >> {} ", ip, region.getCode2(), region.getCname());

					return new RegionAddress(region.getCname(), addrArr[1], addrArr[2], "", addrArr[4]);

				}

			}
			return XdbSearcher.NOT_MATCH_REGION_ADDRESS;
		} catch (Exception e) {
			log.error("IP : {} >> Country/Region Parser Error：{}", ip, e.getMessage());
			return XdbSearcher.NOT_MATCH_REGION_ADDRESS;
		}
	}

	public RegionEnum getRegionByIp(String ip) {
		try {
			Optional<JSONObject> optional = this.getLocationByIp(ip);
			if(optional.isPresent()) {
				JSONObject regionData = optional.get();
				if(regionData.getIntValue("status") == 0) {

					// CN|浙江省|杭州市|None|None|99|99
					String address = regionData.getString("address");

					String[] addrArr = StringUtils.tokenizeToStringArray(address, "|");

					RegionEnum region = RegionEnum.getByCode2(addrArr[0]);

					log.info(" IP : {} >> Country/Region : {} >> {} ", ip, region.getCode2(), region.getCname());

					return region;

				}
			}
			return RegionEnum.UK;
		} catch (Exception e) {
			log.error("IP : {} >> Country/Region Parser Error：{}", ip, e.getMessage());
			return RegionEnum.UK;
		}
	}

    public Location getLocation(String ip) {
    	try {
			Optional<JSONObject> optional = this.getLocationByIp(ip);
			if(optional.isPresent()) {
				JSONObject regionData = optional.get();
				if(regionData.getIntValue("status") == 0) {

					// CN|浙江省|杭州市|None|None|99|99
					JSONObject content = regionData.getJSONObject("content");
					JSONObject point = content.getJSONObject("point");
					// 当前城市中心点经度
					Double longitude = point.getDouble("x");
					// 当前城市中心点纬度
					Double latitude = point.getDouble("y");

					log.info(" IP : {} >> longitude,latitude : {},{} ", ip, longitude, latitude);

					return new Location(longitude, latitude);
				}
			}
		} catch (Exception e) {
			log.error("IP : {} >> Location Parser Error：{}", ip, e.getMessage());
		}
        return null;
    }

	public boolean isMainlandIp(String ip) {
		try {
			Optional<JSONObject> optional = this.getLocationByIp(ip);
			if(optional.isPresent()) {

				JSONObject regionData = optional.get();
				log.info(" IP : {} >> Region : {} ", ip, regionData.toJSONString());



			}
		} catch (Exception e) {
			log.error("IP Region Parser Error：{}", e.getMessage());
		}
		return true;
	}

	@Data
	@AllArgsConstructor
	public class Location {

		/**
		 * 经度
		 */
		private final Double longitude;
		/**
		 * 纬度
		 */
		private final Double latitude;

	}


	public static void main(String[] args) throws IOException {

		BaiduRegionTemplate template = new BaiduRegionTemplate("CGxeqGuAGgP7n475kMPTi58y2EqjAPTh",
				new OkHttpClient.Builder().build());

		Optional<JSONObject> mapLL2 = template.getLocationByIp("183.128.136.82"); // lng：116.86380647644208  lat：38.297615350325717
		System.out.println(mapLL2.get().toJSONString());
	}


}
