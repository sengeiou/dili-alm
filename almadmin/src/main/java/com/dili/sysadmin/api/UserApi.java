package com.dili.sysadmin.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.Role;
import com.dili.sysadmin.domain.User;
import com.dili.sysadmin.domain.dto.UserDepartmentRole;
import com.dili.sysadmin.domain.dto.UserDepartmentRoleQuery;
import com.dili.sysadmin.service.RoleService;
import com.dili.sysadmin.service.UserService;
import com.netflix.governator.annotations.binding.Request;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-11 16:56:50.
 */
@Api("/user")
@Controller
@RequestMapping("/userApi")
public class UserApi {
	@Autowired
	UserService userService;
	@Autowired
	private RoleService roleService;

	@ApiOperation(value = "查询User列表接口", notes = "查询User列表接口，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "User", paramType = "form", value = "User的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<List<User>> list(@RequestBody(required = false) User user) {
		return BaseOutput.success().setData(userService.list(user));
	}

	@RequestMapping(value = "listUserByRole")
	@ResponseBody
	public BaseOutput<List<User>> listUserByRole(@RequestBody Long id) {
		return BaseOutput.success().setData(userService.findUserByRole(id));
	}

	@ResponseBody
	@RequestMapping(value = "/listByExample", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<User> listByExample(@RequestBody(required = false) User user) {
		return BaseOutput.success().setData(this.userService.listByExample(user));
	}

	@RequestMapping(value = "/listUserRole")
	@ResponseBody
	public BaseOutput<List<Role>> listUserRole(@RequestBody Long userId) {
		return BaseOutput.success().setData(this.roleService.findByUserId(userId));
	}

	@RequestMapping(value = "/findByUserId")
	@ResponseBody
	public BaseOutput<User> findByUserId(@RequestBody Long userId) {
		return BaseOutput.success().setData(this.userService.get(userId));
	}

	@ResponseBody
	@RequestMapping(value = "/findUserContainDepartmentAndRole", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<List<UserDepartmentRole>> findUserContainDepartmentAndRole(@RequestBody UserDepartmentRoleQuery query) {
		List<UserDepartmentRole> list = this.userService.findUserContainDepartmentAndRole(query);
		return BaseOutput.success().setData(list);
	}
}