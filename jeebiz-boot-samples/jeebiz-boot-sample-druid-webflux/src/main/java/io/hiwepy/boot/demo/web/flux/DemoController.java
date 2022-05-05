/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.demo.web.flux;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.hiwepy.boot.demo.dao.entities.DemoEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class DemoController {

    @GetMapping("/{user}")
    public Mono<DemoEntity> getUser(@PathVariable Long user) {
        // ...
    	return null;
    }

    @GetMapping("/{user}/customers")
    public Flux<DemoEntity> getUserCustomers(@PathVariable Long user) {
        // ...
    	return null;
    }

    @DeleteMapping("/{user}")
    public Mono<DemoEntity> deleteUser(@PathVariable Long user) {
        // ...
    	return null;
    }

}
