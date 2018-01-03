package com.dili.sysadmin.domain.dto;

import com.dili.sysadmin.domain.Department;

public class DepartmentUserCountDto extends Department {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2900694985273019656L;

	private Integer userCount = 0;

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

}
