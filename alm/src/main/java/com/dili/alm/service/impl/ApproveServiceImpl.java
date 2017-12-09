package com.dili.alm.service.impl;

import com.alibaba.fastjson.JSON;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ApproveMapper;
import com.dili.alm.domain.*;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.apply.ApplyApprove;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.*;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.dili.alm.domain.ProjectState.NOT_START;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-12-04 17:39:37.
 */
@Service
public class ApproveServiceImpl extends BaseServiceImpl<Approve, Long> implements ApproveService {


    @Autowired
    private DataDictionaryService dataDictionaryService;


    @Autowired
    private ProjectApplyService projectApplyService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectChangeService projectChangeService;

    @Autowired
    private ProjectCompleteService projectCompleteService;

    @Autowired
    private UserRpc userRpc;

    public ApproveMapper getActualDao() {
        return (ApproveMapper) getDao();
    }

    @Override
    public void buildApplyApprove(Map modelMap, Long id) {
        Approve approve = this.get(id);

        // 构建Provider
        Map<Object, Object> metadata = new HashMap<>();
        metadata.put("projectLeader", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("businessOwner", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("projectType", JSON.parse("{provider:'projectTypeProvider'}"));
        metadata.put("dep", JSON.parse("{provider:'depProvider'}"));
        metadata.put("createMemberId", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("created", JSON.parse("{provider:'dateProvider'}"));

        Map dto = null;
        try {
            dto = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(approve)).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(dto).remove("description");

        modelMap.put("apply", dto);
        modelMap.put("extend", dto.remove("extend"));
        modelMap.put("json", JSON.toJSON(dto));


        String approveDescription = approve.getDescription();
        // 能否审批
        boolean canOpt = false;

        // 只有审批中的才能操作
        if (StringUtils.isNotBlank(approveDescription) && approve.getStatus() == AlmConstants.ApplyState.APPROVE.getCode()) {
            List<ApplyApprove> approveList = JSON.parseArray(approveDescription, ApplyApprove.class);

            /*
               能够操作的情况:
                当前操作用户在审批组中
                且没有审批过意见
                且不是组长
             */
            canOpt = approveList.stream()
                    .anyMatch(applyApprove -> Objects.equals(applyApprove.getUserId(), SessionContext.getSessionContext().getUserTicket().getId())
                            && applyApprove.getResult()     == null
                            && !Objects.equals(applyApprove.getUserId(), getApproveLeader()));


            /*
               如果都不能处理，检查是否扭转到组长
             */
            if (!canOpt) {
                /*
                   处理条件如下:
                    当前操作用户属于组长
                    且审批组中其他成员都全部审批完毕 (审批完毕成员数等于总成员数减1)
                    注：目前只支持一位组长
                 */
                canOpt = Objects.equals(SessionContext.getSessionContext().getUserTicket().getId(), getApproveLeader())
                        && approveList.stream().filter(applyApprove -> !Objects.equals(applyApprove.getUserId(), getApproveLeader()) && applyApprove.getResult() != null)
                        .count() == approveList.size() - 1;
            }

        }
        modelMap.put("canOpt", canOpt);
    }

    @Override
    public void buildChangeApprove(Map modelMap, Long id) {
        Approve approve = this.get(id);
        // 构建Provider
        Map<Object, Object> metadata = new HashMap<>();
        metadata.put("createMemberId", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("created", JSON.parse("{provider:'dateProvider'}"));

        Map dto = null;
        try {
            dto = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(approve)).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        modelMap.put("approve",dto);

        ProjectChange change = projectChangeService.get(approve.getProjectApplyId());
        modelMap.put("change",change);

        String approveDescription = approve.getDescription();
        // 能否审批
        boolean canOpt = false;

        // 只有审批中的才能操作
        if (StringUtils.isNotBlank(approveDescription) && approve.getStatus() == AlmConstants.ApplyState.APPROVE.getCode()) {
            List<ApplyApprove> approveList = JSON.parseArray(approveDescription, ApplyApprove.class);

            /*
               能够操作的情况:
                当前操作用户在审批组中
                且没有审批过意见
                且不是组长
             */
            canOpt = approveList.stream()
                    .anyMatch(applyApprove -> Objects.equals(applyApprove.getUserId(), SessionContext.getSessionContext().getUserTicket().getId())
                            && applyApprove.getResult()     == null
                            && !Objects.equals(applyApprove.getUserId(), getApproveLeader()));


            /*
               如果都不能处理，检查是否扭转到组长
             */
            if (!canOpt) {
                /*
                   处理条件如下:
                    当前操作用户属于组长
                    且审批组中其他成员都全部审批完毕 (审批完毕成员数等于总成员数减1)
                    注：目前只支持一位组长
                 */
                canOpt = Objects.equals(SessionContext.getSessionContext().getUserTicket().getId(), getApproveLeader())
                        && approveList.stream().filter(applyApprove -> !Objects.equals(applyApprove.getUserId(), getApproveLeader()) && applyApprove.getResult() != null)
                        .count() == approveList.size() - 1;
            }

        }
        modelMap.put("canOpt", canOpt);
    }

    @Override
    public void buildCompleteApprove(Map modelMap, Long id) {
        Approve approve = this.get(id);
        // 构建Provider
        Map<Object, Object> metadata = new HashMap<>();
        metadata.put("createMemberId", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("created", JSON.parse("{provider:'dateProvider'}"));

        Map dto = null;
        try {
            dto = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(approve)).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        modelMap.put("approve",dto);

        ProjectComplete complete = projectCompleteService.get(approve.getProjectApplyId());
        modelMap.put("complete",complete);

        String approveDescription = approve.getDescription();
        // 能否审批
        boolean canOpt = false;

        // 只有审批中的才能操作
        if (StringUtils.isNotBlank(approveDescription) && approve.getStatus() == AlmConstants.ApplyState.APPROVE.getCode()) {
            List<ApplyApprove> approveList = JSON.parseArray(approveDescription, ApplyApprove.class);

            /*
               能够操作的情况:
                当前操作用户在审批组中
                且没有审批过意见
                且不是组长
             */
            canOpt = approveList.stream()
                    .anyMatch(applyApprove -> Objects.equals(applyApprove.getUserId(), SessionContext.getSessionContext().getUserTicket().getId())
                            && applyApprove.getResult()     == null
                            && !Objects.equals(applyApprove.getUserId(), getApproveLeader()));


            /*
               如果都不能处理，检查是否扭转到组长
             */
            if (!canOpt) {
                /*
                   处理条件如下:
                    当前操作用户属于组长
                    且审批组中其他成员都全部审批完毕 (审批完毕成员数等于总成员数减1)
                    注：目前只支持一位组长
                 */
                canOpt = Objects.equals(SessionContext.getSessionContext().getUserTicket().getId(), getApproveLeader())
                        && approveList.stream().filter(applyApprove -> !Objects.equals(applyApprove.getUserId(), getApproveLeader()) && applyApprove.getResult() != null)
                        .count() == approveList.size() - 1;
            }

        }
        modelMap.put("canOpt", canOpt);
    }

    @Override
    public BaseOutput applyApprove(Long id, String opt, String notes) {
        Approve approve = this.get(id);
        String approveDescription = approve.getDescription();
        if (StringUtils.isNotBlank(approveDescription)) {
            List<ApplyApprove> approveList = JSON.parseArray(approveDescription, ApplyApprove.class);
            ApplyApprove current = approveList.stream().filter(v -> Objects.equals(v.getUserId(), SessionContext.getSessionContext().getUserTicket().getId())).findFirst().get();
            current.setApproveDate(new Date());
            current.setResult(opt);
            current.setNotes(notes);
            approve.setDescription(JSON.toJSONString(approveList));
            this.updateSelective(approve);

            /*
                如果审批的是组长，需特殊处理
             */
            if (Objects.equals(SessionContext.getSessionContext().getUserTicket().getId(), getApproveLeader())) {

                /*
                    处理立项申请单和立项审批单状态
                 */


                switch (opt) {
                    case "reject":
                        approve.setStatus(AlmConstants.ApplyState.NOPASS.getCode());
                        if(Objects.equals(approve.getType(), AlmConstants.ApproveType.APPLY.getCode())){
                            ProjectApply apply = projectApplyService.get(approve.getProjectApplyId());
                            apply.setStatus(AlmConstants.ApplyState.NOPASS.getCode());
                            projectApplyService.updateSelective(apply);
                        }else if(Objects.equals(approve.getType(), AlmConstants.ApproveType.CHANGE.getCode())){
                            ProjectChange change = projectChangeService.get(approve.getProjectApplyId());
                            change.setStatus(AlmConstants.ApplyState.NOPASS.getCode());
                            projectChangeService.update(change);
                        } else if(Objects.equals(approve.getType(), AlmConstants.ApproveType.COMPLETE.getCode())){
                            ProjectComplete complete = projectCompleteService.get(approve.getProjectApplyId());
                            complete.setStatus(AlmConstants.ApplyState.NOPASS.getCode());
                            projectCompleteService.update(complete);
                        }
                        break;
                    case "accept":
                        approve.setStatus(AlmConstants.ApplyState.PASS.getCode());
                        if(Objects.equals(approve.getType(), AlmConstants.ApproveType.APPLY.getCode())){
                            ProjectApply apply = projectApplyService.get(approve.getProjectApplyId());
                            apply.setStatus(AlmConstants.ApplyState.PASS.getCode());
                            // 立项审批通过生成项目信息
                            buildProject(apply, approve);
                            projectApplyService.updateSelective(apply);
                        }else if(Objects.equals(approve.getType(), AlmConstants.ApproveType.CHANGE.getCode())){
                            ProjectChange change = projectChangeService.get(approve.getProjectApplyId());
                            change.setStatus(AlmConstants.ApplyState.PASS.getCode());
                            projectChangeService.update(change);
                        } else if(Objects.equals(approve.getType(), AlmConstants.ApproveType.COMPLETE.getCode())){
                            ProjectComplete complete = projectCompleteService.get(approve.getProjectApplyId());
                            complete.setStatus(AlmConstants.ApplyState.PASS.getCode());
                            projectCompleteService.update(complete);
                        }
                        break;
                    default:
                        break;
                }

                updateSelective(approve);
            }
        }
        return BaseOutput.success();
    }


    /**
     * 立项审批通过生成项目信息
     */
    public void buildProject(ProjectApply apply, Approve approve) {
        Project build = DTOUtils.newDTO(Project.class);
        build.setSerialNumber(apply.getNumber());
        build.setName(apply.getName());
        build.setType(apply.getType());
        build.setProjectManager(apply.getCreateMemberId());
        build.setDevelopManager(apply.getDevelopmentManager());
        build.setTestManager(apply.getTestManager());
        build.setProductManager(apply.getProductManager());
        build.setStartDate(apply.getStartDate());
        build.setEndDate(apply.getEndDate());
        build.setDep(apply.getDep());
        build.setBusinessOwner(apply.getBusinessOwner());
        build.setProjectState(NOT_START.getValue());
        try {
            projectService.insertSelective(build);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取系统中项目委员会组长用户主键
     *
     * @return userId
     */
    private Long getApproveLeader() {
        List<DataDictionaryValueDto> values = dataDictionaryService.findByCode(AlmConstants.ROLE_CODE).getValues();

        String roleId = values.stream()
                .filter(v -> Objects.equals(v.getCode(), AlmConstants.ROLE_CODE_WYH_LEADER))
                .findFirst().map(DataDictionaryValue::getValue)
                .orElse(null);

        if (roleId != null) {
            List<User> users = userRpc.listUserByRole(Long.valueOf(roleId)).getData();
            return users.stream().findFirst().map(User::getId).orElse(null);
        }

        return null;
    }
}