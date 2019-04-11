package net.jeebiz.boot.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.jeebiz.boot.demo.dao.entities.MyEvent;

@SpringBootApplication
//@EnableSwagger2Doc
@EnableTransactionManagement
public class DemoApplication {

	public static void main(String[] args) throws Exception {
		 SpringApplication.run(DemoApplication.class, args);
	}
	
	@Bean   // 1
    public CommandLineRunner initData(MongoOperations mongo) {  // 2
        return (String... args) -> {    // 3
            mongo.dropCollection(MyEvent.class);    // 4
            mongo.createCollection(MyEvent.class, CollectionOptions.empty().size(200).capped()); // 5
        };
    }
	

}
