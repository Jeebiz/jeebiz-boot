package io.hiwepy.boot.autoconfigure;

import io.hiwepy.boot.autoconfigure.datascope.DataScopeProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.biz.context.SpringContextAwareContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration(proxyBeanMethods = false)
public class DataScopeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DataScopeProvider dataScopeProvider() {
        return new DataScopeProvider(){};
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringContextAwareContext springContextAwareContext() {
        return new SpringContextAwareContext();
    }

}
