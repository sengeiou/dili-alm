package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.alm.domain.Department;
import com.dili.alm.domain.Role;
import com.dili.alm.domain.User;

public class UserDepartmentRole extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1562069406116228728L;

	private Department department;
	private List<Role> roles;

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

}
