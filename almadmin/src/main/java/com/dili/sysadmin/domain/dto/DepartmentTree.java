package com.dili.sysadmin.domain.dto;

import java.util.List;

import com.dili.sysadmin.domain.Department;

public class DepartmentTree extends Department {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6783029063616710962L;

	private List<DepartmentTree> children;

	public List<DepartmentTree> getChildren() {
		return children;
	}

	public void setChildren(List<DepartmentTree> children) {
		this.children = children;
	}

}
