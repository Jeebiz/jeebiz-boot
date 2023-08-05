/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.demo.repository.entities;

import io.hiwepy.boot.api.dao.entities.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DemoEntity extends BaseEntity<DemoEntity> {

    private static final long serialVersionUID = 6189820231775242317L;

    private String id;

    private String name;

    private String text;

}
