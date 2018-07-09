package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.ProjectActionRecordConfig;
import com.dili.alm.service.ProjectActionRecordConfigService;
import com.dili.ss.domain.BaseOutput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-07-09 15:58:15.
 */
@Api("/projectActionRecordConfig")
@Controller
@RequestMapping("/projectActionRecordConfig")
public class ProjectActionRecordConfigController {
	@Autowired
	ProjectActionRecordConfigService projectActionRecordConfigService;

	@ApiOperation("跳转到ProjectActionRecordConfig页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "projectActionRecordConfig/index";
	}

	@ApiOperation(value = "查询ProjectActionRecordConfig", notes = "查询ProjectActionRecordConfig，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectActionRecordConfig", paramType = "form", value = "ProjectActionRecordConfig的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectActionRecordConfig> list(ProjectActionRecordConfig projectActionRecordConfig) {
		return projectActionRecordConfigService.list(projectActionRecordConfig);
	}

	@ApiOperation(value = "分页查询ProjectActionRecordConfig", notes = "分页查询ProjectActionRecordConfig，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectActionRecordConfig", paramType = "form", value = "ProjectActionRecordConfig的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(ProjectActionRecordConfig projectActionRecordConfig) throws Exception {
		return projectActionRecordConfigService.listEasyuiPageByExample(projectActionRecordConfig, true).toString();
	}

	@ApiOperation("新增ProjectActionRecordConfig")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectActionRecordConfig", paramType = "form", value = "ProjectActionRecordConfig的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(ProjectActionRecordConfig projectActionRecordConfig) {
		projectActionRecordConfigService.insertSelective(projectActionRecordConfig);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改ProjectActionRecordConfig")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectActionRecordConfig", paramType = "form", value = "ProjectActionRecordConfig的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(ProjectActionRecordConfig projectActionRecordConfig) {
		projectActionRecordConfigService.updateSelective(projectActionRecordConfig);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除ProjectActionRecordConfig")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "ProjectActionRecordConfig的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		projectActionRecordConfigService.delete(id);
		return BaseOutput.success("删除成功");
	}
}