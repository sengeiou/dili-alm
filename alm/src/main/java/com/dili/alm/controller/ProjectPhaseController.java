package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.ss.domain.BaseOutput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-30 16:05:32.
 */
@Api("/projectPhase")
@Controller
@RequestMapping("/projectPhase")
public class ProjectPhaseController {
	@Autowired
	ProjectPhaseService projectPhaseService;

	@ApiOperation("跳转到ProjectPhase页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "projectPhase/index";
	}

	@ApiOperation(value = "查询ProjectPhase", notes = "查询ProjectPhase，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectPhase", paramType = "form", value = "ProjectPhase的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectPhase> list(ProjectPhase projectPhase) {
		return projectPhaseService.list(projectPhase);
	}

	@ApiOperation(value = "分页查询ProjectPhase", notes = "分页查询ProjectPhase，返回easyui分页信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectPhase", paramType = "form", value = "ProjectPhase的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(ProjectPhase projectPhase) throws Exception {
		return projectPhaseService.listEasyuiPageByExample(projectPhase, true).toString();
	}

	@ApiOperation("新增ProjectPhase")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectPhase", paramType = "form", value = "ProjectPhase的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(ProjectPhase projectPhase) {
		projectPhaseService.insertSelective(projectPhase);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改ProjectPhase")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectPhase", paramType = "form", value = "ProjectPhase的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(ProjectPhase projectPhase) {
		projectPhaseService.updateSelective(projectPhase);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除ProjectPhase")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "ProjectPhase的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		projectPhaseService.delete(id);
		return BaseOutput.success("删除成功");
	}
}