package net.jeebiz.boot.api.web.servlet.handler;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.biz.utils.RemoteAddrUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import net.jeebiz.boot.api.XHeaders;
import net.jeebiz.boot.api.sequence.Sequence;

public class Slf4jMDCInterceptor implements HandlerInterceptor {

	private final Sequence sequence;
	
	public Slf4jMDCInterceptor(Sequence sequence) {
		this.sequence = sequence;
	}
	
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)

			throws Exception {
        
        MDC.put("requestId", StringUtils.defaultString(request.getHeader(XHeaders.X_REQUEST_ID), sequence.nextId().toString()));
		MDC.put("requestURL", request.getRequestURL().toString());
		MDC.put("requestURI", request.getRequestURI());
		MDC.put("queryString", request.getQueryString());
		MDC.put("remoteAddr", RemoteAddrUtils.getRemoteAddr(request));
		MDC.put("remoteHost", request.getRemoteHost());
		MDC.put("remotePort", String.valueOf(request.getRemotePort()));
		MDC.put("localAddr", request.getLocalAddr());
		MDC.put("localName", request.getLocalName());
		
		Enumeration<String> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String key = names.nextElement();
			MDC.put("header." + key, request.getHeader(key));
		}
		
		Enumeration<String> params = request.getParameterNames();
		while (params.hasMoreElements()) {
			String key = params.nextElement();
			MDC.put("param." + key, request.getParameter(key));
		}
		
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exception) throws Exception {
		//MDC.remove(SESSION_KEY);
	}

}
