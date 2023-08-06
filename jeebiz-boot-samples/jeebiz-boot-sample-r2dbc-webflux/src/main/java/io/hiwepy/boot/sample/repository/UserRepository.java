/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.sample.repository;

import io.hiwepy.boot.sample.dao.entities.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, String> {  // 1

    Mono<User> findByUsername(String username);     // 2

    Mono<Long> deleteByUsername(String username);

}
