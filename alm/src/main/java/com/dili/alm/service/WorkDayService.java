package com.dili.alm.service;

import java.text.ParseException;

import com.dili.alm.domain.WorkDay;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

public interface WorkDayService extends BaseService<WorkDay, Long> {
	 /**
	  * 判断并默认下周工作时间
	 * @throws ParseException 
	  */
	void updateNextWeeklyWorkDays();

}
