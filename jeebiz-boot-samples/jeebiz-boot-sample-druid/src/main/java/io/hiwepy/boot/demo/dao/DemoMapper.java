/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.demo.dao;

import org.apache.ibatis.annotations.Mapper;

import io.hiwepy.boot.api.dao.BaseMapper;
import io.hiwepy.boot.demo.dao.entities.DemoEntity;

@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {


}
