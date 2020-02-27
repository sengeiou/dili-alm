package com.dili.alm.rpc;

import java.util.List;

import com.dili.alm.domain.DepartmentALM;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import com.dili.uap.sdk.domain.Department;

@Restful("http://alm.diligrp.com")
public interface DepartmentALMRpc {

	@POST("/api/departmentApi/findByUserId.api")
	BaseOutput<List<Department>> findByUserId(@VOBody Long userId);

	@POST("/api/department/list")
	BaseOutput<List<DepartmentALM>> list(@VOBody DepartmentALM department);

}
