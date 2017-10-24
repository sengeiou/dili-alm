package com.dili.alm.controller;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.quartz.domain.ScheduleJob;
import com.dili.ss.quartz.service.ScheduleJobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-10-24 09:32:32.
 */
@Api("/scheduleJob")
@Controller
@RequestMapping("/scheduleJob")
public class ScheduleJobController {

	protected static final Logger log = LoggerFactory.getLogger(ScheduleJobController.class);
    @Autowired
    ScheduleJobService scheduleJobService;

	/**
	 * 动态刷新调度器
	 * @return
	 */
	@RequestMapping("/refreshAll.aspx")
	public @ResponseBody BaseOutput refreshAll(){
		scheduleJobService.refresh(scheduleJobService.list(null));
		return BaseOutput.success("调度器刷新完成");
	}

    @ApiOperation("跳转到ScheduleJob页面")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "scheduleJob/index";
    }

    @ApiOperation(value="查询ScheduleJob", notes = "查询ScheduleJob，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ScheduleJob", paramType="form", value = "ScheduleJob的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<ScheduleJob> list(ScheduleJob scheduleJob) {
        return scheduleJobService.list(scheduleJob);
    }

    @ApiOperation(value="分页查询ScheduleJob", notes = "分页查询ScheduleJob，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ScheduleJob", paramType="form", value = "ScheduleJob的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(ScheduleJob scheduleJob) throws Exception {
        return scheduleJobService.listEasyuiPageByExample(scheduleJob, true).toString();
    }

    @ApiOperation("新增ScheduleJob")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ScheduleJob", paramType="form", value = "ScheduleJob的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(ScheduleJob scheduleJob) {
        scheduleJobService.insertSelective(scheduleJob);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改ScheduleJob")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ScheduleJob", paramType="form", value = "ScheduleJob的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(ScheduleJob scheduleJob) {
        scheduleJobService.updateSelective(scheduleJob);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除ScheduleJob")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "ScheduleJob的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        scheduleJobService.delete(id);
        return BaseOutput.success("删除成功");
    }


}