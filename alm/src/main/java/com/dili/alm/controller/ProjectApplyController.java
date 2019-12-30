package com.dili.alm.controller;

import java.util.ArrayList;
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
import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectApplyQueryDto;
import com.dili.alm.domain.dto.ProjectApplyDto;
import com.dili.alm.domain.dto.RoiDto;
import com.dili.alm.domain.dto.RoiUpdateDto;
import com.dili.alm.domain.dto.apply.ApplyDescription;
import com.dili.alm.domain.dto.apply.ApplyFiles;
import com.dili.alm.domain.dto.apply.ApplyGoalsFunctions;
import com.dili.alm.domain.dto.apply.ApplyImpact;
import com.dili.alm.domain.dto.apply.ApplyMajorResource;
import com.dili.alm.domain.dto.apply.ApplyRisk;
import com.dili.alm.exceptions.ProjectApplyException;
import com.dili.alm.provider.ProjectTypeProvider;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.uap.sdk.session.SessionContext;
import com.google.common.collect.Lists;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-21 16:19:02.
 */
@Api("/projectApply")
@Controller
@RequestMapping("/projectApply")
public class ProjectApplyController {

	private static final String DATA_AUTH_TYPE = "Project";

	@Autowired
	ProjectApplyService projectApplyService;

	@Autowired
	ProjectTypeProvider projectTypeProvider;

	@Autowired
	FilesService filesService;

	@Autowired
	private ProjectService projectService;

	@ApiOperation("跳转到ProjectApply页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		modelMap.put("sessionID", SessionContext.getSessionContext().getUserTicket().getId());
		return "projectApply/index";
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String add() {
		return "projectApply/add";
	}

	@RequestMapping(value = "/toStep/{step}/{id}", method = RequestMethod.GET)
	public String toStep(ModelMap modelMap, @PathVariable("id") Long id, @PathVariable("step") int step) throws Exception {
		ProjectApply projectApply = DTOUtils.newDTO(ProjectApply.class);
		projectApply.setId(id);

		Map<Object, Object> metadata = new HashMap<>(2);

		JSONObject projectTypeProvider = new JSONObject();
		projectTypeProvider.put("provider", "projectTypeProvider");
		metadata.put("type", projectTypeProvider);

		List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, projectApplyService.listByExample(projectApply));

		if (CollectionUtils.isEmpty(maps)) {
			return "redirect:/projectApply/index.html";
		}
		Map applyDTO = maps.get(0);
		if (!AlmConstants.ApplyState.APPLY.check(applyDTO.get("status"))) {
			return "redirect:/projectApply/index.html";
		}
		try {
			if (Long.parseLong(applyDTO.get("createMemberId").toString()) != SessionContext.getSessionContext().getUserTicket().getId()) {
				return "redirect:/projectApply/index.html";
			}
		} catch (Exception ignored) {
		}
		modelMap.put("apply", applyDTO);
		if (step == 1) {
			projectApplyService.buildStepOne(modelMap, applyDTO);
		}
		if (step == 5) {
			RoiDto roi = AlmCache.getInstance().getProjectApplyRois().get(id);
			modelMap.addAttribute("roi", roi);
		}
		return "projectApply/step" + step;
	}

	@RequestMapping(value = "/reApply/{id}", method = RequestMethod.GET)
	public String reApply(@PathVariable("id") Long id) {
		Long reApplyId = projectApplyService.reApply(id);
		return reApplyId == -1 ? "redirect:/projectApply/index.html" : "redirect:/projectApply/toStep/1/" + reApplyId;
	}

	@RequestMapping(value = "/toDetails/{id}", method = RequestMethod.GET)
	public String toDetails(ModelMap modelMap, @PathVariable("id") Long id) throws Exception {
		ProjectApply projectApply = DTOUtils.newDTO(ProjectApply.class);
		projectApply.setId(id);
		Map<Object, Object> metadata = new HashMap<>(1);
		JSONObject projectTypeProvider = new JSONObject();
		projectTypeProvider.put("provider", "projectTypeProvider");
		metadata.put("type", projectTypeProvider);

		List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, projectApplyService.listByExample(projectApply));

