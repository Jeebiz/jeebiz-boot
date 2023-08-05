/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.demo;

import java.net.URL;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.ImmutableMap;

@SpringBootTest(classes = DemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoApplication_Test {


    /**
     * @LocalServerPort 提供了 @Value("${local.server.port}") 的代替
     */
    @LocalServerPort
    private int port;
    private URL base;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public void setUp() throws Exception {
        String url = String.format("http://localhost:%d/", port);
        System.out.println(String.format("port is : [%d]", port));
        this.base = new URL(url);
    }

    /**
     * 向"/test"地址发送请求，并打印返回结果
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {

        Map<String, Object> requestBody = new ImmutableMap.Builder<String, Object>()
                .put("name", 1)
                .put("text", 60).build();

        ResponseEntity<String> response = this.restTemplate.postForEntity(
                this.base.toString() + "/demo/new", requestBody, String.class);
        System.out.println(String.format("测试结果为：%s", response.getBody()));
    }

}
