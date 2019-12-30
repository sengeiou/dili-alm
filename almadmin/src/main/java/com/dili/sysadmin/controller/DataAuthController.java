package com.dili.sysadmin.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.DataAuth;
import com.dili.sysadmin.domain.dto.DataAuthTypeDto;
import com.dili.sysadmin.domain.dto.EditRoleDataAuthDto;
import com.dili.sysadmin.domain.dto.EditUserDataAuthDto;
import com.dili.sysadmin.domain.dto.UpdateRoleDataAuthDto;
import com.dili.sysadmin.domain.dto.UpdateUserDataAuthDto;
import com.dili.sysadmin.domain.dto.UserDataAuthDto;
import com.dili.sysadmin.service.DataAuthService;
import com.dili.sysadmin.service.UserDataAuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:51.
 */
@Api("/dataAuth")
@Controller
@RequestMapping("/dataAuth")
public class DataAuthController {

	private final static Logger log = LoggerFactory.getLogger(DataAuthController.class);

	@Autowired
	DataAuthService dataAuthService;
	@Autowired
	UserDataAuthService userDataAuthService;
	
	@ApiOperation("跳转到DataAuth页面")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "dataAuth/index";
	}

	@ApiOperation(value = "查询DataAuth", notes = "查询DataAuth，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "DataAuth", paramType = "form", value = "DataAuth的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<DataAuth> list(@ModelAttribute DataAuth dataAuth) {
		return dataAuthService.list(dataAuth);
	}

	@ApiOperation(value = "分页查询DataAuth", notes = "分页查询DataAuth，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "DataAuth", paramType = "form", value = "DataAuth的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(@ModelAttribute DataAuth dataAuth) throws Exception {
		return dataAuthService.listEasyuiPageByExample(dataAuth, true).toString();
	}

	@ApiOperation("新增DataAuth")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "DataAuth", paramType = "form", value = "DataAuth的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(@ModelAttribute DataAuth dataAuth) {
		dataAuthService.insertSelective(dataAuth);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改DataAuth")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "DataAuth", paramType = "form", value = "DataAuth的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(@ModelAttribute DataAuth dataAuth) {
		dataAuthService.updateSelective(dataAuth);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除DataAuth")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "DataAuth的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		dataAuthService.delete(id);
		return BaseOutput.success("删除成功");
	}

	@RequestMapping(value = "/editUserDataAuth.html")
	public String editUserDataAuthView(@RequestParam(required = false, defaultValue = "1L") Long userId, ModelMap modelMap) {
		List<DataAuthTypeDto> types = this.dataAuthService.fetchDataAuthType();
		if (CollectionUtils.isNotEmpty(types)) {
			modelMap.addAttribute("type", types.get(0).getType());
		}
		modelMap.addAttribute("userId", userId).addAttribute("allTypes", types);
		return "dataAuth/editUserDataAuth";
	}

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/editUserDataAuth.json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseOutput<Object> editUserDataAuth(@RequestParam Long userId, @RequestParam String type) {
		EditUserDataAuthDto model = this.dataAuthService.queryEditUserDataAuth(userId, type);
		return BaseOutput.success().setData(model);
	}

	@ResponseBody
	@RequestMapping(value = "/updateUserDataAuth.json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseOutput<Object> updateUserDataAuth(@RequestBody @Valid UpdateUserDataAuthDto dto) {
		return this.dataAuthService.updateUserDataAuth(dto);
	}

	@RequestMapping(value = "/editRoleDataAuth.html")
	public String roleDataAuthView(@RequestParam Long roleId, ModelMap map) {
		List<DataAuthTypeDto> types = this.dataAuthService.fetchDataAuthType();
		if (CollectionUtils.isNotEmpty(types)) {
			map.addAttribute("type", types.get(0).getType());
		}
		map.addAttribute("roleId", roleId).addAttribute("allTypes", types);
		return "role/roleDataAuth";
	}

	@ResponseBody
	@RequestMapping(value = "/editRoleDataAuth.json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseOutput<Object> roleDataAuthJson(@RequestParam Long roleId, @RequestParam String type) {
		EditRoleDataAuthDto model = this.dataAuthService.queryEditRoleDataAuth(roleId, type);
		return BaseOutput.success().setData(model);
	}
	
	@ResponseBody
	@RequestMapping(value = "/updateRoleDataAuth.json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseOutput<Object> updateRoleDataAuth(@RequestBody @Valid UpdateRoleDataAuthDto dto) {
		return this.dataAuthService.updateRoleDataAuth(dto);
	}
	
	@ResponseBody
	@RequestMapping(value = "/updateUserDataAuthList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseOutput<Object> updateUserDataAuthList() {
		userDataAuthService.updateUserDataAuthList();
		return  BaseOutput.success("添加失败");
	}
}