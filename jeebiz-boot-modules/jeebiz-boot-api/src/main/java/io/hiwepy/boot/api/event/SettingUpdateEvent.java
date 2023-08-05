package io.hiwepy.boot.api.event;

import org.springframework.biz.context.event.EnhancedEvent;

import java.util.Map;

/**
 * 系统参数更新事件
 */
@SuppressWarnings("serial")
public class SettingUpdateEvent extends EnhancedEvent<Map<String, String>> {

    public SettingUpdateEvent(Object source, Map<String, String> props) {
        super(source, props);
    }

}
