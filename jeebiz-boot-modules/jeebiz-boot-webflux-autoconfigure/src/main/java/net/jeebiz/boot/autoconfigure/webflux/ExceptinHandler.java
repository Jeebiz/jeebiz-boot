/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure.webflux;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jeebiz.boot.api.Constants;

public abstract class ExceptinHandler {

	public static final String STATUS_FAIL = "fail";
	public static final String STATUS_ERROR = "error";

	protected static final Logger LOG = LoggerFactory.getLogger(DefaultExceptinHandler.class);
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
				ThreadContext.put(entry.getKey(), String.valueOf(entry.getValue()));
			}
		}

		LOG.error(Constants.bizMarker, ex.getMessage(), ex);
	}

	protected void logException(Exception ex, String code) {

		ThreadContext.put("clazz", ex.getClass().getName());
		ThreadContext.put("type", ex.getClass().getSimpleName());
		ThreadContext.put("code", code);
		ThreadContext.put("msg", ex.getClass().getSimpleName());

		// 自身类.class.isAssignableFrom(自身类或子类.class)
		// Exception.class.isAssignableFrom(ex.getClass())

		LOG.error(Constants.bizMarker, ex.getMessage(), ex);
	}

}
