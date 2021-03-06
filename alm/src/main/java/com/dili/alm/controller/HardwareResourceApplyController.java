package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.domain.HardwareResourceRequirement;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.HardwareResourceApplyListPageQueryDto;
import com.dili.alm.domain.dto.HardwareResourceApplyUpdateDto;
import com.dili.alm.domain.dto.HardwareResourceRequirementDto;
import com.dili.alm.exceptions.HardwareResourceApplyException;
import com.dili.alm.service.DataDictionaryValueService;
import com.dili.alm.service.HardwareResourceApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.impl.HardwareResourceApplyServiceImpl;
import com.dili.alm.utils.DateUtil;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.rpc.restful.BpmcFormRpc;
import com.dili.bpmc.sdk.rpc.restful.TaskRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.domain.dto.UserQuery;
import com.dili.uap.sdk.rpc.DepartmentRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;

/*import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;*/
import tk.mybatis.mapper.entity.Example;

/**
 * ???MyBatis Generator?????????????????? This file was generated on 2018-03-20 17:22:08.
 */
/*@Api("/hardwareResourceApply")*/
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
    @Autowired
    TaskRpc taskRpc;
    @Autowired
    BpmcFormRpc bpmcFormRpc;

	private static final String DEPARTMENT_MAINTENANCE_CODE = "maintenance";
	private static final String DATA_AUTH_TYPE = "project";

	/*@ApiOperation("?????????HardwareResourceApply??????")*/
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		modelMap.addAttribute("user", SessionContext.getSessionContext().getUserTicket());
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
	
	@GetMapping("/detailForTask")
	public String detailForTask(@RequestParam Long id, ModelMap modelMap) {
		HardwareResourceApply apply = this.hardwareResourceApplyService.getDetailViewModel(id);
		modelMap.addAttribute("serviceEnvironmentText", apply.aget("serviceEnvironmentText"))
				.addAttribute("requirementList", apply.aget("requirementList"))
				.addAttribute("opRecords", apply.aget("opRecords"));
		Map<Object, Object> viewModel = HardwareResourceApplyServiceImpl.buildApplyViewModel(apply);
		modelMap.addAttribute("model", viewModel);
		return "hardwareResourceApply/process/detailForTask";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addView(ModelMap modelMap) {
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (!CollectionUtils.isEmpty(dataAuths)) {
			List<Long> projectIds = new ArrayList<>();
			dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("value").toString())));
			Example example = new Example(Project.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andIn("id", projectIds).andNotEqualTo("projectState", ProjectState.CLOSED.getValue());
			List<Project> projects = this.projectService.selectByExample(example);
			modelMap.addAttribute("projects", projects);
		}

		/** ?????? ???????????? ***/
		List<Department> departments = this.deptRpc.listByDepartment(DTOUtils.newDTO(Department.class)).getData();
		modelMap.addAttribute("departments", departments);

		/** ???????????? **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("userInfo", userTicket);

		/*** ?????? **/
		List<DataDictionaryValueDto> ddValue = ddvService.listDataDictionaryValueByCode(AlmConstants.ALM_ENVIRONMENT);
		modelMap.addAttribute("ddValue", ddValue);
		/*** ???????????????????????????????????? begin **/
		Department deptQuery = DTOUtils.newDTO(Department.class);
		deptQuery.setCode(AlmConstants.OPERATION_DEPARTMENT_CODE);
		deptQuery.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<Department>> deptOutput = this.deptRpc.listByDepartment(deptQuery);
		if (deptOutput.isSuccess() && CollectionUtils.isNotEmpty(deptOutput.getData())) {
			Long departmentId = deptOutput.getData().get(0).getId();
			UserQuery userQuery =  DTOUtils.newDTO(UserQuery.class);
			userQuery.setFirmCode(AlmConstants.ALM_FIRM_CODE);
			userQuery.setDepartmentId(departmentId);
			BaseOutput<List<User>> userOutput = this.userRpc.listByExample(userQuery);
			if (userOutput.isSuccess() && CollectionUtils.isNotEmpty(userOutput.getData())) {
				StringBuilder sb = new StringBuilder();
				userOutput.getData().forEach(u -> sb.append(u.getEmail()).append(";"));
				modelMap.addAttribute("opUsers", sb.substring(0, sb.length() - 1));
			}
		} /*** ???????????????????????????????????? end **/
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
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("value").toString())));
		Example example = new Example(Project.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", projectIds).andNotEqualTo("projectState", ProjectState.CLOSED.getValue());
		List<Project> projects = this.projectService.selectByExample(example);
		modelMap.addAttribute("projects", projects);
		List<Project> plist = this.projectService.list(null);
		modelMap.addAttribute("plist", plist).addAttribute("ulist", AlmCache.getInstance().getUserMap().values());

		/** ?????? ???????????? ***/
		Department newDepartment = DTOUtils.newDTO(Department.class);
		newDepartment.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		List<Department> departments = this.deptRpc.listByDepartment(newDepartment).getData();
		modelMap.addAttribute("departments", departments);

		/** ???????????? **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("userInfo", userTicket);

		/*** ?????? **/
		List<DataDictionaryValueDto> ddValue = ddvService.listDataDictionaryValueByCode(AlmConstants.ALM_ENVIRONMENT);
		modelMap.addAttribute("ddValue", ddValue);

		/*** ???????????????????????????????????? begin **/
		Department deptQuery =DTOUtils.newDTO(Department.class);
		deptQuery.setCode(AlmConstants.OPERATION_DEPARTMENT_CODE);
		deptQuery.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<Department>> deptOutput = this.deptRpc.listByDepartment(deptQuery);
		HardwareResourceApply dto = this.hardwareResourceApplyService.get(id);
		modelMap.addAttribute("apply", dto);
		List<String> envList = (List<String>) JSONObject.parseObject(dto.getServiceEnvironment(),
				new TypeReference<List<String>>() {
				});
		modelMap.addAttribute("se2", envList);
		modelMap.addAttribute("submit", DateUtil.getDate(dto.getApplyDate()));// ????????????
		modelMap.addAttribute("cread", DateUtil.getDate(dto.getCreated()));// ????????????
		modelMap.put("taskId", null);

		/** ?????????????????? ***/
		List<HardwareResourceRequirement> requirementList = hardwareResourceApplyService.listRequirement(dto.getId());
		List<HardwareResourceRequirementDto> requirementDtoList = new ArrayList<HardwareResourceRequirementDto>(
				requirementList.size());
		requirementList.forEach(c -> {
			requirementDtoList.add(DTOUtils.toEntity(c, HardwareResourceRequirementDto.class, true));
		});

		modelMap.addAttribute("requirementList", JSONObject.toJSONString(requirementList));

		return "hardwareResourceApply/edit";
	}

