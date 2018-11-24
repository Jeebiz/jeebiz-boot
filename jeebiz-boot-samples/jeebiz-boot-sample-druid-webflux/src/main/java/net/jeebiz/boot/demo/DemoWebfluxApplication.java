package net.jeebiz.boot.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 基于Reactor Netty实现WebFlux服务
 */
@EnableAutoConfiguration
@SpringBootApplication
public class DemoWebfluxApplication implements CommandLineRunner {

	public static void main(String[] args) throws Exception {
		 SpringApplication.run(DemoWebfluxApplication.class, args);
		 
		/*AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(WebFluxConfig.class);
        //通过ApplicationContext创建HttpHandler
        HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(applicationContext).build();
        ReactorHttpHandlerAdapter httpHandlerAdapter = new ReactorHttpHandlerAdapter(httpHandler);
        HttpServer.create("localhost",8080).newHandler(httpHandlerAdapter).block();
        System.in.read();*/
		 
	}

	@Override
	public void run(String... args) throws Exception {
		System.err.println("Spring Boot Application（Jeebiz-Demo） Started !");
	}
	
}
