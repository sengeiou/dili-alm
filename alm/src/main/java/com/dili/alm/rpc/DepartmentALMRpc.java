package com.dili.alm.rpc;

import java.util.List;

import com.dili.alm.rpc.dto.AlmDepartmentDto;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;

@Restful("http://alm.diligrp.com")
public interface DepartmentALMRpc {

	@POST("/departmentApi/findByUserId.api")
	BaseOutput<List<AlmDepartmentDto>> findByUserId(@VOBody Long userId);

	@POST("/departmentApi/list.api")
	BaseOutput<List<AlmDepartmentDto>> list(@VOBody AlmDepartmentDto department);

}
