package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.WorkSchedule;
import com.dili.alm.domain.WorkScheduleEntity;
import com.dili.alm.domain.dto.WorkScheduleDateDto;
import com.dili.alm.service.WorkScheduleService;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:19:21.
 */
@Api("/workSchedule")
@Controller
@RequestMapping("/workSchedule")
public class WorkScheduleController {
    @Autowired
    WorkScheduleService workScheduleService;

    @ApiOperation("跳转到WorkSchedule页面")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "workSchedule/index";
    }

    @ApiOperation(value="查询WorkSchedule", notes = "查询WorkSchedule，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="WorkSchedule", paramType="form", value = "WorkSchedule的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<WorkSchedule> list(WorkSchedule workSchedule) {
        return workScheduleService.list(workSchedule);
    }

    @ApiOperation(value="分页查询WorkSchedule", notes = "分页查询WorkSchedule，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="WorkSchedule", paramType="form", value = "WorkSchedule的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(WorkSchedule workSchedule) throws Exception {
        return workScheduleService.listEasyuiPageByExample(workSchedule, true).toString();
    }
    
	@ResponseBody
	@RequestMapping(value = "/getWorkSchedule.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<WorkScheduleEntity> getWorkSchedule(WorkSchedule workSchedule) throws Exception {
			UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
			workSchedule.setUserId(userTicket.getId());
			return workScheduleService.listWorkScheduleDto(workSchedule);
		}
	
	@ResponseBody
	@RequestMapping(value = "/getWorkScheduleDate.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<WorkScheduleDateDto> getWorkScheduleDate(WorkSchedule workSchedule) throws Exception {
			UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
			workSchedule.setUserId(userTicket.getId());
			return workScheduleService.listWorkScheduleDate(workSchedule);
		}
	
	
    @ApiOperation("新增WorkSchedule")
    @ApiImplicitParams({
		@ApiImplicitParam(name="WorkSchedule", paramType="form", value = "WorkSchedule的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(WorkSchedule workSchedule) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		workSchedule.setUserId(userTicket.getId());
    	workScheduleService.insertSelective(workSchedule);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改WorkSchedule")
    @ApiImplicitParams({
		@ApiImplicitParam(name="WorkSchedule", paramType="form", value = "WorkSchedule的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(WorkSchedule workSchedule) {
        workScheduleService.updateSelective(workSchedule);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除WorkSchedule")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "WorkSchedule的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        workScheduleService.delete(id);
        return BaseOutput.success("删除成功");
    }
    
    @ApiOperation("根据Id查询WorkSchedule")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "WorkSchedule的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/getWorkScheduleById", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody WorkSchedule getWorkScheduleById(Long id)  {
        return workScheduleService.get(id);
    }
}