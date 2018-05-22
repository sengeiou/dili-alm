package com.dili.alm.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.AlmConstants.ApplyState;
import com.dili.alm.dao.ProjectCompleteMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectComplete;
import com.dili.alm.domain.Team;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ProjectCompleteException;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.ProjectCompleteService;
import com.dili.alm.service.TeamService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.SystemConfigUtils;
import com.dili.sysadmin.sdk.session.SessionContext;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-12-08 11:29:47.
 */
@Transactional(rollbackFor = ApplicationException.class)
@Service
public class ProjectCompleteServiceImpl extends BaseServiceImpl<ProjectComplete, Long>
		implements ProjectCompleteService {

	@Autowired
	private ApproveService approveService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TeamService teamService;
	@Autowired
	private ProjectMapper projectMapper;

	public ProjectCompleteMapper getActualDao() {
		return (ProjectCompleteMapper) getDao();
	}

	@Override
	public void approve(ProjectComplete complete) {
		if (complete.getStatus() == null) {
			return;
		}

		/*
		 * 处理生成审批单
		 */
		if (complete.getStatus() == AlmConstants.ApplyState.APPROVE.getCode()) {
			ProjectComplete projectComplete = get(complete.getId());
			Approve as = DTOUtils.as(complete, Approve.class);
			as.setId(null);
			as.setCreated(new Date());
			as.setNumber(projectComplete.getNumber());
			as.setName(projectComplete.getName());
			as.setProjectApplyId(projectComplete.getId());
			as.setStatus(AlmConstants.ApplyState.APPROVE.getCode());
			as.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
			as.setType(AlmConstants.ApproveType.COMPLETE.getCode());
			as.setProjectType(projectComplete.getType());

			approveService.insertBefore(as);
			sendMail(projectComplete);
		}
	}

	public void sendMail(ProjectComplete complete) {
		try {
			String[] sendTo = complete.getEmail().split(",");
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(sendTo);
			message.setFrom(SystemConfigUtils.getProperty("spring.mail.username"));
			message.setSubject("结项申请");
			message.setText(complete.getName() + "的结项申请已经提交成功");
			mailSender.send(message);
		} catch (Exception e) {
			LOGGER.error("邮件发送出错:", e);
		}
	}

	@Override
	public Long reComplete(Long id) {
		ProjectComplete complete = get(id);
		if (complete == null) {
			return -1L;
		}
		if (!complete.getCreateMemberId().equals(SessionContext.getSessionContext().getUserTicket().getId())) {
			return -1L;
		}
		if (complete.getRestatus() != null) {
			return -1L;
		}
		complete.setRestatus(0);
		update(complete);
		complete.setId(null);
		complete.setCreated(new Date());
		complete.setRestatus(null);
		complete.setStatus(AlmConstants.ApplyState.APPLY.getCode());
		insertSelective(complete);
		return complete.getId();
	}

	@Override
	public Object loadMembers(Long id) throws Exception {
		ProjectComplete projectComplete = this.get(id);
		if (StringUtils.isBlank(projectComplete.getMembers())) {
			Team team = DTOUtils.newDTO(Team.class);
			team.setProjectId(projectComplete.getProjectId());
			List<Team> list = teamService.list(team);
			Map<Object, Object> metadata = new HashMap<>();
			metadata.put("memberId", JSON.parse("{provider:'memberProvider'}"));
			metadata.put("role", JSON.parse("{provider:'teamRoleProvider'}"));
			metadata.put("joinTime", JSON.parse("{provider:'datetimeProvider'}"));
			metadata.put("leaveTime", JSON.parse("{provider:'datetimeProvider'}"));
			List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, list);
			maps.forEach(map -> map.put("created", DateUtil.getDate(new Date())));
			return maps;
		}
		return projectComplete.getPerformance();
	}

	@Override
	public void insertWithCheck(ProjectComplete projectComplete) throws ProjectCompleteException {
		// 验证项目是否已结项
		Project project = this.projectMapper.selectByPrimaryKey(projectComplete.getProjectId());
		if (project == null) {
			throw new ProjectCompleteException("项目不存在");
		}
		if (project.getCloseTime() != null) {
			throw new ProjectCompleteException("该项目已结项，不能重复结项");
		}
		ProjectComplete pcQuery = DTOUtils.newDTO(ProjectComplete.class);
		pcQuery.setProjectId(projectComplete.getProjectId());
		List<ProjectComplete> list = this.getActualDao().select(pcQuery);
		if (CollectionUtils.isNotEmpty(list)) {
			ProjectComplete target = list.stream().filter(pc -> !pc.getStatus().equals(ApplyState.NOPASS.getCode()))
					.findFirst().orElse(null);
			if (target != null) {
				throw new ProjectCompleteException("不能重复提交结项申请");
			}
		}
		int rows = this.getActualDao().insertSelective(projectComplete);
		if (rows <= 0) {
			throw new ProjectCompleteException("新增结项申请失败");
		}
	}
}