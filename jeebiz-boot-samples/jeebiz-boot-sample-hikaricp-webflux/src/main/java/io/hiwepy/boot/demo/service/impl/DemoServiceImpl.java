/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.demo.service.impl;

import org.springframework.stereotype.Service;

import io.hiwepy.boot.api.service.BaseServiceImpl;
import io.hiwepy.boot.demo.dao.DemoMapper;
import io.hiwepy.boot.demo.dao.entities.DemoEntity;
import io.hiwepy.boot.demo.service.IDemoService;

@Service
public class DemoServiceImpl extends BaseServiceImpl<DemoMapper, DemoEntity> implements IDemoService{

}
