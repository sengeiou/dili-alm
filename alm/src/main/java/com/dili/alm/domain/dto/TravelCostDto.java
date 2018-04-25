package com.dili.alm.domain.dto;

import java.util.List;

import com.dili.alm.domain.TravelCost;
import com.dili.alm.domain.TravelCostDetail;

public interface TravelCostDto extends TravelCost {

	List<TravelCostDetail> getTravelCostDetail();

	void setTravelCostDetail(TravelCostDetail travelCostDetail);
}
