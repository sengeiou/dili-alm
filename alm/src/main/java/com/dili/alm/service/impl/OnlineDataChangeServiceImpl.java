package com.dili.alm.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.component.BpmcUtil;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.OnlineDataChangeMapper;
import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.TaskDto;
import com.dili.alm.domain.TaskMapping;
import com.dili.alm.domain.dto.OnlineDataChangeBpmcDtoDto;
import com.dili.alm.rpc.MyTasksRpc;
import com.dili.alm.rpc.RuntimeApiRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.OnlineDataChangeService;
import com.dili.alm.service.ProjectService;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.BeanConver;
import com.dili.uap.sdk.domain.User;

/**
 * MyBatis Generator
 * This file was generated on 2019-12-25 18:22:44.
 */
@Service
public class OnlineDataChangeServiceImpl extends BaseServiceImpl<OnlineDataChange, Long> implements OnlineDataChangeService {

	 @Autowired
	   	private   MyTasksRpc  tasksRpc;
	    
	    @Autowired
	  	private   RuntimeApiRpc  runtimeRpc;
	    @Autowired
		private BpmcUtil bpmcUtil;
	    
	    @Autowired
		private UserRpc userRpc;
	    
    public OnlineDataChangeMapper getActualDao() {
        return (OnlineDataChangeMapper)getDao();
    }
    @Autowired
	private ProjectService   projectService;
	@Override
	public void insertOnLineData(OnlineDataChange onlineDataChange, Long id) {
		onlineDataChange.setApplyUserId(id);
    	onlineDataChange.setIsSubmit((byte)1);
    	onlineDataChange.setDataStatus((byte)2);
        this.insertSelective(onlineDataChange);
        try {
    	   Map<String, Object> map=new HashMap<String, Object>();
    	   map.put("dataId", onlineDataChange.getId()+"");
    	   
   	 	   Project  pro=	projectService.get(onlineDataChange.getProjectId());
   	 	   map.put("dept", pro.getProjectManager()+"");
   	     //  map.put("dept","1");
		   BaseOutput<ProcessInstanceMapping>  object= runtimeRpc.startProcessInstanceByKey("almOnlineDataChangeProcess", onlineDataChange.getId().toString(), id+"",map);
	       System.out.println(object.getCode()+object.getData()+object.getErrorData());
	       onlineDataChange.setProcessInstanceId(object.getData().getProcessInstanceId()); 
	       onlineDataChange.setId(onlineDataChange.getId());
	       
	       this.update(onlineDataChange);
        } catch (Exception e) {
		   e.printStackTrace();
		   System.out.println(e);
	    }
	}
	@Override
	public void updateOnlineDate(OnlineDataChange onlineDataChange, Long id) {
		OnlineDataChange  odc=	this.get(onlineDataChange.getId());
        if(odc.getProcessInstanceId()==null) {
        	
		   	 try {
		    	   Map<String, Object> map=new HashMap<String, Object>();
		    	   map.put("dataId", onlineDataChange.getId()+"");
		    	   Project  pro=	projectService.get(onlineDataChange.getProjectId());
		   	 	   map.put("dept", pro.getProjectManager()+"");
		   	 	   
				   BaseOutput<ProcessInstanceMapping>  object= runtimeRpc.startProcessInstanceByKey("almOnlineDataChangeProcess", onlineDataChange.getId().toString(), id+"",map);
			       System.out.println(object.getCode()+object.getData()+object.getErrorData());
			     
			       OnlineDataChange onlineData=new OnlineDataChange();
			       onlineData.setProcessInstanceId(object.getData().getProcessInstanceId());
			       onlineData.setId(onlineDataChange.getId());
			       onlineData.setDataStatus((byte)2);
			       onlineData.setIsSubmit((byte)1);
			       this.updateSelective(onlineData);
		        } catch (Exception e) {
				   e.printStackTrace();
				   System.out.println(e);
			    }
        } else {
        	ArrayList list=new ArrayList<String>();
        	list.add(odc.getProcessInstanceId());
        	TaskDto tdo=DTOUtils.newDTO(TaskDto.class);
        	tdo.setProcessInstanceIds(list);
        	BaseOutput<List<TaskMapping>>  task=tasksRpc.listTaskMapping(tdo);
        	String  taskId=task.getData().get(0).getId();
    		onlineDataChange.setDataStatus((byte)2);
    		onlineDataChange.setIsSubmit((byte)1);
    		this.updateSelective(onlineDataChange);
    		Map<String, Object> map=new HashMap<>();
        	map.put("approved", "true");
        	tasksRpc.complete(taskId);
    	   
       }
		
	}
	@Override
	public void agreeDeptOnlineDataChange(String taskId) {
		BaseOutput<Map<String, Object>>  mapId=tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange=new  OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte)3);
		this.updateSelective(onlineDataChange);
		OnlineDataChange onlineDataChangeTemp=this.get(Long.parseLong(dataId));
		Map<String, Object> map=new HashMap<>();
    	map.put("approved", "true");
	    Project  pro=	projectService.get(onlineDataChangeTemp.getProjectId());
	    
	    User uprojectser = DTOUtils.newDTO(User.class);
		uprojectser.setId(pro.getTestManager());
		uprojectser.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<User>> listUserByExample = userRpc.listByExample(uprojectser);
	
		Assignment record = DTOUtils.newDTO(Assignment.class);
		record.setAssignee(pro.getTestManager().toString());//项目测试人员的ID
		List<User> listUsernName = listUserByExample.getData();////项目测试人员组的ID
		if (listUsernName != null && listUsernName.size() > 0) {
		    record.setAssignee(listUsernName.get(0).getId().toString());
		    map.put("test",listUsernName.get(0).getId().toString()+"");
		}
   	 	   
    	tasksRpc.complete(taskId, map);
	}
	@Override
	public void notAgreeDeptOnlineDataChange(String taskId) {
		BaseOutput<Map<String, Object>>  mapId=tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange=new  OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte)1);
		this.updateSelective(onlineDataChange);
		OnlineDataChange	onlineDataChangeTemp=this.get(Long.parseLong(dataId));
		Map<String, Object> map=new HashMap<>();
    	map.put("approved", "false");
     	map.put("submit", ""+onlineDataChangeTemp.getApplyUserId());
    	tasksRpc.complete(taskId, map);
	}
	@Override
	public void agreeTestOnlineDataChange(String taskId) {
		BaseOutput<Map<String, Object>>  mapId=tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange=new  OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte)4);
		this.updateSelective(onlineDataChange);
    	Map<String, Object> map=new HashMap<>();
    	map.put("approved", "true");
    	tasksRpc.complete(taskId, map);
		
	}
	@Override
	public void notAgreeTestOnlineDataChange(String taskId) {
		BaseOutput<Map<String, Object>>  mapId=tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange=new  OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte)1);
		this.updateSelective(onlineDataChange);
		Map<String, Object> map=new HashMap<>();
    	map.put("approved", "false");
    	
    	OnlineDataChange	onlineDataChangeTemp=this.get(Long.parseLong(dataId));
    	map.put("submit", ""+onlineDataChangeTemp.getApplyUserId());
    	tasksRpc.complete(taskId, map);
	}
	@Override
	public void agreeDBAOnlineDataChange(String taskId) {
		BaseOutput<Map<String, Object>>  mapId=tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange=new  OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte)5);
		this.updateSelective(onlineDataChange);
		Map<String, Object> map=new HashMap<>();
    	map.put("approved", "true");
    	tasksRpc.complete(taskId, map);
	}
	@Override
	public void agreeOnlineDataChange(String taskId) {
		BaseOutput<Map<String, Object>>  mapId=tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange=new  OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte)6);
		this.updateSelective(onlineDataChange);
		Map<String, Object> map=new HashMap<>();
    	map.put("approved", "true");
  	    map.put("submit", onlineDataChange.getApplyUserId());
  	    
    	tasksRpc.complete(taskId, map);
		
	}
	@Override
	public void indexOnlineDataChange(String taskId) {
		BaseOutput<Map<String, Object>>  mapId=tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange=new  OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte)2);
		this.updateSelective(onlineDataChange);
		Map<String, Object> map=new HashMap<>();
    	map.put("approved", "true");
    	tasksRpc.complete(taskId);
		
	}
	@Override
	public String listPageOnlineData(OnlineDataChange onlineDataChange, String projectIdcc, Long id) {
		onlineDataChange.setApplyUserId(id);
   	    if(NumberUtils.isNumber(projectIdcc)) {
   	 	   onlineDataChange.setProjectId(Long.parseLong(projectIdcc));
	    }
   	 
    	List<OnlineDataChange> list = this.list(onlineDataChange);
    	 // Page<OnlineDataChange> page =  (Page<OnlineDataChange>) list;
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
			   List onlineDataChangeList = ValueProviderUtils.buildDataByProvider(metadata,targetList);
			  EasyuiPageOutput taskEasyuiPageOutput = new EasyuiPageOutput(Integer.valueOf(Integer.parseInt(String.valueOf(list.size()))), onlineDataChangeList);
			  return taskEasyuiPageOutput.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	
	
	
	
}