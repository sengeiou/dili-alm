package com.dili.alm.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.service.AssignmentService;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.ss.domain.BaseOutput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("/assignmentApi")
@Controller
@RequestMapping("/assignmentApi")
public class AssignmentApi {
	@Autowired
	private AssignmentService assignmentService;

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
/*	@ApiOperation("根据变更ID，返回dba审批人")
	@RequestMapping("/setDbaOnlineDataChangeAssigneeName")
	public @ResponseBody BaseOutput<Assignment> setDbaOnlineDataChangeAssigneeName(@RequestBody Long onlineDataChangeId) {

		Assignment record = assignmentService.setDbaOnlineDataChangeAssigneeName(onlineDataChangeId);
		return BaseOutput.success().setData(record);

	}

	// 根据变更ID，返回dba审批人
	@ApiOperation("根据变更ID，返回dba审批人")
	@RequestMapping("/setOnlineConfirm")
	public @ResponseBody BaseOutput<Assignment> setOnlineConfirm(@RequestBody Long onlineDataChangeId) {

		Assignment record = assignmentService.setOnlineConfirm(onlineDataChangeId);
		return BaseOutput.success().setData(record);

	}*/

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
		Assignment record = assignmentService.setReciprocate(jsonObj.getLong("businessKey"));
		return BaseOutput.success().setData(record);

	}
}