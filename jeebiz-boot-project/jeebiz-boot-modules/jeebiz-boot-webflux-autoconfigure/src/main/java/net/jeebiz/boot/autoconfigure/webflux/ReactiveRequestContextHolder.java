/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure.webflux;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.server.ServerResponse.Context;

import reactor.core.publisher.Mono;

/**
 * ReactiveRequestContextHolder
 *
 * @author L.cm
 */
public class ReactiveRequestContextHolder {
	
    static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

    /**
     * Gets the {@code Mono<ServerHttpRequest>} from Reactor {@link Context}
     * @return the {@code Mono<ServerHttpRequest>}
     */
    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.subscriberContext()
            .map(ctx -> ctx.get(CONTEXT_KEY));
    }

}