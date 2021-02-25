/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.dto;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.OrderItem;

import io.swagger.annotations.ApiModelProperty;

public abstract class AbstractOrderedPaginationDTO extends AbstractPaginationDTO {

	/**
	 * 排序信息
	 */
	@ApiModelProperty(value = "orders", dataType = "java.util.List<OrderBy>", notes = "排序信息")
	private List<OrderItem> orders;

	public List<OrderItem> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderItem> orders) {
		this.orders = orders;
	}

}