/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.dao.entities;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 	键值对模型对象
 */
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String toString() {
		return "key:" + key + " value:" + value;
	}

	@Override
	public int compareTo(PairModel o) {
		return this.key.compareTo(o.key);
	}
}
