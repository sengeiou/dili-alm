package com.dili.alm.domain.dto;

import java.util.List;

import javax.persistence.Column;

import com.dili.alm.domain.HardwareResource;
import com.dili.ss.domain.annotation.Operator;

public interface HardwareResourceQueryDto extends HardwareResource {

	@Operator(Operator.IN)
	@Column(name = "`project_id`")
	List<Long> getProjectIds();

	void setProjectIds(List<Long> projectIds);
}
