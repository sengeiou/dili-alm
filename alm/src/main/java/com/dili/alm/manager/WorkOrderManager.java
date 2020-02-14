package com.dili.alm.manager;

import java.util.Date;
import java.util.List;

import com.dili.alm.domain.OperationResult;
import com.dili.uap.sdk.domain.User;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.WorkOrderSource;
import com.dili.alm.exceptions.WorkOrderException;

public interface WorkOrderManager {

	WorkOrderSource getType();

	void add(WorkOrder workOrder) throws WorkOrderException;

	void update(WorkOrder workOrder) throws WorkOrderException;

	void submit(WorkOrder workOrder) throws WorkOrderException;

	void allocate(WorkOrder workOrder, Long executorId, Integer workOrderType, Integer priority, OperationResult result,
			String description) throws WorkOrderException;

	void close(WorkOrder workOrder, Long operatorId, OperationResult result, String description) throws WorkOrderException;

	void solve(WorkOrder workOrder, Date startDate, Date endDate, Integer taskHours, Integer overtimeHours,
			String workContent) throws WorkOrderException;

	WorkOrder getOperationRecordsViewModel(WorkOrder workOrder);

	List<User> getReceivers();

	void systemClose(WorkOrder workOrder) throws WorkOrderException;
	
	void submitAgain(WorkOrder workOrder,String taskId) throws WorkOrderException;

}
