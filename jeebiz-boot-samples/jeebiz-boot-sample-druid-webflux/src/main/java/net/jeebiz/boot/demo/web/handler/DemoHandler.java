/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.demo.web.handler;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.google.common.collect.Lists;

import net.jeebiz.boot.demo.web.vo.DemoVo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 类似于Controller，处理用户请求的真实逻辑
 */
@Component
public class DemoHandler {

	Logger log = LoggerFactory.getLogger(getClass());

	public Mono<ServerResponse> getDemoList(ServerRequest request) { // Lambda 匿名参数
		List<DemoVo> demoList = Lists.newArrayList();
		Flux<DemoVo> userFlux = Flux.fromIterable(demoList);
		userFlux.subscribe(user -> log.info(user.toString()));
		return ServerResponse.ok().body(userFlux, DemoVo.class);
	}

	public Mono<ServerResponse> getDemo(ServerRequest request) {
		
		String userId = request.pathVariable("userId");
		DemoVo data = new DemoVo();
		Mono<DemoVo> userMono = Mono.just(data);

		userMono.subscribe(user -> log.info(user.toString()));
		return ServerResponse.ok().body(userMono, DemoVo.class);
	}

	public Mono<ServerResponse> getDemos(ServerRequest request) {
		DemoVo studentBody = new DemoVo();
		request.bodyToMono(DemoVo.class).subscribe(student -> BeanUtils.copyProperties(student, studentBody));
		return ok().contentType(APPLICATION_JSON_UTF8).body(fromObject(studentBody));
	}

	public Mono<ServerResponse> newDemo(ServerRequest request) {
		return ok().contentType(TEXT_PLAIN).body(fromObject("success"));
	}

}