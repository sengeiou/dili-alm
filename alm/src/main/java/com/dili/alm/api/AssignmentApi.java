package com.dili.alm.api;

import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.Team;
import com.dili.uap.sdk.domain.User;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.AssignmentService;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.OnlineDataChangeService;
import com.dili.alm.service.ProjectService;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
	//@ApiOperation("根据变更ID，返回dba审批人")
/*	@RequestMapping("/setDbaOnlineDataChangeAssigneeName")
	public @ResponseBody BaseOutput<Assignment> setDbaOnlineDataChangeAssigneeName(@RequestBody Long onlineDataChangeId) {
		
		Assignment record = assignmentService.setDbaOnlineDataChangeAssigneeName(onlineDataChangeId);
		return BaseOutput.success().setData(record);
		
	}*/
	
	
	
	
	
	
	                                                                            
	
	
}