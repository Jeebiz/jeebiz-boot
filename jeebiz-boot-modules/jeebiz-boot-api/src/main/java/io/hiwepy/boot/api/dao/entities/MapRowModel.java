/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.dao.entities;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
@SuppressWarnings("serial")
public class MapRowModel implements Cloneable, Serializable {

    private Map<String, String> row = new HashMap<String, String>();

}
