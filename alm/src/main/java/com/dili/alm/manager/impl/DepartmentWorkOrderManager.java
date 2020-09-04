package com.dili.alm.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.BpmConsts;
import com.dili.alm.dao.WorkOrderExecutionRecordMapper;
import com.dili.alm.dao.WorkOrderMapper;
import com.dili.alm.dao.WorkOrderOperationRecordMapper;
import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.WorkOrderExecutionRecord;
import com.dili.alm.domain.WorkOrderOperationRecord;
import com.dili.alm.domain.WorkOrderOperationType;
import com.dili.alm.domain.WorkOrderSource;
import com.dili.alm.domain.WorkOrderState;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.exceptions.WorkOrderException;
/*import com.dili.alm.rpc.MyTasksRpcCopy;
import com.dili.alm.rpc.RuntimeApiRpcCopy;*/
import com.dili.alm.service.DataDictionaryService;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.rpc.RuntimeRpc;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.util.DateUtils;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.UserRpc;
import com.google.common.collect.Sets;

@Component
public class DepartmentWorkOrderManager extends BaseWorkOrderManager {

	private static final String DEPARTMENT_SWITCH = "department_work_order_switch";

	@Autowired
	private WorkOrderMapper workOrderMapper;
	@Autowired
	private DataDictionaryService ddService;
	@Autowired
	private WorkOrderOperationRecordMapper woorMapper;
	@Autowired
	private WorkOrderExecutionRecordMapper woerMapper;

	@Autowired
	private UserRpc userRpc;

	@Autowired
	private TaskRpc tasksRpc;

	@Autowired
	private RuntimeRpc runtimeRpc;

	@Override
	public void update(WorkOrder workOrder) throws WorkOrderException {
		workOrder.setAcceptorId(null);
		this.updateExactSimple(workOrder);
		int rows = this.updateExactSimple(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("修改工单失败");
		}
	}

	@Override
	public void submit(WorkOrder workOrder) throws WorkOrderException {
		// 部门工单，直接跳转到工单解决
		workOrder.setWorkOrderState(WorkOrderState.SOLVING.getValue());
		User mailReceiver = AlmCache.getInstance().getUserMap().get(workOrder.getExecutorId());
		workOrder.setSubmitTime(new Date());
		workOrder.setModifyTime(new Date());
		int rows = this.workOrderMapper.updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("提交工单失败");
		}
		if (mailReceiver == null) {
			throw new WorkOrderException("受理人不存在");
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("workOrderSource", WorkOrderSource.DEPARTMENT.getValue().toString());
		// map.put("solve", workOrder.getExecutorId().toString());
		map.put(BpmConsts.WorkOrderApply.SOLVE.getName(), workOrder.getExecutorId().toString());

		try {
			BaseOutput<ProcessInstanceMapping> object = runtimeRpc.startProcessInstanceByKey(BpmConsts.WorkOrderApply_PROCESS, workOrder.getId().toString(), workOrder.getExecutorId() + "", map);
			if (object.getCode().equals("5000")) {
				throw new WorkOrderException("新增工单失败流控中心调不通");
			}
			workOrder.setProcessInstanceId(object.getData().getProcessInstanceId());
			workOrder.setProcessDefinitionId(object.getData().getProcessDefinitionId());
			workOrder.setId(workOrder.getId());
			workOrderMapper.updateByPrimaryKey(workOrder);
		} catch (Exception e) {
			throw new WorkOrderException("新增工单失败流控中心调不通");
		}

		this.sendMail(workOrder, "工单申请", Sets.newHashSet(mailReceiver.getEmail()));
	}

