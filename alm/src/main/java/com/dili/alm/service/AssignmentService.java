package com.dili.alm.service;

import com.dili.bpmc.sdk.dto.Assignment;

public interface AssignmentService {
	
	Assignment setSubmitOnlineDataChangeAssigneeName(Long onlineDataChangeId);
	
	Assignment setDeptOnlineDataChangeAssigneeName(Long onlineDataChangeId);
	
	Assignment  setTestOnlineDataChangeAssigneeName(Long onlineDataChangeId);
	
/*	Assignment   setDbaOnlineDataChangeAssigneeName(Long onlineDataChangeId);*/
	

}
