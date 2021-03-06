package com.dili.alm.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.beetl.core.GroupTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.NumberGenerator;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.BpmConsts;
import com.dili.alm.dao.DemandProjectMapper;
import com.dili.alm.dao.WorkOrderExecutionRecordMapper;
import com.dili.alm.dao.WorkOrderMapper;
import com.dili.alm.domain.DemandProject;
import com.dili.alm.domain.DemandProjectStatus;
import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.WorkOrderExecutionRecord;
import com.dili.alm.domain.WorkOrderSource;
import com.dili.alm.domain.WorkOrderState;
import com.dili.alm.domain.dto.WorkOrderUpdateDto;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.WorkOrderException;
import com.dili.alm.manager.WorkOrderManager;
import com.dili.alm.rpc.resolver.MyTaskRpc;
import com.dili.alm.service.WorkOrderService;
import com.dili.bpmc.sdk.rpc.restful.TaskRpc;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.User;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-05-23 11:51:37.
 */
@Transactional(rollbackFor = ApplicationException.class)
@Service
public class WorkOrderServiceImpl extends BaseServiceImpl<WorkOrder, Long> implements WorkOrderService {

	@Qualifier("mailContentTemplate")
	@Autowired
	private GroupTemplate groupTemplate;

	@Value("${spring.mail.username:}")
	private String mailFrom;

	@Qualifier("workOrderNumberGenerator")
	@Autowired
	private NumberGenerator numberGenerator;
	@Qualifier("workOrderManagerMap")
	@Autowired
	private Map<Integer, WorkOrderManager> workOrderManagerMap;
	@Autowired
	private WorkOrderExecutionRecordMapper woerMapper;
	@Autowired
	private DemandProjectMapper demandProjectMapper;
	
	@Autowired
	private   TaskRpc  tasksRpc;
	@Autowired
	private MyTaskRpc myTaskRpc;
	
	@Override
	public void allocate(Long id, Long executorId, Integer workOrderType, Integer priority, OperationResult result,
			String description) throws WorkOrderException {
		// 检查工单是否存在
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		if (workOrder == null) {
			throw new WorkOrderException("工单不存在");
		}
		WorkOrderManager workOrderManager = this.workOrderManagerMap.get(workOrder.getWorkOrderSource());
		workOrderManager.allocate(workOrder, executorId, workOrderType, priority, result, description);
	}

	@Override
	public void close(Long id, Long operatorId, OperationResult result, String description) throws WorkOrderException {
		// 检查工单是否存在
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		if (workOrder == null) {
			throw new WorkOrderException("工单不存在");
		}
		WorkOrderManager workOrderManager = this.workOrderManagerMap.get(workOrder.getWorkOrderSource());
		workOrderManager.close(workOrder, operatorId, result, description);
	}

	public void checkTimeToClose() throws WorkOrderException {
		WorkOrder query = DTOUtils.newDTO(WorkOrder.class);
		query.setWorkOrderState(WorkOrderState.SOLVED.getValue());
		List<WorkOrder> workOrders = this.getActualDao().select(query);
		if (CollectionUtils.isEmpty(workOrders)) {
			return;
		}
		long overTime = System.currentTimeMillis();
		for (WorkOrder workOrder : workOrders) {
			if (overTime - workOrder.getModifyTime().getTime() >= AlmConstants.CLOSE_OVER_TIME) {
				WorkOrderManager workOrderManager = this.workOrderManagerMap.get(workOrder.getWorkOrderSource());
				workOrderManager.systemClose(workOrder);
			}
		}
	}

	public WorkOrderMapper getActualDao() {
		return (WorkOrderMapper) getDao();
	}

	@Override
	public WorkOrder getOperationRecordsViewModel(Long id) {
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		WorkOrderManager workOrderManager = this.workOrderManagerMap.get(workOrder.getWorkOrderSource());
		workOrderManager.getOperationRecordsViewModel(workOrder);
		return workOrder;
	}

