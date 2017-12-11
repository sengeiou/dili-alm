package com.dili.alm.service.impl;

import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ProjectChangeMapper;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-30 17:21:55.
 */
@Service
public class ProjectChangeServiceImpl extends BaseServiceImpl<ProjectChange, Long> implements ProjectChangeService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ApproveService approveService;

    @Autowired
    private ProjectService projectService;
    public ProjectChangeMapper getActualDao() {
        return (ProjectChangeMapper) getDao();
    }

    @Override
    public void approve(ProjectChange change) {
        if (change.getStatus() == null) {
            return;
        }

        /*
           处理生成审批单
         */
        if (change.getStatus() == AlmConstants.ApplyState.APPROVE.getCode()) {
            Approve as = DTOUtils.as(this.get(change.getId()), Approve.class);
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
        try {
            String[] sendTo = change.getEmail().split(",");
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("yanggang@diligrp.com");
            message.setFrom(SystemConfigUtils.getProperty("spring.mail.username"));
            message.setSubject("变更申请");
            message.setText(change.getProjectName() + "的变更申请[" + change.getName() + "]已经提交成功");
            mailSender.send(message);
        } catch (Exception e) {
            LOGGER.error("邮件发送出错:", e);
        }
    }

    @Override
    public Long reChange(Long id) {
        ProjectChange change = get(id);
        change.setId(null);
        change.setCreated(new Date());
        change.setStatus(AlmConstants.ApplyState.APPLY.getCode());
        insert(change);
        return change.getId();
    }
}