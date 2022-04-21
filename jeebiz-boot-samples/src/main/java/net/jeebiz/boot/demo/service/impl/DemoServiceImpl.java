/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package net.jeebiz.boot.demo.service.impl;

import org.springframework.stereotype.Service;

import net.jeebiz.boot.api.service.BaseServiceImpl;
import net.jeebiz.boot.demo.dao.DemoMapper;
import net.jeebiz.boot.demo.dao.entities.DemoEntity;
import net.jeebiz.boot.demo.service.IDemoService;

@Service
public class DemoServiceImpl extends BaseServiceImpl<DemoMapper, DemoEntity> implements IDemoService{

}
