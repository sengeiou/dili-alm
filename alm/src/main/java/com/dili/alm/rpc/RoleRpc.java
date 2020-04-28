package com.dili.alm.rpc;

import java.util.Collection;
import java.util.List;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.ReqParam;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import com.dili.uap.sdk.domain.Role;
import com.dili.uap.sdk.domain.dto.RoleUserDto;
import com.dili.uap.sdk.domain.dto.UserRoleIdDto;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Restful("${uap.contextPath}")
public interface RoleRpc {

	@POST("/roleApi/listByUser.api")
	BaseOutput<List<Role>> listByUser(@ReqParam(value = "userId") Long userId, @ReqParam(value = "userName") String userName);

	@POST("/userApi/listUserRoleIdByUserIds.api")
	BaseOutput<List<UserRoleIdDto>> listUserRoleIdByUserIds(@VOBody Collection<Long> userIds);

	@POST("/roleApi/listRoleUserByRoleIds.api")
	BaseOutput<List<RoleUserDto>> listRoleUserByRoleIds(@VOBody Collection<Long> roleIds);

}
