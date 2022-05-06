/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.demo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import io.hiwepy.boot.demo.dao.entities.MyEvent;
import reactor.core.publisher.Flux;

/**
 * https://blog.51cto.com/liukang/2090198
 */
public interface MyEventRepository extends ReactiveMongoRepository<MyEvent, Long> { // 1
	
	 @Tailable   // 1
     Flux<MyEvent> findBy(); // 2
	 
}