package com.dili.alm.service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

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
	String  selectWeeklyQuestion(WeeklyPara weeklyPara);
	/**   
	 * 当前重要风险
	 * */
	String  selectWeeklyRist(WeeklyPara weeklyPara);
	/** 
	 * 本周进展情况 
	 *  */
	List<TaskDto>  selectWeeklyProgress(WeeklyPara weeklyPara );
	/**
	 *  下周工作计划
	 * */
	List<NextWeeklyDto>   selectNextWeeklyProgress(WeeklyPara weeklyPara);
	
	/** 
	 * 下周项目阶段
	 *   * */
	List<String> selectNextProjectPhase(WeeklyPara weeklyPara);
	
	/** 
	 * 本周项目阶段
	 *   * */
	List<String> selectProjectPhase(WeeklyPara weeklyPara);
	/** 
	 * 本周项目版本
	 * */
	List<String> selectProjectVersion(WeeklyPara weeklyPara );
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
	
	/**
	 * 文件下载
	 * @param file
	 * @param id
	 * @return
	 */
	File downLoad(File file,String id);
	/**
	 * 根据id返回页面详情
	 * @param id
	 * @return
	 */
	Map<Object,Object> getDescById(String id);
	
	/**
	 * 根据周报中个的项目经理id 查询项目经理
	 * @return
	 */
	List<WeeklyPara>  getUser();
	
	
}