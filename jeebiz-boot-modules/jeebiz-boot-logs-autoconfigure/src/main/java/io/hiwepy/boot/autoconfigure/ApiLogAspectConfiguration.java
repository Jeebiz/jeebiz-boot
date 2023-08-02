/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure;

import io.hiwepy.boot.autoconfigure.aspect.ApiOperationLogProvider;
import io.hiwepy.boot.autoconfigure.aspect.DefaultApiOperationLogProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiLogAspectConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ApiOperationLogProvider apiOperationLogProvider() {
        return new DefaultApiOperationLogProvider();
    }

}
