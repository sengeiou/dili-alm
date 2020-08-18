package com.dili.alm.api;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.dili.alm.constant.BpmConsts;
import com.dili.alm.service.AssignmentService;
import com.dili.alm.service.ProjectOnlineApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;

/*import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;*/

/*@Api("/assignmentApi")*/
@RestController
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
//	@ApiOperation("申请数据变")
	@PostMapping("/setSubmitOnlineDataChangeAssigneeName.api")
	public BaseOutput<Assignment> setsubmitOnlineDataChangeAssigneeName( TaskMapping taskMapping) {
		String dept = taskMapping.getProcessVariables().get(BpmConsts.OnlineDataChangeProcessConstant.submit.getName()).toString();
		Assignment assignment = DTOUtils.newInstance(Assignment.class);
		assignment.setAssignee(dept);
		return BaseOutput.successData(assignment);
	}

	// 根据变更ID，返回部门审批人
/*	@ApiOperation("根据变更ID，返回部门审批人")*/
	@PostMapping("/setDeptOnlineDataChangeAssigneeName.api")
	public BaseOutput<Assignment> setDeptOnlineDataChangeAssigneeName( TaskMapping taskMapping) {
		String dept = taskMapping.getProcessVariables().get(BpmConsts.OnlineDataChangeProcessConstant.dept.getName()).toString();
		Assignment assignment = DTOUtils.newInstance(Assignment.class);
		assignment.setAssignee(dept);
		return BaseOutput.successData(assignment);
	}

	// 根据变更ID，返回测试审批人
/*	@ApiOperation("根据变更ID，返回测试审批人")*/
	@PostMapping("/setTestOnlineDataChangeAssigneeName.api")
	public BaseOutput<Assignment> setTestOnlineDataChangeAssigneeName( TaskMapping taskMapping) {
		String dept = taskMapping.getProcessVariables().get(BpmConsts.OnlineDataChangeProcessConstant.test.getName()).toString();
		Assignment assignment = DTOUtils.newInstance(Assignment.class);
		assignment.setAssignee(dept);
		return BaseOutput.successData(assignment);
	}

	// 根据变更ID，返回dba审批人
	/*
	 * @ApiOperation("根据变更ID，返回dba审批人")
	 * 
	 * @RequestMapping("/setDbaOnlineDataChangeAssigneeName") public @ResponseBody
	 * BaseOutput<Assignment> setDbaOnlineDataChangeAssigneeName( Long
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
	 * BaseOutput<Assignment> setOnlineConfirm( Long onlineDataChangeId)
	 * {
	 * 
	 * Assignment record = assignmentService.setOnlineConfirm(onlineDataChangeId);
	 * return BaseOutput.success().setData(record);
	 * 
	 * }
	 */

	// 根据变更ID，返回测试审批人
/*	@ApiOperation("根据变更ID,項目申請人")*/
	@PostMapping("/getOnlineDataChangeUserName.api")
	public BaseOutput<String> getOnlineDataChangeUserName( TaskMapping taskMapping) {
		String dept = taskMapping.getProcessVariables().get("apply").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(dept);
		return BaseOutput.success().setData(assignment);
	}

    /****需求管理 begin*****/
/*	@ApiOperation("获取需求部门经理审批人")*/
	@PostMapping("/setDepartmentManagerId.api")
	public BaseOutput<String> setDepartmentManagerId( TaskMapping taskMapping) {
		String executorId = taskMapping.getProcessVariables().get("departmentManagerId").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(executorId);
		return BaseOutput.success().setData(assignment);
	}
/*	@ApiOperation("根据需求ID,需求反馈人")*/
	@PostMapping("/setFeedback.api")
	public BaseOutput<String> setFeedback( TaskMapping taskMapping) {
/*		Map<String, Object> map = taskMapping.getProcessVariables();
		Assignment record = assignmentService.setFeedback(map.get("businessKey").toString());
		return BaseOutput.success().setData(record);*/
		String executorId = taskMapping.getProcessVariables().get("AssignExecutorId").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(executorId);
		return BaseOutput.success().setData(assignment);
	}

	
/*	@ApiOperation("根据变更ID,需求处理人")*/
	@PostMapping("/setReciprocate.api")
	public BaseOutput<String> setReciprocate( TaskMapping taskMapping) {
/*		Map<String, Object> map = taskMapping.getProcessVariables();
		Assignment record = assignmentService.setReciprocate(map.get("businessKey").toString());
		return BaseOutput.success().setData(record);*/
		String executorId = taskMapping.getProcessVariables().get("AssignExecutorId").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(executorId);
		return BaseOutput.success().setData(assignment);

	}

	// 驳回返回给需求发布人
