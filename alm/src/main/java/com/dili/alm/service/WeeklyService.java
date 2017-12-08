package com.dili.alm.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.WeeklyJson;
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
public interface WeeklyService extends BaseService<Weekly, Long> {
	

	/** 
	 * 自定义listpage 查询
	 */
	EasyuiPageOutput  getListPage(WeeklyPara weeklyPara);
	
	ProjectWeeklyDto  getProjectWeeklyDtoById(Long projectId);
	
	
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
	List<NextWeeklyDto>   selectNextWeeklyProgress(Long id);
	
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
	 * 保存当前重大问题 
	 * @param list
	 * @return
	 */
	Integer   updateMaxQuestion(String list,Long id );
	/**
	 * 
	 * @param list
	 * @return
	 */
	Integer   updateMaxRist(String list,Long id );
	
	File downLoad(File file,String id);
	
	
}