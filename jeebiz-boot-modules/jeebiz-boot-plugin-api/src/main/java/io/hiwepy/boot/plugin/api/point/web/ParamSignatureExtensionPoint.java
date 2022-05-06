package io.hiwepy.boot.plugin.api.point.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.pf4j.ExtensionPoint;
import org.pf4j.PluginRuntimeException;

public interface ParamSignatureExtensionPoint extends ExtensionPoint {
	 
	void sign(HttpServletRequest request, Map<String, Object> params) throws PluginRuntimeException;
	
	String getAppkey(String appid);
	
	String getAppSecret(String appid);
	
}
