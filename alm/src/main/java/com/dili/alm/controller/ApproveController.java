package com.dili.alm.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectComplete;
import com.dili.alm.domain.dto.apply.ApplyApprove;
import com.dili.alm.exceptions.ApproveException;
import com.dili.alm.rpc.MyTasksRpc;
import com.dili.alm.rpc.RoleRpc;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.rpc.BpmcFormRpc;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-04 17:39:37.
 */
@Api("/approve")
@Controller
@RequestMapping("/approve")
public class ApproveController {
	@Autowired
	private ApproveService approveService;

	@Autowired
	private RoleRpc roleRpc;
	@Autowired
	private ProjectService projectService;
	
    @Autowired
    BpmcFormRpc bpmcFormRpc;
    @Autowired
    TaskRpc bpmcTaskRpc;
	@ApiOperation("跳转到Approve页面")
	@RequestMapping(value = "/apply/index.html", method = RequestMethod.GET)
	public String applyIndex(ModelMap modelMap) {
		modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
		return "approveApply/index";
	}

	@ApiOperation("跳转到Approve页面")
	@RequestMapping(value = "/change/index.html", method = RequestMethod.GET)
	public String changeIndex(ModelMap modelMap) {
		modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
		return "approveChange/index";
	}

	@ApiOperation("跳转到Approve页面")
	@RequestMapping(value = "/complete/index.html", method = RequestMethod.GET)
	public String completeIndex(ModelMap modelMap) {
		modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
		return "approveComplete/index";
	}
	
	
	@ApiOperation("跳转到/apply/wyhLeaderApprove页面")
	@RequestMapping(value = "/apply/wyhLeaderApprove.html", method = RequestMethod.GET)
	public String wyhLeaderApplyApprove(ModelMap modelMap,String  taskId, String viewMode) {
		approveService.getModel(modelMap, taskId);
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
		} else {
			modelMap.put("viewMode", false);
		}
		return "approveApply/wyhLeaderApprove";
		
	}
	
	
	@ApiOperation("跳转到/apply/wyhManagerApprove页面")
	@RequestMapping(value = "/apply/wyhManagerApprove.html", method = RequestMethod.GET)
	public String wyhManagerApplyApprove(ModelMap modelMap,String  taskId, String viewMode) {
		
		approveService.getModel(modelMap, taskId);
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
		} else {
			modelMap.put("viewMode", false);
		}
		return "approveApply/wyhManagerApprove";
	}
	

	
	@RequestMapping("/applyWyhLeaderApprove")
	@ResponseBody
	public BaseOutput<Object> applyWyhLeaderApprove(String taskId, String opt, String notes) {
		try {
			approveService.bpmcApprove(taskId, opt, notes);
			return BaseOutput.success();
		} catch (ApproveException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}
	
	@RequestMapping("/applyWyhManagerApprove")
	@ResponseBody
	public BaseOutput<Object> applyWyhManagerApprove(String taskId,  String opt, String notes) {
		try {
			approveService.bpmcApprove(taskId, opt, notes);
			return BaseOutput.success();
		} catch (ApproveException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}
	
	@ApiOperation("立项页面流程审批")
    @RequestMapping(value = "/applyApproveByAlm.html", method = RequestMethod.GET)
	public String applyApproveByAlm(@RequestParam Long id, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
		Approve approve = approveService.get(id);
        //根据业务号查询任务
        TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
        taskDto.setProcessInstanceBusinessKey(approve.getId().toString());
        BaseOutput<List<TaskMapping>> outputList = bpmcTaskRpc.list(taskDto);
        if(!outputList.isSuccess()){
        	return "用户错误！"+outputList.getMessage(); 
        }
        //获取formKey
        TaskMapping task  = outputList.getData().get(0);
        //通过bpmc的form表单，跳转到相应的提交页面
        ActForm selectActFrom  = bpmcFormRpc.getByKey(task.getFormKey()).getData();
        String url = selectActFrom.getActionUrl();
        if(WebUtil.strIsEmpty(task.getAssignee())) {
            bpmcTaskRpc.claim(task.getId().toString(), SessionContext.getSessionContext().getUserTicket().getId().toString());
        }else {
        	if(!task.getAssignee().equals(SessionContext.getSessionContext().getUserTicket().getId().toString())) {
        		return null;
        	}
        }
        approveService.getModel(modelMap, task.getId());
		modelMap.put("viewMode", false);
    	modelMap.put("taskId",task.getId());
		return url+"?taskId="+task.getId();
	}
	@RequestMapping(value = "/apply/{id}", method = RequestMethod.GET)
	public String apply(ModelMap modelMap, @PathVariable("id") Long id, String viewMode) {

		approveService.buildApplyApprove(modelMap, id);
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
		} else {
			modelMap.put("viewMode", false);
		}
		return "approveApply/approve";
	}
	@ApiOperation("跳转到/change/wyhLeaderApprove页面")
	@RequestMapping(value = "/change/wyhLeaderApprove.html", method = RequestMethod.GET)
	public String wyhLeaderChangeApprove(ModelMap modelMap,String  taskId, String viewMode) {
		BaseOutput<Map<String, Object>>  map=bpmcTaskRpc.getVariables(taskId);
		String id = (String) map.getData().get("businessKey");
		approveService.buildChangeApprove(modelMap, Long.valueOf(id));
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
		} else {
			modelMap.put("viewMode", false);
		}
		modelMap.put("taskId",taskId);
		return "approveChange/wyhLeaderApprove";
		
	}
	
	@ApiOperation("跳转到/change/wyhManagerApprove页面")
	@RequestMapping(value = "/change/wyhManagerApprove.html", method = RequestMethod.GET)
	public String wyhManagerChangeApprove(ModelMap modelMap,String  taskId, String viewMode) {
		BaseOutput<Map<String, Object>>  map=bpmcTaskRpc.getVariables(taskId);
		String id = (String) map.getData().get("businessKey");
		approveService.buildChangeApprove(modelMap, Long.valueOf(id));
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
		} else {
			modelMap.put("viewMode", false);
		}
		modelMap.put("taskId",taskId);
		return "approveChange/wyhManagerApprove";
	}
	
	@RequestMapping("/changeWyhManagerApprove")
	@ResponseBody
	public BaseOutput<Object> changeWyhManagerApprove(String taskId,  String opt, String notes) {
		try {
			approveService.bpmcApprove(taskId, opt, notes);
			return BaseOutput.success();
		} catch (ApproveException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}
	
	@RequestMapping("/changeWyhLeaderApprove")
	@ResponseBody
	public BaseOutput<Object> changeWyhLeaderApprove(String taskId, String opt, String notes) {
		try {
			approveService.bpmcApprove(taskId, opt, notes);
			return BaseOutput.success();
		} catch (ApproveException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}
	@ApiOperation("变更页面流程审批")
    @RequestMapping(value = "/changeApproveByAlm.html", method = RequestMethod.GET)
	public String changeApproveByAlm(@RequestParam Long id, @RequestParam(required = false) Boolean cover, ModelMap modelMap) {
		Approve approve = approveService.get(id);
        //根据业务号查询任务
        TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
        taskDto.setProcessInstanceBusinessKey(approve.getId().toString());
        BaseOutput<List<TaskMapping>> outputList = bpmcTaskRpc.list(taskDto);
        if(!outputList.isSuccess()){
        	return "用户错误！"+outputList.getMessage(); 
        }
        //获取formKey
        TaskMapping task  = outputList.getData().get(0);
        //通过bpmc的form表单，跳转到相应的提交页面
        ActForm selectActFrom  = bpmcFormRpc.getByKey(task.getFormKey()).getData();
        String url = selectActFrom.getActionUrl();
        if(WebUtil.strIsEmpty(task.getAssignee())) {
            bpmcTaskRpc.claim(task.getId().toString(), SessionContext.getSessionContext().getUserTicket().getId().toString());
        }else {
        	if(!task.getAssignee().equals(SessionContext.getSessionContext().getUserTicket().getId().toString())) {
        		return null;
        	}
        }

		approveService.buildChangeApprove(modelMap, id);
		modelMap.put("viewMode", false);
    	modelMap.put("taskId",task.getId());
		return url+"?taskId="+task.getId();
	}
	@RequestMapping(value = "/change/{id}", method = RequestMethod.GET)
	public String change(ModelMap modelMap, @PathVariable("id") Long id, String viewMode) {

		approveService.buildChangeApprove(modelMap, id);
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
		} else {
			modelMap.put("viewMode", false);
		}
		return "approveChange/approve";
	}

	@RequestMapping(value = "/verify/{id}", method = RequestMethod.GET)
	public String verify(ModelMap modelMap, @PathVariable("id") Long id, String viewMode) {

		approveService.buildChangeApprove(modelMap, id);
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
		} else {
			modelMap.put("viewMode", false);
		}
		return "approveChange/verify";
	}
	@ApiOperation("跳转到/complete/wyhLeaderApprove页面")
	@RequestMapping(value = "/complete/wyhLeaderApprove.html", method = RequestMethod.GET)
	public String wyhLeaderCompleteApprove(ModelMap modelMap,String  taskId, String viewMode) throws Exception {
		BaseOutput<Map<String, Object>>  map=bpmcTaskRpc.getVariables(taskId);
		String id = (String) map.getData().get("businessKey");
		approveService.buildCompleteApprove(modelMap, Long.valueOf(id));
		Project project = this.projectService.get(((ProjectComplete) modelMap.get("complete")).getProjectId());

		Map<Object, Object> metadata1 = new HashMap<>();
		metadata1.put("dep", "depProvider");
		metadata1.put("type", "projectTypeProvider");
		metadata1.put("startDate", "almDateProvider");
		metadata1.put("endDate", "almDateProvider");
		metadata1.put("actualStartDate", "datetimeProvider");
		metadata1.put("projectManager", "memberProvider");
		metadata1.put("testManager", "memberProvider");
		metadata1.put("productManager", "memberProvider");
		metadata1.put("developManager", "memberProvider");
		metadata1.put("businessOwner", "memberProvider");
		metadata1.put("originator", "memberProvider");

		Map projectViewModel = ValueProviderUtils.buildDataByProvider(metadata1, Arrays.asList(project)).get(0);
		modelMap.addAttribute("project1", projectViewModel);
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
			return "approveComplete/detail";
		} else {
			modelMap.put("viewMode", false);
		}
		modelMap.put("taskId",taskId);
		return "approveComplete/wyhLeaderApprove";
		
	}
	
	@ApiOperation("跳转到/complete/wyhManagerApprove页面")
	@RequestMapping(value = "/complete/wyhManagerApprove.html", method = RequestMethod.GET)
	public String wyhManagerCompleteApprove(ModelMap modelMap,String  taskId, String viewMode) throws Exception {
		BaseOutput<Map<String, Object>>  map=bpmcTaskRpc.getVariables(taskId);
		String id = (String) map.getData().get("businessKey");
		approveService.buildCompleteApprove(modelMap, Long.valueOf(id));
		Project project = this.projectService.get(((ProjectComplete) modelMap.get("complete")).getProjectId());

		Map<Object, Object> metadata1 = new HashMap<>();
		metadata1.put("dep", "depProvider");
		metadata1.put("type", "projectTypeProvider");
		metadata1.put("startDate", "almDateProvider");
		metadata1.put("endDate", "almDateProvider");
		metadata1.put("actualStartDate", "datetimeProvider");
		metadata1.put("projectManager", "memberProvider");
		metadata1.put("testManager", "memberProvider");
		metadata1.put("productManager", "memberProvider");
		metadata1.put("developManager", "memberProvider");
		metadata1.put("businessOwner", "memberProvider");
		metadata1.put("originator", "memberProvider");

		Map projectViewModel = ValueProviderUtils.buildDataByProvider(metadata1, Arrays.asList(project)).get(0);
		modelMap.addAttribute("project1", projectViewModel);
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
			return "approveComplete/detail";
		} else {
			modelMap.put("viewMode", false);
		}
		modelMap.put("taskId",taskId);
		return "approveComplete/wyhManagerApprove";
	}
	
	@RequestMapping("/completeWyhManagerApprove")
	@ResponseBody
	public BaseOutput<Object> completeWyhManagerApprove(String taskId,  String opt, String notes) {
		try {
			approveService.bpmcApprove(taskId, opt, notes);
			return BaseOutput.success();
		} catch (ApproveException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}
	
	@RequestMapping("/completeWyhLeaderApprove")
	@ResponseBody
	public BaseOutput<Object> completeWyhLeaderApprove(String taskId, String opt, String notes) {
		try {
			approveService.bpmcApprove(taskId, opt, notes);
			return BaseOutput.success();
		} catch (ApproveException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}
	@ApiOperation("结项页面流程审批")
    @RequestMapping(value = "/completeApproveByAlm.html", method = RequestMethod.GET)
	public String completeApproveByAlm(@RequestParam Long id, @RequestParam(required = false) Boolean cover, ModelMap modelMap) throws Exception {
		Approve approve = approveService.get(id);
        //根据业务号查询任务
        TaskDto taskDto = DTOUtils.newInstance(TaskDto.class);
        taskDto.setProcessInstanceBusinessKey(approve.getId().toString());
        BaseOutput<List<TaskMapping>> outputList = bpmcTaskRpc.list(taskDto);
        if(!outputList.isSuccess()){
        	return "用户错误！"+outputList.getMessage(); 
        }
        //获取formKey
        TaskMapping task  = outputList.getData().get(0);
        //通过bpmc的form表单，跳转到相应的提交页面
        ActForm selectActFrom  = bpmcFormRpc.getByKey(task.getFormKey()).getData();
        String url = selectActFrom.getActionUrl();
        if(WebUtil.strIsEmpty(task.getAssignee())) {
            bpmcTaskRpc.claim(task.getId().toString(), SessionContext.getSessionContext().getUserTicket().getId().toString());
        }else {
        	if(!task.getAssignee().equals(SessionContext.getSessionContext().getUserTicket().getId().toString())) {
        		return null;
        	}
        }
        approveService.buildCompleteApprove(modelMap, Long.valueOf(id));
		Project project = this.projectService.get(((ProjectComplete) modelMap.get("complete")).getProjectId());

		Map<Object, Object> metadata1 = new HashMap<>();
		metadata1.put("dep", "depProvider");
		metadata1.put("type", "projectTypeProvider");
		metadata1.put("startDate", "almDateProvider");
		metadata1.put("endDate", "almDateProvider");
		metadata1.put("actualStartDate", "datetimeProvider");
		metadata1.put("projectManager", "memberProvider");
		metadata1.put("testManager", "memberProvider");
		metadata1.put("productManager", "memberProvider");
		metadata1.put("developManager", "memberProvider");
		metadata1.put("businessOwner", "memberProvider");
		metadata1.put("originator", "memberProvider");

		Map projectViewModel = ValueProviderUtils.buildDataByProvider(metadata1, Arrays.asList(project)).get(0);
		modelMap.addAttribute("project1", projectViewModel);
		modelMap.put("viewMode", false);
    	modelMap.put("taskId",task.getId());
		return url+"?taskId="+task.getId();
	}
	@RequestMapping(value = "/complete/{id}", method = RequestMethod.GET)
	public String complete(ModelMap modelMap, @PathVariable("id") Long id, String viewMode) throws Exception {

		approveService.buildCompleteApprove(modelMap, id);
		Project project = this.projectService.get(((ProjectComplete) modelMap.get("complete")).getProjectId());

		Map<Object, Object> metadata1 = new HashMap<>();
		metadata1.put("dep", "depProvider");
		metadata1.put("type", "projectTypeProvider");
		metadata1.put("startDate", "almDateProvider");
		metadata1.put("endDate", "almDateProvider");
		metadata1.put("actualStartDate", "datetimeProvider");
		metadata1.put("projectManager", "memberProvider");
		metadata1.put("testManager", "memberProvider");
		metadata1.put("productManager", "memberProvider");
		metadata1.put("developManager", "memberProvider");
		metadata1.put("businessOwner", "memberProvider");
		metadata1.put("originator", "memberProvider");

		Map projectViewModel = ValueProviderUtils.buildDataByProvider(metadata1, Arrays.asList(project)).get(0);
		modelMap.addAttribute("project1", projectViewModel);
		if (StringUtils.isNotBlank(viewMode)) {
			modelMap.put("viewMode", true);
			return "approveComplete/detail";
		} else {
			modelMap.put("viewMode", false);
		}
		return "approveComplete/approve";
	}

	@RequestMapping("/loadDesc")
	@ResponseBody
	public List<Map> loadDesc(Long id) throws Exception {
		Approve approve = approveService.get(id);
		Map<Object, Object> metadata = new HashMap<>(2);
		metadata.put("userId", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("approveDate", JSON.parse("{provider:'datetimeProvider'}"));
		List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata,
				JSON.parseArray(approve.getDescription(), ApplyApprove.class));
		return maps;
	}
	/**
	 * alm3.5以前的审批功能
	 * @param id
	 * @param opt
	 * @param notes
	 * @return
	 */
	@RequestMapping("/applyApprove")
	@ResponseBody
	public BaseOutput<Object> applyApprove(Long id, String opt, String notes) {
		try {
			approveService.applyApprove(id, opt, notes);
			return BaseOutput.success();
		} catch (ApproveException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping("/verityApprove")
	@ResponseBody
	public BaseOutput verityApprove(Long id, String opt, String notes) {
		return approveService.verity(id, opt, notes);
	}

	@ApiOperation(value = "查询Approve", notes = "查询Approve，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Approve", paramType = "form", value = "Approve的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Approve> list(Approve approve) {
		return approveService.list(approve);
	}

	@ApiOperation(value = "分页查询Approve", notes = "分页查询Approve，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Approve", paramType = "form", value = "Approve的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/apply/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(Approve approve) throws Exception {
		approve.setType(AlmConstants.ApproveType.APPLY.getCode());
		if (approve.getCreated() != null) {
			Date temp = approve.getCreated();
			approve.setCreated(DateUtil.appendDateToEnd(approve.getCreated()));
			approve.setCreatedStart(DateUtil.appendDateToStart(temp));
		}

		return approveService.selectApproveByPage(approve).toString();
	}

	@RequestMapping(value = "/change/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String changeListPage(Approve approve) throws Exception {
		approve.setType(AlmConstants.ApproveType.CHANGE.getCode());
		if (approve.getCreated() != null) {
			Date temp = approve.getCreated();
			approve.setCreated(DateUtil.appendDateToEnd(approve.getCreated()));
			approve.setCreatedStart(DateUtil.appendDateToStart(temp));
		}
		return approveService.selectApproveByPage(approve).toString();
	}

	@RequestMapping(value = "/complete/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String completeListPage(Approve approve) throws Exception {
		approve.setType(AlmConstants.ApproveType.COMPLETE.getCode());
		if (approve.getCreated() != null) {
			Date temp = approve.getCreated();
			approve.setCreated(DateUtil.appendDateToEnd(approve.getCreated()));
			approve.setCreatedStart(DateUtil.appendDateToStart(temp));
		}
		return approveService.selectApproveByPage(approve).toString();
	}

	@ApiOperation("新增Approve")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Approve", paramType = "form", value = "Approve的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(Approve approve) {
		approveService.insertSelective(approve);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改Approve")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Approve", paramType = "form", value = "Approve的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(Approve approve) {
		approveService.updateSelective(approve);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除Approve")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "Approve的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		approveService.delete(id);
		return BaseOutput.success("删除成功");
	}

	@RequestMapping(value = "/doc/apply/{id}", method = RequestMethod.GET)
	public void applyDoc(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {

		String fileName = "立项申请文档.docx";
		// 默认使用IE的方式进行编码
		try {
			String userAgent = request.getHeader("User-Agent");
			String rtn = "filename=\"" + fileName + "\"";
			if (userAgent != null) {
				userAgent = userAgent.toLowerCase();
				// IE浏览器，只能采用URLEncoder编码
				if (userAgent.contains("msie")) {
					rtn = "filename=\"" + fileName + "\"";
				}
				// Opera浏览器只能采用filename*
				else if (userAgent.contains("opera")) {
					rtn = "filename*=UTF-8''" + fileName;
				}
				// Safari浏览器，只能采用ISO编码的中文输出
				else if (userAgent.contains("safari")) {
					rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
				} else if (userAgent.contains("mozilla")) {
					rtn = "filename*=UTF-8''" + fileName;
				}
			}
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;" + rtn);
			approveService.downloadProjectDoc(AlmConstants.ApproveType.APPLY, id, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/doc/change/{id}", method = RequestMethod.GET)
	public void changeDoc(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {

		String fileName = "变更申请文档.docx";
		// 默认使用IE的方式进行编码
		try {
			String userAgent = request.getHeader("User-Agent");
			String rtn = "filename=\"" + fileName + "\"";
			if (userAgent != null) {
				userAgent = userAgent.toLowerCase();
				// IE浏览器，只能采用URLEncoder编码
				if (userAgent.contains("msie")) {
					rtn = "filename=\"" + fileName + "\"";
				}
				// Opera浏览器只能采用filename*
				else if (userAgent.contains("opera")) {
					rtn = "filename*=UTF-8''" + fileName;
				}
				// Safari浏览器，只能采用ISO编码的中文输出
				else if (userAgent.contains("safari")) {
					rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
				} else if (userAgent.contains("mozilla")) {
					rtn = "filename*=UTF-8''" + fileName;
				}
			}
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;" + rtn);
			approveService.downloadProjectDoc(AlmConstants.ApproveType.CHANGE, id, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/doc/complete/{id}", method = RequestMethod.GET)
	public void completeDoc(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {

		String fileName = "结项申请文档.docx";
		// 默认使用IE的方式进行编码
		try {
			String userAgent = request.getHeader("User-Agent");
			String rtn = "filename=\"" + fileName + "\"";
			if (userAgent != null) {
				userAgent = userAgent.toLowerCase();
				// IE浏览器，只能采用URLEncoder编码
				if (userAgent.contains("msie")) {
					rtn = "filename=\"" + fileName + "\"";
				}
				// Opera浏览器只能采用filename*
				else if (userAgent.contains("opera")) {
					rtn = "filename*=UTF-8''" + fileName;
				}
				// Safari浏览器，只能采用ISO编码的中文输出
				else if (userAgent.contains("safari")) {
					rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
				} else if (userAgent.contains("mozilla")) {
					rtn = "filename*=UTF-8''" + fileName;
				}
			}
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;" + rtn);
			approveService.downloadProjectDoc(AlmConstants.ApproveType.COMPLETE, id, response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}