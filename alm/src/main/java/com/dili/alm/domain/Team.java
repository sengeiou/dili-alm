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
 * This file was generated on 2017-12-07 16:15:59.
 */
@Table(name = "`team`")
public interface Team extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="ID")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`project_id`")
    @FieldDef(label="所属项目id")
    @EditMode(editor = FieldEditor.Combo, required = false, params="{\"provider\":\"projectProvider\"}")
    Long getProjectId();

    void setProjectId(Long projectId);

    @Column(name = "`member_id`")
    @FieldDef(label="所属成员id")
    @EditMode(editor = FieldEditor.Combo, required = false, params="{\"provider\":\"memberProvider\"}")
    Long getMemberId();

    void setMemberId(Long memberId);

    @Column(name = "`join_time`")
    @FieldDef(label="加入时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getJoinTime();

    void setJoinTime(Date joinTime);

    @Column(name = "`role`")
    @FieldDef(label="团队角色", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getRole();

    void setRole(String role);

    @Column(name = "`deletable`")
    @FieldDef(label="是否可删除")
    @EditMode(editor = FieldEditor.Number, required = true)
    Boolean getDeletable();

    void setDeletable(Boolean deletable);

    @Column(name = "`leave_time`")
    @FieldDef(label="离开时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getLeaveTime();

    void setLeaveTime(Date leaveTime);
}