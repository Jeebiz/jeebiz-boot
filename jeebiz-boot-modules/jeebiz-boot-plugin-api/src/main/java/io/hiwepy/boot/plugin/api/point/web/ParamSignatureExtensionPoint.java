package io.hiwepy.boot.plugin.api.point.web;

import jakarta.servlet.http.HttpServletRequest;
import org.pf4j.ExtensionPoint;
import org.pf4j.PluginRuntimeException;

import java.util.Map;

public interface ParamSignatureExtensionPoint extends ExtensionPoint {

    void sign(HttpServletRequest request, Map<String, Object> params) throws PluginRuntimeException;

    String getAppkey(String appid);

    String getAppSecret(String appid);

}
