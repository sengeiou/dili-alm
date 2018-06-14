package com.dili.alm.controller;

import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.VerifyApproval;
import com.dili.alm.exceptions.ProjectApplyException;
import com.dili.alm.service.MessageService;
import com.dili.alm.service.ProjectChangeService;
import com.dili.alm.service.TaskService;
import com.dili.alm.service.VerifyApprovalService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-30 17:21:55.
 */
@Api("/projectChange")
@Controller
@RequestMapping("/projectChange")
public class ProjectChangeController {
	@Autowired
	private ProjectChangeService projectChangeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private VerifyApprovalService verifyApprovalService;

	@Autowired
	private MessageService messageService;

	@ApiOperation("跳转到ProjectChange页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
		return "projectChange/index";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add(ModelMap modelMap) {
		return "projectChange/add";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(ModelMap modelMap, Long id) {
		ProjectChange change = projectChangeService.get(id);
		if (change == null) {
			return "redirect:/projectChange/index.html";
		}
		if (!change.getCreateMemberId().equals(SessionContext.getSessionContext().getUserTicket().getId())) {
			return "redirect:/projectChange/index.html";
		}
		modelMap.put("obj", change);
		if (!AlmConstants.ApplyState.APPLY.check(change.getStatus())) {
			return "redirect:/projectChange/index.html";
		}
		return "projectChange/edit";
	}

	@RequestMapping(value = "/loadTask", method = RequestMethod.GET)
	@ResponseBody
	public Object loadTask(Task task) throws Exception {
		task.setStatus(AlmConstants.TaskStatus.COMPLETE.getCode());
		return taskService.listEasyuiPageByExample(task, true).toString();
	}

	@RequestMapping(value = "loadVerify")
	@ResponseBody
	public Object loadVerify(VerifyApproval verifyApproval) throws Exception {
		return verifyApprovalService.listEasyuiPageByExample(verifyApproval, true);
	}

	@RequestMapping(value = "loadProjectChange")
	@ResponseBody
	public Object loadProjectChange(ProjectChange change) {
		change.setOrder("desc");
		change.setSort("created");
		List<ProjectChange> changes = projectChangeService.listByExample(change);
		if (CollectionUtils.isNotEmpty(changes)) {
			return DateUtil.getDate(changes.get(0).getEstimateLaunchDate());

		}
		return "";
	}

	@RequestMapping(value = "/toDetails/{id}", method = RequestMethod.GET)
	public String toDetails(ModelMap modelMap, @PathVariable("id") Long id) {
		ProjectChange change = projectChangeService.get(id);
		if (change == null) {
			return "redirect:/projectChange/index.html";
		}
		modelMap.put("obj", change);
		return "projectChange/details";
	}

	@RequestMapping(value = "/reChange/{id}", method = RequestMethod.GET)
	public String reChange(@PathVariable("id") Long id) {
		Long reApplyId = projectChangeService.reChange(id);
		return reApplyId == -1 ? "redirect:/projectChange/index.html" : "redirect:/projectChange/edit?id=" + reApplyId;
	}

	@ApiOperation(value = "查询ProjectChange", notes = "查询ProjectChange，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectChange", paramType = "form", value = "ProjectChange的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectChange> list(ProjectChange projectChange) {
		return projectChangeService.list(projectChange);
	}

	@ApiOperation(value = "分页查询ProjectChange", notes = "分页查询ProjectChange，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectChange", paramType = "form", value = "ProjectChange的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(ProjectChange projectChange) throws Exception {
		return projectChangeService.listEasyuiPageByExample(projectChange, true).toString();
	}

	@ApiOperation("新增ProjectChange")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectChange", paramType = "form", value = "ProjectChange的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(ProjectChange projectChange) {
		projectChange.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
		projectChangeService.insertSelective(projectChange);
		try {
			projectChangeService.approve(projectChange);
			return BaseOutput.success("新增成功")
					.setData(String.valueOf(projectChange.getId() + ":" + projectChange.getName()));
		} catch (ProjectApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("修改ProjectChange")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectChange", paramType = "form", value = "ProjectChange的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> update(ProjectChange projectChange) {
		projectChange.setSubmitDate(new Date());
		projectChangeService.updateSelective(projectChange);
		try {
			projectChangeService.approve(projectChange);
			return BaseOutput.success("修改成功")
					.setData(String.valueOf(projectChange.getId() + ":" + projectChange.getName()));
		} catch (ProjectApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("删除ProjectChange")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "ProjectChange的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		ProjectChange change = projectChangeService.get(id);
		if (change != null
				&& change.getCreateMemberId().equals(SessionContext.getSessionContext().getUserTicket().getId())) {
			messageService.deleteMessage(id, AlmConstants.MessageType.CHANGE.code);
			projectChangeService.delete(id);
		}
		return BaseOutput.success("删除成功").setData(String.valueOf(change.getId() + ":" + change.getName()));
	}
}