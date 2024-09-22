/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure.webmvc;

import io.hiwepy.boot.api.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

public abstract class ExceptinHandler {

    public static final String STATUS_FAIL = "fail";
    public static final String STATUS_ERROR = "error";

    protected static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    protected static final String XML_HTTP_REQUEST = "XMLHttpRequest";
    protected static final String X_REQUESTED_WITH = "X-Requested-With";

    protected boolean isAjaxRequest(HttpServletRequest request) {
        return XML_HTTP_REQUEST.equalsIgnoreCase(request.getHeader(X_REQUESTED_WITH));
    }

    protected void logException(Exception ex) {
        HttpServletRequest request = WebUtils.getHttpServletRequest();
        if (Objects.nonNull(request)) {
            LOG.error("URI : {} Request Fail. IP >> {} ", request.getRequestURI(), WebUtils.getRemoteAddr(request));
        }
        LOG.error(ex.getMessage(), ex);
    }

    protected void logException(Exception ex, Map<String, Object> detailMap) {
        HttpServletRequest request = WebUtils.getHttpServletRequest();
        if (Objects.nonNull(request)) {
            LOG.error("URI : {} Request Fail. IP >> {} ", request.getRequestURI(), WebUtils.getRemoteAddr(request));
        }
        for (final Map.Entry<String, Object> entry : detailMap.entrySet()) {
            Object val = entry.getValue();
            if (val instanceof String) {
                MDC.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        LOG.error(ex.getMessage(), ex);
    }

}
