package com.dili.sysadmin.dao;

import java.util.List;

import com.dili.ss.base.MyMapper;
import com.dili.sysadmin.domain.Department;
import com.dili.sysadmin.domain.User;
import com.dili.sysadmin.domain.dto.UserDepartmentQuery;
import com.dili.sysadmin.domain.dto.UserDepartmentRole;
import com.dili.sysadmin.domain.dto.UserDepartmentRoleQuery;

public interface UserMapper extends MyMapper<User> {

	List<User> findUserByRole(Long roleId);

	Long countByRoleId(Long roleId);

	List<String> receptByUsername(String username);

	int deleteUserRole(Long userId);

	List<User> findUserByMenu(Long menuId);

	int countByDepartmentId(Long deptId);

	List<UserDepartmentRole> findUserContainDepartmentAndRole(UserDepartmentRoleQuery query);

	List<UserDepartmentRole> selectByCondition(UserDepartmentQuery query);

	Integer countByDepartmentIds(List<Department> children);
}