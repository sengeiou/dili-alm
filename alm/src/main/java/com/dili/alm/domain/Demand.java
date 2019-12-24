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
 * This file was generated on 2019-12-23 17:32:14.
 */
@Table(name = "`demand`")
public interface Demand extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="自增编号")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`serial_number`")
    @FieldDef(label="需求编号，区域首字母+年月日+时分+001自增数字", maxLength = 25)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getSerialNumber();

    void setSerialNumber(String serialNumber);

    @Column(name = "`name`")
    @FieldDef(label="需求名称", maxLength = 25)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getName();

    void setName(String name);

    @Column(name = "`user_id`")
    @FieldDef(label="需求申请人")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getUserId();

    void setUserId(Long userId);

    @Column(name = "`belong_pro_id`")
    @FieldDef(label="所属系统")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getBelongProId();

    void setBelongProId(Long belongProId);

    @Column(name = "`type`")
    @FieldDef(label="需求类型（数据字典）")
    @EditMode(editor = FieldEditor.Text, required = true)
    Byte getType();

    void setType(Byte type);

    @Column(name = "`status`")
    @FieldDef(label="需求状态（数据字典）")
    @EditMode(editor = FieldEditor.Text, required = true)
    Byte getStatus();

    void setStatus(Byte status);

    @Column(name = "`finish_date`")
    @FieldDef(label="需求期望实现时间(年月日)")
    @EditMode(editor = FieldEditor.Date, required = true)
    Date getFinishDate();

    void setFinishDate(Date finishDate);

    @Column(name = "`document_url`")
    @FieldDef(label="相关附件", maxLength = 120)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getDocumentUrl();

    void setDocumentUrl(String documentUrl);

    @Column(name = "`create_date`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreateDate();

    void setCreateDate(Date createDate);

    @Column(name = "`submit_date`")
    @FieldDef(label="提交时间（开启流程）")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getSubmitDate();

    void setSubmitDate(Date submitDate);

    @Column(name = "`content`")
    @FieldDef(label="需求内容")
    @EditMode(editor = FieldEditor.Text, required = true)
    String getContent();

    void setContent(String content);

    @Column(name = "`reason`")
    @FieldDef(label="需求背景")
    @EditMode(editor = FieldEditor.Text, required = false)
    String getReason();

    void setReason(String reason);
}