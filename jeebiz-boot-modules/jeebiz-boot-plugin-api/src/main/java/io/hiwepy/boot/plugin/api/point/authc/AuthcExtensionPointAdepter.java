package io.hiwepy.boot.plugin.api.point.authc;

import org.pf4j.PluginRuntimeException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
