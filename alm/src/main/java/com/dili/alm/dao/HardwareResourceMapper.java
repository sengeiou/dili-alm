package com.dili.alm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dili.alm.domain.HardwareResource;
import com.dili.ss.base.MyMapper;

public interface HardwareResourceMapper extends MyMapper<HardwareResource> {

	List<HardwareResource> selectByIds(@Param("hardwareResource")HardwareResource hardwareResource, @Param("list")List<Long> list);

	int selectByIdsCounts(@Param("hardwareResource")HardwareResource hardwareResource, @Param("list")List<Long> list);
}