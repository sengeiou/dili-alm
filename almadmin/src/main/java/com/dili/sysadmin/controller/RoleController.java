package com.dili.sysadmin.controller;

import com.alibaba.fastjson.JSON;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.Role;
import com.dili.sysadmin.domain.dto.EditRoleMenuAndResouceDto;
import com.dili.sysadmin.domain.dto.RoleUserDto;
import com.dili.sysadmin.domain.dto.UpdateRoleMenuResourceDto;
import com.dili.sysadmin.exception.RoleException;
import com.dili.sysadmin.service.DataAuthService;
import com.dili.sysadmin.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:51.
 */
@Api("/role")
@Controller
@RequestMapping("/role")
public class RoleController {
	@Autowired
	RoleService roleService;
	@Autowired
	private DataAuthService dataAuthService;

	public static final String INDEX_PATH = "role/index";
	public static final String REDIRECT_INDEX_PAGE = "redirect:/role/index.html";

	@ApiOperation("跳转到Role页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return INDEX_PATH;
	}

	@ApiOperation(value = "查询Role", notes = "查询Role，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Role", paramType = "form", value = "Role的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput list(@ModelAttribute Role role) {
		List<Role> retList = roleService.list(role);
		return BaseOutput.success("查询成功").setData(retList);
	}

	@RequestMapping(value = "/list.json", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Role> listJson(Role role) {
		return roleService.list(role);
	}

	@ApiOperation(value = "查询某个用户的Role", notes = "查询某个用户的Role，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Role", paramType = "form", value = "Role的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listByUserId", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput listByUserId(Long userid) {
		List<Role> retList = roleService.findByUserId(userid);
		return BaseOutput.success("查询成功").setData(retList);
	}

	@ApiOperation(value = "查询某个用户未绑定的Role", notes = "查询某个用户未绑定的Role，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Role", paramType = "form", value = "Role的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listNotBindByUserId", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput listNotBindByUserId(Long userid, Long departmentId) {
		List<Role> retList = roleService.findNotBindWithUserByDepartmentId(userid, departmentId);
		return BaseOutput.success("查询成功").setData(retList);
	}

	@ApiOperation(value = "分页查询Role", notes = "分页查询Role，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Role", paramType = "form", value = "Role的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(@ModelAttribute Role role) throws Exception {
		return roleService.listEasyuiPageByExample(role, true).toString();
	}

	@ApiOperation("新增Role")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Role", paramType = "form", value = "Role的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(@ModelAttribute Role role) {
		return roleService.insertAndGet(role);
	}

	@ApiOperation("修改Role")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Role", paramType = "form", value = "Role的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> update(Role role, HttpServletRequest request) {
		return roleService.updateAfterCheck(role);
	}

	@ApiOperation("删除Role")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "Role的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		roleService.delete(id);
		return BaseOutput.success("删除成功");
	}

	@ApiOperation("删除Role需要判断角色未绑定任何用户")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "Role的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/deleteIfUserNotBind", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput deleteIfUserNotBind(Long id) {
		return roleService.deleteIfUserNotBind(id);
	}

	@RequestMapping(value = "/roleMenuAndResource.html")
	public String roleMenuAndResourceView(@RequestParam Long roleId, ModelMap map) {
		map.addAttribute("roleId", roleId);
		return "role/roleMenuAndResource";
	}

	@ResponseBody
	@RequestMapping(value = "/roleMenuAndResource.json")
	public BaseOutput roleMenuAndResourceData(@RequestParam Long roleId) {
		EditRoleMenuAndResouceDto dto = this.roleService.queryEditRoleMenuAndService(roleId);
		return BaseOutput.success().setData(dto);
	}

	@ResponseBody
	@RequestMapping(value = "/updateRoleMenuResource")
	public BaseOutput<Object> updateRoleMenuResource(@RequestBody UpdateRoleMenuResourceDto dto, BindingResult br) {
		if (br.hasErrors()) {
			return BaseOutput.failure(br.getFieldError().getDefaultMessage());
		}
		try {
			this.roleService.updateRoleMenuResource(dto);
			return BaseOutput.success();
		} catch (RoleException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("解绑Role和User")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Dto", paramType = "form", value = "Role的主键", required = false, dataType = "string") })
	@RequestMapping(value = "/unbindRoleUser", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput unbindRoleUser(@RequestBody RoleUserDto dto) {
		roleService.unbindRoleUser(dto);
		return BaseOutput.success("解绑成功");
	}

	@ResponseBody
	@RequestMapping(value = "/listByDepartment", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> listByDepartment(@RequestParam Long departmentId) {
		if (departmentId == null) {
			return this.list(new Role());
		}
		return this.roleService.listByDepartment(departmentId);
	}
}