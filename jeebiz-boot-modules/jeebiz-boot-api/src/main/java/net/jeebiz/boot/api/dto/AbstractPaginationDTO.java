/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.dto;

import javax.validation.constraints.Min;

import io.swagger.annotations.ApiModelProperty;

public abstract class AbstractPaginationDTO {

	/**
	 * 每页记录数
	 */
	@ApiModelProperty(name = "limit", dataType = "Integer", example = "15", value = "每页记录数")
	@Min(value = 2, message = "每页至少2条数据")
	private int limit = 15;

	/**
	 * 当前页码
	 */
	@ApiModelProperty(name = "pageNo", dataType = "Integer", example = "1", value = "当前页码")
	@Min(value = 1, message = "最小页码不能小于1")
	private int pageNo = 1;

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

}