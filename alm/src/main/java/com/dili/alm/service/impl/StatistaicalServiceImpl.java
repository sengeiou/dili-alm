package com.dili.alm.service.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.dao.WorkDayMapper;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectProgressDto;
import com.dili.alm.domain.dto.ProjectStatusCountDto;
import com.dili.alm.domain.dto.ProjectTypeCountDto;
import com.dili.alm.domain.dto.ProjectYearCoverDto;
import com.dili.alm.domain.dto.ProjectYearCoverForAllDto;
import com.dili.alm.domain.dto.SelectTaskHoursByUserDto;
import com.dili.alm.domain.dto.SelectTaskHoursByUserProjectDto;
import com.dili.alm.domain.dto.SelectYearsDto;
import com.dili.alm.domain.dto.TaskByUsersDto;
import com.dili.alm.domain.dto.TaskHoursByProjectDto;
import com.dili.alm.domain.dto.TaskStateCountDto;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.StatisticalService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.mysql.fabric.xmlrpc.base.Data;

@Service
public class StatistaicalServiceImpl implements StatisticalService {

	@Autowired
	ProjectMapper projectMapper;

	@Autowired
	TaskMapper taskMapper;

	@Autowired
	DataDictionaryService dataDictionaryService;

	@Autowired
	WorkDayMapper wdMapper;

	private static final String PROJECT_TYPE_CODE = "project_type";
	private static final String TASK_STATE_CODE = "task_status";
	private static final String SELECT_HOURS_BY_USER_START_DATE = "2018-01-01";// 项目上线日期
	private static final String FILE_NAME_STR = "项目总览.xls";
	private static final String PROJECT_TYPE_TITLE = "项目列表";
	private static final String TASK_STATE_TITLE = "任务列表";

