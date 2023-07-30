/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.dao.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 	键值对模型对象
 */
@Data
@Accessors(chain = true)
@SuppressWarnings("serial")
@ApiModel(value = "PairModel", description = "键值对")
public class PairModel implements Cloneable, Serializable, Comparable<PairModel> {

    @ApiModelProperty(name = "key", dataType = "String", value = "数据键")
    protected String key;
    @ApiModelProperty(name = "value", dataType = "String", value = "数据值")
    protected String value;
    @ApiModelProperty(name = "checked", dataType = "String", value = "是否选中")
    protected String checked;

    public PairModel() {

    }

    public PairModel(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return "key:" + key + " value:" + value;
    }

    @Override
    public int compareTo(PairModel o) {
        return this.key.compareTo(o.key);
    }
}
