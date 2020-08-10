/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure.webflux;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * https://developer.aliyun.com/article/696829
 * @author <a href="https://github.com/vindell">wandl</a>
 * @since 2020-08-07
 */
/**
 * ReactiveRequestContextFilter
 *
 * @author L.cm
 */
public class ReactiveRequestContextFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        return chain.filter(exchange)
            .subscriberContext(ctx -> ctx.put(ReactiveRequestContextHolder.CONTEXT_KEY, request));
    }
    
}