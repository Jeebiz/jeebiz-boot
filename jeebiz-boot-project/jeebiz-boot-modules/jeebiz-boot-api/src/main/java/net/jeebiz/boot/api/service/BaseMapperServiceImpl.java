/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.dozermapper.core.Mapper;

import net.jeebiz.boot.api.dao.BaseDao;

public class BaseMapperServiceImpl<T, E extends BaseDao<T>> extends BaseServiceImpl<T, E> {

	@Autowired
	private Mapper beanMapper; 
	
	public Mapper getBeanMapper() {
		return beanMapper;
	}

	public void setBeanMapper(Mapper beanMapper) {
		this.beanMapper = beanMapper;
	}
	
}
