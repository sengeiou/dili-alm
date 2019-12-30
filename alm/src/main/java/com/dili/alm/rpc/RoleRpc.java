package com.dili.alm.rpc;

import java.util.List;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import com.dili.uap.sdk.domain.Role;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Restful("http://uap.diligrp.com")
public interface RoleRpc {

	@POST("/roleApi/listByUser.api")
	BaseOutput<List<Role>> listByUser(@VOBody Long userId,@VOBody String userNames);
	
}
