/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.api.web;

import com.github.dozermapper.core.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseMapperController extends BaseController {

	@Autowired
	private Mapper beanMapper; 
	
	public Mapper getBeanMapper() {
		return beanMapper;
	}

	public void setBeanMapper(Mapper beanMapper) {
		this.beanMapper = beanMapper;
	}
	
}
