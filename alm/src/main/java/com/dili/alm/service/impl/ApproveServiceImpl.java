package com.dili.alm.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.MailManager;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ApproveMapper;
import com.dili.alm.dao.ProjectActionRecordMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.ActionDateType;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectAction;
import com.dili.alm.domain.ProjectActionRecord;
import com.dili.alm.domain.ProjectActionType;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectComplete;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.TeamRole;
import com.dili.alm.domain.User;
import com.dili.alm.domain.VerifyApproval;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.apply.ApplyApprove;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ApproveException;
import com.dili.alm.exceptions.ProjectApplyException;
import com.dili.alm.provider.ProjectTypeProvider;
import com.dili.alm.rpc.RoleRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.MessageService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectChangeService;
import com.dili.alm.service.ProjectCompleteService;
import com.dili.alm.service.ProjectPhaseService;
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
import com.dili.sysadmin.sdk.session.SessionContext;
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
	private FilesService filesService;

	@Autowired
	private MailManager mailManager;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MessageService messageService;

	@Autowired
	private ProjectApplyService projectApplyService;

	@Autowired
	private ProjectChangeService projectChangeService;

	@Autowired
	private ProjectCompleteService projectCompleteService;

	@Autowired
	private ProjectMapper projectMapper;

	@Autowired
	private ProjectPhaseService projectPhaseService;

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
	private ProjectTypeProvider projectTypeProvider;

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
			ApplyApprove current = approveList.stream().filter(
					v -> Objects.equals(v.getUserId(), SessionContext.getSessionContext().getUserTicket().getId()))
					.findFirst().get();
			current.setApproveDate(new Date());
			current.setResult(opt);
			current.setNotes(notes);
			approve.setDescription(JSON.toJSONString(approveList));

			/*
			 * 如果审批的是组长，需特殊处理
			 */
			if (Objects.equals(SessionContext.getSessionContext().getUserTicket().getId(), getApproveLeader())) {

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
					approve.setStatus(AlmConstants.ApplyState.PASS.getCode());
					if (Objects.equals(approve.getType(), AlmConstants.ApproveType.APPLY.getCode())) {
						ProjectApply apply = projectApplyService.get(approve.getProjectApplyId());
						apply.setStatus(AlmConstants.ApplyState.PASS.getCode());
						// 立项审批通过生成项目信息
						buildProject(apply, approve);
						projectApplyService.updateSelective(apply);
						sendMail(apply, true);
					} else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.CHANGE.getCode())) {
						ProjectChange change = projectChangeService.get(approve.getProjectApplyId());
						change.setStatus(AlmConstants.ApplyState.PASS.getCode());
						projectChangeService.updateSelective(change);
						updateProjectEndDate(change);
						insertProjectChangeActionRecord(change);
						sendMail(change, true);
					} else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.COMPLETE.getCode())) {
						ProjectComplete complete = projectCompleteService.get(approve.getProjectApplyId());
						complete.setStatus(AlmConstants.ApplyState.PASS.getCode());
						closeProject(complete);
						insertProjectCloseActionRecord(complete);
						projectCompleteService.updateSelective(complete);
						sendMail(complete, true);
					}
					break;
				default:
					break;
				}
			}
			updateSelective(approve);
			sendMessage(id, approve.getType(), SessionContext.getSessionContext().getUserTicket().getId(),
					approve.getCreateMemberId());
			sendLeaderApproveMessage(id, approve.getType(), approveList);
		}
	}

	private void insertProjectCloseActionRecord(ProjectComplete complete) {

	}

	private void insertProjectChangeActionRecord(ProjectChange change) {
		ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.PROJECT_CHANGE_APPROVE.getCode());
		par.setActionDate(new Date());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.PROJECT.getValue());
		par.setProjectId(change.getProjectId());

	}

	@Override
	public void buildApplyApprove(Map modelMap, Long id) {
		Approve approve = this.get(id);

		String approveDescription = approve.getDescription();
		modelMap.put("canOpt", checkApprove(approveDescription, approve.getStatus()));

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
		modelMap.put("canOpt", checkApprove(approveDescription, approve.getStatus()));

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

	}

	@Override
	public void buildCompleteApprove(Map modelMap, Long id) {
		Approve approve = this.get(id);

		String approveDescription = approve.getDescription();
		modelMap.put("canOpt", checkApprove(approveDescription, approve.getStatus()));

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
				List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata,
						projectApplyService.listByExample(apply));
				Map applyDTO = maps.get(0);
				map.put("apply", applyDTO);

				projectApplyService.buildStepOne(map, applyDTO);

				map.put("planDto", projectApplyService.loadPlan(id));

				map.put("roi", JSON.parseObject(apply.getRoi(), Map.class));

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
				map.put("version", projectVersionService.get(change.getVersionId()).getVersion());
				map.put("projectType", AlmCache.getInstance().getProjectTypeMap().get(project.getType()));
				map.put("phase", AlmCache.getInstance().getPhaseNameMap()
						.get(projectPhaseService.get(change.getPhaseId()).getName()));
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
				List<Map> projectDtos = ValueProviderUtils.buildDataByProvider(metadata,
						projectService.listByExample(project));
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
		String roleId = values.stream().filter(v -> Objects.equals(v.getCode(), AlmConstants.ROLE_CODE_WYH_LEADER))
				.findFirst().map(DataDictionaryValue::getValue).orElse(null);

		int rows = insertSelective(as);
		if (rows <= 0) {
			throw new ProjectApplyException("插入审批记录失败");
		}

		if (roleId != null) {
			List<ApplyApprove> approveList = Lists.newArrayList();
			List<User> userByRole = userRpc.listUserByRole(Long.parseLong(roleId)).getData();
			Long approveLeader = getApproveLeader();
			final ApplyApprove[] leaderApprove = { null };
			userByRole.forEach(u -> {
				ApplyApprove approve = new ApplyApprove();
				approve.setUserId(u.getId());
				approve.setRole(roleRpc.listRoleNameByUserId(Long.valueOf(u.getId())).getData());
				if (!u.getId().equals(approveLeader)) {
					approveList.add(approve);
					sendMessage(as.getId(), as.getType(), as.getCreateMemberId(), u.getId());
				} else {
					leaderApprove[0] = approve;
				}
			});
			if (leaderApprove[0] != null) {
				approveList.add(leaderApprove[0]);
			}
			as.setDescription(JSON.toJSONString(approveList));
			rows = updateSelective(as);
			if (rows <= 0) {
				throw new ProjectApplyException("更新审批记录失败");
			}
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
		approve.setStatus(
				Objects.equals(opt, "accept") ? AlmConstants.ChangeState.VRIFY.getCode() : approve.getStatus());
		updateSelective(approve);
		return BaseOutput.success();
	}

	/**
	 * @param applyId
	 * @param projectId
	 */
	private void buildFile(Long applyId, Long projectId) {
		List<Files> files = projectApplyService.listFiles(applyId);
		files.forEach(f -> {
			f.setProjectId(projectId);
			filesService.updateSelective(f);
		});
	}

	/**
	 * 立项审批通过生成项目信息
	 * 
	 * @throws ProjectApplyException
	 */
	private void buildProject(ProjectApply apply, Approve approve) throws ApproveException {
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
		// 插入立项规划
		this.addProjectAction(project);
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
				throw new ApproveException("未知的团队类型");
			}
			int rows = teamService.insert(team);
			if (rows <= 0) {
				throw new ApproveException("创建团队失败");
			}

		}
	}

	/**
	 * 检查是否有审批权限
	 */
	private boolean checkApprove(String approveDescription, Integer status) {
		// 能否审批
		boolean canOpt = false;

		// 只有审批中的才能操作
		if (StringUtils.isNotBlank(approveDescription) && status == AlmConstants.ApplyState.APPROVE.getCode()) {
			List<ApplyApprove> approveList = JSON.parseArray(approveDescription, ApplyApprove.class);

			/*
			 * 能够操作的情况: 当前操作用户在审批组中包括项目委员会组长
			 */
			canOpt = approveList.stream()
					.anyMatch(applyApprove -> Objects.equals(applyApprove.getUserId(),
							SessionContext.getSessionContext().getUserTicket().getId())
							&& applyApprove.getResult() == null);

		}
		return canOpt;
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

		String roleId = values.stream().filter(v -> Objects.equals(v.getCode(), AlmConstants.ROLE_CODE_WYH_LEADER))
				.findFirst().map(DataDictionaryValue::getValue).orElse(null);

		if (roleId != null) {
			List<User> users = userRpc.listUserByRole(Long.valueOf(roleId)).getData();
			return users.stream().findFirst().map(User::getId).orElse(null);
		}

		return null;
	}

	private void sendLeaderApproveMessage(Long id, String type, List<ApplyApprove> approveList) {
		try {
			if (CollectionUtils.isNotEmpty(approveList)) {
				boolean canSend = approveList.stream()
						.filter(applyApprove -> !Objects.equals(applyApprove.getUserId(), getApproveLeader())
								&& applyApprove.getResult() != null)
						.count() == approveList.size() - 1;
				if (canSend) {
					sendMessage(id, type, SessionContext.getSessionContext().getUserTicket().getId(),
							getApproveLeader());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendMail(ProjectApply projectApply, boolean accept) {
		sendMail(projectApply.getEmail().split(","), "立项申请",
				projectApply.getName() + "的立项申请" + (accept ? "已经审批通过" : "审批未通过"));
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
				this.mailManager.sendMail(this.mailFrom, addr, template.render(), true,
						accept ? "变更申请已经审批通过" : "变更申请审批未通过", null);
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

	private void sendMessage(Long id, String type, Long sender, Long recipient) {
		try {
			switch (type) {
			case "apply":
				messageService.insertMessage("http://alm.diligrp.com:8083/alm/approve/apply/" + id, sender, recipient,
						AlmConstants.MessageType.APPLY.getCode());
				break;
			case "change":
				messageService.insertMessage("http://alm.diligrp.com:8083/alm/approve/change/" + id, sender, recipient,
						AlmConstants.MessageType.CHANGE.getCode());
				break;
			case "complete":
				messageService.insertMessage("http://alm.diligrp.com:8083/alm/approve/complete/" + id, sender,
						recipient, AlmConstants.MessageType.COMPLETE.getCode());
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateProjectEndDate(ProjectChange change) throws ApproveException {
		Project project = this.projectMapper.selectByPrimaryKey(change.getProjectId());
		project.setEstimateLaunchDate(change.getEstimateLaunchDate());
		project.setEndDate(change.getEndDate());
		int rows = this.projectMapper.updateByPrimaryKeySelective(project);
		if (rows <= 0) {
			throw new ApproveException("更新项目信息失败");
		}
	}
}