package com.dili.alm.service.impl;

import com.alibaba.fastjson.JSON;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ProjectChangeMapper;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.apply.ApplyApprove;
import com.dili.alm.rpc.RoleRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectChangeService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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

    public ProjectChangeMapper getActualDao() {
        return (ProjectChangeMapper) getDao();
    }

    @Override
    public void approve(ProjectChange change) {
        if (change.getStatus() == null) {
            return;
        }
        if (change.getStatus() == AlmConstants.ApplyState.APPROVE.getCode()) {
            Approve as = DTOUtils.as(this.get(change.getId()), Approve.class);
            as.setId(null);
            as.setCreated(new Date());
            as.setProjectApplyId(change.getId());
            as.setStatus(AlmConstants.ApplyState.APPROVE.getCode());
            as.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
            as.setType(AlmConstants.ApproveType.CHANGE.getCode());

            DataDictionaryDto code = dataDictionaryService.findByCode(AlmConstants.ROLE_CODE);
            List<DataDictionaryValueDto> values = code.getValues();
            String roleId = values.stream()
                    .filter(v -> Objects.equals(v.getCode(), AlmConstants.ROLE_CODE_WYH))
                    .findFirst().map(DataDictionaryValue::getValue)
                    .orElse(null);

            if (roleId != null) {
                List<ApplyApprove> approveList = Lists.newArrayList();
                List<User> userByRole = userRpc.listUserByRole(Long.parseLong(roleId)).getData();
                userByRole.forEach(u -> {
                    ApplyApprove approve = new ApplyApprove();
                    approve.setUserId(u.getId());
                    approve.setRole(roleRpc.listRoleNameByUserId(Long.valueOf(u.getId())).getData());
                    approveList.add(approve);
                });
                as.setDescription(JSON.toJSONString(approveList));
            }
            approveService.insert(as);
        }
    }
}