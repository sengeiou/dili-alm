package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.alm.domain.TravelCostApply;

public interface TravelCostApplyUpdateDto extends TravelCostApply {

	List<TravelCostDto> getTravelCost();

	void setTravelCost(List<TravelCostDto> travelCost);
}
