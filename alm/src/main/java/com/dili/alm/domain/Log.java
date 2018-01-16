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
 * This file was generated on 2017-11-23 10:19:20.
 */
@Table(name = "`log`")
public interface Log extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`created`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreated();

    void setCreated(Date created);

    @Column(name = "`modified`")
    @FieldDef(label="修改时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getModified();

    void setModified(Date modified);

    @Column(name = "`operator_id`")
    @FieldDef(label="操作员")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getOperatorId();

    void setOperatorId(Long operatorId);

    @Column(name = "`ip`")
    @FieldDef(label="IP", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getIp();

    void setIp(String ip);

    @Column(name = "`content`")
    @FieldDef(label="内容", maxLength = 100)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getContent();

    void setContent(String content);
    
    @Column(name = "`log_number`")
    @FieldDef(label="操作日志Id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getLogNumber();

    void setLogNumber(Long logNumber);
    
    @Column(name = "`log_order`")
    @FieldDef(label="每日自增Id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getLogOrder();

    void setLogOrder(Long logOrder);
}