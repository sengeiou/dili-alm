package com.dili.alm.service;

import java.util.List;

import com.dili.bpmc.sdk.dto.Assignment;

/**
 * 
 * <B>Description</B>
 * 本软件源代码版权归农丰时代及其团队所有,未经许可不得任意复制与传播
 * <B>农丰时代科技有限公司</B>
 *
 * @Description TODO(用一句话描述该文件做什么)
 * @author yangfan
 * @date 2021年3月5日
 */
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
	 * 需求处理反馈候选人
	 * @param setFeedback
	 * @return
	 */
	Assignment setFeedback(String queryUserId);
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
