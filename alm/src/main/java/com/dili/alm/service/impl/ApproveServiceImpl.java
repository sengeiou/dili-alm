package com.dili.alm.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.BpmcUtil;
import com.dili.alm.component.MailManager;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ApproveMapper;
import com.dili.alm.dao.DemandProjectMapper;
import com.dili.alm.dao.ProjectActionRecordMapper;
import com.dili.alm.dao.ProjectCostMapper;
import com.dili.alm.dao.ProjectEarningMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.domain.ActionDateType;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.DemandProject;
import com.dili.alm.domain.DemandProjectStatus;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectAction;
import com.dili.alm.domain.ProjectActionRecord;
import com.dili.alm.domain.ProjectActionType;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectComplete;
import com.dili.alm.domain.ProjectCost;
import com.dili.alm.domain.ProjectEarning;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.TeamRole;
import com.dili.alm.domain.VerifyApproval;
import com.dili.alm.domain.dto.ApproveDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.RoiDto;
import com.dili.alm.domain.dto.apply.ApplyApprove;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ApproveException;
import com.dili.alm.exceptions.ProjectApplyException;
import com.dili.alm.provider.ProjectTypeProvider;
import com.dili.alm.rpc.resolver.MyRuntimeRpc;
import com.dili.alm.rpc.resolver.MyTaskRpc;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectChangeService;
import com.dili.alm.service.ProjectCompleteService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.TeamService;
import com.dili.alm.service.VerifyApprovalService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.bpmc.sdk.dto.TaskIdentityDto;
import com.dili.bpmc.sdk.rpc.restful.TaskRpc;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.exception.AppException;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.SystemConfigUtils;
import com.dili.uap.sdk.domain.Role;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.rpc.RoleRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;
import com.google.common.collect.Lists;

import cn.afterturn.easypoi.word.WordExportUtil;

/**
 * ???MyBatis Generator?????????????????? This file was generated on 2017-12-04 17:39:37.
 */
@Service
public class ApproveServiceImpl extends BaseServiceImpl<Approve, Long> implements ApproveService {

	@Autowired
	private DataDictionaryService dataDictionaryService;

	@Autowired
	private MailManager mailManager;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private ProjectApplyService projectApplyService;

	@Autowired
	private ProjectChangeService projectChangeService;

	@Autowired
	private ProjectCompleteService projectCompleteService;

	@Autowired
	private ProjectMapper projectMapper;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private DemandProjectMapper demandProjectMapper;

	@Autowired
	private RoleRpc roleRpc;

	@Autowired
	private TeamService teamService;
	@Autowired
	private UserRpc userRpc;
	@Autowired
	private VerifyApprovalService verifyApprovalService;

	private String projectChangeMailTemplate;

	@Qualifier("mailContentTemplate")
	@Autowired
	private GroupTemplate groupTemplate;

	@Value("${spring.mail.username:}")
	private String mailFrom;
	@Autowired
	private ProjectActionRecordMapper parMapper;
	@Autowired
	private ProjectTypeProvider projectTypeProvider;
	@Autowired
	private ProjectEarningMapper projectEarningMapper;
	@Autowired
	private ProjectCostMapper projectCostMapper;
	@Autowired
	private ProjectVersionMapper projectVersionMapper;

	@Autowired
	private TaskRpc taskRpc;

	@Autowired
	private BpmcUtil bpmcUtil;
	
	@Autowired 
	private MyRuntimeRpc myRuntimeRpc;
	
	@Autowired
	private MyTaskRpc myTaskRpc;

