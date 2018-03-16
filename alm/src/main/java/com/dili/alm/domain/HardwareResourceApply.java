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
 * This file was generated on 2018-03-13 15:29:59.
 */
@Table(name = "`hardware_resource_apply`")
public interface HardwareResourceApply extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`project_id`")
    @FieldDef(label="项目id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getProjectId();

    void setProjectId(Long projectId);

    @Column(name = "`project_name`")
    @FieldDef(label="项目名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getProjectName();

    void setProjectName(String projectName);

    @Column(name = "`project_serial_number`")
    @FieldDef(label="项目编号", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getProjectSerialNumber();

    void setProjectSerialNumber(String projectSerialNumber);

    @Column(name = "`project_manager_id`")
    @FieldDef(label="项目经理id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getProjectManagerId();

    void setProjectManagerId(Long projectManagerId);

    @Column(name = "`application_department_id`")
    @FieldDef(label="申请部门id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getApplicationDepartmentId();

    void setApplicationDepartmentId(Long applicationDepartmentId);

    @Column(name = "`applicant_id`")
    @FieldDef(label="申请人id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getApplicantId();

    void setApplicantId(Long applicantId);

    @Column(name = "`application_date`")
    @FieldDef(label="申请日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getApplicationDate();

    void setApplicationDate(Date applicationDate);

    @Column(name = "`service_environment`")
    @FieldDef(label="使用环境（数据字典）", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getServiceEnvironment();

    void setServiceEnvironment(String serviceEnvironment);

    @Column(name = "`apply_reason`")
    @FieldDef(label="申请原因", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getApplyReason();

    void setApplyReason(String applyReason);

    @Column(name = "`other_description`")
    @FieldDef(label="其他原因", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getOtherDescription();

    void setOtherDescription(String otherDescription);
}