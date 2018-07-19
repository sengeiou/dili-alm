package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.alm.domain.TravelCostDetailEntity;

public class TravelCostDtoBean extends TravelCostEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4063694974338766372L;

	private List<TravelCostDetailEntity> travelCostDetail;

	public List<TravelCostDetailEntity> getTravelCostDetail() {
		return travelCostDetail;
	}

	public void setTravelCostDetail(List<TravelCostDetailEntity> travelCostDetail) {
		this.travelCostDetail = travelCostDetail;
	}
}
