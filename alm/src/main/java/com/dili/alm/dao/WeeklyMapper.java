package com.dili.alm.dao;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.dto.NextWeeklyDto;
import com.dili.alm.domain.dto.ProjectWeeklyDto;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.ss.base.MyMapper;
import com.dili.alm.domain.dto.TaskDto;

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
	
	/***
	 * 周报详情周报项目基本信息
	 * @param id
	 * @return
	 */
	ProjectWeeklyDto selectProjectWeeklyDto(Long ID );
	
	
	
	/** 
	 * 当前重大问题 -
	 * */
	String  selectWeeklyQuestion(String id);
	/**   
	 * 当前重要风险
	 * */
	String  selectWeeklyRist(String id);
	/** 
	 * 本周进展情况 
	 *  */
	List<TaskDto>  selectWeeklyProgress(Long id );
	/**
	 *  下周工作计划
	 * */
	List<NextWeeklyDto>  selectNextWeeklyProgress(Long id);
	
	/** 
	 * 下周项目阶段
	 *   * */
	List<String> selectNextProjectPhase(Long id);
	
	/** 
	 * 本周项目阶段
	 *   * */
	List<String> selectProjectPhase(Long id);
	/** 
	 * 本周项目版本
	 * */
	List<String> selectProjectVersion(Long id );
	
	
	/**
	 * 更新Rist or question
	 * @param weeklyPara
	 * @return
	 */
    Integer  updateRiskOrByQuestion(WeeklyPara weeklyPara);
    
    /**
     * 根据周报负责人 查询项目经理
     */
    List<WeeklyPara> getUser();
	
}
	
	
	