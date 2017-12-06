package com.dili.alm.service.impl;

import com.alibaba.fastjson.JSON;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.cache.ProjectNumberGenerator;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ProjectApplyMapper;
import com.dili.alm.domain.*;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.apply.ApplyApprove;
import com.dili.alm.domain.dto.apply.ApplyFiles;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.ApproveService;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-21 16:19:02.
 */
@Service
public class ProjectApplyServiceImpl extends BaseServiceImpl<ProjectApply, Long> implements ProjectApplyService {

    @Autowired
    private ProjectNumberGenerator projectNumberGenerator;
    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Autowired
    private FilesService filesService;

    @Autowired
    private UserRpc userRpc;

    @Autowired
    private ApproveService approveService;

    private static final String APPLY_PLAN_PHASE_CODE = "apply_plan_phase";

    public ProjectApplyMapper getActualDao() {
        return (ProjectApplyMapper) getDao();
    }

    @Override
    public int insertApply(ProjectApply apply) {
        apply.setNumber(getProjectNumber(apply));
        apply.setName(apply.getNumber());
        apply.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
        apply.setStatus(AlmConstants.ApplyState.APPLY.getCode());
        return getActualDao().insertSelective(apply);
    }

    @Override
    public List<DataDictionaryValueDto> getPlanPhase() {
        DataDictionaryDto dto = this.dataDictionaryService.findByCode(APPLY_PLAN_PHASE_CODE);
        if (dto == null) {
            return null;
        }
        return dto.getValues();
    }

    @Override
    public BaseOutput submit(ProjectApply projectApply, ApplyFiles files) {
        if (StringUtils.isNotBlank(files.getDels())) {
            List<Files> filesFromJson = JSON.parseArray(files.getDels(), Files.class);
            filesFromJson.forEach(file -> filesService.delete(file.getId()));
        }
        if (StringUtils.isNotBlank(files.getFiles())) {
            List<Files> filesFromJson = JSON.parseArray(files.getFiles(), Files.class);
            filesFromJson.forEach(file -> {
                file.setType(FileType.APPLY.getValue());
                file.setRecordId(projectApply.getId());
                filesService.updateSelective(file);
            });
        }
        if (projectApply.getStatus() == AlmConstants.ApplyState.APPROVE.getCode()) {
            Approve as = DTOUtils.as(this.get(projectApply.getId()), Approve.class);
            as.setId(null);
            as.setCreated(new Date());
            as.setProjectApplyId(projectApply.getId());
            as.setStatus(AlmConstants.ApplyState.APPROVE.getCode());
            as.setProjectLeader(as.getCreateMemberId());
            as.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
            as.setProjectType(as.getType());
            as.setExtend(as.getDescription());
            as.setType(AlmConstants.ApproveType.APPLY.getCode());

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
                    approveList.add(approve);
                });
                as.setDescription(JSON.toJSONString(approveList));
            }
            approveService.insert(as);
        }

        this.updateSelective(projectApply);
        return BaseOutput.success(String.valueOf(projectApply.getId()));
    }

    /**
     * 生成规则方式为[级别编号+项目级别]-[2位年]-[4位自增数]
     *
     * @param apply
     * @return
     */
    private String getProjectNumber(ProjectApply apply) {
        String number = apply.getType() + (apply.getPid() == null ? "1" : "2");
        String yearLast = new SimpleDateFormat("yy", Locale.CHINESE).format(Calendar.getInstance().getTime());
        return number + "-" + yearLast + "-" + projectNumberGenerator.get();
    }
}