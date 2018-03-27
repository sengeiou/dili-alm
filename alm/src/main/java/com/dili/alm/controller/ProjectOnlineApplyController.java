package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.dto.ProjectOnlineApplyUpdateDto;
import com.dili.alm.exceptions.ProjectOnlineApplyException;
import com.dili.alm.service.ProjectOnlineApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import tk.mybatis.mapper.entity.Example;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-13 15:31:10.
 */
@Api("/projectOnlineApply")
@Controller
@RequestMapping("/projectOnlineApply")
public class ProjectOnlineApplyController {

	private static final String DATA_AUTH_TYPE = "Project";

	@Autowired
	ProjectOnlineApplyService projectOnlineApplyService;
	@Autowired
	private ProjectService projectService;

	@ApiOperation("跳转到ProjectOnlineApply页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "projectOnlineApply/index";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addView(ModelMap modelMap) {
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return null;
		}
		List<Long> projectIds = new ArrayList<>();
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("dataId").toString())));
		Example example = new Example(ProjectOnlineApply.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", projectIds);
		List<Project> projects = this.projectService.selectByExample(example);
		modelMap.addAttribute("projects", projects);
		return "projectOnlineApply/add";
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
	public @ResponseBody BaseOutput<Object> insert(@Valid ProjectOnlineApplyUpdateDto projectOnlineApply,
			BindingResult br) {
		if (br.hasErrors()) {
			return BaseOutput.failure(br.getFieldError().getDefaultMessage());
		}
		// 获取申请人
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			return BaseOutput.failure("请先登录");
		}
		projectOnlineApply.setApplicantId(user.getId());
		try {
			projectOnlineApplyService.saveOrUpdate(projectOnlineApply);
			return BaseOutput.success();
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("修改ProjectOnlineApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ProjectOnlineApply", paramType = "form", value = "ProjectOnlineApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> update(@Valid ProjectOnlineApplyUpdateDto projectOnlineApply,
			BindingResult br) {
		try {
			projectOnlineApplyService.saveOrUpdate(projectOnlineApply);
			return BaseOutput.success();
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ApiOperation("删除ProjectOnlineApply")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "ProjectOnlineApply的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		try {
			projectOnlineApplyService.deleteProjectOnlineApply(id);
			return BaseOutput.success();
		} catch (ProjectOnlineApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}
}