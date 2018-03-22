package com.dili.alm.service.impl;

import com.dili.alm.dao.EmailAddressMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectOnlineApplyMapper;
import com.dili.alm.dao.ProjectOnlineSubsystemMapper;
import com.dili.alm.domain.ApplyType;
import com.dili.alm.domain.EmailAddress;
import com.dili.alm.domain.OperationResult;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.ProjectOnlineApplyState;
import com.dili.alm.domain.ProjectOnlineSubsystem;
import com.dili.alm.domain.dto.ProjectOnlineApplyUpdateDto;
import com.dili.alm.exceptions.ProjectOnlineApplyException;
import com.dili.alm.service.ProjectOnlineApplyDetailDto;
import com.dili.alm.service.ProjectOnlineApplyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-13 15:31:10.
 */
@Service
public class ProjectOnlineApplyServiceImpl extends BaseServiceImpl<ProjectOnlineApply, Long>
		implements ProjectOnlineApplyService {

	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private ProjectOnlineSubsystemMapper posMapper;
	@Autowired
	private EmailAddressMapper emailAddressMapper;

	public ProjectOnlineApplyMapper getActualDao() {
		return (ProjectOnlineApplyMapper) getDao();
	}

	@Override
	public void saveOrUpdate(ProjectOnlineApplyUpdateDto dto) {
		if (dto.getId() == null) {
			this.insertProjectOnlineApply(dto);
		} else {
			this.updateProjectOnlineApply(dto);
		}
	}

	@Override
	public void updateProjectOnlineApply(ProjectOnlineApplyUpdateDto apply) {

		// 判断状态必须是未提交状态才能编辑
		ProjectOnlineApply old = this.getActualDao().selectByPrimaryKey(apply.getId());
		Assert.isTrue(old.getApplyState().equals(ProjectOnlineApplyState.APPLING.getValue()), "当前状态不能编辑");

		// 查询出项目信息
		Project project = this.projectMapper.selectByPrimaryKey(apply.getProjectId());
		apply.setProjectName(project.getName());
		apply.setBusinessOwnerId(project.getBusinessOwner());
		apply.setProjectManagerId(project.getProjectManager());
		apply.setProductManagerId(project.getProductManager());
		apply.setTestManagerId(project.getTestManager());
		apply.setDevelopmentManagerId(project.getDevelopManager());

		// 判断申请上线日期是否大于当前日期
		Assert.isTrue(apply.getOnlineDate().getTime() > System.currentTimeMillis(), "申请上线日期不能小于当前日期");

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
		Assert.isTrue(result > 0, "更新失败");

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
		Assert.state(apply.getApplyState().equals(ProjectOnlineApplyState.APPLING.getValue()), "当前状态不能提交申请");
		// 提交申请进入测试确认状态
		apply.setApplyState(ProjectOnlineApplyState.TESTER_CONFIRING.getValue());
	}

	@Override
	public void startExecute(Long applyId, String description) throws ProjectOnlineApplyException {
		
	}

	@Override
	public void testerConfirm(Long applyId, OperationResult result) throws ProjectOnlineApplyException {
		// TODO Auto-generated method stub

	}

	@Override
	public void excuteConfirm(Long applyId, OperationResult result) throws ProjectOnlineApplyException {
		// TODO Auto-generated method stub

	}

	@Override
	public void validate(Long applyId) throws ProjectOnlineApplyException {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	public void insertProjectOnlineApply(ProjectOnlineApplyUpdateDto apply) {
		// 获取申请人
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		Assert.notNull(user, "请先登录");
		apply.setApplicantId(user.getId());
		// 查询出项目信息
		Project project = this.projectMapper.selectByPrimaryKey(apply.getProjectId());
		apply.setProjectName(project.getName());
		apply.setBusinessOwnerId(project.getBusinessOwner());
		apply.setProjectManagerId(project.getProjectManager());
		apply.setProductManagerId(project.getProductManager());
		apply.setTestManagerId(project.getTestManager());
		apply.setDevelopmentManagerId(project.getDevelopManager());

		// 判断申请上线日期是否大于当前日期
		Assert.isTrue(apply.getOnlineDate().getTime() > System.currentTimeMillis(), "申请上线日期不能小于当前日期");

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
		Assert.isTrue(result > 0, "插入失败");

		for (String str : apply.getEmailAddress().split(";")) {
			EmailAddress ea = DTOUtils.newDTO(EmailAddress.class);
			ea.setApplyId(apply.getId());
			ea.setApplyType(ApplyType.ONLINE.getValue());
			ea.setEmailAddress(str);
			this.emailAddressMapper.insert(ea);
		}
	}
}