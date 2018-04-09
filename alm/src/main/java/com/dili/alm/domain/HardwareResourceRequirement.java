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
 * This file was generated on 2018-04-09 11:05:47.
 */
@Table(name = "`hardware_resource_requirement`")
public interface HardwareResourceRequirement extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`cpu_amount`")
    @FieldDef(label="cpu内核数")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getCpuAmount();

    void setCpuAmount(Integer cpuAmount);

    @Column(name = "`memory_amount`")
    @FieldDef(label="内存大小（GB）")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getMemoryAmount();

    void setMemoryAmount(Integer memoryAmount);

    @Column(name = "`disk_amount`")
    @FieldDef(label="磁盘大小（GB）")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getDiskAmount();

    void setDiskAmount(Integer diskAmount);

    @Column(name = "`notes`")
    @FieldDef(label="备注", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getNotes();

    void setNotes(String notes);

    @Column(name = "`apply_id`")
    @FieldDef(label="IT资源申请id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getApplyId();

    void setApplyId(Long applyId);
}