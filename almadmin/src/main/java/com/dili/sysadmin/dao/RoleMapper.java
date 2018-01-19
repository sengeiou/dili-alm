package com.dili.sysadmin.dao;

import java.util.List;
import java.util.Map;

import com.dili.ss.base.MyMapper;
import com.dili.sysadmin.domain.Role;

public interface RoleMapper extends MyMapper<Role> {

	List<Role> findByUserId(Long id);

	List<Role> findByMenuId(Long menuId);

	void saveRoleMenu(Map<String, Object> map);

	void roleResourceBatchDelete(Map<String, Object> params);

	void roleResourceBatchInsert(Long roleId, List<Long> resourcesToBeInsert);

	void delUser(Map<String, Object> map);

	List<Role> findByResource(Long id);

	List<Role> selectByDepartmentId(Long departmentId);

}