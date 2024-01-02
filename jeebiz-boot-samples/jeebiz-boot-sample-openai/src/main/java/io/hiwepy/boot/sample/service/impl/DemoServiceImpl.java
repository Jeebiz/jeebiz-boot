/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.sample.service.impl;

import io.hiwepy.boot.api.service.BaseServiceImpl;
import io.hiwepy.boot.sample.dao.DemoMapper;
import io.hiwepy.boot.sample.dao.entities.DemoEntity;
import io.hiwepy.boot.sample.service.IDemoService;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl extends BaseServiceImpl<DemoMapper, DemoEntity> implements IDemoService {

}
