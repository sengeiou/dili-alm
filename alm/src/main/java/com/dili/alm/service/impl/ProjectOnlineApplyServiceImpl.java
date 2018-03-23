package com.dili.alm.service.impl;

import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.dili.alm.domain.dto.ProjectOnlineApplyUpdateDto;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ProjectOnlineApplyException;
import com.dili.alm.service.ProjectOnlineApplyDetailDto;
import com.dili.alm.service.ProjectOnlineApplyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.util.SystemConfigUtils;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-13 15:31:10.
 */
@Transactional(rollbackFor = ApplicationException.class)
@Service
public class ProjectOnlineApplyServiceImpl extends BaseServiceImpl<ProjectOnlineApply, Long>
		implements ProjectOnlineApplyService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectOnlineApplyServiceImpl.class);

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
	@Value("${com.dili.project.online.apply.submit.message:}")
	private String submitEmailContent;

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

	@Override
	public ProjectOnlineApplyDetailDto getEditViewDataById(Long applyId) {
		// TODO Auto-generated method stub
		return null;
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
		EmailAddress record = DTOUtils.newDTO(EmailAddress.class);
		record.setApplyId(applyId);
		List<EmailAddress> emails = this.emailAddressMapper.select(record);
		if (CollectionUtils.isEmpty(emails)) {
			return;
		}
		emails.forEach(e -> {
			String from = SystemConfigUtils.getProperty("spring.mail.username");
			String content = "";
			String subject = "上线申请";
			try {
				this.mailManager.sendMail(from, e.getEmailAddress(), content, subject, null);
			} catch (MessagingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		});
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
		// 执行成功,产品经理验证,这个地方要判断是不是所有被分配的执行人都执行了，如果所有人都执行了则跳到下一个状态（产品经理验证）
		flag = true;
		if (OperationResult.SUCCESS.equals(result)) {
			ProjectOnlineOperationRecord record = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
			record.setApplyId(applyId);
			record.setOperationType(ProjectOnlineApplyOperationType.OPERATION_EXECUTOR.getValue());
			List<ProjectOnlineOperationRecord> list = this.poorMapper.select(record);
			for (String str : apply.getExecutorId().split(",")) {
				ProjectOnlineOperationRecord t = list.stream().filter(v -> v.getOperatorId().equals(Long.valueOf(str)))
						.findFirst().orElse(null);
				if (t == null) {
					flag = false;
					break;
				}
			}
			if (flag) {
				apply.setApplyState(ProjectOnlineApplyState.VARIFING.getValue());
			}
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
	}

	@Override
	public void verify(Long applyId) throws ProjectOnlineApplyException {
		// TODO Auto-generated method stub

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
}