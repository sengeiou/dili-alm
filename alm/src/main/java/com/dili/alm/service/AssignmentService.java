package com.dili.alm.service;

import java.util.List;

import com.dili.bpmc.sdk.dto.Assignment;

public interface AssignmentService {
	
	Assignment setSubmitOnlineDataChangeAssigneeName(Long onlineDataChangeId);
	
	Assignment setDeptOnlineDataChangeAssigneeName(Long onlineDataChangeId);
	
	Assignment  setTestOnlineDataChangeAssigneeName(Long onlineDataChangeId);
	
    Assignment   setDbaOnlineDataChangeAssigneeName(Long onlineDataChangeId);
	
	String    setTestOnlineDataChangeUserName(Long onlineDataChangeId);
	Assignment   setOnlineConfirm(Long onlineDataChangeId);
	/**
	 * 需求处理候选人
	 * @param onlineDataChangeId
	 * @return
	 */
	Assignment setReciprocate(String queryUserId);

}
