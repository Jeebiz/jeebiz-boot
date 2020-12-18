package net.jeebiz.boot.plugin.api.point.authc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.pf4j.ExtensionPoint;
import org.pf4j.PluginRuntimeException;

public interface AuthcExtensionPoint extends ExtensionPoint {

	String getToken(HttpServletRequest request, Map<String, Object> params) throws PluginRuntimeException;

	void handleHeader(HttpServletRequest request, Map<String, Object> params) throws PluginRuntimeException;

	void handleRequest(HttpServletRequest request, Map<String, Object> params) throws PluginRuntimeException;

	Object handleResult(Object res) throws PluginRuntimeException;

}
