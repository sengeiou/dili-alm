package com.dili.alm.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.dili.uap.sdk.domain.dto.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.Demand;
import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.domain.Project;
import com.dili.alm.service.AssignmentService;
import com.dili.alm.service.DemandService;
import com.dili.alm.service.HardwareResourceApplyService;
import com.dili.alm.service.OnlineDataChangeService;
import com.dili.alm.service.ProjectService;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.UserRpc;
@Service
public class AssignmentServiceImpl implements AssignmentService {
	/**LJ ADD**/
	@Autowired
	HardwareResourceApplyService hardwareResourceApplyService;
	@Autowired
	private OnlineDataChangeService onlineDataChangeService;
	@Autowired
	private ProjectService   projectService;
	@Autowired
	private DemandService demandService;
	@Autowired
	private UserRpc userRpc;
	
	@Override
	public Assignment setSubmitOnlineDataChangeAssigneeName(Long onlineDataChangeId) {
		OnlineDataChange  odc=onlineDataChangeService.get(onlineDataChangeId);
		Assignment record = DTOUtils.newInstance(Assignment.class);
		record.setAssignee(odc.getApplyUserId().toString());
		return record;
	}

	@Override
	public Assignment setDeptOnlineDataChangeAssigneeName(Long onlineDataChangeId) {
       OnlineDataChange  odc=onlineDataChangeService.get(onlineDataChangeId);
        
		Project  pro=	projectService.get(odc.getProjectId());
		
		UserQuery uprojectser =DTOUtils.newInstance(UserQuery.class);
		uprojectser.setId(pro.getProjectManager());
		BaseOutput<List<User>> listUserByExample = userRpc.listByExample(uprojectser);
		
		Assignment record = DTOUtils.newInstance(Assignment.class);
		
		List<User> listUsernName = listUserByExample.getData();
		if (listUsernName != null && listUsernName.size() > 0)
		    record.setAssignee(listUsernName.get(0).getId().toString());
		
		return record;
	}

	@Override
	public Assignment setTestOnlineDataChangeAssigneeName(Long onlineDataChangeId) {
        OnlineDataChange  odc=onlineDataChangeService.get(onlineDataChangeId);
		
		Project  pro=	projectService.get(odc.getProjectId());
		
		UserQuery uprojectser = DTOUtils.newInstance(UserQuery.class);
		uprojectser.setId(pro.getTestManager());
		uprojectser.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<User>> listUserByExample = userRpc.listByExample(uprojectser);
	
		Assignment record = DTOUtils.newInstance(Assignment.class);
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
  		
  		Assignment record = DTOUtils.newInstance(Assignment.class);
  		
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
	  		
	  		Assignment record = DTOUtils.newInstance(Assignment.class);
	  		
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
	public Assignment setReciprocate(String busCode){
		Assignment record = DTOUtils.newInstance(Assignment.class);
		Demand demand = demandService.getByCode(busCode);
		record.setAssignee(demand.getReciprocateId().toString()); 
		return record;
	}
	
	@Override
	public Assignment setDemandAppId(String busCode){
		Assignment record = DTOUtils.newInstance(Assignment.class);
		Demand demand = demandService.getByCode(busCode);
		record.setAssignee(demand.getUserId().toString()); 
		return record;
	}
	/**LJ ADD begin**/
	@Override
	public Assignment setOpdrator(String busCode) {
		Assignment record = DTOUtils.newInstance(Assignment.class);
		HardwareResourceApply apply = hardwareResourceApplyService.get(Long.parseLong(busCode));
		List<Long> ids = JSONArray.parseArray(apply.getExecutors(), Long.class);
		record.setAssignee(ids.get(0).toString()); 
		return record;
	}


	@Override
	public Assignment setProjectManager(String applyId) {
		Assignment record = DTOUtils.newInstance(Assignment.class);
		HardwareResourceApply apply = hardwareResourceApplyService.get(Long.parseLong(applyId));
		record.setAssignee(apply.getProjectManagerId().toString()); 
		return record;
	}
	/**LJ ADD end**/

	@Override
	public Assignment setHardwareResourceApplyApply(String applyId) {
		Assignment record = DTOUtils.newInstance(Assignment.class);
		HardwareResourceApply apply = hardwareResourceApplyService.get(Long.parseLong(applyId));
		record.setAssignee(apply.getApplicantId().toString()); 
		return record;
	}

	@Override
	public Assignment setFeedback(String queryUserId) {
		Assignment record = DTOUtils.newInstance(Assignment.class);
		Demand demand = demandService.getByCode(queryUserId);
		record.setAssignee(demand.getFeedbackId().toString()); 
		return record;
	}
}
