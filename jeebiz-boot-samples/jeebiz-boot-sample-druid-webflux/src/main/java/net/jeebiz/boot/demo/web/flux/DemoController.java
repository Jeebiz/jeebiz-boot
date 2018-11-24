/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.demo.web.flux;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.jeebiz.boot.demo.dao.entities.DemoModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class DemoController {
 
    @GetMapping("/{user}")
    public Mono<DemoModel> getUser(@PathVariable Long user) {
        // ...
    	return null;
    }
    
    @GetMapping("/{user}/customers")
    public Flux<DemoModel> getUserCustomers(@PathVariable Long user) {
        // ...
    	return null;
    }
 
    @DeleteMapping("/{user}")
    public Mono<DemoModel> deleteUser(@PathVariable Long user) {
        // ...
    	return null;
    }
 
}