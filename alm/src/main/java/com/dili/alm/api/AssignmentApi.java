package com.dili.alm.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.service.AssignmentService;
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

	//申请数据变
	@ApiOperation("申请数据变")
	@RequestMapping("/setSubmitOnlineDataChangeAssigneeName.api")
	public @ResponseBody BaseOutput<Assignment> setsubmitOnlineDataChangeAssigneeName(@RequestBody Long onlineDataChangeId) {
		Assignment record = assignmentService.setSubmitOnlineDataChangeAssigneeName(onlineDataChangeId);
		return BaseOutput.success().setData(record);
	}
	//根据变更ID，返回部门审批人
	@ApiOperation("根据变更ID，返回部门审批人")
	@RequestMapping("/setDeptOnlineDataChangeAssigneeName.api")
	public @ResponseBody BaseOutput<Assignment> setDeptOnlineDataChangeAssigneeName(@RequestBody Long onlineDataChangeId) {
		Assignment record = assignmentService.setDeptOnlineDataChangeAssigneeName(onlineDataChangeId);
		
		return BaseOutput.success().setData(record);
		
	}
	//根据变更ID，返回测试审批人
	@ApiOperation("根据变更ID，返回测试审批人")
	@RequestMapping("/setTestOnlineDataChangeAssigneeName.api")
	public @ResponseBody BaseOutput<Assignment> setTestOnlineDataChangeAssigneeName(@RequestBody Long onlineDataChangeId) {
		
		Assignment record = assignmentService.setTestOnlineDataChangeAssigneeName(onlineDataChangeId);
		return BaseOutput.success().setData(record);
		
	}
	//根据变更ID，返回dba审批人
	@ApiOperation("根据变更ID，返回dba审批人")
	@RequestMapping("/setDbaOnlineDataChangeAssigneeName")
	public @ResponseBody BaseOutput<Assignment> setDbaOnlineDataChangeAssigneeName(@RequestBody Long onlineDataChangeId) {
		
		Assignment record = assignmentService.setDbaOnlineDataChangeAssigneeName(onlineDataChangeId);
		return BaseOutput.success().setData(record);
		
	}
	//根据变更ID，返回dba审批人
	@ApiOperation("根据变更ID，返回dba审批人")
	@RequestMapping("/setOnlineConfirm")
	public @ResponseBody BaseOutput<Assignment> setOnlineConfirm(@RequestBody Long onlineDataChangeId) {
		
		Assignment record = assignmentService.setOnlineConfirm(onlineDataChangeId);
		return BaseOutput.success().setData(record);
		
	}
	
	
	
	//根据变更ID，返回测试审批人
	@ApiOperation("根据变更ID,項目申請人")
	@RequestMapping("/getTestOnlineDataChangeUserName.api")
	public @ResponseBody BaseOutput<String> getTestOnlineDataChangeUserName(@RequestBody Long onlineDataChangeId) {
		
		String record = assignmentService.setTestOnlineDataChangeUserName(onlineDataChangeId);
		return BaseOutput.success().setData(record);
		
	}
	
	
	
	
	                                                                            
	
	
}