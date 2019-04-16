/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.webmvc;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.dozermapper.core.DozerBeanMapper;

public abstract class BaseMapperController extends BaseController {

	@Autowired
	private DozerBeanMapper beanMapper; 
	
	public DozerBeanMapper getBeanMapper() {
		return beanMapper;
	}

	public void setBeanMapper(DozerBeanMapper beanMapper) {
		this.beanMapper = beanMapper;
	}
	
}
