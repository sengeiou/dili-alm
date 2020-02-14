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
	 * @param setReciprocate
	 * @return
	 */
	Assignment setReciprocate(String queryUserId);
	/**
	 * 返回给需求申请人
	 * @param setDemandAppId
	 * @return
	 */
	Assignment setDemandAppId(String busCode);
	/***LJ add begin***/
	/**
	 * 添加配置申请it资源 项目经理
	 * @param setOpdratpr
	 * @return
	 */
	Assignment setProjectManager(String applyId);

	/**
	 * 添加配置申请it资源 运维操作员
	 * @param setOpdratpr
	 * @return
	 */
	Assignment setOpdrator(String applyId);
	
	/**
	 * 添加配置申请it资源 返还编辑者
	 * @param setOpdratpr
	 * @return
	 */
	Assignment setHardwareResourceApplyApply(String applyId);
	/***LJ add end***/
}
