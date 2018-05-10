package com.dili.alm.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-04-24 16:44:28.
 */
@Table(name = "`travel_cost_detail`")
public interface TravelCostDetail extends IBaseDomain {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "`id`")
	@FieldDef(label = "id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getId();

	void setId(Long id);

	@Column(name = "`cost_id`")
	@FieldDef(label = "差旅成本id")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getCostId();

	void setCostId(Long costId);

	@Column(name = "`cost_name`")
	@FieldDef(label = "费用名称", maxLength = 255)
	@EditMode(editor = FieldEditor.Text, required = true)
	String getCostName();

	void setCostName(String costName);

	@Column(name = "`cost_amount`")
	@FieldDef(label = "费用")
	@EditMode(editor = FieldEditor.Number, required = true)
	Long getCostAmount();

	void setCostAmount(Long costAmount);

}