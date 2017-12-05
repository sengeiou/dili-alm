package com.dili.alm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.ProjectVersionFormDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectVersionChangeStateViewDto;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

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
	@Autowired
	private FilesService filesService;
	@Autowired
	private ProjectPhaseService phaseService;

	@ApiOperation("跳转到Milestones页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "milestones/index";
	}

	@ApiOperation("跳转到Files页面")
	@RequestMapping(value = "/files.html", method = RequestMethod.GET)
	public String files(Files files, ModelMap modelMap, HttpServletRequest request) {
		// request.setAttribute("milestonesId", files.getMilestonesId());
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
	public @ResponseBody List<Map> listPage(ProjectVersion projectVersion) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject projectStateProvider = new JSONObject();
		projectStateProvider.put("provider", "projectStateProvider");
		metadata.put("versionState", projectStateProvider);

		JSONObject onlineProvider = new JSONObject();
		onlineProvider.put("provider", "onlineProvider");
		metadata.put("online", onlineProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("actualStartDate", datetimeProvider);
		metadata.put("plannedStartDate", datetimeProvider);
		metadata.put("plannedEndDate", datetimeProvider);
		List<ProjectVersion> list = this.projectVersionService.listByExample(projectVersion);
		return ValueProviderUtils.buildDataByProvider(metadata, list);
	}

	@ApiOperation("新增Milestones")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Milestones", paramType = "form", value = "Milestones的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput insert(ProjectVersionFormDto dto) {
		return projectVersionService.insertSelectiveWithOutput(dto);
	}

	@ApiOperation("修改Milestones")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Milestones", paramType = "form", value = "Milestones的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(ProjectVersionFormDto dto) {
		return projectVersionService.updateSelectiveWithOutput(dto);
	}

	@ApiOperation("删除Milestones")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "Milestones的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		return projectVersionService.deleteWithOutput(id);
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addView(@RequestParam Long projectId, ModelMap map) {
		Project project = this.projectService.get(projectId);
		map.addAttribute("project", project);
		return "project/version/form";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editView(@RequestParam Long projectId, @RequestParam(required = false) Long id, ModelMap map) {
		Project project = this.projectService.get(projectId);
		map.addAttribute("project", project);
		ProjectVersion version = this.projectVersionService.get(id);
		map.addAttribute("model", version);
		Files record = DTOUtils.newDTO(Files.class);
		record.setVersionId(id);
		List<Files> files = this.filesService.list(record);
		map.addAttribute("files", files);
		return "project/version/form";
	}

	@RequestMapping(value = "/changeState", method = RequestMethod.GET)
	public String changeStateView(@RequestParam Long id, ModelMap map) {
		ProjectVersionChangeStateViewDto model = this.projectVersionService.getChangeStateViewData(id);
		Project project = this.projectService.get(model.getVersion().getProjectId());
		List<DataDictionaryValueDto> states = this.projectService.getProjectStates();
		map.addAttribute("model", model.getVersion()).addAttribute("changes", model.getChanges()).addAttribute("project", project).addAttribute("states", states);
		return "project/version/changeState";
	}

	@ResponseBody
	@RequestMapping(value = "/changeState", method = RequestMethod.POST)
	public BaseOutput<Object> changeState(@RequestParam Long id, @RequestParam Integer versionState) {
		return this.projectVersionService.changeState(id, versionState);
	}

	@ResponseBody
	@RequestMapping(value = "/file/list", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Files> listFiles(@RequestParam Long versionId) {
		return this.projectVersionService.listFiles(versionId);
	}
}