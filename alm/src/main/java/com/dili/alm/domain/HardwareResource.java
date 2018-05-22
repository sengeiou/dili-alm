package com.dili.alm.domain;

import com.dili.ss.domain.annotation.Like;
import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-04-03 12:02:09.
 */
@Table(name = "`hardware_resource`")
public interface HardwareResource extends IBaseDomain {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "`id`")
	@FieldDef(label = "id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getId();

	void setId(Long id);

	@Column(name = "`project_id`")
	@FieldDef(label = "项目id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getProjectId();

	void setProjectId(Long projectId);

	@Column(name = "`maintenance_date`")
	@FieldDef(label = "维护日期")
	@EditMode(editor = FieldEditor.Datetime, required = true)
	Date getMaintenanceDate();

	void setMaintenanceDate(Date maintenanceDate);

	@Column(name = "`machine_name`")
	@FieldDef(label = "机器名称", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = true)
	String getMachineName();

	void setMachineName(String machineName);

	@Column(name = "`cpu_core_amount`")
	@FieldDef(label = "cpu内核数")
	@EditMode(editor = FieldEditor.Number, required = true)
	Integer getCpuCoreAmount();

	void setCpuCoreAmount(Integer cpuCoreAmount);

	@Column(name = "`memory`")
	@FieldDef(label = "内存")
	@EditMode(editor = FieldEditor.Number, required = true)
	Integer getMemory();

	void setMemory(Integer memory);

	@Column(name = "`disk`")
	@FieldDef(label = "磁盘")
	@EditMode(editor = FieldEditor.Number, required = true)
	Integer getDisk();

	void setDisk(Integer disk);

	@Like(Like.BOTH)
	@Column(name = "`ip`")
	@FieldDef(label = "ip地址", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = true)
	String getIp();

	void setIp(String ip);

	@Column(name = "`regional`")
	@FieldDef(label = "地域")
	@EditMode(editor = FieldEditor.Number, required = true)
	Integer getRegional();

	void setRegional(Integer regional);

	@Column(name = "`note`")
	@FieldDef(label = "备注", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = false)
	String getNote();

	void setNote(String note);

	@Column(name = "`is_submit`")
	@FieldDef(label = "是否已提交")
	@EditMode(editor = FieldEditor.Number, required = false)
	Integer getIsSubmit();

	void setIsSubmit(Integer isSubmit);

	@Column(name = "`maintenance_owner`")
	@FieldDef(label = "维护人员")
	@EditMode(editor = FieldEditor.Number, required = false)
	Long getMaintenanceOwner();

	void setMaintenanceOwner(Long maintenanceOwner);

	@Column(name = "`last_modify_date`")
	@FieldDef(label = "最后修改时间")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getLastModifyDate();

	void setLastModifyDate(Date lastModifyDate);

	@Column(name = "`service_environment`")
	@FieldDef(label = "使用环境（数据字典）")
	@EditMode(editor = FieldEditor.Number, required = false)
	Long getServiceEnvironment();

	void setServiceEnvironment(Long serviceEnvironment);

	@Column(name = "`cost`")
	@FieldDef(label = "成本")
	@EditMode(editor = FieldEditor.Text, required = false)
	BigDecimal getCost();

	void setCost(BigDecimal cost);
}