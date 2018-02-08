package com.dili.sysadmin.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.DepartmentRole;
import com.dili.sysadmin.domain.dto.DataDictionaryDto;
import com.dili.sysadmin.domain.dto.DataDictionaryValueDto;
import com.dili.sysadmin.rpc.DataDictrionaryRPC;
import com.dili.sysadmin.service.DepartmentRoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-07 15:11:37.
 */
@Api("/departmentRole")
@Controller
@RequestMapping("/departmentRole")
public class DepartmentRoleController {

	private static final String RANK_CODE = "rank";

	private static final Logger LOG = LoggerFactory.getLogger(DepartmentRoleController.class);

	@Autowired
	DepartmentRoleService departmentRoleService;
	@Autowired
	private DataDictrionaryRPC dataDictrionaryRPC;

	@ApiOperation("跳转到DepartmentRole页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "departmentRole/index";
	}

	@ApiOperation(value = "查询DepartmentRole", notes = "查询DepartmentRole，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "DepartmentRole", paramType = "form", value = "DepartmentRole的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map> list(@ModelAttribute DepartmentRole departmentRole) {
		return departmentRoleService.listByProvider(departmentRole);
	}

	@ApiOperation(value = "分页查询DepartmentRole", notes = "分页查询DepartmentRole，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "DepartmentRole", paramType = "form", value = "DepartmentRole的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(@ModelAttribute DepartmentRole departmentRole) throws Exception {
		return departmentRoleService.listEasyuiPageByExample(departmentRole, true).toString();
	}

	@ApiOperation("新增DepartmentRole")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "DepartmentRole", paramType = "form", value = "DepartmentRole的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(@ModelAttribute DepartmentRole departmentRole) {
		return departmentRoleService.insertAndGet(departmentRole);
	}

	@ApiOperation("修改DepartmentRole")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "DepartmentRole", paramType = "form", value = "DepartmentRole的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(@ModelAttribute DepartmentRole departmentRole) {
		return departmentRoleService.updateAndGet(departmentRole);
	}

	@ApiOperation("删除DepartmentRole")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "DepartmentRole的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> delete(Long id) {
		return departmentRoleService.deleteAfterCheck(id);
	}

	@ResponseBody
	@RequestMapping(value = "/rank.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<DataDictionaryValueDto> getRanks() {
		BaseOutput<DataDictionaryDto> output = this.dataDictrionaryRPC.findDataDictionaryByCode(RANK_CODE);
		if (output == null || !output.isSuccess()) {
			LOG.error(output.getResult());
			return null;
		}
		DataDictionaryDto dd = output.getData();
		if (dd == null) {
			return null;
		}
		return dd.getValues();
	}
}