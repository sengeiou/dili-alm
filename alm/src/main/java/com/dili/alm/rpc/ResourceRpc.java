package com.dili.alm.rpc;

import java.util.List;

import com.dili.alm.domain.Role;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOSingleParameter;
@Restful("http://alm.diligrp.com")
public interface ResourceRpc {
	@POST("/resourceApi/listResourceCodeByUserId")
	BaseOutput<List<String>> listResourceCodeByUserId(@VOSingleParameter Long id);

}
