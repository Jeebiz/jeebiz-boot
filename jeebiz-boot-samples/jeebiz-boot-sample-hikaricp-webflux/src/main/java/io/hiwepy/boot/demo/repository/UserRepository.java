/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.demo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import io.hiwepy.boot.demo.dao.entities.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, String> {  // 1

    Mono<User> findByUsername(String username);     // 2

    Mono<Long> deleteByUsername(String username);

}
