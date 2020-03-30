package com.dili.alm.service;

import com.dili.alm.domain.OnlineDataChangeLog;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-20 17:22:08.
 */
public interface OnlineDataChangeLogService extends BaseService<OnlineDataChangeLog, Long> {
	
	void insertDataExeLog(String dataId,String operationName,int opertateResult ) ;

}