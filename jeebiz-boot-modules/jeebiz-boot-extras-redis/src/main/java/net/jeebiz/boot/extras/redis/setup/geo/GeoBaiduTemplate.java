package net.jeebiz.boot.extras.redis.setup.geo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSONObject;
import com.github.hiwepy.httpconn.HttpConnectionUtils;
import com.github.hiwepy.httpconn.handler.JSONResponseHandler;

/**
 * 	
 *  
 *  
 */
public class GeoBaiduTemplate {
	
	private static String geocoding = "http://api.map.baidu.com/geocoding/v3/?address=%s&output=json&ak=%s";
	private static String reverse_geocoding = "http://api.map.baidu.com/reverse_geocoding/v3/?ak=%s&output=json&coordtype=%s&location=%s,%s";
	private static String location = "http://api.map.baidu.com/location/ip?ak=%s&ip=%s&coor=bd09ll";
	
	
	private static JSONResponseHandler responseHandler = new JSONResponseHandler();
	
	/**
	 * 调用百度API 
	 * @param addr
	 * @return
	 * @throws IOException 
	 */
	public Map<String, BigDecimal> getLatAndLngByAddress(String ak, String addr) throws IOException{
        
        // {"message":"APP Referer校验失败","status":220}
        JSONObject json = this.getLocationByAddress(ak, addr);
        JSONObject result = json.getJSONObject("result");
        JSONObject location = result.getJSONObject("location");
        
        Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
        map.put("lat", location.getBigDecimal("lat"));
        map.put("lng", location.getBigDecimal("lng"));
        return map;
	}
	
	
	/**
	 * 1、地理编码服务： http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding
	 * @param addr
	 * @return
	 * @throws IOException 
	 */
	public JSONObject getLocationByAddress(String ak, String addr) throws IOException{
        String address = "";
        try {  
            address = java.net.URLEncoder.encode(addr,"UTF-8");  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        }
        String url = String.format(geocoding, address, ak);
        System.out.println(url);
        // {"message":"APP Referer校验失败","status":220}
        JSONObject json = HttpConnectionUtils.httpRequestWithGet(url, new JSONResponseHandler());
        if(json.getInteger("status") != 0) {
        	throw new IOException(json.getString("message"));
        }
        return json;
	}
    
	/**
	 * 2、逆地理编码：http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding-abroad
	 * @param ak
	 * @param lng lng<经度> 
	 * @param lat lat<纬度>
	 * @return
	 */
	public JSONObject getLocationByGeo(String ak, double lng, double lat) {
		return this.getLocationByGeo(ak, "wgs84ll", lng, lat);
	}
	
	/**
	 * 2、逆地理编码：http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding-abroad
	 * @param ak
	 * @param coordtype 坐标的类型，目前支持的坐标类型包括：bd09ll（百度经纬度坐标）、bd09mc（百度米制坐标）、gcj02ll（国测局经纬度坐标，仅限中国）、wgs84ll（ GPS经纬度） 
	 * @param lng lng<经度> 
	 * @param lat lat<纬度>
	 * @return
	 */
	public JSONObject getLocationByGeo(String ak, String coordtype, double lng, double lat) {
		if (lng == 0 || lat == 0) {
			throw new IllegalArgumentException ("lng or lat must greater than 0");
		}
		try {
			String url = String.format(reverse_geocoding, ak, coordtype, lat, lng);
	        System.out.println(url);
			JSONObject json = HttpConnectionUtils.httpRequestWithGet(url, responseHandler);
	        if(json.getInteger("status") != 0) {
	        	throw new IOException(json.getString("message"));
	        }
	        return json;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
   /**
    * 3、IP获取经纬度：   http://lbsyun.baidu.com/index.php?title=webapi/ip-api
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
	public JSONObject getLocationByIp(String ak, String ip) {
		if (Objects.isNull(ip)) {
			throw new NullPointerException("ip can not empty");
		}
		try {
			String url = String.format(location, ak, ip);
	        System.out.println(url);
	        JSONObject json = HttpConnectionUtils.httpRequestWithGet(url, responseHandler);
	        if(json.getInteger("status") != 0) {
	        	throw new IOException(json.getString("message"));
	        }
	        return json;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	 /** 
	  * unicode 字节码转换成 中文 
	  * @param ascii 
	  * @return 
	  */  
	public String decodeUnicode(String ascii) {  
		char aChar;  
		int len = ascii.length();  
		StringBuffer outBuffer = new StringBuffer(len);  
		for (int x = 0; x < len;) {  
			aChar = ascii.charAt(x++);  
			if (aChar == '\\') {  
			    aChar = ascii.charAt(x++);  
			    if (aChar == 'u') {  
					int value = 0;  
					for (int i = 0; i < 4; i++) {  
						aChar = ascii.charAt(x++);  
						switch (aChar) {  
							case '0':  
							case '1':  
							case '2':  
							case '3':  
							case '4':  
							case '5':  
							case '6':  
							case '7':  
							case '8':  
							case '9':  
							value = (value << 4) + aChar - '0';  
							break;  
							case 'a':  
							case 'b':  
							case 'c':  
							case 'd':  
							case 'e':  
							case 'f':  
							value = (value << 4) + 10 + aChar - 'a';  
							break;  
							case 'A':  
							case 'B':  
							case 'C':  
							case 'D':  
							case 'E':  
							case 'F':  
							value = (value << 4) + 10 + aChar - 'A';  
							break;  
							default:  
							throw new IllegalArgumentException("Malformed encoding.");  
						}  
					}  
					outBuffer.append((char) value);  
			    } else {  
			    	if (aChar == 't') {  
			    		aChar = '\t';  
			    	} else if (aChar == 'r') {  
			    		aChar = '\r';  
			    	} else if (aChar == 'n') {  
			    		aChar = '\n';  
			    	} else if (aChar == 'f') {  
			    		aChar = '\f';  
			    	}  
			    	outBuffer.append(aChar);  
			    }  
			} else {  
				outBuffer.append(aChar);  
			}  
		}  
		return outBuffer.toString();  
	}  
	
	public static void main(String[] args) throws IOException {
		
		GeoBaiduTemplate template = new GeoBaiduTemplate();
		
		String ak = "你的访问应用（AK）";
		
		Map<String, BigDecimal> mapLL = template.getLatAndLngByAddress(ak, "浙江省杭州市西湖区"); // lng：116.86380647644208  lat：38.297615350325717
		mapLL.get("lat");
		mapLL.get("lng");
		System.out.println("lng："+mapLL.get("lng") + "  lat："+mapLL.get("lat"));
		
		JSONObject mapLL2 = template.getLocationByIp(ak, "115.204.225.154"); // lng：116.86380647644208  lat：38.297615350325717
		System.out.println(mapLL2.toJSONString());

		JSONObject mapLL3 = template.getLocationByGeo(ak, "wgs84ll", 120.005488, 30.285870 ); // lng：116.86380647644208  lat：38.297615350325717
		System.out.println(mapLL3.toJSONString());
		
		
	}

	
}
