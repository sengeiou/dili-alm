package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.TravelCostApply;
import com.dili.alm.service.TravelCostApplyService;
import com.dili.ss.domain.BaseOutput;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-04-24 16:43:13.
 */
@Api("/travelCostApply")
@Controller
@RequestMapping("/travelCostApply")
public class TravelCostApplyController {
	@Autowired
	TravelCostApplyService travelCostApplyService;

	@ApiOperation("跳转到TravelCostApply页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "travelCostApply/index";
	}

	@ApiOperation(value = "查询TravelCostApply", notes = "查询TravelCostApply，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TravelCostApply", paramType = "form", value = "TravelCostApply的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<TravelCostApply> list(TravelCostApply travelCostApply) {
		return travelCostApplyService.list(travelCostApply);
	}

	@ApiOperation(value = "分页查询TravelCostApply", notes = "分页查询TravelCostApply，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TravelCostApply", paramType = "form", value = "TravelCostApply的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(TravelCostApply travelCostApply) throws Exception {
		return travelCostApplyService.listEasyuiPageByExample(travelCostApply, true).toString();
	}

	@ApiOperation("新增TravelCostApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TravelCostApply", paramType = "form", value = "TravelCostApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(TravelCostApply travelCostApply) {
		travelCostApplyService.insertSelective(travelCostApply);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改TravelCostApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TravelCostApply", paramType = "form", value = "TravelCostApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(TravelCostApply travelCostApply) {
		travelCostApplyService.updateSelective(travelCostApply);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除TravelCostApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "TravelCostApply的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		travelCostApplyService.delete(id);
		return BaseOutput.success("删除成功");
	}
}