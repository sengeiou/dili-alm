package com.dili.alm.controller;

import com.dili.alm.domain.TaskExecutionInfo;
import com.dili.alm.service.TaskExecutionInfoService;
import com.dili.ss.domain.BaseOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-22 16:02:20.
 */
@Api("/taskExecutionInfo")
@Controller
@RequestMapping("/taskExecutionInfo")
public class TaskExecutionInfoController {
    @Autowired
    TaskExecutionInfoService taskExecutionInfoService;

    @ApiOperation("跳转到TaskExecutionInfo页面")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "taskExecutionInfo/index";
    }

    @ApiOperation(value="查询TaskExecutionInfo", notes = "查询TaskExecutionInfo，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskExecutionInfo", paramType="form", value = "TaskExecutionInfo的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskExecutionInfo> list(TaskExecutionInfo taskExecutionInfo) {
        return taskExecutionInfoService.list(taskExecutionInfo);
    }

    @ApiOperation(value="分页查询TaskExecutionInfo", notes = "分页查询TaskExecutionInfo，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskExecutionInfo", paramType="form", value = "TaskExecutionInfo的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(TaskExecutionInfo taskExecutionInfo) throws Exception {
        return taskExecutionInfoService.listEasyuiPageByExample(taskExecutionInfo, true).toString();
    }

    @ApiOperation("新增TaskExecutionInfo")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskExecutionInfo", paramType="form", value = "TaskExecutionInfo的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(TaskExecutionInfo taskExecutionInfo) {
        taskExecutionInfoService.insertSelective(taskExecutionInfo);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改TaskExecutionInfo")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskExecutionInfo", paramType="form", value = "TaskExecutionInfo的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(TaskExecutionInfo taskExecutionInfo) {
        taskExecutionInfoService.updateSelective(taskExecutionInfo);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除TaskExecutionInfo")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "TaskExecutionInfo的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        taskExecutionInfoService.delete(id);
        return BaseOutput.success("删除成功");
    }
}