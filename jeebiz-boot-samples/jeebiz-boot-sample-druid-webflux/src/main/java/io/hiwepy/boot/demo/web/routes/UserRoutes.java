/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.demo.web.routes;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.hiwepy.boot.demo.web.handler.DemoHandler;

@Configuration
public class UserRoutes {

	@Bean
	public RouterFunction<ServerResponse> monoRouterFunction(DemoHandler demoHandler) {
		return RouterFunctions
				.route(RequestPredicates.GET("/{user}").and(RequestPredicates.accept(APPLICATION_JSON)),
						demoHandler::getDemo)
				.andRoute(RequestPredicates.GET("/{user}/customers").and(RequestPredicates.accept(APPLICATION_JSON)),
						demoHandler::getDemoList)
				.andRoute(RequestPredicates.DELETE("/{user}").and(RequestPredicates.accept(APPLICATION_JSON)),
						demoHandler::newDemo);
	}

	@Bean
	@Autowired
	public RouterFunction<ServerResponse> routersFunction(DemoHandler demoHandler) {
		return RouterFunctions.route(RequestPredicates.GET("/api/users"), demoHandler::getDemoList)
				.and(RouterFunctions.route(RequestPredicates.GET("/api/user/{userId}"), demoHandler::getDemo));
	}

}
