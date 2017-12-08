package com.dili.alm.controller;

import com.alibaba.fastjson.JSON;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.dto.ProjectPhaseAddViewDto;
import com.dili.alm.domain.dto.ProjectPhaseEditViewDto;
import com.dili.alm.domain.dto.ProjectPhaseFormDto;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.ValueProviderUtils;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-30 16:05:32.
 */
@Api("/projectPhase")
@Controller
@RequestMapping("/project/phase")
public class ProjectPhaseController {
	@Autowired
	ProjectPhaseService projectPhaseService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectVersionService projectVersionService;
	@Autowired
	private FilesService filesService;

	@ApiOperation("跳转到ProjectPhase页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "projectPhase/index";
	}

	@ApiOperation(value = "查询ProjectPhase", notes = "查询ProjectPhase，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectPhase", paramType = "form", value = "ProjectPhase的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map> list(ProjectPhase projectPhase) {
		return projectPhaseService.listEasyUiModels(projectPhase);
	}

	@ApiOperation(value = "分页查询ProjectPhase", notes = "分页查询ProjectPhase，返回easyui分页信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectPhase", paramType = "form", value = "ProjectPhase的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(ProjectPhase projectPhase) throws Exception {
		return projectPhaseService.listEasyuiPageByExample(projectPhase, true).toString();
	}

	@RequestMapping(value = "/listPhase", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody Object listPhase(ProjectPhase projectPhase) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();
		metadata.put("name", JSON.parse("{provider:'phaseNameProvider'}"));
		List<ProjectPhase> list = projectPhaseService.list(projectPhase);
		return ValueProviderUtils.buildDataByProvider(metadata, list);
	}

	@ApiOperation("新增ProjectPhase")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectPhase", paramType = "form", value = "ProjectPhase的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> insert(ProjectPhaseFormDto projectPhase) {
		return projectPhaseService.addProjectPhase(projectPhase);
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editView(@RequestParam Long id, ModelMap map) {
		ProjectPhaseEditViewDto dto = this.projectPhaseService.getEditViewData(id);
		map.addAttribute("model", dto);
		return "project/phase/form";
	}

	@ApiOperation("修改ProjectPhase")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectPhase", paramType = "form", value = "ProjectPhase的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(ProjectPhaseFormDto projectPhase) {
		return projectPhaseService.updateProjectPhase(projectPhase);
	}

	@ApiOperation("删除ProjectPhase")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "ProjectPhase的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		return projectPhaseService.deleteWithOutput(id);
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addView(@RequestParam Long projectId, ModelMap map) {
		ProjectPhaseAddViewDto dto = this.projectPhaseService.getAddViewData(projectId);
		map.addAttribute("model", dto);
		return "project/phase/form";
	}
}