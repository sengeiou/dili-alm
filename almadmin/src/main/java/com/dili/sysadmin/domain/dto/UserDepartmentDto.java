package com.dili.sysadmin.domain.dto;

import java.util.List;

import org.springframework.cglib.beans.BeanCopier;

import com.dili.sysadmin.domain.Department;
import com.dili.sysadmin.domain.Role;
import com.dili.sysadmin.domain.User;

public class UserDepartmentDto extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 781633557170038317L;

	private static final BeanCopier COPIER = BeanCopier.create(User.class, UserDepartmentDto.class, false);

	private Department department;
	private List<Role> roles;

	public UserDepartmentDto() {
	}

	public UserDepartmentDto(User user) {
		COPIER.copy(user, this, null);
	}

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
