package com.dili.alm.rpc;

import com.dili.uap.sdk.domain.Department;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Restful("http://alm.diligrp.com")
public interface DepartmentALMRpc {
	
	@POST("/departmentApi/findByUserId.api")
	BaseOutput<List<Department>> findByUserId(@VOBody Long userId);
	
	@POST("/api/department/list")
	BaseOutput<List<Department>> list(@VOBody Department department );
	
	
	
}
