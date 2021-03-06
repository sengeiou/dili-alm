package com.dili.alm.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.BpmConsts;
import com.dili.alm.dao.WorkOrderMapper;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.WorkOrderSource;
import com.dili.alm.domain.WorkOrderState;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.exceptions.WorkOrderException;
import com.dili.alm.rpc.resolver.MyRuntimeRpc;
import com.dili.alm.rpc.resolver.MyTaskRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.rpc.restful.RuntimeRpc;
import com.dili.bpmc.sdk.rpc.restful.TaskRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.UserRpc;
import com.google.common.collect.Sets;

@Component
public class DevCenterWorkOrderManager extends BaseWorkOrderManager {

	private static final Object DEV_SWITCH = "dev_center_work_order_switch";
	@Autowired
	private DataDictionaryService ddService;
	@Autowired
	private WorkOrderMapper workOrderMapper;

	@Autowired
	private UserRpc userRpc;
	@Autowired
	private TaskRpc tasksRpc;
	@Autowired
	private RuntimeRpc runtimeRpc;
	
	@Autowired 
	private MyRuntimeRpc myRuntimeRpc;
	
	@Autowired
	private MyTaskRpc myTaskRpc;

	@Override
	public void update(WorkOrder workOrder) throws WorkOrderException {
		workOrder.setExecutorId(null);
		this.updateExactSimple(workOrder);
		int rows = this.updateExactSimple(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("新增工单失败");
		}
	}

	@Override
	public void submit(WorkOrder workOrder) throws WorkOrderException {
		// 外部工单
		workOrder.setWorkOrderState(WorkOrderState.OPENED.getValue());
		User mailReceiver = AlmCache.getInstance().getUserMap().get(workOrder.getAcceptorId());
		Date now = new Date();
		workOrder.setSubmitTime(now);
		workOrder.setModifyTime(now);
		int rows = this.workOrderMapper.updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("提交工单失败");
		}
		if (mailReceiver == null) {
			throw new WorkOrderException("受理人不存在");
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("workOrderSource", WorkOrderSource.DEVELOPMENT_CENTER.getValue().toString());

		if (workOrder.getWorkOrderSource() == WorkOrderSource.DEPARTMENT.getValue()) {
			// map.put("solve", workOrder.getExecutorId().toString());
			map.put(BpmConsts.WorkOrderApply.SOLVE.getName(), workOrder.getExecutorId().toString());
		} else {
			// map.put("allocate", workOrder.getAcceptorId().toString());
			map.put(BpmConsts.WorkOrderApply.ALLOCATE.getName(), workOrder.getAcceptorId().toString());
		}

		try {
			BaseOutput<ProcessInstanceMapping> object = myRuntimeRpc.startProcessInstanceByKey(BpmConsts.WorkOrderApply_PROCESS, workOrder.getId().toString(), workOrder.getAcceptorId() + "", map);
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
		DataDictionaryValueDto ddValue = dd.getValues().stream().filter(v -> v.getCode().equals(DEV_SWITCH)).findFirst().orElse(null);
		if (ddValue == null) {
			return null;
		}
		if (Boolean.valueOf(ddValue.getValue())) {
			dd = this.ddService.findByCode(AlmConstants.DEV_WORK_ORDER_RECEIVERS_CODE);
			if (dd == null || CollectionUtils.isEmpty(dd.getValues())) {
				return null;
			}
			List<User> target = new ArrayList<>(dd.getValues().size());
			dd.getValues().forEach(v -> {
				Map.Entry<Long, User> entry = AlmCache.getInstance().getUserMap().entrySet().stream().filter(e -> e.getValue().getUserName().equals(v.getValue())).findFirst().orElse(null);
				if (entry != null) {
					target.add(entry.getValue());
				}
			});
			return target;
		}
		return new ArrayList<>(AlmCache.getInstance().getUserMap().values());
	}

	@Override
	public WorkOrderSource getType() {
		return WorkOrderSource.DEVELOPMENT_CENTER;
	}

	@Override
	public void submitAgain(WorkOrder workOrder, String taskId) throws WorkOrderException {
		// 外部工单
		workOrder.setWorkOrderState(WorkOrderState.OPENED.getValue());
		User mailReceiver = AlmCache.getInstance().getUserMap().get(workOrder.getAcceptorId());
		Date now = new Date();
		workOrder.setSubmitTime(now);
		workOrder.setModifyTime(now);
		int rows = this.workOrderMapper.updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("提交工单失败");
		}
		if (mailReceiver == null) {
			throw new WorkOrderException("受理人不存在");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		/*
		 * map.put("workOrderSource", "2"); if(workOrder.getWorkOrderSource()==2) {
		 * map.put("solve", workOrder.getExecutorId().toString()); }else {
		 * map.put("allocate", workOrder.getAcceptorId().toString()); }
		 */

		map.put("workOrderSource", WorkOrderSource.DEVELOPMENT_CENTER.getValue().toString());

		if (workOrder.getWorkOrderSource() == WorkOrderSource.DEPARTMENT.getValue()) {
			// map.put("solve", workOrder.getExecutorId().toString());
			map.put(BpmConsts.WorkOrderApply.SOLVE.getName(), workOrder.getExecutorId().toString());
		} else {
			// map.put("allocate", workOrder.getAcceptorId().toString());
			map.put(BpmConsts.WorkOrderApply.ALLOCATE.getName(), workOrder.getAcceptorId().toString());
		}

		try {
			myTaskRpc.complete(taskId, map);
		} catch (Exception e) {
			throw new WorkOrderException("研发工单失败");
		}
		// tasksRpc.complete(taskId,map);

		this.sendMail(workOrder, "工单申请", Sets.newHashSet(mailReceiver.getEmail()));
	}

}
