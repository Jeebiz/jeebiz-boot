package net.jeebiz.boot.demo.setup.listener;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

/**
 * 
 * @className	： ApplicationEnvironmentPreparedEventListener
 * @description	： Spring Boot 配置环境事件监听 
 * ApplicationEnvironmentPreparedEvent：Spring Boot 对应Enviroment已经准备完毕，但此时上下文context还没有创建。
 * @author 		： <a href="https://github.com/wandl">wandl</a>
 * @date		： 2017年11月10日 下午4:55:22
 * @version 	V1.0
 */
public class ApplicationEnvironmentPreparedEventListener implements
                                                          ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    private Logger logger = LoggerFactory.getLogger(ApplicationEnvironmentPreparedEventListener.class);

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {

        ConfigurableEnvironment envi = event.getEnvironment();
        MutablePropertySources mps = envi.getPropertySources();
        if (mps != null) {
            Iterator<PropertySource<?>> iter = mps.iterator();
            while (iter.hasNext()) {
                PropertySource<?> ps = iter.next();
                logger
                    .info("ps.getName:{};ps.getSource:{};ps.getClass:{}", ps.getName(), ps.getSource(), ps.getClass());
            }
        }
    }

}
