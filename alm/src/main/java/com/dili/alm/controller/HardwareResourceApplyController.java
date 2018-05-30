package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.ApproveResult;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.domain.HardwareResourceRequirement;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.HardwareResourceApplyListPageQueryDto;
import com.dili.alm.domain.dto.HardwareResourceApplyUpdateDto;
import com.dili.alm.domain.dto.HardwareResourceRequirementDto;
import com.dili.alm.exceptions.HardwareResourceApplyException;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryValueService;
import com.dili.alm.service.HardwareResourceApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.impl.HardwareResourceApplyServiceImpl;
import com.dili.alm.utils.DateUtil;
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
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-20 17:22:08.
 */
@Api("/hardwareResourceApply")
@Controller
@RequestMapping("/hardwareResourceApply")
public class HardwareResourceApplyController {
	@Autowired
	HardwareResourceApplyService hardwareResourceApplyService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private UserRpc userRpc;
	@Autowired
	private DepartmentRpc deptRpc;
	@Autowired
	private DataDictionaryValueService ddvService;

	private static final String DATA_AUTH_TYPE = "Project";

	@ApiOperation("跳转到HardwareResourceApply页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "hardwareResourceApply/index";
	}

	@GetMapping("/detail")
	public String detail(@RequestParam Long id, ModelMap modelMap) {
		HardwareResourceApply apply = this.hardwareResourceApplyService.getDetailViewModel(id);
		modelMap.addAttribute("serviceEnvironmentText", apply.aget("serviceEnvironmentText"))
				.addAttribute("requirementList", apply.aget("requirementList"))
				.addAttribute("opRecords", apply.aget("opRecords"));
		Map<Object, Object> viewModel = HardwareResourceApplyServiceImpl.buildApplyViewModel(apply);
		modelMap.addAttribute("model", viewModel);
		return "hardwareResourceApply/detail";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addView(ModelMap modelMap) {
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (!CollectionUtils.isEmpty(dataAuths)) {
			List<Long> projectIds = new ArrayList<>();
			dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("dataId").toString())));
			Example example = new Example(ProjectOnlineApply.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andIn("id", projectIds);
			List<Project> projects = this.projectService.selectByExample(example);
			modelMap.addAttribute("projects", projects);
		}

		/** 查询 所有部门 ***/
		List<Department> departments = this.deptRpc.list(new Department()).getData();
		modelMap.addAttribute("departments", departments);

