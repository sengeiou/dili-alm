package com.dili.alm.dao;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.ss.base.MyMapper;

public interface WeeklyMapper extends MyMapper<Weekly> {
	
	/**
	 * 周报搜索条件查询
	 * @param weeklyPara
	 * @return
	 */
	CopyOnWriteArrayList<WeeklyPara> selectListPageByWeeklyPara(WeeklyPara weeklyPara);
	/**
	 * 周报搜索条件查询条数
	 * @param weeklyPara
	 * @return
	 */
	Integer selectPageByWeeklyParaCount(WeeklyPara weeklyPara);
}