package com.dili.alm.service.impl;

import com.dili.alm.component.MailManager;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ProjectChangeMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.ProjectChangeService;
import com.dili.alm.service.ProjectService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.util.SystemConfigUtils;
import com.dili.sysadmin.sdk.session.SessionContext;

import org.apache.commons.io.IOUtils;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-30 17:21:55.
 */
@Service
public class ProjectChangeServiceImpl extends BaseServiceImpl<ProjectChange, Long> implements ProjectChangeService {

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private ApproveService approveService;

	@Autowired
	private ProjectService projectService;
	@Autowired
	private MailManager mailManager;
	@Value("${spring.mail.username:}")
	private String mailFrom;
	@Qualifier("mailContentTemplate")
	@Autowired
	private GroupTemplate groupTemplate;
	@Autowired
	private ProjectMapper projectMapper;
	private String projectChangeMailTemplate;

	public ProjectChangeMapper getActualDao() {
		return (ProjectChangeMapper) getDao();
	}

	public ProjectChangeServiceImpl() {
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

	@Override
	public void approve(ProjectChange change) {
		if (change.getStatus() == null) {
			return;
		}

		/*
		 * 处理生成审批单
		 */
		if (change.getStatus() == AlmConstants.ApplyState.APPROVE.getCode()) {
			Approve as = DTOUtils.as(this.get(change.getId()), Approve.class);
			change = get(change.getId());
			as.setId(null);
			as.setName(change.getProjectName());
			as.setCreated(new Date());
			as.setProjectApplyId(change.getId());
			as.setStatus(AlmConstants.ApplyState.APPROVE.getCode());
			as.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
			as.setType(AlmConstants.ApproveType.CHANGE.getCode());
			Project project = projectService.get(change.getProjectId());
			as.setProjectType(project.getType());
			as.setExtend(change.getType());

			approveService.insertBefore(as);
			sendMail(change);
		}
	}

	public void sendMail(ProjectChange change) {
		// 构建邮件内容
		Project project = this.projectMapper.selectByPrimaryKey(change.getProjectId());
		Template template = this.groupTemplate.getTemplate(this.projectChangeMailTemplate);
		template.binding("model", change);
		template.binding("project1", project);

		// 发送
		for (String addr : change.getEmail().split(",")) {
			try {
				this.mailManager.sendMail(this.mailFrom, addr, template.render(), true, "变更申请", null);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public Long reChange(Long id) {
		ProjectChange change = get(id);
		if (change == null) {
			return -1L;
		}
		if (change.getRestatus() != null) {
			return -1L;
		}
		if (!change.getCreateMemberId().equals(SessionContext.getSessionContext().getUserTicket().getId())) {
			return -1L;
		}
		change.setRestatus(0);
		update(change);
		change.setId(null);
		change.setCreated(new Date());
		change.setRestatus(null);
		change.setStatus(AlmConstants.ApplyState.APPLY.getCode());
		insertSelective(change);
		return change.getId();
	}
}