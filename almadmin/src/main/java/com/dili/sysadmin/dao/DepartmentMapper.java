package com.dili.sysadmin.dao;

import java.util.List;

import com.dili.ss.base.MyMapper;
import com.dili.sysadmin.domain.Department;
import com.dili.sysadmin.domain.dto.DepartmentContainRole;

public interface DepartmentMapper extends MyMapper<Department> {

	List<Department> findByUserId(Long id);

	List<DepartmentContainRole> findByUserIdContainRoles(Long userId);
}