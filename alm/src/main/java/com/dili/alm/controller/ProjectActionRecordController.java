package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.ProjectActionRecord;
import com.dili.alm.service.ProjectActionRecordService;
import com.dili.ss.domain.BaseOutput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-06-12 14:36:05.
 */
@Api("/projectActionRecord")
@Controller
@RequestMapping("/projectActionRecord")
public class ProjectActionRecordController {
    @Autowired
    ProjectActionRecordService projectActionRecordService;

    @ApiOperation("跳转到ProjectActionRecord页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "projectActionRecord/index";
    }

    @ApiOperation(value="查询ProjectActionRecord", notes = "查询ProjectActionRecord，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ProjectActionRecord", paramType="form", value = "ProjectActionRecord的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<ProjectActionRecord> list(ProjectActionRecord projectActionRecord) {
        return projectActionRecordService.list(projectActionRecord);
    }

    @ApiOperation(value="分页查询ProjectActionRecord", notes = "分页查询ProjectActionRecord，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ProjectActionRecord", paramType="form", value = "ProjectActionRecord的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(ProjectActionRecord projectActionRecord) throws Exception {
        return projectActionRecordService.listEasyuiPageByExample(projectActionRecord, true).toString();
    }

    @ApiOperation("新增ProjectActionRecord")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ProjectActionRecord", paramType="form", value = "ProjectActionRecord的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(ProjectActionRecord projectActionRecord) {
        projectActionRecordService.insertSelective(projectActionRecord);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改ProjectActionRecord")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ProjectActionRecord", paramType="form", value = "ProjectActionRecord的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(ProjectActionRecord projectActionRecord) {
        projectActionRecordService.updateSelective(projectActionRecord);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除ProjectActionRecord")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "ProjectActionRecord的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        projectActionRecordService.delete(id);
        return BaseOutput.success("删除成功");
    }
}