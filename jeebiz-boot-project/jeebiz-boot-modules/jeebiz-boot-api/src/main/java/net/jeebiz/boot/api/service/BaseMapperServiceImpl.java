/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.service;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;

import net.jeebiz.boot.api.dao.BaseDao;

public class BaseMapperServiceImpl<T, E extends BaseDao<T>> extends BaseServiceImpl<T, E> {

	@Autowired
	private DozerBeanMapper beanMapper; 
	
	public DozerBeanMapper getBeanMapper() {
		return beanMapper;
	}

	public void setBeanMapper(DozerBeanMapper beanMapper) {
		this.beanMapper = beanMapper;
	}
	
}