	public ApproveServiceImpl() {
		super();
		InputStream in = null;
		try {
			Resource res = new ClassPathResource("conf/projectChangeMailTemplate.html");
			in = res.getInputStream();
			this.projectChangeMailTemplate = IOUtils.toString(in, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * alm3.5?????????????????????
	 * 
	 * @param id
	 * @param opt
	 * @param notes
	 * @throws ApproveException
	 */
	@Transactional(rollbackFor = ApplicationException.class)
	@Override
	public synchronized void applyApprove(Long id, String opt, String notes) throws ApproveException {
		Approve approve = this.get(id);
		if (approve.getStatus() != AlmConstants.ApplyState.APPROVE.getCode()) {
			throw new ApproveException("????????????????????????????????????");
		}
		String approveDescription = approve.getDescription();
		if (StringUtils.isNotBlank(approveDescription)) {
			List<ApplyApprove> approveList = JSON.parseArray(approveDescription, ApplyApprove.class);
			ApplyApprove current = approveList.stream().filter(v -> Objects.equals(v.getUserId(), SessionContext.getSessionContext().getUserTicket().getId())).findFirst().get();
			current.setApproveDate(new Date());
			current.setResult(opt);
			current.setNotes(notes);
			approve.setDescription(JSON.toJSONString(approveList));

			/*
			 * ?????????????????????????????????????????????
			 */

			switch (opt) {
			case "reject":
				approve.setStatus(AlmConstants.ApplyState.NOPASS.getCode());
				if (Objects.equals(approve.getType(), AlmConstants.ApproveType.APPLY.getCode())) {
					ProjectApply apply = projectApplyService.get(approve.getProjectApplyId());
					apply.setStatus(AlmConstants.ApplyState.NOPASS.getCode());
					projectApplyService.updateSelective(apply);
					sendMail(apply, false);

				} else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.CHANGE.getCode())) {
					ProjectChange change = projectChangeService.get(approve.getProjectApplyId());
					change.setStatus(AlmConstants.ApplyState.NOPASS.getCode());
					projectChangeService.updateSelective(change);
					sendMail(change, false);
				} else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.COMPLETE.getCode())) {
					ProjectComplete complete = projectCompleteService.get(approve.getProjectApplyId());
					complete.setStatus(AlmConstants.ApplyState.NOPASS.getCode());
					projectCompleteService.updateSelective(complete);
					sendMail(complete, false);
				}
				break;
			case "accept":
				if (Objects.equals(approve.getType(), AlmConstants.ApproveType.APPLY.getCode()) && approveList.get(1).equals(current)) {
					approve.setStatus(AlmConstants.ApplyState.PASS.getCode());
					ProjectApply apply = projectApplyService.get(approve.getProjectApplyId());
					apply.setStatus(AlmConstants.ApplyState.PASS.getCode());
					// ????????????????????????????????????
					Project project = buildProject(apply, approve);
					// ??????????????????
					this.addProjectAction(project);
					projectApplyService.updateSelective(apply);
					sendMail(apply, true);
				} else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.CHANGE.getCode())) {
					approve.setStatus(AlmConstants.ApplyState.PASS.getCode());
					ProjectChange change = projectChangeService.get(approve.getProjectApplyId());
					change.setStatus(AlmConstants.ApplyState.PASS.getCode());
					projectChangeService.updateSelective(change);
					updateProjectEndDate(change);
					insertProjectChangeActionRecord(change);
					sendMail(change, true);
				} else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.COMPLETE.getCode())) {
					approve.setStatus(AlmConstants.ApplyState.PASS.getCode());
					ProjectComplete complete = projectCompleteService.get(approve.getProjectApplyId());
					complete.setStatus(AlmConstants.ApplyState.PASS.getCode());
					closeProject(complete);
					insertProjectCompleteActionRecord(complete);
					projectCompleteService.updateSelective(complete);
					sendMail(complete, true);
				}
				break;
			default:
				break;
			}
			updateSelective(approve);
		}
	}

	@Transactional(rollbackFor = ApplicationException.class)
	@Override
	public synchronized void bpmcApprove(String taskId, String opt, String notes, Boolean isManager) throws ApproveException {
		BaseOutput<Map<String, Object>> map = taskRpc.getVariables(taskId);
		Long id = Long.valueOf(map.getData().get("businessKey").toString());
		Approve approve = this.get(id);
		if (approve.getStatus() != AlmConstants.ApplyState.APPROVE.getCode()) {
			throw new ApproveException("????????????????????????????????????");
		}
		/*
		 * ?????????????????????????????????????????????
		 */
		String approveDescription = approve.getDescription();
		List<ApplyApprove> approveList = new ArrayList<ApplyApprove>();
		if (!WebUtil.strIsEmpty(approveDescription)) {
			approveList = JSON.parseArray(approveDescription, ApplyApprove.class);
		}
		ApplyApprove current = new ApplyApprove();
		Long userId = SessionContext.getSessionContext().getUserTicket().getId();
		current.setUserId(userId);
		BaseOutput<List<Role>> listRoleByUserId = roleRpc.listByUser(Long.valueOf(userId), "");
		current.setRole(listRoleByUserId.getData().stream().map(Role::getRoleName).collect(Collectors.joining(",")));
		current.setApproveDate(new Date());
		current.setResult(opt);
		current.setNotes(notes);
		// ????????????
		if (approveList != null && approveList.size() > 0) {
			ApplyApprove applyApprove = approveList.get(approveList.size() - 1);
			if (applyApprove.getUserId().longValue() == current.getUserId().longValue() && applyApprove.getRole().equals(current.getRole()) && applyApprove.getResult().equals(current.getResult())) {
				throw new ApproveException("??????????????????????????????");
			}
		}
		approveList.add(current);
		approve.setDescription(JSON.toJSONString(approveList));

		switch (opt) {
		case "reject":
			Map<String, Object> map1 = new HashMap<String, Object>();

			BaseOutput<Map<String, Object>> taskVariablesOutput = taskRpc.getVariables(taskId);
			if (!taskVariablesOutput.isSuccess()) {
				throw new AppException(taskVariablesOutput.getMessage());
			}
			Map<String, Object> variables = taskVariablesOutput.getData();
			// ????????????????????????
			int statusValue = AlmConstants.ApplyState.NOPASS.getCode();
			if (variables.containsKey("reStatus")) {
				map1.put("reStatus", "1");
				statusValue = AlmConstants.ApplyState.REFUSED.getCode();
			} else {
				map1.put("reStatus", "0");
			}
			approve.setStatus(statusValue);
			if (Objects.equals(approve.getType(), AlmConstants.ApproveType.APPLY.getCode())) {
				ProjectApply apply = projectApplyService.get(approve.getProjectApplyId());
				apply.setStatus(statusValue);
				if (statusValue == AlmConstants.ApplyState.REFUSED.getCode()) {
					// ????????????
					DemandProject demandProject = new DemandProject();
					demandProject.setProjectNumber(apply.getNumber());
					demandProject.setStatus(DemandProjectStatus.ASSOCIATED.getValue());
					List<DemandProject> select = this.demandProjectMapper.select(demandProject);
					if (select != null && select.size() > 0) {
						int delete = this.demandProjectMapper.delete(demandProject);
						if (delete != select.size()) {
							throw new ApproveException("??????????????????");
						}
					}
				}
				projectApplyService.updateSelective(apply);
				sendMail(apply, false);

			} else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.CHANGE.getCode())) {
				ProjectChange change = projectChangeService.get(approve.getProjectApplyId());
				change.setStatus(statusValue);
				projectChangeService.updateSelective(change);
				sendMail(change, false);
			} else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.COMPLETE.getCode())) {
				ProjectComplete complete = projectCompleteService.get(approve.getProjectApplyId());
				complete.setStatus(statusValue);
				projectCompleteService.updateSelective(complete);
				sendMail(complete, false);
			}
			map1.put("approved", "false");
			BaseOutput<String> output = myTaskRpc.complete(taskId, map1);
			if (!output.isSuccess()) {
				LOGGER.error(output.getMessage());
				throw new ApproveException("??????????????????");
			}
			break;
		case "accept":
			Map<String, Object> map2 = new HashMap<String, Object>();
			map2.put("approved", "true");
			if (Objects.equals(approve.getType(), AlmConstants.ApproveType.APPLY.getCode()) && isManager) {
				approve.setStatus(AlmConstants.ApplyState.PASS.getCode());
				ProjectApply apply = projectApplyService.get(approve.getProjectApplyId());
				apply.setStatus(AlmConstants.ApplyState.PASS.getCode());
				// ????????????????????????????????????
				Project project = buildProject(apply, approve);
				// ??????????????????
				this.addProjectAction(project);
				projectApplyService.updateSelective(apply);
				sendMail(apply, true);
			} else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.CHANGE.getCode()) && isManager) {
				approve.setStatus(AlmConstants.ApplyState.PASS.getCode());
				ProjectChange change = projectChangeService.get(approve.getProjectApplyId());
				change.setStatus(AlmConstants.ApplyState.PASS.getCode());
				projectChangeService.updateSelective(change);
				updateProjectEndDate(change);
				insertProjectChangeActionRecord(change);
				sendMail(change, true);
			} else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.COMPLETE.getCode()) && isManager) {
				approve.setStatus(AlmConstants.ApplyState.PASS.getCode());
				ProjectComplete complete = projectCompleteService.get(approve.getProjectApplyId());
				complete.setStatus(AlmConstants.ApplyState.PASS.getCode());
				closeProject(complete);
				insertProjectCompleteActionRecord(complete);
				projectCompleteService.updateSelective(complete);
				sendMail(complete, true);
			}
			BaseOutput<String> output2 = myTaskRpc.complete(taskId, map2);
			if (!output2.isSuccess()) {
				LOGGER.error(output2.getMessage());
				throw new ApproveException("??????????????????");
			}
			break;
		default:
			break;
		}

		updateSelective(approve);
	}

	private void insertProjectCompleteActionRecord(ProjectComplete complete) throws ApproveException {

		// ??????????????????
		ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_COMPLETE_APPLY.getCode());
		par.setActionDate(complete.getCreated());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		par.setProjectId(complete.getProjectId());
		int rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("??????????????????????????????");
		}

		Date now = new Date();

		// ????????????????????????
		par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_COMPLETE_APPROVE.getCode());
		par.setActionDate(now);
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		par.setProjectId(complete.getProjectId());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("??????????????????????????????");
		}

		// ????????????
		par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_COMPLETE.getCode());
		par.setActionDate(now);
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		par.setProjectId(complete.getProjectId());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("??????????????????????????????");
		}
	}

	private void insertProjectChangeActionRecord(ProjectChange change) throws ApproveException {
		// ????????????????????????
		ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_CHANGE_APPLY.getCode());
		par.setActionDate(change.getSubmitDate());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		par.setProjectId(change.getProjectId());
		int rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("??????????????????????????????");
		}
		// ????????????????????????
		par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_CHANGE_APPROVE.getCode());
		par.setActionDate(new Date());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		par.setProjectId(change.getProjectId());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("??????????????????????????????");
		}
	}

	@Override
	public void buildApplyApprove(Map modelMap, Long id) {
		Approve approve = this.get(id);

		// ??????Provider
		Map<Object, Object> metadata = new HashMap<>();
		metadata.put("projectLeader", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("businessOwner", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("projectType", JSON.parse("{provider:'projectTypeProvider'}"));
		metadata.put("dep", JSON.parse("{provider:'depProvider'}"));
		metadata.put("createMemberId", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("created", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("expectedLaunchDate", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("estimateLaunchDate", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("startDate", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("endDate", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("status", JSON.parse("{provider:'approveStateProvider'}"));

		Map dto = null;
		try {
			dto = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(approve)).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Objects.requireNonNull(dto).remove("description");

		modelMap.put("apply", dto);
		modelMap.put("extend", dto.remove("extend"));
		modelMap.put("json", JSON.toJSON(dto));

	}

	@Override
	public void buildChangeApprove(Map modelMap, Long id) {
		Approve approve = this.getActualDao().selectByPrimaryKey(id);

		String approveDescription = approve.getDescription();

		// ??????Provider
		Map<Object, Object> metadata = new HashMap<>();
		metadata.put("createMemberId", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("created", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("status", JSON.parse("{provider:'changeStateProvider'}"));

		Map dto = null;
		try {
			dto = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(approve)).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		modelMap.put("approve", dto);

		ProjectChange change = projectChangeService.get(approve.getProjectApplyId());
		modelMap.put("change", change);
		Project project = this.projectMapper.selectByPrimaryKey(change.getProjectId());
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata1 = new HashMap<>();

		JSONObject projectTypeProvider = new JSONObject();
		projectTypeProvider.put("provider", "projectTypeProvider");
		metadata1.put("type", projectTypeProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata1.put("originator", memberProvider);

		JSONObject provider = new JSONObject();
		provider.put("provider", "datetimeProvider");
		metadata1.put("validTimeBegin", provider);
		metadata1.put("validTimeEnd", provider);
		metadata1.put("created", provider);
		metadata1.put("modified", provider);
		metadata1.put("actualStartDate", provider);

		try {
			Map map = ValueProviderUtils.buildDataByProvider(metadata1, Arrays.asList(project)).get(0);
			map.put("numberAndName", map.get("serialNumber") + "(" + map.get("name") + ")");
			modelMap.put("project1", map);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public Approve buildCompleteApprove(Map modelMap, Long id) {
		Approve approve = this.get(id);

		String approveDescription = approve.getDescription();

		// ??????Provider
		Map<Object, Object> metadata = new HashMap<>();
		metadata.put("createMemberId", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("created", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("status", JSON.parse("{provider:'approveStateProvider'}"));

		Map dto = null;
		try {
			dto = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(approve)).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		modelMap.put("approve", dto);

		ProjectComplete complete = projectCompleteService.get(approve.getProjectApplyId());
		modelMap.put("complete", complete);
		return approve;
	}

	@Override
	public void downloadProjectDoc(AlmConstants.ApproveType approveType, Long id, OutputStream os) {
		switch (approveType) {
		case APPLY:
			try {
				ProjectApply apply = projectApplyService.get(id);
				Map<String, Object> map = new HashMap<>();
				Map<Object, Object> metadata = new HashMap<>(2);
				metadata.put("projectLeader", JSON.parse("{provider:'memberProvider'}"));
				metadata.put("productManager", JSON.parse("{provider:'memberProvider'}"));
				metadata.put("developmentManager", JSON.parse("{provider:'memberProvider'}"));
				metadata.put("testManager", JSON.parse("{provider:'memberProvider'}"));
				metadata.put("businessOwner", JSON.parse("{provider:'memberProvider'}"));
				metadata.put("dep", JSON.parse("{provider:'depProvider'}"));
				metadata.put("expectedLaunchDate", JSON.parse("{provider:'dateProvider'}"));
				metadata.put("estimateLaunchDate", JSON.parse("{provider:'dateProvider'}"));
				metadata.put("startDate", JSON.parse("{provider:'dateProvider'}"));
				metadata.put("endDate", JSON.parse("{provider:'dateProvider'}"));
				metadata.put("type", JSON.parse("{provider:'projectTypeProvider'}"));
				List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(apply));
				Map applyDTO = maps.get(0);
				map.put("apply", applyDTO);

				projectApplyService.buildStepOne(map, applyDTO);

				map.put("planDto", projectApplyService.loadPlan(id));

				RoiDto roi = new RoiDto();
				ProjectEarning peQuery = DTOUtils.newDTO(ProjectEarning.class);
				peQuery.setApplyId(id);
				List<ProjectEarning> earnings = this.projectEarningMapper.select(peQuery);
				roi.setEarnings(earnings);

				ProjectCost pcQuery = DTOUtils.newDTO(ProjectCost.class);
				pcQuery.setApplyId(id);
				List<ProjectCost> costs = this.projectCostMapper.select(pcQuery);
				roi.setCosts(costs);
				List<ProjectEarning> businessIndicators = roi.getBusinessIndicators();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				for (int i = 0; i < businessIndicators.size(); i++) {
					map.put("indicatorName" + i, businessIndicators.get(i).getIndicatorName());
					map.put("indicatorCurrentStatus" + i, businessIndicators.get(i).getIndicatorCurrentStatus());
					map.put("projectObjective" + i, businessIndicators.get(i).getProjectObjective());
					Date date = businessIndicators.get(i).getImplemetionDate();
					map.put("implemetionDate" + i, date);
				}
				List<ProjectEarning> efficiencyAndPerformanceIndicators = roi.getEfficiencyAndPerformanceIndicators();
				int size = businessIndicators.size();
				for (int i = 0; i < efficiencyAndPerformanceIndicators.size(); i++) {
					map.put("indicatorName" + (i + size), efficiencyAndPerformanceIndicators.get(i).getIndicatorName());
					map.put("indicatorCurrentStatus" + (i + size), efficiencyAndPerformanceIndicators.get(i).getIndicatorCurrentStatus());
					map.put("projectObjective" + (i + size), efficiencyAndPerformanceIndicators.get(i).getProjectObjective());
					Date date = efficiencyAndPerformanceIndicators.get(i).getImplemetionDate();
					map.put("implemetionDate" + (i + size), date);
				}
				map.put("roi", roi);

				map.put("loadImpact", projectApplyService.loadImpact(id));
				map.put("loadRisk", projectApplyService.loadRisk(id));
				map.put("descDto", JSON.parseObject(apply.getDescription()));
				map.put("gf", JSON.parseObject(apply.getGoalsFunctions()));
				exportWord("apply", os, map);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case CHANGE:
			ProjectChange change = projectChangeService.get(id);
			try {
				Project project = projectService.get(change.getProjectId());
				Map<String, Object> map = new HashMap<>();
				map.put("name", change.getName());
				map.put("number", change.getNumber());
				map.put("projectName", change.getProjectName());
				map.put("projectType", AlmCache.getInstance().getProjectTypeMap().get(project.getType()));
				map.put("type", AlmCache.getInstance().getChangeType().get(change.getType()));
				map.put("workingHours", change.getWorkingHours());
				map.put("affectsOnline", change.getAffectsOnline() == 1 ? "???" : "???");
				map.put("estimateLaunchDate", DateUtil.getDate(change.getEstimateLaunchDate()));
				map.put("content", StringUtils.isBlank(change.getContent()) ? "???" : change.getContent());
				map.put("effects", StringUtils.isBlank(change.getEffects()) ? "???" : change.getEffects());
				exportWord("change", os, map);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case COMPLETE:
			ProjectComplete complete = projectCompleteService.get(id);
			try {
				Map<String, Object> map = new HashMap<>();
				Project project = DTOUtils.newDTO(Project.class);
				project.setId(complete.getProjectId());
				Map<Object, Object> metadata = new HashMap<>();
				metadata.put("type", JSON.parse("{provider:'projectTypeProvider'}"));
				metadata.put("projectManager", JSON.parse("{provider:'memberProvider'}"));
				metadata.put("dep", JSON.parse("{provider:'depProvider'}"));
				metadata.put("businessOwner", JSON.parse("{provider:'memberProvider'}"));
				metadata.put("startDate", JSON.parse("{provider:'dateProvider'}"));
				metadata.put("endDate", JSON.parse("{provider:'dateProvider'}"));
				metadata.put("actualEndDate", JSON.parse("{provider:'dateProvider'}"));
				metadata.put("estimateLaunchDate", JSON.parse("{provider:'dateProvider'}"));
				metadata.put("launchDate", JSON.parse("{provider:'dateProvider'}"));
				List<Map> projectDtos = ValueProviderUtils.buildDataByProvider(metadata, projectService.listByExample(project));
				map.put("project", projectDtos.get(0));
				map.put("cl", complete);
				map.put("clrs", formatReason(complete.getReason()));
				map.put("if", formatInfo(complete.getInformation()));
				map.put("lpf", JSON.parseArray(complete.getPerformance(), Map.class));
				map.put("lq", JSON.parseArray(complete.getQuestion(), Map.class));
				map.put("mb", projectCompleteService.loadMembers(id));
				map.put("hd", JSON.parseArray(complete.getHardware(), Map.class));
				map.put("now", DateUtil.getDate(new Date()));
				exportWord("complete", os, map);
			} catch (Exception e) {
				e.printStackTrace();
			}
		default:
			break;
		}
	}

	public ApproveMapper getActualDao() {
		return (ApproveMapper) getDao();
	}

	@Override
	public void insertBefore(Approve as) throws ProjectApplyException {
		/*
		 * DataDictionaryDto code =
		 * dataDictionaryService.findByCode(AlmConstants.ROLE_CODE);
		 * List<DataDictionaryValueDto> values = code.getValues(); String roleId =
		 * values.stream().filter(v -> Objects.equals(v.getCode(),
		 * AlmConstants.ROLE_CODE_WYH_LEADER)).findFirst().map(DataDictionaryValue::
		 * getValue).orElse(null); String superRoleId = null;
		 * 
		 * if (roleId == null) { throw new ProjectApplyException("?????????????????????????????????"); }
		 * 
		 * List<ApplyApprove> approveList = Lists.newArrayList();
		 * 
		 * List<User> userByRole =
		 * userRpc.listUserByRoleId(Long.parseLong(roleId)).getData(); if
		 * (CollectionUtils.isEmpty(userByRole)) { throw new
		 * ProjectApplyException("?????????????????????????????????"); }
		 * 
		 * ApplyApprove approve = new ApplyApprove();
		 * approve.setUserId(userByRole.get(0).getId()); BaseOutput<List<Role>>
		 * listRoleByUserId =
		 * roleRpc.listByUser(Long.valueOf(userByRole.get(0).getId()),"");
		 * approve.setRole(listRoleByUserId.getData().stream().map(Role::getRoleName).
		 * collect(Collectors.joining(","))); approveList.add(approve);
		 * 
		 * // if (as.getType().equals(AlmConstants.ApproveType.APPLY.getCode())) {
		 * superRoleId = values.stream().filter(v -> Objects.equals(v.getCode(),
		 * AlmConstants.ROLE_CODE_WYH_SUPER_LEADER)).findFirst().map(DataDictionaryValue
		 * ::getValue).orElse(null); if (superRoleId == null) { throw new
		 * ProjectApplyException("?????????????????????????????????"); }
		 * 
		 * userByRole = userRpc.listUserByRoleId(Long.parseLong(superRoleId)).getData();
		 * if (CollectionUtils.isEmpty(userByRole)) { throw new
		 * ProjectApplyException("?????????????????????????????????"); }
		 * 
		 * approve = new ApplyApprove(); approve.setUserId(userByRole.get(0).getId());
		 * BaseOutput<List<Role>> listRoleByUserId1 =
		 * roleRpc.listByUser(Long.valueOf(userByRole.get(0).getId()),"");
		 * approve.setRole(listRoleByUserId1.getData().stream().map(Role::getRoleName).
		 * collect(Collectors.joining(","))); approveList.add(approve); // }
		 * 
		 * as.setDescription(JSON.toJSONString(approveList));
		 */
		as.setDescription(null);
		int rows = insertSelective(as);
		if (rows <= 0) {
			throw new ProjectApplyException("????????????????????????");
		}

	}

	@Override
	public void updateBefore(Approve as) throws ProjectApplyException {
		/*
		 * DataDictionaryDto code =
		 * dataDictionaryService.findByCode(AlmConstants.ROLE_CODE);
		 * List<DataDictionaryValueDto> values = code.getValues(); String roleId =
		 * values.stream().filter(v -> Objects.equals(v.getCode(),
		 * AlmConstants.ROLE_CODE_WYH_LEADER)).findFirst().map(DataDictionaryValue::
		 * getValue).orElse(null); String superRoleId = null;
		 * 
		 * if (roleId == null) { throw new ProjectApplyException("?????????????????????????????????"); }
		 * 
		 * List<ApplyApprove> approveList = Lists.newArrayList();
		 * 
		 * List<User> userByRole =
		 * userRpc.listUserByRoleId(Long.parseLong(roleId)).getData(); if
		 * (CollectionUtils.isEmpty(userByRole)) { throw new
		 * ProjectApplyException("?????????????????????????????????"); }
		 * 
		 * ApplyApprove approve = new ApplyApprove();
		 * approve.setUserId(userByRole.get(0).getId()); BaseOutput<List<Role>>
		 * listRoleByUserId =
		 * roleRpc.listByUser(Long.valueOf(userByRole.get(0).getId()),"");
		 * approve.setRole(listRoleByUserId.getData().stream().map(Role::getRoleName).
		 * collect(Collectors.joining(","))); approveList.add(approve);
		 * 
		 * // if (as.getType().equals(AlmConstants.ApproveType.APPLY.getCode())) {
		 * superRoleId = values.stream().filter(v -> Objects.equals(v.getCode(),
		 * AlmConstants.ROLE_CODE_WYH_SUPER_LEADER)).findFirst().map(DataDictionaryValue
		 * ::getValue).orElse(null); if (superRoleId == null) { throw new
		 * ProjectApplyException("?????????????????????????????????"); }
		 * 
		 * userByRole = userRpc.listUserByRoleId(Long.parseLong(superRoleId)).getData();
		 * if (CollectionUtils.isEmpty(userByRole)) { throw new
		 * ProjectApplyException("?????????????????????????????????"); }
		 * 
		 * approve = new ApplyApprove(); approve.setUserId(userByRole.get(0).getId());
		 * BaseOutput<List<Role>> listRoleByUserId1 =
		 * roleRpc.listByUser(Long.valueOf(userByRole.get(0).getId()),"");
		 * approve.setRole(listRoleByUserId1.getData().stream().map(Role::getRoleName).
		 * collect(Collectors.joining(","))); approveList.add(approve); // }
		 * 
		 * as.setDescription(JSON.toJSONString(approveList));
		 */
		int rows = updateSelective(as);
		if (rows <= 0) {
			throw new ProjectApplyException("????????????????????????");
		}

	}

	@Override
	public BaseOutput verity(Long id, String opt, String notes) {
		VerifyApproval verifyApproval = DTOUtils.newDTO(VerifyApproval.class);
		verifyApproval.setApproveId(id);
		verifyApproval.setApprover(SessionContext.getSessionContext().getUserTicket().getId());
		verifyApproval.setCreated(new Date());
		verifyApproval.setResult(opt);
		verifyApproval.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
		verifyApprovalService.insert(verifyApproval);
		Approve approve = get(id);
		approve.setStatus(Objects.equals(opt, "accept") ? AlmConstants.ChangeState.VRIFY.getCode() : approve.getStatus());
		updateSelective(approve);
		return BaseOutput.success();
	}

	@Override
	@Transactional(rollbackFor = ApplicationException.class)
	public BaseOutput changeVerity(Long id, String opt, String notes, String taskId) {
		VerifyApproval verifyApproval = DTOUtils.newDTO(VerifyApproval.class);
		verifyApproval.setApproveId(id);
		verifyApproval.setApprover(SessionContext.getSessionContext().getUserTicket().getId());
		verifyApproval.setCreated(new Date());
		verifyApproval.setResult(opt);
		verifyApproval.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
		try {
			Approve approve = get(id);
			Map<String, Object> map2 = new HashMap<String, Object>();
			verifyApprovalService.insert(verifyApproval);
			if (Objects.equals(opt, "accept")) {
				approve.setStatus(AlmConstants.ChangeState.VRIFY.getCode());
				updateSelective(approve);
				map2.put("approved", "true");
				BaseOutput<String> output2 = myTaskRpc.complete(taskId, map2);
				if (!output2.isSuccess()) {
					LOGGER.error(output2.getMessage());
					throw new ApproveException("??????????????????");
				}
			} else {
				map2.put("approved", "false");
				BaseOutput<String> output2 = myTaskRpc.complete(taskId, map2);
				if (!output2.isSuccess()) {
					LOGGER.error(output2.getMessage());
					throw new ApproveException("??????????????????");
				}
			}
		} catch (ApproveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return BaseOutput.failure(e.getMessage());
		}

		return BaseOutput.success();
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @throws ProjectApplyException
	 */
	private Project buildProject(ProjectApply apply, Approve approve) throws ApproveException {
		Project project = DTOUtils.newDTO(Project.class);
		project.setSerialNumber(apply.getNumber());
		project.setName(apply.getName());
		project.setType(apply.getType());
		project.setProjectManager(apply.getProjectLeader());
		project.setDevelopManager(apply.getDevelopmentManager());
		project.setTestManager(apply.getTestManager());
		project.setProductManager(apply.getProductManager());
		project.setStartDate(apply.getStartDate());
		project.setEndDate(apply.getEndDate());
		project.setDep(apply.getDep());
		project.setApplyId(apply.getId());
		project.setBusinessOwner(apply.getBusinessOwner());
		project.setProjectState(ProjectState.NOT_START.getValue());
		project.setOriginator(apply.getCreateMemberId());
		project.setEstimateLaunchDate(apply.getEstimateLaunchDate());
		int rows = projectService.insertSelective(project);
		if (rows <= 0) {
			throw new ApproveException("??????????????????");
		}
		buildTeam(project);
		return project;
	}

	private void addProjectAction(Project project) throws ApproveException {
		Date now = new Date();
		// ????????????????????????????????????
		ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_PLAN.getCode());
		par.setActionDateType(ActionDateType.PERIOD.getValue());
		par.setActionStartDate(project.getStartDate());
		par.setActionEndDate(project.getEndDate());
		par.setProjectId(project.getId());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		int rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("??????????????????????????????");
		}
		// ??????????????????????????????????????????
		par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_APPLY.getCode());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionDate(now);
		par.setProjectId(project.getId());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("??????????????????????????????");
		}
		// ????????????????????????????????????????????????
		par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_APPLY_APPROVE.getCode());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionDate(now);
		par.setProjectId(project.getId());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("??????????????????????????????");
		}
	}

	private void buildTeam(Project project) throws ApproveException {
		for (TeamRole teamRole : TeamRole.values()) {
			Team team = DTOUtils.newDTO(Team.class);
			team.setProjectId(project.getId());
			team.setJoinTime(new Date());
			team.setDeletable(false);
			switch (teamRole) {
			case PROJECT_MANAGER:
				team.setMemberId(project.getProjectManager());
				team.setRole(TeamRole.PROJECT_MANAGER.getValue());
				break;
			case PRODUCT_MANAGER:
				team.setMemberId(project.getProductManager());
				team.setRole(TeamRole.PRODUCT_MANAGER.getValue());
				break;
			case TEST_MANAGER:
				team.setMemberId(project.getTestManager());
				team.setRole(TeamRole.TEST_MANAGER.getValue());
				break;
			case DEVELOP_MANAGER:
				team.setMemberId(project.getDevelopManager());
				team.setRole(TeamRole.DEVELOP_MANAGER.getValue());
				break;
			default:
				return;
			}
			int rows = teamService.insertTeamAndUserDataAuth(team);
			if (rows <= 0) {
				throw new ApproveException("??????????????????");
			}

		}
	}

	private void closeProject(ProjectComplete complete) {
		Long projectId = complete.getProjectId();
		Project project = projectService.get(projectId);
		Date now = new Date();
		project.setActualEndDate(now);
		project.setProjectState(ProjectState.CLOSED.getValue());
		project.setActualEndDate(now);
		project.setCloseTime(now);
		projectService.updateSelective(project);
	}

	private void exportWord(String name, OutputStream os, Map map) throws Exception {
		InputStream stream = getClass().getClassLoader().getResourceAsStream("word/" + name + ".docx");
		File targetFile = new File(name + ".docx");
		if (!targetFile.exists()) {
			FileUtils.copyInputStreamToFile(stream, targetFile);
		}
		XWPFDocument doc = WordExportUtil.exportWord07(targetFile.getPath(), map);
		doc.write(os);
		os.close();
	}

	private String formatInfo(String info) {
		switch (info) {
		case "0":
			return "????????????????????????";
		case "1":
			return "?????????????????????????????????????????????????????????";
		default:
			return "";
		}
	}

	private String formatReason(String reason) {

		switch (reason) {
		case "0":
			return "????????????";
		case "1":
			return "????????????";
		default:
			return "???????????????" + reason;
		}
	}

	/**
	 * ????????????????????????????????????????????????
	 *
	 * @return userId
	 */
	private Long getApproveLeader() {
		List<DataDictionaryValueDto> values = dataDictionaryService.findByCode(AlmConstants.ROLE_CODE).getValues();

		String roleId = values.stream().filter(v -> Objects.equals(v.getCode(), AlmConstants.ROLE_CODE_WYH_LEADER)).findFirst().map(DataDictionaryValue::getValue).orElse(null);

		if (roleId != null) {
			List<User> users = userRpc.listUserByRoleId(Long.valueOf(roleId)).getData();
			return users.stream().findFirst().map(User::getId).orElse(null);
		}

		return null;
	}

	/**
	 * ??????????????????????????????????????????????????????
	 *
	 * @return userId
	 */
	private Long getApproveSuperLeader() {
		//???????????????
		List<DataDictionaryValueDto> values = dataDictionaryService.findByCode(AlmConstants.ROLE_CODE).getValues();
		//????????????Id
		String roleId = values.stream().filter(v -> Objects.equals(v.getCode(), AlmConstants.ROLE_CODE_WYH_SUPER_LEADER)).findFirst().map(DataDictionaryValue::getValue).orElse(null);
		if (roleId != null) {
			List<User> users = userRpc.listUserByRoleId(Long.valueOf(roleId)).getData();
			return users.stream().findFirst().map(User::getId).orElse(null);
		}

		return null;
	}

	private void sendMail(ProjectApply projectApply, boolean accept) {
		sendMail(projectApply.getEmail().split(","), "????????????", projectApply.getName() + "???????????????" + (accept ? "??????????????????" : "???????????????"));
	}

	private void sendMail(ProjectChange change, boolean accept) {
		// ??????????????????
		Project project = this.projectMapper.selectByPrimaryKey(change.getProjectId());
		Map<Object, Object> viewModel = ProjectChangeServiceImpl.buildViewModel(change);
		Template template = this.groupTemplate.getTemplate(this.projectChangeMailTemplate);
		viewModel.put("projectType", this.projectTypeProvider.getDisplayText(project.getType(), null, null));
		template.binding("model", viewModel);
		String content = template.render();

		// ??????
		for (String addr : change.getEmail().split(",")) {
			try {
				this.mailManager.sendMail(this.mailFrom, addr, content, true, accept ? "??????????????????????????????" : "???????????????????????????", null);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	private void sendMail(ProjectComplete complete, boolean accept) {
		sendMail(complete.getEmail().split(","), "????????????", complete.getName() + "???????????????" + (accept ? "??????????????????" : "???????????????"));
	}

	private void sendMail(String[] sendTo, String title, String text) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(sendTo);
			message.setFrom(SystemConfigUtils.getProperty("spring.mail.username"));
			message.setSubject(title);
			message.setText(text);
			mailSender.send(message);
		} catch (Exception e) {
			LOGGER.error("??????????????????:", e);
		}
	}

	private void updateProjectEndDate(ProjectChange change) throws ApproveException {
		// ????????????????????????
		Project project = this.projectMapper.selectByPrimaryKey(change.getProjectId());
		project.setEstimateLaunchDate(change.getEstimateLaunchDate());
		project.setEndDate(change.getEndDate());
		int rows = this.projectMapper.updateByPrimaryKeySelective(project);
		if (rows <= 0) {
			throw new ApproveException("????????????????????????");
		}

	}

	@Override
	public Approve selectOne(Approve selectApprove) {
		return this.getDao().selectOne(selectApprove);
	}

	@Override
	public void getModel(ModelMap modelMap, String taskId) {
		BaseOutput<Map<String, Object>> map = taskRpc.getVariables(taskId);
		String id = (String) map.getData().get("businessKey");
		Approve approve = this.getDao().selectByPrimaryKey(Long.parseLong(id));
		// ??????Provider
		Map<Object, Object> metadata = new HashMap<>();
		metadata.put("projectLeader", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("businessOwner", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("projectType", JSON.parse("{provider:'projectTypeProvider'}"));
		metadata.put("dep", JSON.parse("{provider:'depProvider'}"));
		metadata.put("createMemberId", JSON.parse("{provider:'memberProvider'}"));
		metadata.put("created", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("expectedLaunchDate", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("estimateLaunchDate", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("startDate", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("endDate", JSON.parse("{provider:'dateProvider'}"));
		metadata.put("status", JSON.parse("{provider:'approveStateProvider'}"));

		Map dto = null;
		try {
			dto = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(approve)).get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Objects.requireNonNull(dto).remove("description");

		modelMap.put("apply", dto);
		modelMap.put("extend", dto.remove("extend"));
		modelMap.put("json", JSON.toJSON(dto));
		modelMap.put("taskId", taskId);
	}

	@Override
	public EasyuiPageOutput selectApproveByPage(Approve approve) {
		List<Approve> selectByExample = this.listByExample(approve);
		int total = this.getDao().selectCount(approve);
		List<ApproveDto> listByExample = DTOUtils.as(selectByExample, ApproveDto.class);
		bpmcUtil.fitLoggedUserIsCanHandledProcess(listByExample);
		List<ApproveDto> listDto = getIsSameAssignee(listByExample);

		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = null == approve.getMetadata() ? new HashMap<>() : approve.getMetadata();
		if (AlmConstants.ApproveType.CHANGE.getCode() == approve.getType()) {
			JSONObject changeStateProvider = new JSONObject();
			changeStateProvider.put("provider", "changeStateProvider");
			metadata.put("status", changeStateProvider);
			JSONObject changeTypeProvider = new JSONObject();
			changeTypeProvider.put("provider", "changeTypeProvider");
			metadata.put("type", changeTypeProvider);
		} else {
			JSONObject projectStatusProvider = new JSONObject();
			projectStatusProvider.put("provider", "approveStateProvider");
			metadata.put("status", projectStatusProvider);
			JSONObject projectTypeProvider = new JSONObject();
			projectTypeProvider.put("provider", "projectTypeProvider");
			metadata.put("type", projectTypeProvider);
		}

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("createMemberId", memberProvider);

		JSONObject provider = new JSONObject();
		provider.put("provider", "datetimeProvider");
		metadata.put("created", provider);
		metadata.put("modified", provider);

		approve.setMetadata(metadata);
		try {
			List list = ValueProviderUtils.buildDataByProvider(approve, listDto);
			return new EasyuiPageOutput(Long.valueOf(total), list);
		} catch (Exception e) {
			e.getMessage();
		}
		return 	new EasyuiPageOutput(0L, Collections.emptyList());


	}

	/**
	 * ????????????????????????????????????????????????
	 *
	 * @return userId
	 */
	private List<ApproveDto> getIsSameAssignee(List<ApproveDto> listDto) {
		// ????????????????????????????????????
		if (!CollectionUtils.isEmpty(listDto)) {
			Set<String> processInstanceIds = new HashSet<>();
			listDto.forEach(d -> {
				if (StringUtils.isNotBlank(d.getProcessInstanceId())) {
					processInstanceIds.add(d.getProcessInstanceId());
				}
			});
			if (!CollectionUtils.isEmpty(processInstanceIds)) {
				BaseOutput<List<TaskIdentityDto>> tiOutput = this.taskRpc.listTaskIdentityByProcessInstanceIds(new ArrayList<String>(processInstanceIds));
				if (tiOutput.isSuccess() && !CollectionUtils.isEmpty(tiOutput.getData())) {
					Long userId = SessionContext.getSessionContext().getUserTicket().getId();
					listDto.forEach(d -> {
						for (TaskIdentityDto taskIdentity : tiOutput.getData()) {
							if (taskIdentity.getProcessInstanceId().equals(d.getProcessInstanceId())) {
								if (StringUtils.isNotBlank(taskIdentity.getAssignee())) {
									if (Long.valueOf(taskIdentity.getAssignee()).equals(userId)) {
										d.setIsSameAssignee(1);
									} else {
										d.setIsSameAssignee(2);
									}
								} else {
									d.setIsSameAssignee(-1);
								}
							}

						}
					});
				}
			}
		}
		return listDto;
	}
}