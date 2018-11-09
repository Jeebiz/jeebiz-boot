/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.dao.entities;

import java.io.Serializable;

/**
 * 键值对模型对象
 * @author vindell
 */
@SuppressWarnings("serial")
public class PairModel implements Cloneable, Serializable {
	
	protected String key;
	protected String value;

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

	public String toString() {
		return "key:" + key + " value:" + value;
	}
}
