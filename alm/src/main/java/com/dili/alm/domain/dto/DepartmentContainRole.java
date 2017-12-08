package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.alm.domain.Department;
import com.dili.alm.domain.Role;

public class DepartmentContainRole extends Department {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1562069406116228728L;

	private List<Role> roles;

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

}
