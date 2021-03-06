package com.dili.alm.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-09-12 14:53:52.
 */
@Table(name = "`project_complete`")
public interface ProjectComplete extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`project_id`")
    @FieldDef(label="项目Id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getProjectId();

    void setProjectId(Long projectId);

    @Column(name = "`number`")
    @FieldDef(label="项目编号", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getNumber();

    void setNumber(String number);

    @Column(name = "`name`")
    @FieldDef(label="项目名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getName();

    void setName(String name);

    @Column(name = "`type`")
    @FieldDef(label="项目类型", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getType();

    void setType(String type);

    @Column(name = "`start_date`")
    @FieldDef(label="计划开始日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getStartDate();

    void setStartDate(Date startDate);

    @Column(name = "`end_date`")
    @FieldDef(label="计划结束日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getEndDate();

    void setEndDate(Date endDate);

    @Column(name = "`launch_date`")
    @FieldDef(label="实际上线日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getLaunchDate();

    void setLaunchDate(Date launchDate);

    @Column(name = "`complete_date`")
    @FieldDef(label="结项日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getCompleteDate();

    void setCompleteDate(Date completeDate);

    @Column(name = "`status`")
    @FieldDef(label="status")
    @EditMode(editor = FieldEditor.Text, required = false)
    Integer getStatus();

    void setStatus(Integer status);

    @Column(name = "`created`")
    @FieldDef(label="created")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreated();

    void setCreated(Date created);

    @Column(name = "`modified`")
    @FieldDef(label="modified")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getModified();

    void setModified(Date modified);

    @Column(name = "`create_member_id`")
    @FieldDef(label="createMemberId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getCreateMemberId();

    void setCreateMemberId(Long createMemberId);

    @Column(name = "`modify_member_id`")
    @FieldDef(label="modifyMemberId")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getModifyMemberId();

    void setModifyMemberId(Long modifyMemberId);

    @Column(name = "`reStatus`")
    @FieldDef(label="restatus")
    @EditMode(editor = FieldEditor.Text, required = false)
    Integer getRestatus();

    void setRestatus(Integer restatus);

    @Column(name = "`other_cost`")
    @FieldDef(label="�����ɱ�")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getOtherCost();

    void setOtherCost(Long otherCost);

    @Column(name = "`software_cost`")
    @FieldDef(label="����ɱ�")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getSoftwareCost();

    void setSoftwareCost(Long softwareCost);

    @Column(name = "`reason`")
    @FieldDef(label="reason")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getReason();

    void setReason(String reason);

    @Column(name = "`information`")
    @FieldDef(label="项目资料齐备情况")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getInformation();

    void setInformation(String information);

    @Column(name = "`range`")
    @FieldDef(label="range")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getRange();

    void setRange(String range);

    @Column(name = "`management_method`")
    @FieldDef(label="项目管理方法")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getManagementMethod();

    void setManagementMethod(String managementMethod);

    @Column(name = "`appraise`")
    @FieldDef(label="项目管理过程评价")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getAppraise();

    void setAppraise(String appraise);

    @Column(name = "`technical_evaluation`")
    @FieldDef(label="技术方法评价")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getTechnicalEvaluation();

    void setTechnicalEvaluation(String technicalEvaluation);

    @Column(name = "`raise`")
    @FieldDef(label="进步与提高")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getRaise();

    void setRaise(String raise);

    @Column(name = "`result`")
    @FieldDef(label="成果")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getResult();

    void setResult(String result);

    @Column(name = "`product`")
    @FieldDef(label="产品")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProduct();

    void setProduct(String product);

    @Column(name = "`performance`")
    @FieldDef(label="主要功能和性能")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getPerformance();

    void setPerformance(String performance);

    @Column(name = "`suggest`")
    @FieldDef(label="建议")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getSuggest();

    void setSuggest(String suggest);

    @Column(name = "`question`")
    @FieldDef(label="问题")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getQuestion();

    void setQuestion(String question);

    @Column(name = "`members`")
    @FieldDef(label="成员")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getMembers();

    void setMembers(String members);

    @Column(name = "`hardware`")
    @FieldDef(label="硬件")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getHardware();

    void setHardware(String hardware);

    @Column(name = "`email`")
    @FieldDef(label="email")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getEmail();

    void setEmail(String email);
}