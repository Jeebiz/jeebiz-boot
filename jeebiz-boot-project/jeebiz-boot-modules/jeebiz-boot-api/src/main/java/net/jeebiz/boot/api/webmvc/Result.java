/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.webmvc;

import java.util.Collections;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


public class Result<T> {

	/**
	 * 状态码
	 */
	private int code = 0;
	/**
	 * 当前页码
	 */
	private long current;
	/**
	 * 每页显示条数，默认 15
	 */
	private long size = 15;
	/**
	 * 总页码
	 */
	private long pages;
	/**
	 * 总记录数
	 */
	private long total;
	/**
	 * 数据集：bootstrap-table要求服务器返回的json包含：total，rows；不想修改前端的默认配置
	 */
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
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
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
