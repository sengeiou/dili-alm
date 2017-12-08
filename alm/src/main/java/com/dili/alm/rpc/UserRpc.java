package com.dili.alm.rpc;

import com.dili.alm.domain.Role;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.UserDepartmentRole;
import com.dili.alm.domain.dto.UserDepartmentRoleQuery;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import com.dili.ss.retrofitful.annotation.VOSingleParameter;

import java.util.List;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Restful("http://almadmin.diligrp.com/almadmin")
public interface UserRpc {
	@POST("/userApi/list")
	BaseOutput<List<User>> list(@VOBody User user);

	@POST("/userApi/listByExample")
	BaseOutput<List<User>> listByExample(@VOBody User user);

	@POST("/userApi/listUserByRole")
	BaseOutput<List<User>> listUserByRole(@VOSingleParameter Long id);

	@POST("/userApi/listUserRole")
	BaseOutput<List<Role>> findUserRoles(@VOSingleParameter Long memberId);

	@POST("/userApi/findByUserId")
	BaseOutput<User> findUserById(@VOSingleParameter Long memberId);

	@POST("/userApi/findUserContainDepartmentAndRole")
	BaseOutput<List<UserDepartmentRole>> findUserContainDepartmentAndRole(UserDepartmentRoleQuery dto);
}
