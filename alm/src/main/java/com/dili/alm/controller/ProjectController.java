package com.dili.alm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.UploadProjectFileDto;
import com.dili.alm.exceptions.ProjectException;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.TeamService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tk.mybatis.mapper.entity.Example;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-18 17:22:54.
 */
@Api("/project")
@Controller
@RequestMapping("/project")
public class ProjectController {
	@Autowired
	ProjectService projectService;
	@Autowired
	private UserRpc userRPC;
	@Autowired
	private TeamService teamService;
	@Autowired
	private FilesService filesService;

	private void refreshMember() {
		AlmCache.USER_MAP.clear();
		BaseOutput<List<User>> output = this.userRPC.list(new User());
		if (!output.isSuccess()) {
			return;
		}
		List<User> users = output.getData();
		if (CollectionUtils.isEmpty(users)) {
			return;
		}
		users.forEach(u -> {
			AlmCache.USER_MAP.put(u.getId(), u);
		});
	}

	@ApiOperation("跳转到Project页面")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "project/index";
	}

	@SuppressWarnings("rawtypes")
	@ApiOperation(value = "查询Project", notes = "查询Project，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Project", paramType = "form", value = "Project的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map> list(Project project) {
		refreshMember();
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

		// 测试数据
		List<Project> list = this.projectService.listByExample(project);
		try {
			return ValueProviderUtils.buildDataByProvider(metadata, list);
		} catch (Exception e) {
			return null;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/list.json", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Project> listJson(Project project) {
		refreshMember();
		return this.projectService.list(project);
	}

	@ApiOperation(value = "分页查询Project", notes = "分页查询Project，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Project", paramType = "form", value = "Project的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(Project project) throws Exception {
		refreshMember();
		return projectService.listEasyuiPageByExample(project, true).toString();
	}

	@ApiOperation("新增Project")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Project", paramType = "form", value = "Project的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> insert(Project project) {
		try {
			return projectService.insertAfterCheck(project);
		} catch (ProjectException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("修改Project")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Project", paramType = "form", value = "Project的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(Project project) {
		return projectService.updateAfterCheck(project);
	}

	@ApiOperation("删除Project")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "Project的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> delete(Long id) {
		return projectService.deleteBeforeCheck(id);
	}

	@ResponseBody
	@RequestMapping(value = "/type.json", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<DataDictionaryValueDto> projectTypeJson() {
		return this.projectService.getPojectTypes();
	}

	@ResponseBody
	@RequestMapping(value = "/state.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<DataDictionaryValueDto> projectStateJson() {
		return this.projectService.getProjectStates();
	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String detail(@RequestParam Long id, @RequestParam(defaultValue = "false") Boolean editable,
			@RequestParam(required = false) String backUrl, ModelMap map) {
		try {
			Map<Object, Object> model = this.projectService.getDetailViewData(id);
			UserTicket user = SessionContext.getSessionContext().getUserTicket();
			Boolean projectManager = this.teamService.teamMemberIsProjectManager(user.getId(), id);
			Boolean projectMember = this.teamService.currentUserIsTeamMember(user.getId(), id);
			map.addAttribute("model", model).addAttribute("editable", editable)
					.addAttribute("projectManager", projectManager).addAttribute("projectMember", projectMember)
					.addAttribute("loginUserId", user.getId()).addAttribute("backUrl",
							StringUtils.isNotBlank(backUrl) ? URLDecoder.decode(backUrl, "UTF-8") : "");
		} catch (Exception e) {
			return null;
		}
		return "project/detail";
	}

	@RequestMapping(value = "/getProjectList")
	public @ResponseBody Object getProjectList(Project project) throws Exception {
		List<Project> list = projectService.list(project);
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
		map.addAttribute("project", project).addAttribute("creator",
				SessionContext.getSessionContext().getUserTicket());
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

		JSONObject projectPhaseProvider = new JSONObject();
		projectPhaseProvider.put("provider", "projectPhaseProvider");
		metadata.put("phaseId", projectPhaseProvider);

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