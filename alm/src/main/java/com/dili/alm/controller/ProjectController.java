package com.dili.alm.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectQueryDto;
import com.dili.alm.domain.dto.UploadProjectFileDto;
import com.dili.alm.exceptions.ProjectException;
import com.dili.alm.provider.ProjectProvider;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.TeamService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-18 17:22:54.
 */
@Api("/project")
@Controller
@RequestMapping("/project")
public class ProjectController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

	private static final String DATA_AUTH_TYPE = "Project";

	@Autowired
	ProjectService projectService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private FilesService filesService;

	@ResponseBody
	@RequestMapping(value = "/pause", method = RequestMethod.POST)
	public BaseOutput<Object> pause(@RequestParam Long id) {
		try {
			this.projectService.pause(id);
			return BaseOutput.success();
		} catch (ProjectException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value = "/resume", method = RequestMethod.POST)
	public BaseOutput<Object> resume(@RequestParam Long id) {
		try {
			this.projectService.resume(id);
			return BaseOutput.success();
		} catch (ProjectException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("跳转到Project页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("user", user);
		return "project/index";
	}

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "查询Project", notes = "查询Project，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Project", paramType = "form", value = "Project的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map> list(ProjectQueryDto project) {
		try {
			AlmCache.clearCache();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return new ArrayList<>(0);
		}
		List<Long> projectIds = new ArrayList<>(dataAuths.size());
		dataAuths.forEach(dataAuth -> projectIds.add(Long.valueOf(dataAuth.get("dataId").toString())));
		project.setProjectIds(projectIds);
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject projectTypeProvider = new JSONObject();
		projectTypeProvider.put("provider", "projectTypeProvider");
		metadata.put("type", projectTypeProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("created", datetimeProvider);
		metadata.put("modified", datetimeProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("projectManager", memberProvider);
		metadata.put("testManager", memberProvider);
		metadata.put("productManager", memberProvider);

		if (project.getActualStartDate() != null) {
			project.setActualBeginStartDate(project.getActualStartDate());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(project.getActualStartDate());
			calendar.set(Calendar.HOUR, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			project.setActualEndStartDate(calendar.getTime());
			project.setActualStartDate(null);
		}
		// 测试数据
		List<Project> list = this.projectService.listByExample(project);
		try {
			return ValueProviderUtils.buildDataByProvider(metadata, list);
		} catch (Exception e) {
			return null;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/list.json", method = { RequestMethod.GET, RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Project> listJson(Project project) {
		try {
			AlmCache.clearCache();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return this.projectService.list(project);
	}

	@ResponseBody
	@RequestMapping(value = "/listViewData.json", method = { RequestMethod.GET, RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Map> listViewDataJson(Project project) {
		try {
			AlmCache.clearCache();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		List<Project> list = this.projectService.list(project);
		try {
			return ProjectProvider.parseEasyUiModelList(list);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	@ApiOperation(value = "分页查询Project", notes = "分页查询Project，返回easyui分页信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Project", paramType = "form", value = "Project的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(ProjectQueryDto project) throws Exception {
		try {
			AlmCache.clearCache();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		if (project.getProjectState() == null) {
			project.setProjectStates(Arrays.asList(ProjectState.NOT_START.getValue(), ProjectState.IN_PROGRESS.getValue()));
		}
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return null;
		}
		List<Long> projectIds = new ArrayList<>(dataAuths.size());
		dataAuths.forEach(dataAuth -> projectIds.add(Long.valueOf(dataAuth.get("dataId").toString())));
		project.setProjectIds(projectIds);
		if (project.getActualStartDate() != null) {
			project.setActualBeginStartDate(project.getActualStartDate());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(project.getActualStartDate());
			calendar.set(Calendar.HOUR, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			project.setActualEndStartDate(calendar.getTime());
			project.setActualStartDate(null);
		}
		project.setSort("created");
		project.setOrder("desc");
		return projectService.listEasyuiPageByExample(project, true).toString();
	}

	@ApiOperation("新增Project")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Project", paramType = "form", value = "Project的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> insert(Project project) {
		try {
			return projectService.insertAfterCheck(project);
		} catch (ProjectException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("修改Project")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Project", paramType = "form", value = "Project的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(Project project) {
		return projectService.updateAfterCheck(project);
	}

	@ApiOperation("删除Project")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "Project的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> delete(Long id) {
		return projectService.deleteBeforeCheck(id);
	}

	@ResponseBody
	@RequestMapping(value = "/type.json", method = { RequestMethod.GET, RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<DataDictionaryValueDto> projectTypeJson() {
		return this.projectService.getPojectTypes();
	}

	@ResponseBody
	@RequestMapping(value = "/state.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<DataDictionaryValueDto> projectStateJson() {
		return this.projectService.getProjectStates();
	}

	@RequestMapping(value = "/detail.html", method = RequestMethod.GET)
	public String detail(@RequestParam Long id, @RequestParam(defaultValue = "false") Boolean editable, @RequestParam(required = false) String backUrl, ModelMap map) {
		try {
			Map<Object, Object> model = this.projectService.getDetailViewData(id);
			UserTicket user = SessionContext.getSessionContext().getUserTicket();
			Boolean projectManager = this.teamService.teamMemberIsProjectManager(user.getId(), id);
			Boolean projectMember = this.teamService.currentUserIsTeamMember(user.getId(), id);
			if (!model.get("projectState").equals(ProjectState.NOT_START.getValue()) && !model.get("projectState").equals(ProjectState.IN_PROGRESS.getValue())) {
				editable = false;
			}
			map.addAttribute("model", model).addAttribute("editable", editable).addAttribute("projectManager", projectManager).addAttribute("projectMember", projectMember)
					.addAttribute("loginUserId", user.getId()).addAttribute("backUrl", StringUtils.isNotBlank(backUrl) ? URLDecoder.decode(backUrl, "UTF-8") : "");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "project/detail";
	}

	@RequestMapping(value = "/getProjectList")
	public @ResponseBody Object getProjectList(Project project, @RequestParam(defaultValue = "false", required = false) Boolean queryAll,
			@RequestParam(required = false, defaultValue = "false") Boolean dataAuth) throws Exception {
		Example example = new Example(Project.class);
		Criteria criteria = example.createCriteria();
		if (dataAuth) {
			List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
			if (CollectionUtils.isEmpty(dataAuths)) {
				return new ArrayList<>(0);
			}
			List<Long> projectIds = new ArrayList<>(dataAuths.size());
			dataAuths.forEach(da -> projectIds.add(Long.valueOf(da.get("dataId").toString())));
			criteria.andIn("id", projectIds);
		}
		if (!queryAll) {
			criteria.andNotEqualTo("projectState", ProjectState.CLOSED.getValue());
		}
		List<Project> list = this.projectService.selectByExample(example);
		Map<Object, Object> metadata = new HashMap<>();
		metadata.put("type", JSON.parse("{provider:'projectTypeProvider'}"));
		metadata.put("projectManager", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("dep", JSON.parse("{provider:'depProvider'}"));
		metadata.put("businessOwner", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("startDate", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("endDate", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("actualEndDate", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("estimateLaunchDate", JSON.parse("{provider:'dateProvider'}"));
		return ValueProviderUtils.buildDataByProvider(metadata, list);
	}

	@RequestMapping(value = "/uploadFileView", method = RequestMethod.GET)
	public String uploadFileView(@RequestParam Long projectId, ModelMap map) {
		Project project = this.projectService.get(projectId);
		map.addAttribute("project", project).addAttribute("creator", SessionContext.getSessionContext().getUserTicket());
		return "project/uploadFile";
	}

	@ResponseBody
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public BaseOutput<Object> uploadFile(UploadProjectFileDto dto) {
		try {
			return this.projectService.uploadFileAndSendMail(dto);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value = "/fileTypes.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<DataDictionaryValueDto> getFileTypes() {
		return this.projectService.getFileTypes();
	}

	@RequestMapping(value = "/files/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map> list(Files files) {
		Example example = new Example(Files.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("projectId", files.getProjectId()).andIsNotNull("type");
		List<Files> list = this.filesService.selectByExample(example);
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject fileTypeProvider = new JSONObject();
		fileTypeProvider.put("provider", "fileTypeProvider");
		metadata.put("type", fileTypeProvider);

		JSONObject projectVersionProvider = new JSONObject();
		projectVersionProvider.put("provider", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("createMemberId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("created", datetimeProvider);

		try {
			return ValueProviderUtils.buildDataByProvider(metadata, list);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}