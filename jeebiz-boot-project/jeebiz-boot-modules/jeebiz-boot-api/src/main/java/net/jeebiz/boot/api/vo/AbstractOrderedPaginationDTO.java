/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import net.jeebiz.boot.api.dao.entities.OrderBy;

public abstract class AbstractOrderedPaginationDTO extends AbstractPaginationDTO {

	/**
	 * 排序信息
	 */
	@ApiModelProperty(value = "orders", dataType = "java.util.List<OrderBy>", notes = "排序信息")
	private List<OrderBy> orders;

	public List<OrderBy> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderBy> orders) {
		this.orders = orders;
	}

}