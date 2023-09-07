/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure;

import io.hiwepy.boot.api.sequence.Sequence;
import io.hiwepy.boot.autoconfigure.config.SequenceProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Sequence.class)
@EnableConfigurationProperties(SequenceProperties.class)
public class DefaultSequenceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Sequence sequence(SequenceProperties properties) {
        long workerId = Objects.isNull(properties.getWorkerId()) ? 0L : properties.getWorkerId();
        long dataCenterId = Objects.isNull(properties.getDataCenterId()) ? 0L : properties.getDataCenterId();
        long timeOffset = Objects.isNull(properties.getTimeOffset()) ? 5L : properties.getTimeOffset();
        long randomSequenceLimit = Objects.isNull(properties.getRandomSequenceLimit()) ? 0L : properties.getRandomSequenceLimit();
        return new Sequence(workerId, dataCenterId, properties.isUseSystemClock(), timeOffset, randomSequenceLimit);
    }

}
