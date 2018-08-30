package com.dili.alm.controller;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.TravelCostApply;
import com.dili.alm.domain.TravelCostApplyResult;
import com.dili.alm.domain.TravelCostApplyState;
import com.dili.alm.domain.User;
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

	@ResponseBody
	@RequestMapping("/users.json")
	public List<User> getUsers() {
		return AlmCache.getInstance().getUserMap().values().stream().filter(u -> u.getId() != 1)
				.collect(Collectors.toList());
	}

	@ResponseBody
	@RequestMapping("/getDepartmentByUserId.json")
	public BaseOutput<Department> getDepartmentByUserId(@RequestParam Long userId) {
		return BaseOutput.success().setData(AlmCache.getInstance().getDepMap().values().stream()
				.filter(d -> d.getId().equals(AlmCache.getInstance().getUserMap().get(userId).getDepartmentId()))
				.findFirst().orElse(null));
	}

	@ResponseBody
	@RequestMapping("/getRootDepartment.json")
	public BaseOutput<Department> getRootDepartment(@RequestParam Long deptId) {
		Map.Entry<Long, Department> entry = AlmCache.getInstance().getDepMap().entrySet().stream()
				.filter(e -> e.getKey().equals(deptId)).findFirst().orElse(null);
		Department department = entry.getValue();
		if (department.getParentId() == null) {
			return BaseOutput.success().setData(department);
		} else {
			do {
				department = AlmCache.getInstance().getDepMap().get(department.getParentId());
			} while (department.getParentId() != null);
			return BaseOutput.success().setData(department);
		}
	}

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
		apply.setSort("creationTime");
		apply.setOrder("desc");
		if (apply.getSubmitEndDate() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(apply.getSubmitEndDate());
			c.add(Calendar.DAY_OF_MONTH, 1);
			apply.setSubmitEndDate(c.getTime());
		}
		return travelCostApplyService.listEasyuiPageByExample(apply, true).toString();
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addView(ModelMap modelMap) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("user", user);
		modelMap.addAttribute("projects", AlmCache.getInstance().getProjectMap().values().stream()
				.filter(p -> !p.getProjectState().equals(ProjectState.CLOSED.getValue())).collect(Collectors.toList()));
		// 查询费用项
		modelMap.addAttribute("costItems", AlmCache.getInstance().getTravelCostItemMap());
		return "travelCostApply/add";
	}

	@ApiOperation("新增TravelCostApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TravelCostApply", paramType = "form", value = "TravelCostApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(@RequestBody TravelCostApplyUpdateDto apply) {
		try {
			this.travelCostApplyService.saveOrUpdate(apply);
			return BaseOutput.success();
		} catch (TravelCostApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/saveAndSubmit", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> saveAndSubmit(@RequestBody TravelCostApplyUpdateDto apply) {
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
		modelMap.addAttribute("projects", AlmCache.getInstance().getProjectMap().values().stream()
				.filter(p -> !p.getProjectState().equals(ProjectState.CLOSED.getValue())).collect(Collectors.toList()));
		// 查询费用项
		modelMap.addAttribute("costItems", AlmCache.getInstance().getTravelCostItemMap());
		return "travelCostApply/update";
	}

	@ApiOperation("修改TravelCostApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "TravelCostApply", paramType = "form", value = "TravelCostApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput update(@RequestBody TravelCostApplyUpdateDto apply) {
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