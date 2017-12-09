package com.dili.alm.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.TeamDepartmentRoleQuery;
import com.dili.alm.domain.dto.UserDepartmentRoleQuery;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.TeamService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-24 14:31:10.
 */
@Api("/team")
@Controller
@RequestMapping("/team")
public class TeamController {
	@Autowired
	TeamService teamService;
	@Autowired
	ProjectService projectService;
	@Autowired
	private UserRpc userRPC;
	@Autowired
	private DepartmentRpc deptRPC;

	/**
	 * 刷新项目缓存
	 */
	private void refreshProject() {
		List<Project> list = projectService.list(DTOUtils.newDTO(Project.class));
		list.forEach(project -> {
			AlmCache.PROJECT_MAP.put(project.getId(), project);
		});
	}

	private void refreshMember() {
		AlmCache.USER_MAP.clear();
		BaseOutput<List<User>> output = this.userRPC.list(new User());
		if (!output.isSuccess()) {
			return;
		}
		List<User> users = output.getData();
		if (CollectionUtils.isEmpty(users)) {
			return;
		}
		users.forEach(u -> {
			AlmCache.USER_MAP.put(u.getId(), u);
		});
	}

	@ApiOperation("跳转到Team页面")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(@RequestParam Long projectId, ModelMap modelMap) {
		refreshProject();
		modelMap.addAttribute("projectId", projectId);
		return "team/index";
	}

	@ApiOperation(value = "查询Team", notes = "查询Team，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Team", paramType = "form", value = "Team的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Map<Object, Object>> list(TeamDepartmentRoleQuery dto) {
		refreshMember();

		try {
			return this.teamService.listContainUserInfo(dto);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@ApiOperation(value = "分页查询Team", notes = "分页查询Team，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Team", paramType = "form", value = "Team的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(Team team) throws Exception {
		return teamService.listEasyuiPageByExample(team, true).toString();
	}

	@ApiOperation("新增Team")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Team", paramType = "form", value = "Team的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> insert(Team team) {
		return teamService.insertAfterCheck(team);
	}

	@ApiOperation("修改Team")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Team", paramType = "form", value = "Team的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> update(Team team) {
		return teamService.updateAfterCheck(team);
	}

	@ApiOperation("删除Team")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "Team的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		return teamService.deleteAfterCheck(id);
	}

	@ResponseBody
	@RequestMapping(value = "/role.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<DataDictionaryValueDto> getTeamRoles() {
		return this.teamService.getTeamRoles();
	}

	@ResponseBody
	@RequestMapping(value = "/department.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Department> listDepartments() {
		BaseOutput<List<Department>> output = this.deptRPC.list(null);
		if (output != null && output.isSuccess()) {
			return output.getData();
		}
		return null;
	}
}