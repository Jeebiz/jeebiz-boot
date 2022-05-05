/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.dao.entities;

import java.io.Serializable;
import java.util.List;

/**
 * 国际化Model
 */
@SuppressWarnings("serial")
public class I18nModel implements Serializable {

	/**模块名称：通常指功能模块代码*/
	protected String module;
	/**国际化资源文件名称*/
	protected String resource;
	/**国际化信息集合*/
	protected List<PairModel> i18nList;

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public List<PairModel> getI18nList() {
		return i18nList;
	}

	public void setI18nList(List<PairModel> i18nList) {
		this.i18nList = i18nList;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("{");
		builder.append("module:" ).append(module).append(",");
		builder.append("resource:" ).append(resource).append(",");
		builder.append("[");
		for (PairModel pairModel : i18nList) {
			builder.append("key:" + pairModel.getKey() + " value:" + pairModel.getValue());
		}
		builder.append("]}");
		return builder.toString();
	}

}
