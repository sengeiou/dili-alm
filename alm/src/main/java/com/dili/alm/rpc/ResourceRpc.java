package com.dili.alm.rpc;

import java.util.List;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;

@Restful("${uap.contextPath}")
public interface ResourceRpc {
	@POST("/resourceApi/listResourceCodeByUserId")
	BaseOutput<List<String>> listResourceCodeByUserId(@VOBody Long id);

}
