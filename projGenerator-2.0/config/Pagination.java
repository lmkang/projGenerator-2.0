package com.ld.model;

import java.util.List;

/**
 * 通用的分页对象
 * 
 * @version 2.0
 * @since 2017-11-03
 * @author lmkang25@163.com
 *
 * @param <T>
 */
public class Pagination<T> {

	private List<T> recordList;// 记录
	private long recordCount;// 总记录数
	private int pageCount;// 总页数
	private int currentPage;// 当前页码
	private int pageSize;// 每页显示的条数
	
	public Pagination() {
		
	}

	/**
	 * 含参构造方法
	 * @param recordList 数据列表
	 * @param recordCount 数据总记录数
	 * @param pageSize 每页显示的条数
	 * @param currentPage 当前页面
	 */
	public Pagination(List<T> recordList, long recordCount, int pageSize, int currentPage) {
		this.recordList = recordList;
		this.recordCount = recordCount;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.pageCount = (int) ((recordCount + pageSize - 1) / pageSize);
	}

	public List<T> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<T> recordList) {
		this.recordList = recordList;
	}

	public long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
