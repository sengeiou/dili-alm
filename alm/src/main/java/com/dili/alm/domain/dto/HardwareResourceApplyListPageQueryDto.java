package com.dili.alm.domain.dto;

import java.util.Date;

import javax.persistence.Column;

import com.dili.alm.domain.HardwareResourceApply;
import com.dili.ss.domain.annotation.Operator;

public interface HardwareResourceApplyListPageQueryDto extends HardwareResourceApply {

	void setSubmitBeginTime(Date submitBeginTime);

	@Operator(Operator.GREAT_EQUAL_THAN)
	@Column(name = "`submit_time`")
	Date getSubmitBeginTime();

	void setSubmitEndTime(Date submitEndTime);

	@Operator(Operator.LITTLE_EQUAL_THAN)
	@Column(name = "`submit_time`")
	Date getSubmitEndTime();
}
