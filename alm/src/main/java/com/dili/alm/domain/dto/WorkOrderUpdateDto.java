package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.alm.domain.WorkOrder;

public interface WorkOrderUpdateDto extends WorkOrder {

	List<Long> getCopyUserIds();

	void setCopyUserIds(List<Long> copyUserIds);

}
