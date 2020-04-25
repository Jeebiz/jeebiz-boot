package net.jeebiz.boot.plugin.api.point.authc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.pf4j.PluginRuntimeException;

public class AuthcExtensionPointAdepter implements AuthcExtensionPoint{

	@Override
	public String getToken(HttpServletRequest request, Map<String, Object> params) throws PluginRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleHeader(HttpServletRequest request, Map<String, Object> params) throws PluginRuntimeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleRequest(HttpServletRequest request, Map<String, Object> params) throws PluginRuntimeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object handleResult(Object res) throws PluginRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

}
