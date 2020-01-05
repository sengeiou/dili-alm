package com.dili.alm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.domain.Project;
import com.dili.uap.sdk.domain.User;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.AssignmentService;
import com.dili.alm.service.OnlineDataChangeService;
import com.dili.alm.service.ProjectService;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
@Service
public class AssignmentServiceImpl implements AssignmentService {

	@Autowired
	private OnlineDataChangeService onlineDataChangeService;
	@Autowired
	private ProjectService   projectService;
	@Autowired
	private UserRpc userRpc;
	
	@Override
	public Assignment setSubmitOnlineDataChangeAssigneeName(Long onlineDataChangeId) {
		OnlineDataChange  odc=onlineDataChangeService.get(onlineDataChangeId);
		Assignment record = DTOUtils.newDTO(Assignment.class);
		record.setAssignee(odc.getApplyUserId().toString());
		return record;
	}

	@Override
	public Assignment setDeptOnlineDataChangeAssigneeName(Long onlineDataChangeId) {
       OnlineDataChange  odc=onlineDataChangeService.get(onlineDataChangeId);
        
		Project  pro=	projectService.get(odc.getProjectId());
		
		User uprojectser =DTOUtils.newDTO(User.class);
		uprojectser.setId(pro.getDevelopManager());
		BaseOutput<List<User>> listUserByExample = userRpc.listByExample(uprojectser);
		
		Assignment record = DTOUtils.newDTO(Assignment.class);
		
		List<User> listUsernName = listUserByExample.getData();
		if (listUsernName != null && listUsernName.size() > 0)
		    record.setAssignee(listUsernName.get(0).getId().toString());
		
		return record;
	}

	@Override
	public Assignment setTestOnlineDataChangeAssigneeName(Long onlineDataChangeId) {
        OnlineDataChange  odc=onlineDataChangeService.get(onlineDataChangeId);
		
		Project  pro=	projectService.get(odc.getProjectId());
		
		User uprojectser = DTOUtils.newDTO(User.class);
		uprojectser.setId(pro.getTestManager());
		uprojectser.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<User>> listUserByExample = userRpc.listByExample(uprojectser);
	
		Assignment record = DTOUtils.newDTO(Assignment.class);
		record.setAssignee(pro.getTestManager().toString());//项目测试人员的ID
		List<User> listUsernName = listUserByExample.getData();////项目测试人员组的ID
		if (listUsernName != null && listUsernName.size() > 0)
		    record.setAssignee(listUsernName.get(0).getId().toString());
		
		return record;
		
	}

	@Override
	public String setTestOnlineDataChangeUserName(Long onlineDataChangeId) {
		OnlineDataChange  odc=onlineDataChangeService.get(onlineDataChangeId);
		return odc.getApplyUserId().toString();
	}

      @Override
	public Assignment setDbaOnlineDataChangeAssigneeName(Long onlineDataChangeId) {
    	  OnlineDataChange  odc=onlineDataChangeService.get(onlineDataChangeId);
  		
  		Project  pro=	projectService.get(odc.getProjectId());
  		BaseOutput<List<User>> listUserByExample = userRpc.listUserByRoleId(pro.getTestManager());
  		
  		Assignment record = DTOUtils.newDTO(Assignment.class);
  		
  		List<User> listUsernName = listUserByExample.getData();
  		
  		List<String>  reList=new ArrayList<>();
  		if (listUsernName != null && listUsernName.size() > 0) {
  	       for (User user : listUsernName) {
  	    	   reList.add(user.getId().toString());
  		   }		
  		}	
  		record.setCandidateUser(reList); 
  		
  		return record;
	}

	@Override
	public Assignment setOnlineConfirm(Long onlineDataChangeId) {
		  OnlineDataChange  odc=onlineDataChangeService.get(onlineDataChangeId);
	  		
	  		Project  pro=	projectService.get(odc.getProjectId());
	  		
	  		BaseOutput<List<User>> listUserByExample = userRpc.listUserByRoleId(pro.getTestManager());
	  		
	  		Assignment record = DTOUtils.newDTO(Assignment.class);
	  		
	  		List<User> listUsernName = listUserByExample.getData();
	  		
	  		List<String>  reList=new ArrayList<>();
	  		if (listUsernName != null && listUsernName.size() > 0) {
	  	       for (User user : listUsernName) {
	  	    	   reList.add(user.getId().toString());
	  		   }		
	  		}	
	  		record.setCandidateUser(reList); 
	  		
	  		return record;
	}
	
	

}
