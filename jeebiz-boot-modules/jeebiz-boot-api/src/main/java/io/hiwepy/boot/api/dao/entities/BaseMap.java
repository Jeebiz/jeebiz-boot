/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.dao.entities;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;

@Data
@Accessors(chain = true)
@SuppressWarnings("serial")
public class BaseMap extends HashMap<String, Object> {

    /**
     * 应用唯一ID
     */
    protected String appid;


}
