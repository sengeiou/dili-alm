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
 * This file was generated on 2017-11-27 10:29:51.
 */
@Table(name = "`project_version`")
public interface ProjectVersion extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`code`")
    @FieldDef(label="项目发布编号", maxLength = 40)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getCode();

    void setCode(String code);

    @Column(name = "`project_id`")
    @FieldDef(label="项目id")
    @EditMode(editor = FieldEditor.Combo, required = false, params="{\"provider\":\"projectProvider\"}")
    Long getProjectId();

    void setProjectId(Long projectId);

    @Column(name = "`git`")
    @FieldDef(label="git地址", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getGit();

    void setGit(String git);

    @Column(name = "`redmine_url`")
    @FieldDef(label="redmine文档地址", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getRedmineUrl();

    void setRedmineUrl(String redmineUrl);

    @Column(name = "`version`")
    @FieldDef(label="版本号", maxLength = 40)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getVersion();

    void setVersion(String version);

    @Column(name = "`notes`")
    @FieldDef(label="备注", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getNotes();

    void setNotes(String notes);

    @Column(name = "`creator_id`")
    @FieldDef(label="发布人id")
    @EditMode(editor = FieldEditor.Combo, required = false, params="{\"provider\":\"memberProvider\"}")
    Long getCreatorId();

    void setCreatorId(Long creatorId);

    @Column(name = "`modifier_id`")
    @FieldDef(label="修改人id")
    @EditMode(editor = FieldEditor.Combo, required = false, params="{\"provider\":\"memberProvider\"}")
    Long getModifierId();

    void setModifierId(Long modifierId);

    @Column(name = "`created`")
    @FieldDef(label="发布时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreated();

    void setCreated(Date created);

    @Column(name = "`modified`")
    @FieldDef(label="修改时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getModified();

    void setModified(Date modified);

    @Column(name = "`release_time`")
    @FieldDef(label="上线时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getReleaseTime();

    void setReleaseTime(Date releaseTime);

    @Column(name = "`email_notice`")
    @FieldDef(label="是否上线通知")
    @EditMode(editor = FieldEditor.Combo, required = false, params="{\"data\":[{\"text\":\"通知\",\"value\":0},{\"text\":\"不通知\",\"value\":1}],\"provider\":\"emailNoticeProvider\"}")
    Integer getEmailNotice();

    void setEmailNotice(Integer emailNotice);

    @Column(name = "`host`")
    @FieldDef(label="主机", maxLength = 120)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getHost();

    void setHost(String host);

    @Column(name = "`port`")
    @FieldDef(label="端口")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getPort();

    void setPort(Integer port);

    @Column(name = "`visit_url`")
    @FieldDef(label="访问地址", maxLength = 120)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getVisitUrl();

    void setVisitUrl(String visitUrl);

    @Column(name = "`version_state`")
    @FieldDef(label="版本状态")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getVersionState();

    void setVersionState(Integer versionState);

    @Column(name = "`planned_start_date`")
    @FieldDef(label="计划开始日期")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getPlannedStartDate();

    void setPlannedStartDate(Date plannedStartDate);

    @Column(name = "`planned_end_date`")
    @FieldDef(label="计划结束日期")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getPlannedEndDate();

    void setPlannedEndDate(Date plannedEndDate);

    @Column(name = "`planned_online_date`")
    @FieldDef(label="计划上线日期")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getPlannedOnlineDate();

    void setPlannedOnlineDate(Date plannedOnlineDate);

    @Column(name = "`actual_start_date`")
    @FieldDef(label="实际开始日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getActualStartDate();

    void setActualStartDate(Date actualStartDate);

    @Column(name = "`actual_end_date`")
    @FieldDef(label="实际结束日期")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getActualEndDate();

    void setActualEndDate(Date actualEndDate);

    @Column(name = "`completed_progress`")
    @FieldDef(label="完成进度")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getCompletedProgress();

    void setCompletedProgress(Integer completedProgress);

    @Column(name = "`online`")
    @FieldDef(label="是否已上线，0未上线，1已上线")
    @EditMode(editor = FieldEditor.Text, required = true)
    Byte getOnline();

    void setOnline(Byte online);
}