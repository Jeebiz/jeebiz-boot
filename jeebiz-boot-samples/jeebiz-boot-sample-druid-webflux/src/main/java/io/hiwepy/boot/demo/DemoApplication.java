package io.hiwepy.boot.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import io.micrometer.core.instrument.MeterRegistry;
import io.hiwepy.boot.api.sequence.Sequence;
import io.hiwepy.boot.autoconfigure.EnableExtrasConfiguration;

/**
 * 基于Reactor Netty实现WebFlux服务
 */
@EnableExtrasConfiguration
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }

    @Bean
    public Sequence sequence() {
        return new Sequence(0L);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.err.println("Spring Boot Application（Jeebiz-Demo） Started !");
    }

}
