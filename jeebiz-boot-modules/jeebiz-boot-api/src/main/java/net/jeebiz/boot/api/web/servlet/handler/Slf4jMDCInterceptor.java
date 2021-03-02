package net.jeebiz.boot.api.web.servlet.handler;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.biz.utils.WebUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class Slf4jMDCInterceptor implements HandlerInterceptor {
	/**
	 * 会话ID
	 */
	private final static String SESSION_KEY = "sessionId";

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)

			throws Exception {
		
		// 设置SessionId
        String token = UUID.randomUUID().toString().replace("-", "");
        MDC.put(SESSION_KEY, token);
		MDC.put("uuid", UUID.randomUUID().toString()); // Add the fishtag;
		MDC.put("requestURL", request.getRequestURL().toString());
		MDC.put("requestURI", request.getRequestURI());
		MDC.put("queryString", request.getQueryString());
		MDC.put("remoteAddr", WebUtils.getRemoteAddr(request));
		MDC.put("remoteHost", request.getRemoteHost());
		MDC.put("remotePort", String.valueOf(request.getRemotePort()));
		MDC.put("localAddr", request.getLocalAddr());
		MDC.put("localName", request.getLocalName());

		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception exception) throws Exception {
		// 删除SessionId
		MDC.remove(SESSION_KEY);
	}

}
