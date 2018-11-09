/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.jeebiz.boot.datax.setup.config;

import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  
import org.springframework.retry.RetryCallback;  
import org.springframework.retry.RetryContext;  
import org.springframework.retry.RetryListener;  
  
public class RetryFailuireItemListener implements RetryListener{  
      
    private static final Logger logger = LoggerFactory.getLogger(RetryFailuireItemListener.class);  
  
    @Override  
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {  
        return true;  
    }  
  
    @Override  
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,  
            Throwable throwable) {  
    }  
  
    @Override  
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,  
            Throwable throwable) {  
        logger.error("【重试异常】："+throwable.getMessage());  
    }  
  
}  