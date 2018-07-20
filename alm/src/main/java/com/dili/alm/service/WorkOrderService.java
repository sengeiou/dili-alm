package com.dili.alm.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.User;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.WorkOrderSource;
import com.dili.alm.domain.dto.WorkOrderUpdateDto;
import com.dili.alm.exceptions.WorkOrderException;
import com.dili.ss.base.BaseService;
import com.dili.ss.metadata.ValueProviderUtils;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-05-23 11:51:37.
 */
public interface WorkOrderService extends BaseService<WorkOrder, Long> {

	public static Map<Object, Object> parseViewModel(WorkOrder workOrder) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject workOrderTypeProvider = new JSONObject();
		workOrderTypeProvider.put("provider", "workOrderTypeProvider");
		metadata.put("workOrderType", workOrderTypeProvider);

		JSONObject workOrderPriorityProvider = new JSONObject();
		workOrderPriorityProvider.put("provider", "workOrderPriorityProvider");
		metadata.put("priority", workOrderPriorityProvider);

		JSONObject workOrderChannelProvider = new JSONObject();
		workOrderChannelProvider.put("provider", "workOrderChannelProvider");
		metadata.put("channel", workOrderChannelProvider);

		JSONObject workOrderSourceProvider = new JSONObject();
		workOrderSourceProvider.put("provider", "workOrderSourceProvider");
		metadata.put("workOrderSource", workOrderSourceProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("acceptorId", memberProvider);
		metadata.put("applicantId", memberProvider);
		metadata.put("executorId", memberProvider);

		JSONObject filesProvider = new JSONObject();
		filesProvider.put("provider", "filesProvider");
		metadata.put("attachmentFileId", filesProvider);

		JSONObject workOrderStateProvider = new JSONObject();
		workOrderStateProvider.put("provider", "workOrderStateProvider");
		metadata.put("workOrderState", workOrderStateProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("creationTime", datetimeProvider);
		
		metadata.put("startDate", "almDateProvider");
		metadata.put("endDate", "almDateProvider");

		List<Map> listMap = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(workOrder));
		return listMap.get(0);

	}

	void saveOrUpdate(WorkOrderUpdateDto dto) throws WorkOrderException;

	void saveAndSubmit(WorkOrderUpdateDto dto) throws WorkOrderException;

	/**
	 * 分配
	 * 
	 * @param id
	 *            工单id
	 * @param executorId
	 *            执行人id
	 * @param workType
	 *            TODO
	 * @param priority
	 *            TODO
	 * @param result
	 *            分配意见
	 * @param description
	 *            TODO
	 * @throws WorkOrderException
	 */
	void allocate(Long id, Long executorId, Integer workType, Integer priority, OperationResult result,
			String description) throws WorkOrderException;

	/**
	 * 解决工单
	 * 
	 * @param id
	 *            工单id
	 * @param startDate TODO
	 * @param endDate TODO
	 * @param taskHours
	 *            任务工时
	 * @param overtimeHours
	 *            加班工时
	 * @param workContent
	 *            工作内容描述
	 * @throws WorkOrderException
	 */
	void solve(Long id, Date startDate, Date endDate, Integer taskHours, Integer overtimeHours, String workContent) throws WorkOrderException;

	/**
	 * 关闭工单
	 * 
	 * @param id
	 *            工单id
	 * @param operatorId
	 *            TODO
	 * @param description TODO
	 * @throws WorkOrderException
	 */
	void close(Long id, Long operatorId, String description) throws WorkOrderException;

	Map<Object, Object> getViewModel(Long id);

	WorkOrder getOperationRecordsViewModel(Long id);

	List<User> getReceivers(WorkOrderSource type);

	void deleteWorkOrder(Long id) throws WorkOrderException;
}