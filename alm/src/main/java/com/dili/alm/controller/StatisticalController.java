package com.dili.alm.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.ProjectTaskHourCountDto;
import com.dili.alm.domain.dto.ProjectTypeCountDto;
import com.dili.alm.domain.dto.ProjectYearCoverDto;
import com.dili.alm.domain.dto.ProjectYearCoverForAllDto;
import com.dili.alm.domain.dto.SelectTaskHoursByUserDto;
import com.dili.alm.domain.dto.SelectTaskHoursByUserProjectDto;
import com.dili.alm.domain.dto.SelectYearsDto;
import com.dili.alm.domain.dto.TaskByUsersDto;
import com.dili.alm.domain.dto.TaskHoursByProjectDto;
import com.dili.alm.domain.dto.TaskStateCountDto;
import com.dili.alm.domain.dto.UserProjectTaskHourCountDto;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.StatisticalService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-18 17:22:54.
 */
@Api("/statistical")
@Controller
@RequestMapping("/statistical")
public class StatisticalController {

	private static final String DATA_AUTH_TYPE = "Project";
	@Autowired
	private StatisticalService statisticalService;

	@Autowired
	private ProjectService projectService;
	@Autowired
	private TaskMapper taskMapper;

	@ApiOperation("跳转到projectOverview页面")
	@RequestMapping(value = "/projectOverview", method = RequestMethod.GET)
	public String projectOverview(ModelMap modelMap) {
		return "statistical/projectOverview";
	}

	@ApiOperation("跳转到projectProgress页面")
	@RequestMapping(value = "/projectProgress", method = RequestMethod.GET)
	public String projectProgress(ModelMap modelMap) {
		return "statistical/projectProgress";
	}

	@ApiOperation("跳转到projectType页面")
	@RequestMapping(value = "/projectType", method = RequestMethod.GET)
	public String projectType(ModelMap modelMap) {
		return "statistical/projectType";
	}

	@ApiOperation("跳转到taskHoursForUser页面")
	@RequestMapping(value = "/taskHoursForUser", method = RequestMethod.GET)
	public String taskHoursForUser(ModelMap modelMap) {
		return "statistical/taskHoursForUser";
	}

	@ApiOperation("跳转到taskHoursForProject页面")
	@RequestMapping(value = "/taskHoursForProject", method = RequestMethod.GET)
	public String taskHoursForProject(ModelMap modelMap) {
		return "statistical/taskHoursForProject";
	}

	@ApiOperation("跳转到projectType页面")
	@RequestMapping(value = "/projectYearCover", method = RequestMethod.GET)
	public String projectYearCover(ModelMap modelMap) {
		return "statistical/projectYearCover";
	}

	@ApiOperation(value = "查询项目", notes = "查询返回easyui信息")
	@RequestMapping(value = "/ProjectOverviewlist", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String ProjectOverviewlist(String startTime, String endTime, Integer flat) throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		return statisticalService.getProjectTypeCountDTO(startTime2, endTime2).toString();

	}

	@ApiOperation(value = "查询项目任务数量", notes = "查询返回List信息")
	@RequestMapping(value = "/ProjectOverviewTasklist", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<TaskStateCountDto> ProjectOverviewTasklist(String startTime, String endTime, Integer flat)
			throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		return statisticalService.getProjectToTaskCount(startTime2, endTime2);

	}

	/*** 查询工时相关services****by******JING***BEGIN ****/

