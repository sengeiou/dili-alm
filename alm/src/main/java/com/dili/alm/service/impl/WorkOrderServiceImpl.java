package com.dili.alm.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.component.NumberGenerator;
import com.dili.alm.dao.WorkOrderMapper;
import com.dili.alm.dao.WorkOrderOperationRecordMapper;
import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.WorkOrderOperationRecord;
import com.dili.alm.domain.WorkOrderOperationType;
import com.dili.alm.domain.WorkOrderState;
import com.dili.alm.domain.dto.WorkOrderUpdateDto;
import com.dili.alm.exceptions.WorkOrderException;
import com.dili.alm.provider.MemberProvider;
import com.dili.alm.service.WorkOrderService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-05-23 11:51:37.
 */
@Service
public class WorkOrderServiceImpl extends BaseServiceImpl<WorkOrder, Long> implements WorkOrderService {

	@Autowired
	private WorkOrderOperationRecordMapper woorMapper;

	@Qualifier("workOrderNumberGenerator")
	@Autowired
	private NumberGenerator numberGenerator;
	@Autowired
	private MemberProvider memberProvider;

	public WorkOrderMapper getActualDao() {
		return (WorkOrderMapper) getDao();
	}

	@Override
	public void saveOrUpdate(WorkOrderUpdateDto dto) throws WorkOrderException {
		if (dto.getId() == null) {
			this.insertWorkOrder(dto);
		} else {
			this.updateWorkOrder(dto);
		}
	}

	private void updateWorkOrder(WorkOrderUpdateDto dto) throws WorkOrderException {
		if (CollectionUtils.isNotEmpty(dto.getCopyUserIds())) {
			dto.setCopyUserId(JSON.toJSONString(dto.getCopyUserIds()));
		}
		int rows = this.getActualDao().updateByPrimaryKeySelective(dto);
		if (rows <= 0) {
			throw new WorkOrderException("新增工单失败");
		}
	}

	private void insertWorkOrder(WorkOrderUpdateDto dto) throws WorkOrderException {
		if (CollectionUtils.isNotEmpty(dto.getCopyUserIds())) {
			dto.setCopyUserId(JSON.toJSONString(dto.getCopyUserIds()));
		}
		int rows = this.getActualDao().insertSelective(dto);
		if (rows <= 0) {
			throw new WorkOrderException("新增工单失败");
		}
	}

	@Override
	public void saveAndSubmit(WorkOrderUpdateDto dto) throws WorkOrderException {
		this.saveOrUpdate(dto);
		this.submit(dto.getId());
	}

