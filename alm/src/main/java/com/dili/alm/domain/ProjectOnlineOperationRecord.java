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
 * This file was generated on 2018-03-23 11:45:33.
 */
@Table(name = "`project_online_operation_record`")
public interface ProjectOnlineOperationRecord extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`operation_name`")
    @FieldDef(label="操作名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getOperationName();

    void setOperationName(String operationName);

    @Column(name = "`description`")
    @FieldDef(label="描述", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getDescription();

    void setDescription(String description);

    @Column(name = "`operate_time`")
    @FieldDef(label="操作时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getOperateTime();

    void setOperateTime(Date operateTime);

    @Column(name = "`opertate_result`")
    @FieldDef(label="操作结果")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getOpertateResult();

    void setOpertateResult(Integer opertateResult);

    @Column(name = "`apply_id`")
    @FieldDef(label="项目上线申请id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getApplyId();

    void setApplyId(Long applyId);

    @Column(name = "`operation_type`")
    @FieldDef(label="操作类型，确认是什么角色的人做的操作")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getOperationType();

    void setOperationType(Integer operationType);

    @Column(name = "`operator_id`")
    @FieldDef(label="操作人")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getOperatorId();

    void setOperatorId(Long operatorId);
}