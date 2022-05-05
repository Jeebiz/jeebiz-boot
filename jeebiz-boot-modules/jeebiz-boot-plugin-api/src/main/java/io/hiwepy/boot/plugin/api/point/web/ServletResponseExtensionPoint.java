package io.hiwepy.boot.plugin.api.point.web;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.pf4j.ExtensionPoint;
import org.springframework.http.ResponseEntity;

public interface ServletResponseExtensionPoint extends ExtensionPoint  {

	String dewrap(HttpServletResponse response, Map<String, Object> realParams);

	<T> ResponseEntity<T> dewrap(ResponseEntity<T> responseEntity);

}
