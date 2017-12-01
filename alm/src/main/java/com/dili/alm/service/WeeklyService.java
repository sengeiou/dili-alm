package com.dili.alm.service;

import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.EasyuiPageOutput;

/**
 * ��MyBatis Generator�����Զ�����
 * This file was generated on 2017-11-30 12:37:17.
 */
public interface WeeklyService extends BaseService<Weekly, Long> {
	

	/** 
	 * 自定义listpage 查询
	 */
	EasyuiPageOutput  getListPage(WeeklyPara weeklyPara);
}