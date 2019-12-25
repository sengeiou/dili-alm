package com.dili.alm.domain.dto;

import java.util.List;


import com.dili.alm.domain.ProjectApply;


public interface ProjectApplyDto extends ProjectApply{
	
	List<String> getDemandIds();

	void setDemandIds(List<String> demandIds);
}
