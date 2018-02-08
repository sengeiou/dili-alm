package com.dili.sysadmin.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.Menu;
import com.dili.sysadmin.domain.MenuType;
import com.dili.sysadmin.domain.dto.MenuListDto;
import com.dili.sysadmin.domain.dto.UpdateMenuDto;
import com.dili.sysadmin.exception.MenuException;
import com.dili.sysadmin.service.MenuService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-14 15:30:18.
 */
@Api("/menu")
@Controller
@RequestMapping("/menu")
public class MenuController {
	@Autowired
	private MenuService menuService;

	@ApiOperation("跳转到Menu页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "menu/index";
	}

	@ApiOperation(value = "查询Menu", notes = "查询Menu，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Menu", paramType = "form", value = "Menu的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Menu> list(@RequestParam Long parentId) {
		Menu query = new Menu();
		query.setParentId(parentId);
		return this.menuService.list(query);
	}

	@RequestMapping(value = "/list.json", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String listJson() {
		List<MenuListDto> list = this.menuService.listContainAndParseResource();
		MenuListDto root = new MenuListDto();
		root.setId(-1L);
		root.setName("权限菜单");
		root.addAttribute("type", MenuType.DIRECTORY);
		list.add(root);
		return JSONArray.toJSONString(list);
	}

	@ApiOperation(value = "分页查询Menu", notes = "分页查询Menu，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Menu", paramType = "form", value = "Menu的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(@ModelAttribute Menu menu) throws Exception {
		return menuService.listEasyuiPageByExample(menu, true).toString();
	}

	@ApiOperation("新增Menu")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Menu", paramType = "form", value = "Menu的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(@ModelAttribute Menu menu) {
		try {
			menuService.insertWithOutput(menu);
			return BaseOutput.success().setData(menu);
		} catch (MenuException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("修改Menu")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Menu", paramType = "form", value = "Menu的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> update(@ModelAttribute @Valid UpdateMenuDto command, BindingResult br) {
		return menuService.update(command);
	}

	@ApiOperation("删除Menu")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "Menu的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> delete(Long id) {
		return menuService.deleteCheckIsBinding(id);
	}
}