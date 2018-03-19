package com.dili.alm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Log;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.TaskByUsersDto;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectQueryDto;
import com.dili.alm.domain.dto.UploadProjectFileDto;
import com.dili.alm.exceptions.ProjectException;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.StatisticalService;
import com.dili.alm.service.TeamService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tk.mybatis.mapper.entity.Example;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-18 17:22:54.
 */
@Api("/statistical")
@Controller
@RequestMapping("/statistical")
public class StatisticalController {
	@Autowired
	private StatisticalService statisticalService;
	
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
	
	
	@ApiOperation(value="查询时间段内员工工时", notes = "查询返回easyui信息")
    @RequestMapping(value="/taskHoursByUser", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskByUsersDto> taskHoursByUser(String startTime,String endTime,Long userId,Long departmentId) throws Exception {

		return statisticalService.listTaskHoursByUser(startTime, endTime, userId, departmentId);
   
    }
	
	@ApiOperation(value="查询时间段内每个项目员工工时", notes = "查询返回easyui信息")
    @RequestMapping(value="/taskHoursByProject", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskDetails> taskHoursByProject(String startTime,String endTime,Long userId,Long departmentId) throws Exception {

		return null;
    }
}