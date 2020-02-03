package com.dili.alm.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.MailManager;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ApproveMapper;
import com.dili.alm.dao.ProjectActionRecordMapper;
import com.dili.alm.dao.ProjectCostMapper;
import com.dili.alm.dao.ProjectEarningMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.domain.ActionDateType;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.DataDictionaryValue;
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
import com.dili.uap.sdk.domain.User;
import com.dili.alm.domain.VerifyApproval;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.RoiDto;
import com.dili.alm.domain.dto.apply.ApplyApprove;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ApproveException;
import com.dili.alm.exceptions.ProjectApplyException;
import com.dili.alm.provider.ProjectTypeProvider;
import com.dili.alm.rpc.RoleRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectChangeService;
import com.dili.alm.service.ProjectCompleteService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.alm.service.TeamService;
import com.dili.alm.service.VerifyApprovalService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.SystemConfigUtils;
import com.dili.uap.sdk.domain.Role;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;
import com.google.common.collect.Lists;

import cn.afterturn.easypoi.word.WordExportUtil;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-04 17:39:37.
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
	private ProjectVersionService projectVersionService;

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

	@Transactional(rollbackFor = ApplicationException.class)
	@Override
	public synchronized void applyApprove(Long id, String opt, String notes) throws ApproveException {
		Approve approve = this.get(id);
		if (approve.getStatus() != AlmConstants.ApplyState.APPROVE.getCode()) {
			throw new ApproveException("当前状态不能进行审批操作");
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
			 * 处理立项申请单和立项审批单状态
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
					// 立项审批通过生成项目信息
					Project project = buildProject(apply, approve);
					// 插入立项规划
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

	private void insertProjectCompleteActionRecord(ProjectComplete complete) throws ApproveException {

		// 结项申请记录
		ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_COMPLETE_APPLY.getCode());
		par.setActionDate(complete.getCreated());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		par.setProjectId(complete.getProjectId());
		int rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("插入项目进程记录失败");
		}

		Date now = new Date();

		// 结项申请审批记录
		par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_COMPLETE_APPROVE.getCode());
		par.setActionDate(now);
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		par.setProjectId(complete.getProjectId());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("插入项目进程记录失败");
		}

		// 结项记录
		par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_COMPLETE.getCode());
		par.setActionDate(now);
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		par.setProjectId(complete.getProjectId());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("插入项目进程记录失败");
		}
	}

	private void insertProjectChangeActionRecord(ProjectChange change) throws ApproveException {
		// 变更申请提交记录
		ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_CHANGE_APPLY.getCode());
		par.setActionDate(change.getSubmitDate());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		par.setProjectId(change.getProjectId());
		int rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("插入项目进程记录失败");
		}
		// 变更申请审批记录
		par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_CHANGE_APPROVE.getCode());
		par.setActionDate(new Date());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		par.setProjectId(change.getProjectId());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("插入项目进程记录失败");
		}
	}

	@Override
	public void buildApplyApprove(Map modelMap, Long id) {
		Approve approve = this.get(id);

		modelMap.put("canOpt", checkApprove(approve));

		// 构建Provider
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
		modelMap.put("canOpt", checkApprove(approve));

		// 构建Provider
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
		modelMap.put("canOpt", checkApprove(approve));

		// 构建Provider
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
				List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, projectApplyService.listByExample(apply));
				Map applyDTO = maps.get(0);
				map.put("apply", applyDTO);

				projectApplyService.buildStepOne(map, applyDTO);

				map.put("planDto", projectApplyService.loadPlan(id));

				// map.put("roi", JSON.parseObject(apply.getRoi(), Map.class));
				RoiDto roi = new RoiDto();
				ProjectEarning peQuery = DTOUtils.newDTO(ProjectEarning.class);
				peQuery.setApplyId(id);
				List<ProjectEarning> earnings = this.projectEarningMapper.select(peQuery);
				roi.setEarnings(earnings);

				ProjectCost pcQuery = DTOUtils.newDTO(ProjectCost.class);
				pcQuery.setApplyId(id);
				List<ProjectCost> costs = this.projectCostMapper.select(pcQuery);
				roi.setCosts(costs);
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
				map.put("affectsOnline", change.getAffectsOnline() == 1 ? "是" : "否");
				map.put("estimateLaunchDate", DateUtil.getDate(change.getEstimateLaunchDate()));
				map.put("content", StringUtils.isBlank(change.getContent()) ? "无" : change.getContent());
				map.put("effects", StringUtils.isBlank(change.getEffects()) ? "无" : change.getEffects());
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
		DataDictionaryDto code = dataDictionaryService.findByCode(AlmConstants.ROLE_CODE);
		List<DataDictionaryValueDto> values = code.getValues();
		String roleId = values.stream().filter(v -> Objects.equals(v.getCode(), AlmConstants.ROLE_CODE_WYH_LEADER)).findFirst().map(DataDictionaryValue::getValue).orElse(null);
		String superRoleId = null;

		if (roleId == null) {
			throw new ProjectApplyException("请先指定项目委员会组长");
		}

		List<ApplyApprove> approveList = Lists.newArrayList();

		List<User> userByRole = userRpc.listUserByRoleId(Long.parseLong(roleId)).getData();
		if (CollectionUtils.isEmpty(userByRole)) {
			throw new ProjectApplyException("请先指定项目委员会组长");
		}

		ApplyApprove approve = new ApplyApprove();
		approve.setUserId(userByRole.get(0).getId());
		BaseOutput<List<Role>> listRoleByUserId = roleRpc.listByUser(Long.valueOf(userByRole.get(0).getId()),"");	
		approve.setRole(listRoleByUserId.getData().stream().map(Role::getRoleName).collect(Collectors.joining(",")));
		approveList.add(approve);

		if (as.getType().equals(AlmConstants.ApproveType.APPLY.getCode())) {
			superRoleId = values.stream().filter(v -> Objects.equals(v.getCode(), AlmConstants.ROLE_CODE_WYH_SUPER_LEADER)).findFirst().map(DataDictionaryValue::getValue).orElse(null);
			if (superRoleId == null) {
				throw new ProjectApplyException("请先指定项目委员会组长");
			}

			userByRole = userRpc.listUserByRoleId(Long.parseLong(superRoleId)).getData();
			if (CollectionUtils.isEmpty(userByRole)) {
				throw new ProjectApplyException("请先指定项目委员会组长");
			}

			approve = new ApplyApprove();
			approve.setUserId(userByRole.get(0).getId());
			BaseOutput<List<Role>> listRoleByUserId1 = roleRpc.listByUser(Long.valueOf(userByRole.get(0).getId()),"");	
			approve.setRole(listRoleByUserId1.getData().stream().map(Role::getRoleName).collect(Collectors.joining(",")));
			approveList.add(approve);
		}

		as.setDescription(JSON.toJSONString(approveList));
		int rows = insertSelective(as);
		if (rows <= 0) {
			throw new ProjectApplyException("更新审批记录失败");
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

	/**
	 * 立项审批通过生成项目信息
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
			throw new ApproveException("创建项目失败");
		}
		buildTeam(project);
		return project;
	}

	private void addProjectAction(Project project) throws ApproveException {
		Date now = new Date();
		// 插入项目进程记录立项计划
		ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_PLAN.getCode());
		par.setActionDateType(ActionDateType.PERIOD.getValue());
		par.setActionStartDate(project.getStartDate());
		par.setActionEndDate(project.getEndDate());
		par.setProjectId(project.getId());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		int rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("插入项目活动记录失败");
		}
		// 插入项目进程记录新增立项记录
		par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_APPLY.getCode());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionDate(now);
		par.setProjectId(project.getId());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("插入项目活动记录失败");
		}
		// 插入项目进程记录新增立项审批记录
		par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_APPLY_APPROVE.getCode());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionDate(now);
		par.setProjectId(project.getId());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ApproveException("插入项目活动记录失败");
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
				throw new ApproveException("创建团队失败");
			}

		}
	}

	/**
	 * 检查是否有审批权限
	 */
	private boolean checkApprove(Approve approve) {

		// 只有审批中的才能操作
		if (!approve.getStatus().equals(AlmConstants.ApplyState.APPROVE.getCode())) {
			return false;
		}

		if (StringUtils.isBlank(approve.getDescription())) {
			return false;
		}

		List<ApplyApprove> approveList = JSON.parseArray(approve.getDescription(), ApplyApprove.class);

		if (CollectionUtils.isEmpty(approveList)) {
			return false;
		}

		ApplyApprove leaderApprove = approveList.get(0);
		UserTicket user = SessionContext.getSessionContext().getUserTicket();

		// 当前登录用户是项目委员会组长，且没有审批过，则可以审批
		if (leaderApprove.getUserId().equals(user.getId()) && StringUtils.isEmpty(leaderApprove.getResult())) {
			return true;
		}

		if (approve.getType().equals(AlmConstants.ApproveType.APPLY.getCode())) {
			if (StringUtils.isEmpty(leaderApprove.getResult())) {
				return false;
			}
			// 如果是立项审批，还需要研发中心的集团领导人审批
			ApplyApprove superLeaderApprove = approveList.get(1);
			if (superLeaderApprove.getUserId().equals(user.getId()) && StringUtils.isEmpty(superLeaderApprove.getResult())) {
				return true;
			}
		}
		return false;

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
			return "项目结项总结报告";
		case "1":
			return "结项资料清单（清单与交付物完备并一致）";
		default:
			return "";
		}
	}

	private String formatReason(String reason) {

		switch (reason) {
		case "0":
			return "正常结项";
		case "1":
			return "项目取消";
		default:
			return "其他原因：" + reason;
		}
	}

	/**
	 * 获取系统中项目委员会组长用户主键
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
	 * 获取系统中项目委员会超级组长用户主键
	 *
	 * @return userId
	 */
	private Long getApproveSuperLeader() {
		List<DataDictionaryValueDto> values = dataDictionaryService.findByCode(AlmConstants.ROLE_CODE).getValues();

		String roleId = values.stream().filter(v -> Objects.equals(v.getCode(), AlmConstants.ROLE_CODE_WYH_SUPER_LEADER)).findFirst().map(DataDictionaryValue::getValue).orElse(null);

		if (roleId != null) {
			List<User> users = userRpc.listUserByRoleId(Long.valueOf(roleId)).getData();
			return users.stream().findFirst().map(User::getId).orElse(null);
		}

		return null;
	}

	private void sendMail(ProjectApply projectApply, boolean accept) {
		sendMail(projectApply.getEmail().split(","), "立项申请", projectApply.getName() + "的立项申请" + (accept ? "已经审批通过" : "审批未通过"));
	}

	private void sendMail(ProjectChange change, boolean accept) {
		// 构建邮件内容
		Project project = this.projectMapper.selectByPrimaryKey(change.getProjectId());
		Map<Object, Object> viewModel = ProjectChangeServiceImpl.buildViewModel(change);
		Template template = this.groupTemplate.getTemplate(this.projectChangeMailTemplate);
		viewModel.put("projectType", this.projectTypeProvider.getDisplayText(project.getType(), null, null));
		template.binding("model", viewModel);

		// 发送
		for (String addr : change.getEmail().split(",")) {
			try {
				this.mailManager.sendMail(this.mailFrom, addr, template.render(), true, accept ? "变更申请已经审批通过" : "变更申请审批未通过", null);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	private void sendMail(ProjectComplete complete, boolean accept) {
		sendMail(complete.getEmail().split(","), "结项申请", complete.getName() + "的结项申请" + (accept ? "已经审批通过" : "审批未通过"));
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
			LOGGER.error("邮件发送出错:", e);
		}
	}

	private void updateProjectEndDate(ProjectChange change) throws ApproveException {
		// 更新项目结束日期
		Project project = this.projectMapper.selectByPrimaryKey(change.getProjectId());
		project.setEstimateLaunchDate(change.getEstimateLaunchDate());
		project.setEndDate(change.getEndDate());
		int rows = this.projectMapper.updateByPrimaryKeySelective(project);
		if (rows <= 0) {
			throw new ApproveException("更新项目信息失败");
		}

	}

	@Override
	public Approve selectOne(Approve selectApprove) {
		return this.getDao().selectOneExpand(selectApprove);
	}
}