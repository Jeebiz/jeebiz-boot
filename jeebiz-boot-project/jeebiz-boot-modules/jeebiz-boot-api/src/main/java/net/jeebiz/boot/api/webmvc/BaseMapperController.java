/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.webmvc;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.dozermapper.core.Mapper;

public abstract class BaseMapperController extends BaseApiController {

	@Autowired
	private Mapper beanMapper; 
	
	public Mapper getBeanMapper() {
		return beanMapper;
	}

	public void setBeanMapper(Mapper beanMapper) {
		this.beanMapper = beanMapper;
	}
	
}