/*	@ApiOperation("根据变更ID,項目申請人")*/
	@PostMapping("/setDemandAppId.api")
	public BaseOutput<String> setDemandAppId( TaskMapping taskMapping) {
/*		Map<String, Object> map = taskMapping.getProcessVariables();
		Assignment record = assignmentService.setDemandAppId(map.get("businessKey").toString());
		return BaseOutput.success().setData(record);*/
		String executorId = taskMapping.getProcessVariables().get("AssignExecutorId").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(executorId);
		return BaseOutput.success().setData(assignment);
	}
	 /****需求管理 end*****/
	/**LJ add begin**/
	// 资源申请，设置项目经理
/*	@ApiOperation("项目id,设置项目经理")*/
	@PostMapping("/setHardwareApplyProjectManager.api")
	public BaseOutput<String> setHardwareApplyProjectManager( TaskMapping taskMapping) {
/*		Map<String, Object> map = taskMapping.getProcessVariables();
		Assignment record = assignmentService.setProjectManager(map.get("businessKey").toString());
		return BaseOutput.success().setData(record);*/
		String executorId = taskMapping.getProcessVariables().get("AssignExecutorId").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(executorId);
		return BaseOutput.success().setData(assignment);
	}
	
	// 资源申请，设置部署运维人员
/*	@ApiOperation("根据页面保存的运维操作人员")*/
	@PostMapping("/setHardwareApplyOpdrator.api")
	public BaseOutput<String> setHardwareApplyOpdrator( TaskMapping taskMapping) {
/*		Map<String, Object> map = taskMapping.getProcessVariables();
		Assignment record = assignmentService.setOpdrator(map.get("businessKey").toString());
		return BaseOutput.success().setData(record);*/
		String executorId = taskMapping.getProcessVariables().get("AssignExecutorId").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(executorId);
		return BaseOutput.success().setData(assignment);
	}
	// 资源申请，设置返回编辑
/*	@ApiOperation("返回编辑者")*/
	@RequestMapping("/setHardwareApplyApplicant.api")
	public BaseOutput<String> setHardwareApplyApplicant( TaskMapping taskMapping) {
/*		Map<String, Object> map = taskMapping.getProcessVariables();
		Assignment record = assignmentService.setHardwareResourceApplyApply(map.get("businessKey").toString());
		return BaseOutput.success().setData(record);*/
		String executorId = taskMapping.getProcessVariables().get("AssignExecutorId").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(executorId);
		return BaseOutput.success().setData(assignment);
	}
	/**LJ add end**/
	@ResponseBody
	@RequestMapping(value = "/getProjectOnlineApplyProjectManager.api")
	public BaseOutput<Assignment> getProjectManager( TaskMapping taskMapping) {
		String projectManagerId = taskMapping.getProcessVariables().get(BpmConsts.ProjectOnlineApplyConstant.PROJECT_MANAGER_KEY.getName()).toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(projectManagerId);
		return BaseOutput.success().setData(assignment);
	}

	@ResponseBody
	@RequestMapping(value = "/getProjectOnlineApplyExecutor.api")
	public BaseOutput<Assignment> getProjectOnlineApplyExecutor( TaskMapping taskMapping) {
		String executorId = taskMapping.getProcessVariables().get(BpmConsts.ProjectOnlineApplyConstant.EXECUTOR_KEY.getName()).toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(executorId);
		return BaseOutput.success().setData(assignment);
	}

	@ResponseBody
	@RequestMapping(value = "/getProjectOnlineApplyProductManager.api")
	public BaseOutput<Assignment> getProjectOnlineApplyProductManager( TaskMapping taskMapping) {
		String productManagerId = taskMapping.getProcessVariables().get(BpmConsts.ProjectOnlineApplyConstant.PRODUCT_MANAGER_KEY.getName()).toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(productManagerId);
		return BaseOutput.success().setData(assignment);
	}
	
	@ResponseBody
	@RequestMapping(value = "/getProjectOnlineApplyApplicant.api")
	public BaseOutput<Assignment> getProjectOnlineApplyApplicant( TaskMapping taskMapping) {
		String applicantId = taskMapping.getProcessVariables().get(BpmConsts.ProjectOnlineApplyConstant.APPLICANT_KEY.getName()).toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(applicantId);
		return BaseOutput.success().setData(assignment);
	}
}