package com.dili.alm.controller;

import com.dili.alm.domain.ProjectChange;
import com.dili.alm.service.ProjectChangeService;
import com.dili.ss.domain.BaseOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-30 17:21:55.
 */
@Api("/projectChange")
@Controller
@RequestMapping("/projectChange")
public class ProjectChangeController {
	@Autowired
	ProjectChangeService projectChangeService;

	@ApiOperation("跳转到ProjectChange页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "projectChange/index";
	}

	@ApiOperation(value = "查询ProjectChange", notes = "查询ProjectChange，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectChange", paramType = "form", value = "ProjectChange的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectChange> list(ProjectChange projectChange) {
		return projectChangeService.list(projectChange);
	}

	@ApiOperation(value = "分页查询ProjectChange", notes = "分页查询ProjectChange，返回easyui分页信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectChange", paramType = "form", value = "ProjectChange的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(ProjectChange projectChange) throws Exception {
		return projectChangeService.listEasyuiPageByExample(projectChange, true).toString();
	}

	@ApiOperation("新增ProjectChange")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectChange", paramType = "form", value = "ProjectChange的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(ProjectChange projectChange) {
		projectChangeService.insertSelective(projectChange);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改ProjectChange")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectChange", paramType = "form", value = "ProjectChange的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(ProjectChange projectChange) {
		projectChangeService.updateSelective(projectChange);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除ProjectChange")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "ProjectChange的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		projectChangeService.delete(id);
		return BaseOutput.success("删除成功");
	}
}