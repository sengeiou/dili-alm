package com.dili.alm.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.ProjectCostStatisticDto;
import com.dili.alm.domain.dto.ProjectHoursStatisticsDto;
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
import com.dili.alm.domain.dto.UserProjectHoursStatisticsDto;
import com.dili.alm.domain.dto.UserProjectTaskHourCountDto;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.StatisticalService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.util.DateUtils;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.handler.inter.IExcelDictHandler;



/**
 * ???MyBatis Generator?????????????????? This file was generated on 2017-10-18 17:22:54.
 */
@Controller
@RequestMapping("/statistical")
public class StatisticalController {

	private static final String DATA_AUTH_TYPE = "project";
	private static final Logger LOGGER = LoggerFactory.getLogger(StatisticalController.class);
	@Autowired
	private StatisticalService statisticalService;

	@Autowired
	private ProjectService projectService;
	@Autowired
	private TaskMapper taskMapper;

	@RequestMapping(value = "/projectOverview", method = RequestMethod.GET)
	public String projectOverview(ModelMap modelMap) {
		return "statistical/projectOverview";
	}

	@RequestMapping(value = "/projectProgress", method = RequestMethod.GET)
	public String projectProgress(ModelMap modelMap) {
		return "statistical/projectProgress";
	}

	@RequestMapping(value = "/projectType", method = RequestMethod.GET)
	public String projectType(ModelMap modelMap) {
		return "statistical/projectType";
	}

	@RequestMapping(value = "/taskHoursForUser", method = RequestMethod.GET)
	public String taskHoursForUser(ModelMap modelMap) {
		return "statistical/taskHoursForUser";
	}

	@RequestMapping(value = "/taskHoursForProject", method = RequestMethod.GET)
	public String taskHoursForProject(ModelMap modelMap) {
		return "statistical/taskHoursForProject";
	}

	@RequestMapping(value = "/projectYearCover", method = RequestMethod.GET)
	public String projectYearCover(ModelMap modelMap) {
		return "statistical/projectYearCover";
	}

	@RequestMapping(value = "/ProjectOverviewlist", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String ProjectOverviewlist(String startTime, String endTime, Integer flat) throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		return statisticalService.getProjectTypeCountDTO(startTime2, endTime2).toString();

	}

	@RequestMapping(value = "/ProjectOverviewTasklist", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<TaskStateCountDto> ProjectOverviewTasklist(String startTime, String endTime, Integer flat) throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		return statisticalService.getProjectToTaskCount(startTime2, endTime2);

	}

	/***
	 * ??????????????????services****by******JING***BEGIN
	 * 
	 * @param order
	 * @param sort
	 ****/

