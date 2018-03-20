package com.dili.alm.controller;


import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.dto.ProjectProgressDto;
import com.dili.alm.domain.dto.ProjectTypeCountDTO;
import com.dili.alm.domain.dto.TaskStateCountDto;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.StatisticalService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.TaskByUsersDto;












import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;













import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
		if(flat!=null){
			startTime = DateUtil.getPastDate(flat);
			endTime = DateUtil.getToDay();
		}else{
			if(WebUtil.strIsEmpty(startTime)&&WebUtil.strIsEmpty(endTime)){
				startTime = DateUtil.getPastDate(7);
				endTime = DateUtil.getToDay();
			}else if(!WebUtil.strIsEmpty(startTime)&&WebUtil.strIsEmpty(endTime)){
				endTime =DateUtil.getFutureDate(30);
			}else if(WebUtil.strIsEmpty(startTime)&&(!WebUtil.strIsEmpty(endTime))){
				startTime =DateUtil.getFutureDate(30);
			}
		}
		return statisticalService.getProjectTypeCountDTO(startTime, endTime).toString();
   
    }
	


	

	@ApiOperation(value="查询项目任务数量", notes = "查询返回List信息")
    @RequestMapping(value="/ProjectOverviewTasklist", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskStateCountDto> ProjectOverviewTasklist(String startTime,String endTime,Integer flat) throws Exception {
		if(flat!=null){
			startTime = DateUtil.getPastDate(flat);
			endTime = DateUtil.getToDay();
		}else{
			if(WebUtil.strIsEmpty(startTime)&&WebUtil.strIsEmpty(endTime)){
				startTime = DateUtil.getPastDate(7);
				endTime = DateUtil.getToDay();
			}else if(!WebUtil.strIsEmpty(startTime)&&WebUtil.strIsEmpty(endTime)){
				endTime =DateUtil.getFutureDate(30);
			}else if(WebUtil.strIsEmpty(startTime)&&(!WebUtil.strIsEmpty(endTime))){
				startTime =DateUtil.getFutureDate(30);
			}
		}
		return statisticalService.getProjectToTaskCount(startTime, endTime);

    }
	
	/***查询工时相关services****by******JING***BEGIN****/
	
	@ApiOperation(value="查询时间段内员工工时", notes = "查询返回easyui信息")
    @RequestMapping(value="/taskHoursByUser", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskByUsersDto> taskHoursByUser(String startTime,String endTime,Long userId,Long departmentId) throws Exception {

		return statisticalService.listTaskHoursByUser(startTime, endTime, userId, departmentId);
   
    }
	
	/***查询工时相关services****by******JING***END****/
	
	
	@ApiOperation(value="查询项目进展总汇", notes = "查询返回easyui信息")
    @RequestMapping(value="/ProjectProgressList", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String ProjectProgressList(Project project,String startTime,String endTime,Integer flat,String ids) throws Exception {
		if(flat!=null){
			startTime = DateUtil.getPastDate(flat);
			endTime = DateUtil.getToDay();
		}else{
			if(WebUtil.strIsEmpty(startTime)&&WebUtil.strIsEmpty(endTime)){
				startTime = DateUtil.getPastDate(7);
				endTime = DateUtil.getToDay();
			}else if(!WebUtil.strIsEmpty(startTime)&&WebUtil.strIsEmpty(endTime)){
				endTime =DateUtil.getFutureDate(30);
			}else if(WebUtil.strIsEmpty(startTime)&&(!WebUtil.strIsEmpty(endTime))){
				startTime =DateUtil.getFutureDate(30);
			}
		}
		List<Long> list=new ArrayList<Long>();
		if(!WebUtil.strIsEmpty(ids)){
			String[] split = ids.split(",");
			for (String string : split) {
				list.add(Long.parseLong(string));
			}
		}
		return statisticalService.getProjectProgresstDTO(project,startTime, endTime, list).toString();
   
    }
	
	@ApiOperation(value="查询所有", notes = "查询返回List信息")
    @RequestMapping(value="/projecTypetList", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<ProjectTypeCountDTO> projecTypetList(String startTime,String endTime,Integer flat) throws Exception {
		if(flat!=null){
			startTime = DateUtil.getPastDate(flat);
			endTime = DateUtil.getToDay();
		}else{
			if(WebUtil.strIsEmpty(startTime)&&WebUtil.strIsEmpty(endTime)){
				startTime = DateUtil.getPastDate(7);
				endTime = DateUtil.getToDay();
			}else if(!WebUtil.strIsEmpty(startTime)&&WebUtil.strIsEmpty(endTime)){
				endTime =DateUtil.getFutureDate(30);
			}else if(WebUtil.strIsEmpty(startTime)&&(!WebUtil.strIsEmpty(endTime))){
				startTime =DateUtil.getFutureDate(30);
			}
		}
		return statisticalService.getProjectToTypeSummary(startTime, endTime); 

    }
	
	@ApiOperation(value="查询项目类型统计", notes = "查询返回List信息")
    @RequestMapping(value="/projectList", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Project> projectList(Project project) throws Exception {
		return projectService.list(project);

    }
}
