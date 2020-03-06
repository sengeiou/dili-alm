package com.dili.alm.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.TeamDepartmentRoleQuery;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.TeamService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.domain.dto.UserDepartmentRole;
import com.dili.uap.sdk.domain.dto.UserDepartmentRoleQuery;
import com.dili.uap.sdk.rpc.DepartmentRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;
import com.google.common.collect.Lists;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamController.class);

	@Autowired
	TeamService teamService;
	@Autowired
	ProjectService projectService;
	@Autowired
	private UserRpc userRPC;
	@Autowired
	private DepartmentRpc deptRPC;

	@ApiOperation("跳转到Team页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(@RequestParam Long projectId, @RequestParam(defaultValue = "false") Boolean editable,
			@RequestParam(required = false) String backUrl, ModelMap modelMap) throws UnsupportedEncodingException {
		try {
			AlmCache.clearCache();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		Boolean projectManager = this.teamService.teamMemberIsProjectManager(user.getId(), projectId);
		modelMap.addAttribute("projectId", projectId).addAttribute("editable", editable).addAttribute("projectManager",
				projectManager);
		if (StringUtils.isNotBlank(backUrl)) {
			modelMap.addAttribute("backUrl", URLDecoder.decode(backUrl, "UTF-8"));
		}
		return "team/index";
	}

	@ApiOperation(value = "查询Team", notes = "查询Team，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Team", paramType = "form", value = "Team的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String list(TeamDepartmentRoleQuery dto) {
		try {
			AlmCache.clearCache();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		try {
			List<Map<Object, Object>> list = this.teamService.listContainUserInfo(dto);
			EasyuiPageOutput e = new EasyuiPageOutput();
			e.setRows(list);
			e.setTotal(list.size());
			return e.toString();
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
		Department department = DTOUtils.newDTO(Department.class);
		department.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<Department>> output = this.deptRPC.listByDepartment(department);
		if (output != null && output.isSuccess()) {
			return output.getData();
		}
		return null;
	}

	@RequestMapping(value = "/members", method = RequestMethod.GET)
	public String members(ModelMap modelMap, @RequestParam("textboxId") String textboxId, String dep) {
		modelMap.put("textboxId", textboxId);
		modelMap.put("dep", dep);
		if (StringUtils.isNotBlank(dep)) {
			AlmCache.getInstance().getDepMap().forEach((Long k, Department v) -> {
				if (Objects.equals(v.getCode(), dep)) {
					modelMap.put("dep", k);
				}
			});
		}
		return "team/members";
	}

	@ResponseBody
	@RequestMapping(value = "/members.json", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<UserDepartmentRole> membersJson(UserDepartmentRoleQuery user, @RequestParam Long projectId) {
		BaseOutput<List<UserDepartmentRole>> output = this.userRPC.findUserContainDepartmentAndRole(user);
		if (output == null || !output.isSuccess()) {
			return Lists.newArrayList();
		}
		List<UserDepartmentRole> udrs = output.getData();
		if (org.apache.commons.collections.CollectionUtils.isEmpty(udrs)) {
			return Lists.newArrayList();
		}
		Team teamQuery = DTOUtils.newDTO(Team.class);
		teamQuery.setProjectId(projectId);
		List<Team> teams = this.teamService.list(teamQuery);
		Set<Long> userIds = new HashSet<>();
		teams.forEach(t -> {
			userIds.add(t.getMemberId());
		});
		Iterator<UserDepartmentRole> it = udrs.iterator();
		while (it.hasNext()) {
			UserDepartmentRole udr = it.next();
			if (!userIds.contains(udr.getId())) {
				it.remove();
			}
		}
		return udrs;
	}
}