	@Override
	public Map<Object, Object> getViewModel(Long id) {
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		try {
			return WorkOrderService.parseViewModel(workOrder);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void saveAndSubmit(WorkOrderUpdateDto dto,String[] demandIds) throws WorkOrderException {
		dto.setCreationTime(new Date());
		this.saveOrUpdate(dto,demandIds);
		this.submit(dto.getId());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveOrUpdate(WorkOrderUpdateDto dto,String[] demandIds) throws WorkOrderException {
			if (dto.getId() == null) {
				this.insertWorkOrder(dto);
			} else {
				this.updateWorkOrder(dto);
				DemandProject demandProject=new DemandProject();
				demandProject.setWorkOrderId(dto.getId());
				demandProject.setStatus(DemandProjectStatus.ASSOCIATED.getValue());
				List<DemandProject> select = this.demandProjectMapper.select(demandProject);
				if(select!=null&&select.size()>0) {
					int delete = this.demandProjectMapper.delete(demandProject);
					if(delete!=select.size()) {
						throw new WorkOrderException("刪除关联失败");
					}
				}
			}
			if(demandIds!=null&&demandIds.length>0) {
				for (String demandId : demandIds) {
					DemandProject demandProject= new DemandProject();
					demandProject.setStatus(DemandProjectStatus.ASSOCIATED.getValue());
					demandProject.setWorkOrderId(dto.getId());
					demandProject.setDemandId(Long.valueOf(demandId));
					int insertExact = this.demandProjectMapper.insertExact(demandProject);
					if(insertExact==0) {
						throw new WorkOrderException("插入关联失败");
					}
				}
			}


	}

	@Override
	public void solve(Long id, Date startDate, Date endDate, Integer taskHours, Integer overtimeHours,
			String workContent) throws WorkOrderException {
		// 检查工单是否存在
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		if (workOrder == null) {
			throw new WorkOrderException("工单不存在");
		}
		WorkOrderManager workOrderManager = this.workOrderManagerMap.get(workOrder.getWorkOrderSource());
		workOrderManager.solve(workOrder, startDate, endDate, taskHours, overtimeHours, workContent);
	}

	private void insertWorkOrder(WorkOrderUpdateDto dto) throws WorkOrderException {
		if (CollectionUtils.isNotEmpty(dto.getCopyUserIds())) {
			dto.setCopyUserId(JSON.toJSONString(dto.getCopyUserIds()));
		}
		WorkOrderManager workOrderManager = this.workOrderManagerMap.get(dto.getWorkOrderSource());
		workOrderManager.add(dto);
	}

	private void submit(Long id) throws WorkOrderException {
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		WorkOrderManager workOrderManager = this.workOrderManagerMap.get(workOrder.getWorkOrderSource());
		workOrderManager.submit(workOrder);
	}
	private void submitAgain(Long id,String taskId) throws WorkOrderException {
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		WorkOrderManager workOrderManager = this.workOrderManagerMap.get(workOrder.getWorkOrderSource());
		workOrderManager.submitAgain(workOrder,taskId);
	}

	private void updateWorkOrder(WorkOrderUpdateDto dto) throws WorkOrderException {
		if (CollectionUtils.isNotEmpty(dto.getCopyUserIds())) {
			dto.setCopyUserId(JSON.toJSONString(dto.getCopyUserIds()));
		}
		WorkOrderManager workOrderManager = this.workOrderManagerMap.get(dto.getWorkOrderSource());
		workOrderManager.update(dto);
	}

	@Override
	public List<User> getReceivers(WorkOrderSource type) {
		WorkOrderManager workOrderManager = this.workOrderManagerMap.get(type.getValue());
		return workOrderManager.getReceivers();
	}

	@Override
	@Transactional
	public void deleteWorkOrder(Long id) throws WorkOrderException {
		WorkOrder workOrder = this.getActualDao().selectByPrimaryKey(id);
		if (!workOrder.getWorkOrderState().equals(WorkOrderState.APPLING.getValue())) {
			throw new WorkOrderException("当前状态不能删除");
		}
		DemandProject demandProject=new DemandProject();
		demandProject.setWorkOrderId(id);
		demandProject.setStatus(DemandProjectStatus.ASSOCIATED.getValue());
		List<DemandProject> select = this.demandProjectMapper.select(demandProject);
		if(select!=null&&select.size()>0) {
			int delete = this.demandProjectMapper.delete(demandProject);
			if(delete!=select.size()) {
				throw new WorkOrderException("刪除关联失败");
			}
		}
		this.getActualDao().deleteByPrimaryKey(id);
	}

	@Override
	public WorkOrder getDetailViewModel(Long id) {
		WorkOrder workOrder = this.getOperationRecordsViewModel(id);
		WorkOrderExecutionRecord woerQuery = DTOUtils.newDTO(WorkOrderExecutionRecord.class);
		woerQuery.setWorkOrderId(id);
		List<WorkOrderExecutionRecord> woerList = this.woerMapper.select(woerQuery);
		workOrder.aset("executionRecords", woerList);
		return workOrder;
	}

	@Override
	public void saveAndAgainSubmit(WorkOrderUpdateDto dto, String[] demandIds,String taskId,Boolean isNeedClaim) throws WorkOrderException {
		dto.setCreationTime(new Date());
		this.saveOrUpdate(dto,demandIds);
		this.submitAgain(dto.getId(),taskId);
	}

	@Override
	public Map<String, String> getDataDictionaryDto() {
		
		return  AlmCache.getInstance().getWorkOrderTypeMap() ;
	}

	@Override
	public void solveAgree(String taskId, Boolean isNeedClaim) throws WorkOrderException {
		BaseOutput<Map<String, Object>>  mapId=tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		Map<String, Object> map=new HashMap<String, Object>();
		WorkOrder workOrder = this.get(Long.parseLong(dataId));
		
		//map.put("close", workOrder.getApplicantId().toString());
	    map.put(BpmConsts.WorkOrderApply.CLOSE.getName(),  workOrder.getApplicantId().toString());
		
		if (isNeedClaim) {
			BaseOutput<String> output =tasksRpc.claim(taskId,  workOrder.getApplicantId().toString());
			if (!output.isSuccess()) {
				LOGGER.error(output.getMessage());
				throw new WorkOrderException("任务签收失败");
			}
		}
		try{
			 myTaskRpc.complete(taskId,map);
		} catch (Exception e) {
			//LOGGER.error(output.getMessage());
			throw new WorkOrderException("任务签收失败");
		}
		
		
	}

	@Override
	public void closeAgree(String taskId, Boolean isNeedClaim) throws WorkOrderException {
		try{
			 tasksRpc.complete(taskId);
		} catch (Exception e) {
			throw new WorkOrderException("任务签收失败");
		}
		
	}

	@Override
	public void allocateAgree(Long executorId, String taskId, Boolean isNeedClaim) throws WorkOrderException {
		Map<String, Object> map=new HashMap<String, Object>();
		 map.put("result", "1");
		// map.put("solve", executorId+"");
	     map.put(BpmConsts.WorkOrderApply.SOLVE.getName(), executorId+"");
		 if (isNeedClaim) {
				BaseOutput<String> output =tasksRpc.claim(taskId,  executorId+"");
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new WorkOrderException("任务签收失败");
				}
			}
		 
		 try{
		   myTaskRpc.complete(taskId,map);
		 } catch (Exception e) {
				throw new WorkOrderException("任务签收失败");
	    }
	}

	@Override
	public void allocateNotAgree(Long id, Long executorId, String taskId, Boolean isNeedClaim)
			throws WorkOrderException {
		Map<String, Object> map=new HashMap<String, Object>();
		 map.put("result", "0");
		WorkOrder workOrder = this.get(id);
		if (workOrder == null) {
			throw new WorkOrderException("工单不存在");
		}

		//map.put("edit", ""+workOrder.getApplicantId()+"");
		map.put(BpmConsts.WorkOrderApply.EDIT.getName(), ""+workOrder.getApplicantId()+"");
		  if (isNeedClaim) {
				BaseOutput<String> output =tasksRpc.claim(taskId,  executorId+"");
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new WorkOrderException("任务签收失败");
				}
			}
	    try{ 
		    myTaskRpc.complete(taskId,map);
		  } catch (Exception e) {
				throw new WorkOrderException("任务签收失败");
	    }
	}
}