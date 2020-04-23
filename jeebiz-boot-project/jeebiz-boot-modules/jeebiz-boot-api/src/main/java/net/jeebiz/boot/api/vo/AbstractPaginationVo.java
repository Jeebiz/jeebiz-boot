/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import net.jeebiz.boot.api.dao.entities.OrderBy;

public abstract class AbstractPaginationVo {

	/**
	 * 每页记录数
	 */
	@ApiModelProperty(name = "limit", dataType = "Integer", example = "15", value = "每页记录数")
	private int limit = 15;

	/**
	 * 当前页码
	 */
	@ApiModelProperty(name = "pageNo", dataType = "Integer", example = "1", value = "当前页码")
	private int pageNo = 1;
	/**
	 * 排序信息
	 */
	@ApiModelProperty(value = "orders", dataType = "java.util.List<OrderBy>", notes = "排序信息")
	private List<OrderBy> orders;

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

	public List<OrderBy> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderBy> orders) {
		this.orders = orders;
	}

}