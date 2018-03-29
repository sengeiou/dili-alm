package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.ProjectOnlineApplyUpdateDto;
import com.dili.alm.domain.dto.ProjectOnlineSubsystemDto;
import com.dili.alm.exceptions.ProjectOnlineApplyException;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectOnlineApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.impl.ProjectOnlineApplyServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import tk.mybatis.mapper.entity.Example;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-13 15:31:10.
 */
@Api("/projectOnlineApply")
@Controller
@RequestMapping("/projectOnlineApply")
public class ProjectOnlineApplyController {

	private static final String DATA_AUTH_TYPE = "Project";

	@Autowired
	ProjectOnlineApplyService projectOnlineApplyService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private FilesService fileService;
	@Autowired
	private UserRpc userRpc;
	@Autowired
	private DepartmentRpc deptRpc;
	private static final String EMAIL_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

	@ApiOperation("跳转到ProjectOnlineApply页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "projectOnlineApply/index";
	}

	@ResponseBody
	@RequestMapping(value = "/saveAndSubmit", method = RequestMethod.POST)
	public BaseOutput<Object> saveAndSubmit(@Valid ProjectOnlineApplyAddDto apply, BindingResult br) {
		if (br.hasErrors()) {
			return BaseOutput.failure(br.getFieldError().getDefaultMessage());
		}
		String error = this.validate(apply);
		if (StringUtils.isNotBlank(error)) {
			return BaseOutput.failure(error);
		}
		ProjectOnlineApplyUpdateDto dto = this.parseServiceModel(apply);
		try {
			projectOnlineApplyService.saveAndSubmit(dto);
			return BaseOutput.success().setData(ProjectOnlineApplyServiceImpl.buildApplyViewModel(dto));
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addView(ModelMap modelMap) {
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return null;
		}
		List<Long> projectIds = new ArrayList<>();
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("dataId").toString())));
		Example example = new Example(ProjectOnlineApply.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", projectIds);
		List<Project> projects = this.projectService.selectByExample(example);
		modelMap.addAttribute("projects", projects);
		Department deptQuery = new Department();
		deptQuery.setCode(AlmConstants.OPERATION_DEPARTMENT_CODE);
		BaseOutput<List<Department>> deptOutput = this.deptRpc.list(deptQuery);
		if (deptOutput.isSuccess() && CollectionUtils.isNotEmpty(deptOutput.getData())) {
			Long departmentId = deptOutput.getData().get(0).getId();
			User userQuery = new User();
			userQuery.setDepartmentId(departmentId);
			BaseOutput<List<User>> userOutput = this.userRpc.list(userQuery);
			if (userOutput.isSuccess() && CollectionUtils.isNotEmpty(userOutput.getData())) {
				StringBuilder sb = new StringBuilder();
				userOutput.getData().forEach(u -> sb.append(u.getEmail()).append(";"));
				modelMap.addAttribute("opUsers", sb.substring(0, sb.length() - 1));
			}
		}
		return "projectOnlineApply/add";
	}

	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String updateView(@RequestParam Long id, ModelMap modelMap) {
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return null;
		}
		List<Long> projectIds = new ArrayList<>();
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("dataId").toString())));
		Example example = new Example(ProjectOnlineApply.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", projectIds);
		List<Project> projects = this.projectService.selectByExample(example);
		modelMap.addAttribute("projects", projects);
		List<Project> plist = this.projectService.list(null);
		modelMap.addAttribute("plist", plist).addAttribute("ulist", AlmCache.USER_MAP.values());
		try {
			ProjectOnlineApply dto = this.projectOnlineApplyService.getEditViewDataById(id);
			Map<Object, Object> model = ProjectOnlineApplyServiceImpl.buildApplyViewModel(dto);
			modelMap.addAttribute("apply", model);
			return "projectOnlineApply/update";
		} catch (ProjectOnlineApplyException e) {
			return "";
		}
	}

	@ApiOperation(value = "查询ProjectOnlineApply", notes = "查询ProjectOnlineApply，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectOnlineApply", paramType = "form", value = "ProjectOnlineApply的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectOnlineApply> list(ProjectOnlineApply projectOnlineApply) {
		return projectOnlineApplyService.list(projectOnlineApply);
	}

	@ApiOperation(value = "分页查询ProjectOnlineApply", notes = "分页查询ProjectOnlineApply，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectOnlineApply", paramType = "form", value = "ProjectOnlineApply的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(ProjectOnlineApplyListQueryDto projectOnlineApply) throws Exception {
		return projectOnlineApplyService.listEasyuiPageByExample(projectOnlineApply, true).toString();
	}

	@SuppressWarnings("unchecked")
	@ApiOperation("新增ProjectOnlineApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectOnlineApply", paramType = "form", value = "ProjectOnlineApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/add", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> insert(@Valid ProjectOnlineApplyAddDto apply, BindingResult br) {
		if (br.hasErrors()) {
			return BaseOutput.failure(br.getFieldError().getDefaultMessage());
		}
		String error = this.validate(apply);
		if (StringUtils.isNotBlank(error)) {
			return BaseOutput.failure(error);
		}
		ProjectOnlineApplyUpdateDto dto = this.parseServiceModel(apply);
		try {
			projectOnlineApplyService.saveOrUpdate(dto);
			ProjectOnlineApply vm = this.projectOnlineApplyService.get(dto.getId());
			return BaseOutput.success().setData(ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm));
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("修改ProjectOnlineApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectOnlineApply", paramType = "form", value = "ProjectOnlineApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> update(@Valid ProjectOnlineApplyAddDto apply, BindingResult br) {
		if (br.hasErrors()) {
			return BaseOutput.failure(br.getFieldError().getDefaultMessage());
		}
		String error = this.validate(apply);
		if (StringUtils.isNotBlank(error)) {
			return BaseOutput.failure(error);
		}
		ProjectOnlineApplyUpdateDto dto = this.parseServiceModel(apply);
		try {
			projectOnlineApplyService.saveOrUpdate(dto);
			ProjectOnlineApply vm = this.projectOnlineApplyService.get(dto.getId());
			return BaseOutput.success().setData(ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm));
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("删除ProjectOnlineApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "ProjectOnlineApply的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		try {
			projectOnlineApplyService.deleteProjectOnlineApply(id);
			return BaseOutput.success();
		} catch (ProjectOnlineApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	private String validate(ProjectOnlineApplyAddDto apply) {
		// 获取申请人
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			return "请先登录";
		}
		apply.setApplicantId(user.getId());
		if (apply.getSqlFile() == null && StringUtils.isBlank(apply.getSqlScript())) {
			return "sql脚本不能为空";
		}
		if (apply.getStartupScriptFile() == null && StringUtils.isBlank(apply.getStartupScript())) {
			return "启动脚本不能为空";
		}
		Pattern pattern = Pattern.compile(EMAIL_REGEX);
		String[] emails = apply.getEmailAddress().split(";");
		for (String str : emails) {
			Matcher matcher = pattern.matcher(str.trim());
			if (!matcher.matches()) {
				return "邮箱格式不正确";
			}
		}
		return null;
	}

	private ProjectOnlineApplyUpdateDto parseServiceModel(ProjectOnlineApplyAddDto apply) {
		ProjectOnlineApplyUpdateDto dto = DTOUtils.toEntity(apply, ProjectOnlineApplyUpdateDto.class, false);
		Files f = null;
		if (!apply.getDependencySystemFile().isEmpty()) {
			f = this.fileService.uploadFile(new MultipartFile[] { apply.getDependencySystemFile() }, null).get(0);
			dto.setDependencySystemFileId(f.getId());
		}
		if (!apply.getSqlFile().isEmpty()) {
			f = this.fileService.uploadFile(new MultipartFile[] { apply.getSqlFile() }, null).get(0);
			dto.setSqlFileId(f.getId());
		}
		if (!apply.getStartupScriptFile().isEmpty()) {
			f = this.fileService.uploadFile(new MultipartFile[] { apply.getStartupScriptFile() }, null).get(0);
			dto.setStartupScriptFileId(f.getId());
		}
		List<ProjectOnlineSubsystemDto> posDtos = new ArrayList<>();
		for (int i = 0; i < apply.getSubProjectName().size(); i++) {
			ProjectOnlineSubsystemDto posDto = new ProjectOnlineSubsystemDto();
			posDto.setManagerName(apply.getManagerId().get(i));
			posDto.setProjectName(apply.getSubProjectName().get(i));
			posDtos.add(posDto);
		}
		dto.setSubsystem(posDtos);
		String[] emails = apply.getEmailAddress().split(";");
		dto.setEmailAddress(Arrays.asList(emails));
		return dto;
	}
}