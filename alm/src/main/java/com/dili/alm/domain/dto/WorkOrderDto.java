package com.dili.alm.domain.dto;

import com.dili.alm.component.ProcessHandleInfoDto;
import com.dili.alm.domain.WorkOrder;

public interface WorkOrderDto  extends WorkOrder, ProcessHandleInfoDto {
	
	Integer getTaskHours();
	void setTaskHours(Integer taskHours);
	
	Long getOvertimeHours();
	void setOvertimeHours(Integer overtimeHours);
	
}
