package com.dili.alm.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.component.BpmcUtil;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.BpmConsts;
import com.dili.alm.dao.OnlineDataChangeMapper;
import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.domain.OnlineDataChangeLog;
import com.dili.alm.domain.Project;
/*import com.dili.alm.domain.TaskDto;*/
/*import com.dili.alm.domain.TaskMapping;*/
import com.dili.alm.domain.dto.OnlineDataChangeBpmcDtoDto;
import com.dili.alm.exceptions.OnlineDataChangeException;
import com.dili.alm.rpc.RoleRpc;
import com.dili.alm.rpc.RuntimeApiRpc;
import com.dili.alm.service.OnlineDataChangeLogService;
import com.dili.alm.service.OnlineDataChangeService;
import com.dili.alm.service.ProjectService;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.BeanConver;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;

/**
 * MyBatis Generator This file was generated on 2019-12-25 18:22:44.
 */
//@Transactional(rollbackFor = Exception.class)
@Transactional(rollbackFor = OnlineDataChangeException.class)
@Service
public class OnlineDataChangeServiceImpl extends BaseServiceImpl<OnlineDataChange, Long> implements OnlineDataChangeService {

	@Autowired
	private TaskRpc tasksRpc;

	@Autowired
	private RuntimeApiRpc runtimeRpc;
	@Autowired
	private BpmcUtil bpmcUtil;

	@Autowired
	private UserRpc userRpc;

	@Autowired
	private RoleRpc roleRpc;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private OnlineDataChangeMapper onlineDataChangeMapper;
	
	
	@Autowired
	private   OnlineDataChangeLogService    onlineDataChangeLogService;

	public OnlineDataChangeMapper getActualDao() {
		return (OnlineDataChangeMapper) getDao();
	}

