/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.api.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;

public abstract class AbstractPaginationDTO {

	/**
	 * 每页记录数
	 */
	@ApiModelProperty(name = "limit", example = "15", value = "每页记录数")
	@Min(value = 2, message = "每页至少2条数据")
	private int limit = 15;

	/**
	 * 当前页码
	 */
	@ApiModelProperty(name = "pageNo", example = "1", value = "当前页码")
	@Min(value = 1, message = "最小页码不能小于1")
	private int pageNo = 1;

    /** 开始时间 */
	@ApiModelProperty(name = "pageNo", value = "开始时间")
    private String beginTime;

    /** 结束时间 */
	@ApiModelProperty(name = "pageNo", value = "结束时间")
    private String endTime;

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