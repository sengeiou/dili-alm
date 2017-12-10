package com.dili.alm.domain;

import java.util.List;

import javax.persistence.Column;

import com.dili.ss.domain.annotation.Operator;

public interface ProjectTaskInProgress extends Task {

	@Column(name = "`project_id`")
	@Operator(Operator.IN)
	List<Long> getInProgressProjectIds();

	void setInProgressProjectIds(List<Long> projectIds);
}
