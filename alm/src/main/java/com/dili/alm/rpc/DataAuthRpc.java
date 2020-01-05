package com.dili.alm.rpc;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.DataAuthRef;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import com.dili.ss.retrofitful.annotation.VOField;

import io.swagger.annotations.ApiOperation;

@Restful("http://uap.diligrp.com/")
public interface DataAuthRpc {

	@POST("/dataAuthApi/addUserDataAuth.api")
	BaseOutput<Object> addUserDataAuth(@VOField("userId") Long userId, @VOField("value") String value, @VOField("refCode") String refCode);

	@POST("/dataAuthApi/deleteUserDataAuth.api")
	BaseOutput<Object> deleteUserDataAuth(@VOField("userId") Long userId, @VOField("value") String value, @VOField("refCode") String refCode);

	@POST("/dataAuthApi/listDataAuths.api") 
	BaseOutput<List<DataAuthRef>> listDataAuths(@VOBody DataAuthRef dataAuthRef);
        
}
