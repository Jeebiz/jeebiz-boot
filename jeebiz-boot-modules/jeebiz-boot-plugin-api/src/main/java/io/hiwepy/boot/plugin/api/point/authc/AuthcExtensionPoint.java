package io.hiwepy.boot.plugin.api.point.authc;

import org.pf4j.ExtensionPoint;
import org.pf4j.PluginRuntimeException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface AuthcExtensionPoint extends ExtensionPoint {

    String getToken(HttpServletRequest request, Map<String, Object> params) throws PluginRuntimeException;

    void handleHeader(HttpServletRequest request, Map<String, Object> params) throws PluginRuntimeException;

    void handleRequest(HttpServletRequest request, Map<String, Object> params) throws PluginRuntimeException;

    Object handleResult(Object res) throws PluginRuntimeException;

}
