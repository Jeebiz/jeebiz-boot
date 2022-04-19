/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import net.jeebiz.boot.api.dao.BaseMapper;
import net.jeebiz.boot.demo.dao.entities.DemoEntity;

@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity>{

	
}
