package com.dili.sysadmin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.dao.DataAuthMapper;
import com.dili.sysadmin.dao.MenuMapper;
import com.dili.sysadmin.dao.ResourceMapper;
import com.dili.sysadmin.dao.RoleMapper;
import com.dili.sysadmin.dao.RoleMenuMapper;
import com.dili.sysadmin.dao.RoleResourceMapper;
import com.dili.sysadmin.dao.UserRoleMapper;
import com.dili.sysadmin.domain.Menu;
import com.dili.sysadmin.domain.Resource;
import com.dili.sysadmin.domain.Role;
import com.dili.sysadmin.domain.RoleMenu;
import com.dili.sysadmin.domain.RoleResource;
import com.dili.sysadmin.domain.UserRole;
import com.dili.sysadmin.domain.dto.EditRoleMenuAndResouceDto;
import com.dili.sysadmin.domain.dto.MenuDto;
import com.dili.sysadmin.domain.dto.MenuJsonDto;
import com.dili.sysadmin.domain.dto.MenuResourceDto;
import com.dili.sysadmin.domain.dto.RoleUserDto;
import com.dili.sysadmin.domain.dto.UpdateRoleMenuResourceDto;
import com.dili.sysadmin.exception.RoleException;
import com.dili.sysadmin.manager.RoleManager;
import com.dili.sysadmin.service.RoleService;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:51.
 */
@Service
public class RoleServiceImpl extends BaseServiceImpl<Role, Long> implements RoleService {

	@Autowired
	private RoleMapper roleMapper;
	@Autowired
	private ResourceMapper resourceMapper;
	@Autowired
	private MenuMapper menuMapper;
	@Autowired
	private RoleMenuMapper roleMenuMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private RoleResourceMapper roleResourceMapper;
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private DataAuthMapper dataAuthMapper;

	public RoleMapper getActualDao() {
		return (RoleMapper) getDao();
	}

	@Override
	public void saveRoleMenu(Long roleId, Long[] menuIds) {
		RoleMenu record = new RoleMenu();
		record.setRoleId(roleId);
		this.roleMenuMapper.delete(record);
		for (Long menuId : menuIds) {
			record.setMenuId(menuId);
			this.roleMenuMapper.insert(record);
		}
		this.roleManager.reloadRoleUserPermission(roleId);
	}

	@Override
	public List<MenuJsonDto> getRoleMenuAndResources(Long roleId) {
		List<Menu> menus = this.menuMapper.findByRoleId(roleId);
		List<MenuJsonDto> vos = new ArrayList<>(menus.size());
		BeanCopier copier = BeanCopier.create(Menu.class, MenuJsonDto.class, false);
		for (Menu menu : menus) {
			MenuJsonDto vo = new MenuJsonDto();
			copier.copy(menu, vo, null);
			if (StringUtils.isNotBlank(menu.getMenuUrl())) {
				Resource record = new Resource();
				record.setMenuId(menu.getId());
				Resource resource = this.resourceMapper.selectOne(record);
				vo.setResourceId(resource.getId());
			}
			vos.add(vo);
		}
		return vos;
	}

	@Override
	public List<Role> findByUserId(Long userId) {
		return this.roleMapper.findByUserId(userId);
	}

	@Override
	public List<Role> findNotBindWithUser(Long userId) {
		List<Role> userRoles = this.roleMapper.findByUserId(userId);
		List<Role> allRoles = this.roleMapper.selectAll();
		return (List<Role>) CollectionUtils.subtract(allRoles, userRoles);
	}

	@Transactional
	@Override
	public BaseOutput<Object> deleteIfUserNotBind(Long roleId) {
		if (roleId.equals(1L)) {
			return BaseOutput.failure("超级管理员角色不能删除");
		}
		UserRole record = new UserRole();
		record.setRoleId(roleId);
		int count = this.userRoleMapper.selectCount(record);
		if (count > 0) {
			return BaseOutput.failure("该角色下绑定了用户，请先解绑用户后再删除");
		}
		int rows = this.roleMapper.deleteByPrimaryKey(roleId);
		if (rows <= 0) {
			return BaseOutput.failure("删除角色失败");
		}
		RoleMenu roleMenuQuery = new RoleMenu();
		roleMenuQuery.setRoleId(roleId);
		this.roleMenuMapper.delete(roleMenuQuery);
		RoleResource roleResource = new RoleResource();
		roleResource.setRoleId(roleId);
		this.roleResourceMapper.delete(roleResource);
		return BaseOutput.success();
	}

	@Override
	public EditRoleMenuAndResouceDto queryEditRoleMenuAndService(Long roleId) {
		List<MenuDto> allMenus = this.menuMapper.selectMenuDto();
		List<MenuDto> roleMenus = this.menuMapper.selectRoleMenuDto(roleId);
		EditRoleMenuAndResouceDto dto = new EditRoleMenuAndResouceDto();
		dto.setAllMenus(this.parseMenuResourceDto(allMenus));
		dto.setRoleMenus(this.parseMenuResourceDto(roleMenus));
		return dto;
	}