/*	@ApiOperation(value = "??????HardwareResourceApply", notes = "??????HardwareResourceApply?????????????????????")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "HardwareResourceApply", paramType = "form", value = "HardwareResourceApply???form??????", required = false, dataType = "string") 
			})*/
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

/*	@ApiOperation(value = "????????????HardwareResourceApply", notes = "????????????HardwareResourceApply?????????easyui????????????")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "HardwareResourceApply", paramType = "form", value = "HardwareResourceApply???form??????", required = false, dataType = "string")
			})*/
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(HardwareResourceApplyListPageQueryDto query,
			@RequestParam(required = false) String applyStateShow) throws Exception {
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return new EasyuiPageOutput(0L, Collections.emptyList()).toString();
		}
		List<Long> projectIds = new ArrayList<>();
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("value").toString())));
		query.setProjectIds(projectIds);
		query.setSort("created");
		query.setOrder("desc");
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

/*	@ApiOperation("??????HardwareResourceApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "HardwareResourceApply", paramType = "form", value = "HardwareResourceApply???form??????", required = true, dataType = "string") 
			})*/
	@RequestMapping(value = "/save", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(HardwareResourceApplyUpdateDto hardwareResourceApply,
			String[] serviceEnvironmentChk, String configurationRequirementJsonStr) {
		Set<Long> serviceEnvironments = new HashSet<Long>();
		if (serviceEnvironmentChk == null || serviceEnvironmentChk.length <= 0) {
			return BaseOutput.failure("?????????????????????");
		}
		if (StringUtils.isBlank(configurationRequirementJsonStr)) {
			return BaseOutput.failure("?????????????????????");
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
			return BaseOutput.failure("????????????");
		}
		return BaseOutput.success("????????????");
	}

/*	@ApiOperation("??????HardwareResourceApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "HardwareResourceApply", paramType = "form", value = "HardwareResourceApply???form??????", required = true, dataType = "string") 
			})*/
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(HardwareResourceApplyUpdateDto hardwareResourceApply,
			String[] serviceEnvironmentChk, String configurationRequirementJsonStr,String source,String taskId) {
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
		return BaseOutput.success("????????????");
	}

/*	@ApiOperation("??????HardwareResourceApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "HardwareResourceApply?????????", required = true, dataType = "long") 
			})*/
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		hardwareResourceApplyService.delete(id);
		return BaseOutput.success("????????????");
	}