	@Override
	public List<User> getReceivers() {
		DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.WORK_ORDER_MEMBER_FILTER_SWITCH);
		if (dd == null) {
			return null;
		}
		if (CollectionUtils.isEmpty(dd.getValues())) {
			return null;
		}
		DataDictionaryValueDto ddValue = dd.getValues().stream().filter(v -> v.getCode().equals(DEPARTMENT_SWITCH)).findFirst().orElse(null);
		if (ddValue == null) {
			return null;
		}
		if (Boolean.valueOf(ddValue.getValue())) {
			return super.getReceivers();
		}
		return new ArrayList<>(AlmCache.getInstance().getUserMap().values());
	}

	@Override
	public void solve(WorkOrder workOrder, Date startDate, Date endDate, Integer taskHours, Integer overtimeHours, String workContent) throws WorkOrderException {
		// 检查状态
		if (!workOrder.getWorkOrderState().equals(WorkOrderState.SOLVING.getValue())) {
			throw new WorkOrderException("当前状态不能执行解决操作");
		}

		// 判断工时
		int days = DateUtils.differentDays(startDate, endDate) + 1;
		if (taskHours > days * 8) {
			throw new WorkOrderException("常规工时超过最大值，每日常规工时最多为8小时！");
		}

		// 生成操作记录
		WorkOrderOperationRecord woor = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		woor.setOperationType(WorkOrderOperationType.EXECUTOR.getValue());
		woor.setOperationName(WorkOrderOperationType.EXECUTOR.getName());
		woor.setOperationResult(OperationResult.SUCCESS.getValue());
		woor.setOperatorId(workOrder.getExecutorId());
		woor.setDescription(workContent);
		woor.setWorkOrderId(workOrder.getId());
		int rows = this.woorMapper.insertSelective(woor);
		if (rows <= 0) {
			throw new WorkOrderException("插入操作记录失败");
		}

		// 插入工单执行记录
		WorkOrderExecutionRecord woer = DTOUtils.newDTO(WorkOrderExecutionRecord.class);
		woer.setWorkOrderId(workOrder.getId());
		woer.setStartDate(startDate);
		woer.setEndDate(endDate);
		woer.setTaskHours(taskHours);
		woer.setOvertimeHours(overtimeHours);
		woer.setWorkContent(workContent);
		rows = this.woerMapper.insertSelective(woer);
		if (rows <= 0) {
			throw new WorkOrderException("插入工单执行记录失败");
		}

		// 更新工单
		workOrder.setWorkOrderState(WorkOrderState.SOLVED.getValue());
		workOrder.setModifyTime(new Date());
		rows = this.workOrderMapper.updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("更新工单状态失败");
		} // 发邮件
		Set<String> mails = new HashSet<>();
		User applicant = AlmCache.getInstance().getUserMap().get(workOrder.getApplicantId());
		if (applicant == null) {
			throw new WorkOrderException("申请人不存在");
		}
		// 邮件通知部门经理
		// 查询数据字典配置
		DataDictionaryDto ddDto = this.ddService.findByCode(AlmConstants.DEPARTMENT_MANAGER_ROLE_CONFIG_CODE);
		if (ddDto == null || CollectionUtils.isEmpty(ddDto.getValues())) {
			throw new WorkOrderException("请先配置部门经理数据字典");
		}
		Map<Long, User> userMap = AlmCache.getInstance().getUserMap();
		Long applicantDeptId = userMap.get(workOrder.getApplicantId()).getDepartmentId();
		User manager = userMap.values().stream().filter(u -> {
			if (u.getDepartmentId() == null) {
				return false;
			}
			if (!u.getDepartmentId().equals(applicantDeptId)) {
				return false;
			}
			DataDictionaryValueDto value = ddDto.getValues().stream().filter(v -> v.getValue().equals(u.getUserName())).findFirst().orElse(null);
			if (value == null) {
				return false;
			}
			return true;
		}).findFirst().orElse(null);
		if (manager != null) {
			mails.add(manager.getEmail());
		}
		this.sendMail(workOrder, "工单执行", mails);
	}

	@Override
	public WorkOrderSource getType() {
		return WorkOrderSource.DEPARTMENT;
	}

	@Override
	public void submitAgain(WorkOrder workOrder, String taskId) throws WorkOrderException {
		// 部门工单，直接跳转到工单解决
		workOrder.setWorkOrderState(WorkOrderState.SOLVING.getValue());
		User mailReceiver = AlmCache.getInstance().getUserMap().get(workOrder.getExecutorId());
		workOrder.setSubmitTime(new Date());
		workOrder.setModifyTime(new Date());
		int rows = this.workOrderMapper.updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("提交工单失败");
		}
		if (mailReceiver == null) {
			throw new WorkOrderException("受理人不存在");
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("workOrderSource", WorkOrderSource.DEPARTMENT.getValue().toString());

		// map.put("solve", workOrder.getExecutorId().toString());
		map.put(BpmConsts.WorkOrderApply.SOLVE.getName(), workOrder.getExecutorId().toString());
		/*
		 * if(workOrder.getWorkOrderSource()==2) { map.put("solve",
		 * workOrder.getExecutorId().toString()); }else { map.put("allocate",
		 * workOrder.getAcceptorId().toString()); }
		 */
		try {
			tasksRpc.complete(taskId, map);
		} catch (Exception e) {
			throw new WorkOrderException("部门工单失败");
		}

		this.sendMail(workOrder, "工单申请", Sets.newHashSet(mailReceiver.getEmail()));
	}

}
