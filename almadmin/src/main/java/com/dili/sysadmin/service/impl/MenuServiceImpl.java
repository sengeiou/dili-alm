package com.dili.sysadmin.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.dao.MenuMapper;
import com.dili.sysadmin.dao.ResourceMapper;
import com.dili.sysadmin.dao.RoleMenuMapper;
import com.dili.sysadmin.domain.Menu;
import com.dili.sysadmin.domain.MenuType;
import com.dili.sysadmin.domain.Resource;
import com.dili.sysadmin.domain.RoleMenu;
import com.dili.sysadmin.domain.dto.MenuListDto;
import com.dili.sysadmin.domain.dto.UpdateMenuDto;
import com.dili.sysadmin.exception.ApplicationException;
import com.dili.sysadmin.exception.MenuException;
import com.dili.sysadmin.manager.UserManager;
import com.dili.sysadmin.service.MenuService;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:51.
 */
@Service
public class MenuServiceImpl extends BaseServiceImpl<Menu, Long> implements MenuService {

	private static final int THREAD_POOL_SIZE = 10;

	@Autowired
	private MenuMapper menuDao;
	@Autowired
	private ResourceMapper resourceDao;
	@Autowired
	private RoleMenuMapper roleMenuMapper;
	@Autowired
	private UserManager userManager;

	public MenuMapper getActualDao() {
		return (MenuMapper) getDao();
	}

	@Override
	public List<Menu> findUserMenu(String userId) throws MenuException {
		if (StringUtils.isBlank(userId)) {
			throw new MenuException("请先登录");
		}
		List<Menu> menus = this.menuDao.findByUserId(Long.valueOf(userId));
		Iterator<Menu> it = menus.iterator();
		while (it.hasNext()) {
			Menu menu = it.next();
			if (menu.getType().equals(2)) {
				it.remove();
			}
		}
		return menus;
	}

	@Override
	public List<MenuListDto> listContainAndParseResource() {
		List<Menu> menus = this.menuDao.selectAll();
		List<MenuListDto> targetList = new ArrayList<>(menus.size());
		for (Menu menu : menus) {
			MenuListDto vo = new MenuListDto();
			vo.setId(menu.getId());
			vo.setName(menu.getName());
			vo.setParentId(menu.getParentId());
			vo.addAttribute("type", menu.getType());
			targetList.add(vo);
		}
		return targetList;
	}

	@Override
	public BaseOutput<Object> update(UpdateMenuDto dto) {
		Menu menu = this.menuDao.selectByPrimaryKey(dto.getId());
		if (menu == null) {
			return BaseOutput.failure("菜单不存在");
		}
		String url = menu.getMenuUrl();
		menu.setName(dto.getName());
		menu.setMenuUrl(dto.getMenuUrl());
		menu.setType(dto.getType());
		menu.setDescription(dto.getDescription());
		menu.setOrderNumber(dto.getOrderNumber());
		menu.setModified(new Date());
		int result = this.menuDao.updateByPrimaryKey(menu);
		if (result <= 0) {
			return BaseOutput.failure("更新菜单失败");
		}
		if (!dto.getMenuUrl().equals(url)) {
			this.userManager.reloadUserUrlsByMenu(dto.getId());
		}
		return BaseOutput.success("修改成功").setData(menu);
	}

	@Override
	public BaseOutput<Object> deleteCheckIsBinding(Long menuId) {
		Menu menu = this.menuDao.selectByPrimaryKey(menuId);
		if (menu == null) {
			return BaseOutput.failure("菜单不存在");
		}
		Menu menuQuery = new Menu();
		menuQuery.setParentId(menu.getId());
		int count = this.menuDao.selectCount(menuQuery);
		if (count > 0) {
			return BaseOutput.failure("该菜单下包含子菜单，不能删除");
		}
		Resource resourceQuery = new Resource();
		resourceQuery.setMenuId(menuId);
		count = this.resourceDao.selectCount(resourceQuery);
		if (count > 0) {
			return BaseOutput.failure("该菜单下包含资源权限，不能删除");
		}
		RoleMenu record = new RoleMenu();
		record.setMenuId(menuId);
		count = this.roleMenuMapper.selectCount(record);
		if (count > 1) {
			return BaseOutput.failure("该菜单下绑定有角色，不能删除");
		}
		count = this.menuDao.deleteByPrimaryKey(menuId);
		if (count <= 0) {
			return BaseOutput.failure("删除失败");
		}
		this.userManager.reloadUserUrlsByMenu(menuId);
		return BaseOutput.success("删除成功");
	}

	@Transactional(rollbackFor = ApplicationException.class)
	@Override
	public void insertWithOutput(Menu menu) throws MenuException {
		int result = this.insertSelective(menu);
		if (result <= 0) {
			throw new MenuException("新增菜单失败");
		}
		RoleMenu record = new RoleMenu();
		record.setRoleId(1L);
		record.setMenuId(menu.getId());
		result = this.roleMenuMapper.insertSelective(record);
		if (result <= 0) {
			throw new MenuException("绑定超级管理员角色失败");
		}
	}

}