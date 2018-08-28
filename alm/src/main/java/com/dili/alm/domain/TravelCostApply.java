package com.dili.alm.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dili.ss.domain.annotation.Like;
import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-08-14 16:22:12.
 */
@Table(name = "`travel_cost_apply`")
public interface TravelCostApply extends IBaseDomain {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "`id`")
	@FieldDef(label = "id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getId();

	void setId(Long id);

	@Column(name = "`serial_number`")
	@FieldDef(label = "申请编号", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = true)
	String getSerialNumber();

	void setSerialNumber(String serialNumber);

	@Column(name = "`apply_type`")
	@FieldDef(label = "申请类型")
	@EditMode(editor = FieldEditor.Number, required = false)
	Integer getApplyType();

	void setApplyType(Integer applyType);

	@Column(name = "`project_id`")
	@FieldDef(label = "项目id")
	@EditMode(editor = FieldEditor.Number, required = false)
	Long getProjectId();

	void setProjectId(Long projectId);

	@Like(Like.BOTH)
	@Column(name = "`custom_note`")
	@FieldDef(label = "申请描述", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = false)
	String getCustomNote();

	void setCustomNote(String customNote);

	@Column(name = "`applicant_id`")
	@FieldDef(label = "申请人id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getApplicantId();

	void setApplicantId(Long applicantId);

	@Column(name = "`root_departemnt_id`")
	@FieldDef(label = "所属中心id，根部门id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getRootDepartemntId();

	void setRootDepartemntId(Long rootDepartemntId);

	@Column(name = "`department_id`")
	@FieldDef(label = "申请部门id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getDepartmentId();

	void setDepartmentId(Long departmentId);

	@Column(name = "`apply_state`")
	@FieldDef(label = "申请状态")
	@EditMode(editor = FieldEditor.Number, required = true)
	Integer getApplyState();

	void setApplyState(Integer applyState);

	@Column(name = "`total_amount`")
	@FieldDef(label = "总计")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getTotalAmount();

	void setTotalAmount(Long totalAmount);

	@Column(name = "`travel_day_amount`")
	@FieldDef(label = "出差天数")
	@EditMode(editor = FieldEditor.Number, required = true)
	Integer getTravelDayAmount();

	void setTravelDayAmount(Integer travelDayAmount);

	@Column(name = "`submit_date`")
	@FieldDef(label = "申请提交时间")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getSubmitDate();

	void setSubmitDate(Date submitDate);

	@Column(name = "`creation_time`")
	@FieldDef(label = "创建时间")
	@EditMode(editor = FieldEditor.Datetime, required = true)
	Date getCreationTime();

	void setCreationTime(Date creationTime);

	@Column(name = "`modification_time`")
	@FieldDef(label = "修改时间")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getModificationTime();

	void setModificationTime(Date modificationTime);
}