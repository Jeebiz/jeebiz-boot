/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure;

import org.springframework.biz.context.SpringContextAwareContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ComponentScan({"io.hiwepy.**.api", "io.hiwepy.**.feign", "io.hiwepy.**.setup", "io.hiwepy.**.service", "io.hiwepy.**.aspect", "io.hiwepy.**.task", "io.hiwepy.**.strategy", "io.hiwepy.**.extras"})
public class DefaultSpringContextConfiguration {

    @Bean
    public SpringContextAwareContext springContextAwareContext() {
        return new SpringContextAwareContext();
    }

}


