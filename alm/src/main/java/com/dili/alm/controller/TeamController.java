package com.dili.alm.controller;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.User;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.TeamService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public String index(ModelMap modelMap) {
		refreshProject();
		return "team/index";
	}

	@ApiOperation(value = "查询Team", notes = "查询Team，返回列表信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Team", paramType = "form", value = "Team的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String list(Team team) {
		refreshMember();
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("memberId", memberProvider);

		JSONObject teamTypeProvider = new JSONObject();
		teamTypeProvider.put("provider", "teamTypeProvider");
		metadata.put("type", teamTypeProvider);

		JSONObject memberStateProvider = new JSONObject();
		memberStateProvider.put("provider", "memberStateProvider");
		metadata.put("memberState", memberStateProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("joinTime", datetimeProvider);
		metadata.put("leaveTime", datetimeProvider);

		// 测试数据
		List<Team> list = this.teamService.listByExample(team);
		Page<T> page = (Page)list;
		try {
			List results = ValueProviderUtils.buildDataByProvider(metadata, list);
			return new EasyuiPageOutput(Integer.parseInt(String.valueOf(page.getTotal())), results).toString();
		} catch (Exception e) {
			return null;
		}
	}

	@ApiOperation(value = "分页查询Team", notes = "分页查询Team，返回easyui分页信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Team", paramType = "form", value = "Team的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(Team team) throws Exception {
		return teamService.listEasyuiPageByExample(team, true).toString();
	}

	@ApiOperation("新增Team")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Team", paramType = "form", value = "Team的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> insert(Team team) {
		return teamService.insertAfterCheck(team);
	}

	@ApiOperation("修改Team")
	@ApiImplicitParams({ @ApiImplicitParam(name = "Team", paramType = "form", value = "Team的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> update(Team team) {
		return teamService.updateAfterCheck(team);
	}

	@ApiOperation("删除Team")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "Team的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		teamService.delete(id);
		return BaseOutput.success("删除成功");
	}
}