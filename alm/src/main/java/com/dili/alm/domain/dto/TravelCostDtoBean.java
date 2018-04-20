package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.alm.domain.TravelCostDetailEntity;

public class TravelCostDtoBean extends TravelCostEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4063694974338766372L;

	private String setOutPlaceText;
	private String destinationPlaceText;
	private List<TravelCostDetailEntity> travelCostDetail;

	public String getSetOutPlaceText() {
		return setOutPlaceText;
	}

	public String getDestinationPlaceText() {
		return destinationPlaceText;
	}

	public void setSetOutPlaceText(String setOutPlaceText) {
		this.setOutPlaceText = setOutPlaceText;
	}

	public void setDestinationPlaceText(String destinationPlaceText) {
		this.destinationPlaceText = destinationPlaceText;
	}

	public List<TravelCostDetailEntity> getTravelCostDetail() {
		return travelCostDetail;
	}

	public void setTravelCostDetail(List<TravelCostDetailEntity> travelCostDetail) {
		this.travelCostDetail = travelCostDetail;
	}
}
