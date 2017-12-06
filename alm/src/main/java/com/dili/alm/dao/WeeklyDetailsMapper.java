package com.dili.alm.dao;

import com.dili.alm.domain.WeeklyDetails;
import com.dili.ss.base.MyMapper;

public interface WeeklyDetailsMapper extends MyMapper<WeeklyDetails> {
	

    WeeklyDetails getWeeklyDetailsByWeeklyId(Long id);
	Integer createInsert(WeeklyDetails weeklyDetails);
}