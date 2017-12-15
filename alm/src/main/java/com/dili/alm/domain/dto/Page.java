package com.dili.alm.domain.dto;

public class Page {
	private int pageIndex;

	private int pageSize;

	private int totalCount;

	private int pageCount;
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
		
	}
	public int getPageIndex() {
		return pageIndex;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		this.pageCount = (totalCount%pageSize ==0)? totalCount/pageSize:totalCount/pageSize+1;
	}

	public int getPageCount() {
		return pageCount;
	}

	@Override
	public String toString() {
		return "Page [pageIndex=" + pageIndex + ", pageSize=" + pageSize
				+ ", totalCount=" + totalCount + ", pageCount=" + pageCount
				+ "]";
	}
	

	
}
