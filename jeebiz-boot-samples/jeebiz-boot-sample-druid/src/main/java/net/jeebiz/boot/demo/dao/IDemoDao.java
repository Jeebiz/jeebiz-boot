/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import net.jeebiz.boot.api.dao.BaseDao;
import net.jeebiz.boot.demo.dao.entities.DemoModel;

@Mapper
public interface IDemoDao extends BaseDao<DemoModel>{

	
}
