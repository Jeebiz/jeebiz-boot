package net.jeebiz.boot.demo;

import javax.sql.DataSource;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.micrometer.core.instrument.MeterRegistry;
import net.jeebiz.boot.api.sequence.Sequence;

@EnableAutoConfiguration
@EnableCaching(proxyTargetClass = true)
@EnableDubbo
@EnableScheduling
@EnableTransactionManagement
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
