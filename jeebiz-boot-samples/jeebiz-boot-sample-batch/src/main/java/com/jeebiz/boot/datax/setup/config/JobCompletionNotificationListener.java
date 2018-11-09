/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.jeebiz.boot.datax.setup.config;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;  
  
@Component  
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {  
  
    @Override  
    public void afterJob(JobExecution jobExecution) {  
          
    }  
  
    @Override  
    public void beforeJob(JobExecution jobExecution) {  
        super.beforeJob(jobExecution);  
    }  
} 