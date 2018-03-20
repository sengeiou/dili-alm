package com.dili.alm.controller;

import java.util.Date;

import javax.persistence.Column;

import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.ss.domain.annotation.Operator;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

public interface ProjectOnlineApplyListQueryDto extends ProjectOnlineApply {

	@Operator(Operator.GREAT_EQUAL_THAN)
	@Column(name = "`submit_time`")
	@FieldDef(label = "申请提交日期")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getSubmitStartDate();

	void setSubmitStartDate(Date submitStartDate);

	@Operator(Operator.LITTLE_EQUAL_THAN)
	@Column(name = "`submit_time`")
	@FieldDef(label = "申请提交日期")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getSubmitEndDate();

	void setSubmitEndDate(Date submitEndDate);
}
