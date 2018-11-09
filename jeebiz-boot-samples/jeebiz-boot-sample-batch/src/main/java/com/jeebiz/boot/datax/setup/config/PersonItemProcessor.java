/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package com.jeebiz.boot.datax.setup.config;

import org.springframework.batch.item.ItemProcessor;
  
public class PersonItemProcessor implements ItemProcessor<Person, Person> {  
      
    public String inputFile;  
  
    public PersonItemProcessor() {  
    }  
      
    public PersonItemProcessor(String inputFile) {  
        this.inputFile = inputFile;  
    }  
    // 数据处理  
    public Person process(Person blackListDO) throws Exception {  
       /* blackListDO.setDeleteFlag(0);  
        blackListDO.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));  */
        return blackListDO;  
    }  
  
} 