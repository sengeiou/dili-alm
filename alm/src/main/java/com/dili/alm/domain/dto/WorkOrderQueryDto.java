package com.dili.alm.domain.dto;

import java.util.Date;

import javax.persistence.Column;

import com.dili.alm.component.ProcessHandleInfoDto;
import com.dili.alm.domain.WorkOrder;
import com.dili.ss.domain.annotation.Operator;

public interface WorkOrderQueryDto extends WorkOrder ,ProcessHandleInfoDto{

	@Operator(Operator.GREAT_EQUAL_THAN)
	@Column(name = "`submit_time`")
	Date getSubmitStartDate();

	void setSubmitStartDate(Date submitStartDate);

	@Operator(Operator.LITTLE_EQUAL_THAN)
	@Column(name = "`submit_time`")
	Date getSubmitEndDate();

	void setSubmitEndDate(Date submitEndDate);
}
