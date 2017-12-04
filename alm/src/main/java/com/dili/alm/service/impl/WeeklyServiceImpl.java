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
import com.dili.alm.dao.WeeklyMapper;
import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.dto.ProjectWeeklyDto;
import com.dili.alm.domain.dto.TaskDto;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.alm.service.WeeklyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;

/**
 * ��MyBatis Generator�����Զ����� This file was generated on 2017-11-29
 * 14:08:40.
 */
@Service
public class WeeklyServiceImpl extends BaseServiceImpl<Weekly, Long> implements  WeeklyService {

	@Autowired
	WeeklyMapper weeklyMapper;

	public WeeklyMapper getActualDao() {
		return (WeeklyMapper) getDao();
	}

	@Override
	public EasyuiPageOutput getListPage(WeeklyPara weeklyPara) {

		// 查询总页数
		int total = weeklyMapper.selectPageByWeeklyParaCount(weeklyPara);
		// 查询list
		CopyOnWriteArrayList<WeeklyPara> list = weeklyMapper
				.selectListPageByWeeklyPara(weeklyPara);
		CopyOnWriteArrayList<WeeklyPara> copyList = new CopyOnWriteArrayList();
		if (list != null && list.size() > 0) {
			for (WeeklyPara weeklyPara2 : list) {

				weeklyPara2.setDate(weeklyPara2.getStartDate() + " 到 "
						+ weeklyPara2.getEndDate());
				if (weeklyPara2.getProjectType().equalsIgnoreCase("I")) {
					weeklyPara2.setProjectType("内部项目");
				} else if (weeklyPara2.getProjectType().equalsIgnoreCase("K")) {
					weeklyPara2.setProjectType("重点项目");
				} else if (weeklyPara2.getProjectType().equalsIgnoreCase("R")) {
					weeklyPara2.setProjectType("预约项目");
				} else if (weeklyPara2.getProjectType().equalsIgnoreCase("G")) {
					weeklyPara2.setProjectType("一般项目");
				}
				copyList.add(weeklyPara2);
			}
		}

		EasyuiPageOutput out = new EasyuiPageOutput(total, list);
		return out;
	}

	@Override
	public ProjectWeeklyDto getProjectWeeklyDtoById(Long projectId) {
		ProjectWeeklyDto pd = weeklyMapper.selectProjectWeeklyDto(projectId);
		//pd.setPlanDate(pd.getPlanDate().substring(0,10));
	//	pd.setBeginAndEndTime(getWeekFristDay()+"到"+getWeekFriday());
		return pd;
	}

	public static String getWeekFristDay() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
		return df.format(cal.getTime());
	}

	public static String getWeekFriday() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = new GregorianCalendar();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + 4);
		Date last = cal.getTime();
		return formater.format(last);
	}
	
	/** 
	 * 当前重大问题 -
	 * */
	@Override
	public String selectWeeklyQuestion(WeeklyPara weeklyPara) {
		return weeklyMapper.selectWeeklyQuestion(weeklyPara);
	}
	/**   
	 * 当前重要风险
	 * */
	@Override
	public String selectWeeklyRist(WeeklyPara weeklyPara) {
		return weeklyMapper.selectWeeklyRist(weeklyPara);
	}
	/** 
	 * 本周进展情况 
	 *  */
	@Override
	public TaskDto selectWeeklyProgress(Long id) {
		return weeklyMapper.selectWeeklyProgress(id);
	}
	/**
	 *  下周工作计划
	 * */
	@Override
	public Weekly selectNextWeeklyProgress(Long id) {
		return weeklyMapper.selectNextWeeklyProgress(id);
	}
	/** 
	 * 本周项目阶段
	 * */
	@Override
	public List<String> selectProjectPhase(Long id) {
		return weeklyMapper.selectProjectPhase(id);
	}
	/** 
	 * 本周项目版本
	 * */
	@Override
	public List<String> selectProjectVersion(Long id) {
		return weeklyMapper.selectProjectVersion(id);
	}

}