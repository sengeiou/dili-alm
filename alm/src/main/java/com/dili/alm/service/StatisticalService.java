package com.dili.alm.service;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;

import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.TaskByUsersDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectTypeCountDTO;
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
	
	
	
	
	/***查询工时相关services****by******JING***END****/
}