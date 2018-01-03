package com.dili.alm.domain.dto;

import java.util.Date;

import javax.persistence.Column;

import com.dili.alm.domain.Project;
import com.dili.ss.domain.annotation.Operator;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

public interface ProjectQueryDto extends Project{

	@Operator(Operator.GREAT_EQUAL_THAN)
	@Column(name = "`actual_start_date`")
	@FieldDef(label = "项目开始日期")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getActualBeginStartDate();
	
	void setActualBeginStartDate(Date actualBeginStartDate);
	
	@Operator(Operator.LITTLE_EQUAL_THAN)
	@Column(name = "`actual_start_date`")
	@FieldDef(label = "项目开始日期")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	Date getActualEndStartDate();
	
	void setActualEndStartDate(Date actualEndStartDate);
}