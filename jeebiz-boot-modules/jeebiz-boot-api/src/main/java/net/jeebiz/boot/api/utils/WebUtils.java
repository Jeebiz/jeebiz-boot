package net.jeebiz.boot.api.utils;


import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * http://blog.csdn.net/caoshuming_500/article/details/20952329
 * https://www.cnblogs.com/wang1001/p/9605761.html
 * @author <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Slf4j
public class WebUtils extends org.springframework.web.util.WebUtils {
	
	private static String[] xheaders = new String[]{"X-Forwarded-For", "x-forwarded-for"};
	private static String[] headers = new String[]{"Cdn-Src-Ip", "Proxy-Client-IP", "WL-Proxy-Client-IP", "X-Real-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
	private static String LOCAL_HOST = "localhost";
	private static String LOCAL_IP6 = "0:0:0:0:0:0:0:1";
	private static String LOCAL_IP = "127.0.0.1";
	private static String UNKNOWN = "unknown";   
	
	/**
	 * 获取请求客户端IP地址，支持代理服务器
	 * @param request {@link HttpServletRequest} 对象
	 * @return IP地址
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		
		// 1、获取客户端IP地址，支持代理服务器
		String remoteAddr = UNKNOWN; 
		for (String xheader : xheaders) {
			remoteAddr = request.getHeader(xheader);
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
				
				remoteAddr = request.getHeader(header);
				log.debug(" {} : {} " , header, remoteAddr);
				
				if(StringUtils.hasText(remoteAddr) && !UNKNOWN.equalsIgnoreCase(remoteAddr)){
					break;
				}
			}
		}
		// 2、没有取得特定标记的值
		if (!StringUtils.hasText(remoteAddr) || UNKNOWN.equalsIgnoreCase(remoteAddr)) { 
			remoteAddr = request.getRemoteAddr();
		}
		// 3、判断是否localhost访问
		if( LOCAL_HOST.equals(remoteAddr) || LOCAL_IP6.equals(remoteAddr)){
			remoteAddr = LOCAL_IP;  
		}
		 
		return remoteAddr;
	}
	
	/**
	 *  获得请求的客户端信息【ip,port,name】
	 *  @param request {@link HttpServletRequest} 对象
	 *  @return 客户端信息[ip,port,name]
	 */
	public static String[] getRemoteInfo(HttpServletRequest request) {
		if (request == null) {
			return new String[] { "", "", "" };
		}
		return new String[] { getRemoteAddr(request), request.getRemotePort() + "", request.getRemoteHost()};
	}
	
	
	 
	
}
