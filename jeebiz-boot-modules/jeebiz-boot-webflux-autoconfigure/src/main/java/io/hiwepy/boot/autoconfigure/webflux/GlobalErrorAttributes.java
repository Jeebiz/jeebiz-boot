/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure.webflux;

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.rmi.ServerException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        return assembleError(request);
    }

    private Map<String, Object> assembleError(ServerRequest request) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        Throwable error = getError(request);
        if (error instanceof ServerException) {
            //errorAttributes.put("code", ((ServerException) error).getCode());
            errorAttributes.put("data", error.getMessage());
        } else {
            errorAttributes.put("code", HttpStatus.INTERNAL_SERVER_ERROR);
            errorAttributes.put("data", "INTERNAL SERVER ERROR");
        }
        return errorAttributes;
    }
}