	private void submit(Long id) throws WorkOrderException {
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		workOrder.setWorkOrderState(WorkOrderState.OPENED.getValue());
		workOrder.setSubmitTime(new Date());
		int rows = this.getActualDao().updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("提交工单失败");
		}
	}

	@Override
	public void allocate(Long id, Long executorId, OperationResult result, String description)
			throws WorkOrderException {
		// 检查工单是否存在
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		if (workOrder == null) {
			throw new WorkOrderException("工单不存在");
		}
		// 检查状态
		if (!workOrder.getWorkOrderState().equals(WorkOrderState.OPENED.getValue())) {
			throw new WorkOrderException("当前状态不能执行分配操作");
		}
		// 生成操作记录
		WorkOrderOperationRecord woor = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		woor.setOperatorId(workOrder.getAcceptorId());
		woor.setOperationName(WorkOrderOperationType.ACCEPTOR.getName());
		woor.setOperationType(WorkOrderOperationType.ACCEPTOR.getValue());
		woor.setOperationResult(result.getValue());
		woor.setDescription(description);
		woor.setWorkOrderId(workOrder.getId());
		int rows = this.woorMapper.insertSelective(woor);
		if (rows <= 0) {
			throw new WorkOrderException("插入操作记录失败");
		}
		// 更新工单状态
		if (result.equals(OperationResult.FAILURE)) {
			workOrder.setWorkOrderState(WorkOrderState.APPLING.getValue());
		} else {
			workOrder.setWorkOrderState(WorkOrderState.SOLVING.getValue());
		}
		// 更新执行人
		workOrder.setExecutorId(executorId);
		rows = this.getActualDao().updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("更新工单状态失败");
		}
	}

	@Override
	public void solve(Long id, Integer taskHours, Integer overtimeHours, String workContent) throws WorkOrderException {
		// 检查工单是否存在
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		if (workOrder == null) {
			throw new WorkOrderException("工单不存在");
		}
		// 检查状态
		if (!workOrder.getWorkOrderState().equals(WorkOrderState.SOLVING.getValue())) {
			throw new WorkOrderException("当前状态不能执行分配操作");
		}
		// 生成操作记录
		WorkOrderOperationRecord woor = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		woor.setOperatorId(workOrder.getAcceptorId());
		woor.setOperationName(WorkOrderOperationType.EXECUTOR.getName());
		woor.setOperationType(WorkOrderOperationType.EXECUTOR.getValue());
		woor.setOperationResult(OperationResult.SUCCESS.getValue());
		woor.setWorkOrderId(workOrder.getId());
		int rows = this.woorMapper.insertSelective(woor);
		if (rows <= 0) {
			throw new WorkOrderException("插入操作记录失败");
		}
		// 更新工单状态
		workOrder.setWorkOrderState(WorkOrderState.SOLVED.getValue());
		rows = this.getActualDao().updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("更新工单状态失败");
		}
	}

	@Override
	public void close(Long id) throws WorkOrderException {
		// 检查工单是否存在
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		if (workOrder == null) {
			throw new WorkOrderException("工单不存在");
		}
		// 检查状态
		if (!workOrder.getWorkOrderState().equals(WorkOrderState.SOLVED.getValue())) {
			throw new WorkOrderException("当前状态不能执行分配操作");
		}
		// 生成操作记录
		WorkOrderOperationRecord woor = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		woor.setOperatorId(workOrder.getAcceptorId());
		woor.setOperationName(WorkOrderOperationType.CONFIRM.getName());
		woor.setOperationType(WorkOrderOperationType.CONFIRM.getValue());
		woor.setOperationResult(OperationResult.SUCCESS.getValue());
		woor.setWorkOrderId(workOrder.getId());
		int rows = this.woorMapper.insertSelective(woor);
		if (rows <= 0) {
			throw new WorkOrderException("插入操作记录失败");
		}
		// 更新工单状态
		workOrder.setWorkOrderState(WorkOrderState.CLOSED.getValue());
		rows = this.getActualDao().updateByPrimaryKeySelective(workOrder);
		if (rows <= 0) {
			throw new WorkOrderException("更新工单状态失败");
		}
	}

	@Override
	public Map<Object, Object> getViewModel(Long id) {
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		try {
			return parseViewModel(workOrder);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	public static Map<Object, Object> parseViewModel(WorkOrder workOrder) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject workOrderTypeProvider = new JSONObject();
		workOrderTypeProvider.put("provider", "workOrderTypeProvider");
		metadata.put("workOrderType", workOrderTypeProvider);

		JSONObject workOrderPriorityProvider = new JSONObject();
		workOrderPriorityProvider.put("provider", "workOrderPriorityProvider");
		metadata.put("priority", workOrderPriorityProvider);

		JSONObject workOrderSourceProvider = new JSONObject();
		workOrderSourceProvider.put("provider", "workOrderSourceProvider");
		metadata.put("workOrderSource", workOrderSourceProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("acceptorId", memberProvider);
		metadata.put("applicantId", memberProvider);

		JSONObject filesProvider = new JSONObject();
		filesProvider.put("provider", "filesProvider");
		metadata.put("attachmentFileId", filesProvider);

		JSONObject workOrderStateProvider = new JSONObject();
		workOrderStateProvider.put("provider", "workOrderStateProvider");
		metadata.put("workOrderState", workOrderStateProvider);

		List<Map> listMap = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(workOrder));
		return listMap.get(0);

	}

	@Override
	public WorkOrder getOperatinoRecordsViewModel(Long id) {
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		List<Long> copyUserIds = JSON.parseArray(workOrder.getCopyUserId(), Long.class);
		StringBuilder sb = new StringBuilder();
		copyUserIds.forEach(cid -> sb.append(this.memberProvider.getDisplayText(cid, null, null)).append(","));
		workOrder.aset("copyUsers", sb.substring(0, sb.length() - 1));
		WorkOrderOperationRecord woorQuery = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		woorQuery.setWorkOrderId(id);
		List<WorkOrderOperationRecord> opRecords = this.woorMapper.select(woorQuery);
		try {
			workOrder.aset("opRecords", parseOperationRecordViewModel(opRecords));
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return workOrder;
	}

	private List<Map> parseOperationRecordViewModel(List<WorkOrderOperationRecord> opRecords) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("operatorId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("operationTime", datetimeProvider);

		return ValueProviderUtils.buildDataByProvider(metadata, opRecords);
	}
}