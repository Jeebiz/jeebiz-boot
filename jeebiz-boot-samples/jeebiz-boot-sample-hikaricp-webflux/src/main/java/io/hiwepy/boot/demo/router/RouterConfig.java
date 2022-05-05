/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.demo.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.hiwepy.boot.demo.router.handler.TimeHandler;

@Configuration
public class RouterConfig {

    @Autowired
    private TimeHandler timeHandler;

    @Bean
    public RouterFunction<ServerResponse> timerRouter() {
        return route(GET("/time"), req -> timeHandler.getTime(req))
                .andRoute(GET("/date"), timeHandler::getDate);  // 这种方式相对于上一行更加简洁
    }

}
