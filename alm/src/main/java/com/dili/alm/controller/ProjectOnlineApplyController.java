package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.BpmConsts;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.ProjectOnlineApplyListQueryDto;
import com.dili.alm.domain.dto.ProjectOnlineApplyUpdateDto;
import com.dili.alm.domain.dto.ProjectOnlineSubsystemDto;
import com.dili.alm.exceptions.ProjectOnlineApplyException;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectOnlineApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.impl.ProjectOnlineApplyServiceImpl;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.rpc.restful.RuntimeRpc;
import com.dili.bpmc.sdk.rpc.restful.TaskRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;


import tk.mybatis.mapper.entity.Example;

/**
 * ???MyBatis Generator?????????????????? This file was generated on 2018-03-13 15:31:10.
 */
@Controller
@RequestMapping("/projectOnlineApply")
public class ProjectOnlineApplyController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectOnlineApplyController.class);
	private static final String DATA_AUTH_TYPE = "project";

	@Autowired
	ProjectOnlineApplyService projectOnlineApplyService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private FilesService fileService;
	@Autowired
	private DataDictionaryService ddService;
	private static final String EMAIL_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	@Autowired
	private TaskRpc taskRpc;
	@Autowired
	private RuntimeRpc runtimeRpc;

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public String detail(@RequestParam Long id, ModelMap modelMap) {
		try {
			ProjectOnlineApply vm = this.projectOnlineApplyService.getDetailViewData(id);
			modelMap.addAttribute("apply", ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm));
			return "projectOnlineApply/detail";
		} catch (ProjectOnlineApplyException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	@RequestMapping(value = "/projectManagerConfirm", method = RequestMethod.GET)
	public String projectManagerConfirmView(@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim, ModelMap modelMap) {
		try {
			BaseOutput<Map<String, Object>> output = this.taskRpc.getVariables(taskId);
			if (!output.isSuccess()) {
				LOGGER.error(output.getMessage());
				return "redirect:/projectOnlineApply/index.html";
			}
			String serialNumber = output.getData().get("businessKey").toString();
			ProjectOnlineApply vm = this.projectOnlineApplyService.getProjectManagerConfirmViewModel(serialNumber);
			modelMap.addAttribute("apply", ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm)).addAttribute("taskId", taskId).addAttribute("isNeedClaim", isNeedClaim);
		} catch (ProjectOnlineApplyException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "projectOnlineApply/projectManagerConfirm";
	}

	@ResponseBody
	@RequestMapping(value = "/projectManagerConfirm", method = RequestMethod.POST)
	public BaseOutput<Object> projectManagerConfirm(@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim, @RequestParam Long id, @RequestParam Integer result,
			@RequestParam(required = false) String description) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			return BaseOutput.failure("????????????");
		}
		try {
			this.projectOnlineApplyService.projectManagerConfirm(id, user.getId(), OperationResult.valueOf(result), description, taskId, isNeedClaim);
			ProjectOnlineApply vm = this.projectOnlineApplyService.getEasyUiRowData(id);
			return BaseOutput.success().setData(ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm));
		} catch (ProjectOnlineApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/testConfirm", method = RequestMethod.GET)
	public String testConfirmView(@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim, ModelMap modelMap) {
		try {
			BaseOutput<Map<String, Object>> output = this.taskRpc.getVariables(taskId);
			if (!output.isSuccess()) {
				LOGGER.error(output.getMessage());
				return "redirect:/projectOnlineApply/index.html";
			}
			String serialNumber = output.getData().get("businessKey").toString();
			ProjectOnlineApply vm = this.projectOnlineApplyService.getTestConfirmViewModel(serialNumber);
			modelMap.addAttribute("apply", ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm)).addAttribute("taskId", taskId).addAttribute("isNeedClaim", isNeedClaim);
		} catch (ProjectOnlineApplyException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "projectOnlineApply/testConfirm";
	}

	@ResponseBody
	@RequestMapping(value = "/testConfirm", method = RequestMethod.POST)
	public BaseOutput<Object> testConfirm(@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim, @RequestParam Long id, @RequestParam Integer result,
			@RequestParam(required = false) String description) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			return BaseOutput.failure("????????????");
		}
		try {
			this.projectOnlineApplyService.testerConfirm(id, user.getId(), OperationResult.valueOf(result), description, taskId, isNeedClaim);
			ProjectOnlineApply vm = this.projectOnlineApplyService.getEasyUiRowData(id);
			return BaseOutput.success().setData(ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm));
		} catch (ProjectOnlineApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value = "/startExecute", method = RequestMethod.POST)
	public BaseOutput<Object> startExecute(@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim, @RequestParam Long id, @RequestParam String executor,
			@RequestParam Integer result, @RequestParam(required = false) String description) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			return BaseOutput.failure("????????????");
		}
		if (StringUtils.isBlank(executor)) {
			return BaseOutput.failure("?????????????????????");
		}
		String[] strs = executor.split(",");
		if (strs.length <= 0) {
			return BaseOutput.failure("?????????????????????");
		}
		if (strs.length > 2) {
			return BaseOutput.failure("???????????????????????????");
		}
		Set<Long> executors = new HashSet<>(2);
		for (String str : strs) {
			executors.add(Long.valueOf(str));
		}
		try {
			this.projectOnlineApplyService.startExecute(id, user.getId(), executors, description, taskId, isNeedClaim);
			ProjectOnlineApply vm = this.projectOnlineApplyService.getEasyUiRowData(id);
			return BaseOutput.success().setData(ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm));
		} catch (ProjectOnlineApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/startExecute", method = RequestMethod.GET)
	public String startExecuteView(@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim, ModelMap modelMap) {
		try {
			BaseOutput<Map<String, Object>> output = this.taskRpc.getVariables(taskId);
			if (!output.isSuccess()) {
				LOGGER.error(output.getMessage());
				return "redirect:/projectOnlineApply/index.html";
			}
			String serialNumber = output.getData().get("businessKey").toString();
			ProjectOnlineApply vm = this.projectOnlineApplyService.getStartExecuteViewData(serialNumber);
			modelMap.addAttribute("apply", ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm)).addAttribute("taskId", taskId).addAttribute("isNeedClaim", isNeedClaim);
		} catch (ProjectOnlineApplyException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "projectOnlineApply/startExecute";
	}

	@RequestMapping(value = "/confirmExecute", method = RequestMethod.GET)
	public String confirmExecuteView(@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim, ModelMap modelMap) {
		try {
			BaseOutput<Map<String, Object>> output = this.taskRpc.getVariables(taskId);
			if (!output.isSuccess()) {
				LOGGER.error(output.getMessage());
				return "redirect:/projectOnlineApply/index.html";
			}
			String serialNumber = output.getData().get("businessKey").toString();
			ProjectOnlineApply vm = this.projectOnlineApplyService.getConfirmExecuteViewModel(serialNumber);
			modelMap.addAttribute("apply", ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm)).addAttribute("taskId", taskId).addAttribute("isNeedClaim", isNeedClaim);
		} catch (ProjectOnlineApplyException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "projectOnlineApply/confirmExecute";
	}

	@ResponseBody
	@RequestMapping(value = "/confirmExecute", method = RequestMethod.POST)
	public BaseOutput<Object> confirmExecute(@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim, @RequestParam Long id, @RequestParam Integer result,
			@RequestParam(required = false) String description) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			return BaseOutput.failure("????????????");
		}
		try {
			this.projectOnlineApplyService.excuteConfirm(id, user.getId(), OperationResult.valueOf(result), description, taskId, isNeedClaim);
			ProjectOnlineApply vm = this.projectOnlineApplyService.getEasyUiRowData(id);
			return BaseOutput.success().setData(ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm));
		} catch (ProjectOnlineApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/verify", method = RequestMethod.GET)
	public String verifyView(@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim, ModelMap modelMap) {
		try {
			BaseOutput<Map<String, Object>> output = this.taskRpc.getVariables(taskId);
			if (!output.isSuccess()) {
				LOGGER.error(output.getMessage());
				return "redirect:/projectOnlineApply/index.html";
			}
			String serialNumber = output.getData().get("businessKey").toString();
			ProjectOnlineApply vm = this.projectOnlineApplyService.getVerifyViewData(serialNumber);
			modelMap.addAttribute("apply", ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm)).addAttribute("taskId", taskId).addAttribute("isNeedClaim", isNeedClaim);
		} catch (ProjectOnlineApplyException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
		return "projectOnlineApply/verify";
	}

	@ResponseBody
	@RequestMapping(value = "/verify", method = RequestMethod.POST)
	public BaseOutput<Object> verify(@RequestParam String taskId, @RequestParam(defaultValue = "false") Boolean isNeedClaim, @RequestParam Long id, @RequestParam Integer result,
			@RequestParam(required = false) String description) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			return BaseOutput.failure("????????????");
		}
		try {
			this.projectOnlineApplyService.verify(id, user.getId(), OperationResult.valueOf(result), description, taskId, isNeedClaim);
			ProjectOnlineApply vm = this.projectOnlineApplyService.getEasyUiRowData(id);
			return BaseOutput.success().setData(ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm));
		} catch (ProjectOnlineApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("user", user);
		return "projectOnlineApply/index";
	}

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "/saveAndSubmit", method = RequestMethod.POST)
	public BaseOutput<Object> saveAndSubmit(@RequestParam(required = false) String taskId, @Valid ProjectOnlineApplyAddDto apply, BindingResult br) {
		if (br.hasErrors()) {
			return BaseOutput.failure(br.getFieldError().getDefaultMessage());
		}
		String error = this.validate(apply);
		if (StringUtils.isNotBlank(error)) {
			return BaseOutput.failure(error);
		}
		ProjectOnlineApplyUpdateDto dto = this.parseServiceModel(apply);
		try {
			projectOnlineApplyService.saveAndSubmit(dto, taskId);
			ProjectOnlineApply vm = this.projectOnlineApplyService.getEasyUiRowData(dto.getId());
			return BaseOutput.success().setData(ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm));
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addView(ModelMap modelMap) {
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			modelMap.addAttribute("projects", new ArrayList<>(0));
		} else {
			List<Long> projectIds = new ArrayList<>();
			dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("value").toString())));
			Example example = new Example(Project.class);
			Example.Criteria criteria = example.createCriteria();
			// ????????????????????????
			//criteria.andIn("id", projectIds).andNotEqualTo("projectState", ProjectState.CLOSED.getValue());
			criteria.andIn("id", projectIds);
			List<Project> projects = this.projectService.selectByExample(example);
			modelMap.addAttribute("projects", projects);
		}
		DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.MARKET_CODE);
		if (dd != null) {
			modelMap.addAttribute("markets", dd.getValues());
		}
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("applicant", user);
		return "projectOnlineApply/add";
	}

	@RequestMapping(value = "/update", method = RequestMethod.GET)
	public String updateView(@RequestParam(required = false) String taskId, @RequestParam(required = false, defaultValue = "false") Boolean dialog, @RequestParam(required = false) Long id,
			ModelMap modelMap) {
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return null;
		}
		List<Long> projectIds = new ArrayList<>();
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("value").toString())));
		Example example = new Example(Project.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", projectIds).andNotEqualTo("projectState", ProjectState.CLOSED.getValue());
		List<Project> projects = this.projectService.selectByExample(example);
		modelMap.addAttribute("projects", projects);
		List<Project> plist = this.projectService.list(null);
		modelMap.addAttribute("plist", plist).addAttribute("ulist", AlmCache.getInstance().getUserMap().values());
		DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.MARKET_CODE);
		if (dd != null) {
			modelMap.addAttribute("markets", dd.getValues());
		}
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("applicant", user);
		try {
			ProjectOnlineApply dto = null;
			if (StringUtils.isNotBlank(taskId)) {
				BaseOutput<Map<String, Object>> output = this.taskRpc.getVariables(taskId);
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					return "";
				}
				String serialNumber = output.getData().get(BpmConsts.ProjectOnlineApplyConstant.BUSINESS_KEY.getName()).toString();
				dto = this.projectOnlineApplyService.getEditViewDataBySerialNumber(serialNumber);
			} else if (id != null) {
				dto = this.projectOnlineApplyService.getEditViewDataById(id);
			} else {
				LOGGER.error("????????????");
				return "";
			}
			Map<Object, Object> model = ProjectOnlineApplyServiceImpl.buildApplyViewModel(dto);
			modelMap.addAttribute("apply", model).addAttribute("taskId", taskId);
			if (dialog) {
				return "projectOnlineApply/update";
			} else {
				return "projectOnlineApply/updateBody";
			}
		} catch (ProjectOnlineApplyException e) {
			LOGGER.error(e.getMessage(), e);
			return "";
		}
	}

	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<ProjectOnlineApply> list(ProjectOnlineApply projectOnlineApply) {
		return projectOnlineApplyService.list(projectOnlineApply);
	}

	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(ProjectOnlineApplyListQueryDto projectOnlineApply) throws Exception {
		projectOnlineApply.setSort("created");
		projectOnlineApply.setOrder("desc");
		return projectOnlineApplyService.listEasyuiPageByExample(projectOnlineApply, true).toString();
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/add", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> insert(@Valid ProjectOnlineApplyAddDto apply, BindingResult br) {
		if (br.hasErrors()) {
			return BaseOutput.failure(br.getFieldError().getDefaultMessage());
		}
		String error = this.validate(apply);
		if (StringUtils.isNotBlank(error)) {
			return BaseOutput.failure(error);
		}
		ProjectOnlineApplyUpdateDto dto = this.parseServiceModel(apply);
		try {
			projectOnlineApplyService.saveOrUpdate(dto);
			ProjectOnlineApply vm = this.projectOnlineApplyService.getEasyUiRowData(dto.getId());
			return BaseOutput.success().setData(ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm));
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> update(@Valid ProjectOnlineApplyAddDto apply, BindingResult br) {
		if (br.hasErrors()) {
			return BaseOutput.failure(br.getFieldError().getDefaultMessage());
		}
		String error = this.validate(apply);
		if (StringUtils.isNotBlank(error)) {
			return BaseOutput.failure(error);
		}
		ProjectOnlineApplyUpdateDto dto = this.parseServiceModel(apply);
		try {
			projectOnlineApplyService.saveOrUpdate(dto);
			ProjectOnlineApply vm = this.projectOnlineApplyService.getEasyUiRowData(dto.getId());
			return BaseOutput.success().setData(ProjectOnlineApplyServiceImpl.buildApplyViewModel(vm));
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		try {
			projectOnlineApplyService.deleteProjectOnlineApply(id);
			return BaseOutput.success();
		} catch (ProjectOnlineApplyException e) {
			return BaseOutput.failure(e.getMessage());
		}
	}

	private String validate(ProjectOnlineApplyAddDto apply) {
		// ???????????????
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			return "????????????";
		}
		apply.setApplicantId(user.getId());
		if (apply.getSqlFileId() == null && apply.getSqlFile() == null && StringUtils.isBlank(apply.getSqlScript())) {
			return "sql??????????????????";
		}
		if (apply.getStartupScriptFileId() == null && apply.getStartupScriptFile() == null && StringUtils.isBlank(apply.getStartupScript())) {
			return "????????????????????????";
		}
		if (apply.getDependencySystemFileId() == null && apply.getDependencySystemFile() == null && StringUtils.isBlank(apply.getDependencySystem())) {
			return "????????????????????????";
		}
		// ????????????id?????????????????????????????????
		apply.setSqlFileId(null);
		apply.setStartupScriptFileId(null);
		apply.setDependencySystemFileId(null);

		if (apply.getMarketVersion() && CollectionUtils.isEmpty(apply.getMarket())) {
			return "??????????????????";
		}

		if (StringUtils.isNotBlank(apply.getEmailAddress())) {
			Pattern pattern = Pattern.compile(EMAIL_REGEX);
			String[] emails = apply.getEmailAddress().split(";");
			for (String str : emails) {
				Matcher matcher = pattern.matcher(str.trim());
				if (!matcher.matches()) {
					return "?????????????????????";
				}
			}
		}
		return null;
	}

	private ProjectOnlineApplyUpdateDto parseServiceModel(ProjectOnlineApplyAddDto apply) {
		ProjectOnlineApplyUpdateDto dto = DTOUtils.toEntity(apply, ProjectOnlineApplyUpdateDto.class, false);
		dto.setMarkets(apply.getMarket());
		dto.setMarketVersion(apply.getMarketVersion());
		Files f = null;
		if (!apply.getDependencySystemFile().isEmpty()) {
			f = this.fileService.uploadFile(new MultipartFile[] { apply.getDependencySystemFile() }, null).get(0);
			dto.setDependencySystemFileId(f.getId());
		}
		if (!apply.getSqlFile().isEmpty()) {
			f = this.fileService.uploadFile(new MultipartFile[] { apply.getSqlFile() }, null).get(0);
			dto.setSqlFileId(f.getId());
		}
		if (!apply.getStartupScriptFile().isEmpty()) {
			f = this.fileService.uploadFile(new MultipartFile[] { apply.getStartupScriptFile() }, null).get(0);
			dto.setStartupScriptFileId(f.getId());
		}
		List<ProjectOnlineSubsystemDto> posDtos = new ArrayList<>();
		for (int i = 0; i < apply.getSubProjectName().size(); i++) {
			ProjectOnlineSubsystemDto posDto = new ProjectOnlineSubsystemDto();
			posDto.setManagerName(apply.getManagerId().get(i));
			posDto.setProjectName(apply.getSubProjectName().get(i));
			posDtos.add(posDto);
		}
		dto.setSubsystem(posDtos);
		String[] emails = apply.getEmailAddress().split(";");
		dto.setEmailAddress(Arrays.asList(emails));
		return dto;
	}
}