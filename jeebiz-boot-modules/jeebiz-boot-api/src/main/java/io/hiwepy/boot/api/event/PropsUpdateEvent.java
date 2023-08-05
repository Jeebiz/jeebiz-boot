package io.hiwepy.boot.api.event;

import org.springframework.biz.context.event.EnhancedEvent;

import java.util.Properties;

@SuppressWarnings("serial")
public class PropsUpdateEvent extends EnhancedEvent<Properties> {

    public PropsUpdateEvent(Object source, Properties props) {
        super(source, props);
    }

}
