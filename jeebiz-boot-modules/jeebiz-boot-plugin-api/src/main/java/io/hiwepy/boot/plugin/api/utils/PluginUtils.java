package io.hiwepy.boot.plugin.api.utils;

import io.hiwepy.boot.plugin.api.annotation.ExtensionMapping;
import io.hiwepy.boot.plugin.api.exception.PluginInvokeException;
import io.hiwepy.boot.plugin.api.point.web.ServletRequestExtensionPoint;
import io.hiwepy.boot.plugin.api.point.web.ServletResponseExtensionPoint;
import org.pf4j.PluginManager;
import org.springframework.util.StringUtils;

import java.util.List;

public class PluginUtils {

    public static <T> T getExtensionPoint(PluginManager pluginManager, Class<T> type, String pluginId,
                                          String extensionId) throws PluginInvokeException {
        if (StringUtils.hasText(pluginId) && StringUtils.hasText(extensionId)) {
            List<T> extensions = pluginManager.getExtensions(type, pluginId);
            for (T extension : extensions) {
                ExtensionMapping em = extension.getClass().getAnnotation(ExtensionMapping.class);
                if (StringUtils.hasText(em.id()) && em.id().equals(extensionId)) {
                    return extension;
                }
            }
        }
        return null;
    }

    public static ServletRequestExtensionPoint getRequestExtensionPoint(PluginManager pluginManager, String pluginId,
                                                                        String extensionId) throws PluginInvokeException {
        return getExtensionPoint(pluginManager, ServletRequestExtensionPoint.class, pluginId, extensionId);
    }

    public static ServletResponseExtensionPoint getResponseExtensionPoint(PluginManager pluginManager, String pluginId,
                                                                          String extensionId) throws PluginInvokeException {
        return getExtensionPoint(pluginManager, ServletResponseExtensionPoint.class, pluginId, extensionId);
    }

}
