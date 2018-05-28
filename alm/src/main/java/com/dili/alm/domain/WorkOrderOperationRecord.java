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
 * This file was generated on 2018-05-23 15:53:52.
 */
@Table(name = "`work_order_operation_record`")
public interface WorkOrderOperationRecord extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`work_order_id`")
    @FieldDef(label="工单id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getWorkOrderId();

    void setWorkOrderId(Long workOrderId);

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

    @Column(name = "`operation_time`")
    @FieldDef(label="操作时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getOperationTime();

    void setOperationTime(Date operationTime);

    @Column(name = "`operation_result`")
    @FieldDef(label="操作结果")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getOperationResult();

    void setOperationResult(Integer operationResult);

    @Column(name = "`operation_type`")
    @FieldDef(label="操作类型，确认是什么角色的人做的操作")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getOperationType();

    void setOperationType(Integer operationType);

    @Column(name = "`operator_id`")
    @FieldDef(label="操作人id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getOperatorId();

    void setOperatorId(Long operatorId);
}