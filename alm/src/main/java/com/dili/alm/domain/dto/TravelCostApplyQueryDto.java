package com.dili.alm.domain.dto;

import java.util.Date;

import javax.persistence.Column;

import com.dili.alm.domain.TravelCostApply;
import com.dili.ss.domain.annotation.Operator;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

public interface TravelCostApplyQueryDto extends TravelCostApply {

	@Operator(Operator.GREAT_EQUAL_THAN)
	@Column(name = "`submit_date`")
	@FieldDef(label = "项目开始日期")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getSubmitStartDate();

	void setSubmitStartDate(Date submitStartDate);

	@Operator(Operator.LITTLE_EQUAL_THAN)
	@Column(name = "`submit_date`")
	@FieldDef(label = "项目开始日期")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getSubmitEndDate();

	void setSubmitEndDate(Date submitEndDate);
}
