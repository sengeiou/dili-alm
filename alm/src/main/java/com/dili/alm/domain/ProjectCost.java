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
 * This file was generated on 2018-08-17 15:32:05.
 */
@Table(name = "`project_cost`")
public interface ProjectCost extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`apply_id`")
    @FieldDef(label="申请id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getApplyId();

    void setApplyId(Long applyId);

    @Column(name = "`amount`")
    @FieldDef(label="数量")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getAmount();

    void setAmount(Integer amount);

    @Column(name = "`rate`")
    @FieldDef(label="费率", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getRate();

    void setRate(String rate);

    @Column(name = "`software_cost`")
    @FieldDef(label="软件成本")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getSoftwareCost();

    void setSoftwareCost(Long softwareCost);

    @Column(name = "`total`")
    @FieldDef(label="合计")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getTotal();

    void setTotal(Long total);

    @Column(name = "`note`")
    @FieldDef(label="备注", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getNote();

    void setNote(String note);

    @Column(name = "`cost_type`")
    @FieldDef(label="类型")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getCostType();

    void setCostType(Integer costType);
}