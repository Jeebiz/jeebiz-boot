/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.dao.entities;

@SuppressWarnings("serial")
public class PaginationModel extends BaseModel {

	protected static final int DEFAULT_LIMIT = 15;
	
	/**
	 * 分页起始位置
	 */
	private int offset = 0;
	/**
	 * 每页记录数
	 */
	private int limit = 15;
	/**
	 * 当前页码
	 */
	private int pageNo;
	/**
	 * 总页数
	 */
	private int totalPage;
	/**
	 * 总记录数
	 */
	private int totalCount;
	/**
	 * 排序字段名称
	 */
	private String sortName;
	/**
	 * 排序类型 asc \ desc
	 */
	private String sortOrder;
	
	public int getPageNo() {
		return pageNo < 0 ? (getOffset() / getLimit() + 1 ) : pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}


	public int getOffset() {
		// 计算第一条记录的位置，Oracle分页是通过rownum进行的，而rownum是从1开始的
		return offset < 0 ? ( pageNo < 0 ? 0 : ( (getPageNo() - 1 ) * getLimit() + 1) ): offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit <= 0 ? DEFAULT_LIMIT : limit;
	}

	public void setLimit(int limit) {
	
		this.limit = limit;
	}

	public void prevPage(){
		setPageNo(( getPageNo()-1 ) > 0 ? getPageNo() - 1 : 0);
	}
	
	public void nextPage(){
		setPageNo(getPageNo()+1);
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
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