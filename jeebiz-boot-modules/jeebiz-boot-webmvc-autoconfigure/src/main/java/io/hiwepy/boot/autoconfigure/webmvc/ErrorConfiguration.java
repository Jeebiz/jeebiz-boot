/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.autoconfigure.webmvc;

import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.stereotype.Component;

@Component
public class ErrorConfiguration implements ErrorPageRegistrar {
	
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        /*ErrorPage[] errorPages = new ErrorPage[]{
                new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"),
                new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500"),
                new ErrorPage(Throwable.class, "/error/500")
        };
        registry.addErrorPages(errorPages);*/
    }
}
