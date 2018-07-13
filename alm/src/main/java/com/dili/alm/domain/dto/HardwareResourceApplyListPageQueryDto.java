package com.dili.alm.domain.dto;

import java.util.Date;
import java.util.List;

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

	@Operator(Operator.IN)
	@Column(name = "`apply_state`")
	List<Integer> getApplyStateList();

	void setApplyStateList(List<Integer> applyStateList);

	@Operator(Operator.IN)
	@Column(name = "`project_id`")
	List<Long> getProjectIds();

	void setProjectIds(List<Long> projectIds);
}
