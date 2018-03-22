package com.dili.alm.service;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;

import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectYearCoverDto;
import com.dili.alm.domain.TaskByUsersDto;
import com.dili.alm.domain.TaskHoursByProjectDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectProgressDto;
import com.dili.alm.domain.dto.ProjectTypeCountDto;
import com.dili.alm.domain.dto.TaskStateCountDto;
import com.dili.alm.domain.dto.UploadProjectFileDto;
import com.dili.alm.exceptions.ProjectException;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;

public interface StatisticalService {

	EasyuiPageOutput getProjectTypeCountDTO(String startTime,
			String endTime);

	
	
	
	/***查询工时相关services****by******JING***BEGIN****/
	List<TaskByUsersDto> listTaskHoursByUser(String startTime,String endTime,Long userId,Long departmentId);
	
	List<TaskHoursByProjectDto> listProject(String startTime,String endTime);
	
	List<ProjectYearCoverDto> listProjectYearCover(String startTime,String endTime);
	/***查询工时相关services****by******JING***END****/

	List<TaskStateCountDto> getProjectToTaskCount(String startTime,
			String endTime);


	EasyuiPageOutput getProjectProgresstDTO(Project project,
			String startTime, String endTime, List<Long> ids);


	List<ProjectTypeCountDto> getProjectToTypeSummary(String startTime,
			String endTime);


	void downloadProjectType(OutputStream os, String startTime, String endTime) throws Exception;


}
