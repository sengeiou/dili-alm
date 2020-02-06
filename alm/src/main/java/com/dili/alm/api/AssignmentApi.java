package com.dili.alm.api;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.BpmConsts;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.service.AssignmentService;
import com.dili.alm.service.ProjectOnlineApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicates;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("/assignmentApi")
@Controller
@RequestMapping("/assignmentApi")
public class AssignmentApi {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssignmentApi.class);
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectOnlineApplyService projectOnlineApplyService;

	// 申请数据变
	@ApiOperation("申请数据变")
	@RequestMapping("/setSubmitOnlineDataChangeAssigneeName.api")
	public @ResponseBody BaseOutput<Assignment> setsubmitOnlineDataChangeAssigneeName(HttpServletRequest request) {
		Map<String, String[]> map = request.getParameterMap();
		String[] strs = map.get("processVariables");
		JSONObject jsonObj = JSON.parseObject(strs[0]);
		Assignment record = assignmentService.setSubmitOnlineDataChangeAssigneeName(jsonObj.getLong("businessKey"));
		return BaseOutput.success().setData(record);
	}

	// 根据变更ID，返回部门审批人
	@ApiOperation("根据变更ID，返回部门审批人")
	@RequestMapping("/setDeptOnlineDataChangeAssigneeName.api")
	public @ResponseBody BaseOutput<Assignment> setDeptOnlineDataChangeAssigneeName(HttpServletRequest request) {
		Map<String, String[]> map = request.getParameterMap();
		String[] strs = map.get("processVariables");
		JSONObject jsonObj = JSON.parseObject(strs[0]);
		Assignment record = assignmentService.setDeptOnlineDataChangeAssigneeName(jsonObj.getLong("businessKey"));

		return BaseOutput.success().setData(record);

	}

	// 根据变更ID，返回测试审批人
	@ApiOperation("根据变更ID，返回测试审批人")
	@RequestMapping("/setTestOnlineDataChangeAssigneeName.api")
	public @ResponseBody BaseOutput<Assignment> setTestOnlineDataChangeAssigneeName(HttpServletRequest request) {
		Map<String, String[]> map = request.getParameterMap();
		String[] strs = map.get("processVariables");
		JSONObject jsonObj = JSON.parseObject(strs[0]);
		Assignment record = assignmentService.setTestOnlineDataChangeAssigneeName(jsonObj.getLong("businessKey"));
		return BaseOutput.success().setData(record);

	}

	// 根据变更ID，返回dba审批人
	/*
	 * @ApiOperation("根据变更ID，返回dba审批人")
	 * 
	 * @RequestMapping("/setDbaOnlineDataChangeAssigneeName") public @ResponseBody
	 * BaseOutput<Assignment> setDbaOnlineDataChangeAssigneeName(@RequestBody Long
	 * onlineDataChangeId) {
	 * 
	 * Assignment record =
	 * assignmentService.setDbaOnlineDataChangeAssigneeName(onlineDataChangeId);
	 * return BaseOutput.success().setData(record);
	 * 
	 * }
	 * 
	 * // 根据变更ID，返回dba审批人
	 * 
	 * @ApiOperation("根据变更ID，返回dba审批人")
	 * 
	 * @RequestMapping("/setOnlineConfirm") public @ResponseBody
	 * BaseOutput<Assignment> setOnlineConfirm(@RequestBody Long onlineDataChangeId)
	 * {
	 * 
	 * Assignment record = assignmentService.setOnlineConfirm(onlineDataChangeId);
	 * return BaseOutput.success().setData(record);
	 * 
	 * }
	 */

	// 根据变更ID，返回测试审批人
	@ApiOperation("根据变更ID,項目申請人")
	@RequestMapping("/getOnlineDataChangeUserName.api")
	public @ResponseBody BaseOutput<String> getTestOnlineDataChangeUserName(HttpServletRequest request) {
		Map<String, String[]> map = request.getParameterMap();
		String[] strs = map.get("processVariables");
		JSONObject jsonObj = JSON.parseObject(strs[0]);
		String record = assignmentService.setTestOnlineDataChangeUserName(jsonObj.getLong("businessKey"));
		return BaseOutput.success().setData(record);

	}

	// 根据变更ID，返回测试审批人
	@ApiOperation("根据变更ID,項目申請人")
	@RequestMapping("/setReciprocate.api")
	public @ResponseBody BaseOutput<String> setReciprocate(HttpServletRequest request) {
		Map<String, String[]> map = request.getParameterMap();
		String[] strs = map.get("processVariables");
		JSONObject jsonObj = JSON.parseObject(strs[0]);
		Assignment record = assignmentService.setReciprocate(jsonObj.get("businessKey").toString());
		return BaseOutput.success().setData(record);

	}

	// 驳回返回给需求发布人
	@ApiOperation("根据变更ID,項目申請人")
	@RequestMapping("/setDemandAppId.api")
	public @ResponseBody BaseOutput<String> setDemandAppId(HttpServletRequest request) {
		Map<String, String[]> map = request.getParameterMap();
		String[] strs = map.get("processVariables");
		JSONObject jsonObj = JSON.parseObject(strs[0]);
		Assignment record = assignmentService.setDemandAppId(jsonObj.get("businessKey").toString());
		return BaseOutput.success().setData(record);
	}

	@ResponseBody
	@RequestMapping(value = "/getProjectOnlineApplyProjectManager.api")
	public BaseOutput<Assignment> getProjectManager(TaskMapping taskMapping) {
		String projectManagerId = taskMapping.getProcessVariables().get(BpmConsts.ProjectOnlineApplyConstant.PROJECT_MANAGER_KEY.getName()).toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(projectManagerId);
		return BaseOutput.success().setData(assignment);
	}

	@ResponseBody
	@RequestMapping(value = "/getProjectOnlineApplyExecutor.api")
	public BaseOutput<Assignment> getProjectOnlineApplyExecutor(TaskMapping taskMapping) {
		String executorId = taskMapping.getProcessVariables().get(BpmConsts.ProjectOnlineApplyConstant.EXECUTOR_KEY.getName()).toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(executorId);
		return BaseOutput.success().setData(assignment);
	}

	@ResponseBody
	@RequestMapping(value = "/getProjectOnlineApplyProductManager.api")
	public BaseOutput<Assignment> getProjectOnlineApplyProductManager(TaskMapping taskMapping) {
		String productManagerId = taskMapping.getProcessVariables().get(BpmConsts.ProjectOnlineApplyConstant.PRODUCT_MANAGER_KEY.getName()).toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(productManagerId);
		return BaseOutput.success().setData(assignment);
	}
	
	@ResponseBody
	@RequestMapping(value = "/getProjectOnlineApplyApplicant.api")
	public BaseOutput<Assignment> getProjectOnlineApplyApplicant(TaskMapping taskMapping) {
		String applicantId = taskMapping.getProcessVariables().get(BpmConsts.ProjectOnlineApplyConstant.APPLICANT_KEY.getName()).toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(applicantId);
		return BaseOutput.success().setData(assignment);
	}
}