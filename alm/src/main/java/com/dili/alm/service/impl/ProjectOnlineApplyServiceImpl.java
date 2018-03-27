package com.dili.alm.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Template;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.component.MailManager;
import com.dili.alm.dao.EmailAddressMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectOnlineApplyMapper;
import com.dili.alm.dao.ProjectOnlineOperationRecordMapper;
import com.dili.alm.dao.ProjectOnlineSubsystemMapper;
import com.dili.alm.domain.ApplyType;
import com.dili.alm.domain.EmailAddress;
import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.ProjectOnlineApplyOperationType;
import com.dili.alm.domain.ProjectOnlineApplyState;
import com.dili.alm.domain.ProjectOnlineOperationRecord;
import com.dili.alm.domain.ProjectOnlineSubsystem;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectOnlineApplyUpdateDto;
import com.dili.alm.domain.dto.ProjectOnlineOperationRecordDto;
import com.dili.alm.domain.dto.UserDepartmentRole;
import com.dili.alm.domain.dto.UserDepartmentRoleQuery;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ProjectOnlineApplyException;
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

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-13 15:31:10.
 */
@Transactional(rollbackFor = ApplicationException.class, propagation = Propagation.REQUIRED)
@Service
public class ProjectOnlineApplyServiceImpl extends BaseServiceImpl<ProjectOnlineApply, Long>
		implements ProjectOnlineApplyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectOnlineApplyServiceImpl.class);

	private static final String DEPARTMENT_MANAGER_ROLE_CONFIG_CODE = "department_manager_role_config";
	private static final String TEST_MANAGER_CODE = "test_manager";
	private static final String OPERATION_MANAGER_CODE = "operation_manager";
	private static final String OPERATION_DEPARTMENT_CODE = "maintenance";

	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private ProjectOnlineSubsystemMapper posMapper;
	@Autowired
	private EmailAddressMapper emailAddressMapper;
	@Autowired
	private ProjectOnlineOperationRecordMapper poorMapper;
	@Autowired
	private MailManager mailManager;
	@Value("${spring.mail.username:}")
	private String mailFrom;
	@Autowired
	private DataDictionaryService ddService;
	@Autowired
	private BeetlGroupUtilConfiguration beetlConfig;
	@Autowired
	private UserRpc userRpc;

	public ProjectOnlineApplyMapper getActualDao() {
		return (ProjectOnlineApplyMapper) getDao();
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
	public void updateProjectOnlineApply(ProjectOnlineApplyUpdateDto apply) throws ProjectOnlineApplyException {

		// 判断状态必须是未提交状态才能编辑
		ProjectOnlineApply old = this.getActualDao().selectByPrimaryKey(apply.getId());
		if (old.getApplyState().equals(ProjectOnlineApplyState.APPLING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能编辑");
		}

		// 查询出项目信息
		Project project = this.projectMapper.selectByPrimaryKey(apply.getProjectId());
		apply.setProjectName(project.getName());
		apply.setBusinessOwnerId(project.getBusinessOwner());
		apply.setProjectManagerId(project.getProjectManager());
		apply.setProductManagerId(project.getProductManager());
		apply.setTestManagerId(project.getTestManager());
		apply.setDevelopmentManagerId(project.getDevelopManager());

		// 判断申请上线日期是否大于当前日期
		if (apply.getOnlineDate().getTime() > System.currentTimeMillis()) {
			throw new ProjectOnlineApplyException("申请上线日期不能小于当前日期");
		}

		// 更新子系统，先删除后插入
		if (CollectionUtils.isNotEmpty(apply.getSubsystem())) {
			// 先删除
			ProjectOnlineSubsystem record = DTOUtils.newDTO(ProjectOnlineSubsystem.class);
			record.setApplyId(apply.getId());
			this.posMapper.delete(record);

			// 插入
			apply.getSubsystem().forEach(s -> {
				if (s.getProjectId() != null) {
					Project p = this.projectMapper.selectByPrimaryKey(s.getProjectId());
					s.setProjectName(p.getName());
				}
				this.posMapper.insert(s);
			});
		}

		int result = this.getActualDao().updateByPrimaryKeySelective(apply);
		if (result > 0) {
			throw new ProjectOnlineApplyException("更新失败");
		}

		// 更新邮件通知地址，先删除后插入
		// 先删除
		EmailAddress eaCondition = DTOUtils.newDTO(EmailAddress.class);
		eaCondition.setApplyId(apply.getId());
		this.emailAddressMapper.delete(eaCondition);

		// 插入
		for (String str : apply.getEmailAddress().split(";")) {
			EmailAddress ea = DTOUtils.newDTO(EmailAddress.class);
			ea.setApplyId(apply.getId());
			ea.setApplyType(ApplyType.ONLINE.getValue());
			ea.setEmailAddress(str);
			this.emailAddressMapper.insert(ea);
		}
	}

	@Transactional(rollbackFor = ApplicationException.class, propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
	@Override
	public ProjectOnlineApplyDetailDto getEditViewDataById(Long applyId) {
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
	public void submit(Long applyId) throws ProjectOnlineApplyException {
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(applyId);
		if (apply == null) {
			throw new ProjectOnlineApplyException("申请记录不存在");
		}
		if (!apply.getApplyState().equals(ProjectOnlineApplyState.APPLING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能提交申请");
		}
		// 提交申请进入测试确认状态
		apply.setApplyState(ProjectOnlineApplyState.TESTER_CONFIRING.getValue());
		int result = this.getActualDao().updateByPrimaryKey(apply);
		if (result <= 0) {
			throw new ProjectOnlineApplyException("更新状态失败");
		}
		// 发送邮件
		this.sendMail(apply, "上线申请");
	}

	private void sendMail(ProjectOnlineApply apply, String string) {
		EmailAddress record = DTOUtils.newDTO(EmailAddress.class);
		record.setApplyId(apply.getId());
		List<EmailAddress> emails = this.emailAddressMapper.select(record);
		if (CollectionUtils.isEmpty(emails)) {
			return;
		}
		emails.forEach(e -> {
			String subject = "上线申请";
			try {
				String content = this.getMailContent();
				Template template = this.beetlConfig.getGroupTemplate().getTemplate(content);
				template.binding("apply", this.buildApplyViewModel(apply));
				ProjectOnlineOperationRecord poor = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
				poor.setApplyId(apply.getId());
				List<ProjectOnlineOperationRecord> list = this.poorMapper.select(poor);
				template.binding("opRecords", this.buildOperationRecordViewModel(list));
				this.mailManager.sendMail(this.mailFrom, e.getEmailAddress(), template.render(), subject, null);
			} catch (MessagingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		});
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

	private Map<Object, Object> buildApplyViewModel(ProjectOnlineApply apply) {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("businessOwnerId", memberProvider);
		metadata.put("projectManagerId", memberProvider);
		metadata.put("productManagerId", memberProvider);
		metadata.put("testManagerId", memberProvider);
		metadata.put("developmentManagerId", memberProvider);
		metadata.put("applicantId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "almDateProvider");
		metadata.put("onlineDate", datetimeProvider);
		metadata.put("actualOnlineDate", datetimeProvider);

		JSONObject applyStateProvider = new JSONObject();
		applyStateProvider.put("provider", "projectOnlineApplyStateProvider");
		metadata.put("applyState", applyStateProvider);

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

	private String getMailContent() {
		Resource res = new ClassPathResource("conf/projectOlineApplyMailContentTemplate.html");
		InputStream in = null;
		try {
			in = res.getInputStream();
			return IOUtils.toString(in);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
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
	public void startExecute(Long applyId, Long executorId, List<Long> executors, String description)
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
			if (output.isSuccess()) {
				throw new ProjectOnlineApplyException("查询执行人信息失败");
			}
			if (CollectionUtils.isEmpty(output.getData())) {
				throw new ProjectOnlineApplyException("执行人不存在");
			}
			if (!OPERATION_DEPARTMENT_CODE.equals(output.getData().get(0).getDepartment().getCode())) {
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
		if (!apply.getApplyState().equals(ProjectOnlineApplyState.TESTER_CONFIRING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能执行测试确认");
		}
		// 测试回退
		if (OperationResult.FAILURE.equals(result)) {
			apply.setApplyState(ProjectOnlineApplyState.APPLING.getValue());
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
		// 发邮件
		if (OperationResult.FAILURE.equals(result)) {
			this.sendMail(apply, "上线失败");
		}
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
		if (!apply.getApplyState().equals(ProjectOnlineApplyState.EXECUTING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能执行确认操作");
		}
		if (OperationResult.FAILURE.equals(result)) {
			apply.setApplyState(ProjectOnlineApplyState.FAILURE.getValue());
		}
		if (OperationResult.SUCCESS.equals(result)) {
			apply.setApplyState(ProjectOnlineApplyState.COMPLETED.getValue());
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
		// 发邮件
		if (OperationResult.FAILURE.equals(result)) {
			this.sendMail(apply, "上线失败");
		}
		if (OperationResult.SUCCESS.equals(result)) {
			this.sendMail(apply, "上线成功");
		}
	}

	@Transactional
	@Override
	public void insertProjectOnlineApply(ProjectOnlineApplyUpdateDto apply) throws ProjectOnlineApplyException {
		// 查询出项目信息
		Project project = this.projectMapper.selectByPrimaryKey(apply.getProjectId());
		apply.setProjectName(project.getName());
		apply.setBusinessOwnerId(project.getBusinessOwner());
		apply.setProjectManagerId(project.getProjectManager());
		apply.setProductManagerId(project.getProductManager());
		apply.setTestManagerId(project.getTestManager());
		apply.setDevelopmentManagerId(project.getDevelopManager());

		// 判断申请上线日期是否大于当前日期
		if (apply.getOnlineDate().getTime() > System.currentTimeMillis()) {
			throw new ProjectOnlineApplyException("申请上线日期不能小于当前日期");
		}

		// 插入子系统
		if (CollectionUtils.isNotEmpty(apply.getSubsystem())) {
			apply.getSubsystem().forEach(s -> {
				if (s.getProjectId() != null) {
					Project p = this.projectMapper.selectByPrimaryKey(s.getProjectId());
					s.setProjectName(p.getName());
				}
				this.posMapper.insert(s);
			});
		}

		int result = this.getActualDao().insertSelective(apply);
		if (result > 0) {
			throw new ProjectOnlineApplyException("插入失败");
		}

		for (String str : apply.getEmailAddress().split(";")) {
			EmailAddress ea = DTOUtils.newDTO(EmailAddress.class);
			ea.setApplyId(apply.getId());
			ea.setApplyType(ApplyType.ONLINE.getValue());
			ea.setEmailAddress(str);
			this.emailAddressMapper.insert(ea);
		}
	}

	@Override
	public void deleteProjectOnlineApply(Long id) throws ProjectOnlineApplyException {
		ProjectOnlineApply apply = this.getActualDao().selectByPrimaryKey(id);
		if (!apply.getApplyState().equals(ProjectOnlineApplyState.APPLING.getValue())) {
			throw new ProjectOnlineApplyException("当前状态不能编辑");
		}
		int result = super.delete(id);
		if (result <= 0) {
			throw new ProjectOnlineApplyException("删除失败");
		}
	}

	@Override
	public EasyuiPageOutput listEasyuiPageByExample(ProjectOnlineApply domain, boolean useProvider) throws Exception {
		List<ProjectOnlineApply> list = listByExample(domain);
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (user == null) {
			throw new IllegalArgumentException("用户未登录");
		}
		DataDictionaryDto ddDto = this.ddService.findByCode(DEPARTMENT_MANAGER_ROLE_CONFIG_CODE);
		list.forEach(a -> {

			// 判断界面上用户是否可以编辑申请记录
			// 判断当前申请状态是否是申请中状态
			boolean editable = a.getApplyState().equals(ProjectOnlineApplyState.APPLING.getValue());
			// 判断当前登录用户是否是申请人
			editable = user.getId().equals(a.getApplicantId());
			a.aset("editable", editable);

			// 判断界面上用户是否可以点击测试确认按钮
			// 判断申请状态是否是测试确认状态
			boolean testConfirmable = a.getApplyState().equals(ProjectOnlineApplyState.TESTER_CONFIRING.getValue());
			// 判断当前登录用户是否是测试负责人
			DataDictionaryValueDto ddValueDto = ddDto.getValueByCode(TEST_MANAGER_CODE);
			testConfirmable = ddValueDto != null && user.getId().equals(Long.valueOf(ddValueDto.getValue()));
			a.aset("testConfirmable", testConfirmable);

			// 判断界面上用户是否可点击开始执行按钮
			// 判断申请状态是否是执行中
			boolean startExecutable = a.getApplyState().equals(ProjectOnlineApplyState.EXECUTING.getValue());
			// 判断当前申请是否分配了执行人
			if (StringUtils.isNotEmpty(a.getExecutorId())) {
				startExecutable = false;
			}
			// 判断当前登录用户是否是运维负责人
			ddValueDto = ddDto.getValueByCode(OPERATION_MANAGER_CODE);
			startExecutable = ddValueDto != null && user.getId().equals(Long.valueOf(ddValueDto.getValue()));
			a.aset("startExecutable", startExecutable);

			// 判断界面上用户是否可点击确认执行按钮
			// 判断申请状态是否是执行中
			boolean confirmExecutable = a.getApplyState().equals(ProjectOnlineApplyState.EXECUTING.getValue());
			// 判断当前申请是否分配了执行人
			if (StringUtils.isNotEmpty(a.getExecutorId())) {
				// 检查当前的执行人是否在运维经理分配的执行人当中,同时要判断当前用户是否已经确认执行过
				for (String str : a.getExecutorId().split(",")) {
					if (user.getId().equals(Long.valueOf(str))) {
						confirmExecutable = true;
						break;
					}
				}
				confirmExecutable = false;
			} else {
				confirmExecutable = false;
			}
			a.aset("confirmExecutable", confirmExecutable);

			// 判断界面上用户是否可以点击验证按钮
			// 判断申请状态是否是验证中
			boolean verifiable = a.getApplyState().equals(ProjectOnlineApplyState.VARIFING.getValue());
			// 判断当前登录用户是否是当前项目的产品经理
			verifiable = a.getTestManagerId().equals(user.getId());
			a.aset("verifiable", verifiable);
		});
		@SuppressWarnings("rawtypes")
		long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
		@SuppressWarnings("rawtypes")
		List results = useProvider ? ValueProviderUtils.buildDataByProvider(domain, list) : list;
		return new EasyuiPageOutput(Long.valueOf(total).intValue(), results);
	}

}