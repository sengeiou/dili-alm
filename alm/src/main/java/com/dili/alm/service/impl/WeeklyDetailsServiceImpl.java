package com.dili.alm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dili.alm.dao.WeeklyDetailsMapper;
import com.dili.alm.dao.WeeklyMapper;
import com.dili.alm.domain.WeeklyDetails;
import com.dili.alm.service.WeeklyDetailsService;
import com.dili.ss.base.BaseServiceImpl;

/**
 * ��MyBatis Generator�����Զ����� This file was generated on 2017-11-29
 * 14:08:40.
 */
@Service
public class WeeklyDetailsServiceImpl extends BaseServiceImpl<WeeklyDetails, Long> implements  WeeklyDetailsService {

	@Autowired
	WeeklyDetailsMapper weeklyDetailsMapper;
	@Autowired
	WeeklyMapper weeklyMapper;
	@Override
	public WeeklyDetails getWeeklyDetailsByWeeklyId(Long id) {
		
		return weeklyDetailsMapper.getWeeklyDetailsByWeeklyId(id);
	}

	@Override
	public Integer createInsert(WeeklyDetails weeklyDetails) {
		
		return weeklyDetailsMapper.createInsert(weeklyDetails);
	}
	
	

	

}