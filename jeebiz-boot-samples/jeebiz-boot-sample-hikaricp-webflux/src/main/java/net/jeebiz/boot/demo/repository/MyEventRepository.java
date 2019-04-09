/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.demo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import net.jeebiz.boot.demo.dao.entities.MyEvent;
import reactor.core.publisher.Flux;

/**
 * https://blog.51cto.com/liukang/2090198
 */
public interface MyEventRepository extends ReactiveMongoRepository<MyEvent, Long> { // 1
	
	 @Tailable   // 1
     Flux<MyEvent> findBy(); // 2
	 
}