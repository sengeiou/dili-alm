package com.dili.alm.service;

import com.dili.bpmc.sdk.dto.Assignment;

public interface WorkOrderApplyService {
		
	Assignment setOrderApplyName(Long workOrderId);
	
	Assignment setAllocateName(Long workOrderId);
	
	Assignment setSolveName(Long workOrderId) ;
		
	Assignment setCloseName(Long workOrderId);
	

}
