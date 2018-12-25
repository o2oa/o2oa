package com.x.cms.common.page;

import java.util.List;

public class Pagination<T> {
	private int currentPage; // 当前页
	private int pageSize; // 每页显示条数
	private int totalPage; // 总页数
	private int totalRecord; // 总记录数
	private List<T> dataList; // 分页返回的数据

	private Pagination() {
	}

	/*
	 * 初始化PageModel实例
	 */
	private Pagination(final String pageSize, final String page, final String totalRecord) {
		// 初始化每页显示条数
		setPageSize(pageSize);
		// 设置总记录数
		setTotalRecord(totalRecord);
		// 初始化总页数
		setTotalPage();
		// 初始化当前页
		setCurrentPage(page);
	}

	/*
	 * 外界获得PageModel实例
	 */
	@SuppressWarnings("rawtypes")
	public static Pagination newPagination(final String pageSize, final String page, final String totalRecord) {
		return new Pagination(pageSize, page, totalRecord);
	}

	// 设置当前请求页
	private void setPageSize(String pagesize) {
		try {
			pageSize = Integer.parseInt(pagesize);
		} catch (java.lang.NumberFormatException e) {
			// 这里异常不做处理，当前页默认为1
			pageSize = 12;
		}
		// 如果当前页小于第一页时，当前页指定到首页
		if (pageSize <= 0) {
			pageSize = 12;
		}
	}

	// 设置当前请求页
	private void setTotalRecord(String total) {
		try {
			totalRecord = Integer.parseInt(total);
		} catch (java.lang.NumberFormatException e) {
			// 这里异常不做处理，当前页默认为1
			totalRecord = 0;
		}
	}

	// 设置当前请求页
	private void setCurrentPage(String page) {
		try {
			currentPage = Integer.parseInt(page);

		} catch (java.lang.NumberFormatException e) {
			// 这里异常不做处理，当前页默认为1
			currentPage = 1;
		}
		// 如果当前页小于第一页时，当前页指定到首页
		if (currentPage < 1) {

			currentPage = 1;
		}

		if (currentPage > totalPage) {

			currentPage = totalPage;

		}

	}

	private void setTotalPage() {
		if (totalRecord % pageSize == 0) {

			totalPage = totalRecord / pageSize;
		} else {
			totalPage = totalRecord / pageSize + 1;
		}
	}

	/*
	 * 获得当前页
	 */
	public int getCurrentPage() {
		return currentPage;
	}

	/*
	 * 获得总页数
	 */
	public int getTotalPage() {
		return totalPage;

	}

	/*
	 * 获得开始行数， 从0开始
	 */
	public int getStartRow() {
		return (currentPage - 1) * pageSize;
	}

	/*
	 * 获得结束行
	 */
	public int getEndRow() {
		return currentPage * pageSize - 1;
	}

	/*
	 * 获得翻页数据
	 */
	public List<T> getDataList() {
		return dataList;
	}

	/*
	 * 设置翻页数据
	 */
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	// 首页
	public int getFirst() {
		return 1;
	}

	//
	// 上一页

	public int getPrevious() {

		return currentPage - 1;
	}

	//
	// // 下一页
	public int getNext() {
		return currentPage + 1;
	}

	//
	// // 尾页
	//
	public int getLast() {
		return totalPage;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public int getPageSize() {
		return pageSize;
	}

}
