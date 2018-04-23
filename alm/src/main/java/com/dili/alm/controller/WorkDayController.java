package com.dili.alm.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.WorkDay;
import com.dili.alm.service.WorkDayService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.domain.BaseOutput;

@Api("/workDay")
@Controller
@RequestMapping("/workDay")
public class WorkDayController {
	@Autowired
	private WorkDayService workDayService;


    @ApiImplicitParams({
		@ApiImplicitParam(name="WorkDay", paramType="form", value = "WorkDay的form信息", required = true, dataType = "string")
	})
    @ResponseBody
    @RequestMapping(value="/isWorkEndDayDate", method = {RequestMethod.GET, RequestMethod.POST})
    public int isWorkEndDayDate() {
		WorkDay workDay = workDayService.getNowWeeklyWorkDay();
		if(workDay==null){
			return 1;
		}
        int differentDaysByMillisecond = DateUtil.differentDaysByMillisecond(new Date(), workDay.getWorkEndTime());
        if(differentDaysByMillisecond==0){
        	return 2;
        }
       return 0;
    }
    
    @ApiImplicitParams({
		@ApiImplicitParam(name="WorkDay", paramType="form", value = "WorkDay的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/uploadWordDayDate", method = {RequestMethod.GET, RequestMethod.POST})
    public  @ResponseBody BaseOutput  uploadWordDayDate(@RequestParam("file") MultipartFile file,String year) {
    	return workDayService.upload(file,year);	
    }
    
    
}
