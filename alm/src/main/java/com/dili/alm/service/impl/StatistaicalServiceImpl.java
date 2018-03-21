package com.dili.alm.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectYearCoverDto;
import com.dili.alm.domain.TaskByUsersDto;
import com.dili.alm.domain.TaskHoursByProjectDto;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectProgressDto;
import com.dili.alm.domain.dto.ProjectStatusCountDto;
import com.dili.alm.domain.dto.ProjectTypeCountDTO;
import com.dili.alm.domain.dto.TaskStateCountDto;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.StatisticalService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.metadata.ValueProviderUtils;

@Service
public class StatistaicalServiceImpl implements StatisticalService {
	
	@Autowired 
	ProjectMapper projectMapper;

	@Autowired 
	TaskMapper taskMapper;
	
	@Autowired
	DataDictionaryService dataDictionaryService;
	
	private static final String PROJECT_TYPE_CODE = "project_type";
	
	private static final String SELECT_HOURS_BY_USER_START_DATE = "2018-01-01";//项目上线日期
	
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
	
	@Override
	public List<TaskStateCountDto> getProjectToTaskCount(String startTime,
			String endTime) {
		List<Long> projectIds = projectMapper.getProjectIds(startTime, endTime);
		return taskMapper.selectTaskStateCount(projectIds);
	}


	/***查询工时相关services****by******JING***BEGIN****/

	@Override
	public List<TaskByUsersDto> listTaskHoursByUser(String startTime,
			String endTime, List<Long> dids, List<Long> uids) {
		if (startTime==null) {
			startTime = SELECT_HOURS_BY_USER_START_DATE;
		}else{
			startTime+="";
		}
		if (endTime==null) {//没有默认为至今
			endTime = DateUtil.getDate(new Date())+"";
		}else{
			endTime+="";
		}

		List<TaskByUsersDto> list = taskMapper.selectTaskHourByUser(startTime, endTime, null, null);
		return list;
	}
	
	
	@Override
	public List<TaskHoursByProjectDto> listProjectHours(String startTime,
			String endTime) {
		return taskMapper.selectProjectHours(startTime, endTime);
	}
	@Override
	public List<ProjectYearCoverDto> listProjectYearCover(String year,
			String month) {

		return taskMapper.selectProjectYearsCover(getAppedDate(year,month,true), getAppedDate(year,month,false));
	}
	
	
	private static String getAppedDate(String year,String month,Boolean isBegin){
		Calendar now = Calendar.getInstance();
		String retrunDate ="";
		
		if (year!=null) {
			retrunDate+=year;
		}else{
			retrunDate+=now.get(Calendar.YEAR);
		}
		
		if (month==null) {
			month=now.get(Calendar.MONTH)+"";
		}
		if (isBegin) {
			switch (month) {
			case "1":
				retrunDate+="-01-01";
				break;
			case "2":
				retrunDate+="-02-01";
				break;
			case "3":
				retrunDate+="-03-01";
				break;
			case "4":
				retrunDate+="-04-01";
				break;
			case "5":
				retrunDate+="-05-01";
				break;
			case "6":
				retrunDate+="-06-01";
				break;
			case "7":
				retrunDate+="-07-01";
				break;
			case "8":
				retrunDate+="-08-01";
				break;
			case "9":
				retrunDate+="-09-01";
				break;
			case "10":
				retrunDate+="-10-01";
				break;
			case "11":
				retrunDate+="-11-01";
				break;
			case "12":
				retrunDate+="-12-01";
				break;
			case "13":
				retrunDate+="-01-01";
				break;
			case "14":
				retrunDate+="-04-01";
				break;
			case "15":
				retrunDate+="-07-01";
				break;
			case "16":
				retrunDate+="-10-01";
				break;
			default:
				retrunDate = SELECT_HOURS_BY_USER_START_DATE;
				break;
			}
		}else{
			switch (month) {
			case "1":
				retrunDate  +="-01-31";
				break;
			case "2":
				retrunDate  +="-02-28";
				break;
			case "3":
				retrunDate  +="-03-31";
				break;
			case "4":
				retrunDate  +="-04-30";
				break;
			case "5":
				retrunDate  +="-05-31";
				break;
			case "6":
				retrunDate  +="-06-30";
				break;
			case "7":
				retrunDate  +="-07-31";
				break;
			case "8":
				retrunDate  +="-08-31";
				break;
			case "9":
				retrunDate  +="-09-30";
				break;
			case "10":
				retrunDate  +="-10-31";
				break;
			case "11":
				retrunDate  +="-11-30";
				break;
			case "12":
				retrunDate  +="-12-31";
				break;
			case "13":
				retrunDate  +="-03-31";
				break;
			case "14":
				retrunDate  +="-06-30";
				break;
			case "15":
				retrunDate  +="-09-30";
				break;
			case "16":
				retrunDate  +="-12-31";
				break;
			default:
				retrunDate  = DateUtil.getDate(new Date());
				break;
			}
		}
		return retrunDate;
	}
	
