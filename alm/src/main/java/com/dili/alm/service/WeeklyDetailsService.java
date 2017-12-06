package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.WeeklyDetails;
import com.dili.alm.domain.dto.NextWeeklyDto;
import com.dili.alm.domain.dto.ProjectWeeklyDto;
import com.dili.alm.domain.dto.TaskDto;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.EasyuiPageOutput;

/**
 * ��MyBatis Generator�����Զ�����
 * This file was generated on 2017-11-30 12:37:17.
 */
public interface WeeklyDetailsService extends BaseService<WeeklyDetails, Long> {
	

	
	WeeklyDetails  getWeeklyDetailsByWeeklyId(Long id);
	
	Integer  createInsert(WeeklyDetails weeklyDetails);
	
}