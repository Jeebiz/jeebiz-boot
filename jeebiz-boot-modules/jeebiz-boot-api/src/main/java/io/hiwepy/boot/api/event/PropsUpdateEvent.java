package io.hiwepy.boot.api.event;

import java.util.Properties;

import org.springframework.biz.context.event.EnhancedEvent;

@SuppressWarnings("serial")
public class PropsUpdateEvent extends EnhancedEvent<Properties> {
	
	public PropsUpdateEvent(Object source, Properties props) {
		super(source, props);
	}
	
}
