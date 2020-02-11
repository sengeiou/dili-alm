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
import com.dili.alm.service.WorkOrderApplyService;
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
	public @ResponseBody BaseOutput<Assignment> setOrderApplyAssigneeName(HttpServletRequest request) {
		Map<String, String[]> map = request.getParameterMap();
		String[] strs = map.get("processVariables");
		JSONObject jsonObj = JSON.parseObject(strs[0]);
		Assignment record =workOrderApply.setOrderApplyName(jsonObj.getLong("businessKey"));
		return BaseOutput.success().setData(record);
	}
	// 分配数据变
	@ApiOperation("分配数据变")
	@RequestMapping("/setAllocateAssigneeName.api")
	public @ResponseBody BaseOutput<Assignment> setAllocateAssigneeName(TaskMapping taskMapping) {
		/*Map<String, String[]> map = request.getParameterMap();
		String[] strs = map.get("processVariables");
		JSONObject jsonObj = JSON.parseObject(strs[0]);
		Assignment record =workOrderApply.setAllocateName(jsonObj.getLong("businessKey"));*/
		
		String productManagerId = taskMapping.getProcessVariables().get("allocate").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee("1");
		return BaseOutput.success().setData(assignment);
		
	}

	// 解决人
	@ApiOperation("解决人")
	@RequestMapping("/setSolveAssigneeName.api")
	public @ResponseBody BaseOutput<Assignment> setSolveAssigneeName(TaskMapping taskMapping) {
		String productManagerId = taskMapping.getProcessVariables().get("solve").toString();
		Assignment assignment = DTOUtils.newDTO(Assignment.class);
		assignment.setAssignee("1");
		return BaseOutput.success().setData(assignment);

	}

	// 解决人
		@ApiOperation("关闭人")
		@RequestMapping("/setCloseAssigneeName.api")
		public @ResponseBody BaseOutput<Assignment> setsetCloseAssigneeName(TaskMapping taskMapping) {
			String idid= (String) taskMapping.getProcessVariables().get("close");
			//Long id=Long.parseLong(idid);
			Assignment record = DTOUtils.newDTO(Assignment.class);
			record.setAssignee(idid);
			//Assignment record =workOrderApply.setCloseName(id);
			return BaseOutput.success().setData(record);

		}

	




}