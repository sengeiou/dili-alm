package com.dili.alm.domain.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import com.dili.alm.domain.IndicatorType;
import com.dili.alm.domain.ProjectCost;
import com.dili.alm.domain.ProjectCostType;
import com.dili.alm.domain.ProjectEarning;
import com.dili.alm.domain.Roi;
import com.dili.ss.domain.BaseDomain;

public class RoiDto extends BaseDomain implements Roi {

	/**
	 * 
	 */
	private static final long serialVersionUID = 429479742991874702L;

	private List<ProjectEarning> earnings;
	private List<ProjectCost> costs;

	private String total;

	private Long applyId;

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

	public ProjectCost getHumanCost() {
		if (CollectionUtils.isEmpty(this.costs)) {
			return null;
		}
		return this.costs.stream().filter(c -> c.getCostType().equals(ProjectCostType.HUMAN_COST.getValue()))
				.findFirst().orElse(null);
	}

	public ProjectCost getSoftwareCost() {
		if (CollectionUtils.isEmpty(this.costs)) {
			return null;
		}
		return this.costs.stream().filter(c -> c.getCostType().equals(ProjectCostType.SOFTWARE_COST.getValue()))
				.findFirst().orElse(null);
	}

	public ProjectCost getHardwarePhysicalMachineCost() {
		if (CollectionUtils.isEmpty(this.costs)) {
			return null;
		}
		return this.costs.stream()
				.filter(c -> c.getCostType().equals(ProjectCostType.HARDWARE_PHYSICAL_MACHINE_COST.getValue()))
				.findFirst().orElse(null);
	}

	public ProjectCost getHardwareVirtualMachineCost() {
		if (CollectionUtils.isEmpty(this.costs)) {
			return null;
		}
		return this.costs.stream()
				.filter(c -> c.getCostType().equals(ProjectCostType.HARDWARE_VIRTUAL_MACHINE_COST.getValue()))
				.findFirst().orElse(null);
	}

	public ProjectCost getTravelConferenceCost() {
		if (CollectionUtils.isEmpty(this.costs)) {
			return null;
		}
		return this.costs.stream()
				.filter(c -> c.getCostType().equals(ProjectCostType.TRAVEL_CONFERENCE_COST.getValue())).findFirst()
				.orElse(null);
	}

	public ProjectCost getOtherCost() {
		if (CollectionUtils.isEmpty(this.costs)) {
			return null;
		}
		return this.costs.stream().filter(c -> c.getCostType().equals(ProjectCostType.OTHER_COST.getValue()))
				.findFirst().orElse(null);
	}

	@Override
	public Long getApplyId() {
		return applyId;
	}

	@Override
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	@Override
	public String getTotal() {
		return total;
	}

	@Override
	public void setTotal(String total) {
		this.total = total;
	}

}
