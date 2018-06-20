package com.dili.alm.service;

import java.util.List;
import java.util.Map;

import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.User;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.dto.WorkOrderUpdateDto;
import com.dili.alm.exceptions.WorkOrderException;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-05-23 11:51:37.
 */
public interface WorkOrderService extends BaseService<WorkOrder, Long> {

	void saveOrUpdate(WorkOrderUpdateDto dto) throws WorkOrderException;

	void saveAndSubmit(WorkOrderUpdateDto dto) throws WorkOrderException;

	/**
	 * 分配
	 * 
	 * @param id
	 *            工单id
	 * @param executorId
	 *            执行人id
	 * @param workType TODO
	 * @param priority TODO
	 * @param result
	 *            分配意见
	 * @param description
	 *            TODO
	 * @throws WorkOrderException
	 */
	void allocate(Long id, Long executorId, Integer workType, Integer priority, OperationResult result, String description) throws WorkOrderException;

	/**
	 * 解决工单
	 * 
	 * @param id
	 *            工单id
	 * @param taskHours
	 *            任务工时
	 * @param overtimeHours
	 *            加班工时
	 * @param workContent
	 *            工作内容描述
	 * @throws WorkOrderException
	 */
	void solve(Long id, Integer taskHours, Integer overtimeHours, String workContent) throws WorkOrderException;

	/**
	 * 关闭工单
	 * 
	 * @param id
	 *            工单id
	 * @param operatorId TODO
	 * @throws WorkOrderException
	 */
	void close(Long id, Long operatorId) throws WorkOrderException;

	Map<Object, Object> getViewModel(Long id);

	WorkOrder getOperatinoRecordsViewModel(Long id);

	List<User> getReceivers();
}