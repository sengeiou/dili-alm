package com.dili.alm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.ProjectComplete;
import com.dili.alm.domain.Team;
import com.dili.alm.service.ProjectCompleteService;
import com.dili.alm.service.TeamService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.session.SessionContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-12-08 11:29:47.
 */
@Api("/projectComplete")
@Controller
@RequestMapping("/projectComplete")
public class ProjectCompleteController {
    @Autowired
    ProjectCompleteService projectCompleteService;

    @Autowired
    private TeamService teamService;

    @ApiOperation("跳转到ProjectComplete页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
        return "projectComplete/index";
    }

    @RequestMapping(value="/add", method = RequestMethod.GET)
    public String add() {
        return "projectComplete/add";
    }

    @RequestMapping(value = "/toStep/{step}/{id}", method = RequestMethod.GET)
    public String toStep(ModelMap modelMap, @PathVariable("id") Long id, @PathVariable("step") int step) throws Exception {
        ProjectComplete projectComplete = DTOUtils.newDTO(ProjectComplete.class);
        projectComplete.setId(id);

        Map<Object, Object> metadata = new HashMap<>(2);

        JSONObject projectTypeProvider = new JSONObject();
        projectTypeProvider.put("provider", "projectTypeProvider");
        metadata.put("type", projectTypeProvider);

        List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, projectCompleteService.listByExample(projectComplete));

        Map applyDTO = maps.get(0);
        if (AlmConstants.ApplyState.APPROVE.check(applyDTO.get("status"))) {
            return "redirect:/projectComplete/index.html";
        }
        modelMap.put("apply", applyDTO);
        return "projectComplete/step" + step;
    }

    @ApiOperation(value="查询ProjectComplete", notes = "查询ProjectComplete，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ProjectComplete", paramType="form", value = "ProjectComplete的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<ProjectComplete> list(ProjectComplete projectComplete) {
        return projectCompleteService.list(projectComplete);
    }

    @RequestMapping(value = "/toDetails/{id}", method = RequestMethod.GET)
    public String toDetails(ModelMap modelMap, @PathVariable("id") Long id) throws Exception {
        ProjectComplete projectComplete = DTOUtils.newDTO(ProjectComplete.class);
        projectComplete.setId(id);

        Map<Object, Object> metadata = new HashMap<>(2);

        JSONObject projectTypeProvider = new JSONObject();
        projectTypeProvider.put("provider", "projectTypeProvider");
        metadata.put("type", projectTypeProvider);

        List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, projectCompleteService.listByExample(projectComplete));

        Map applyDTO = maps.get(0);
        modelMap.put("apply", applyDTO);

        return "projectComplete/details";
    }

    @ApiOperation(value="分页查询ProjectComplete", notes = "分页查询ProjectComplete，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ProjectComplete", paramType="form", value = "ProjectComplete的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(ProjectComplete projectComplete) throws Exception {
        return projectCompleteService.listEasyuiPageByExample(projectComplete, true).toString();
    }

    @ApiOperation("新增ProjectComplete")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ProjectComplete", paramType="form", value = "ProjectComplete的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(ProjectComplete projectComplete) {
        projectComplete.setStatus(AlmConstants.ApplyState.APPLY.getCode());
        projectComplete.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
        projectCompleteService.insertSelective(projectComplete);
        return BaseOutput.success(String.valueOf(projectComplete.getId()));
    }

    @RequestMapping(value = "/reComplete/{id}", method = RequestMethod.GET)
    public String reComplete(@PathVariable("id") Long id) {
        Long newId = projectCompleteService.reComplete(id);
        return "redirect:/projectComplete/toStep/1/" + newId;
    }

    @ApiOperation("修改ProjectComplete")
    @ApiImplicitParams({
		@ApiImplicitParam(name="ProjectComplete", paramType="form", value = "ProjectComplete的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(ProjectComplete projectComplete) {
        projectCompleteService.updateSelective(projectComplete);
        projectCompleteService.approve(projectComplete);
        return BaseOutput.success(String.valueOf(projectComplete.getId()));
    }

    @ApiOperation("删除ProjectComplete")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "ProjectComplete的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        projectCompleteService.delete(id);
        return BaseOutput.success("删除成功");
    }

    @RequestMapping("/loadQuestion")
    @ResponseBody
    public Object loadQuestion(Long id) {
        ProjectComplete projectComplete = projectCompleteService.get(id);

        return projectComplete.getQuestion();
    }

    @RequestMapping("/loadPerformance")
    @ResponseBody
    public Object loadPerformance(Long id) {
        ProjectComplete projectComplete = projectCompleteService.get(id);

        return projectComplete.getPerformance();
    }

    @RequestMapping("/loadHardWare")
    @ResponseBody
    public Object loadHardWare(Long id) {
        ProjectComplete projectComplete = projectCompleteService.get(id);

        return projectComplete.getHardware();
    }

    @RequestMapping("/loadMembers")
    @ResponseBody
    public Object loadMembers(Long id) throws Exception {
        ProjectComplete projectComplete = projectCompleteService.get(id);
        if(StringUtils.isBlank(projectComplete.getMembers())){
            Team team = DTOUtils.newDTO(Team.class);
            team.setProjectId(projectComplete.getProjectId());
            List<Team> list = teamService.list(team);
            Map<Object, Object> metadata = new HashMap<>();
            metadata.put("memberId", JSON.parse("{provider:'memberProvider'}"));
            metadata.put("role", JSON.parse("{provider:'teamRoleProvider'}"));
            metadata.put("joinTime", JSON.parse("{provider:'datetimeProvider'}"));
            metadata.put("leaveTime", JSON.parse("{provider:'datetimeProvider'}"));
            List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, list);
            maps.forEach(map -> map.put("created", new Date()));
            return maps;
        }
        return projectComplete.getPerformance();
    }

    @RequestMapping("/loadComplete")
    @ResponseBody
    public Map loadComplete(Long id) throws Exception {
        ProjectComplete projectComplete = DTOUtils.newDTO(ProjectComplete.class);
        projectComplete.setId(id);

        Map<Object, Object> metadata = new HashMap<>();
        metadata.put("projectLeader", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("productManager", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("developmentManager", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("testManager", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("businessOwner", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("dep", JSON.parse("{provider:'depProvider'}"));

        List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, projectCompleteService.listByExample(projectComplete));
        return maps.get(0);
    }
}