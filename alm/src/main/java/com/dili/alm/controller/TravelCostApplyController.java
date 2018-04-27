package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.TravelCostApply;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.TravelCostApplyUpdateDto;
import com.dili.alm.exceptions.TravelCostApplyException;
import com.dili.alm.service.DataDictionaryService;
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
	@Autowired
	private DataDictionaryService ddService;

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

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addView(ModelMap modelMap) {
		modelMap.addAttribute("projects", AlmCache.getInstance().getProjectMap().values());
		// 查询费用项
		DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.TRAVEL_COST_DETAIL_CONFIG_CODE);
		if (dd != null) {
			modelMap.addAttribute("costItems", dd.getValues());
		}
		return "travelCostApply/add";
	}

	@ApiOperation("新增TravelCostApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TravelCostApply", paramType = "form", value = "TravelCostApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(TravelCostApplyUpdateDto travelCostApply) {
		try {
			this.travelCostApplyService.saveOrUpdate(travelCostApply);
			return BaseOutput.success();
		} catch (TravelCostApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("修改TravelCostApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TravelCostApply", paramType = "form", value = "TravelCostApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput update(TravelCostApplyUpdateDto travelCostApply) {
		try {
			this.travelCostApplyService.saveOrUpdate(travelCostApply);
			return BaseOutput.success();
		} catch (TravelCostApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("删除TravelCostApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "TravelCostApply的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		try {
			this.travelCostApplyService.deleteTravelCostApply(id);
			return BaseOutput.success();
		} catch (TravelCostApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}
}