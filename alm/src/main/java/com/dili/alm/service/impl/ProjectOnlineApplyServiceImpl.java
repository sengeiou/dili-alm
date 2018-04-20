package com.dili.alm.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.MailManager;
import com.dili.alm.component.NumberGenerator;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.EmailAddressMapper;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectOnlineApplyMapper;
import com.dili.alm.dao.ProjectOnlineApplyMarketMapper;
import com.dili.alm.dao.ProjectOnlineOperationRecordMapper;
import com.dili.alm.dao.ProjectOnlineSubsystemMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TeamMapper;
import com.dili.alm.dao.VersionMarketOnlineRecordMapper;
import com.dili.alm.domain.ApplyType;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.EmailAddress;
import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.ProjectOnlineApplyMarket;
import com.dili.alm.domain.ProjectOnlineApplyOperationType;
import com.dili.alm.domain.ProjectOnlineApplyState;
import com.dili.alm.domain.ProjectOnlineOperationRecord;
import com.dili.alm.domain.ProjectOnlineSubsystem;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.User;
import com.dili.alm.domain.VersionMarketOnlineRecord;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectOnlineApplyUpdateDto;
import com.dili.alm.domain.dto.ProjectOnlineOperationRecordDto;
import com.dili.alm.domain.dto.UserDepartmentRole;
import com.dili.alm.domain.dto.UserDepartmentRoleQuery;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ProjectOnlineApplyException;
import com.dili.alm.provider.ProjectVersionProvider;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectOnlineApplyDetailDto;
import com.dili.alm.service.ProjectOnlineApplyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.github.pagehelper.Page;

import tk.mybatis.mapper.entity.Example;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-13 15:31:10.
 */
