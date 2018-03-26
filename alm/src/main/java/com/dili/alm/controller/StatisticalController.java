package com.dili.alm.controller;


import com.dili.alm.domain.dto.ProjectTypeCountDto;
import com.dili.alm.domain.dto.ProjectYearCoverDto;
import com.dili.alm.domain.dto.ProjectYearCoverForAllDto;
import com.dili.alm.domain.dto.TaskByUsersDto;
import com.dili.alm.domain.dto.TaskHoursByProjectDto;
import com.dili.alm.domain.dto.TaskStateCountDto;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.StatisticalService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.alm.domain.Project;




import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;








import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-18 17:22:54.
 */
@Api("/statistical")
@Controller
@RequestMapping("/statistical")
public class StatisticalController {
	@Autowired
	private StatisticalService statisticalService;
	
	@Autowired
	private ProjectService projectService;
	
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
	
	
	@ApiOperation(value="查询项目", notes = "查询返回easyui信息")
    @RequestMapping(value="/ProjectOverviewlist", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String ProjectOverviewlist(String startTime,String endTime,Integer flat) throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		return statisticalService.getProjectTypeCountDTO(startTime2, endTime2).toString();
   
    }
	


	

	@ApiOperation(value="查询项目任务数量", notes = "查询返回List信息")
    @RequestMapping(value="/ProjectOverviewTasklist", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskStateCountDto> ProjectOverviewTasklist(String startTime,String endTime,Integer flat) throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		return statisticalService.getProjectToTaskCount(startTime2, endTime2);

    }
	
	/***查询工时相关services****by******JING***BEGIN****/
	
	@ApiOperation(value="查询时间段内员工工时", notes = "查询返回easyui信息")
    @RequestMapping(value="/taskHoursByUser", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskByUsersDto> taskHoursByUser(String startTime,String endTime,String[] userId,String[] departmentId) throws Exception {
		
		List<Long> userIds =null;
		List<Long> departmentIds=null;
		
		if (userId!=null) {
			userIds = new ArrayList<Long>(userId.length);
			for (String long1 : userId) {
				userIds.add(Long.parseLong(long1));
			}
		}
        if (departmentId!=null){ 
        	departmentIds = new ArrayList<Long>(departmentId.length);
	    	for (String long1 : departmentId) {
	    		departmentIds.add(Long.parseLong(long1));
			}
		}
		List<TaskByUsersDto> taskByUserDtoList = statisticalService.listTaskHoursByUser(startTime, endTime,userIds, departmentIds);
		return taskByUserDtoList;
   
    }
	
	
	@ApiOperation(value="查询年度报表", notes = "查询返回easyui信息")
    @RequestMapping(value="/listProjectYearCover", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<ProjectYearCoverDto> listProjectYearCover(String year,String month) throws Exception {

		return statisticalService.listProjectYearCover(year, month);
   
    }
	
	@ApiOperation(value="报表图显示", notes = "查询返回easyui信息")
    @RequestMapping(value="/projectYearCoverForAll", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody ProjectYearCoverForAllDto projectYearCoverForAll(String year,String month) throws Exception {
		ProjectYearCoverForAllDto qq =statisticalService.getProjectYearsCoverForAll(year, month);
		return qq;
    }
	@ApiOperation(value="返回查询后的日期", notes = "查询返回easyui信息")
    @RequestMapping(value="/getSearchDate.json", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String getSearchDate(String year,String month) throws Exception {

		return statisticalService.getSearchDate(year, month);
   
    }
	
	@ApiOperation(value="项目工时查询", notes = "查询返回easyui信息")
    @RequestMapping(value="/listProjectHours", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskHoursByProjectDto> getSearchAllDto(String startTime,String endTime) throws Exception {

		return statisticalService.listProjectHours(startTime, endTime);
   
    }
	/***查询工时相关services****by******JING***END****/
	
	
	@ApiOperation(value="查询项目进展总汇", notes = "查询返回easyui信息")
    @RequestMapping(value="/ProjectProgressList", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String ProjectProgressList(Project project,String startTime,String endTime,Integer flat,String ids) throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		List<Long> list=new ArrayList<Long>();
		if(!WebUtil.strIsEmpty(ids)){
			String[] split = ids.split(",");
			for (String string : split) {
				list.add(Long.parseLong(string));
			}
		}
		return statisticalService.getProjectProgresstDTO(project,startTime2, endTime2, list).toString();
   
    }
	
	@ApiOperation(value="查询所有", notes = "查询返回List信息")
    @RequestMapping(value="/projecTypetList", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<ProjectTypeCountDto> projecTypetList(String startTime,String endTime,Integer flat) throws Exception {
		String startTime2 = getStartTime(startTime, endTime, flat);
		String endTime2 = getEndTime(startTime, endTime, flat);
		return statisticalService.getProjectToTypeSummary(startTime2, endTime2); 

    }
	
	@ApiOperation(value="查询项目类型统计", notes = "查询返回List信息")
    @RequestMapping(value="/projectList", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Project> projectList(Project project) throws Exception {
		return projectService.list(project);

    }
   @RequestMapping(value="/projecOverViewDownload", method = {RequestMethod.GET, RequestMethod.POST})
    public void projecOverViewDownload(String startTime,String endTime,HttpServletRequest request, HttpServletResponse response) throws Exception {
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
    
    
    
    
    
    
    
    
    
    
    
    
    public static String getStartTime(String startTime,String endTime,Integer flat){
    	if(flat!=null){
			startTime = DateUtil.getPastDate(flat);
		}else{
			if(WebUtil.strIsEmpty(startTime)&&WebUtil.strIsEmpty(endTime)){
				startTime = DateUtil.getPastDate(7);
			}else if(WebUtil.strIsEmpty(startTime)&&(!WebUtil.strIsEmpty(endTime))){
				startTime =DateUtil.getFutureDate(30);
			}
		}
    	return startTime;
    }
    public static String getEndTime(String startTime,String endTime,Integer flat){
    	if(flat!=null){
			endTime = DateUtil.getToDay();
		}else{
			if(WebUtil.strIsEmpty(startTime)&&WebUtil.strIsEmpty(endTime)){
				endTime = DateUtil.getToDay();
			}else if(!WebUtil.strIsEmpty(startTime)&&WebUtil.strIsEmpty(endTime)){
				endTime =DateUtil.getFutureDate(30);
			}
		}
    	return endTime;
    }
    public static String getRtn(String fileName,HttpServletRequest request) throws UnsupportedEncodingException{
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
