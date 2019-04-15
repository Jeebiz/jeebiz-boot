/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.vo;

import io.swagger.annotations.ApiModelProperty;

public abstract class AbstractPaginationVo {

	/**
	 * 每页记录数
	 */
	@ApiModelProperty(value = "limit", dataType = "Integer", example = "15", notes = "每页记录数")
	private int limit = 15;

	/**
	 * 当前页码
	 */
	@ApiModelProperty(value = "pageNo", dataType = "Integer", example = "1", notes = "当前页码")
	private int pageNo = 1;
	/**
	 * 排序字段名称
	 */
	@ApiModelProperty(value = "sortName", dataType = "String", example = "name", notes = "排序字段名称")
	private String sortName;
	/**
	 * 排序类型 asc \ desc
	 */
	@ApiModelProperty(value = "sortOrder", dataType = "String", example = "desc", allowableValues = "asc,desc", notes = "排序类型 asc、desc")
	private String sortOrder;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

}