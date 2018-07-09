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
 * This file was generated on 2018-07-09 15:58:15.
 */
@Table(name = "`project_action_record_config`")
public interface ProjectActionRecordConfig extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`action_code`")
    @FieldDef(label="actionCode", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getActionCode();

    void setActionCode(String actionCode);

    @Column(name = "`action_date_type`")
    @FieldDef(label="日期类型")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getActionDateType();

    void setActionDateType(Date actionDateType);

    @Column(name = "`show_date`")
    @FieldDef(label="是否显示数据")
    @EditMode(editor = FieldEditor.Number, required = true)
    Boolean getShowDate();

    void setShowDate(Boolean showDate);

    @Column(name = "`color`")
    @FieldDef(label="颜色", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getColor();

    void setColor(String color);

    @Column(name = "`hint`")
    @FieldDef(label="是否提示")
    @EditMode(editor = FieldEditor.Number, required = true)
    Boolean getHint();

    void setHint(Boolean hint);

    @Column(name = "`hint_message`")
    @FieldDef(label="提示信息", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getHintMessage();

    void setHintMessage(String hintMessage);
}