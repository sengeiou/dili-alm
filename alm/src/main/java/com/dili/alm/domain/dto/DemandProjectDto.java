package com.dili.alm.domain.dto;

import java.util.List;

import javax.persistence.Column;

import com.dili.alm.domain.DemandProject;
import com.dili.ss.domain.annotation.Operator;

public class DemandProjectDto extends DemandProject{
	@Operator(Operator.IN)
	@Column(name = "`demand_id`")
	List<Long> demandIds;

	public List<Long> getDemandIds() {
		return demandIds;
	}

	public void setDemandIds(List<Long> demandIds) {
		this.demandIds = demandIds;
	}
	
}
