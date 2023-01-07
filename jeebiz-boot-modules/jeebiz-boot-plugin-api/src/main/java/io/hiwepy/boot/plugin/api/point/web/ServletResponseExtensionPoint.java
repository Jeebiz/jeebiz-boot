package io.hiwepy.boot.plugin.api.point.web;

import org.pf4j.ExtensionPoint;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface ServletResponseExtensionPoint extends ExtensionPoint  {
	
	String dewrap(HttpServletResponse response, Map<String, Object> realParams);
	
	<T> ResponseEntity<T> dewrap(ResponseEntity<T> responseEntity);
	
}
