package com.dili.sysadmin.manager.impl;

import com.dili.sysadmin.dao.MenuMapper;
import com.dili.sysadmin.dao.ResourceMapper;
import com.dili.sysadmin.domain.Menu;
import com.dili.sysadmin.domain.Resource;
import com.dili.sysadmin.domain.Role;
import com.dili.sysadmin.domain.dto.MenuDto;
import com.dili.sysadmin.domain.dto.MenuJsonDto;
import com.dili.sysadmin.manager.MenuManager;
import com.dili.sysadmin.manager.ResourceManager;
import com.dili.sysadmin.manager.RoleManager;
import com.dili.sysadmin.sdk.session.SessionConstants;
import com.dili.sysadmin.sdk.util.ManageRedisUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <B>Description</B> <br />
 * <B>Copyright</B> Copyright (c) 2014 www.dili7 All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 * 
 * @createTime 2014-7-3 14:24:39
 * @author template
 */
@Component
public class MenuManagerImpl implements MenuManager {
	private final static Logger LOG = LoggerFactory.getLogger(MenuManagerImpl.class);

	/**
	 * 缓存菜单KEY
	 */
	private static final String REDIS_MENU_TREE_KEY = "manage:menu:";

	private static final String ALL_TREE_KEY = REDIS_MENU_TREE_KEY + "allTree";

	private static final String LIST_CHILDREN_KEY = REDIS_MENU_TREE_KEY + "children:";

	private static final String ITEM_KEY = REDIS_MENU_TREE_KEY + "item:";

	/**
	 * 类型目录
	 */
	public static final Integer TYPE_DIR = 0;
	/**
	 * 类型, 叶子
	 */
	public static final Integer TYPE_LEAF = 1;

	@Autowired
	private MenuMapper menuDao;

	@Autowired
	private ResourceManager resourceManager;
	@Autowired
	private ResourceMapper resourceDao;
	@Autowired
	private RoleManager roleManager;
	@Autowired
	private ManageRedisUtil redisUtils;

	private List<MenuDto> getMenusWithParentId(Long parentId, Set<MenuDto> menus) {
		List<MenuDto> children = new ArrayList<MenuDto>();
		for (MenuDto menu : menus) {
			if (menu.getParentId().equals(parentId)) {
				children.add(menu);
			}
		}
		return children;

	}

	public Boolean save(Menu menu) {
		// 去重
		Menu record = new Menu();
		menu.setParentId(menu.getParentId());
		menu.setName(menu.getName());
		if (menuDao.selectCount(record) > 0) {
			return false;
		}

		if (menu.getParentId() != null) {
			redisUtils.remove(LIST_CHILDREN_KEY + menu.getParentId());
		}
		redisUtils.remove(ALL_TREE_KEY);

		return menuDao.insert(menu) > 0;
	}

	public Boolean update(Menu menu) {

		if (menu.getParentId() != null) {
			redisUtils.remove(LIST_CHILDREN_KEY + menu.getParentId());
		}
		redisUtils.remove(ITEM_KEY + menu.getId());
		redisUtils.remove(ALL_TREE_KEY);
		return menuDao.updateByPrimaryKey(menu) > 0;
	}

	/**
	 * 递归删除菜单
	 * 
	 * @param menu
	 */
	private void recursionDel(Menu menu) {
		if (menu.getType().equals(TYPE_DIR)) {
			Menu record = new Menu();
			record.setParentId(menu.getId());
			List<Menu> list = menuDao.select(record);
			for (Menu m : list) {
				recursionDel(m);
			}
		}
		Resource record = new Resource();
		record.setMenuId(menu.getId());
		List<Resource> resources = resourceDao.select(record);
		for (Resource r : resources) {
			resourceManager.del(r.getId());
		}
		menuDao.deleteByPrimaryKey(menu.getId());
	}

	/**
	 * 递归判断菜单是否被使用
	 * 
	 * @param menu
	 */
	private void recursionMenuUsed(Menu menu) {
		if (menu == null || menu.getId() == null) {
			return;
		}
		String using = isUsed(menu.getId());
		if (!using.equals("")) {
			throw new RuntimeException("菜单或子菜单在角色[" + using + "]中正在使用, 不能进行删除!");
		}
		if (menu.getType().equals(TYPE_DIR)) {
			Menu record = new Menu();
			record.setParentId(menu.getId());
			List<Menu> list = menuDao.select(record);
			for (Menu m : list) {
				recursionMenuUsed(m);
			}
		}
	}

	/**
	 * 是否被使用
	 * 
	 * @param menuId
	 * @return
	 */
	private String isUsed(Long menuId) {
		List<Role> list = roleManager.findByMenu(menuId);
		String roles = "";
		for (Role r : list) {
			if (!"".equals(roles)) {
				roles += ",";
			}
			roles += r.getRoleName();
		}
		return roles;
	}

	@Override
	public List<MenuJsonDto> findAllMenuJson() {
		return menuDao.findAllMenuJson();
	}

	@Override
	public Integer countAllChild(Long parentId) {
		Menu record = new Menu();
		record.setParentId(parentId);
		return menuDao.selectCount(record);
	}

	@Override
	public void initUserMenuUrlsInRedis(Long userId) {
		List<String> urls = new ArrayList<>();
		List<Menu> menus = this.menuDao.findByUserId(userId);
		if (CollectionUtils.isEmpty(menus)) {
			return;
		}
		for (Menu menu : menus) {
			if (menu != null && StringUtils.isNotBlank(menu.getMenuUrl())) {
				urls.add(menu.getMenuUrl().trim().replace("http://", "").replace("https://", ""));
			}
		}
		String key = SessionConstants.USER_MENU_URL_KEY + userId;
		this.redisUtils.remove(key);
		if (CollectionUtils.isEmpty(urls)) {
			return;
		}
		@SuppressWarnings("unchecked")
		BoundSetOperations<Object, Object> ops = this.redisUtils.getRedisTemplate().boundSetOps(key);
		ops.add(urls.toArray());
	}

}
