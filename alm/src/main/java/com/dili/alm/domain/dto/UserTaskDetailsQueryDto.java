package com.dili.alm.domain.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.dili.alm.domain.TaskDetails;
import com.dili.ss.domain.annotation.Operator;

public interface UserTaskDetailsQueryDto extends TaskDetails {

	@Operator(Operator.IN)
	@Column(name = "`task_id`")
	List<Long> getTaskIds();

	void setTaskIds(List<Long> taskIds);

	@Operator(Operator.GREAT_EQUAL_THAN)
	@Column(name = "created")
	Date getCreatedStartDate();

	void setCreatedStartDate(Date createdStartDate);

	@Operator(Operator.LITTLE_EQUAL_THAN)
	@Column(name = "created")
	Date getCreatedEndDate();

	void setCreatedEndDate(Date createdEndDate);

	@Operator(Operator.GREAT_EQUAL_THAN)
	@Column(name = "modified")
	Date getModifiedStartDate();

	void setModifiedStartDate(Date modifiedStartDate);

	@Operator(Operator.LITTLE_EQUAL_THAN)
	@Column(name = "modified")
	Date getModifiedEndDate();

	void setModifiedEndDate(Date modifiedEndDate);
}
