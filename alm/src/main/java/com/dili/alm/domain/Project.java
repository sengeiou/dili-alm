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
 * This file was generated on 2018-05-14 11:12:41.
 */
@Table(name = "`project`")
public interface Project extends IBaseDomain {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "`id`")
	@FieldDef(label = "ID")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getId();

	void setId(Long id);

	@Column(name = "`apply_id`")
	@FieldDef(label = "立项申请id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getApplyId();

	void setApplyId(Long applyId);

	@Column(name = "`serial_number`")
	@FieldDef(label = "serialNumber", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = false)
	String getSerialNumber();

	void setSerialNumber(String serialNumber);

	@Column(name = "`parent_id`")
	@FieldDef(label = "上级项目id")
	@EditMode(editor = FieldEditor.Number, required = false)
	Long getParentId();

	void setParentId(Long parentId);

	@Like(Like.BOTH)
	@Column(name = "`name`")
	@FieldDef(label = "项目名称", maxLength = 20)
	@EditMode(editor = FieldEditor.Text, required = false)
	String getName();

	void setName(String name);

	@Column(name = "`type`")
	@FieldDef(label = "项目类型", maxLength = 10)
	@EditMode(editor = FieldEditor.Text, required = false)
	String getType();

	void setType(String type);

	@Column(name = "`project_manager`")
	@FieldDef(label = "项目负责人")
	@EditMode(editor = FieldEditor.Combo, required = false, params = "{\"provider\":\"memberProvider\"}")
	Long getProjectManager();

	void setProjectManager(Long projectManager);

	@Column(name = "`develop_manager`")
	@FieldDef(label = "developManager")
	@EditMode(editor = FieldEditor.Number, required = false)
	Long getDevelopManager();

	void setDevelopManager(Long developManager);

	@Column(name = "`test_manager`")
	@FieldDef(label = "测试负责人")
	@EditMode(editor = FieldEditor.Combo, required = false, params = "{\"provider\":\"memberProvider\"}")
	Long getTestManager();

	void setTestManager(Long testManager);

	@Column(name = "`product_manager`")
	@FieldDef(label = "产品负责人")
	@EditMode(editor = FieldEditor.Combo, required = false, params = "{\"provider\":\"memberProvider\"}")
	Long getProductManager();

	void setProductManager(Long productManager);

	@Column(name = "`created`")
	@FieldDef(label = "创建时间")
	@EditMode(editor = FieldEditor.Datetime, required = true)
	Date getCreated();

	void setCreated(Date created);

	@Column(name = "`modified`")
	@FieldDef(label = "修改时间")
	@EditMode(editor = FieldEditor.Datetime, required = true)
	Date getModified();

	void setModified(Date modified);

	@Column(name = "`start_date`")
	@FieldDef(label = "项目开始日期")
	@EditMode(editor = FieldEditor.Date, required = false)
	Date getStartDate();

	void setStartDate(Date startDate);

	@Column(name = "`end_date`")
	@FieldDef(label = "项目结束日期")
	@EditMode(editor = FieldEditor.Date, required = false)
	Date getEndDate();

	void setEndDate(Date endDate);

	@Column(name = "`actual_start_date`")
	@FieldDef(label = "实际开始时间")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getActualStartDate();

	void setActualStartDate(Date actualStartDate);

	@Column(name = "`actual_end_date`")
	@FieldDef(label = "actualEndDate")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getActualEndDate();

	void setActualEndDate(Date actualEndDate);

	@Column(name = "`project_state`")
	@FieldDef(label = "状态")
	@EditMode(editor = FieldEditor.Number, required = true)
	Integer getProjectState();

	void setProjectState(Integer projectState);

	@Column(name = "`task_count`")
	@FieldDef(label = "任务数")
	@EditMode(editor = FieldEditor.Number, required = true)
	Integer getTaskCount();

	void setTaskCount(Integer taskCount);

	@Column(name = "`member_count`")
	@FieldDef(label = "成员数")
	@EditMode(editor = FieldEditor.Number, required = true)
	Integer getMemberCount();

	void setMemberCount(Integer memberCount);

	@Column(name = "`completed_progress`")
	@FieldDef(label = "完成进度，单位百分之")
	@EditMode(editor = FieldEditor.Number, required = true)
	Integer getCompletedProgress();

	void setCompletedProgress(Integer completedProgress);

	@Column(name = "`originator`")
	@FieldDef(label = "项目发起人")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getOriginator();

	void setOriginator(Long originator);

	@Column(name = "`dep`")
	@FieldDef(label = "dep")
	@EditMode(editor = FieldEditor.Number, required = false)
	Long getDep();

	void setDep(Long dep);

	@Column(name = "`business_owner`")
	@FieldDef(label = "businessOwner")
	@EditMode(editor = FieldEditor.Number, required = false)
	Long getBusinessOwner();

	void setBusinessOwner(Long businessOwner);

	@Column(name = "`estimate_launch_date`")
	@FieldDef(label = "estimateLaunchDate")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getEstimateLaunchDate();

	void setEstimateLaunchDate(Date estimateLaunchDate);

	@Column(name = "`close_time`")
	@FieldDef(label = "项目关闭时间")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getCloseTime();

	void setCloseTime(Date closeTime);
}