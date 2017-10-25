package com.dili.alm.controller;

import com.dili.alm.domain.Team;
import com.dili.alm.service.TeamService;
import com.dili.ss.domain.BaseOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-10-24 14:31:10.
 */
@Api("/team")
@Controller
@RequestMapping("/team")
public class TeamController {
    @Autowired
    TeamService teamService;

    @ApiOperation("跳转到Team页面")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "team/index";
    }

    @ApiOperation(value="查询Team", notes = "查询Team，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Team", paramType="form", value = "Team的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Team> list(Team team) {
        return teamService.list(team);
    }

    @ApiOperation(value="分页查询Team", notes = "分页查询Team，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Team", paramType="form", value = "Team的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(Team team) throws Exception {
        return teamService.listEasyuiPageByExample(team, true).toString();
    }

    @ApiOperation("新增Team")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Team", paramType="form", value = "Team的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Team team) {
        teamService.insertSelective(team);
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改Team")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Team", paramType="form", value = "Team的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Team team) {
        teamService.updateSelective(team);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除Team")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Team的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        teamService.delete(id);
        return BaseOutput.success("删除成功");
    }
}