/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.web;

import io.hiwepy.boot.api.ApiRestResponse;
import io.hiwepy.boot.api.exception.PayloadExceptionEvent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.biz.context.NestedMessageSource;
import org.springframework.context.*;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringValueResolver;

public class BaseController implements ApplicationEventPublisherAware, ApplicationContextAware, EmbeddedValueResolverAware {

    protected static final String STATUS_SUCCESS = "success";
    protected static final String STATUS_FAIL = "fail";
    protected static final String STATUS_ERROR = "error";

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
     *
     * @param key  国际化Key
     * @param args 参数
     * @return 国际化字符串
     */
    protected String getMessage(String key, Object... args) {
        return getMessageSource().getMessage(key, args, LocaleContextHolder.getLocale());
    }

    protected <T> ApiRestResponse<T> success(String key, Object... args) {
        return ApiRestResponse.success(getMessage(key, args));
    }

    protected <T> ApiRestResponse<T> fail(String key, Object... args) {
        return ApiRestResponse.fail(getMessage(key, args));
    }

    protected <T> ApiRestResponse<T> error(String key, Object... args) {
        return ApiRestResponse.error(getMessage(key, args));
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