		/** 个人信息 **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("userInfo", userTicket);

		/*** 环境 **/
		DataDictionaryValue dd = DTOUtils.newDTO(DataDictionaryValue.class);
		dd.setDdId((long) 23);
		List<DataDictionaryValue> ddValue = ddvService.list(dd);
		modelMap.addAttribute("ddValue", ddValue);
		/*** 运维部门下的所有人员查询 begin **/
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
		} /*** 运维部门下的所有人员查询 end **/
		return "hardwareResourceApply/add";
	}

	@RequestMapping(value = "/toUpdate", method = RequestMethod.GET)
	public String updateView(@RequestParam Long id, ModelMap modelMap) throws HardwareResourceApplyException {
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return null;
		}
		List<Long> projectIds = new ArrayList<>();
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("dataId").toString())));
		Example example = new Example(HardwareResourceApply.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", projectIds);
		List<Project> projects = this.projectService.selectByExample(example);
		modelMap.addAttribute("projects", projects);
		List<Project> plist = this.projectService.list(null);
		modelMap.addAttribute("plist", plist).addAttribute("ulist", AlmCache.getInstance().getUserMap().values());

		/** 查询 所有部门 ***/
		List<Department> departments = this.deptRpc.list(new Department()).getData();
		modelMap.addAttribute("departments", departments);

		/** 个人信息 **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("userInfo", userTicket);

		/*** 环境 **/
		DataDictionaryValue dd = DTOUtils.newDTO(DataDictionaryValue.class);
		dd.setDdId((long) 23);
		List<DataDictionaryValue> ddValue = ddvService.list(dd);
		modelMap.addAttribute("ddValue", ddValue);

		/*** 运维部门下的所有人员查询 begin **/
		Department deptQuery = new Department();
		deptQuery.setCode(AlmConstants.OPERATION_DEPARTMENT_CODE);
		BaseOutput<List<Department>> deptOutput = this.deptRpc.list(deptQuery);
		HardwareResourceApply dto = this.hardwareResourceApplyService.get(id);
		modelMap.addAttribute("apply", dto);
		List<String> envList = (List<String>) JSONObject.parseObject(dto.getServiceEnvironment(),
				new TypeReference<List<String>>() {
				});
		modelMap.addAttribute("se2", envList);
		modelMap.addAttribute("submit", DateUtil.getDate(dto.getApplyDate()));// 转化时间
		modelMap.addAttribute("cread", DateUtil.getDate(dto.getCreated()));// 转化时间

		/** 配置需求列表 ***/
		List<HardwareResourceRequirement> requirementList = hardwareResourceApplyService.listRequirement(dto.getId());
		List<HardwareResourceRequirementDto> requirementDtoList = new ArrayList<HardwareResourceRequirementDto>(
				requirementList.size());
		requirementList.forEach(c -> {
			requirementDtoList.add(DTOUtils.toEntity(c, HardwareResourceRequirementDto.class, true));
		});

		modelMap.addAttribute("requirementList", JSONObject.toJSONString(requirementList));

		return "hardwareResourceApply/edit";
	}

	@ApiOperation(value = "查询HardwareResourceApply", notes = "查询HardwareResourceApply，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "HardwareResourceApply", paramType = "form", value = "HardwareResourceApply的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listRequirement", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<HardwareResourceRequirement> listRequirement(Long applyId) {
		List<HardwareResourceRequirement> list = null;
		try {
			list = hardwareResourceApplyService.listRequirement(applyId);
		} catch (HardwareResourceApplyException e) {
			return null;
		}
		return list;
	}

	@ApiOperation(value = "分页查询HardwareResourceApply", notes = "分页查询HardwareResourceApply，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "HardwareResourceApply", paramType = "form", value = "HardwareResourceApply的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(HardwareResourceApplyListPageQueryDto query,
			@RequestParam(required = false) String applyStateShow) throws Exception {
		if (StringUtils.isNotBlank(applyStateShow)) {
			List<Integer> stateList = JSON.parseArray(applyStateShow, Integer.class);
			query.setApplyStateList(stateList);
		}
		if (query.getSubmitBeginTime() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(query.getSubmitBeginTime());
			c.add(Calendar.DAY_OF_MONTH, -1);
			query.setSubmitBeginTime(c.getTime());
		}
		if (query.getSubmitEndTime() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(query.getSubmitEndTime());
			c.add(Calendar.DAY_OF_MONTH, 1);
			query.setSubmitEndTime(c.getTime());
		}
		return hardwareResourceApplyService.listEasyuiPageByExample(query, true).toString();
	}

	@ApiOperation("新增HardwareResourceApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "HardwareResourceApply", paramType = "form", value = "HardwareResourceApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/save", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(HardwareResourceApplyUpdateDto hardwareResourceApply,
			String[] serviceEnvironmentChk, String configurationRequirementJsonStr) {
		Set<Long> serviceEnvironments = new HashSet<Long>();
		if (serviceEnvironmentChk == null || serviceEnvironmentChk.length <= 0) {
			return BaseOutput.failure("请选择使用环境");
		}
		if (StringUtils.isBlank(configurationRequirementJsonStr)) {
			return BaseOutput.failure("请添加配置要求");
		}
		for (int i = 0; i < serviceEnvironmentChk.length; i++) {
			serviceEnvironments.add(Long.parseLong(serviceEnvironmentChk[i]));
		}
		hardwareResourceApply.setServiceEnvironments(serviceEnvironments);
		List<HardwareResourceRequirementDto> parseArray = JSONArray.parseArray(configurationRequirementJsonStr,
				HardwareResourceRequirementDto.class);
		hardwareResourceApply.setConfigurationRequirement(parseArray);
		try {
			hardwareResourceApplyService.saveOrUpdate(hardwareResourceApply);
		} catch (HardwareResourceApplyException e) {
			return BaseOutput.failure("新增失败");
		}
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改HardwareResourceApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "HardwareResourceApply", paramType = "form", value = "HardwareResourceApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(HardwareResourceApplyUpdateDto hardwareResourceApply,
			String[] serviceEnvironmentChk, String configurationRequirementJsonStr) {
		Set<Long> serviceEnvironments = new HashSet<Long>();
		for (int i = 0; i < serviceEnvironmentChk.length; i++) {
			serviceEnvironments.add(Long.parseLong(serviceEnvironmentChk[i]));
		}
		hardwareResourceApply.setServiceEnvironments(serviceEnvironments);
		List<HardwareResourceRequirementDto> parseArray = JSONArray.parseArray(configurationRequirementJsonStr,
				HardwareResourceRequirementDto.class);
		hardwareResourceApply.setConfigurationRequirement(parseArray);
		try {
			hardwareResourceApplyService.saveOrUpdate(hardwareResourceApply);
		} catch (HardwareResourceApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除HardwareResourceApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "HardwareResourceApply的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		hardwareResourceApplyService.delete(id);
		return BaseOutput.success("删除成功");
	}

	@ApiOperation("提交申请")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "HardwareResourceApply的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/submit", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput submit(Long id) {
		try {
			hardwareResourceApplyService.submit(id);
		} catch (HardwareResourceApplyException e) {
			BaseOutput.failure("提交失败！");
		}
		return BaseOutput.success("提交成功");
	}

	@RequestMapping(value = "/saveAndSubmit", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput saveAndSubmit(HardwareResourceApplyUpdateDto hardwareResourceApply,
			String[] serviceEnvironmentChk, String configurationRequirementJsonStr) {
		Set<Long> serviceEnvironments = new HashSet<Long>();
		if (serviceEnvironmentChk == null || serviceEnvironmentChk.length <= 0) {
			return BaseOutput.failure("请选择使用环境");
		}
		if (StringUtils.isBlank(configurationRequirementJsonStr)) {
			return BaseOutput.failure("请添加配置要求");
		}
		for (int i = 0; i < serviceEnvironmentChk.length; i++) {
			serviceEnvironments.add(Long.parseLong(serviceEnvironmentChk[i]));
		}
		hardwareResourceApply.setServiceEnvironments(serviceEnvironments);
		List<HardwareResourceRequirementDto> parseArray = JSONArray.parseArray(configurationRequirementJsonStr,
				HardwareResourceRequirementDto.class);
		hardwareResourceApply.setConfigurationRequirement(parseArray);
		try {
			this.hardwareResourceApplyService.saveAndSubmit(hardwareResourceApply);
			return BaseOutput.success();
		} catch (HardwareResourceApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("进入审批申请")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "HardwareResourceApply的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/goApprove", method = { RequestMethod.GET })
	public String goApprove(@RequestParam Long id, ModelMap modelMap) throws HardwareResourceApplyException {

		List<Project> projects = this.projectService.list(null);
		modelMap.addAttribute("projects", projects);
		List<Project> plist = this.projectService.list(null);
		modelMap.addAttribute("plist", plist).addAttribute("ulist", AlmCache.getInstance().getUserMap().values());

		/** 查询 所有部门 ***/
		List<Department> departments = this.deptRpc.list(new Department()).getData();
		modelMap.addAttribute("departments", departments);

		/*** 环境 **/
		DataDictionaryValue dd = DTOUtils.newDTO(DataDictionaryValue.class);
		dd.setDdId((long) 23);
		List<DataDictionaryValue> ddValue = ddvService.list(dd);
		modelMap.addAttribute("ddValue", ddValue);

		/*** 运维部门下的所有人员查询 begin **/
		Department deptQuery = new Department();
		deptQuery.setCode(AlmConstants.OPERATION_DEPARTMENT_CODE);
		BaseOutput<List<Department>> deptOutput = this.deptRpc.list(deptQuery);
		HardwareResourceApply dto = this.hardwareResourceApplyService.getDetailViewModel(id);
		modelMap.addAttribute("apply", dto);
		modelMap.addAttribute("opRecords", dto.aget("opRecords"));
		List<String> envList = (List<String>) JSONObject.parseObject(dto.getServiceEnvironment(),
				new TypeReference<List<String>>() {
				});
		modelMap.addAttribute("se2", envList);
		modelMap.addAttribute("submit", DateUtil.getDate(dto.getApplyDate()));// 转化时间
		modelMap.addAttribute("cread", DateUtil.getDate(dto.getCreated()));// 转化时间

		/** 个人信息 **/
		/*
		 * UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		 */
		User u = userRpc.findUserById(dto.getApplicantId()).getData();
		modelMap.addAttribute("userInfo", u);

		/** 配置需求列表 ***/
		List<HardwareResourceRequirement> requirementList = hardwareResourceApplyService.listRequirement(dto.getId());
		List<HardwareResourceRequirementDto> requirementDtoList = new ArrayList<HardwareResourceRequirementDto>(
				requirementList.size());
		requirementList.forEach(c -> {
			requirementDtoList.add(DTOUtils.toEntity(c, HardwareResourceRequirementDto.class, true));
		});
		modelMap.addAttribute("requirementList", JSONObject.toJSONString(requirementList));

		return "hardwareResourceApply/agreePage";
	}

	@RequestMapping(value = "/managerApprove", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput managerApprove(Long id, Boolean isApproved, String description)
			throws HardwareResourceApplyException {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (isApproved) {

			hardwareResourceApplyService.projectManagerApprove(id, user.getId(), ApproveResult.APPROVED, description);
		} else {
			hardwareResourceApplyService.projectManagerApprove(id, user.getId(), ApproveResult.FAILED, description);
		}

		return BaseOutput.success("提交成功");
	}

	@RequestMapping(value = "/generalManagerApprove", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput generalManagerApprove(Long id, boolean isApproved, String description)
			throws HardwareResourceApplyException {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (isApproved) {

			hardwareResourceApplyService.generalManagerApprove(id, user.getId(), ApproveResult.APPROVED, description);
		} else {
			hardwareResourceApplyService.generalManagerApprove(id, user.getId(), ApproveResult.FAILED, description);
		}
		return BaseOutput.success("提交成功");
	}

	@ApiOperation("运维经理审批")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "HardwareResourceApply的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/operManagerApprove", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput operManagerApprove(Long id, boolean isApproved, String description,
			String[] operDepartmentUsers) throws HardwareResourceApplyException {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (operDepartmentUsers == null) {
			return BaseOutput.failure("还未分配");
		}
		Set<Long> executors = new HashSet<Long>();
		for (int i = 0; i < operDepartmentUsers.length; i++) {
			executors.add(Long.parseLong(operDepartmentUsers[i]));
		}
		hardwareResourceApplyService.operationManagerApprove(id, user.getId(), executors, description);
		return BaseOutput.success("提交成功");
	}

	@ApiOperation("运维实施")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "HardwareResourceApply的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/implementing", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput implementing(Long id, String description) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();

		try {
			hardwareResourceApplyService.operatorExecute(id, user.getId(), description);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}

		return BaseOutput.success("提交成功");
	}

	@ApiOperation("实施人List")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "HardwareResourceApply的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/operMemers.json", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<User> operMemers() throws HardwareResourceApplyException {
		User user = new User();
		user.setDepartmentId((long) 28);
		List<User> operUsers = userRpc.listByExample(user).getData();
		return operUsers;
	}
}