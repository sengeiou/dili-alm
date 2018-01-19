package com.dili.sysadmin.domain.dto;

import java.util.List;

import com.dili.sysadmin.domain.Role;
import com.dili.sysadmin.domain.User;

public class UserDepartmentRole extends User {

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
