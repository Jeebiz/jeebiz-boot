/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.sample.dao;

import io.hiwepy.boot.api.dao.BaseMapper;
import io.hiwepy.boot.sample.dao.entities.DemoEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoMapper extends BaseMapper<DemoEntity> {


}
