package com.dili.alm.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.ProjectTypeCountDto;
import com.dili.alm.domain.dto.ProjectYearCoverDto;
import com.dili.alm.domain.dto.ProjectYearCoverForAllDto;
import com.dili.alm.domain.dto.SelectTaskHoursByUserDto;
import com.dili.alm.domain.dto.TaskByUsersDto;
import com.dili.alm.domain.dto.TaskHoursByProjectDto;
import com.dili.alm.domain.dto.TaskStateCountDto;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.StatisticalService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
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
	public @ResponseBody List<ProjectYearCoverDto> listProjectYearCover(String year, String month) throws Exception {

		return statisticalService.listProjectYearCover(year, month);

	}

	@ApiOperation(value = "报表图显示", notes = "查询返回easyui信息")
	@RequestMapping(value = "/projectYearCoverForAll", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ProjectYearCoverForAllDto projectYearCoverForAll(String year, String month) throws Exception {
		ProjectYearCoverForAllDto qq = statisticalService.getProjectYearsCoverForAll(year, month);
		return qq;
	}

	@ApiOperation(value = "返回查询后的日期", notes = "查询返回easyui信息")
	@RequestMapping(value = "/getSearchDate.json", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String getSearchDate(String year, String month) throws Exception {

		return statisticalService.getSearchDate(year, month);

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
		projectIds.add((long) 15);
		projectIds.add((long) 23);
		startDate = "2018-03-14";
		endDate = "2018-04-11";
		return statisticalService.listProjectHours(startDate, endDate, projectIds);

	}

	@ApiOperation(value = "项目工时图表查询", notes = "查询返回easyui信息")
	@RequestMapping(value = "/listProjectForEchar", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody SelectTaskHoursByUserDto listProjectForEchar(String[] projectIds, Long userId)
			throws Exception {
		List<Long> projectIdList = null;
		if (projectIds != null) {
			projectIdList = new ArrayList<Long>(projectIds.length);
			for (String long1 : projectIds) {
				projectIdList.add(Long.parseLong(long1));
			}
		}
		return statisticalService.selectTotalTaskAndOverHours(projectIdList);

	}

	@ResponseBody
	@RequestMapping(value = "/test", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<Map<Object,Object>> test() throws ParseException {
		List<Long> projectIds = new ArrayList<>();
		List<Project> projects = this.projectService.list(DTOUtils.newDTO(Project.class));
		projects.forEach(p -> projectIds.add(p.getId()));
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return this.taskMapper.sumUserProjectTaskHour(projectIds, df.parse("2018-02-01"), df.parse("2018-05-01"));
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
		projectIds.add((long) 15);
		projectIds.add((long) 23);
		startDate = "2018-03-14";
		endDate = "2018-04-11";
		return statisticalService.listUserHours(startDate, endDate, projectIds);

	}

	@RequestMapping(value = "/export", method = { RequestMethod.POST, RequestMethod.GET })
	public void export(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws IOException {
		try {
			String rtn = getRtn("aa.xls", request);
			response.setContentType("text/html");
			response.setHeader("Content-Disposition", "attachment;" + rtn);
			OutputStream os = response.getOutputStream();

			HSSFWorkbook excel = statisticalService.downloadProjectHours(os, null, null, null);
			excel.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		InputStream stream = getClass().getClassLoader().getResourceAsStream("excel/aa.xls");
		File targetFile = new File("aa.xls");
		if (!targetFile.exists()) {
			FileUtils.copyInputStreamToFile(stream, targetFile);
		}
		HSSFWorkbook excel = (HSSFWorkbook) ExcelExportUtil.exportExcel(list, ExcelType.HSSF);
		excel.write(os);
		os.flush();
		os.close();
	}

	/*** 查询工时相关services****by******JING***END ****/

	@ApiOperation(value = "查询项目进展总汇", notes = "查询返回easyui信息")
	@RequestMapping(value = "/ProjectProgressList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String ProjectProgressList(Project project, String startTime, String endTime, Integer flat,
			String ids) throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		List<Long> list = new ArrayList<Long>();
		if (!WebUtil.strIsEmpty(ids)) {
			String[] split = ids.split(",");
			for (String string : split) {
				list.add(Long.parseLong(string));
			}
		}
		return statisticalService.getProjectProgresstDTO(project, startTime2, endTime2, list).toString();

	}

	@ApiOperation(value = "查询所有", notes = "查询返回List信息")
	@RequestMapping(value = "/projecTypetList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectTypeCountDto> projecTypetList(String startTime, String endTime, Integer flat)
			throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		return statisticalService.getProjectToTypeSummary(startTime2, endTime2);

	}

	@ApiOperation(value = "查询项目类型统计", notes = "查询返回List信息")
	@RequestMapping(value = "/projectList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Project> projectList(Project project) throws Exception {
		return projectService.list(project);

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
