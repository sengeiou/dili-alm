package com.dili.alm.dao;

import java.util.List;

import com.dili.alm.domain.WorkOrder;
import com.dili.ss.base.MyMapper;

public interface WorkOrderMapper extends MyMapper<WorkOrder> {
	
	List<WorkOrder>  selectWorkOrdeByCopyUserId(Long id );
}