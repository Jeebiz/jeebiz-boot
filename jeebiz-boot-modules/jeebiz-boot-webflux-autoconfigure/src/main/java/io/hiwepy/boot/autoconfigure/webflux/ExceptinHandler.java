/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure.webflux;

import io.hiwepy.boot.api.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
        LOG.error(Constants.bizMarker, ex.getMessage(), ex);
    }

    protected void logException(Exception ex, Map<String, Object> detailMap) {

        for (final Map.Entry<String, Object> entry : detailMap.entrySet()) {
            Object val = entry.getValue();
            if (val instanceof String) {
                MDC.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        LOG.error(Constants.bizMarker, ex.getMessage(), ex);
    }

    protected void logException(Exception ex, String code) {

        MDC.put("clazz", ex.getClass().getName());
        MDC.put("type", ex.getClass().getSimpleName());
        MDC.put("code", code);
        MDC.put("msg", ex.getClass().getSimpleName());

        // 自身类.class.isAssignableFrom(自身类或子类.class)
        // Exception.class.isAssignableFrom(ex.getClass())

        LOG.error(Constants.bizMarker, ex.getMessage(), ex);
    }

}

