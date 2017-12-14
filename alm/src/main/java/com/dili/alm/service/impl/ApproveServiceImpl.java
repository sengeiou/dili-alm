package com.dili.alm.service.impl;

import cn.afterturn.easypoi.word.WordExportUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.ApproveMapper;
import com.dili.alm.domain.*;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.apply.ApplyApprove;
import com.dili.alm.rpc.RoleRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.*;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.SystemConfigUtils;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.OutputStream;
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
    private JavaMailSender mailSender;

    @Autowired
    private ProjectChangeService projectChangeService;

    @Autowired
    private ProjectCompleteService projectCompleteService;

    @Autowired
    private VerifyApprovalService verifyApprovalService;

    @Autowired
    private UserRpc userRpc;

    @Autowired
    private RoleRpc roleRpc;


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
        metadata.put("status", JSON.parse("{provider:'approveStateProvider'}"));

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
        checkApprove(approveDescription, approve.getStatus(), modelMap);
    }

    @Override
    public void buildChangeApprove(Map modelMap, Long id) {
        Approve approve = this.get(id);
        // 构建Provider
        Map<Object, Object> metadata = new HashMap<>();
        metadata.put("createMemberId", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("created", JSON.parse("{provider:'dateProvider'}"));
        metadata.put("status", JSON.parse("{provider:'changeStateProvider'}"));

        Map dto = null;
        try {
            dto = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(approve)).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        modelMap.put("approve", dto);

        ProjectChange change = projectChangeService.get(approve.getProjectApplyId());
        modelMap.put("change", change);

        String approveDescription = approve.getDescription();
        checkApprove(approveDescription, approve.getStatus(), modelMap);
    }

    /**
     * 检查是否有审批权限
     */
    private void checkApprove(String approveDescription, Integer status, Map map) {
        // 能否审批
        boolean canOpt = false;

        // 只有审批中的才能操作
        if (StringUtils.isNotBlank(approveDescription) && status == AlmConstants.ApplyState.APPROVE.getCode()) {
            List<ApplyApprove> approveList = JSON.parseArray(approveDescription, ApplyApprove.class);

            /*
               能够操作的情况:
                当前操作用户在审批组中
                且没有审批过意见
                且不是组长
             */
            canOpt = approveList.stream()
                    .anyMatch(applyApprove -> Objects.equals(applyApprove.getUserId(), SessionContext.getSessionContext().getUserTicket().getId())
                            && applyApprove.getResult() == null
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
        map.put("canOpt", canOpt);
    }

    @Override
    public void buildCompleteApprove(Map modelMap, Long id) {
        Approve approve = this.get(id);
        // 构建Provider
        Map<Object, Object> metadata = new HashMap<>();
        metadata.put("createMemberId", JSON.parse("{provider:'memberProvider'}"));
        metadata.put("created", JSON.parse("{provider:'dateProvider'}"));
        metadata.put("status", JSON.parse("{provider:'approveStateProvider'}"));

        Map dto = null;
        try {
            dto = ValueProviderUtils.buildDataByProvider(metadata, Lists.newArrayList(approve)).get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        modelMap.put("approve", dto);

        ProjectComplete complete = projectCompleteService.get(approve.getProjectApplyId());
        modelMap.put("complete", complete);

        String approveDescription = approve.getDescription();
        checkApprove(approveDescription, approve.getStatus(), modelMap);
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
                        if (Objects.equals(approve.getType(), AlmConstants.ApproveType.APPLY.getCode())) {
                            ProjectApply apply = projectApplyService.get(approve.getProjectApplyId());
                            apply.setStatus(AlmConstants.ApplyState.NOPASS.getCode());
                            projectApplyService.updateSelective(apply);
                            sendMail(apply, false);
                        } else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.CHANGE.getCode())) {
                            ProjectChange change = projectChangeService.get(approve.getProjectApplyId());
                            change.setStatus(AlmConstants.ApplyState.NOPASS.getCode());
                            projectChangeService.update(change);
                            sendMail(change, false);
                        } else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.COMPLETE.getCode())) {
                            ProjectComplete complete = projectCompleteService.get(approve.getProjectApplyId());
                            complete.setStatus(AlmConstants.ApplyState.NOPASS.getCode());
                            projectCompleteService.update(complete);
                            sendMail(complete, false);
                        }
                        break;
                    case "accept":
                        approve.setStatus(AlmConstants.ApplyState.PASS.getCode());
                        if (Objects.equals(approve.getType(), AlmConstants.ApproveType.APPLY.getCode())) {
                            ProjectApply apply = projectApplyService.get(approve.getProjectApplyId());
                            apply.setStatus(AlmConstants.ApplyState.PASS.getCode());
                            // 立项审批通过生成项目信息
                            buildProject(apply, approve);
                            projectApplyService.updateSelective(apply);
                            sendMail(apply, true);
                        } else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.CHANGE.getCode())) {
                            ProjectChange change = projectChangeService.get(approve.getProjectApplyId());
                            change.setStatus(AlmConstants.ApplyState.PASS.getCode());
                            projectChangeService.update(change);
                            sendMail(change, true);
                        } else if (Objects.equals(approve.getType(), AlmConstants.ApproveType.COMPLETE.getCode())) {
                            ProjectComplete complete = projectCompleteService.get(approve.getProjectApplyId());
                            complete.setStatus(AlmConstants.ApplyState.PASS.getCode());
                            projectCompleteService.update(complete);
                            sendMail(complete, true);
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

    private void sendMail(ProjectApply projectApply, boolean accept) {
        sendMail(projectApply.getEmail().split(","), "立项申请", projectApply.getName() + "的立项申请" + (accept ? "已经审批通过" : "审批未通过"));
    }

    private void sendMail(ProjectChange change, boolean accept) {
        sendMail(change.getEmail().split(","), "变更申请", change.getProjectName() + "的变更申请[" + change.getName() + "]" + (accept ? "已经审批通过" : "审批未通过"));
    }

    private void sendMail(ProjectComplete complete, boolean accept) {
        sendMail(complete.getEmail().split(","), "结项申请", complete.getName() + "的结项申请" + (accept ? "已经审批通过" : "审批未通过"));
    }

    private void sendMail(String[] sendTo, String title, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("yanggang@diligrp.com");
            message.setFrom(SystemConfigUtils.getProperty("spring.mail.username"));
            message.setSubject(title);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            LOGGER.error("邮件发送出错:", e);
        }
    }


    @Override
    public BaseOutput verity(Long id, String opt, String notes) {
        VerifyApproval verifyApproval = DTOUtils.newDTO(VerifyApproval.class);
        verifyApproval.setApproveId(id);
        verifyApproval.setApprover(SessionContext.getSessionContext().getUserTicket().getId());
        verifyApproval.setCreated(new Date());
        verifyApproval.setResult(opt);
        verifyApproval.setCreateMemberId(SessionContext.getSessionContext().getUserTicket().getId());
        verifyApprovalService.insert(verifyApproval);
        Approve approve = get(id);
        approve.setStatus(Objects.equals(opt, "accept") ?AlmConstants.ChangeState.VRIFY.getCode() : approve.getStatus());
        updateSelective(approve);
        return BaseOutput.success();
    }

    @Override
    public void downloadProjectDoc(AlmConstants.ApproveType approveType, Long id, OutputStream os) {
        switch (approveType) {
            case APPLY:

                try {
                    ProjectApply apply = projectApplyService.get(id);
                    Map<String, Object> map = new HashMap<String, Object>();
                    Map<Object, Object> metadata = new HashMap<>(2);
                    JSONObject projectTypeProvider = new JSONObject();
                    projectTypeProvider.put("provider", "projectTypeProvider");
                    metadata.put("type", projectTypeProvider);
                    List<Map> maps = ValueProviderUtils.buildDataByProvider(metadata, projectApplyService.listByExample(apply));
                    Map applyDTO = maps.get(0);
                    map.put("apply", applyDTO);

                   projectApplyService.buildStepOne(map,applyDTO);


                    XWPFDocument doc = WordExportUtil.exportWord07(
                            "/Users/shaofan/Desktop/apply.docx", applyDTO);
                    FileOutputStream fos = new FileOutputStream("/Users/shaofan/Desktop/simpleExcel1.docx");
                    doc.write(fos);
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void insertBefore(Approve as) {
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
        insert(as);
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
        build.setApplyId(apply.getId());
        build.setBusinessOwner(apply.getBusinessOwner());
        build.setProjectState(NOT_START.getValue());
        build.setOriginator(SessionContext.getSessionContext().getUserTicket().getId());
        projectService.insertSelective(build);
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