		if (CollectionUtils.isEmpty(maps)) {
			return "redirect:/projectApply/index.html";
		}

		Map applyDTO = maps.get(0);
		modelMap.put("apply", applyDTO);
		projectApplyService.buildStepOne(modelMap, applyDTO);
		modelMap.addAttribute("roi", AlmCache.getInstance().getProjectApplyRois().get(id));
		return "projectApply/details";
	}

	@ApiOperation(value = "分页查询ProjectApply", notes = "分页查询ProjectApply，返回easyui分页信息")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectApply", paramType = "form", value = "ProjectApply的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(ProjectApplyQueryDto projectApply) throws Exception {
		if (projectApply.getCreated() != null) {
			Date temp = projectApply.getCreated();
			projectApply.setCreated(DateUtil.appendDateToEnd(projectApply.getCreated()));
			projectApply.setCreatedStart(DateUtil.appendDateToStart(temp));
		}
		return projectApplyService.listEasyuiPageByExample(projectApply, true).toString();
	}

	@RequestMapping(value = "/getProjectList")
	public @ResponseBody Object getProjectList(ProjectApply projectApply) {
		List<ValuePair<?>> buffer = new ArrayList<>();
		buffer.add(new ValuePairImpl("请选择", ""));
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return new ArrayList<>(0);
		}
		List<Long> projectIds = new ArrayList<>(dataAuths.size());
		dataAuths.forEach(dataAuth -> projectIds.add(Long.valueOf(dataAuth.get("dataId").toString())));
		Example example = new Example(Project.class);
		Criteria criteria = example.createCriteria().andIn("id", projectIds);
		List<Project> list = this.projectService.selectByExample(example);
		list.forEach(apply -> buffer.add(new ValuePairImpl<>(apply.getName(), apply.getId())));
		return buffer;
	}

	@ApiOperation("新增ProjectApply")
	@ApiImplicitParams({ @ApiImplicitParam(name = "ProjectApply", paramType = "form", value = "ProjectApply的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(ProjectApply projectApply,String[] demandIds) {
		return projectApplyService.insertApply(projectApply,demandIds);
		 
	}

	@RequestMapping(value = "/insertStep1", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insertStep1(ProjectApply projectApply, ApplyMajorResource majorResource) {
		projectApply.setResourceRequire(JSON.toJSONString(majorResource));
		projectApplyService.updateSelective(projectApply);
		ProjectApply projectApply2 = projectApplyService.get(projectApply.getId());
		return BaseOutput.success(String.valueOf(projectApply2.getId())).setData(projectApply2.getId() + ":" + projectApply2.getName());
	}

	@RequestMapping(value = "/insertStep2", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insertStep2(ProjectApply projectApply, ApplyDescription description) {
		projectApply.setDescription(JSON.toJSONString(description));
		projectApplyService.updateSelective(projectApply);
		ProjectApply projectApply2 = projectApplyService.get(projectApply.getId());
		return BaseOutput.success(String.valueOf(projectApply2.getId())).setData(projectApply2.getId() + ":" + projectApply2.getName());
	}

	@RequestMapping(value = "/insertStep3", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insertStep3(ProjectApply projectApply, ApplyGoalsFunctions goalsFunctions) {
		projectApply.setGoalsFunctions(JSON.toJSONString(goalsFunctions));
		projectApplyService.updateSelective(projectApply);
		ProjectApply projectApply2 = projectApplyService.get(projectApply.getId());
		return BaseOutput.success(String.valueOf(projectApply2.getId())).setData(projectApply2.getId() + ":" + projectApply2.getName());
	}

	@RequestMapping(value = "/insertStep", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insertStep(ProjectApply projectApply) {
		projectApplyService.updateSelective(projectApply);
		ProjectApply projectApply2 = projectApplyService.get(projectApply.getId());
		return BaseOutput.success(String.valueOf(projectApply2.getId())).setData(projectApply2.getId() + ":" + projectApply2.getName());
	}

	@RequestMapping(value = "/insertRoi", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> insertRoi(RoiUpdateDto roi) {
		try {
			projectApplyService.updateRoi(roi);
		} catch (ProjectApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
		ProjectApply projectApply2 = projectApplyService.get(roi.getApplyId());
		return BaseOutput.success(String.valueOf(projectApply2.getId())).setData(projectApply2.getId() + ":" + projectApply2.getName());
	}

	@RequestMapping(value = "/submit", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput submit(ProjectApply projectApply, ApplyFiles files) {
		try {
			projectApplyService.submit(projectApply, files);
			return BaseOutput.success(String.valueOf(projectApply.getId())).setData(projectApply.getId() + ":" + projectApply.getName());
		} catch (ProjectApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping("/loadImpact")
	@ResponseBody
	public List<ApplyImpact> loadImpact(Long id) {
		return projectApplyService.loadImpact(id);
	}

	@RequestMapping("/loadFiles")
	@ResponseBody
	public List<Map> loadFiles(Long id) throws Exception {

		Map<Object, Object> metadata = new HashMap<>();
		metadata.put("created", JSON.parse("{provider:'datetimeProvider'}"));
		return ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(projectApplyService.listFiles(id)));
	}

	@RequestMapping("/loadPlan")
	@ResponseBody
	public List<Map> loadPlan(Long id) throws Exception {
		return projectApplyService.loadPlan(id);
	}

	@RequestMapping("/loadRisk")
	@ResponseBody
	public List<ApplyRisk> loadRisk(Long id) {
		return projectApplyService.loadRisk(id);
	}

	@RequestMapping("/loadApply")
	@ResponseBody
	public Map loadApply(Long id) throws Exception {
		ProjectApply projectApply = DTOUtils.newDTO(ProjectApply.class);
		projectApply.setId(id);

		Map<Object, Object> metadata = new HashMap<>();
		metadata.put("projectLeader", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("productManager", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("developmentManager", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("testManager", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("businessOwner", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("dep", JSON.parse("{provider:'depProvider'}"));
		metadata.put("expectedLaunchDate", JSON.parse("{provider:'datetimeProvider'}"));
		metadata.put("estimateLaunchDate", JSON.parse("{provider:'datetimeProvider'}"));
		metadata.put("startDate", JSON.parse("{provider:'datetimeProvider'}"));
		metadata.put("endDate", JSON.parse("{provider:'datetimeProvider'}"));

		List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, projectApplyService.listByExample(projectApply));
		return maps.get(0);
	}

	@ApiOperation("删除ProjectApply")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "ProjectApply的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> delete(Long id) {
		ProjectApply apply = projectApplyService.get(id);
		if (apply == null) {
			return BaseOutput.failure("申请记录不存在");
		}
		if (!apply.getCreateMemberId().equals(SessionContext.getSessionContext().getUserTicket().getId())) {
			return BaseOutput.failure("只有申请人才能删除记录");
		}
		try {
			projectApplyService.deleteProjectApply(id);
			return BaseOutput.success("删除成功").setData(apply.getId() + ":" + apply.getName());
		} catch (ProjectApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/checkName")
	public @ResponseBody Object checkName(String name, String org) {
		if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(org)) {
			if (name.equals(org)) {
				return true;
			}
		}
		if (StringUtils.isNotBlank(name)) {
			ProjectApplyQueryDto appExample = DTOUtils.newDTO(ProjectApplyQueryDto.class);
			appExample.setName(name);
			List<ProjectApply> applyList = projectApplyService.listByExample(appExample);

			Project project = DTOUtils.newDTO(Project.class);
			project.setName(name);
			List<Project> projectList = projectService.listByExample(project);
			return CollectionUtils.isEmpty(applyList) && CollectionUtils.isEmpty(projectList);
		}
		return false;
	}
}