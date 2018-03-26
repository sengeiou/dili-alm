package com.dili.alm.controller;

import java.util.Date;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	public static final Long WORKDAYID=1L;
	@ApiOperation("修改WorkDay")
    @ApiImplicitParams({
		@ApiImplicitParam(name="WorkDay", paramType="form", value = "WorkDay的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(WorkDay workDay) {
		workDay.setId(WORKDAYID);
		workDayService.updateSelective(workDay);
        return BaseOutput.success("修改成功");
    }

    @ApiImplicitParams({
		@ApiImplicitParam(name="WorkDay", paramType="form", value = "WorkDay的form信息", required = true, dataType = "string")
	})
    @ResponseBody
    @RequestMapping(value="/isWorkEndDayDate", method = {RequestMethod.GET, RequestMethod.POST})
    public boolean isWorkEndDayDate() {
		WorkDay workDay = workDayService.get(WORKDAYID);
        int differentDaysByMillisecond = DateUtil.differentDaysByMillisecond(new Date(), workDay.getWorkEndTime());
        if(differentDaysByMillisecond==0){
        	return true;
        }
       return false;
    }
}