	@Override
	public String getSearchDate(String year, String month) {
		String begin = getAppedDate(year,month,true);
		String end =getAppedDate(year,month,false);
		String returnStr = begin+"至"+end;
		return returnStr;
	}
	/***查询工时相关services****by******JING***END****/
	@Override
	public EasyuiPageOutput getProjectProgresstDTO(Project project,String startTime,String endTime,List<Long> ids) {	
		List<ProjectProgressDto> projectProgressList = projectMapper.getProjectProgressList(project,startTime, endTime, ids);
		int projectProgressListCount = projectMapper.getProjectProgressListCount(startTime, endTime, ids);
		for (ProjectProgressDto projectProgressDto : projectProgressList) {
			switch (projectProgressDto.getProjectState()) {
			case 0:
				projectProgressDto.setDateProgress(0);
				break;
			case 1:
				projectProgressDto.setDateProgress(getDateProgress(projectProgressDto.getStartDate(), projectProgressDto.getEndDate()));
				break;
			case 2:
				projectProgressDto.setDateProgress(null);
				break;
			case 3:
				projectProgressDto.setDateProgress(getDateProgress(projectProgressDto.getStartDate(), projectProgressDto.getEndDate()));
				break;
			case 4:
				projectProgressDto.setCompletedProgress(null);
				projectProgressDto.setDateProgress(null);
				break;
			}
		}
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = null == project.getMetadata() ? new HashMap<>() : project.getMetadata();
		
		JSONObject projectStateProvider = new JSONObject();
		projectStateProvider.put("provider", "projectStateProvider");
		metadata.put("projectState", projectStateProvider);

		JSONObject projectTypeProvider = new JSONObject();
		projectTypeProvider.put("provider", "projectTypeProvider");
		metadata.put("type", projectTypeProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("projectManager", memberProvider);

		JSONObject provider = new JSONObject();
		provider.put("provider", "datetimeProvider");
		metadata.put("estimateLaunchDate", provider);

		project.setMetadata(metadata);
		try {
			List list = ValueProviderUtils.buildDataByProvider(project, projectProgressList);
			return new EasyuiPageOutput(projectProgressListCount, list);
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 
	 */
	public static Integer getDateProgress(Date startTime,Date endTime){
		int toNowDays = DateUtil.differentDaysByMillisecond(startTime, new Date())+1;
		int planDays = DateUtil.differentDaysByMillisecond(startTime, endTime)+1;
		int dateProgress = ((int)(((double)toNowDays/(double)planDays)*100));
		return dateProgress;
	}

	@Override
	public List<ProjectTypeCountDTO> getProjectToTypeSummary(String startTime,
			String endTime) {
		List<ProjectTypeCountDTO> list=new ArrayList<ProjectTypeCountDTO>();
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_TYPE_CODE);
		int projectTotal = projectMapper.getProjectTypeAllCount(startTime, endTime);
		if (dto != null) {
			List<DataDictionaryValueDto> ddvdList = dto.getValues();
			for (DataDictionaryValueDto dataDictionaryValueDto : ddvdList) {
				ProjectTypeCountDTO ptc=new ProjectTypeCountDTO();
				ptc.setType(dataDictionaryValueDto.getCode());
				List<ProjectStatusCountDto> statusCount = projectMapper.getTpyeByProjectCount(dataDictionaryValueDto.getValue(), startTime, endTime);
				int typeTotal=0;
				for (ProjectStatusCountDto projectStatusCountDto : statusCount) {
					typeTotal=typeTotal+projectStatusCountDto.getStateCount();
				}
				ptc.setTypeCount(typeTotal);
				if(projectTotal!=0){
					
					ptc.setProjectTypeProgress((int)(((double)typeTotal/(double)projectTotal)*100));
				}
				list.add(ptc);
			}
			return list;
		}
		return null;
	}



}