@Transactional(rollbackFor = ApplicationException.class, propagation = Propagation.REQUIRED)
@Service
public class ProjectOnlineApplyServiceImpl extends BaseServiceImpl<ProjectOnlineApply, Long>
		implements ProjectOnlineApplyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectOnlineApplyServiceImpl.class);

	@Autowired
	private ProjectOnlineApplyMarketMapper applyMarketMapper;

	private String contentTemplate;
	@Autowired
	private DataDictionaryService ddService;
	@Autowired
	private DepartmentRpc deptRpc;
	@Autowired
	private EmailAddressMapper emailAddressMapper;
	@Autowired
	private FilesMapper filesMapper;
	@Qualifier("mailContentTemplate")
	@Autowired
	private GroupTemplate groupTemplate;
	@Value("${spring.mail.username:}")
	private String mailFrom;
	@Autowired
	private MailManager mailManager;
	@Autowired
	private ProjectOnlineOperationRecordMapper poorMapper;
	@Autowired
	private ProjectOnlineSubsystemMapper posMapper;
	@Autowired
	private ProjectMapper projectMapper;
	@Qualifier("projectOnlineApplyNumberGenerator")
	@Autowired
	private NumberGenerator serialNumberGenerator;
	@Autowired
	private TeamMapper teamMapper;

	@Autowired
	private UserRpc userRpc;
	@Autowired
	private VersionMarketOnlineRecordMapper versionMaketMapper;
	@Autowired
	private ProjectVersionProvider versionProvider;
	@Autowired
	private ProjectVersionMapper versionMapper;

	@SuppressWarnings("unchecked")
	public static Map<Object, Object> buildApplyViewModel(ProjectOnlineApply apply) {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("businessOwnerId", memberProvider);
		metadata.put("projectManagerId", memberProvider);
		metadata.put("productManagerId", memberProvider);
		metadata.put("testManagerId", memberProvider);
		metadata.put("developmentManagerId", memberProvider);
		metadata.put("applicantId", memberProvider);

		JSONObject dateProvider = new JSONObject();
		dateProvider.put("provider", "almDateProvider");
		metadata.put("onlineDate", dateProvider);
		metadata.put("actualOnlineDate", dateProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("submitTime", datetimeProvider);
		metadata.put("created", datetimeProvider);

		JSONObject applyStateProvider = new JSONObject();
		applyStateProvider.put("provider", "projectOnlineApplyStateProvider");
		metadata.put("applyState", applyStateProvider);

		JSONObject filesProvider = new JSONObject();
		filesProvider.put("provider", "filesProvider");
		metadata.put("sqlFileId", filesProvider);
		metadata.put("startupScriptFileId", filesProvider);
		metadata.put("dependencySystemFileId", filesProvider);

		try {
			@SuppressWarnings("rawtypes")
			List<Map> viewModelList = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(apply));
			if (CollectionUtils.isEmpty(viewModelList)) {
				return null;
			}
			return viewModelList.get(0);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	public ProjectOnlineApplyServiceImpl() {
		super();
		InputStream in = null;
		try {
			Resource res = new ClassPathResource("conf/projectOlineApplyMailContentTemplate.html");
			in = res.getInputStream();
			this.contentTemplate = IOUtils.toString(in, "UTF-8");
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

	@Override
	public void deleteProjectOnlineApply(Long id) throws ProjectOnlineApplyException {
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(id);
		if (apply == null) {
			throw new ProjectOnlineApplyException("申请不存在");
		}
		if (!apply.getApplyState().equals(ProjectOnlineApplyState.APPLING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能编辑");
		}
		// 删除子系统
		ProjectOnlineSubsystem posQuery = DTOUtils.newDTO(ProjectOnlineSubsystem.class);
		posQuery.setApplyId(id);
		this.posMapper.delete(posQuery);

		// 删除邮件
		EmailAddress emailQuery = DTOUtils.newDTO(EmailAddress.class);
		emailQuery.setApplyId(id);
		this.emailAddressMapper.delete(emailQuery);

		// 删除文件
		if (apply.getDependencySystemFileId() != null) {
			this.filesMapper.deleteByPrimaryKey(apply.getDependencySystemFileId());
		}
		if (apply.getSqlFileId() != null) {
			this.filesMapper.deleteByPrimaryKey(apply.getSqlFileId());
		}
		if (apply.getStartupScriptFileId() != null) {
			this.filesMapper.deleteByPrimaryKey(apply.getStartupScriptFileId());
		}

		int result = super.delete(id);
		if (result <= 0) {
			throw new ProjectOnlineApplyException("删除失败");
		}
	}

	@Override
	public void excuteConfirm(Long applyId, Long executorId, OperationResult result, String description)
			throws ProjectOnlineApplyException {
		// 验证记录是否存在
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new ProjectOnlineApplyException("申请记录不存在");
		}
		// 验证状态
		if (!apply.getApplyState().equals(ProjectOnlineApplyState.EXECUTING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能执行确认操作");
		}
		// 验证执行人权限
		// 如果执行人id为空，说明运维经理没有分配执行人，抛异常
		if (StringUtils.isEmpty(apply.getExecutorId())) {
			throw new ProjectOnlineApplyException("运维经理未分配执行人");
		}
		// 检查当前的执行人是否在运维经理分配的执行人当中
		boolean flag = false;
		for (String str : apply.getExecutorId().split(",")) {
			if (executorId.equals(Long.valueOf(str))) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			throw new ProjectOnlineApplyException("当前用户没有权限执行当前操作");
		}
		// 执行失败
		if (OperationResult.FAILURE.equals(result)) {
			apply.setApplyState(ProjectOnlineApplyState.FAILURE.getValue());
		}
		// 执行成功,产品经理验证，任意执行人执行确认操作都跳转到产品验证状态
		if (OperationResult.SUCCESS.equals(result)) {
			apply.setApplyState(ProjectOnlineApplyState.VARIFING.getValue());
		}
		// 更新状态
		int rows = this.getActualDao().updateByPrimaryKey(apply);
		if (rows <= 0) {
			throw new ProjectOnlineApplyException("更新状态失败");
		}
		// 生成操作记录
		ProjectOnlineOperationRecord record = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
		record.setApplyId(applyId);
		record.setOperatorId(executorId);
		record.setOperationType(ProjectOnlineApplyOperationType.OPERATION_EXECUTOR.getValue());
		record.setOperationName(ProjectOnlineApplyOperationType.OPERATION_EXECUTOR.getName());
		record.setDescription(description);
		record.setOpertateResult(result.getValue());
		rows = this.poorMapper.insertSelective(record);
		if (rows <= 0) {
			throw new ProjectOnlineApplyException("生成操作记录失败");
		}
		// 发邮件给产品经理
		if (OperationResult.FAILURE.equals(result)) {
			User user = AlmCache.getInstance().getUserMap().get(apply.getProductManagerId());
			if (user == null) {
				throw new ProjectOnlineApplyException("产品经理不存在");
			}
			Set<String> emails = this.getDefaultEmails(apply);
			emails.add(user.getEmail());
			this.sendMail(apply, "上线失败", emails);
		}
	}

	public ProjectOnlineApplyMapper getActualDao() {
		return (ProjectOnlineApplyMapper) getDao();
	}

	@Override
	public ProjectOnlineApply getConfirmExecuteViewModel(Long id) throws ProjectOnlineApplyException {
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(id);
		if (apply == null) {
			throw new ProjectOnlineApplyException("申请记录不存在");
		}
		// 上线子系统
		ProjectOnlineSubsystem subsysQuery = DTOUtils.newDTO(ProjectOnlineSubsystem.class);
		subsysQuery.setApplyId(id);
		List<ProjectOnlineSubsystem> subsystems = this.posMapper.select(subsysQuery);
		apply.aset("subsystems", subsystems);
		// 操作记录
		ProjectOnlineOperationRecord poorQuery = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
		poorQuery.setApplyId(id);
		List<ProjectOnlineOperationRecord> poorList = this.poorMapper.select(poorQuery);
		apply.aset("opRecords", this.buildOperationRecordViewModel(poorList));
		return apply;
	}

	@Override
	public ProjectOnlineApply getDetailViewData(Long id) throws ProjectOnlineApplyException {
		return this.getFlowViewData(id);
	}

	@Override
	public ProjectOnlineApply getEasyUiRowData(Long id) {
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(id);
		this.setMarkets(apply);
		this.setOperationColumn(apply);
		return apply;
	}

	@Transactional(readOnly = true)
	@Override
	public ProjectOnlineApply getEditViewDataById(Long id) throws ProjectOnlineApplyException {
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(id);
		if (apply == null) {
			throw new ProjectOnlineApplyException("申请不存在");
		}
		ProjectOnlineSubsystem subsysQuery = DTOUtils.newDTO(ProjectOnlineSubsystem.class);
		subsysQuery.setApplyId(id);
		List<ProjectOnlineSubsystem> subsystems = this.posMapper.select(subsysQuery);
		apply.aset("subsystems", subsystems);
		EmailAddress emailQuery = DTOUtils.newDTO(EmailAddress.class);
		emailQuery.setApplyId(id);
		List<EmailAddress> emails = this.emailAddressMapper.select(emailQuery);
		apply.aset("emails", emails);
		ProjectOnlineApplyMarket poamQuery = DTOUtils.newDTO(ProjectOnlineApplyMarket.class);
		poamQuery.setApplyId(id);
		// 判断是否针对市场上线
		List<ProjectOnlineApplyMarket> poamList = this.applyMarketMapper.select(poamQuery);
		if (poamList.size() == 1 && poamList.get(0).getMarketCode().equals("-1")) {
			apply.aset("marketVersion", false);
		} else {
			apply.aset("marketVersion", true);
			apply.aset("selectedMarkets", poamList);
		}
		return apply;
	}

	@Override
	public ProjectOnlineApply getProjectManagerConfirmViewModel(Long id) throws ProjectOnlineApplyException {
		return this.getFlowViewData(id);
	}

	@Override
	public ProjectOnlineApply getStartExecuteViewData(Long id) throws ProjectOnlineApplyException {
		return this.getFlowViewData(id);
	}

	@Override
	public ProjectOnlineApply getTestConfirmViewModel(Long id) throws ProjectOnlineApplyException {
		return this.getFlowViewData(id);
	}

	@Override
	public ProjectOnlineApply getVerifyViewData(Long id) throws ProjectOnlineApplyException {
		return this.getFlowViewData(id);
	}

	@Override
	public ProjectOnlineApplyDetailDto getViewDataById(Long applyId) {
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			return null;
		}
		ProjectOnlineApplyDetailDto dto = DTOUtils.toEntity(apply, ProjectOnlineApplyDetailDto.class, true);
		ProjectOnlineOperationRecord record = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
		record.setApplyId(applyId);
		List<ProjectOnlineOperationRecord> list = this.poorMapper.select(record);
		if (CollectionUtils.isNotEmpty(list)) {
			List<ProjectOnlineOperationRecordDto> poorDtos = new ArrayList<>(list.size());
			list.forEach(e -> poorDtos.add(DTOUtils.toEntity(e, ProjectOnlineOperationRecordDto.class, true)));
			dto.setOperations(poorDtos);
		}
		return dto;
	}

	@Override
	public void insertProjectOnlineApply(ProjectOnlineApplyUpdateDto apply) throws ProjectOnlineApplyException {
		// 查询出项目信息
		Project project = this.projectMapper.selectByPrimaryKey(apply.getProjectId());
		String serialNumber = this.serialNumberGenerator.get();
		apply.setSerialNumber(serialNumber);
		apply.setProjectName(project.getName());
		apply.setBusinessOwnerId(project.getBusinessOwner());
		apply.setProjectManagerId(project.getProjectManager());
		apply.setProductManagerId(project.getProductManager());
		apply.setTestManagerId(project.getTestManager());
		apply.setDevelopmentManagerId(project.getDevelopManager());
		apply.setProjectSerialNumber(project.getSerialNumber());
		apply.setVersion(this.versionProvider.getDisplayText(apply.getVersionId(), null, null));

		// 判断申请上线日期是否大于当前日期
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (apply.getOnlineDate().getTime() < calendar.getTimeInMillis()) {
			throw new ProjectOnlineApplyException("申请上线日期不能小于当前日期");
		}

		// 判断市场在当前版本是否上过线
		Example example = new Example(VersionMarketOnlineRecord.class);
		example.createCriteria().andEqualTo("versionId", apply.getVersionId()).andIn("marketCode", apply.getMarkets());
		int count = this.versionMaketMapper.selectCountByExample(example);
		if (count > 0) {
			throw new ProjectOnlineApplyException("改版本已在指定市场上线，不能重复上线");
		}

		int result = this.getActualDao().insertSelective(apply);
		if (result <= 0) {
			throw new ProjectOnlineApplyException("插入失败");
		}

		// 插入子系统
		if (CollectionUtils.isNotEmpty(apply.getSubsystem())) {
			apply.getSubsystem().forEach(s -> {
				s.setApplyId(apply.getId());
				if (StringUtils.isNumeric(s.getManagerName())) {
					Long managerId = Long.valueOf(s.getManagerName());
					User u = AlmCache.getInstance().getUserMap().get(managerId);
					if (u != null) {
						s.setManagerId(managerId);
						s.setManagerName(u.getRealName());
					}
				}
				if (StringUtils.isNumeric(s.getProjectName())) {
					Long projectId = Long.valueOf(s.getProjectName());
					Project p = this.projectMapper.selectByPrimaryKey(projectId);
					if (p != null) {
						s.setProjectId(p.getId());
						s.setProjectName(p.getName());
					}
				}
				this.posMapper.insert(s);
			});
		}

		// 插入上线申请和市场关联表
		// 判断是否根据市场上线
		if (apply.getMarketVersion()) {
			// 针对市场上线
			apply.getMarkets().forEach(m -> {
				ProjectOnlineApplyMarket am = DTOUtils.newDTO(ProjectOnlineApplyMarket.class);
				am.setApplyId(apply.getId());
				am.setMarketCode(m);
				this.applyMarketMapper.insert(am);
			});
		} else {
			// 不针对市场上线
			ProjectOnlineApplyMarket am = DTOUtils.newDTO(ProjectOnlineApplyMarket.class);
			am.setApplyId(apply.getId());
			am.setMarketCode("-1");
			this.applyMarketMapper.insertSelective(am);
		}

		// 更新邮件通知地址，先删除后插入
		// 先删除
		EmailAddress eaCondition = DTOUtils.newDTO(EmailAddress.class);
		eaCondition.setApplyId(apply.getId());
		this.emailAddressMapper.delete(eaCondition);

		// 插入
		apply.getEmailAddress().forEach(s -> {
			EmailAddress ea = DTOUtils.newDTO(EmailAddress.class);
			ea.setApplyId(apply.getId());
			ea.setApplyType(ApplyType.ONLINE.getValue());
			ea.setEmailAddress(s);
			this.emailAddressMapper.insert(ea);
		});
	}

	@Override
	public EasyuiPageOutput listEasyuiPageByExample(ProjectOnlineApply domain, boolean useProvider) throws Exception {
		List<ProjectOnlineApply> list = listByExample(domain);
		list.forEach(a -> {
			this.setMarkets(a);
			this.setOperationColumn(a);
		});
		@SuppressWarnings("rawtypes")
		long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
		@SuppressWarnings("rawtypes")
		List results = useProvider ? ValueProviderUtils.buildDataByProvider(domain, list) : list;
		return new EasyuiPageOutput(Long.valueOf(total).intValue(), results);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.dili.alm.service.ProjectOnlineApplyService#projectManagerConfirm(java.
	 * lang.Long, java.lang.Long, com.dili.alm.domain.OperationResult,
	 * java.lang.String)
	 */
	@Override
	public void projectManagerConfirm(Long applyId, Long executorId, OperationResult result, String description)
			throws ProjectOnlineApplyException {
		// 验证记录是否存在
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new ProjectOnlineApplyException("申请记录不存在");
		}
		// 验证状态
		if (!apply.getApplyState().equals(ProjectOnlineApplyState.PROJECT_MANAGER_CONFIRMING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能执行项目经理确认");
		}
		// 项目经理回退
		if (OperationResult.FAILURE.equals(result)) {
			apply.setApplyState(ProjectOnlineApplyState.APPLING.getValue());
			apply.setSubmitTime(null);
		}
		// 项目经理确认
		if (OperationResult.SUCCESS.equals(result)) {
			// 下一步测试经理确认
			apply.setApplyState(ProjectOnlineApplyState.TESTER_CONFIRMING.getValue());
		}
		// 更新状态
		int rows = this.getActualDao().updateByPrimaryKey(apply);
		if (rows <= 0) {
			throw new ProjectOnlineApplyException("更新状态失败");
		}
		// 生成操作记录
		ProjectOnlineOperationRecord record = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
		record.setApplyId(applyId);
		record.setOperatorId(executorId);
		record.setOperationType(ProjectOnlineApplyOperationType.PROJECT_MANAGER.getValue());
		record.setOperationName(ProjectOnlineApplyOperationType.PROJECT_MANAGER.getName());
		record.setDescription(description);
		record.setOpertateResult(result.getValue());
		rows = this.poorMapper.insertSelective(record);
		if (rows <= 0) {
			throw new ProjectOnlineApplyException("生成操作记录失败");
		}
		// 发邮件
		// 这里面包含了运维不经理，后面就不用在单独发了
		Set<String> emails = this.getDefaultEmails(apply);
		this.sendMail(apply, "上线申请审批", emails);
	}

	@Override
	public void saveAndSubmit(ProjectOnlineApplyUpdateDto dto) throws ProjectOnlineApplyException {
		this.saveOrUpdate(dto);
		this.submit(dto.getId());
	}

	@Override
	public void saveOrUpdate(ProjectOnlineApplyUpdateDto dto) throws ProjectOnlineApplyException {
		if (dto.getId() == null) {
			this.insertProjectOnlineApply(dto);
		} else {
			this.updateProjectOnlineApply(dto);
		}
	}

	@Override
	public void startExecute(Long applyId, Long executorId, Set<Long> executors, String description)
			throws ProjectOnlineApplyException {
		// 验证记录是否存在
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new ProjectOnlineApplyException("申请记录不存在");
		}
		// 验证状态
		if (!apply.getApplyState().equals(ProjectOnlineApplyState.EXECUTING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能执行测试确认");
		}
		// 分配的执行人最多两人，且都是运维部员工
		if (CollectionUtils.isEmpty(executors)) {
			throw new ProjectOnlineApplyException("执行人不能为空");
		}
		if (executors.size() > 2) {
			throw new ProjectOnlineApplyException("最多只能分配2个执行人");
		}
		for (Long value : executors) {
			UserDepartmentRoleQuery dto = new UserDepartmentRoleQuery();
			dto.setUserId(value);
			BaseOutput<List<UserDepartmentRole>> output = this.userRpc.findUserContainDepartmentAndRole(dto);
			if (!output.isSuccess()) {
				throw new ProjectOnlineApplyException("查询执行人信息失败");
			}
			if (CollectionUtils.isEmpty(output.getData())) {
				throw new ProjectOnlineApplyException("执行人不存在");
			}
			if (!AlmConstants.OPERATION_DEPARTMENT_CODE.equals(output.getData().get(0).getDepartment().getCode())) {
				throw new ProjectOnlineApplyException("执行人不是运维部员工");
			}
		}
		// 还要判断有没有分配执行人，分配了的话就不能再执行“开始执行”的操作了
		if (StringUtils.isNotEmpty(apply.getExecutorId())) {
			throw new ProjectOnlineApplyException("已分配执行人的申请不能重复执行");
		}
		StringBuilder sb = new StringBuilder();
		executors.forEach(e -> sb.append(e).append(","));
		apply.setExecutorId(sb.substring(0, sb.length() - 1));
		int rows = this.getActualDao().updateByPrimaryKey(apply);
		if (rows <= 0) {
			throw new ProjectOnlineApplyException("更新数据失败");
		}
		// 生成操作记录
		ProjectOnlineOperationRecord record = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
		record.setApplyId(applyId);
		record.setOperatorId(executorId);
		record.setOperationType(ProjectOnlineApplyOperationType.OPERATION_MANAGER.getValue());
		record.setOperationName(ProjectOnlineApplyOperationType.OPERATION_MANAGER.getName());
		record.setDescription(description);
		record.setOpertateResult(OperationResult.SUCCESS.getValue());
		rows = this.poorMapper.insertSelective(record);
		if (rows <= 0) {
			throw new ProjectOnlineApplyException("生成操作记录失败");
		}
		// 发邮件
		// 给运维部发，这里面包含了运维部执行人，后面就不用在单独发了
		Set<String> emails = this.getDefaultEmails(apply);
		this.sendMail(apply, "上线申请执行", emails);
	}

	@Override
	public void submit(Long applyId) throws ProjectOnlineApplyException {
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new ProjectOnlineApplyException("申请记录不存在");
		}
		if (!apply.getApplyState().equals(ProjectOnlineApplyState.APPLING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能提交申请");
		}
		// 提交申请进入项目经理确认状态
		apply.setApplyState(ProjectOnlineApplyState.PROJECT_MANAGER_CONFIRMING.getValue());
		apply.setSubmitTime(new Date());
		int result = this.getActualDao().updateByPrimaryKey(apply);
		if (result <= 0) {
			throw new ProjectOnlineApplyException("更新状态失败");
		}
		// 发送邮件
		Set<String> emailStrs = this.getDefaultEmails(apply);
		this.sendMail(apply, "上线申请", emailStrs);
	}

	@Override
	public void testerConfirm(Long applyId, Long executorId, OperationResult result, String description)
			throws ProjectOnlineApplyException {
		// 验证记录是否存在
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new ProjectOnlineApplyException("申请记录不存在");
		}
		// 验证状态
		if (!apply.getApplyState().equals(ProjectOnlineApplyState.TESTER_CONFIRMING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能执行测试确认");
		}
		// 测试回退
		if (OperationResult.FAILURE.equals(result)) {
			apply.setApplyState(ProjectOnlineApplyState.APPLING.getValue());
			apply.setSubmitTime(null);
		}
		// 测试确认
		if (OperationResult.SUCCESS.equals(result)) {
			apply.setApplyState(ProjectOnlineApplyState.EXECUTING.getValue());
		}
		// 更新状态
		int rows = this.getActualDao().updateByPrimaryKey(apply);
		if (rows <= 0) {
			throw new ProjectOnlineApplyException("更新状态失败");
		}
		// 生成操作记录
		ProjectOnlineOperationRecord record = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
		record.setApplyId(applyId);
		record.setOperatorId(executorId);
		record.setOperationType(ProjectOnlineApplyOperationType.TEST_MANAGER.getValue());
		record.setOperationName(ProjectOnlineApplyOperationType.TEST_MANAGER.getName());
		record.setDescription(description);
		record.setOpertateResult(result.getValue());
		rows = this.poorMapper.insertSelective(record);
		if (rows <= 0) {
			throw new ProjectOnlineApplyException("生成操作记录失败");
		}
		// 发邮件
		Set<String> emails = this.getDefaultEmails(apply);
		// 给测试部经理发
		User user = AlmCache.getInstance().getUserMap().get(apply.getTestManagerId());
		if (user == null) {
			throw new ProjectOnlineApplyException("测试经理不存在");
		}
		emails.add(user.getEmail());
	}

	@Override
	public void updateProjectOnlineApply(ProjectOnlineApplyUpdateDto apply) throws ProjectOnlineApplyException {

		// 判断状态必须是未提交状态才能编辑
		ProjectOnlineApply old = this.getActualDao().selectByPrimaryKey(apply.getId());
		if (!old.getApplyState().equals(ProjectOnlineApplyState.APPLING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能编辑");
		}

		// 查询出项目信息
		Project project = this.projectMapper.selectByPrimaryKey(apply.getProjectId());
		apply.setSerialNumber(old.getSerialNumber());
		apply.setProjectName(project.getName());
		apply.setBusinessOwnerId(project.getBusinessOwner());
		apply.setProjectManagerId(project.getProjectManager());
		apply.setProductManagerId(project.getProductManager());
		apply.setTestManagerId(project.getTestManager());
		apply.setDevelopmentManagerId(project.getDevelopManager());
		apply.setVersion(this.versionProvider.getDisplayText(apply.getVersionId(), null, null));

		// 判断申请上线日期是否大于当前日期
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		if (apply.getOnlineDate().getTime() < calendar.getTimeInMillis()) {
			throw new ProjectOnlineApplyException("申请上线日期不能小于当前日期");
		}

		// 判断市场在当前版本是否上过线
		Example example = new Example(VersionMarketOnlineRecord.class);
		example.createCriteria().andEqualTo("versionId", apply.getVersionId()).andIn("marketCode", apply.getMarkets());
		int count = this.versionMaketMapper.selectCountByExample(example);
		if (count > 0) {
			throw new ProjectOnlineApplyException("改版本已在指定市场上线，不能重复上线");
		}

		// 更新子系统，先删除后插入
		if (CollectionUtils.isNotEmpty(apply.getSubsystem())) {
			// 先删除
			ProjectOnlineSubsystem record = DTOUtils.newDTO(ProjectOnlineSubsystem.class);
			record.setApplyId(apply.getId());
			this.posMapper.delete(record);

			// 插入
			apply.getSubsystem().forEach(s -> {
				s.setApplyId(apply.getId());
				if (StringUtils.isNumeric(s.getManagerName())) {
					Long managerId = Long.valueOf(s.getManagerName());
					User u = AlmCache.getInstance().getUserMap().get(managerId);
					if (u != null) {
						s.setManagerId(managerId);
						s.setManagerName(u.getRealName());
					}
				}
				if (StringUtils.isNumeric(s.getProjectName())) {
					Long projectId = Long.valueOf(s.getProjectName());
					Project p = this.projectMapper.selectByPrimaryKey(projectId);
					if (p != null) {
						s.setProjectId(p.getId());
						s.setProjectName(p.getName());
					}
				}
				this.posMapper.insert(s);
			});
		}

		// 更新附件
		if (apply.getDependencySystemFileId() != null) {
			this.filesMapper.deleteByPrimaryKey(old.getDependencySystemFileId());
		}
		if (apply.getSqlFileId() != null) {
			this.filesMapper.deleteByPrimaryKey(old.getSqlFileId());
		}
		if (apply.getStartupScriptFileId() != null) {
			this.filesMapper.deleteByPrimaryKey(old.getStartupScriptFileId());
		}

		int result = this.getActualDao().updateByPrimaryKeySelective(apply);
		if (result <= 0) {
			throw new ProjectOnlineApplyException("更新失败");
		}

		// 更新上线申请和市场关系表
		// 先删除
		ProjectOnlineApplyMarket applyMarketQuery = DTOUtils.newDTO(ProjectOnlineApplyMarket.class);
		applyMarketQuery.setApplyId(apply.getId());
		this.applyMarketMapper.delete(applyMarketQuery);
		// 再插入
		if (apply.getMarketVersion()) {
			List<ProjectOnlineApplyMarket> list = new ArrayList<>(apply.getMarkets().size());
			apply.getMarkets().forEach(m -> {
				ProjectOnlineApplyMarket am = DTOUtils.newDTO(ProjectOnlineApplyMarket.class);
				am.setApplyId(apply.getId());
				am.setMarketCode(m);
				list.add(am);
			});
			this.applyMarketMapper.insertList(list);
		} else {
			ProjectOnlineApplyMarket poma = DTOUtils.newDTO(ProjectOnlineApplyMarket.class);
			poma.setApplyId(apply.getId());
			poma.setMarketCode("-1");
			this.applyMarketMapper.insertSelective(poma);
		}

		// 更新邮件通知地址，先删除后插入
		// 先删除
		EmailAddress eaCondition = DTOUtils.newDTO(EmailAddress.class);
		eaCondition.setApplyId(apply.getId());
		this.emailAddressMapper.delete(eaCondition);

		// 插入
		apply.getEmailAddress().forEach(s -> {
			EmailAddress ea = DTOUtils.newDTO(EmailAddress.class);
			ea.setApplyId(apply.getId());
			ea.setApplyType(ApplyType.ONLINE.getValue());
			ea.setEmailAddress(s);
			this.emailAddressMapper.insert(ea);
		});
	}

	@Override
	public void verify(Long applyId, Long verifierId, OperationResult result, String description)
			throws ProjectOnlineApplyException {
		// 验证记录是否存在
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new ProjectOnlineApplyException("申请记录不存在");
		}
		// 验证状态
		if (!apply.getApplyState().equals(ProjectOnlineApplyState.VARIFING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能执行确认操作");
		}
		if (OperationResult.FAILURE.equals(result)) {
			apply.setApplyState(ProjectOnlineApplyState.FAILURE.getValue());
		}
		if (OperationResult.SUCCESS.equals(result)) {
			apply.setApplyState(ProjectOnlineApplyState.COMPLETED.getValue());
			apply.setActualOnlineDate(new Date());
		}
		// 更新状态
		int rows = this.getActualDao().updateByPrimaryKey(apply);
		if (rows <= 0) {
			throw new ProjectOnlineApplyException("更新状态失败");
		}
		// 生成操作记录
		ProjectOnlineOperationRecord record = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
		record.setApplyId(applyId);
		record.setOperatorId(verifierId);
		record.setOperationType(ProjectOnlineApplyOperationType.VERIFIER.getValue());
		record.setOperationName(ProjectOnlineApplyOperationType.VERIFIER.getName());
		record.setDescription(description);
		record.setOpertateResult(result.getValue());
		rows = this.poorMapper.insertSelective(record);
		if (rows <= 0) {
			throw new ProjectOnlineApplyException("新增操作记录失败");
		}
		if (OperationResult.SUCCESS.equals(result)) {
			// 插入上线记录表
			ProjectOnlineApplyMarket poamQuery = DTOUtils.newDTO(ProjectOnlineApplyMarket.class);
			poamQuery.setApplyId(apply.getId());
			List<ProjectOnlineApplyMarket> poamList = this.applyMarketMapper.select(poamQuery);
			if (CollectionUtils.isNotEmpty(poamList)) {
				List<VersionMarketOnlineRecord> vmorList = new ArrayList<>(poamList.size());
				poamList.forEach(a -> {
					VersionMarketOnlineRecord vmor = DTOUtils.newDTO(VersionMarketOnlineRecord.class);
					vmor.setMarketCode(a.getMarketCode());
					vmor.setVersionId(apply.getVersionId());
					vmorList.add(vmor);
				});
				try {
					this.versionMaketMapper.insertList(vmorList);
				} catch (BadSqlGrammarException e) {
					throw new ProjectOnlineApplyException("改版本已在相同市场上线，不能重复上线");
				}
			}
		}
		// 更新项目版本上线状态
		ProjectVersion version = this.versionMapper.selectByPrimaryKey(apply.getVersionId());
		version.setOnline(Boolean.TRUE);
		rows = this.versionMapper.updateByPrimaryKeySelective(version);
		if (rows <= 0) {
			throw new ProjectOnlineApplyException("更新项目版本上线状态失败");
		}
		// 发邮件
		Set<String> emails = this.getDefaultEmails(apply);
		if (OperationResult.FAILURE.equals(result)) {
			this.sendMail(apply, "上线失败", emails);
		}
		if (OperationResult.SUCCESS.equals(result)) {
			this.sendMail(apply, "上线成功", emails);
		}
	}

	@SuppressWarnings("rawtypes")
	private List<Map> buildOperationRecordViewModel(List<ProjectOnlineOperationRecord> list) {
		Map<Object, Object> metadata = new HashMap<>();
		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("operatorId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("operateTime", datetimeProvider);
		try {
			return ValueProviderUtils.buildDataByProvider(metadata, list);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	private Set<String> getApplicantSpecifiedEmails(ProjectOnlineApply apply) {
		EmailAddress record = DTOUtils.newDTO(EmailAddress.class);
		record.setApplyId(apply.getId());
		List<EmailAddress> emails = this.emailAddressMapper.select(record);
		Set<String> emailStrs = new HashSet<>(emails.size());
		emails.forEach(e -> emailStrs.add(e.getEmailAddress()));
		return emailStrs;
	}

	private Set<String> getDefaultEmails(ProjectOnlineApply apply) {
		// 给运维部发
		Set<String> emails = this.getOperationDepartmentUserEmails(apply);
		// 给申请人指定的人发
		emails.addAll(this.getApplicantSpecifiedEmails(apply));
		// 给申请人发
		User user = AlmCache.getInstance().getUserMap().get(apply.getApplicantId());
		if (user == null) {
			throw new NullPointerException("申请人不存在");
		}
		emails.add(user.getEmail());
		return emails;
	}

	private ProjectOnlineApply getFlowViewData(Long id) throws ProjectOnlineApplyException {
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(id);
		if (apply == null) {
			throw new ProjectOnlineApplyException("申请记录不存在");
		}
		// 上线子系统
		ProjectOnlineSubsystem subsysQuery = DTOUtils.newDTO(ProjectOnlineSubsystem.class);
		subsysQuery.setApplyId(id);
		List<ProjectOnlineSubsystem> subsystems = this.posMapper.select(subsysQuery);
		apply.aset("subsystems", subsystems);
		// 操作记录
		ProjectOnlineOperationRecord poorQuery = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
		poorQuery.setApplyId(id);
		List<ProjectOnlineOperationRecord> poorList = this.poorMapper.select(poorQuery);
		apply.aset("opRecords", this.buildOperationRecordViewModel(poorList));
		// 查询上线市场
		this.setMarkets(apply);
		return apply;
	}

	private Set<String> getOperationDepartmentUserEmails(ProjectOnlineApply apply) {
		Set<String> emailStrs = new HashSet<>();
		// 默认邮件发送列表，运维组和项目组成员
		// 运维部成员
		Department deptQuery = new Department();
		deptQuery.setCode(AlmConstants.OPERATION_DEPARTMENT_CODE);
		BaseOutput<List<Department>> deptOutput = this.deptRpc.list(deptQuery);
		if (deptOutput.isSuccess() && CollectionUtils.isNotEmpty(deptOutput.getData())) {
			Long departmentId = deptOutput.getData().get(0).getId();
			User userQuery = new User();
			userQuery.setDepartmentId(departmentId);
			BaseOutput<List<User>> userOutput = this.userRpc.list(userQuery);
			if (userOutput.isSuccess() && CollectionUtils.isNotEmpty(userOutput.getData())) {
				userOutput.getData().forEach(u -> emailStrs.add(u.getEmail()));
			}
		}
		// 项目组成员
		Team teamQuery = DTOUtils.newDTO(Team.class);
		teamQuery.setProjectId(apply.getProjectId());
		List<Team> teams = this.teamMapper.select(teamQuery);
		teams.forEach(t -> {
			User u = AlmCache.getInstance().getUserMap().get(t.getMemberId());
			if (u != null) {
				emailStrs.add(u.getEmail());
			}
		});
		return emailStrs;
	}

	private void sendMail(ProjectOnlineApply apply, String subject, Set<String> emails) {

		// 构建邮件内容
		Template template = this.groupTemplate.getTemplate(this.contentTemplate);
		template.binding("apply", buildApplyViewModel(apply));
		ProjectOnlineOperationRecord poor = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
		poor.setApplyId(apply.getId());
		List<ProjectOnlineOperationRecord> list = this.poorMapper.select(poor);
		template.binding("opRecords", this.buildOperationRecordViewModel(list));

		// 发送
		emails.forEach(s -> {
			try {
				this.mailManager.sendMail(this.mailFrom, s, template.render(), true, subject, null);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		});
	}

	private void setMarkets(ProjectOnlineApply apply) {
		DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.MARKET_CODE);
		ProjectOnlineApplyMarket poamQuery = DTOUtils.newDTO(ProjectOnlineApplyMarket.class);
		poamQuery.setApplyId(apply.getId());
		List<ProjectOnlineApplyMarket> poamList = this.applyMarketMapper.select(poamQuery);
		if (CollectionUtils.isEmpty(poamList)) {
			throw new IllegalArgumentException("数据异常");
		}
		if (poamList.size() == 1 && poamList.get(0).getMarketCode().equals("-1")) {
			apply.aset("marketVersion", false);
		} else {
			apply.aset("marketVersion", true);
			StringBuilder sb = new StringBuilder();
			dd.getValues().stream()
					.filter(v -> poamList.stream().filter(pv -> pv.getMarketCode().equals(v.getValue())).findFirst()
							.orElse(null) != null)
					.collect(Collectors.toList()).forEach(v -> sb.append(v.getCode()).append(","));
			apply.aset("markets", sb.substring(0, sb.length() - 1));
		}
	}

	private void setOperationColumn(ProjectOnlineApply apply) {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			throw new IllegalArgumentException("用户未登录");
		}
		DataDictionaryDto ddDto = this.ddService.findByCode(AlmConstants.DEPARTMENT_MANAGER_ROLE_CONFIG_CODE);
		// 判断界面上用户是否可以编辑申请记录
		// 判断当前申请状态是否是申请中状态
		boolean editable = apply.getApplyState().equals(ProjectOnlineApplyState.APPLING.getValue());
		// 判断当前登录用户是否是申请人
		editable = !editable ? editable : user.getId().equals(apply.getApplicantId());
		apply.aset("editable", editable);

		// 判断界面上用户是否可以点击项目经理确认按钮
		// 判断申请状态是否是项目经理确认状态
		boolean projectManagerConfirmable = apply.getApplyState()
				.equals(ProjectOnlineApplyState.PROJECT_MANAGER_CONFIRMING.getValue());
		// 判断当前登录用户是否是测试负责人
		projectManagerConfirmable = !projectManagerConfirmable ? projectManagerConfirmable
				: user.getId().equals(apply.getProjectManagerId());
		apply.aset("projectManagerConfirmable", projectManagerConfirmable);

		// 判断界面上用户是否可以点击测试确认按钮
		// 判断申请状态是否是测试确认状态
		boolean testConfirmable = apply.getApplyState().equals(ProjectOnlineApplyState.TESTER_CONFIRMING.getValue());
		// 判断当前登录用户是否是测试负责人
		DataDictionaryValueDto ddValueDto = ddDto.getValues().stream()
				.filter(v -> v.getCode().equals(AlmConstants.TEST_MANAGER_CODE)).findFirst().orElse(null);
		testConfirmable = !testConfirmable ? testConfirmable
				: ddValueDto != null && user.getUserName().equals(ddValueDto.getValue());
		apply.aset("testConfirmable", testConfirmable);

		// 判断界面上用户是否可点击开始执行按钮
		// 判断申请状态是否是执行中
		boolean startExecutable = apply.getApplyState().equals(ProjectOnlineApplyState.EXECUTING.getValue());
		// 判断当前申请是否分配了执行人
		if (startExecutable && StringUtils.isNotEmpty(apply.getExecutorId())) {
			startExecutable = false;
		}
		// 判断当前登录用户是否是运维负责人
		ddValueDto = ddDto.getValues().stream().filter(v -> v.getCode().equals(AlmConstants.OPERATION_MANAGER_CODE))
				.findFirst().orElse(null);
		startExecutable = !startExecutable ? startExecutable
				: ddValueDto != null && user.getUserName().equals(ddValueDto.getValue());
		apply.aset("startExecutable", startExecutable);

		// 判断界面上用户是否可点击确认执行按钮
		// 判断申请状态是否是执行中
		boolean confirmExecutable = apply.getApplyState().equals(ProjectOnlineApplyState.EXECUTING.getValue());
		// 判断当前申请是否分配了执行人
		if (confirmExecutable && StringUtils.isNotEmpty(apply.getExecutorId())) {
			// 检查当前的执行人是否在运维经理分配的执行人当中,同时要判断当前用户是否已经确认执行过
			confirmExecutable = false;
			for (String str : apply.getExecutorId().split(",")) {
				if (user.getId().equals(Long.valueOf(str))) {
					confirmExecutable = true;
					break;
				}
			}
		} else {
			confirmExecutable = false;
		}
		apply.aset("confirmExecutable", confirmExecutable);

		// 判断界面上用户是否可以点击验证按钮
		// 判断申请状态是否是验证中
		boolean verifiable = apply.getApplyState().equals(ProjectOnlineApplyState.VARIFING.getValue());
		// 判断当前登录用户是否是当前项目的产品经理
		verifiable = !verifiable ? verifiable : apply.getProductManagerId().equals(user.getId());
		apply.aset("verifiable", verifiable);
	}
}