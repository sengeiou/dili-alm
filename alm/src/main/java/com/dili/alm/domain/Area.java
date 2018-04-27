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
 * This file was generated on 2018-04-27 09:27:25.
 */
@Table(name = "`area`")
public interface Area extends IBaseDomain {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "`id`")
	@FieldDef(label = "区域主键")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getId();

	void setId(Long id);

	@Column(name = "`area_name`")
	@FieldDef(label = "区域名称", maxLength = 16)
	@EditMode(editor = FieldEditor.Text, required = false)
	String getAreaName();

	void setAreaName(String areaName);

	@Column(name = "`area_code`")
	@FieldDef(label = "区域代码", maxLength = 128)
	@EditMode(editor = FieldEditor.Text, required = false)
	String getAreaCode();

	void setAreaCode(String areaCode);

	@Column(name = "`area_short`")
	@FieldDef(label = "区域简称", maxLength = 32)
	@EditMode(editor = FieldEditor.Text, required = false)
	String getAreaShort();

	void setAreaShort(String areaShort);

	@Column(name = "`area_is_hot`")
	@FieldDef(label = "是否热门(0:否、1:是)", maxLength = 1)
	@EditMode(editor = FieldEditor.Text, required = false)
	String getAreaIsHot();

	void setAreaIsHot(String areaIsHot);

	@Column(name = "`area_sequence`")
	@FieldDef(label = "区域序列")
	@EditMode(editor = FieldEditor.Number, required = false)
	Integer getAreaSequence();

	void setAreaSequence(Integer areaSequence);

	@Column(name = "`area_parent_id`")
	@FieldDef(label = "上级主键")
	@EditMode(editor = FieldEditor.Number, required = false)
	Integer getAreaParentId();

	void setAreaParentId(Integer areaParentId);

	@Column(name = "`init_date`")
	@FieldDef(label = "初始时间")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getInitDate();

	void setInitDate(Date initDate);

	@Column(name = "`init_addr`")
	@FieldDef(label = "初始地址", maxLength = 16)
	@EditMode(editor = FieldEditor.Text, required = false)
	String getInitAddr();

	void setInitAddr(String initAddr);
}