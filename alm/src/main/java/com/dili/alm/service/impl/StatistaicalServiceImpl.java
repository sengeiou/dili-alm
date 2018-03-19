package com.dili.alm.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.MailManager;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.dao.TeamMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Log;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectEntity;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskByUsersDto;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.TeamRole;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectDto;
import com.dili.alm.domain.dto.ProjectStatusCountDto;
import com.dili.alm.domain.dto.ProjectTypeCountDTO;
import com.dili.alm.domain.dto.UploadProjectFileDto;
import com.dili.alm.exceptions.ProjectException;
import com.dili.alm.provider.ProjectProvider;
import com.dili.alm.rpc.DataAuthRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.StatisticalService;
import com.dili.alm.service.TeamService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.SystemConfigUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.github.pagehelper.Page;

@Service
public class StatistaicalServiceImpl implements StatisticalService {
	
	@Autowired 
	ProjectMapper projectMapper;
	@Autowired 
	TaskMapper taskMapper;
	
	@Autowired
	DataDictionaryService dataDictionaryService;
	
	private static final String PROJECT_TYPE_CODE = "project_type";
	
	private static final String PROJECT_STATE_CODE = "project_state";
	
	@Override
	public EasyuiPageOutput getProjectTypeCountDTO(String startTime,String endTime) {	
		List<ProjectTypeCountDTO> list=new ArrayList<ProjectTypeCountDTO>();
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_TYPE_CODE);
		if (dto != null) {
			List<DataDictionaryValueDto> ddvdList = dto.getValues();
			for (DataDictionaryValueDto dataDictionaryValueDto : ddvdList) {
				ProjectTypeCountDTO ptc=new ProjectTypeCountDTO();
				ptc.setType(dataDictionaryValueDto.getCode());
				List<ProjectStatusCountDto> statusCount = projectMapper.getTpyeByProjectCount(dataDictionaryValueDto.getValue(), startTime, endTime);
				int total=0;
				for (ProjectStatusCountDto projectStatusCountDto : statusCount) {
					total=total+projectStatusCountDto.getStateCount();
					switch (projectStatusCountDto.getProjectState()) {
						case 0:
							ptc.setNotStartCount(projectStatusCountDto.getStateCount());
							break;
						case 1:
							ptc.setOngoingConut(projectStatusCountDto.getStateCount());
							break;
						case 2:
							ptc.setCompleteCount(projectStatusCountDto.getStateCount());
							break;
						case 3:
							ptc.setSuspendedCount(projectStatusCountDto.getStateCount());
							break;
						case 4:
							ptc.setShutCount(projectStatusCountDto.getStateCount());
							break;
					}
				}
				ptc.setTypeCount(total);
				list.add(ptc);
			}
			return new EasyuiPageOutput(ddvdList.size(), list);
		}
		return null;

	}
	/***查询工时相关services****by******JING***BEGIN****/
	
	@Override
	public List<TaskByUsersDto> listTaskHoursByUser(String startTime,
			String endTime, Long userId, Long departmentId) {
		return taskMapper.selectTaskHourByUser(startTime, endTime, departmentId, userId);
	}
	/***查询工时相关services****by******JING***END****/

}