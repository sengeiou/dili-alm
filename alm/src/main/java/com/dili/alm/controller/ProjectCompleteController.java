package com.dili.alm.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectComplete;
import com.dili.alm.domain.dto.ProjectCompleteQueryDto;
import com.dili.alm.exceptions.ProjectApplyException;
import com.dili.alm.exceptions.ProjectCompleteException;
import com.dili.alm.service.ProjectCompleteService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.session.SessionContext;



/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-08 11:29:47.
 */
@Controller
@RequestMapping("/projectComplete")
public class ProjectCompleteController {
	@Autowired
	ProjectCompleteService projectCompleteService;
	@Autowired
	private ProjectService projectService;

	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
		return "projectComplete/index";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "projectComplete/add";
	}

	@RequestMapping(value = "/toStep/{step}/index.html", method = RequestMethod.GET)
	public String toStep(ModelMap modelMap,  Long id, @PathVariable("step") int step) throws Exception {
		ProjectComplete projectComplete = DTOUtils.newDTO(ProjectComplete.class);
		projectComplete.setId(id);

		Map<Object, Object> metadata = new HashMap<>(2);

		JSONObject projectTypeProvider = new JSONObject();
		projectTypeProvider.put("provider", "projectTypeProvider");
		metadata.put("type", projectTypeProvider);

		List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, projectCompleteService.listByExample(projectComplete));
		if (CollectionUtils.isEmpty(maps)) {
			return "redirect:/projectComplete/index.html";
		}
		Map applyDTO = maps.get(0);
		if (!AlmConstants.ApplyState.APPLY.check(applyDTO.get("status"))) {
			return "redirect:/projectComplete/index.html";
		}
		try {
			if (Long.parseLong(applyDTO.get("createMemberId").toString()) != SessionContext.getSessionContext().getUserTicket().getId()) {
				return "redirect:/projectComplete/index.html";
			}
		} catch (Exception ignored) {
		}
		modelMap.put("apply", applyDTO);
		return "projectComplete/step" + step;
	}

	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectComplete> list(ProjectComplete projectComplete) {
		return projectCompleteService.list(projectComplete);
	}

	@RequestMapping(value = "/toDetails/{id}", method = RequestMethod.GET)
	public String toDetails(ModelMap modelMap, @PathVariable("id") Long id) throws Exception {
		ProjectComplete projectComplete = this.projectCompleteService.get(id);
		Project project = this.projectService.get(projectComplete.getProjectId());

		Map<Object, Object> metadata1 = new HashMap<>();
		metadata1.put("dep", "depProvider");
		metadata1.put("type", "projectTypeProvider");
		metadata1.put("startDate", "almDateProvider");
		metadata1.put("endDate", "almDateProvider");
		metadata1.put("actualStartDate", "datetimeProvider");
		metadata1.put("projectManager", "memberProvider");
		metadata1.put("testManager", "memberProvider");
		metadata1.put("productManager", "memberProvider");
		metadata1.put("developManager", "memberProvider");
		metadata1.put("businessOwner", "memberProvider");
		metadata1.put("originator", "memberProvider");

		Map projectViewModel = ValueProviderUtils.buildDataByProvider(metadata1, Arrays.asList(project)).get(0);
		modelMap.addAttribute("project1", projectViewModel);

		Map<Object, Object> metadata = new HashMap<>(2);

		JSONObject projectTypeProvider = new JSONObject();
		projectTypeProvider.put("provider", "projectTypeProvider");
		metadata.put("type", projectTypeProvider);

		List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(projectComplete));
		if (CollectionUtils.isEmpty(maps)) {
			return "redirect:/projectComplete/index.html";
		}

		Map applyDTO = maps.get(0);
		modelMap.addAttribute("apply", applyDTO);

		return "projectComplete/details";
	}

	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(ProjectCompleteQueryDto projectComplete) throws Exception {
		if (projectComplete.getCreated() != null) {
			Date temp = projectComplete.getCreated();
			projectComplete.setCreated(DateUtil.appendDateToEnd(projectComplete.getCreated()));
			projectComplete.setCreatedStart(DateUtil.appendDateToStart(temp));
		}
		return projectCompleteService.listEasyuiPageByExample(projectComplete, true).toString();
	}

	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(ProjectComplete projectComplete, String reasonText) {
		if (projectComplete.getReason().equalsIgnoreCase("2")) {
			projectComplete.setReason(reasonText);
		}
		projectComplete.setStatus(AlmConstants.ApplyState.APPLY.getCode());
		projectComplete.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
		try {
			projectCompleteService.insertWithCheck(projectComplete);
			return BaseOutput.success(String.valueOf(projectComplete.getId())).setData(projectComplete.getId() + ":" + projectComplete.getName());
		} catch (ProjectCompleteException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/reComplete/{id}", method = RequestMethod.GET)
	public String reComplete(@PathVariable("id") Long id) {
		Long newId = projectCompleteService.reComplete(id);
		return newId == -1 ? "redirect:/projectComplete/index.html" : "redirect:/projectComplete/toStep/1/index.html?id=" + newId;
	}

	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> update(ProjectComplete projectComplete, String reasonText) {
		if (StringUtils.isNotEmpty(projectComplete.getReason())) {
			if (projectComplete.getReason().equalsIgnoreCase("2")) {
				projectComplete.setReason(reasonText);
			}
		}
		projectCompleteService.updateSelective(projectComplete);
		try {
			projectCompleteService.approve(projectComplete);
			return BaseOutput.success(String.valueOf(projectComplete.getId())).setData(projectComplete.getId() + ":" + projectComplete.getName());
		} catch (ProjectApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		ProjectComplete complete = projectCompleteService.get(id);
		if (complete != null && complete.getCreateMemberId().equals(SessionContext.getSessionContext().getUserTicket().getId())) {

			projectCompleteService.delete(id);
		}
		return BaseOutput.success("删除成功").setData(String.valueOf(complete.getId() + ":" + complete.getName()));
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
		return projectCompleteService.loadMembers(id);
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