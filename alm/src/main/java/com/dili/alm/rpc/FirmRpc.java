package com.dili.alm.rpc;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import com.dili.uap.sdk.domain.Firm;



@Restful("http://uap.diligrp.com")
public interface FirmRpc {

	@POST("/firmApi/getByCode.api")
	BaseOutput<Firm> getByCode(@VOBody String code);

}
