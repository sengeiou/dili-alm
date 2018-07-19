package com.dili.alm.domain.dto;

import com.dili.alm.domain.TravelCost;
import com.dili.ss.domain.BaseDomain;

public class TravelCostEntity extends BaseDomain implements TravelCost {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4798850270023738889L;
	private Long applyId;
	private Integer travelDayAmount;
	private String setOutPlace;
	private String destinationPlace;
	private Long totalAmount;

	@Override
	public Long getApplyId() {
		return applyId;
	}

	@Override
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	@Override
	public Integer getTravelDayAmount() {
		return travelDayAmount;
	}

	@Override
	public void setTravelDayAmount(Integer travelDayAmount) {
		this.travelDayAmount = travelDayAmount;
	}

	@Override
	public String getSetOutPlace() {
		return setOutPlace;
	}

	@Override
	public void setSetOutPlace(String setOutPlace) {
		this.setOutPlace = setOutPlace;
	}

	@Override
	public String getDestinationPlace() {
		return destinationPlace;
	}

	@Override
	public void setDestinationPlace(String destinationPlace) {
		this.destinationPlace = destinationPlace;
	}

	@Override
	public Long getTotalAmount() {
		return totalAmount;
	}

	@Override
	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}

}
