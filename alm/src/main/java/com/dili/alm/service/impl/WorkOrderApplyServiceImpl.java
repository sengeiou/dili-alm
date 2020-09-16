package com.dili.alm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dili.alm.dao.WorkOrderMapper;
import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.service.WorkOrderApplyService;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.ss.dto.DTOUtils;
@Service
public class WorkOrderApplyServiceImpl implements WorkOrderApplyService {

	@Autowired
	private  WorkOrderMapper  workOrderMapper;
	
	
	/**
	 * 分配人
	 */
	@Override
	public  Assignment setAllocateName(Long workOrderId) {
		WorkOrder  workOrder=workOrderMapper.selectByPrimaryKey(workOrderId);
		Assignment record = DTOUtils.newInstance(Assignment.class);
		if(workOrder.getWorkOrderSource()==2) {
			record.setAssignee(workOrder.getExecutorId().toString());
		}else {
			record.setAssignee(workOrder.getAcceptorId().toString());
		}
		return record;
	}
/**
 * 解决人
 */
	@Override
	public Assignment setSolveName(Long workOrderId) {
		WorkOrder  workOrder=workOrderMapper.selectByPrimaryKey(workOrderId);
		Assignment record = DTOUtils.newInstance(Assignment.class);
		if(workOrder.getWorkOrderSource()==2) {
			record.setAssignee(workOrder.getExecutorId().toString());
		}else {
			record.setAssignee(workOrder.getAcceptorId().toString());
		}
		return record;
	}
	
	@Override
	public Assignment setCloseName(Long workOrderId) {
		WorkOrder  workOrder=workOrderMapper.selectByPrimaryKey(workOrderId);
		Assignment record = DTOUtils.newInstance(Assignment.class);
		record.setAssignee(workOrder.getAcceptorId().toString());
		return record;
	}
	/**
	 * 工单申请人设置
	 */
	@Override
	public Assignment setOrderApplyName(Long workOrderId) {
		WorkOrder  workOrder=workOrderMapper.selectByPrimaryKey(workOrderId);
		Assignment record = DTOUtils.newInstance(Assignment.class);
		record.setAssignee(workOrder.getApplicantId().toString());
		return record;
	}

	

}
