package com.dili.alm.domain;

import java.util.Date;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-04-09 17:19:52.
 */
@Table(name = "`online_data_change_log`")
public interface OnlineDataChangeLog extends IBaseDomain {
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

    @Column(name = "`online_date_id`")
    @FieldDef(label="业务id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getOnlineDateId();

    void setOnlineDateId(Long onlineDateId);


    @Column(name = "`operator_id`")
    @FieldDef(label="操作人")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getOperatorId();

    void setOperatorId(Long operatorId);
    

}