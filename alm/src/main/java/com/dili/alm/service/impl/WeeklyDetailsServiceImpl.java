package com.dili.alm.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.WeeklyDetailsMapper;
import com.dili.alm.dao.WeeklyMapper;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.WeeklyDetails;
import com.dili.alm.domain.dto.NextWeeklyDto;
import com.dili.alm.domain.dto.ProjectWeeklyDto;
import com.dili.alm.domain.dto.TaskDto;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.WeeklyDetailsService;
import com.dili.alm.service.WeeklyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;

/**
 * ��MyBatis Generator�����Զ����� This file was generated on 2017-11-29
 * 14:08:40.
 */
@Service
public class WeeklyDetailsServiceImpl extends BaseServiceImpl<WeeklyDetails, Long> implements  WeeklyDetailsService {

	@Autowired
	WeeklyDetailsMapper weeklyDetailsMapper;

	@Override
	public WeeklyDetails getWeeklyDetailsByWeeklyId(Long id) {
		
		return weeklyDetailsMapper.getWeeklyDetailsByWeeklyId(id);
	}
	
	

	

}