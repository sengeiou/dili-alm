package com.dili.alm.domain;

import com.dili.ss.domain.annotation.Like;
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
 * This file was generated on 2018-03-19 16:36:42.
 */
@Table(name = "`project_online_apply`")
public interface ProjectOnlineApply extends IBaseDomain {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "`id`")
	@FieldDef(label = "id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getId();

	void setId(Long id);

	@Like(Like.BOTH)
	@Column(name = "`project_name`")
	@FieldDef(label = "项目名称", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = true)
	String getProjectName();

	void setProjectName(String projectName);

	@Column(name = "`project_id`")
	@FieldDef(label = "项目id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getProjectId();

	void setProjectId(Long projectId);

	@Column(name = "`project_serial_number`")
	@FieldDef(label = "项目编号", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = true)
	String getProjectSerialNumber();

	void setProjectSerialNumber(String projectSerialNumber);

	@Column(name = "`version_id`")
	@FieldDef(label = "项目版本id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getVersionId();

	void setVersionId(Long versionId);

	@Column(name = "`version`")
	@FieldDef(label = "版本名称", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = true)
	String getVersion();

	void setVersion(String version);

	@Column(name = "`project_manager_id`")
	@FieldDef(label = "项目经理id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getProjectManagerId();

	void setProjectManagerId(Long projectManagerId);

	@Column(name = "`product_manager_id`")
	@FieldDef(label = "产品经理id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getProductManagerId();

	void setProductManagerId(Long productManagerId);

	@Column(name = "`test_manager_id`")
	@FieldDef(label = "测试经理id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getTestManagerId();

	void setTestManagerId(Long testManagerId);

	@Column(name = "`development_manager_id`")
	@FieldDef(label = "研发经理id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getDevelopmentManagerId();

	void setDevelopmentManagerId(Long developmentManagerId);

	@Column(name = "`online_date`")
	@FieldDef(label = "申请上线日期")
	@EditMode(editor = FieldEditor.Datetime, required = true)
	Date getOnlineDate();

	void setOnlineDate(Date onlineDate);

	@Column(name = "`dependency_description`")
	@FieldDef(label = "上线子系统及依赖关系描述", maxLength = 500)
	@EditMode(editor = FieldEditor.Text, required = false)
	String getDependencyDescription();

	void setDependencyDescription(String dependencyDescription);

	@Column(name = "`scope_description`")
	@FieldDef(label = "上线影响范围描述", maxLength = 500)
	@EditMode(editor = FieldEditor.Text, required = false)
	String getScopeDescription();

	void setScopeDescription(String scopeDescription);

	@Column(name = "`applicant_id`")
	@FieldDef(label = "申请人id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getApplicantId();

	void setApplicantId(Long applicantId);

	@Column(name = "`created`")
	@FieldDef(label = "创建时间")
	@EditMode(editor = FieldEditor.Datetime, required = true)
	Date getCreated();

	void setCreated(Date created);

	@Column(name = "`apply_state`")
	@FieldDef(label = "状态")
	@EditMode(editor = FieldEditor.Number, required = true)
	Integer getApplyState();

	void setApplyState(Integer applyState);

	@Column(name = "`actual_online_date`")
	@FieldDef(label = "实际上线日期")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getActualOnlineDate();

	void setActualOnlineDate(Date actualOnlineDate);

	@Column(name = "`submit_time`")
	@FieldDef(label = "申请提交日期")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getSubmitTime();

	void setSubmitTime(Date submitTime);
}