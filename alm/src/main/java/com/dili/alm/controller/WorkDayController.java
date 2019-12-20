package com.dili.alm.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dili.alm.domain.dto.WorkDayRoleDto;
import com.dili.alm.service.WorkDayService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

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
    	UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();

		if (userTicket == null) {
			return 2;
		}
		BaseOutput<WorkDayRoleDto> showWorkDay = workDayService.showWorkDay(userTicket.getId());
		if("false".equals(showWorkDay.getCode())){
			return 3;
		}
		WorkDayRoleDto workDayRoleDto = showWorkDay.getData();
		if(workDayRoleDto.getId()==null){
			return 1;
		}
        int differentDaysByMillisecond = DateUtil.differentDaysByMillisecond(new Date(), workDayRoleDto.getWorkEndTime());
        if(differentDaysByMillisecond==0){
        	return 4;
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
