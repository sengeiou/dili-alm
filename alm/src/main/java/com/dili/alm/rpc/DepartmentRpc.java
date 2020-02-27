package com.dili.alm.rpc;

import java.util.List;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import com.dili.uap.sdk.domain.Department;

@Restful("http://uap.diligrp.com")
public interface DepartmentRpc {

	@POST("/departmentApi/getOne.api")
	BaseOutput<Department> getOne(@VOBody(required = false) Department department);

	@POST("/api/departmentApi/listByExample.api")
	BaseOutput<Department> listByExample(@VOBody(required = false) Department department);
	
/*	@POST("/api/departmentApi/findByUserId")
	BaseOutput<Department> findByUserId(@VOBody(required = false) Long memberId);

	@POST("/api/departmentApi/findByDepartmentName")
	BaseOutput<Department> findByDepartmentName(@VOBody(required = false) String departmentName);*/

	@POST("/departmentApi/listByDepartment.api")
	BaseOutput<List<Department>> listByDepartment(@VOBody(required = false) Department department);
	
	@POST("/departmentApi/findByUserId.api")
	BaseOutput<List<Department>> findByUserId(@VOBody Long userId);
	
	@POST("/departmentApi/get.api")
	BaseOutput<Department> get(@VOBody Long id);
	
	@POST("/departmentApi/getFirstDepartment.api")
	BaseOutput<Department> getFirstDepartment(@VOBody Long id);
}
