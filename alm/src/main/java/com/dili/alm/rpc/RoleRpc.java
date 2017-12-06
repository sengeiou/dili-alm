package com.dili.alm.rpc;

import com.dili.alm.domain.Role;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOSingleParameter;

import java.util.List;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Restful("http://127.0.0.1:8080/almadmin")
public interface RoleRpc {

	@POST("/roleApi/listRoleByUserId")
	BaseOutput<List<Role>> listRoleByUserId(@VOSingleParameter Long id);
}