	@ApiOperation(value = "查询时间段内员工工时", notes = "查询返回easyui信息")
	@RequestMapping(value = "/taskHoursByUser", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<TaskByUsersDto> taskHoursByUser(String startTime, String endTime, String[] userId,
			String[] departmentId) throws Exception {

		List<Long> userIds = null;
		List<Long> departmentIds = null;

		if (userId != null) {
			userIds = new ArrayList<Long>(userId.length);
			for (String long1 : userId) {
				userIds.add(Long.parseLong(long1));
			}
		}
		if (departmentId != null) {
			departmentIds = new ArrayList<Long>(departmentId.length);
			for (String long1 : departmentId) {
				departmentIds.add(Long.parseLong(long1));
			}
		}
		List<TaskByUsersDto> taskByUserDtoList = statisticalService.listTaskHoursByUser(startTime, endTime, userIds,
				departmentIds);
		return taskByUserDtoList;

	}

	@ApiOperation(value = "查询年度报表", notes = "查询返回easyui信息")
	@RequestMapping(value = "/listProjectYearCover", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectYearCoverDto> listProjectYearCover(String year, String month,String aaa,String weekNum) throws Exception {
		return statisticalService.listProjectYearCover(year, month,weekNum);

	}
	


	@ApiOperation(value = "报表图显示", notes = "查询返回easyui信息")
	@RequestMapping(value = "/projectYearCoverForAll", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ProjectYearCoverForAllDto projectYearCoverForAll(String year, String month,boolean isFullYear,String weekNum) throws Exception {
		ProjectYearCoverForAllDto all = statisticalService.getProjectYearsCoverForAll(year, month,isFullYear,weekNum);
		return all;
	}

	@ApiOperation(value = "返回查询后的日期", notes = "查询返回easyui信息")
	@RequestMapping(value = "/getYears.json", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<SelectYearsDto> getYears() throws Exception {
		
		return statisticalService.SelectYears();

	}
	
	@ApiOperation(value = "获取当月星期数", notes = "查询返回easyui信息")
	@RequestMapping(value = "/getWeekNum.json", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String getWeekNum(String year,String month) throws Exception {
		if(year==null||month==null){
			return String.valueOf(5);
		}
		int yearIntager = 0;
		int monthIntager = 0;
		try {
			yearIntager = Integer.parseInt(year);
			monthIntager = Integer.parseInt(month);
		} catch (Exception e) {
			return String.valueOf(5);
		}

		int size = DateUtil.getWeeks(yearIntager, monthIntager).size()/2 ;//得到开始结束时间所有key值总数
		return String.valueOf(size);

	}

	@ApiOperation(value = "返回查询年份", notes = "查询返回easyui信息")
	@RequestMapping(value = "/getSearchDate.json", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String getSearchDate(String year, String month,String weekNum) throws Exception {
		String returnStr = JSONObject.toJSONString(statisticalService.getSearchDate(year,month,weekNum));
		return returnStr;

	}

	@ApiOperation(value = "项目工时查询", notes = "查询返回easyui信息")
	@RequestMapping(value = "/listProjectHours", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<TaskHoursByProjectDto> getSearchAllDto(String startDate, String endDate, String[] project)
			throws Exception {
		List<Long> projectIds = new ArrayList<Long>();
		if (project != null) {
			projectIds = new ArrayList<Long>(project.length);
			for (String long1 : project) {
				projectIds.add(Long.parseLong(long1));
			}
		}
		return statisticalService.listProjectHours(startDate, endDate, projectIds);

	}

	@ApiOperation(value = "项目工时图表查询", notes = "查询返回easyui信息")
	@RequestMapping(value = "/listProjectForEchar", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<SelectTaskHoursByUserProjectDto> listProjectForEchar(String[] projectIds, Long userId)
			throws Exception {
		List<Long> projectIdList = new ArrayList<Long>();
		if (projectIds != null&&projectIds.length>0) {
			projectIdList = new ArrayList<Long>(projectIds.length);
			for (String long1 : projectIds) {
				projectIdList.add(Long.parseLong(long1));
			}
		}else{
			projectIdList.add(Long.getLong(String.valueOf(-1)));
		}
		return statisticalService.selectTotalTaskAndOverHoursForEchars(projectIdList);

	}
	/*
	 * 项目工时部分优化测试，转为list bean传递页面保存
	 */
	@ResponseBody
	@RequestMapping(value = "/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<UserProjectTaskHourCountDto> test() throws ParseException {
		List<Long> projectIds = new ArrayList<>();
		List<Project> projects = this.projectService.list(DTOUtils.newDTO(Project.class));
		projects.forEach(p -> projectIds.add(p.getId()));
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		List<Map<Object, Object>> listMap = this.taskMapper.sumUserProjectTaskHour(projectIds, df.parse("2018-02-01"),
				df.parse("2018-05-01"));
		return this.buildUserProjectTaskHourCountDto(listMap);
	}

	/*
	 * 项目工时部分待优化部分，转为list bean传递页面保存
	 */
	private List<UserProjectTaskHourCountDto> buildUserProjectTaskHourCountDto(List<Map<Object, Object>> listMap) {
		List<UserProjectTaskHourCountDto> target = new ArrayList<>(listMap.size());
		listMap.forEach(lm -> {
			UserProjectTaskHourCountDto dto = new UserProjectTaskHourCountDto();
			dto.setUserId(Long.valueOf(lm.get("user_id").toString()));
			dto.setRealName(lm.get("real_name").toString());
			dto.setTotalHour(Long.valueOf(lm.get("project_user_total_hour").toString()));
			dto.setTotalTaskHour(Long.valueOf(lm.get("project_user_total_task_hour").toString()));
			dto.setTotalOverHour(Long.valueOf(lm.get("project_user_total_over_hour").toString()));
			lm.entrySet().forEach(e -> {
				int index = e.getKey().toString().indexOf("_");
				String key = e.getKey().toString().substring(0, index);
				if (!StringUtils.isNumeric(key)) {
					return;
				}
				Long projectId = Long.valueOf(key);
				ProjectTaskHourCountDto pthcDto = new ProjectTaskHourCountDto();
				pthcDto.setProjectId(projectId);
				if (dto.getProjectTaskHours().contains(pthcDto)) {
					return;
				}
				pthcDto.setTotalTaskHour(Long.valueOf(lm.get(projectId + "_total_task_hour").toString()));
				pthcDto.setTotalOverHour(Long.valueOf(lm.get(projectId + "_total_over_hour").toString()));
				dto.getProjectTaskHours().add(pthcDto);
			});
			target.add(dto);
		});
		return target;
	}

	@ApiOperation(value = "项目总工时查询", notes = "查询返回easyui信息")
	@RequestMapping(value = "/listUserHours", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<SelectTaskHoursByUserDto> listUserHours(String startDate, String endDate,
			String[] project) throws Exception {
		List<Long> projectIds = null;

		if (project != null) {
			projectIds = new ArrayList<Long>(project.length);
			for (String long1 : project) {
				projectIds.add(Long.parseLong(long1));
			}
		}
		
		 List<SelectTaskHoursByUserDto> aa = statisticalService.listUserHours(startDate, endDate, projectIds);
		return aa;

	}

	@RequestMapping(value = "/exportPorjectHours", method = { RequestMethod.POST, RequestMethod.GET })
	public @ResponseBody BaseOutput export(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap,String startDate, String endDate,
			String[] project) throws IOException {
		try {
			String rtn = getRtn("项目工时统计.xls", request);
			response.setContentType("text/html");
			response.setHeader("Content-Disposition", "attachment;" + rtn);
			OutputStream os = response.getOutputStream();
			List<Long> projectIds = null;

			if (project != null) {
				projectIds = new ArrayList<Long>(project.length);
				for (String long1 : project) {
					projectIds.add(Long.parseLong(long1));
				}
			}
			HSSFWorkbook excel = statisticalService.downloadProjectHours(os, startDate, endDate, projectIds);
			excel.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
			return BaseOutput.failure("导出失败");
		}
		return BaseOutput.success("导出成功");
	}


	/*** 查询工时相关services****by******JING***END ****/

	@ApiOperation(value = "查询项目进展总汇", notes = "查询返回easyui信息")
	@RequestMapping(value = "/ProjectProgressList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String ProjectProgressList(Project project, String startTime, String endTime, Integer flat,
			String ids) throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		Integer f=1;
		if(WebUtil.strIsEmpty(startTime)&&WebUtil.strIsEmpty(startTime)){
			f=0;
		}
		List<Integer> list = new ArrayList<Integer>();
		if (!WebUtil.strIsEmpty(ids)) {
			String[] split = ids.split(",");
			for (String string : split) {
				list.add(Integer.parseInt(string));
			}
		}
		return statisticalService.getProjectProgresstDTO(project, startTime2, endTime2, list,f).toString();

	}

	@ApiOperation(value = "查询所有类型汇总", notes = "查询返回List信息")
	@RequestMapping(value = "/projecTypetList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectTypeCountDto> projecTypetList(String startTime, String endTime, Integer flat)
			throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		return statisticalService.getProjectToTypeSummary(startTime2, endTime2);

	}

	@ApiOperation(value = "查询项目集合", notes = "查询返回List信息")
	@RequestMapping(value = "/projectList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Project> projectList(Project project) throws Exception {
		return projectService.list(project);

	}
	@ApiOperation(value = "查询项目状态集合", notes = "查询返回List信息")
	@RequestMapping(value = "/projectStateList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map<String,String>> projectStateList() throws Exception {
		return projectService.projectStateList();

	}
	

	@RequestMapping(value = "/projecOverViewDownload", method = { RequestMethod.GET, RequestMethod.POST })
	public void projecOverViewDownload(String startTime, String endTime, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String startTime2 = getStartTime(startTime, endTime, null);
		String endTime2 = getEndTime(startTime, endTime, null);
		String fileName = "项目总览.xls";
		// 默认使用IE的方式进行编码
		try {
			String rtn = getRtn(fileName, request);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;" + rtn);
			statisticalService.downloadProjectType(response.getOutputStream(), startTime2, endTime2);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/homeProjectCount", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map<String, Object>> homeProjectCount(String flat) {
		List<Long> projectIds = new ArrayList<>();
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		Long userId = userTicket.getId();
		if (!WebUtil.strIsEmpty(flat) && flat.equals("1")) {
			@SuppressWarnings({ "rawtypes" })
			List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
			if (CollectionUtils.isEmpty(dataAuths)) {
				return null;
			}
			dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("dataId").toString())));
			userId = null;
		}
		return statisticalService.getHomeProject(userId, projectIds);
	}

	@RequestMapping(value = "/homeTaskCount", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map<String, Object>> homeTaskCount(String flat) {
		List<Long> projectIds = new ArrayList<>();
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		Long userId = userTicket.getId();
		if (!WebUtil.strIsEmpty(flat) && flat.equals("1")) {
			@SuppressWarnings({ "rawtypes" })
			List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
			if (CollectionUtils.isEmpty(dataAuths)) {
				return null;
			}
			dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("dataId").toString())));
			userId = null;
		}
		return statisticalService.getHomeProjectTask(userId, projectIds);

	}

	public static String getStartTime(String startTime, String endTime, Integer flat) {
		if (flat != null) {
			startTime = DateUtil.getPastDate(flat);
		} else {
			if (WebUtil.strIsEmpty(startTime) && WebUtil.strIsEmpty(endTime)) {
				startTime = DateUtil.getPastDate(7);
			} else if (WebUtil.strIsEmpty(startTime) && (!WebUtil.strIsEmpty(endTime))) {
				startTime = DateUtil.getFutureDate(30);
			}
		}
		return startTime;
	}

	public static String getEndTime(String startTime, String endTime, Integer flat) {
		if (flat != null) {
			endTime = DateUtil.getToDay();
		} else {
			if (WebUtil.strIsEmpty(startTime) && WebUtil.strIsEmpty(endTime)) {
				endTime = DateUtil.getToDay();
			} else if (!WebUtil.strIsEmpty(startTime) && WebUtil.strIsEmpty(endTime)) {
				endTime = DateUtil.getFutureDate(30);
			}
		}
		return endTime;
	}

	public static String getRtn(String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
		String userAgent = request.getHeader("User-Agent");
		String rtn = "filename=\"" + fileName + "\"";
		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
			// IE浏览器，只能采用URLEncoder编码
			if (userAgent.contains("msie")) {
				rtn = "filename=\"" + fileName + "\"";
			}
			// Opera浏览器只能采用filename*
			else if (userAgent.contains("opera")) {
				rtn = "filename*=UTF-8''" + fileName;
			}
			// Safari浏览器，只能采用ISO编码的中文输出
			else if (userAgent.contains("safari")) {
				rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
			} else if (userAgent.contains("mozilla")) {
				rtn = "filename*=UTF-8''" + fileName;
			}
		}
		return rtn;
	}
}
