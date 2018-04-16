package com.dili.alm.service;

import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.ProjectTypeCountDto;
import com.dili.alm.domain.dto.ProjectYearCoverDto;
import com.dili.alm.domain.dto.ProjectYearCoverForAllDto;
import com.dili.alm.domain.dto.SelectTaskHoursByUserDto;
import com.dili.alm.domain.dto.SelectTaskHoursByUserProjectDto;
import com.dili.alm.domain.dto.TaskByUsersDto;
import com.dili.alm.domain.dto.TaskHoursByProjectDto;
import com.dili.alm.domain.dto.TaskStateCountDto;
import com.dili.ss.domain.EasyuiPageOutput;

public interface StatisticalService {

	EasyuiPageOutput getProjectTypeCountDTO(String startTime,
			String endTime);

	
	
	
	/***查询工时相关services****by******JING***BEGIN****/
	List<TaskByUsersDto> listTaskHoursByUser(String startTime,String endTime,List<Long> userId,List<Long> departmentId);
	
	List<TaskHoursByProjectDto> listProjectHours(String startTime,String endTime,List<Long> projectId);
	 
	List<SelectTaskHoursByUserDto> listUserHours(String startTime,String endTime, List<Long> projectIds) throws ParseException;
	
	List<ProjectYearCoverDto> listProjectYearCover(String startTime,String endTime);
	
	String getSearchDate(String year,String month);
	
	SelectTaskHoursByUserDto selectTotalTaskAndOverHours(List<Long> projectIds);
	
	List<SelectTaskHoursByUserProjectDto>  selectTotalTaskAndOverHoursForEchars(List<Long> projectIds);
	
	ProjectYearCoverForAllDto getProjectYearsCoverForAll(String year,String month);
	
	HSSFWorkbook downloadProjectHours(OutputStream os, String startTime,String endTime, List<Long> projectIds) throws Exception;
	/***查询工时相关services****by******JING***END****/

	List<TaskStateCountDto> getProjectToTaskCount(String startTime,
			String endTime);


	EasyuiPageOutput getProjectProgresstDTO(Project project,
			String startTime, String endTime, List<Long> ids);


	List<ProjectTypeCountDto> getProjectToTypeSummary(String startTime,
			String endTime);


	void downloadProjectType(OutputStream os, String startTime, String endTime) throws Exception;




	List<Map<String, Object>> getHomeProject(Long userId, List<Long> projectId);




	List<Map<String, Object>> getHomeProjectTask(Long userId,
			List<Long> projectId);

	
}
