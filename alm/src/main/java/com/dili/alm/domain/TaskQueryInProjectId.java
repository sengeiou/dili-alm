package com.dili.alm.domain;

import java.util.List;

import javax.persistence.Column;

import com.dili.ss.domain.annotation.Operator;

public interface TaskQueryInProjectId extends Task {

	@Column(name = "`project_id`")
	@Operator(Operator.IN)
	List<Long> getInProjectIds();

	void setInProjectIds(List<Long> projectIds);
}
