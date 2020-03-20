package com.dili.alm.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.service.WorkOrderApplyService;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("/workOrderApplyApi")
@Controller
@RequestMapping("/workOrderApplyApi")
public class WorkOrderApplyApi {
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkOrderApplyApi.class);
	@Autowired
	private WorkOrderApplyService workOrderApply;

	
	// 编辑
	@ApiOperation("编辑")
	@RequestMapping("/setOrderApplyAssigneeName.api")
	public @ResponseBody BaseOutput<Assignment> setOrderApplyAssigneeName(@RequestBody TaskMapping taskMapping) {
	/*	Map<String, String[]> map = request.getParameterMap();
		String[] strs = map.get("processVariables");
		JSONObject jsonObj = JSON.parseObject(strs[0]);
		Assignment record =workOrderApply.setOrderApplyName(jsonObj.getLong("businessKey"));*/
		
		String edit = taskMapping.getProcessVariables().get("edit").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(edit);
		return BaseOutput.success().setData(assignment);
	}
	// 分配数据变
	@ApiOperation("分配数据变")
	@RequestMapping("/setAllocateAssigneeName.api")
	public @ResponseBody BaseOutput<Assignment> setAllocateAssigneeName(@RequestBody TaskMapping taskMapping) {
		/*Map<String, String[]> map = request.getParameterMap();
		String[] strs = map.get("processVariables");
		JSONObject jsonObj = JSON.parseObject(strs[0]);
		Assignment record =workOrderApply.setAllocateName(jsonObj.getLong("businessKey"));*/
		
		String productManagerId = taskMapping.getProcessVariables().get("allocate").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(productManagerId);
		return BaseOutput.success().setData(assignment);
		
	}

	// 解决人
	@ApiOperation("解决人")
	@RequestMapping("/setSolveAssigneeName.api")
	public @ResponseBody BaseOutput<Assignment> setSolveAssigneeName(@RequestBody TaskMapping taskMapping) {
		String productManagerId = taskMapping.getProcessVariables().get("solve").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(productManagerId);
		return BaseOutput.success().setData(assignment);

	}

	// 解决人
	@ApiOperation("关闭人")
	@RequestMapping("/setCloseAssigneeName.api")
	public @ResponseBody BaseOutput<Assignment> setsetCloseAssigneeName(@RequestBody TaskMapping taskMapping) {
		String idid= (String) taskMapping.getProcessVariables().get("close");
		//Long id=Long.parseLong(idid);
		Assignment record = DTOUtils.newDTO(Assignment.class);
		record.setAssignee(idid);
		//Assignment record =workOrderApply.setCloseName(id);
		return BaseOutput.success().setData(record);

	}

	//
/*	public @ResponseBody BaseOutput<Assignment> setDeptOnlineDataChangeAssigneeName(@RequestBody TaskMapping taskMapping) {
		String dept = taskMapping.getProcessVariables().get("dept").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee(dept);
		return BaseOutput.success().setData(assignment);

	}
*/




}