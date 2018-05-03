package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;

public class TravelCostDetailEntity extends BaseDomain implements TravelCostDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8960223135925057539L;

	private Long costId;

	private String costName;

	private Long costAmount;

	@Override
	public Long getCostId() {
		return costId;
	}

	@Override
	public void setCostId(Long costId) {
		this.costId = costId;
	}

	@Override
	public String getCostName() {
		return costName;
	}

	@Override
	public void setCostName(String costName) {
		this.costName = costName;
	}

	@Override
	public Long getCostAmount() {
		return costAmount;
	}

	@Override
	public void setCostAmount(Long costAmount) {
		this.costAmount = costAmount;
	}

}
