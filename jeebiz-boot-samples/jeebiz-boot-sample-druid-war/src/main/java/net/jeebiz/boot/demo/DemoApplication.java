package net.jeebiz.boot.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableCaching(proxyTargetClass = true)
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class DemoApplication {

	public static void main(String[] args) throws Exception {
		 SpringApplication.run(DemoApplication.class, args);
	}

}
