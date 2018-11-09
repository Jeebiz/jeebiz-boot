package net.jeebiz.boot.autoconfigure;

import org.springframework.biz.context.SpringContextAwareContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"net.jeebiz.**.setup", "net.jeebiz.**.service", "net.jeebiz.**.aspect", "net.jeebiz.**.task"})
public class JeebizServiceConfiguration {
	
	@Bean
	public SpringContextAwareContext springContextAwareContext() {
		return new SpringContextAwareContext();
	}
	
}


