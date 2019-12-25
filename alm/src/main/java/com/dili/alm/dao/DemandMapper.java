package com.dili.alm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dili.alm.domain.Demand;
import com.dili.ss.base.MyMapper;

public interface DemandMapper extends MyMapper<Demand> {
	List<Demand> selectDemandListToProject(@Param("projectId")Long projectId,@Param("demandProjectStatus")Integer demandProjectStatus);
}