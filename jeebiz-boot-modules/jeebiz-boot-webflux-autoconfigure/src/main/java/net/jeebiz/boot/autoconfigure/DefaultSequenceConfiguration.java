/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package net.jeebiz.boot.autoconfigure;

import java.util.Objects;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.jeebiz.boot.api.sequence.Sequence;
import net.jeebiz.boot.autoconfigure.config.SequenceProperties;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Sequence.class)
@EnableConfigurationProperties(SequenceProperties.class)
public class DefaultSequenceConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Sequence sequence(SequenceProperties properties) {

    	long dataCenterId = Objects.isNull(properties.getDataCenterId()) ? 0L : properties.getDataCenterId();
    	long workerId = Objects.isNull(properties.getWorkerId()) ? 0x000000FF & Sequence.getLastIPAddress() : properties.getWorkerId();

        return new Sequence(dataCenterId, workerId, properties.isClock(), properties.getTimeOffset(), properties.isRandomSequence());
    }

}
