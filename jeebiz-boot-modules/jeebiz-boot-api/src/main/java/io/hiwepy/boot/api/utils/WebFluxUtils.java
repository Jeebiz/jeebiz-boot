package io.hiwepy.boot.api.utils;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpServletRequest;

import org.springframework.biz.utils.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;

import lombok.extern.slf4j.Slf4j;
import io.hiwepy.boot.api.XHeaders;
import reactor.core.publisher.Flux;

@Slf4j
public class WebFluxUtils {

	private static final String XML_HTTP_REQUEST = "XMLHttpRequest";
    private static final String X_REQUESTED_WITH = "X-Requested-With";
    private static final String CONTENT_TYPE_JSON = "application/json";
	private static final String[] xheaders = new String[]{"X-Forwarded-For", "x-forwarded-for"};
	private static final String[] headers = new String[]{"Cdn-Src-Ip", "Proxy-Client-IP", "WL-Proxy-Client-IP", "X-Real-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
	private static final String LOCAL_HOST = "localhost";
	private static final String LOCAL_IP6 = "0:0:0:0:0:0:0:1";
	private static final String LOCAL_IP = "127.0.0.1";
	private static final String UNKNOWN = "unknown";   
	
    public static boolean isAjaxResponse(ServerHttpRequest request ) {
		return isAjaxRequest(request) || isContentTypeJson(request) || isPostRequest(request);
	}

    public static boolean isObjectRequest(ServerHttpRequest request ) {
        return isPostRequest(request) && isContentTypeJson(request);
    }

    public static boolean isObjectRequest(HttpRequest request ) {
        return isPostRequest(request) && isContentTypeJson(request);
    }
    
    public static boolean isAjaxRequest(ServerHttpRequest request ) {
        return XML_HTTP_REQUEST.equals(request.getHeaders().getFirst(X_REQUESTED_WITH));
    }
    
    public static boolean isAjaxRequest(HttpRequest request ) {
        return request.getHeaders().get(X_REQUESTED_WITH).contains(XML_HTTP_REQUEST);
    }

    public static boolean isContentTypeJson(ServerHttpRequest request ) {
        return request.getHeaders().get(HttpHeaders.CONTENT_TYPE).contains(CONTENT_TYPE_JSON);
    }
    
    public static boolean isContentTypeJson(HttpRequest request ) {
        return request.getHeaders().get(HttpHeaders.CONTENT_TYPE).contains(CONTENT_TYPE_JSON);
    }
    
    public static boolean isPostRequest(ServerHttpRequest request ) {
        return HttpMethod.POST.compareTo(request.getMethod()) == 0;
    }
    
    public static boolean isPostRequest(HttpRequest request ) {
        return HttpMethod.POST.compareTo(request.getMethod()) == 0;
    }
    
	/**
	 * 2、从Flux<DataBuffer>中获取字符串的方法
	 * @return 请求体
	 */
	public static String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
		if(serverHttpRequest.getHeaders().getContentLength() == 0) {
			return org.apache.commons.lang3.StringUtils.EMPTY;
		}
		// 获取请求体
		Flux<DataBuffer> body = serverHttpRequest.getBody();
		AtomicReference<String> bodyRef = new AtomicReference<>();
		body.subscribe(buffer -> {
			CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
			DataBufferUtils.release(buffer);
			bodyRef.set(charBuffer.toString());
		});
		// 获取request body
		return bodyRef.get();
	}
	

	/**
	 * 获取请求客户端IP地址，支持代理服务器
	 * @param request {@link HttpServletRequest} 对象
	 * @return IP地址
	 */
	public static String getRemoteAddr(ServerHttpRequest request) {
		
		// 1、获取客户端IP地址，支持代理服务器
		String remoteAddr = UNKNOWN; 
		for (String xheader : xheaders) {
			remoteAddr = request.getHeaders().getFirst(xheader);
			log.debug(" {} : {} " , xheader, remoteAddr);
			if (StringUtils.hasText(remoteAddr) && !UNKNOWN.equalsIgnoreCase(remoteAddr)) {  
	            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
	            if( remoteAddr.indexOf(",") !=-1 ){
	            	remoteAddr = remoteAddr.split(",")[0];
	            }
	            break;
	        }
		}
		if (!StringUtils.hasText(remoteAddr) || UNKNOWN.equalsIgnoreCase(remoteAddr)) { 
			for (String header : headers) {
				
				remoteAddr = request.getHeaders().getFirst(header);
				log.debug(" {} : {} " , header, remoteAddr);
				
				if(StringUtils.hasText(remoteAddr) && !UNKNOWN.equalsIgnoreCase(remoteAddr)){
					break;
				}
			}
		}
		
		// 2、没有取得特定标记的值
		if (!StringUtils.hasText(remoteAddr) || UNKNOWN.equalsIgnoreCase(remoteAddr)) { 
			remoteAddr = request.getRemoteAddress().getAddress().getHostAddress();
		}
		// 3、判断是否localhost访问
		if( LOCAL_HOST.equals(remoteAddr) || LOCAL_IP6.equals(remoteAddr)){
			remoteAddr = LOCAL_IP;  
		}
		 
		return remoteAddr;
	}
	
	public static boolean isSameSegment(ServerHttpRequest request) {
        String localIp = request.getLocalAddress().getAddress().getHostAddress();
        String remoteIp = getRemoteAddr(request);
        log.info("localIp:{},remoteIp:{} url:{}", localIp, remoteIp, request.getPath().value());
        int mask = getIpV4Value("255.255.255.0");
        boolean flag = (mask & getIpV4Value(localIp)) == (mask & getIpV4Value(remoteIp));
        return flag;
    }
	
	public static int getIpV4Value(String ipOrMask) {
        byte[] addr = getIpV4Bytes(ipOrMask);
        int address1 = addr[3] & 0xFF;
        address1 |= ((addr[2] << 8) & 0xFF00);
        address1 |= ((addr[1] << 16) & 0xFF0000);
        address1 |= ((addr[0] << 24) & 0xFF000000);
        return address1;
    }

    public static byte[] getIpV4Bytes(String ipOrMask) {
        try {

            String[] addrs = ipOrMask.split("\\.");
            int length = addrs.length;
            byte[] addr = new byte[length];
            for (int index = 0; index < length; index++) {
                addr[index] = (byte) (Integer.parseInt(addrs[index]) & 0xff);
            }
            return addr;
        } catch (Exception e) {
        }
        return new byte[4];
    }
    
    public static String getDeviceId(ServerHttpRequest request ) {
        HttpHeaders headers = request.getHeaders();
    	// 1、判断是否 Apple 设备
        String deviceId = headers.getFirst(XHeaders.X_DEVICE_IDFA);
		if(!StringUtils.hasText(deviceId)) {
			deviceId = headers.getFirst(XHeaders.X_DEVICE_OAID);
		}
		if(!StringUtils.hasText(deviceId)) {
			deviceId = headers.getFirst(XHeaders.X_DEVICE_OPENUDID);
		}
		// 2、判断是否 Android 设备
		if(!StringUtils.hasText(deviceId)) {
			deviceId = headers.getFirst(XHeaders.X_DEVICE_IMEI);
		}
		if(!StringUtils.hasText(deviceId)) {
			deviceId = headers.getFirst(XHeaders.X_DEVICE_ANDROIDID);
		}
		if(!StringUtils.hasText(deviceId)) {
			deviceId = headers.getFirst(XHeaders.X_DEVICE_OAID);
		}
        return deviceId;
    }
    
}
