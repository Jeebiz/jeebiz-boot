package io.hiwepy.boot.api.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.biz.utils.WebUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import io.hiwepy.boot.api.XHeaders;
import io.hiwepy.boot.api.sequence.Sequence;

public class Slf4jMDCInterceptor implements HandlerInterceptor {

	private final Sequence sequence;

	public Slf4jMDCInterceptor(Sequence sequence) {
		this.sequence = sequence;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)

			throws Exception {

        MDC.put("requestId", StringUtils.defaultString(request.getHeader(XHeaders.X_REQUEST_ID), sequence.nextId().toString()));
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

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
						   ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
								Exception exception) throws Exception {
		MDC.clear();
	}

}
