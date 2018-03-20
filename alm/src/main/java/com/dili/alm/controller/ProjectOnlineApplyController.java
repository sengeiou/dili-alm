package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.dto.apply.ApplyMajorResource;
import com.dili.alm.service.ProjectOnlineApplyService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.google.common.collect.Lists;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-13 15:31:10.
 */
@Api("/projectOnlineApply")
@Controller
@RequestMapping("/projectOnlineApply")
public class ProjectOnlineApplyController {
	@Autowired
	ProjectOnlineApplyService projectOnlineApplyService;

	@ApiOperation("跳转到ProjectOnlineApply页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "projectOnlineApply/index";
	}

	@ApiOperation(value = "查询ProjectOnlineApply", notes = "查询ProjectOnlineApply，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectOnlineApply", paramType = "form", value = "ProjectOnlineApply的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectOnlineApply> list(ProjectOnlineApply projectOnlineApply) {
		return projectOnlineApplyService.list(projectOnlineApply);
	}

	@ApiOperation(value = "分页查询ProjectOnlineApply", notes = "分页查询ProjectOnlineApply，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectOnlineApply", paramType = "form", value = "ProjectOnlineApply的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(ProjectOnlineApplyListQueryDto projectOnlineApply) throws Exception {
		return projectOnlineApplyService.listEasyuiPageByExample(projectOnlineApply, true).toString();
	}

	@ApiOperation("新增ProjectOnlineApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectOnlineApply", paramType = "form", value = "ProjectOnlineApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(ProjectOnlineApply projectOnlineApply) {
		projectOnlineApplyService.insertSelective(projectOnlineApply);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改ProjectOnlineApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectOnlineApply", paramType = "form", value = "ProjectOnlineApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(ProjectOnlineApply projectOnlineApply) {
		projectOnlineApplyService.updateSelective(projectOnlineApply);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除ProjectOnlineApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "ProjectOnlineApply的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		projectOnlineApplyService.delete(id);
		return BaseOutput.success("删除成功");
	}
}