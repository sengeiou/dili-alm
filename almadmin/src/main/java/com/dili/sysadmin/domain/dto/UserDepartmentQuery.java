package com.dili.sysadmin.domain.dto;

import com.dili.sysadmin.domain.User;

public class UserDepartmentQuery extends User {

	private Long departmentId;

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

}
