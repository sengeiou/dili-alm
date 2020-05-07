package com.dili.alm.rpc;

import com.dili.alm.domain.SystemDto;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;

import java.util.List;

@Restful("${uap.contextPath}")
public interface SysProjectRpc {

	@POST("/systemApi/list.api")
	BaseOutput<List<SystemDto>> list(@VOBody SystemDto SystemDto);

}
