package io.hiwepy.boot.plugin.api.point.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.pf4j.ExtensionPoint;
import org.springframework.http.RequestEntity;

public interface ServletRequestExtensionPoint extends ExtensionPoint {

	String wrap(HttpServletRequest request, Map<String, Object> realParams);

	<T> RequestEntity<T> wrap(RequestEntity<T> requestEntity);

}
