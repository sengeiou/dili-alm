package com.dili.alm.rpc;

import com.dili.alm.domain.Department;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;

import java.util.List;

@Restful("http://alm.diligrp.com")
public interface DepartmentRpc {

	@POST("/api/department/list")
	BaseOutput<List<Department>> list(@VOBody(required = false) Department department);

	@POST("/api/department/findByUserId")
	BaseOutput<List<Department>> findByUserId(@VOBody(required = false) Long memberId);

	@POST("/api/department/findByDepartmentName")
	BaseOutput<Department> findByDepartmentName(@VOBody(required = false) String departmentName);

	@POST("/api/department/getChildDepartments")
	BaseOutput<List<Department>> getChildDepartments(@VOBody(required = false) Long departmentId);
}
