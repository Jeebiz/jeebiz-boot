package io.hiwepy.boot.api.utils;

import io.hiwepy.boot.api.XHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.biz.utils.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class WebUtils extends org.springframework.biz.utils.WebUtils {

	public static String getDeviceId(HttpServletRequest request) {
    	// 1、判断是否 Apple 设备
        String deviceId = request.getHeader(XHeaders.X_DEVICE_IDFA);
		if(!StringUtils.hasText(deviceId)) {
			deviceId = request.getHeader(XHeaders.X_DEVICE_OAID);
		}
		if(!StringUtils.hasText(deviceId)) {
			deviceId = request.getHeader(XHeaders.X_DEVICE_OPENUDID);
		}
		// 2、判断是否 Android 设备
		if(!StringUtils.hasText(deviceId)) {
			deviceId = request.getHeader(XHeaders.X_DEVICE_IMEI);
		}
		if(!StringUtils.hasText(deviceId)) {
			deviceId = request.getHeader(XHeaders.X_DEVICE_ANDROIDID);
		}
		if(!StringUtils.hasText(deviceId)) {
			deviceId = request.getHeader(XHeaders.X_DEVICE_OAID);
		}
        return deviceId;
    }

	public static HttpServletRequest getHttpServletRequest() {
        try {
        	RequestAttributes requestAttributes = getRequestAttributesSafely();
        	if (requestAttributes != null) {
        		return ((ServletRequestAttributes) requestAttributes).getRequest();
        	}
        } catch (Exception e) {
           log.error(e.getMessage());
        }
        return null;
    }
    
    public static RequestAttributes getRequestAttributesSafely(){
        RequestAttributes requestAttributes = null;
        try{
            requestAttributes = RequestContextHolder.currentRequestAttributes();
        } catch (IllegalStateException e){
            
        }
        return requestAttributes;
    }
    
}
