package com.dili.sysadmin.service;

import java.util.List;

import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.Role;
import com.dili.sysadmin.domain.dto.EditRoleMenuAndResouceDto;
import com.dili.sysadmin.domain.dto.MenuJsonDto;
import com.dili.sysadmin.domain.dto.RoleUserDto;
import com.dili.sysadmin.domain.dto.UpdateRoleMenuResourceDto;
import com.dili.sysadmin.exception.RoleException;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:51.
 */
public interface RoleService extends BaseService<Role, Long> {

	void saveRoleMenu(Long roleId, Long[] menuIds);

	List<MenuJsonDto> getRoleMenuAndResources(Long roleId);

	List<Role> findByUserId(Long userId);

	List<Role> findNotBindWithUser(Long userId);

	BaseOutput<Object> deleteIfUserNotBind(Long roleId);

	EditRoleMenuAndResouceDto queryEditRoleMenuAndService(Long roleId);

	void updateRoleMenuResource(UpdateRoleMenuResourceDto dto) throws RoleException;

	BaseOutput<Object> unbindRoleUser(RoleUserDto dto);

	BaseOutput<Role> insertAndGet(Role role);

	BaseOutput<Object> updateAfterCheck(Role role);

	BaseOutput<Object> listByDepartment(Long departmentId);

	List<Role> findNotBindWithUserByDepartmentId(Long userid, Long departmentId);

}