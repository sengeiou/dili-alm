package com.dili.alm.service.impl;

import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ProjectCompleteMapper;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.ProjectComplete;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.ProjectCompleteService;
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
 * This file was generated on 2017-12-08 11:29:47.
 */
@Service
public class ProjectCompleteServiceImpl extends BaseServiceImpl<ProjectComplete, Long> implements ProjectCompleteService {

    @Autowired
    private ApproveService approveService;

    @Autowired
    private JavaMailSender mailSender;

    public ProjectCompleteMapper getActualDao() {
        return (ProjectCompleteMapper) getDao();
    }

    @Override
    public void approve(ProjectComplete complete) {
        if (complete.getStatus() == null) {
            return;
        }

        /*
           处理生成审批单
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
            message.setTo("yanggang@diligrp.com");
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
        complete.setId(null);
        complete.setCreated(new Date());
        complete.setStatus(AlmConstants.ApplyState.APPLY.getCode());
        insert(complete);
        return complete.getId();
    }
}