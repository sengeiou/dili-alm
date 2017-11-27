package com.dili.alm.controller;

import com.dili.alm.domain.Files;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.ss.domain.BaseOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-20 11:02:17.
 */
@Api("/milestones")
@Controller
@RequestMapping("/project/version")
public class ProjectVersionController {
	@Autowired
	ProjectVersionService projectVersionService;
	@Autowired
	private ProjectService projectService;

	@ApiOperation("跳转到Milestones页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "milestones/index";
	}

	@ApiOperation("跳转到Files页面")
	@RequestMapping(value = "/files.html", method = RequestMethod.GET)
	public String files(Files files, ModelMap modelMap, HttpServletRequest request) {
		request.setAttribute("milestonesId", files.getMilestonesId());
		return "milestones/files";
	}

	@ApiOperation(value = "查询Milestones", notes = "查询Milestones，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Milestones", paramType = "form", value = "Milestones的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectVersion> list(ProjectVersion projectVersion) {
		return projectVersionService.list(projectVersion);
	}

	@ApiOperation(value = "分页查询Milestones", notes = "分页查询Milestones，返回easyui分页信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Milestones", paramType = "form", value = "Milestones的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(ProjectVersion projectVersion) throws Exception {
		return projectVersionService.listEasyuiPageByExample(projectVersion, true).toString();
	}

	@ApiOperation("新增Milestones")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Milestones", paramType = "form", value = "Milestones的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(ProjectVersion projectVersion) {
		return projectVersionService.insertSelectiveWithOutput(projectVersion);
	}

	@ApiOperation("修改Milestones")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Milestones", paramType = "form", value = "Milestones的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(ProjectVersion projectVersion) {
		return projectVersionService.updateSelectiveWithOutput(projectVersion);
	}

	@ApiOperation("删除Milestones")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "Milestones的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		return projectVersionService.deleteWithOutput(id);
	}

	@RequestMapping(value = "/form", method = RequestMethod.GET)
	public String versionForm(@RequestParam Long projectId, @RequestParam(required = false) Long id, ModelMap map) {
		Project project = this.projectService.get(projectId);
		map.addAttribute("project", project);
		if (id != null) {
			ProjectVersion version = this.projectVersionService.get(id);
			map.addAttribute("model", version);
		}
		return "project/version/form";
	}
}