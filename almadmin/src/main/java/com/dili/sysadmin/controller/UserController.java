package com.dili.sysadmin.controller;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.dao.UserMapper;
import com.dili.sysadmin.domain.Role;
import com.dili.sysadmin.domain.User;
import com.dili.sysadmin.domain.dto.AddUserDto;
import com.dili.sysadmin.domain.dto.UpdateUserDto;
import com.dili.sysadmin.domain.dto.UpdateUserPasswordDto;
import com.dili.sysadmin.exception.UserException;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.util.WebContent;
import com.dili.sysadmin.service.UserService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-11 16:56:50.
 */
@Api("/user")
@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserService userService;
	@Autowired
	UserMapper userMapper;

	public static final String INDEX_PATH = "user/index";
	public static final String REDIRECT_INDEX_PAGE = "redirect:/user/index.html";

	public static final String ONLINELIST_PATH = "user/onlineList";

	@ApiOperation("跳转到User页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return INDEX_PATH;
	}

	@ApiOperation("跳转到在线User页面")
	@RequestMapping(value = "/onlineList.html", method = RequestMethod.GET)
	public String onlineList(ModelMap modelMap) {
		return ONLINELIST_PATH;
	}

	@ApiOperation("跳转到User测试页面")
	@RequestMapping(value = "/index1", method = RequestMethod.GET)
	public String index1(ModelMap modelMap) {
		return "user/index1";
	}

	@ApiOperation(value = "查询User", notes = "查询User，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "User", paramType = "form", value = "User的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<User> list(@ModelAttribute User user) {
		return userService.list(user);
	}

	@ApiOperation(value = "分页查询User", notes = "分页查询User，返回easyui分页信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "User", paramType = "form", value = "User的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(@ModelAttribute User user) {
		return this.userService.listPageUserDto(user).toString();
	}

	@ApiOperation("新增User")
	@ApiImplicitParams({ @ApiImplicitParam(name = "User", paramType = "form", value = "User的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(@Valid AddUserDto dto, BindingResult br) {
		if (br.hasErrors()) {
			return BaseOutput.failure(br.getFieldError().getDefaultMessage());
		}
		return userService.add(dto);
	}

	@SuppressWarnings("unchecked")
	@ApiOperation("修改User")
	@ApiImplicitParams({ @ApiImplicitParam(name = "User", paramType = "form", value = "User的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> update(@RequestBody UpdateUserDto dto) {
		Map<Object, Object> metadata = new HashMap<>();
		JSONObject userStatusProvider = new JSONObject();
		userStatusProvider.put("provider", "userStatusProvider");
		metadata.put("status", userStatusProvider);
		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("validTimeBegin", datetimeProvider);
		metadata.put("validTimeEnd", datetimeProvider);
		metadata.put("created", datetimeProvider);
		metadata.put("modified", datetimeProvider);
		metadata.put("lastLoginTime", datetimeProvider);

		try {
			User user = userService.update(dto);
			List<Map> list = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(user));
			return BaseOutput.success("修改用户信息成功").setData(list.get(0));
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("添加一个新User")
	@ApiImplicitParams({ @ApiImplicitParam(name = "User", paramType = "form", value = "User的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/add", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> add(@RequestBody AddUserDto dto) {
		Map<Object, Object> metadata = new HashMap<>();
		JSONObject userStatusProvider = new JSONObject();
		userStatusProvider.put("provider", "userStatusProvider");
		metadata.put("status", userStatusProvider);
		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("validTimeBegin", datetimeProvider);
		metadata.put("validTimeEnd", datetimeProvider);
		metadata.put("created", datetimeProvider);
		metadata.put("modified", datetimeProvider);
		metadata.put("lastLoginTime", datetimeProvider);

		return userService.add(dto);
	}

	@ApiOperation("删除User")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "User的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		userService.delete(id);
		return BaseOutput.success("删除成功");
	}

	@ApiOperation("逻辑删除User")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "User的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/logicDelete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> logicDelete(Long id) {
		return userService.logicDelete(id);
	}

	@ApiOperation("禁用User")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "User的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/disableUser", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput disableUser(Long id) {
		try {
			userService.disableUser(id);
			return BaseOutput.success("禁用成功");
		} catch (UserException e) {
			e.printStackTrace();
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("启用User")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "User的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/enableUser", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput enableUser(Long id) {
		try {
			userService.enableUser(id);
			return BaseOutput.success("启用成功");
		} catch (UserException e) {
			e.printStackTrace();
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation(value = "根据角色id查询User", notes = "根据角色id查询User，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "User", paramType = "form", value = "User的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/findUserByRole", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String findUserByRole(@ModelAttribute Role role) {
		List<User> retList = userService.findUserByRole(role.getId());
		return new EasyuiPageOutput(retList.size(), retList).toString();
	}

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/fetchLoginUserInfo")
	public BaseOutput<Object> fetchLoginUserInfo() {
		String str = WebContent.getCookie("u");
		if (StringUtils.isBlank(str)) {
			return BaseOutput.failure("用户未登录");
		}
		UserTicket userTicket = this.userService.fetchLoginUserInfo(Long.valueOf(str));
		return BaseOutput.success().setData(userTicket);
	}

	@ApiOperation("修改User密码")
	@ApiImplicitParams({ @ApiImplicitParam(name = "pwd", paramType = "form", value = "User的主键", required = false, dataType = "string") })
	@RequestMapping(value = "/changePwd", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> changePwd(UpdateUserPasswordDto userPasswordDto) {
		return userService.updateUserPassword(userPasswordDto);
	}

	@ApiOperation(value = "查询在线User", notes = "查询在线User，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "User", paramType = "form", value = "User的form信息", required = false, dataType = "Long") })
	@RequestMapping(value = "/listOnlineUsers", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	List<Map> listOnlineUsers(User user) throws Exception {
		return userService.listOnlineUsers(user);
	}

	@ApiOperation("强制下线User")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "User的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/kickUserOffline", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput kickUserOffline(Long id) {
		userService.kickUserOffline(id);
		return BaseOutput.success("下线成功");
	}

}