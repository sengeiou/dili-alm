package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.OnlineSystem;
import com.dili.alm.service.OnlineSystemService;
import com.dili.ss.domain.BaseOutput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-13 15:30:51.
 */
@Api("/onlineSystem")
@Controller
@RequestMapping("/onlineSystem")
public class OnlineSystemController {
	@Autowired
	OnlineSystemService onlineSystemService;

	@ApiOperation("跳转到OnlineSystem页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "onlineSystem/index";
	}

	@ApiOperation(value = "查询OnlineSystem", notes = "查询OnlineSystem，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "OnlineSystem", paramType = "form", value = "OnlineSystem的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<OnlineSystem> list(OnlineSystem onlineSystem) {
		return onlineSystemService.list(onlineSystem);
	}

	@ApiOperation(value = "分页查询OnlineSystem", notes = "分页查询OnlineSystem，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "OnlineSystem", paramType = "form", value = "OnlineSystem的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(OnlineSystem onlineSystem) throws Exception {
		return onlineSystemService.listEasyuiPageByExample(onlineSystem, true).toString();
	}

	@ApiOperation("新增OnlineSystem")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "OnlineSystem", paramType = "form", value = "OnlineSystem的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(OnlineSystem onlineSystem) {
		onlineSystemService.insertSelective(onlineSystem);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改OnlineSystem")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "OnlineSystem", paramType = "form", value = "OnlineSystem的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(OnlineSystem onlineSystem) {
		onlineSystemService.updateSelective(onlineSystem);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除OnlineSystem")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "OnlineSystem的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		onlineSystemService.delete(id);
		return BaseOutput.success("删除成功");
	}
}