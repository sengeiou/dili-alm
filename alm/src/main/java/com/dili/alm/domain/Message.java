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
 * This file was generated on 2017-12-05 10:42:19.
 */
@Table(name = "`message`")
public interface Message extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`url`")
    @FieldDef(label="跳转Url", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getUrl();

    void setUrl(String url);

    @Column(name = "`sender`")
    @FieldDef(label="发送人Id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getSender();

    void setSender(Long sender);

    @Column(name = "`recipient`")
    @FieldDef(label="接收人Id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getRecipient();

    void setRecipient(Long recipient);

    @Column(name = "`created`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreated();

    void setCreated(Date created);

    @Column(name = "`type`")
    @FieldDef(label="消息类型")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getType();

    void setType(Integer type);

    @Column(name = "`is_read`")
    @FieldDef(label="是否已读")
    @EditMode(editor = FieldEditor.Number, required = false)
    Boolean getIsRead();

    void setIsRead(Boolean isRead);
    
    @Column(name = "`name`")
    @FieldDef(label="消息名称", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getName();

    void setName(String name);
}