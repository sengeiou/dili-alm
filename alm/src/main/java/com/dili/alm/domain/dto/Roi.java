package com.dili.alm.domain.dto;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.dili.alm.domain.IndicatorType;
import com.dili.alm.domain.ProjectCost;
import com.dili.alm.domain.ProjectEarning;

public class Roi implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 429479742991874702L;

	private List<ProjectEarning> earnings;
	private List<ProjectCost> costs;

	public List<ProjectEarning> getEarnings() {
		return earnings;
	}

	public List<ProjectCost> getCosts() {
		return costs;
	}

	public void setEarnings(List<ProjectEarning> earnings) {
		this.earnings = earnings;
	}

	public void setCosts(List<ProjectCost> costs) {
		this.costs = costs;
	}

	public List<ProjectEarning> getBusinessIndicators() {
		if (CollectionUtils.isEmpty(this.earnings)) {
			return null;
		}
		return this.earnings.stream().filter(pe -> pe.getIndicatorType().equals(IndicatorType.BUSINESS.getValue()))
				.collect(Collectors.toList());
	}

	public List<ProjectEarning> getEfficiencyAndPerformanceIndicators() {
		if (CollectionUtils.isEmpty(this.earnings)) {
			return null;
		}
		return this.earnings.stream()
				.filter(pe -> pe.getIndicatorType().equals(IndicatorType.EFFICIENCY_AND_PERFORMANCE.getValue()))
				.collect(Collectors.toList());
	}

}
