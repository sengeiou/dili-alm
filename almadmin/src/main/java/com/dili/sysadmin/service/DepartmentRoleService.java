package com.dili.sysadmin.service;

import java.util.List;
import java.util.Map;

import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.DepartmentRole;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-07 15:11:37.
 */
public interface DepartmentRoleService extends BaseService<DepartmentRole, Long> {

	BaseOutput<Object> insertAndGet(DepartmentRole departmentRole);

	List<Map> listByProvider(DepartmentRole departmentRole);

	BaseOutput<Object> updateAndGet(DepartmentRole departmentRole);
}