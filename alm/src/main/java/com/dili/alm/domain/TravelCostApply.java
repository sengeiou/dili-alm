package com.dili.alm.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-04-24 16:43:13.
 */
@Table(name = "`travel_cost_apply`")
public interface TravelCostApply extends IBaseDomain {
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

    @Column(name = "`applicant_id`")
    @FieldDef(label="申请人id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getApplicantId();

    void setApplicantId(Long applicantId);

    @Column(name = "`root_departemnt_id`")
    @FieldDef(label="所属中心id，根部门id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getRootDepartemntId();

    void setRootDepartemntId(Long rootDepartemntId);

    @Column(name = "`department_id`")
    @FieldDef(label="申请部门id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getDepartmentId();

    void setDepartmentId(Long departmentId);

    @Column(name = "`apply_state`")
    @FieldDef(label="申请状态")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getApplyState();

    void setApplyState(Integer applyState);

    @Column(name = "`total_amount`")
    @FieldDef(label="总计")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getTotalAmount();

    void setTotalAmount(Long totalAmount);
}