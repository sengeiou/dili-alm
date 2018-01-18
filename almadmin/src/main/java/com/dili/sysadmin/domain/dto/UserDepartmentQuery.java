package com.dili.sysadmin.domain.dto;

import java.util.HashSet;
import java.util.Set;

import com.dili.sysadmin.domain.User;

public class UserDepartmentQuery extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8184724215801301959L;

	private Set<Long> departmentIds = new HashSet<>();

	public Set<Long> getDepartmentIds() {
		return departmentIds;
	}

	public void setDepartmentIds(Set<Long> departmentIds) {
		this.departmentIds = departmentIds;
	}

}