	@Override
	public void insertOnLineData(OnlineDataChange onlineDataChange, Long id) throws OnlineDataChangeException {
		onlineDataChange.setApplyUserId(id);
		onlineDataChange.setIsSubmit((byte) 1);
		onlineDataChange.setDataStatus((byte) 2);
		onlineDataChange.setCreateDate(new Date());
		this.insertSelective(onlineDataChange);

		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("dataId", onlineDataChange.getId() + "");

			Project pro = projectService.get(onlineDataChange.getProjectId());
		//	map.put("dept", pro.getProjectManager() + "");
			map.put(BpmConsts.OnlineDataChangeProcessConstant.dept.getName(), pro.getProjectManager() + "");
			// map.put("dept","1");
			
			BaseOutput<ProcessInstanceMapping> object = runtimeRpc.startProcessInstanceByKey(BpmConsts.ONLINEDATACHANGE_PROCESS, onlineDataChange.getId().toString(), id + "", map);
			if (object.getCode().equals("5000")) {
				throw new OnlineDataChangeException("runtimeRpc失败");
			}

			onlineDataChange.setProcessInstanceId(object.getData().getProcessInstanceId());
			onlineDataChange.setProcessDefinitionId(object.getData().getProcessDefinitionId());
			onlineDataChange.setId(onlineDataChange.getId());

			update(onlineDataChange);
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println(e);
			  throw new OnlineDataChangeException("失败");
		}
	}

	@Override
	public void updateOnlineDate(OnlineDataChange onlineDataChange, Long id) throws OnlineDataChangeException {
		OnlineDataChange odc = this.get(onlineDataChange.getId());
		if (odc.getProcessInstanceId() == null) {

			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("dataId", onlineDataChange.getId() + "");
				Project pro = projectService.get(onlineDataChange.getProjectId());
				//map.put("dept", pro.getProjectManager() + "");
				map.put(BpmConsts.OnlineDataChangeProcessConstant.dept.getName(), pro.getProjectManager() + "");
				BaseOutput<ProcessInstanceMapping> object = runtimeRpc.startProcessInstanceByKey(BpmConsts.ONLINEDATACHANGE_PROCESS, onlineDataChange.getId().toString(), id + "", map);
				// System.out.println(object.getCode()+object.getData()+object.getErrorData());
				if (object.getCode().equals("5000")) {
					throw new OnlineDataChangeException("失败");
				}

				OnlineDataChange onlineData = new OnlineDataChange();
				onlineData.setProcessInstanceId(object.getData().getProcessInstanceId());
				onlineData.setProcessDefinitionId(object.getData().getProcessDefinitionId());
				onlineData.setId(onlineDataChange.getId());
				onlineData.setDataStatus((byte) 2);
				onlineData.setIsSubmit((byte) 1);
				this.updateSelective(onlineData);
			} catch (Exception e) {
				e.printStackTrace();
				throw new OnlineDataChangeException("失败");
				//System.out.println(e);
			}
		} else {
			try {
				ArrayList list = new ArrayList<String>();
				list.add(odc.getProcessInstanceId());
				TaskDto tdo = DTOUtils.newDTO(TaskDto.class);
				tdo.setProcessInstanceIds(list);
				BaseOutput<List<TaskMapping>> task = tasksRpc.listTaskMapping(tdo);
				String taskId = task.getData().get(0).getId();
				onlineDataChange.setDataStatus((byte) 2);
				onlineDataChange.setIsSubmit((byte) 1);
				this.updateSelective(onlineDataChange);
				Map<String, Object> map = new HashMap<>();
				map.put("approved", "true");
			
				tasksRpc.complete(taskId);
			} catch (Exception e) {

				throw new OnlineDataChangeException("失败");
			}

		}

	}

	@Override
	public void agreeDeptOnlineDataChange(String taskId, Boolean isNeedClaim) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 3);
		this.updateSelective(onlineDataChange);
		OnlineDataChange onlineDataChangeTemp = this.get(Long.parseLong(dataId));
		Map<String, Object> map = new HashMap<>();
		map.put("approved", "true");
		Project pro = projectService.get(onlineDataChangeTemp.getProjectId());

		User uprojectser = DTOUtils.newDTO(User.class);
		uprojectser.setId(pro.getTestManager());
		uprojectser.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<User>> listUserByExample = userRpc.listByExample(uprojectser);

		Assignment record = DTOUtils.newDTO(Assignment.class);
		record.setAssignee(pro.getTestManager().toString());// 项目测试人员的ID
		List<User> listUsernName = listUserByExample.getData();//// 项目测试人员组的ID
		if (listUsernName != null && listUsernName.size() > 0) {
			record.setAssignee(listUsernName.get(0).getId().toString());
			//map.put("test", listUsernName.get(0).getId().toString() + "");
		    map.put(BpmConsts.OnlineDataChangeProcessConstant.test.getName(), listUsernName.get(0).getId().toString() + "");
		}
	
		try {
			if (isNeedClaim) {
				BaseOutput<String> output = tasksRpc.claim(taskId, listUsernName.get(0).getId().toString() + "");
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new OnlineDataChangeException("任务签收失败");
				}
			}
			tasksRpc.complete(taskId, map);
			//AlmConstants
			
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGEMANAGER.getCode(), 1);
		//	insertDataExeLog(dataId);
			
		} catch (Exception e) {
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGEMANAGER.getCode(), 0);
			LOGGER.error("任务签收失败");
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	
	@Override
	public void notAgreeDeptOnlineDataChange(String taskId, Boolean isNeedClaim) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 1);
		this.updateSelective(onlineDataChange);
		OnlineDataChange onlineDataChangeTemp = this.get(Long.parseLong(dataId));
		Map<String, Object> map = new HashMap<>();
		map.put("approved", "false");
	//	map.put("submit", "" + onlineDataChangeTemp.getApplyUserId());
		map.put(BpmConsts.OnlineDataChangeProcessConstant.submit.getName(), "" + onlineDataChangeTemp.getApplyUserId());
		
		
		try {
			if (isNeedClaim) {
				BaseOutput<String> output = tasksRpc.claim(taskId, onlineDataChangeTemp.getApplyUserId() + "");
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new OnlineDataChangeException("任务签收失败");
				}
			}

			tasksRpc.complete(taskId, map);
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGEMANAGER.getCode(), 2);
		} catch (Exception e) {
			LOGGER.error("任务签收失败");
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGEMANAGER.getCode(), 0);
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	@Override
	public void agreeTestOnlineDataChange(String taskId, Boolean isNeedClaim) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 4);
		this.updateSelective(onlineDataChange);

		Map<String, Object> map = new HashMap<>();
		map.put("approved", "true");
		try {
			tasksRpc.complete(taskId, map);
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGETEST.getCode(), 1);
		} catch (Exception e) {
			LOGGER.error("任务签收失败");
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGETEST.getCode(), 0);
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	@Override
	public void notAgreeTestOnlineDataChange(String taskId, Boolean isNeedClaim) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 1);
		onlineDataChangeMapper.updateByPrimaryKeySelective(onlineDataChange);
		Map<String, Object> map = new HashMap<>();
		map.put("approved", "false");

		OnlineDataChange onlineDataChangeTemp = this.get(Long.parseLong(dataId));
	//	map.put("submit", "" + onlineDataChangeTemp.getApplyUserId());
		map.put(BpmConsts.OnlineDataChangeProcessConstant.submit.getName(), "" + onlineDataChangeTemp.getApplyUserId());
		
		
		try {
			if (isNeedClaim) {
				BaseOutput<String> output = tasksRpc.claim(taskId, onlineDataChangeTemp.getApplyUserId() + "");
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new OnlineDataChangeException("任务签收失败");
				}
			}
			tasksRpc.complete(taskId, map);
			
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGETEST.getCode(), 2);
		} catch (Exception e) {
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGETEST.getCode(), 0);
			LOGGER.error("任务签收失败");
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	@Override
	public void agreeDBAOnlineDataChange(String taskId, Boolean isNeedClaim) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 5);
		this.updateSelective(onlineDataChange);
		Map<String, Object> map = new HashMap<>();
		map.put("approved", "true");
		Long id = SessionContext.getSessionContext().getUserTicket().getId();
		// 签收任务
		
		try {
			if (isNeedClaim) {
				BaseOutput<String> output = tasksRpc.claim(taskId, id.toString());
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new OnlineDataChangeException("任务签收失败");
				}
			}
			tasksRpc.complete(taskId, map);
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGEDBA.getCode(), 1);
		} catch (Exception e) {
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGEDBA.getCode(), 0);
			LOGGER.error("任务签收失败");
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	@Override
	public void agreeOnlineDataChange(String taskId, Boolean isNeedClaim) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 6);
		this.updateSelective(onlineDataChange);
		Map<String, Object> map = new HashMap<>();
		map.put("approved", "true");
		//map.put("submit", onlineDataChange.getApplyUserId());
		map.put(BpmConsts.OnlineDataChangeProcessConstant.submit.getName(), onlineDataChange.getApplyUserId());
		Long id = SessionContext.getSessionContext().getUserTicket().getId();
		
		try {
			if (isNeedClaim) {
				BaseOutput<String> output = tasksRpc.claim(taskId, id.toString() + "");
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new OnlineDataChangeException("任务签收失败");
				}
			}
			
			tasksRpc.complete(taskId);
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.ONLINEDBADATACHANGE.getCode(), 1);
		} catch (Exception e) {
			LOGGER.error("任务签收失败");
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.ONLINEDBADATACHANGE.getCode(), 0);
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	@Override
	public void indexOnlineDataChange(String taskId, OnlineDataChange onlineDataChange) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		// OnlineDataChange onlineDataChange=new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 2);
		this.updateSelective(onlineDataChange);
		Map<String, Object> map = new HashMap<>();
		map.put("approved", "true");
		try {
			tasksRpc.complete(taskId);
			
		} catch (Exception e) {
			LOGGER.error("任务签收失败");
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	@Override
	public String listPageOnlineData(OnlineDataChange onlineDataChange, String projectIdcc, Long id) {
		onlineDataChange.setSort("create_date");
		onlineDataChange.setOrder("desc");
		// onlineDataChange.setApplyUserId(id);
		if (NumberUtils.isNumber(projectIdcc)) {
			onlineDataChange.setProjectId(Long.parseLong(projectIdcc));
		}

		List<OnlineDataChange> list = onlineDataChangeMapper.selectList(onlineDataChange);
		// Page<OnlineDataChange> page = (Page<OnlineDataChange>) list;
		Map<Object, Object> metadata = null == onlineDataChange.getMetadata() ? new HashMap<>() : onlineDataChange.getMetadata();
		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);
		JSONObject projectVersionProvider = new JSONObject();
		projectVersionProvider.put("provider", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("applyUserId", memberProvider);

		JSONObject dateProvider = new JSONObject();
		dateProvider.put("provider", "dateProvider");
		metadata.put("applyDate", dateProvider);
		metadata.put("updateDate", dateProvider);

		try {
			List<OnlineDataChangeBpmcDtoDto> targetList = BeanConver.copyList(list, OnlineDataChangeBpmcDtoDto.class);

			bpmcUtil.fitLoggedUserIsCanHandledProcess(targetList);

			/*
			 * Set<Long> dbaRoleIds = new HashSet<>(); dbaRoleIds.add(Long.parseLong("75"));
			 * 
			 * Set<Long> onlingRoleIds = new HashSet<>();
			 * onlingRoleIds.add(Long.parseLong("44"));
			 */
			Project pro;
			/*
			 * List<Long> dbaList; List<Long> onLingList;
			 */
			for (OnlineDataChangeBpmcDtoDto odcData : targetList) {
				pro = null;
				pro = projectService.get(odcData.getProjectId());
				if (pro != null) {
					odcData.setTestManager(pro.getTestManager());// "test负责人
					odcData.setProjectManager(pro.getProjectManager());// "项目负责人
				}

				/*
				 * dbaList=new ArrayList<Long>(); List<RoleUserDto>
				 * dbaDto=roleRpc.listRoleUserByRoleIds(dbaRoleIds).getData(); for (RoleUserDto
				 * roleUserDto : dbaDto) { List<User> lsitUser=roleUserDto.getUsers(); for (User
				 * object : lsitUser) { dbaList.add(object.getId()); }
				 * 
				 * }
				 */
				/*
				 * List<RoleUserDto>
				 * onlingRoleIdDto=roleRpc.listRoleUserByRoleIds(onlingRoleIds).getData();
				 * 
				 * onLingList=new ArrayList<Long>(); for (RoleUserDto roleUserDto :
				 * onlingRoleIdDto) {
				 * 
				 * List<User> lsitUser=roleUserDto.getUsers(); for (User object : lsitUser) {
				 * onLingList.add(object.getId()); } } odcData.setDbaManager(dbaList);
				 * odcData.setOnlineManager(onLingList);
				 */
			}

			List onlineDataChangeList = ValueProviderUtils.buildDataByProvider(metadata, targetList);
			EasyuiPageOutput taskEasyuiPageOutput = new EasyuiPageOutput(Integer.valueOf(Integer.parseInt(String.valueOf(list.size()))), onlineDataChangeList);
			return taskEasyuiPageOutput.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	 * @Override public void notAgreeDBAOnlineDataChange(String taskId, Boolean
	 * isNeedClaim) throws OnlineDataChangeException { // TODO Auto-generated method
	 * stub BaseOutput<Map<String, Object>> mapId=tasksRpc.getVariables(taskId);
	 * String dataId = (String) mapId.getData().get("businessKey"); OnlineDataChange
	 * onlineDataChange=new OnlineDataChange();
	 * onlineDataChange.setId(Long.parseLong(dataId));
	 * onlineDataChange.setDataStatus((byte)1);
	 * onlineDataChangeMapper.updateByPrimaryKeySelective(onlineDataChange);
	 * Map<String, Object> map=new HashMap<>(); map.put("approved", "false");
	 * 
	 * OnlineDataChange onlineDataChangeTemp=this.get(Long.parseLong(dataId));
	 * 
	 * Project pro=projectService.get(onlineDataChangeTemp.getProjectId());
	 * map.put("test", ""+pro.getTestManager()); if (isNeedClaim) {
	 * BaseOutput<String> output
	 * =tasksRpc.claim(taskId,onlineDataChangeTemp.getApplyUserId()+""); if
	 * (!output.isSuccess()) { LOGGER.error(output.getMessage()); throw new
	 * OnlineDataChangeException("任务签收失败"); } } tasksRpc.complete(taskId, map);
	 * 
	 * }
	 */

	@Override
	public void notAgreeOnlineDataChange(String taskId, Boolean isNeedClaim) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 4);
		onlineDataChangeMapper.updateByPrimaryKeySelective(onlineDataChange);
		Map<String, Object> map = new HashMap<>();
		map.put("approved", "false");

		OnlineDataChange onlineDataChangeTemp = this.get(Long.parseLong(dataId));
		// map.put("submit", ""+onlineDataChangeTemp.getApplyUserId());
		
		try {
			if (isNeedClaim) {
				Long id = SessionContext.getSessionContext().getUserTicket().getId();
				BaseOutput<String> output = tasksRpc.claim(taskId, id.toString());
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new OnlineDataChangeException("任务签收失败");
				}
			}
			tasksRpc.complete(taskId, map);
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.ONLINEDBADATACHANGE.getCode(), 2);
		} catch (Exception e) {
			LOGGER.error("任务签收失败");
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.ONLINEDBADATACHANGE.getCode(), 0);
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

}