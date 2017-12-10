package com.dili.alm.service.impl;

import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ProjectChangeMapper;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.rpc.RoleRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectChangeService;
import com.dili.alm.service.ProjectService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-30 17:21:55.
 */
@Service
public class ProjectChangeServiceImpl extends BaseServiceImpl<ProjectChange, Long> implements ProjectChangeService {

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Autowired
    private RoleRpc roleRpc;

    @Autowired
    private UserRpc userRpc;

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