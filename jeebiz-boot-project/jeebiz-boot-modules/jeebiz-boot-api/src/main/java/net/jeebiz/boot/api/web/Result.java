/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.web;

import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.jeebiz.boot.api.ApiCode;


@ApiModel(value = "Result", description = "分页查询结果对象")
public class Result<T> {

	/**
	 * 状态码
	 */
	@ApiModelProperty(name = "code", dataType = "String", value = "状态码")
	private String code = ApiCode.SC_SUCCESS.getCode();
	/**
	 * 当前页码
	 */
	@ApiModelProperty(name = "current", dataType = "Long", value = "当前页码")
	private long current;
	/**
	 * 每页显示条数，默认 15
	 */
	@ApiModelProperty(name = "size", dataType = "Long", value = "每页显示条数，默认 15")
	private long size = 15;
	/**
	 * 总页码
	 */
	@ApiModelProperty(name = "pages", dataType = "Long", value = "总页码")
	private long pages;
	/**
	 * 总记录数
	 */
	@ApiModelProperty(name = "total", dataType = "Long", value = "总记录数")
	private long total;
	/**
	 * 数据集：bootstrap-table要求服务器返回的json包含：total，rows；不想修改前端的默认配置
	 */
	@ApiModelProperty(name = "rows", dataType = "java.util.List<T>", value = "数据集")
	private List<T> rows = Collections.emptyList();

	public Result() {
	}
	
	public Result(List<T> rows) {
		
		this.total = rows.size();
		this.current = 1;
		this.size = rows.size();
		this.pages = 1;
		this.rows = rows;
		
	}
	
	@SuppressWarnings("rawtypes")
	public Result(Page pageResult, List<T> rows) {
		
		this.total = pageResult.getTotal();
		this.current = pageResult.getCurrent();
		this.size = pageResult.getSize();
		this.pages = pageResult.getPages();
		this.rows = rows;
		
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getCurrent() {
		return current;
	}

	public void setCurrent(long current) {
		this.current = current;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getPages() {
		return pages;
	}

	public void setPages(long pages) {
		this.pages = pages;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

}
