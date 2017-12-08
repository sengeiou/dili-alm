package com.dili.alm.rpc;

import com.dili.alm.domain.Department;
import com.dili.alm.domain.dto.DepartmentContainRole;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import com.dili.ss.retrofitful.annotation.VOSingleParameter;

import java.util.List;

@Restful("http://almadmin.diligrp.com/almadmin")
public interface DepartmentRpc {

	@POST("/api/department/list")
	BaseOutput<List<Department>> list(@VOBody Department department);

	@POST("/api/department/findByUserId")
	BaseOutput<List<Department>> findByUserId(@VOSingleParameter Long memberId);

	@POST("/api/department/findByUserIdContainRoles")
	BaseOutput<List<DepartmentContainRole>> findByUserIdContainRoles(@VOSingleParameter Long memberId);
}