	@RequestMapping(value = "/taskHoursByUser", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<TaskByUsersDto> taskHoursByUser(String startTime, String endTime, String[] userId, String[] departmentId, @RequestParam(required = false) String order,
			@RequestParam(required = false) String sort) throws Exception {

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
		List<TaskByUsersDto> taskByUserDtoList = statisticalService.listTaskHoursByUser(startTime, endTime, userIds, departmentIds, order, sort);
		// return new EasyuiPageOutput(taskByUserDtoList.size(),
		// taskByUserDtoList).toString();
		return taskByUserDtoList;

	}

	@RequestMapping(value = "/listProjectYearCover", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectYearCoverDto> listProjectYearCover(String year, String month, String aaa, String weekNum) throws Exception {
		return statisticalService.listProjectYearCover(year, month, weekNum);

	}

	@RequestMapping(value = "/projectYearCoverForAll", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ProjectYearCoverForAllDto projectYearCoverForAll(String year, String month, boolean isFullYear, String weekNum) throws Exception {
		ProjectYearCoverForAllDto all = statisticalService.getProjectYearsCoverForAll(year, month, isFullYear, weekNum);
		return all;
	}

	@RequestMapping(value = "/getYears.json", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<SelectYearsDto> getYears() throws Exception {
		List<SelectYearsDto> selectYears = new ArrayList<SelectYearsDto>();
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int beginYear = year - 5;
		int endYear = year + 5;
		for (int i = beginYear; i <= endYear; i++) {
			SelectYearsDto dto = new SelectYearsDto();
			dto.setText(i + "");
			dto.setValue(i + "");
			selectYears.add(dto);
		}
		return selectYears;

	}

	@RequestMapping(value = "/getWeekNum.json", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String getWeekNum(String year, String month) throws Exception {
		if (year == null || month == null) {
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

		int size = DateUtil.getWeeks(yearIntager, monthIntager).size() / 2;// ??????????????????????????????key?????????
		return String.valueOf(size);

	}

	@RequestMapping(value = "/getSearchDate.json", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String getSearchDate(String year, String month, String weekNum) throws Exception {
		String returnStr = JSONObject.toJSONString(statisticalService.getSearchDate(year, month, weekNum));
		return returnStr;

	}

	@RequestMapping(value = "/listProjectHours", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<TaskHoursByProjectDto> getSearchAllDto(String[] project) throws Exception {
		List<Long> projectIds = new ArrayList<Long>();
		if (project != null) {
			projectIds = new ArrayList<Long>(project.length);
			for (String long1 : project) {
				projectIds.add(Long.parseLong(long1));
			}
		}
		return statisticalService.listProjectHours(null, null, projectIds);

	}

	@RequestMapping(value = "/listProjectForEchar", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<SelectTaskHoursByUserProjectDto> listProjectForEchar(String[] projectIds, Long userId, Date startDate, Date endDate) throws Exception {
		List<Long> projectIdList = new ArrayList<Long>();
		if (projectIds != null && projectIds.length > 0) {
			projectIdList = new ArrayList<Long>(projectIds.length);
			for (String long1 : projectIds) {
				projectIdList.add(Long.parseLong(long1));
			}
		} else {
			projectIdList.add(Long.getLong(String.valueOf(-1)));
		}
		return statisticalService.selectTotalTaskAndOverHoursForEchars(projectIdList, startDate, endDate);

	}

	/*
	 * ??????????????????????????????????????????list bean??????????????????
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

	@RequestMapping(value = "/listUserHours", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<SelectTaskHoursByUserDto> listUserHours(String startDate, String endDate, String[] project) throws Exception {
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
	public @ResponseBody BaseOutput export(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap, String startDate, String endDate, String[] project) throws IOException {
		try {
			String rtn = getRtn("??????????????????.xls", request);
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
			return BaseOutput.failure("????????????");
		}
		return BaseOutput.success("????????????");
	}

	/*** ??????????????????services****by******JING***END ****/

	@RequestMapping(value = "/ProjectProgressList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String ProjectProgressList(Project project, Integer flat, String ids) throws Exception {
		Integer f = 0;
		List<Integer> list = new ArrayList<Integer>();
		if (!WebUtil.strIsEmpty(ids)) {
			String[] split = ids.split(",");
			for (String string : split) {
				list.add(Integer.parseInt(string));
			}
		}
		return statisticalService.getProjectProgresstDTO(project, list, f).toString();

	}

	@RequestMapping(value = "/projecTypetList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectTypeCountDto> projecTypetList(String startTime, String endTime, Integer flat) throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		return statisticalService.getProjectToTypeSummary(startTime2, endTime2);

	}

	@RequestMapping(value = "/projectList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Project> projectList(Project project) throws Exception {
		return projectService.list(project);

	}

	@RequestMapping(value = "/projectStateList", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map<String, String>> projectStateList() throws Exception {
		return projectService.projectStateList();

	}

	@RequestMapping(value = "/projecOverViewDownload", method = { RequestMethod.GET, RequestMethod.POST })
	public void projecOverViewDownload(String startTime, String endTime, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String startTime2 = getStartTime(startTime, endTime, null);
		String endTime2 = getEndTime(startTime, endTime, null);
		String fileName = "????????????.xls";
		// ????????????IE?????????????????????
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
			throw new RuntimeException("?????????");
		}
		Long userId = userTicket.getId();
		if (!WebUtil.strIsEmpty(flat) && flat.equals("1")) {
			@SuppressWarnings({ "rawtypes" })
			List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
			if (CollectionUtils.isEmpty(dataAuths)) {
				return null;
			}
			dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("value").toString())));
			userId = null;
		}
		return statisticalService.getHomeProject(userId, projectIds);
	}

	@RequestMapping(value = "/homeTaskCount", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map<String, Object>> homeTaskCount(String flat) {
		List<Long> projectIds = new ArrayList<>();
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("?????????");
		}
		Long userId = userTicket.getId();
		if (!WebUtil.strIsEmpty(flat) && flat.equals("1")) {
			@SuppressWarnings({ "rawtypes" })
			List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
			if (CollectionUtils.isEmpty(dataAuths)) {
				return null;
			}
			dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("value").toString())));
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
			// IE????????????????????????URLEncoder??????
			if (userAgent.contains("msie")) {
				rtn = "filename=\"" + fileName + "\"";
			}
			// Opera?????????????????????filename*
			else if (userAgent.contains("opera")) {
				rtn = "filename*=UTF-8''" + fileName;
			}
			// Safari????????????????????????ISO?????????????????????
			else if (userAgent.contains("safari")) {
				rtn = "filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO8859-1") + "\"";
			} else if (userAgent.contains("mozilla")) {
				rtn = "filename*=UTF-8''" + fileName;
			}
		}
		return rtn;
	}

	@GetMapping("/projectCostStatistic.html")
	public String projectCostStatisticView(ModelMap map) {
		map.addAttribute("projects", AlmCache.getInstance().getProjectMap().values());
		return "statistical/projectCostStatistic";
	}

	@ResponseBody
	@RequestMapping("/projectCostStatistic")
	public List<ProjectCostStatisticDto> projectCostStatistic(@RequestParam(required = false) Long projectId) {
		if (projectId <= 0) {
			return this.projectService.projectCostStatistic(null);
		}
		return this.projectService.projectCostStatistic(projectId);
	}

	@ResponseBody
	@RequestMapping(value = "/exportProjectCost")
	public void exportProjectCost(@RequestParam(required = false) Long projectId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		InputStream stream = getClass().getClassLoader().getResourceAsStream("excel/projectCostStatisticTemplate.xlsx");
		File targetFile = new File("projectCostStatisticTemplate.xlsx");
		if (!targetFile.exists()) {
			FileUtils.copyInputStreamToFile(stream, targetFile);
		}
		TemplateExportParams params = new TemplateExportParams(targetFile.getPath());
		List<ProjectCostStatisticDto> list = this.projectService.projectCostStatistic(projectId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectCosts", this.buildProjectCostListMap(list));

		Workbook workbook = ExcelExportUtil.exportExcel(params, map);
		OutputStream os = null;
		try {
			response.setHeader("Content-Type", "Content-Type: application/vnd.ms-excel;charset=UTF-8");
			response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode("??????????????????", "UTF-8") + ".xlsx");
			os = response.getOutputStream();
			workbook.write(os);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	@GetMapping("/userProjectHoursStatistics.html")
	public String userProjectHoursStatistics() {
		return "statistical/userProjectHoursStatistics";
	}

	@ResponseBody
	@RequestMapping("/userProjectHoursStatistics")
	public List<UserProjectHoursStatisticsDto> userProjectHoursStatistics(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long departmentId,
			@RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = StringUtils.isBlank(startTime) ? DateUtil.getCurrYearFirst() : df.parse(startTime);
		if (StringUtils.isBlank(endTime)) {
			endTime = df.format(new Date());
		}
		Date endDate = StringUtils.isBlank(endTime) ? new Date() : df.parse(endTime);
		return this.statisticalService.userProjectHoursStatistics(userId, departmentId, startDate, endDate);
	}

	@ResponseBody
	@RequestMapping("/exportUserProjectHoursStatistics")
	public void exportUserProjectHoursStatistics(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long departmentId, @RequestParam(required = false) String startTime,
			@RequestParam(required = false) String endTime, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException, ParseException {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = StringUtils.isBlank(startTime) ? DateUtil.getCurrYearFirst() : df.parse(startTime);
		if (StringUtils.isBlank(endTime)) {
			endTime = df.format(new Date());
		}
		Date endDate = StringUtils.isBlank(endTime) ? new Date() : df.parse(endTime);
		List<UserProjectHoursStatisticsDto> list = this.statisticalService.userProjectHoursStatistics(userId, departmentId, startDate, endDate);
		list.forEach(up -> {
			if (CollectionUtils.isEmpty(up.getProjectStatistics())) {
				up.setProjectStatistics(new ArrayList<ProjectHoursStatisticsDto>() {
					{
						add(new ProjectHoursStatisticsDto());
					}
				});
			}
		});
		final ExportParams exportParams = new ExportParams("??????????????????", "??????????????????", ExcelType.XSSF);
		exportParams.setDictHandler(new IExcelDictHandler() {
			@Override
			public String toName(String dict, Object obj, String name, Object value) {
				if ("projectType".equals(dict)) {
					return ((ProjectHoursStatisticsDto) obj).getProjectTypeName();
				}
				return null;
			}

			@Override
			public String toValue(String dict, Object obj, String name, Object value) {
				return null;
			}
		});
		Workbook workbook = ExcelExportUtil.exportExcel(exportParams, UserProjectHoursStatisticsDto.class, list);

		if (workbook == null) {
			return;
		}

		String rtn = getRtn("????????????????????????.xlsx", request);
		response.setContentType("text/html");
		response.setHeader("Content-Disposition", "attachment;" + rtn);
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			workbook.write(os);
			os.flush();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
//			ExcelExportUtil.closeExportBigExcel();
			if (os != null) {
				try {
					os.close();
					workbook.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}

		}
	}

	private List<Map<String, String>> buildProjectCostListMap(List<ProjectCostStatisticDto> list) {
		List<Map<String, String>> listMap = new ArrayList<>(list.size());
		list.forEach(pcsd -> {
			listMap.add(new HashMap<String, String>() {
				{
					put("projectName", pcsd.getProjectName());
					put("planHumanCost", pcsd.getPlanHumanCost());
					put("planSoftwareCost", pcsd.getPlanSoftwareCost());
					put("planHardwareCost", pcsd.getPlanHardwareCost());
					put("planTravelCost", pcsd.getPlanTravelCost());
					put("planOtherCost", pcsd.getPlanOtherCost());
					put("planTotalCost", pcsd.getPlanTotalCost());
					put("actualHumanCost", pcsd.getActualHumanCost());
					put("actualSoftwareCost", pcsd.getActualSoftwareCost());
					put("actualHardwareCost", pcsd.getActualHardwareCost());
					put("actualTravelCost", pcsd.getActualTravelCost());
					put("actualOtherCost", pcsd.getActualOtherCost());
					put("actualTotalCost", pcsd.getActualTotalCost());
				}
			});
		});
		return listMap;
	}
}
