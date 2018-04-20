package com.dili.alm.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.TravelCostApply;
import com.dili.alm.domain.TravelCostApplyResult;
import com.dili.alm.domain.TravelCostApplyState;
import com.dili.alm.domain.dto.TravelCostApplyQueryDto;
import com.dili.alm.domain.dto.TravelCostApplyUpdateDto;
import com.dili.alm.exceptions.TravelCostApplyException;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.TravelCostApplyService;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(TravelCostApplyController.class);
	@Autowired
	TravelCostApplyService travelCostApplyService;
	@Autowired
	private DataDictionaryService ddService;

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String detail(@RequestParam Long id, ModelMap modelMap) {
		try {
			TravelCostApply apply = this.travelCostApplyService.getUpdateViewData(id);
			modelMap.addAttribute("apply", apply);
			// 查询费用项
			modelMap.addAttribute("costItems", AlmCache.getInstance().getTravelCostItemMap());
			return "travelCostApply/detail";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	@RequestMapping(value = "/review", method = RequestMethod.GET)
	public String reviewView(@RequestParam Long id, ModelMap modelMap) {
		try {
			TravelCostApply apply = this.travelCostApplyService.getUpdateViewData(id);
			modelMap.addAttribute("apply", apply);
			// 查询费用项
			modelMap.addAttribute("costItems", AlmCache.getInstance().getTravelCostItemMap());
			return "travelCostApply/review";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/review", method = RequestMethod.POST)
	public BaseOutput<Object> review(@RequestParam Long id, @RequestParam Integer result) {
		try {
			this.travelCostApplyService.review(id, TravelCostApplyResult.valueOf(result));
			return BaseOutput.success();
		} catch (TravelCostApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("跳转到TravelCostApply页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("user", user).addAttribute("states", TravelCostApplyState.values());
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
	public @ResponseBody String listPage(TravelCostApplyQueryDto apply) throws Exception {
		return travelCostApplyService.listEasyuiPageByExample(apply, true).toString();
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addView(ModelMap modelMap) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("user", user);
		Map.Entry<Long, Department> entry = AlmCache.getInstance().getDepMap().entrySet().stream()
				.filter(e -> e.getKey().equals(user.getDepartmentId())).findFirst().orElse(null);
		Department department = entry.getValue();
		modelMap.addAttribute("department", department);
		if (department.getParentId() == null) {
			modelMap.addAttribute("rootDepartment", department);
		} else {
			do {
				department = AlmCache.getInstance().getDepMap().get(department.getParentId());
			} while (department.getParentId() != null);
			modelMap.addAttribute("rootDepartment", department);
		}
		modelMap.addAttribute("projects", AlmCache.getInstance().getProjectMap().values());
		// 查询费用项
		modelMap.addAttribute("costItems", AlmCache.getInstance().getTravelCostItemMap());
		return "travelCostApply/add";
	}

	@ApiOperation("新增TravelCostApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TravelCostApply", paramType = "form", value = "TravelCostApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(@RequestBody TravelCostApplyUpdateDto apply) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		apply.setApplicantId(user.getId());
		try {
			this.travelCostApplyService.saveOrUpdate(apply);
			return BaseOutput.success();
		} catch (TravelCostApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/saveAndSubmit", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput saveAndSubmit(@RequestBody TravelCostApplyUpdateDto apply) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		apply.setApplicantId(user.getId());
		try {
			this.travelCostApplyService.saveAndSubmit(apply);
			return BaseOutput.success();
		} catch (TravelCostApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String updateView(@RequestParam Long id, ModelMap modelMap) throws Exception {
		@SuppressWarnings("rawtypes")
		TravelCostApply viewModel = this.travelCostApplyService.getUpdateViewData(id);
		modelMap.addAttribute("apply", viewModel);
		// 查询项目
		modelMap.addAttribute("projects", AlmCache.getInstance().getProjectMap().values());
		// 查询费用项
		modelMap.addAttribute("costItems", AlmCache.getInstance().getTravelCostItemMap());
		return "travelCostApply/update";
	}

	@ApiOperation("修改TravelCostApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TravelCostApply", paramType = "form", value = "TravelCostApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput update(@RequestBody TravelCostApplyUpdateDto apply) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		apply.setApplicantId(user.getId());
		try {
			this.travelCostApplyService.saveOrUpdate(apply);
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