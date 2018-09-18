package com.dili.alm.service;

import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.ProjectTypeCountDto;
import com.dili.alm.domain.dto.ProjectYearCoverDto;
import com.dili.alm.domain.dto.ProjectYearCoverForAllDto;
import com.dili.alm.domain.dto.SelectTaskHoursByUserDto;
import com.dili.alm.domain.dto.SelectTaskHoursByUserProjectDto;
import com.dili.alm.domain.dto.SelectYearsDto;
import com.dili.alm.domain.dto.TaskByUsersDto;
import com.dili.alm.domain.dto.TaskHoursByProjectDto;
import com.dili.alm.domain.dto.TaskStateCountDto;
import com.dili.ss.domain.EasyuiPageOutput;

public interface StatisticalService {

	EasyuiPageOutput getProjectTypeCountDTO(String startTime, String endTime);

	/*** 查询工时相关services****by******JING***BEGIN ****/
	/**
	 * 员工工时查询
	 * 
	 * @param startTime
	 * @param endTime
	 * @param userId
	 * @param departmentId
	 * @param order
	 *            TODO
	 * @param sort
	 *            TODO
	 * @return
	 */
	List<TaskByUsersDto> listTaskHoursByUser(String startTime, String endTime, List<Long> userId,
			List<Long> departmentId, String order, String sort);

	/**
	 * 项目工时-查询项目以及总工时，既项目工时表头
	 * 
	 * @param startTime
	 * @param endTime
	 * @param projectId
	 * @return
	 */
	List<TaskHoursByProjectDto> listProjectHours(String startTime, String endTime, List<Long> projectId);

	/**
	 * 项目工时-查询每个用户的填写总工时
	 * 
	 * @param startTime
	 * @param endTime
	 * @param projectIds
	 * @return
	 * @throws ParseException
	 */
	List<SelectTaskHoursByUserDto> listUserHours(String startTime, String endTime, List<Long> projectIds)
			throws ParseException;

	/**
	 * 年度报表-项目数据展示
	 * 
	 * @param 年份
	 * @param 月份
	 * @return
	 */
	List<ProjectYearCoverDto> listProjectYearCover(String year, String month, String week);

	/**
	 * 年度报表-查询时间
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	String getSearchDate(String year, String month, String weekNum);

	/**
	 * 项目工时-查询总计
	 * 
	 * @param projectIds
	 * @return
	 */
	SelectTaskHoursByUserDto selectTotalTaskAndOverHours(List<Long> projectIds, Date startDate, Date endDate);

	/**
	 * 项目工时查询 ，echar数据显示
	 * 
	 * @param projectIds
	 * @param startDate
	 *            TODO
	 * @param endDate
	 *            TODO
	 * @return
	 */
	List<SelectTaskHoursByUserProjectDto> selectTotalTaskAndOverHoursForEchars(List<Long> projectIds, Date startDate,
			Date endDate);

	/**
	 * 年度报表，echar显示数据
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	ProjectYearCoverForAllDto getProjectYearsCoverForAll(String year, String month, boolean isFullYear, String weekNum);

	/**
	 * 项目工时-导出excel方法
	 * 
	 * @param os
	 * @param startTime
	 * @param endTime
	 * @param projectIds
	 * @return
	 * @throws Exception
	 */
	HSSFWorkbook downloadProjectHours(OutputStream os, String startTime, String endTime, List<Long> projectIds)
			throws Exception;

	/**
	 * 年度报表-查询系统中的工作年份，用于年度表查询条件
	 * 
	 * @return
	 */
	List<SelectYearsDto> SelectYears();

	/*** 查询工时相关services****by******JING***END ****/

	List<TaskStateCountDto> getProjectToTaskCount(String startTime, String endTime);

	EasyuiPageOutput getProjectProgresstDTO(Project project, List<Integer> stateIds, Integer f);

	List<ProjectTypeCountDto> getProjectToTypeSummary(String startTime, String endTime);

	void downloadProjectType(OutputStream os, String startTime, String endTime) throws Exception;

	List<Map<String, Object>> getHomeProject(Long userId, List<Long> projectId);

	/***
	 * 项目工时-查询员工工时
	 * 
	 * @param userId
	 * @param projectId
	 * @return
	 */
	List<Map<String, Object>> getHomeProjectTask(Long userId, List<Long> projectId);

	Long getUserTotalTaskHour(Long userId);

}