	private List<MenuResourceDto> parseMenuResourceDto(List<MenuDto> menus) {
		if (CollectionUtils.isEmpty(menus)) {
			return null;
		}
		List<MenuResourceDto> target = new ArrayList<>(menus.size());
		for (MenuDto menuDto : menus) {
			MenuResourceDto dto = new MenuResourceDto();
			dto.setId(menuDto.getId());
			dto.setName(menuDto.getName());
			dto.setParentId(menuDto.getParentId());
			target.add(dto);
			if (CollectionUtils.isNotEmpty(menuDto.getResources())) {
				for (Resource resource : menuDto.getResources()) {
					MenuResourceDto resourceDto = new MenuResourceDto();
					resourceDto.setResourceId(resource.getId());
					resourceDto.setName(resource.getName());
					resourceDto.setParentId(resource.getMenuId());
					resourceDto.setResource(true);
					resourceDto.setMenuId(resource.getMenuId());
					target.add(resourceDto);
				}
			}
		}
		return target;
	}

	@Transactional
	@Override
	public void updateRoleMenuResource(UpdateRoleMenuResourceDto dto) throws RoleException {
		int result = 0;
		RoleMenu record = new RoleMenu();
		record.setRoleId(dto.getRoleId());
		result += this.roleMenuMapper.delete(record);
		RoleResource roleResourceRecord = new RoleResource();
		roleResourceRecord.setRoleId(dto.getRoleId());
		result += this.roleResourceMapper.delete(roleResourceRecord);
		for (MenuResourceDto mrDto : dto.getMenuResources()) {
			if (mrDto.isResource()) {
				roleResourceRecord.setId(null);
				roleResourceRecord.setResourceId(mrDto.getResourceId());
				result += this.roleResourceMapper.insertSelective(roleResourceRecord);
			} else {
				record.setId(null);
				record.setMenuId(mrDto.getId());
				result += this.roleMenuMapper.insertSelective(record);
			}
		}
		if (result <= 0) {
			throw new RoleException("更新失败");
		}
		this.roleManager.reloadRoleUserPermission(dto.getRoleId());
	}

	@Override
	public BaseOutput<Object> unbindRoleUser(RoleUserDto dto) {
		Role record = new Role();
		record.setId(dto.getRoleId());
		int count = this.roleMapper.selectCount(record);
		if (count <= 0) {
			return BaseOutput.failure("角色不存在");
		}
		int rows = 0;
		UserRole userRole = new UserRole();
		userRole.setRoleId(dto.getRoleId());
		for (Long userId : dto.getUserIds()) {
			userRole.setUserId(userId);
			rows += this.userRoleMapper.delete(userRole);
		}
		this.roleManager.reloadRoleUserPermission(dto.getRoleId());
		return rows > 0 ? BaseOutput.success() : BaseOutput.failure();
	}

	@Override
	public BaseOutput<Role> insertAndGet(Role role) {
		Role query = new Role();
		query.setRoleName(role.getRoleName());
		int count = this.getActualDao().selectCount(query);
		if (count > 0) {
			return BaseOutput.failure("包含相同名称的角色");
		}
		int rows = this.roleMapper.insert(role);
		if (rows <= 0) {
			return BaseOutput.failure("插入失败");
		}
		role = this.roleMapper.selectByPrimaryKey(role.getId());
		return BaseOutput.success("插入成功").setData(role);
	}

	@Override
	public BaseOutput<Object> updateAfterCheck(Role role) {
		if (role.getId().equals(1L)) {
			return BaseOutput.failure("超级管理员角色不能编辑");
		}
		Role query = new Role();
		query.setRoleName(role.getRoleName());
		Role old = this.getActualDao().selectOne(query);
		if (old != null && !old.getId().equals(role.getId())) {
			return BaseOutput.failure("包含相同名称的角色");
		}
		int result = this.updateSelective(role);
		if (result > 0) {
			return BaseOutput.success().setData(role);
		}
		return BaseOutput.failure();
	}

	@Override
	public BaseOutput<Object> listByDepartment(Long departmentId) {
		List<Role> roles = this.getActualDao().selectByDepartmentId(departmentId);
		return BaseOutput.success().setData(roles);
	}

	@Override
	public List<Role> findNotBindWithUserByDepartmentId(Long userId, Long departmentId) {
		List<Role> userRoles = this.getActualDao().findByUserId(userId);
		List<Role> allRoles = this.getActualDao().selectByDepartmentId(departmentId);
		return (List<Role>) CollectionUtils.subtract(allRoles, userRoles);
	}

}