	/**
	 * 项目总览查询
	 */
	@Override
	public EasyuiPageOutput getProjectTypeCountDTO(String startTime, String endTime) {
		List<ProjectTypeCountDto> list = new ArrayList<ProjectTypeCountDto>();
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_TYPE_CODE);
		if (dto != null) {
			List<DataDictionaryValueDto> ddvdList = dto.getValues();
			for (DataDictionaryValueDto dataDictionaryValueDto : ddvdList) {
				ProjectTypeCountDto ptc = new ProjectTypeCountDto();
				ptc.setType(dataDictionaryValueDto.getCode());
				List<ProjectStatusCountDto> statusCount = projectMapper
						.getTpyeByProjectCount(dataDictionaryValueDto.getValue(), startTime, endTime);
				int total = 0;
				for (ProjectStatusCountDto projectStatusCountDto : statusCount) {
					total = total + projectStatusCountDto.getStateCount();
					switch (projectStatusCountDto.getProjectState()) {
					case 0:
						ptc.setNotStartCount(projectStatusCountDto.getStateCount());
						break;
					case 1:
						ptc.setOngoingConut(projectStatusCountDto.getStateCount());
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
	public List<TaskStateCountDto> getProjectToTaskCount(String startTime, String endTime) {
		List<Long> projectIds = projectMapper.getProjectIds(startTime, endTime);
		List<TaskStateCountDto> list = new ArrayList<TaskStateCountDto>();
		if (projectIds != null && projectIds.size() > 0) {
			return taskMapper.selectTaskStateCount(projectIds);
		}
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(TASK_STATE_CODE);
		if (dto != null) {
			List<DataDictionaryValueDto> ddvdList = dto.getValues();
			for (DataDictionaryValueDto dataDictionaryValueDto : ddvdList) {
				TaskStateCountDto tscDto = new TaskStateCountDto();
				tscDto.setTaskState(dataDictionaryValueDto.getCode());
				tscDto.setStateCount("0");
				list.add(tscDto);
			}
		}
		return list;
	}

	/*** 查询工时相关services****by******JING***BEGIN ****/

	@Override
	public List<TaskByUsersDto> listTaskHoursByUser(String startTime, String endTime, List<Long> uids,
			List<Long> dids) {
		if (startTime == null) {
			startTime = SELECT_HOURS_BY_USER_START_DATE;
		} else {
			startTime += "";
		}
		if (endTime == null) {// 没有默认为至今
			endTime = DateUtil.getDate(new Date()) + "";
		} else {
			endTime += "";
		}

		List<TaskByUsersDto> list = taskMapper.selectTaskHourByUser(startTime, endTime, dids, uids);
		list.add(this.getTotal(startTime, endTime, uids, dids));
		return list;
	}

	private TaskByUsersDto getTotal(String startTime, String endTime, List<Long> uids, List<Long> dids) {
		return taskMapper.selectTotalTaskHourByUser(startTime, endTime, dids, uids);
	}

	/***
	 * 查询项目以及项目工时 ，表头
	 */
	@Override
	public List<TaskHoursByProjectDto> listProjectHours(String startTime, String endTime, List<Long> projectIds) {
		if (startTime == null || startTime.equals("")) {
			startTime = DateUtil.getPastDate(7);
		} else {
			startTime += "";
		}
		if (endTime == null || endTime.equals("")) {// 没有默认为至今
			endTime = DateUtil.getDate(new Date()) + "";
		} else {
			endTime += "";
		}
		return taskMapper.selectProjectHours(startTime, endTime, projectIds);
	}

	/***
	 * 查询时间段内工作的人员，数据填充内容
	 */
	@Override
	public List<SelectTaskHoursByUserDto> listUserHours(String startTime, String endTime, List<Long> projectIds)
			throws ParseException {
		if (startTime == null || startTime.equals("")) {
			startTime = DateUtil.getPastDate(7);
		} else {
			startTime += "";
		}
		if (endTime == null || endTime.equals("")) {// 没有默认为至今
			endTime = DateUtil.getDate(new Date()) + "";
		} else {
			endTime += "";
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		// 查出项目列表，既表头
		List<TaskHoursByProjectDto> taskHoursForPoject = taskMapper.selectProjectHours(startTime, endTime, projectIds);

		// projectId 转为查询条件传递
		List<Long> selectProjectIds = new ArrayList<Long>(taskHoursForPoject.size());
		if (taskHoursForPoject == null || taskHoursForPoject.size() == 0) {
			selectProjectIds.add((long) 0);
		} else {
			taskHoursForPoject.forEach(p -> {
				selectProjectIds.add(p.getProjectId());
			});
		}

		List<Map<Object, Object>> listMap = taskMapper.sumUserProjectTaskHour(selectProjectIds, df.parse(startTime),
				df.parse(endTime));

		// 查出人员信息，既数据，缺少项目的加班工时和正常工时
		// List<SelectTaskHoursByUserDto> list = taskMapper.selectUsersHours(startTime,
		// endTime, projectIds);

		/*
		 * if (list==null||list.size()==0) { return null; }
		 */

		/*
		 * for (SelectTaskHoursByUserDto userHoursEntity : list) {//根据人员信息去查找相应的项目工时 int
		 * totalTaskHours = 0; int totalOverHours = 0; int totalAllHours = 0;
		 */

		// 设置人员ID信息，查询出每个人员的所有项目 加班工时和正常工时
		/*
		 * List<Long> userLongIds= new ArrayList<Long>();
		 * userLongIds.add(userHoursEntity.getUserNo());
		 */
		// List<SelectTaskHoursByUserProjectDto>
		// result=taskMapper.selectUsersProjectHours(userLongIds,null);

		// 循环读取项目，将项目的工时，以key-value的形式和人员信息保存在一起
		/* for (TaskHoursByProjectDto projectHours : taskHoursForPoject) { */

		/*
		 * Long projectId =projectHours.getProjectId();//key值 project+projectId
		 * 
		 * //excel，预设，为0 ，防止出现空 hoursMap.put("project"+projectId+"sumUPOverHours",0+"");
		 * hoursMap.put("project"+projectId+"sumUPTaskHours",0+"");
		 */

		/*
		 * //循环项目工时 for (SelectTaskHoursByUserProjectDto entity: result) { if
		 * (projectId.equals(entity.getProjectId())) { hoursMap.put("project"+projectId,
		 * "sumUPTaskHours:"+entity.getSumUPTaskHours()+",sumUPOverHours:"+entity.
		 * getSumUPOverHours());
		 * hoursMap.put("project"+projectId+"sumUPTaskHours",entity.getSumUPTaskHours()+
		 * "");//excel专用key
		 * hoursMap.put("project"+projectId+"sumUPOverHours",entity.getSumUPOverHours()+
		 * "");//excel专用key totalTaskHours+=entity.getSumUPTaskHours();
		 * totalOverHours+=entity.getSumUPOverHours(); } }
		 */

		/*
		 * }
		 */
		/* } */

		List<SelectTaskHoursByUserDto> list = new ArrayList<SelectTaskHoursByUserDto>(listMap.size());
		listMap.forEach(lm -> {
			SelectTaskHoursByUserDto entity = new SelectTaskHoursByUserDto();
			Map<String, String> hoursMap = new HashMap<String, String>();// 保存每个项目的加班工时和任务工时
			entity.setUserNo(Long.valueOf(lm.get("user_id").toString()));
			entity.setUserName(lm.get("real_name").toString());
			entity.setSumOverHours(Integer.valueOf(lm.get("project_user_total_over_hour").toString()));
			entity.setSumTaskHours(Integer.valueOf(lm.get("project_user_total_task_hour").toString()));
			entity.setTotalHours(Integer.valueOf(lm.get("project_user_total_hour").toString()));
			// excel专用
			for (TaskHoursByProjectDto projectHours : taskHoursForPoject) {
				Long projectId = projectHours.getProjectId();
				hoursMap.put("project" + projectId + "sumUPTaskHours",
						lm.get(projectId + "_total_task_hour").toString());// excel专用key
				hoursMap.put("project" + projectId + "sumUPOverHours",
						lm.get(projectId + "_total_over_hour").toString());
			}
			entity.setProjectHours(hoursMap);
			list.add(entity);
		});
		// 处理项目合计正常工时，加班工时
		List<Long> projectTotalHoursSelectList = new ArrayList<Long>();
		if (taskHoursForPoject == null || taskHoursForPoject.size() == 0) {
			projectTotalHoursSelectList.add((long) 0);
		} else {
			taskHoursForPoject.forEach(p -> {
				projectTotalHoursSelectList.add(p.getProjectId());
			});
		}
		SelectTaskHoursByUserDto totalTaskandOverHour = this.selectTotalTaskAndOverHours(projectTotalHoursSelectList);
		list.add(totalTaskandOverHour);

		return list;
	}

	@Override
	public SelectTaskHoursByUserDto selectTotalTaskAndOverHours(List<Long> projectIds) {

		List<SelectTaskHoursByUserProjectDto> listProjectTaskOverHours = taskMapper.selectUsersProjectHours(null,
				projectIds);
		SelectTaskHoursByUserDto totalTaskandOverHour = new SelectTaskHoursByUserDto();
		totalTaskandOverHour.setUserName("合计");
		totalTaskandOverHour.setUserNo((long) 0);

		Map<String, String> hoursMap = new HashMap<String, String>();

		int totalTaskHours = 0;
		int totalOverHours = 0;
		int totalAllHours = 0;

		for (SelectTaskHoursByUserProjectDto dto : listProjectTaskOverHours) {
			if (dto != null) {
				hoursMap.put("project" + dto.getProjectId() + "sumUPTaskHours", dto.getSumUPTaskHours() + "");
				hoursMap.put("project" + dto.getProjectId() + "sumUPOverHours", dto.getSumUPOverHours() + "");
				totalTaskHours += dto.getSumUPTaskHours();
				totalOverHours += dto.getSumUPOverHours();
			}
		}
		totalAllHours = totalTaskHours + totalOverHours;
		totalTaskandOverHour.setProjectHours(hoursMap);
		totalTaskandOverHour.setSumTaskHours(totalTaskHours);
		totalTaskandOverHour.setSumOverHours(totalOverHours);
		totalTaskandOverHour.setTotalHours(totalAllHours);
		return totalTaskandOverHour;
	}

	/**
	 * 项目工时sheet导出
	 */
	@Override
	public HSSFWorkbook downloadProjectHours(OutputStream os, String startTime, String endTime, List<Long> projectIds)
			throws Exception {

		List<TaskHoursByProjectDto> projects = listProjectHours(startTime, endTime, projectIds);
		List<SelectTaskHoursByUserDto> objs = listUserHours(startTime, endTime, projectIds);

		// 标题
		List<ExcelExportEntity> entityList = new ArrayList<ExcelExportEntity>();
		// 内容
		List<Map<String, Object>> dataResult = new ArrayList<Map<String, Object>>();
		entityList.add(new ExcelExportEntity("员工&项目", "name", 25));
		if (projects != null || projects.size() > 0) {
			for (TaskHoursByProjectDto dto : projects) {
				entityList.add(
						new ExcelExportEntity(dto.getProjectName() + "-常规工时", "project_to_" + dto.getProjectId(), 25));
				entityList.add(
						new ExcelExportEntity(dto.getProjectName() + "-加班工时", "project_oo_" + dto.getProjectId(), 25));
			}
		}

		entityList.add(new ExcelExportEntity("合计-常规工时", "sumhours", 25));
		entityList.add(new ExcelExportEntity("合计-加班工时", "overhours", 25));
		entityList.add(new ExcelExportEntity("合计-总工时", "sohours", 25));
		// 内容
		if (objs != null || objs.size() > 0) {
			for (SelectTaskHoursByUserDto dto2 : objs) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", dto2.getUserName());

				for (TaskHoursByProjectDto dto : projects) {
					map.put("project_to_" + dto.getProjectId(),
							dto2.getProjectHours().get("project" + dto.getProjectId() + "sumUPTaskHours"));
					map.put("project_oo_" + dto.getProjectId(),
							dto2.getProjectHours().get("project" + dto.getProjectId() + "sumUPOverHours"));
				}

				map.put("sumhours", dto2.getSumTaskHours());
				map.put("overhours", dto2.getSumOverHours());
				map.put("sohours", dto2.getTotalHours());
				dataResult.add(map);
			}
		}

		HSSFWorkbook excel = (HSSFWorkbook) ExcelExportUtil.exportExcel(
				new ExportParams("项目工时总览", "项目工时总览" + DateUtil.getDate(new Date())), entityList, dataResult);

		return excel;

	}

	@Override
	public List<ProjectYearCoverDto> listProjectYearCover(String year, String month, String weekNum) {
		String start = null;
		String end = null;
		if (weekNum == "" || weekNum == null) {
			start = getAppedDate(year, month, true);
			end = getAppedDate(year, month, false);
		} else {

			int yeanIntager = Integer.parseInt(year);

			int monthIntager = Integer.parseInt(month);

			int weekNumIntager = Integer.parseInt(weekNum);

			Map<String, String> dateUtilMap = DateUtil.getWeeks(yeanIntager, monthIntager);// 获取当月所有周的开始结束时间

			start = dateUtilMap.get("start" + weekNumIntager);

			end = dateUtilMap.get("end" + weekNumIntager);
		}

		return taskMapper.selectProjectYearsCover(start, end);
	}

	private static String getAppedDate(String year, String month, Boolean isBegin) {
		Calendar now = Calendar.getInstance();
		String retrunDate = "";

		if (year != null) {
			retrunDate += year;
		} else {
			retrunDate += now.get(Calendar.YEAR);
		}

		if (month == null || month == "") {
			if (isBegin) {
				month = "1";
			} else {
				month = "12";
			}
		}
		if (isBegin) {
			switch (month) {
			case "1":
				retrunDate += "-01-01";
				break;
			case "2":
				retrunDate += "-02-01";
				break;
			case "3":
				retrunDate += "-03-01";
				break;
			case "4":
				retrunDate += "-04-01";
				break;
			case "5":
				retrunDate += "-05-01";
				break;
			case "6":
				retrunDate += "-06-01";
				break;
			case "7":
				retrunDate += "-07-01";
				break;
			case "8":
				retrunDate += "-08-01";
				break;
			case "9":
				retrunDate += "-09-01";
				break;
			case "10":
				retrunDate += "-10-01";
				break;
			case "11":
				retrunDate += "-11-01";
				break;
			case "12":
				retrunDate += "-12-01";
				break;
			case "13":
				retrunDate += "-01-01";
				break;
			case "14":
				retrunDate += "-04-01";
				break;
			case "15":
				retrunDate += "-07-01";
				break;
			case "16":
				retrunDate += "-10-01";
				break;
			default:
				retrunDate = SELECT_HOURS_BY_USER_START_DATE;
				break;
			}
		} else {
			switch (month) {
			case "1":
				retrunDate += "-01-31";
				break;
			case "2":
				retrunDate += "-02-28";
				break;
			case "3":
				retrunDate += "-03-31";
				break;
			case "4":
				retrunDate += "-04-30";
				break;
			case "5":
				retrunDate += "-05-31";
				break;
			case "6":
				retrunDate += "-06-30";
				break;
			case "7":
				retrunDate += "-07-31";
				break;
			case "8":
				retrunDate += "-08-31";
				break;
			case "9":
				retrunDate += "-09-30";
				break;
			case "10":
				retrunDate += "-10-31";
				break;
			case "11":
				retrunDate += "-11-30";
				break;
			case "12":
				retrunDate += "-12-31";
				break;
			case "13":
				retrunDate += "-03-31";
				break;
			case "14":
				retrunDate += "-06-30";
				break;
			case "15":
				retrunDate += "-09-30";
				break;
			case "16":
				retrunDate += "-12-31";
				break;
			default:
				retrunDate = DateUtil.getDate(new Date());
				break;
			}
		}
		return retrunDate;
	}

	@Override
	public String getSearchDate(String year, String month, String weekNum) {
		String start = null;
		String end = null;
		if (weekNum == null || weekNum == "") {
			start = getAppedDate(year, month, true);
			end = getAppedDate(year, month, false);

		} else {
			int yeanIntager = Integer.parseInt(year);

			int monthIntager = Integer.parseInt(month);

			int weekNumIntager = Integer.parseInt(weekNum);

			Map<String, String> dateUtilMap = DateUtil.getWeeks(yeanIntager, monthIntager);// 获取当月所有周的开始结束时间

			start = dateUtilMap.get("start" + weekNumIntager);

			end = dateUtilMap.get("end" + weekNumIntager);
		}
		if (StringUtils.isBlank(start)) {
			start = "-";
		}
		if (StringUtils.isBlank(end)) {
			end = "-";
		}
		String returnStr = start + "至" + end;
		return returnStr;
	}

	@Override
	public ProjectYearCoverForAllDto getProjectYearsCoverForAll(String year, String month, boolean isFullYear,
			String weekNum) {
		String start = null;
		String end = null;
		if (isFullYear) {
			start = getAppedDate(year, null, true);
			end = getAppedDate(year, null, false);

		} else if (weekNum == null || weekNum == "") {
			start = getAppedDate(year, month, true);
			end = getAppedDate(year, month, false);

		} else {
			int yeanIntager = Integer.parseInt(year);

			int monthIntager = Integer.parseInt(month);

			int weekNumIntager = Integer.parseInt(weekNum);

			Map<String, String> dateUtilMap = DateUtil.getWeeks(yeanIntager, monthIntager);// 获取当月所有周的开始结束时间

			start = dateUtilMap.get("start" + weekNumIntager);

			end = dateUtilMap.get("end" + weekNumIntager);
		}
		return taskMapper.selectProjectYearsCoverForAll(start, end);
	}

	/*****
	 * 项目工时显示图表
	 */
	@Override
	public List<SelectTaskHoursByUserProjectDto> selectTotalTaskAndOverHoursForEchars(List<Long> projectIds) {
		List<SelectTaskHoursByUserProjectDto> listProjectTaskOverHours = taskMapper.selectUsersProjectHours(null,
				projectIds);

		return listProjectTaskOverHours;
	}

	@Override
	public List<SelectYearsDto> SelectYears() {
		List<String> yearList = wdMapper.getWorkYear();
		List<SelectYearsDto> dtoList = new ArrayList<SelectYearsDto>(yearList.size());
		yearList.forEach(c -> {
			SelectYearsDto newDto = new SelectYearsDto();
			newDto.setText(c + "年");
			newDto.setValue(c);
			dtoList.add(newDto);
		});
		return dtoList;
	}

	/*** 查询工时相关services****by******JING***END ****/

	/**
	 * 项目进展总汇
	 */
	@Override
	public EasyuiPageOutput getProjectProgresstDTO(Project project, String startTime, String endTime,
			List<Integer> stateIds, Integer f) {
		List<ProjectProgressDto> projectProgressList = projectMapper.getProjectProgressList(project, startTime, endTime,
				stateIds, f);
		int projectProgressListCount = projectMapper.getProjectProgressListCount(startTime, endTime, stateIds, f);
		for (ProjectProgressDto projectProgressDto : projectProgressList) {
			projectProgressDto.setProjectProgress(projectProgressDto.getCompletedProgress() + "%");
			projectProgressDto.setLaunchTime(DateUtil.getDate(projectProgressDto.getEstimateLaunchDate()));
			switch (projectProgressDto.getProjectState()) {
			case 0:
				projectProgressDto.setDateProgress(0 + "%");
				break;
			case 1:
				projectProgressDto.setDateProgress(
						getDateProgress(projectProgressDto.getStartDate(), projectProgressDto.getEndDate()) + "%");
				break;
			case 3:
				projectProgressDto.setDateProgress(
						getDateProgress(projectProgressDto.getStartDate(), projectProgressDto.getEndDate()) + "%");
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
	public static Integer getDateProgress(Date startTime, Date endTime) {
		int toNowDays = DateUtil.differentDaysByMillisecond(startTime, new Date()) + 1;
		int planDays = DateUtil.differentDaysByMillisecond(startTime, endTime) + 1;
		int dateProgress = ((int) (((double) toNowDays / (double) planDays) * 100));
		return dateProgress;
	}

	/**
	 * 项目类型总汇
	 */
	@Override
	public List<ProjectTypeCountDto> getProjectToTypeSummary(String startTime, String endTime) {
		List<ProjectTypeCountDto> list = new ArrayList<ProjectTypeCountDto>();
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_TYPE_CODE);
		int projectTotal = projectMapper.getProjectTypeAllCount(startTime, endTime);
		if (dto != null) {
			List<DataDictionaryValueDto> ddvdList = dto.getValues();
			for (DataDictionaryValueDto dataDictionaryValueDto : ddvdList) {
				ProjectTypeCountDto pts = new ProjectTypeCountDto();
				pts.setId(dataDictionaryValueDto.getValue());
				pts.setType(dataDictionaryValueDto.getCode());
				List<ProjectStatusCountDto> statusCount = projectMapper
						.getTpyeByProjectCount(dataDictionaryValueDto.getValue(), startTime, endTime);
				int typeTotal = 0;
				for (ProjectStatusCountDto projectStatusCountDto : statusCount) {
					typeTotal = typeTotal + projectStatusCountDto.getStateCount();
				}
				pts.setTypeCount(typeTotal);
				if (projectTotal != 0) {

					pts.setProjectTypeProgress(((int) (((double) typeTotal / (double) projectTotal) * 100)) + "%");
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
	public void downloadProjectType(OutputStream os, String startTime, String endTime) throws Exception {
		List<ProjectTypeCountDto> ptcList = new ArrayList<ProjectTypeCountDto>();
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_TYPE_CODE);
		if (dto != null) {
			List<DataDictionaryValueDto> ddvdList = dto.getValues();
			for (DataDictionaryValueDto dataDictionaryValueDto : ddvdList) {
				ProjectTypeCountDto ptc = new ProjectTypeCountDto();
				ptc.setType(dataDictionaryValueDto.getCode());
				List<ProjectStatusCountDto> statusCount = projectMapper
						.getTpyeByProjectCount(dataDictionaryValueDto.getValue(), startTime, endTime);
				int total = 0;
				for (ProjectStatusCountDto projectStatusCountDto : statusCount) {
					total = total + projectStatusCountDto.getStateCount();
					switch (projectStatusCountDto.getProjectState()) {
					case 0:
						ptc.setNotStartCount(projectStatusCountDto.getStateCount());
						break;
					case 1:
						ptc.setOngoingConut(projectStatusCountDto.getStateCount());
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
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		map1.put("title", new ExportParams(PROJECT_TYPE_TITLE, PROJECT_TYPE_TITLE));
		map1.put("entity", ProjectTypeCountDto.class);
		map1.put("data", ptcList);
		map2.put("title", new ExportParams(TASK_STATE_TITLE, TASK_STATE_TITLE));
		map2.put("entity", TaskStateCountDto.class);
		map2.put("data", projectToTaskCount);
		list.add(map1);
		list.add(map2);

		exportExcel(os, list);
	}

	/**
	 * 调用ExcelUtil方法
	 * 
	 * @param os
	 * @param list
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void exportExcel(OutputStream os, List<Map<String, Object>> list) throws Exception {

		InputStream stream = getClass().getClassLoader().getResourceAsStream("excel/" + FILE_NAME_STR);
		File targetFile = new File(FILE_NAME_STR);
		if (!targetFile.exists()) {
			FileUtils.copyInputStreamToFile(stream, targetFile);
		}
		HSSFWorkbook excel = (HSSFWorkbook) ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
		excel.write(os);
		os.flush();
		os.close();
	}

	@Override
	public List<Map<String, Object>> getHomeProject(Long userId, List<Long> projectId) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<ProjectStatusCountDto> projectCount = projectMapper.getStateByProjectCount(userId, projectId);
		for (ProjectStatusCountDto projectStatusCountDto : projectCount) {
			Map<String, Object> map = new HashMap<String, Object>();
			String string = AlmCache.getInstance().getProjectStateMap()
					.get(projectStatusCountDto.getProjectState().toString());
			map.put("name", string);
			map.put("value", projectStatusCountDto.getStateCount());
			list.add(map);
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getHomeProjectTask(Long userId, List<Long> projectId) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<TaskStateCountDto> taskCount = taskMapper.getStateByTaskCount(userId, projectId);
		for (TaskStateCountDto taskStateCountDto : taskCount) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", taskStateCountDto.getTaskState());
			map.put("value", taskStateCountDto.getStateCount());
			list.add(map);
		}
		return list;
	}

}
