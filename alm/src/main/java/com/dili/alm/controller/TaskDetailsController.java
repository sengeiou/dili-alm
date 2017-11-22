package com.dili.alm.controller;

import com.dili.alm.domain.TaskDetails;
import com.dili.alm.service.TaskDetailsService;
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
 * This file was generated on 2017-11-22 16:02:19.
 */
@Api("/taskDetails")
@Controller
@RequestMapping("/taskDetails")
public class TaskDetailsController {
    @Autowired
    TaskDetailsService taskDetailsService;

    @ApiOperation("跳转到TaskDetails页面")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "taskDetails/index";
    }

    @ApiOperation(value="查询TaskDetails", notes = "查询TaskDetails，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskDetails", paramType="form", value = "TaskDetails的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<TaskDetails> list(TaskDetails taskDetails) {
        return taskDetailsService.list(taskDetails);
    }

    @ApiOperation(value="分页查询TaskDetails", notes = "分页查询TaskDetails，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskDetails", paramType="form", value = "TaskDetails的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(TaskDetails taskDetails) throws Exception {
        return taskDetailsService.listEasyuiPageByExample(taskDetails, true).toString();
    }

    @ApiOperation("新增TaskDetails")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskDetails", paramType="form", value = "TaskDetails的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(TaskDetails taskDetails) {
        taskDetailsService.insertSelective(taskDetails);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改TaskDetails")
    @ApiImplicitParams({
		@ApiImplicitParam(name="TaskDetails", paramType="form", value = "TaskDetails的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(TaskDetails taskDetails) {
        taskDetailsService.updateSelective(taskDetails);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除TaskDetails")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "TaskDetails的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        taskDetailsService.delete(id);
        return BaseOutput.success("删除成功");
    }
}