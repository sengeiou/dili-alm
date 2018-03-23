package com.dili.alm.service.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.word.WordExportUtil;

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
import com.dili.alm.domain.Team;
import com.dili.alm.domain.TeamRole;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectDto;
import com.dili.alm.domain.dto.ProjectProgressDto;
import com.dili.alm.domain.dto.ProjectStatusCountDto;
import com.dili.alm.domain.dto.ProjectTypeCountDto;
import com.dili.alm.domain.dto.ProjectYearCoverDto;
import com.dili.alm.domain.dto.ProjectYearCoverForAllDto;
import com.dili.alm.domain.dto.TaskByUsersDto;
import com.dili.alm.domain.dto.TaskHoursByProjectDto;
import com.dili.alm.domain.dto.TaskStateCountDto;
import com.dili.alm.domain.dto.UploadProjectFileDto;
import com.dili.alm.exceptions.ProjectException;
import com.dili.alm.provider.ProjectProvider;
import com.dili.alm.rpc.DataAuthRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.StatisticalService;
import com.dili.alm.service.TeamService;
import com.dili.alm.utils.DateUtil;
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
	private static final String SELECT_HOURS_BY_USER_START_DATE = "2018-01-01";//项目上线日期
	private static final String FILE_NAME_STR="项目总览.xls";
	private static final String PROJECT_TYPE_TITLE="项目列表";
	private static final String TASK_STATE_TITLE="任务列表";
	/**
	 * 项目总览查询
	 */
	@Override
	public EasyuiPageOutput getProjectTypeCountDTO(String startTime,String endTime) {	
		List<ProjectTypeCountDto> list=new ArrayList<ProjectTypeCountDto>();
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_TYPE_CODE);
		if (dto != null) {
			List<DataDictionaryValueDto> ddvdList = dto.getValues();
			for (DataDictionaryValueDto dataDictionaryValueDto : ddvdList) {
				ProjectTypeCountDto ptc=new ProjectTypeCountDto();
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
	/**
	 * 项目总览任务数查询
	 */
	@Override
	public List<TaskStateCountDto> getProjectToTaskCount(String startTime,
			String endTime) {
		List<Long> projectIds = projectMapper.getProjectIds(startTime, endTime);
		return taskMapper.selectTaskStateCount(projectIds);
	}

	
	/***查询工时相关services****by******JING***BEGIN****/

	@Override
	public List<TaskByUsersDto> listTaskHoursByUser(String startTime,
			String endTime, List<Long> uids, List<Long> dids) {
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

		List<TaskByUsersDto> list = taskMapper.selectTaskHourByUser(startTime, endTime, dids,uids);
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
	
	@Override
	public ProjectYearCoverForAllDto getProjectYearsCoverForAll(String year, String month) {
		String startDate = getAppedDate(year, month, true);
		String endDate = getAppedDate(year, month, false);
		return taskMapper.selectProjectYearsCoverForAll(startDate,endDate);
	}
	/***查询工时相关services****by******JING***END****/
	
	
	/**
	 * 项目进展总汇
	 */
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
	 * 计算时间进度方法
	 */
	public static Integer getDateProgress(Date startTime,Date endTime){
		int toNowDays = DateUtil.differentDaysByMillisecond(startTime, new Date())+1;
		int planDays = DateUtil.differentDaysByMillisecond(startTime, endTime)+1;
		int dateProgress = ((int)(((double)toNowDays/(double)planDays)*100));
		return dateProgress;
	}
	/**
	 * 项目类型总汇
	 */
	@Override
	public List<ProjectTypeCountDto> getProjectToTypeSummary(String startTime,
			String endTime) {
		List<ProjectTypeCountDto> list=new ArrayList<ProjectTypeCountDto>();
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_TYPE_CODE);
		int projectTotal = projectMapper.getProjectTypeAllCount(startTime, endTime);
		if (dto != null) {
			List<DataDictionaryValueDto> ddvdList = dto.getValues();
			for (DataDictionaryValueDto dataDictionaryValueDto : ddvdList) {
				ProjectTypeCountDto pts=new ProjectTypeCountDto();
				pts.setType(dataDictionaryValueDto.getCode());
				List<ProjectStatusCountDto> statusCount = projectMapper.getTpyeByProjectCount(dataDictionaryValueDto.getValue(), startTime, endTime);
				int typeTotal=0;
				for (ProjectStatusCountDto projectStatusCountDto : statusCount) {
					typeTotal=typeTotal+projectStatusCountDto.getStateCount();
				}
				pts.setTypeCount(typeTotal);
				if(projectTotal!=0){
					
					pts.setProjectTypeProgress((int)(((double)typeTotal/(double)projectTotal)*100));
				}
				list.add(pts);
			}
			return list;
		}
		return null;
	}
	/**
	 * 项目总览多sheet导出
	 */
	@Override
	public void downloadProjectType(OutputStream os,String startTime,
			String endTime) throws Exception {
		List<ProjectTypeCountDto> ptcList=new ArrayList<ProjectTypeCountDto>();
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_TYPE_CODE);
		if (dto != null) {
			List<DataDictionaryValueDto> ddvdList = dto.getValues();
			for (DataDictionaryValueDto dataDictionaryValueDto : ddvdList) {
				ProjectTypeCountDto ptc=new ProjectTypeCountDto();
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
				ptcList.add(ptc);
			}
		}
		List<TaskStateCountDto> projectToTaskCount = this.getProjectToTaskCount(startTime, endTime);
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		Map<String,Object> map1=new HashMap<String, Object>();
		Map<String,Object> map2=new HashMap<String, Object>();
		map1.put("title", new ExportParams(PROJECT_TYPE_TITLE,PROJECT_TYPE_TITLE));
		map1.put("entity", ProjectTypeCountDto.class);
		map1.put("data", ptcList);
		map2.put("title", new ExportParams(TASK_STATE_TITLE,TASK_STATE_TITLE));
		map2.put("entity", TaskStateCountDto.class);
		map2.put("data", projectToTaskCount);
		list.add(map1);
		list.add(map2);
		
		exportExcel(os,list);
	}
	/**
	 * 调用ExcelUtil方法
	 * @param os
	 * @param list
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void exportExcel(OutputStream os,List<Map<String, Object>> list) throws Exception {
        HSSFWorkbook excel = (HSSFWorkbook) ExcelExportUtil.exportExcel(list,ExcelType.HSSF);
        excel.write(os);
        os.flush();
        os.close();
    }
	@Override
	public List<Map<String,Object>> getHomeProject(List<Long> projectId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		
		Long userId=null;
		if(projectId==null||projectId.size()==0){
			userId=userTicket.getId();
		}
		List<ProjectStatusCountDto> projectCount = projectMapper.getStateByProjectCount(userId,projectId);
		for (ProjectStatusCountDto projectStatusCountDto : projectCount) {
			Map<String,Object> map=new HashMap<String, Object>();
			String string = AlmCache.PROJECT_STATE_MAP.get(projectStatusCountDto.getProjectState().toString());
			map.put("name", string);
			map.put("value", projectStatusCountDto.getStateCount());
			list.add(map);
		}
		return list;	
	}
	
	
	@Override
	public List<Map<String,Object>> getHomeProjectTask(List<Long> projectId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		Long userId=null;
		if(projectId==null||projectId.size()==0){
			userId=userTicket.getId();
		}
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
	   List<TaskStateCountDto> taskCount = taskMapper.getStateByTaskCount(userId,projectId);
	   for (TaskStateCountDto taskStateCountDto : taskCount) {
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("name", taskStateCountDto.getTaskState());
			map.put("value", taskStateCountDto.getStateCount());
			list.add(map);
		}
	   return list;
	}
}
