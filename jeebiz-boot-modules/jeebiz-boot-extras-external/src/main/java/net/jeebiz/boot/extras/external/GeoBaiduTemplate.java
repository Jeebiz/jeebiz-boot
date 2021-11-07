package net.jeebiz.boot.extras.external;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.github.hiwepy.httpconn.HttpConnectionUtils;
import com.github.hiwepy.httpconn.handler.JSONResponseHandler;

import okhttp3.OkHttpClient;

/**
 * 	地址获取经纬度： http://lbsyun.baidu.com/index.php?title=webapi/guide/webservice-geocoding
 *  IP获取经纬度：   http://lbsyun.baidu.com/index.php?title=webapi/ip-api
 */
public class GeoBaiduTemplate {
	
	private static String geocoder = "http://api.map.baidu.com/geocoding/v3/?address=%s&output=json&ak=%s";
	private static String geocoder2 = "http://api.map.baidu.com/location/ip?ak=%s&ip=%s&coor=bd09ll";
	private final OkHttpClient okhttp3Client;
	private final String ak;

	public GeoBaiduTemplate(OkHttpClient okhttp3Client, String ak) {
		super();
		this.okhttp3Client = okhttp3Client;
		this.ak = ak; 
	}
	
	/**
	 * 调用百度API 
	 * @param addr
	 * @return
	 * @throws IOException 
	 */
	public Map<String, BigDecimal> getLatAndLngByAddress(String addr) throws IOException{
        
        // {"message":"APP Referer校验失败","status":220}
        JSONObject json = this.getLocationByAddress(addr);
        JSONObject result = json.getJSONObject("result");
        JSONObject location = result.getJSONObject("location");
        
        Map<String, BigDecimal> map = new HashMap<String, BigDecimal>();
        map.put("lat", location.getBigDecimal("lat"));
        map.put("lng", location.getBigDecimal("lng"));
        return map;
	}
	
	/**
	 * 调用百度API 
	 * @param addr
	 * @return
	 * @throws IOException 
	 */
	public JSONObject getLocationByAddress(String addr) throws IOException{
        String address = "";
        try {  
            address = java.net.URLEncoder.encode(addr,"UTF-8");  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        }
        String url = String.format(geocoder, address, this.ak);
        // {"message":"APP Referer校验失败","status":220}
        JSONObject json = HttpConnectionUtils.httpRequestWithGet(url, new JSONResponseHandler());
        if(json.getInteger("status") != 0) {
        	throw new IOException(json.getString("message"));
        }
        return json;
	}
    
   /**
	* 获取指定IP对应的经纬度（为空返回当前机器经纬度）
	/*
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
	public JSONObject getLocationByIp(String ip) {
		if (Objects.isNull(ip)) {
			throw new NullPointerException("ip can not empty");
		}
		try {
			
			String url = String.format(geocoder2, this.ak, ip);
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
		
		GeoBaiduTemplate template = new GeoBaiduTemplate("");
		
		Map<String, BigDecimal> mapLL = template.getLatAndLngByAddress("浙江省杭州市西湖区"); // lng：116.86380647644208  lat：38.297615350325717
		mapLL.get("lat");
		mapLL.get("lng");
		System.out.println("lng："+mapLL.get("lng") + "  lat："+mapLL.get("lat"));
		
		JSONObject mapLL2 = template.getLocationByIp("115.204.225.154"); // lng：116.86380647644208  lat：38.297615350325717
		System.out.println(mapLL2.toJSONString());
	}

	
}