/*	@ApiOperation("????????????")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "HardwareResourceApply?????????", required = true, dataType = "long") 
			})*/
	@RequestMapping(value = "/submit", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput submit(Long id) {
		try {
			hardwareResourceApplyService.submit(id);
		} catch (HardwareResourceApplyException e) {
			BaseOutput.failure("???????????????");
		}
		return BaseOutput.success("????????????");
	}

	@RequestMapping(value = "/saveAndSubmit", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput saveAndSubmit(HardwareResourceApplyUpdateDto hardwareResourceApply,
			String[] serviceEnvironmentChk, String configurationRequirementJsonStr) {
		Set<Long> serviceEnvironments = new HashSet<Long>();
		if (serviceEnvironmentChk == null || serviceEnvironmentChk.length <= 0) {
			return BaseOutput.failure("?????????????????????");
		}
		if (StringUtils.isBlank(configurationRequirementJsonStr)) {
			return BaseOutput.failure("?????????????????????");
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

/*	@ApiOperation("??????????????????")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "HardwareResourceApply?????????", required = true, dataType = "long") 
			})*/
	@RequestMapping(value = "/goApprove", method = { RequestMethod.GET })
	public String goApprove(@RequestParam Long id, ModelMap modelMap) throws HardwareResourceApplyException {

		List<Project> projects = this.projectService.list(null);
		modelMap.addAttribute("projects", projects);
		List<Project> plist = this.projectService.list(null);
		modelMap.addAttribute("plist", plist).addAttribute("ulist", AlmCache.getInstance().getUserMap().values());

		/** ?????? ???????????? ***/
		List<Department> departments = this.deptRpc.listByDepartment(DTOUtils.newDTO(Department.class)).getData();
		modelMap.addAttribute("departments", departments);

		/*** ?????? **/
		List<DataDictionaryValueDto> ddValue = ddvService.listDataDictionaryValueByCode(AlmConstants.ALM_ENVIRONMENT);
		modelMap.addAttribute("ddValue", ddValue);

		/*** ???????????????????????????????????? begin **/
		Department deptQuery = DTOUtils.newDTO(Department.class);
		deptQuery.setCode(AlmConstants.OPERATION_DEPARTMENT_CODE);
		deptQuery.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<Department>> deptOutput = this.deptRpc.listByDepartment(deptQuery);
		HardwareResourceApply dto = this.hardwareResourceApplyService.getDetailViewModel(id);
		modelMap.addAttribute("apply", dto);
		modelMap.addAttribute("opRecords", dto.aget("opRecords"));
		List<String> envList = (List<String>) JSONObject.parseObject(dto.getServiceEnvironment(),
				new TypeReference<List<String>>() {
				});
		modelMap.addAttribute("se2", envList);
		modelMap.addAttribute("submit", DateUtil.getDate(dto.getApplyDate()));// ????????????
		modelMap.addAttribute("cread", DateUtil.getDate(dto.getCreated()));// ????????????

		/** ???????????? **/
		User u = userRpc.findUserById(dto.getApplicantId()).getData();
		modelMap.addAttribute("userInfo", u);

		/** ?????????????????? ***/
		List<HardwareResourceRequirement> requirementList = hardwareResourceApplyService.listRequirement(dto.getId());
		List<HardwareResourceRequirementDto> requirementDtoList = new ArrayList<HardwareResourceRequirementDto>(
				requirementList.size());
		requirementList.forEach(c -> {
			requirementDtoList.add(DTOUtils.toEntity(c, HardwareResourceRequirementDto.class, true));
		});
		modelMap.addAttribute("requirementList", JSONObject.toJSONString(requirementList));
/*
    	String valProcess = dto.getId()+"";
    	
        //???????????????????????????
        TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
        taskDto.setProcessInstanceBusinessKey(valProcess);
        BaseOutput<List<TaskMapping>> outputList = taskRpc.list(taskDto);
        if(!outputList.isSuccess()){
        	return "???????????????"+outputList.getMessage(); 
        }
        
        List<TaskMapping> taskMappings = outputList.getData();
        //??????formKey
        TaskMapping selected = taskMappings.get(0);
        modelMap.put("taskId",selected.getId());*/
		return "hardwareResourceApply/agreePage";
	}
	
	/*@ApiOperation("????????????????????????")*/
	@RequestMapping(value = "/managerApprove", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput managerApprove(Long id, Boolean isApproved, String description){
		try {
			UserTicket user = SessionContext.getSessionContext().getUserTicket();
			if (isApproved) {
				hardwareResourceApplyService.projectManagerApprove(id, user.getId(), ApproveResult.APPROVED, description);
			} else {
				hardwareResourceApplyService.projectManagerApprove(id, user.getId(), ApproveResult.FAILED, description);
			}
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
		return BaseOutput.success("????????????");
	}

/*	@ApiOperation("??????????????????")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "HardwareResourceApply?????????", required = true, dataType = "long") 
			})*/
	@RequestMapping(value = "/operManagerApprove", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput operManagerApprove(Long id, boolean isApproved, String description,
			String[] operDepartmentUsers)  {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (operDepartmentUsers == null) {
			return BaseOutput.failure("????????????");
		}
		Set<Long> executors = new HashSet<Long>();
		for (int i = 0; i < operDepartmentUsers.length; i++) {
			executors.add(Long.parseLong(operDepartmentUsers[i]));
		}
		try {
			hardwareResourceApplyService.operationManagerApprove(id, user.getId(), executors, description);
			
			hardwareResourceApplyService.operatorManagerTask(id);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
		
		return BaseOutput.success("????????????");
	}

/*	@ApiOperation("????????????")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "HardwareResourceApply?????????", required = true, dataType = "long") 
			})*/
	@RequestMapping(value = "/implementing", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput implementing(Long id, String description) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();

		try {
			hardwareResourceApplyService.operatorExecute(id, user.getId(), description);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}

		return BaseOutput.success("????????????");
	}

/*	@ApiOperation("?????????List")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "HardwareResourceApply?????????", required = true, dataType = "long") 
			})*/
	@RequestMapping(value = "/operMemers.json", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<User> operMemers() throws HardwareResourceApplyException {
		Department department=DTOUtils.newDTO(Department.class);
		department.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		department.setCode(DEPARTMENT_MAINTENANCE_CODE);
		Department getDepartment = this.deptRpc.getOne(department).getData();
		UserQuery user = DTOUtils.newDTO(UserQuery.class);
		user.setDepartmentId(getDepartment.getId());
		user.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		List<User> operUsers = userRpc.listByExample(user).getData();
		return operUsers;
	}
	
	/***
	 * ??????????????????
	 */
	/*@ApiOperation("??????????????????????????????")*/
	@RequestMapping(value = "/projectManagerApprove.html", method = { RequestMethod.GET, RequestMethod.POST })
	public  String projectManagerApprove(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
        BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String codeDates = taskVariablesOutput.getData().get("businessKey").toString();
        HardwareResourceApply apply = DTOUtils.newInstance(HardwareResourceApply.class);
        apply = hardwareResourceApplyService.get(Long.parseLong(codeDates));
        
        modelMap.put("apply", apply);

    	modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
        return "hardwareResourceApply/process/projectManagerApprove";
	}
	
	/*@ApiOperation("????????????????????????")*/
	@RequestMapping(value = "/projectManagerApprove.action", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput doProjectManagerApproveForTask(@RequestParam String taskId,Long applyId, Boolean isApproved, String description)
			throws HardwareResourceApplyException {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (isApproved) {
			hardwareResourceApplyService.submitApprove(applyId,taskId,"");
			
			hardwareResourceApplyService.projectManagerApproveForTask(applyId,user.getId(),ApproveResult.APPROVED,description);
		}else {
			hardwareResourceApplyService.rejectApprove(applyId, taskId);
		}
		return BaseOutput.success("????????????");
	}
	
	/*@ApiOperation("??????????????????????????????")*/
	@RequestMapping(value = "/oprationManagerApprove.html", method = { RequestMethod.GET, RequestMethod.POST })
	public   String oprationManagerApproveForTask(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap)
			throws HardwareResourceApplyException {
		BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String codeDates = taskVariablesOutput.getData().get("businessKey").toString();
        HardwareResourceApply apply = DTOUtils.newInstance(HardwareResourceApply.class);
        apply = hardwareResourceApplyService.get(Long.parseLong(codeDates));
        
        String applyJsonStr = JSONObject.toJSONString(apply);
        modelMap.put("apply", apply);
        modelMap.put("modelStr", applyJsonStr);
    	/** ???????????? **/
        User userTicket = this.userRpc.findUserById(apply.getApplicantId()).getData();
        if (userTicket==null) {
			return "???????????????";
		}
    	BaseOutput<Department> de = deptRpc.get(userTicket.getDepartmentId());
    	modelMap.addAttribute("userInfo", userTicket);
    		
    	modelMap.addAttribute("depName",de.getData().getName());
    	modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
        return "hardwareResourceApply/process/oprationManagerApprove";
	}
	
	/*@ApiOperation("????????????????????????")*/
	@RequestMapping(value = "/oprationManagerApprove.action", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput doOprationManagerApproveForTask(@RequestParam String taskId,Long applyId, Boolean isApproved,String description,
			String[] operDepartmentUsers )
			throws HardwareResourceApplyException {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		Set<Long> executors = new HashSet<Long>();
		for (int i = 0; i < operDepartmentUsers.length; i++) {
			executors.add(Long.parseLong(operDepartmentUsers[i]));
		}
		if (isApproved) {
			hardwareResourceApplyService.operationManagerApproveForTask(applyId, user.getId(), executors, description);
			hardwareResourceApplyService.submitApprove(applyId,taskId,operDepartmentUsers[0]);
		}else {
			hardwareResourceApplyService.rejectApprove(applyId, taskId);
		}

		return BaseOutput.success("????????????");
	}
	/*@ApiOperation("????????????????????????")*/
	@RequestMapping(value = "/opdratorExecute.html", method = { RequestMethod.GET, RequestMethod.POST })
	public   String opdratorExecuteForTask(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap)
			throws HardwareResourceApplyException {
		BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String codeDates = taskVariablesOutput.getData().get("businessKey").toString();
        HardwareResourceApply apply = DTOUtils.newInstance(HardwareResourceApply.class);
        apply = hardwareResourceApplyService.get(Long.parseLong(codeDates));

        modelMap.put("apply", apply);

    	modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
        return "hardwareResourceApply/process/opdratorExecute";
	}
	
	/*@ApiOperation("??????????????????")*/
	@RequestMapping(value = "/opdratorExecute.action", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput doOpdratprExecuteForTask(@RequestParam String taskId,Long applyId, String description)
			throws HardwareResourceApplyException {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		hardwareResourceApplyService.submitApprove(applyId,taskId,"");
		hardwareResourceApplyService.operatorExecuteForTask(applyId, user.getId(), description);
		return BaseOutput.success("????????????");
	}
	/*@ApiOperation("????????????????????????")*/
	@RequestMapping(value = "/editHardwareResourceApply.html", method = { RequestMethod.GET, RequestMethod.POST })
	public  String editHardwareResourceApply(@RequestParam String taskId, @RequestParam(required = false) Boolean cover, ModelMap modelMap) throws HardwareResourceApplyException {
        BaseOutput<TaskMapping> output = taskRpc.getById(taskId);
        if(!output.isSuccess()){
            throw new AppException(output.getMessage());
        }
        BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
        if(!taskVariablesOutput.isSuccess()){
            throw new AppException(taskVariablesOutput.getMessage());
        }
        String codeDates = taskVariablesOutput.getData().get("businessKey").toString();
        
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return null;
		}
		List<Long> projectIds = new ArrayList<>();
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("value").toString())));
		Example example = new Example(Project.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", projectIds).andNotEqualTo("projectState", ProjectState.CLOSED.getValue());
		List<Project> projects = this.projectService.selectByExample(example);
		modelMap.addAttribute("projects", projects);
		List<Project> plist = this.projectService.list(null);
		modelMap.addAttribute("plist", plist).addAttribute("ulist", AlmCache.getInstance().getUserMap().values());

		/** ?????? ???????????? ***/
		Department newDepartment = DTOUtils.newDTO(Department.class);
		newDepartment.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		List<Department> departments = this.deptRpc.listByDepartment(newDepartment).getData();
		modelMap.addAttribute("departments", departments);

		/** ???????????? **/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("userInfo", userTicket);

		/*** ?????? **/
		List<DataDictionaryValueDto> ddValue = ddvService.listDataDictionaryValueByCode(AlmConstants.ALM_ENVIRONMENT);
		modelMap.addAttribute("ddValue", ddValue);

		/*** ???????????????????????????????????? begin **/
		Department deptQuery =DTOUtils.newDTO(Department.class);
		deptQuery.setCode(AlmConstants.OPERATION_DEPARTMENT_CODE);
		deptQuery.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<Department>> deptOutput = this.deptRpc.listByDepartment(deptQuery);
		HardwareResourceApply dto = this.hardwareResourceApplyService.get(Long.parseLong(codeDates));
		modelMap.addAttribute("apply", dto);
		List<String> envList = (List<String>) JSONObject.parseObject(dto.getServiceEnvironment(),
				new TypeReference<List<String>>() {
				});
		modelMap.addAttribute("se2", envList);
		modelMap.addAttribute("submit", DateUtil.getDate(dto.getApplyDate()));// ????????????
		modelMap.addAttribute("cread", DateUtil.getDate(dto.getCreated()));// ????????????

		/** ?????????????????? ***/
		List<HardwareResourceRequirement> requirementList = hardwareResourceApplyService.listRequirement(dto.getId());
		List<HardwareResourceRequirementDto> requirementDtoList = new ArrayList<HardwareResourceRequirementDto>(
				requirementList.size());
		requirementList.forEach(c -> {
			requirementDtoList.add(DTOUtils.toEntity(c, HardwareResourceRequirementDto.class, true));
		});

		modelMap.addAttribute("requirementList", JSONObject.toJSONString(requirementList));
        
        HardwareResourceApply apply = DTOUtils.newInstance(HardwareResourceApply.class);
        apply = hardwareResourceApplyService.get(Long.parseLong(codeDates));
        
        modelMap.put("apply", apply);

    	modelMap.put("taskId", taskId);
        modelMap.put("cover", cover == null ? output.getData().getAssignee() == null : cover);
        return "hardwareResourceApply/process/editForTask";
	}
	
	
}