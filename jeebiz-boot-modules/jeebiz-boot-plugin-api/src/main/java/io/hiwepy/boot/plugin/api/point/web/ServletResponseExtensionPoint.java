package io.hiwepy.boot.plugin.api.point.web;

import jakarta.servlet.http.HttpServletResponse;
import org.pf4j.ExtensionPoint;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ServletResponseExtensionPoint extends ExtensionPoint {

    String dewrap(HttpServletResponse response, Map<String, Object> realParams);

    <T> ResponseEntity<T> dewrap(ResponseEntity<T> responseEntity);

}
