/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.webmvc;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.biz.context.NestedMessageSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import net.jeebiz.boot.api.exception.ErrorResponse;
import net.jeebiz.boot.api.exception.PayloadExceptionEvent;
import net.jeebiz.boot.api.utils.ResultUtils;

public class BaseController implements ApplicationEventPublisherAware, ApplicationContextAware, EmbeddedValueResolverAware {

	protected static final String ERROR_VIEW = "/exception/500";

	protected static final String STATUS_SUCCESS = ResultUtils.STATUS_SUCCESS;
	protected static final String STATUS_FAIL = ResultUtils.STATUS_FAIL;
	protected static final String STATUS_ERROR = ResultUtils.STATUS_ERROR;

	private StringValueResolver valueResolver;
	private ApplicationEventPublisher eventPublisher;
	private ApplicationContext context;
	@Autowired
	private NestedMessageSource messageSource;
	
	/**
	 * 统一处理异常，并抛出异常事件方便进行统一的日志实现
	 */
	protected void logException(Object source, Exception ex) {
		getEventPublisher().publishEvent(new PayloadExceptionEvent(source, ex));
	}

	/**
	 * 获取国际化信息
	 * @param key 国际化Key
	 * @param args 参数
	 * @return 国际化字符串
	 */
	protected String getMessage(String key, Object... args) {
		//两个方法在没有使用JSF的项目中是没有区别的
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		//				                      RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		//HttpServletResponse response = ((ServletRequestAttributes)requestAttributes).getResponse();
		return getMessageSource().getMessage(key, args, RequestContextUtils.getLocale(request));
	}

	protected ErrorResponse success(String key, Object... args) {
		return ErrorResponse.success(getMessage(key, args));
	}

	protected ErrorResponse fail(String key, Object... args) {
		return ErrorResponse.fail(getMessage(key, args));
	}

	protected ErrorResponse error(String key, Object... args) {
		return ErrorResponse.error(getMessage(key, args));
	}
	
	public StringValueResolver getValueResolver() {
		return valueResolver;
	}

	public void setValueResolver(StringValueResolver valueResolver) {
		this.valueResolver = valueResolver;
	}

	public ApplicationEventPublisher getEventPublisher() {
		return eventPublisher;
	}

	public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public NestedMessageSource getMessageSource() {
		return messageSource;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}
	
	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.valueResolver = resolver;
	}

}
