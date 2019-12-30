package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.Role;
import com.dili.uap.sdk.domain.User;


public interface UserDepartmentRole extends User {


	Department getDepartment();

	void setDepartment(Department department);

	List<Role> getRoles();

	void setRoles(List<Role> roles);

}
