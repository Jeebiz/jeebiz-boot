package io.hiwepy.boot.api.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SignUtils {

	/**
	 * 1、获取密钥
	 * 
	 * @param appId       客户端id
	 * @param appVersion  版本号
	 * @param appChannel  渠道
	 * @param fixedSecret 固定密钥
	 * @return 密钥
	 */
	public static String salt(String appId, String appVersion, String appChannel, String fixedSecret) {
		String originStr = appId + appVersion + appChannel + fixedSecret;
		String salt = DigestUtils.md5DigestAsHex(originStr.getBytes()).substring(7, 23);
		log.info("salt : {}", salt);
		return salt;
	}

	/**
     * 2、验签
     * @param token     token
     * @param formData  参数
     * @param body      方法体
     * @param salt    	密钥
     * @return          签名原文
     * @throws UnsupportedEncodingException 
     */
    protected static String signOrigin(String token, MultiValueMap<String, String> formData, String body, String salt) throws UnsupportedEncodingException {
        
        StringBuilder requestData = new StringBuilder(StringUtils.defaultString(token)).append("|");
        // form参数
        if(MapUtils.isEmpty(formData)) {
            requestData.append("|");
        } else {
        	
        	TreeMap<String, String> params = new TreeMap<>((o1, o2) -> o1.compareTo(o2));
            formData.forEach((key, value) -> {
                if(value.isEmpty()) {
                    params.put(key, "");
                } else {
                    params.put(key, value.get(0));
                }
            });
            
            params.forEach((key, value) -> {
                if(StringUtils.isEmpty(value)) {
                    requestData.append("|");
                } else {
                    requestData.append(value).append("|");
                }
            });
        }
        // body参数
        if(StringUtils.isEmpty(body)) {
            requestData.append("{}");
        } else {
            requestData.append(StringUtils.deleteWhitespace(body));
        }
        
        String signOrigin = URLEncoder.encode(requestData.toString(), StandardCharsets.UTF_8.name());
		
        return DigestUtils.md5DigestAsHex(signOrigin.getBytes());
    }
    
    public static String sign(String token, MultiValueMap<String, String> formData, String body, String salt) throws UnsupportedEncodingException {
        
        String originSign = signOrigin(token, formData, body, salt);

		// 在originSign字符串中每隔两个位置插入一个salt
		char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = originSign.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = originSign.charAt(i / 3 * 2 + 1);
        }
		String sign = new String(cs);
		
        return sign;
        
    }

	public static boolean verify(String token, String appId, String appVersion, String appChannel,
			String fixedSecret, String clientSign, MultiValueMap<String, String> params, String body)
			throws UnsupportedEncodingException {

		String salt = salt(appId, appVersion, appChannel, fixedSecret);

		String sign = sign(token, params, body, salt);
		
		return sign.equals(clientSign);
	}
	
	/**
     * 2、验签
     * @param token     token
     * @param formData  参数
     * @param body      方法体
     * @param salt    	密钥
     * @return          签名原文
     * @throws UnsupportedEncodingException 
     */
    protected static String signOrigin(String token, Map<String, String[]> formData, String body, String salt) throws UnsupportedEncodingException {
        
        StringBuilder requestData = new StringBuilder(StringUtils.defaultString(token)).append("|");
        // form参数
        if(MapUtils.isEmpty(formData)) {
            requestData.append("|");
        } else {
        	
        	TreeMap<String, String> params = new TreeMap<>((o1, o2) -> o1.compareTo(o2));
            formData.forEach((key, values) -> {
                if(values.length == 0) {
                    params.put(key, "");
                } else {
                    params.put(key, values[0]);
                }
            });
            
            params.forEach((key, value) -> {
                if(StringUtils.isEmpty(value)) {
                    requestData.append("|");
                } else {
                    requestData.append(value).append("|");
                }
            });
        }
        // body参数
        if(StringUtils.isEmpty(body)) {
            requestData.append("{}");
        } else {
            requestData.append(StringUtils.deleteWhitespace(body));
        }
        
        String signOrigin = URLEncoder.encode(requestData.toString(), StandardCharsets.UTF_8.name());
		
        return DigestUtils.md5DigestAsHex(signOrigin.getBytes());
    }
    
    public static String sign(String token, Map<String, String[]> formData, String body, String salt) throws UnsupportedEncodingException {
        
        String originSign = signOrigin(token, formData, body, salt);

		// 在originSign字符串中每隔两个位置插入一个salt
		char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = originSign.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = originSign.charAt(i / 3 * 2 + 1);
        }
		String sign = new String(cs);
		
        return sign;
        
    }

	public static boolean verify(String token, String appId, String appVersion, String appChannel,
			String fixedSecret, String clientSign, Map<String, String[]> formData, String body)
			throws UnsupportedEncodingException {

		String salt = salt(appId, appVersion, appChannel, fixedSecret);

		String sign = sign(token, formData, body, salt);
		
		return sign.equals(clientSign);
	}
	
	public static void main(String[] args) throws Exception {
		
		String appId = "1", appVersion = "20000", appChannel = "ASO000", fixedSecret = "kd2021";
		
		String salt = SignUtils.salt(appId, appVersion, appChannel, fixedSecret);
		System.out.println(salt);
		
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		String token = "";
		String body = "";
		
		String sign = SignUtils.sign(token, formData, body, salt);
		
		System.out.println(sign);
		
		String clientSign = "2136d6ec5e2c17b3ac787bf6f86fffe7ab7b0c389efa970a";

		System.out.println(SignUtils.verify(token, appId, appVersion, appChannel, fixedSecret, clientSign, formData, body));
		
	}
	

}
