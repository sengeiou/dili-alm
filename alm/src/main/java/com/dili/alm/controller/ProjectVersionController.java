package com.dili.alm.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.ProjectVersionFormDto;
import com.dili.alm.exceptions.ProjectVersionException;
import com.dili.alm.provider.ProjectVersionProvider;
import com.dili.alm.service.DemandService;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectVersionController.class);
	@Autowired
	ProjectVersionService projectVersionService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private FilesService filesService;
	@Autowired
	private DemandService demandService;

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
	public @ResponseBody List<Map> listPage(ProjectVersion projectVersion) {
		List<ProjectVersion> list = this.projectVersionService.listByExample(projectVersion);
		try {
			return ProjectVersionProvider.parseEasyUiModelList(list);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	@RequestMapping(value = "/insert", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> insert(ProjectVersionFormDto dto) {
		try {
			projectVersionService.addProjectVersion(dto);
			Map<Object, Object> target = ProjectVersionProvider.parseEasyUiModel(dto);
			return BaseOutput.success("新增成功").setData(target);
		} catch (ProjectVersionException e) {
			return BaseOutput.failure(e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return BaseOutput.failure("内部错误");
		}
	}

	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> update(ProjectVersionFormDto dto) {
		try {
			projectVersionService.updateProjectVersion(dto);
			Map<Object, Object> target;
			try {
				target = ProjectVersionProvider.parseEasyUiModel(dto);
				return BaseOutput.success("修改成功").setData(target);
			} catch (Exception e) {
				return BaseOutput.failure(e.getMessage());
			}
		} catch (ProjectVersionException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping("/detail")
	public String detail(@RequestParam Long id, ModelMap map) {
		ProjectVersion version = this.projectVersionService.get(id);
		map.addAttribute("model", version);
		Project project = this.projectService.get(version.getProjectId());
		map.addAttribute("project", project);
		Files record = DTOUtils.newDTO(Files.class);
		record.setVersionId(id);
		List<Files> files = this.filesService.list(record);
		map.addAttribute("files", files);
		return "project/version/detail";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> delete(Long id) {
		try {
			ProjectVersion projectVersion = projectVersionService.get(id);
			projectVersionService.deleteProjectVersion(id);
			return BaseOutput.success("删除成功").setData(String.valueOf(projectVersion.getId() + ":" + projectVersion.getVersion()));
		} catch (ProjectVersionException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addView(@RequestParam Long projectId, ModelMap map) {
		Project project = this.projectService.get(projectId);
		map.addAttribute("project", project);
		ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setProjectId(projectId);
		List<ProjectVersion> selectByExample = this.projectVersionService.list(projectVersion);
		if(selectByExample!=null&&selectByExample.size()>0) {
			map.addAttribute("addVersionNumber", selectByExample.size());
		}else{
			map.addAttribute("addVersionNumber",0);
		}
		return "project/version/form";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String editView(@RequestParam Long id, ModelMap map) {
		ProjectVersion version = this.projectVersionService.get(id);
		map.addAttribute("model", version);
		Project project = this.projectService.get(version.getProjectId());
		map.addAttribute("project", project);
		Files record = DTOUtils.newDTO(Files.class);
		record.setVersionId(id);
		List<Files> files = this.filesService.list(record);
		map.addAttribute("files", files);
		List<Demand> showDemandList = this.demandService.queryDemandListByProjectIdOrVersionIdOrWorkOrderId(version.getId(), AlmConstants.DemandType.PROJECTVERSION.getCode());
		map.addAttribute("showDemandList", showDemandList);
		ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setProjectId(version.getProjectId());
		List<ProjectVersion> selectByExample = this.projectVersionService.list(projectVersion);
		if(selectByExample!=null&&selectByExample.size()>=0) {
			map.addAttribute("updateVersionNumber", selectByExample.size());
		}else{
			map.addAttribute("updateVersionNumber",0);
		}
		return "project/version/form";
	}

	@ResponseBody
	@RequestMapping(value = "/pause", method = RequestMethod.POST)
	public BaseOutput<Object> pause(@RequestParam Long id) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		try {
			this.projectVersionService.pause(id, user.getId());
			return BaseOutput.success();
		} catch (ProjectVersionException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value = "/resume", method = RequestMethod.POST)
	public BaseOutput<Object> resume(@RequestParam Long id) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		try {
			this.projectVersionService.resume(id, user.getId());
			return BaseOutput.success();
		} catch (ProjectVersionException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value = "/complete", method = RequestMethod.POST)
	public BaseOutput<Object> complete(@RequestParam Long id) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		try {
			this.projectVersionService.complete(id, user.getId());
			return BaseOutput.success();
		} catch (ProjectVersionException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value = "/file/list", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Files> listFiles(@RequestParam Long versionId) {
		return this.projectVersionService.listFiles(versionId);
	}
}