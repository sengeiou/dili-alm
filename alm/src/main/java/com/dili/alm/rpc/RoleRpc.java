package com.dili.alm.rpc;

import java.util.List;

import com.dili.alm.domain.Role;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Restful("http://alm.diligrp.com")
public interface RoleRpc {

	@POST("/roleApi/listRoleByUserId")
	BaseOutput<List<Role>> listRoleByUserId(@VOBody Long id);

	@POST("/roleApi/listRoleNameByUserId")
	BaseOutput<String> listRoleNameByUserId(@VOBody Long id);
}
