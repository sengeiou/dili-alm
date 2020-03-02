package com.dili.alm.service;

import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.exceptions.OnlineDataChangeException;
import com.dili.alm.exceptions.ProjectOnlineApplyException;
import com.dili.ss.base.BaseService;

/**
 *由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-25 18:22:44.
 */
public interface OnlineDataChangeService extends BaseService<OnlineDataChange, Long> {
	
	 void insertOnLineData(OnlineDataChange onlineDataChange, Long id) ;
	 
	 void	 updateOnlineDate(OnlineDataChange onlineDataChange, Long id) ;
	 
	 void agreeDeptOnlineDataChange(String taskId,Boolean isNeedClaim) throws OnlineDataChangeException;
	 
	 void notAgreeDeptOnlineDataChange(String taskId,Boolean isNeedClaim) throws OnlineDataChangeException;
	 
	 void agreeTestOnlineDataChange(String taskId,Boolean isNeedClaim) throws OnlineDataChangeException;
	 
	 void notAgreeTestOnlineDataChange(String taskId,Boolean isNeedClaim) throws OnlineDataChangeException;
	 
	 void agreeDBAOnlineDataChange(String taskId,Boolean isNeedClaim) throws OnlineDataChangeException;
	 
	 void agreeOnlineDataChange(String taskId,Boolean isNeedClaim) throws OnlineDataChangeException;
	 
	 void indexOnlineDataChange(String taskId,OnlineDataChange onlineDataChange) ;
	 String listPageOnlineData(OnlineDataChange onlineDataChange, String projectIdcc, Long id) ;
	 
	 
}