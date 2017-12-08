package com.dili.sysadmin.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dili.sysadmin.domain.Menu;

public class MenuTreeUtil {
	private List<Menu> menu;
	private List<Object> list = new ArrayList<Object>();;
	
	public List<Object> setMenuTree(List<Menu> menus){
		this.menu=menus;
		for (Menu menu : menus) {
			Map<String,Object> maps = new LinkedHashMap<String, Object>();
			if(menu.getParentId()==-1){
				maps.put("id", menu.getId());
				maps.put("created", menu.getCreated());
				maps.put("description", menu.getDescription());
				maps.put("menuUrl", menu.getMenuUrl());
				maps.put("modified", menu.getModified());
				maps.put("name", menu.getName());
				maps.put("orderNumber", menu.getOrderNumber());
				maps.put("parentId", menu.getParentId());
				maps.put("type", menu.getType());
				maps.put("children", setChildren(menu.getId()));
				list.add(maps);
			}
		}
		return list;
	}
	
	public List<Object> setChildren(Long parentId){
		List<Object> obt = new ArrayList<Object>();
		for (Menu menu : menu) {
			Map<String,Object> mapsChild = new LinkedHashMap<String, Object>();
			if(menu.getParentId()==parentId){
				mapsChild.put("id", menu.getId());
				mapsChild.put("created", menu.getCreated());
				mapsChild.put("description", menu.getDescription());
				mapsChild.put("menuUrl", menu.getMenuUrl());
				mapsChild.put("modified", menu.getModified());
				mapsChild.put("name", menu.getName());
				mapsChild.put("orderNumber", menu.getOrderNumber());
				mapsChild.put("parentId", menu.getParentId());
				mapsChild.put("type", menu.getType());
				mapsChild.put("children", setChildren(menu.getId()));
				obt.add(mapsChild);
			}
		}
		return obt;
	}
}
