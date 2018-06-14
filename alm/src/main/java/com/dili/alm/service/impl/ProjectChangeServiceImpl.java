package com.dili.alm.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.component.MailManager;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ProjectChangeMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.provider.ProjectTypeProvider;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.ProjectChangeService;
import com.dili.alm.service.ProjectService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.session.SessionContext;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-30 17:21:55.
 */
@Service
public class ProjectChangeServiceImpl extends BaseServiceImpl<ProjectChange, Long> implements ProjectChangeService {

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
	@Autowired
	private ProjectTypeProvider projectTypeProvider;

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
		Map<Object, Object> viewModel = buildViewModel(change);
		Template template = this.groupTemplate.getTemplate(this.projectChangeMailTemplate);
		viewModel.put("projectType", this.projectTypeProvider.getDisplayText(project.getType(), null, null));
		template.binding("model", viewModel);

		// 发送
		for (String addr : change.getEmail().split(",")) {
			try {
				this.mailManager.sendMail(this.mailFrom, addr, template.render(), true, "变更申请", null);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	public static Map<Object, Object> buildViewModel(ProjectChange project) {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject projectVersionProvider = new JSONObject();
		projectVersionProvider.put("provider", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);

		JSONObject projectPhaseProvider = new JSONObject();
		projectPhaseProvider.put("provider", "projectPhaseProvider");
		metadata.put("phaseId", projectPhaseProvider);

		JSONObject changeTypeProvider = new JSONObject();
		changeTypeProvider.put("provider", "changeTypeProvider");
		metadata.put("type", changeTypeProvider);

		try {
			@SuppressWarnings("rawtypes")
			List<Map> viewModelList = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(project));
			if (CollectionUtils.isEmpty(viewModelList)) {
				return null;
			}
			return viewModelList.get(0);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
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