package com.dili.alm.rpc;

import java.util.List;

import com.dili.alm.domain.Role;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.UserDepartmentRole;
import com.dili.alm.domain.dto.UserDepartmentRoleQuery;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Restful("http://alm.diligrp.com")
public interface UserRpc {
	@POST("/userApi/list")
	BaseOutput<List<User>> list(@VOBody(required = false) User user);

	@POST("/userApi/listByExample")
	BaseOutput<List<User>> listByExample(@VOBody(required = false) User user);

	@POST("/userApi/listUserByRole")
	BaseOutput<List<User>> listUserByRole(@VOBody Long id);

	@POST("/userApi/listUserRole")
	BaseOutput<List<Role>> findUserRoles(@VOBody Long memberId);

	@POST("/userApi/findByUserId")
	BaseOutput<User> findUserById(@VOBody Long memberId);

	@POST("/userApi/findUserContainDepartmentAndRole")
	BaseOutput<List<UserDepartmentRole>> findUserContainDepartmentAndRole(@VOBody(required = false) UserDepartmentRoleQuery dto);
}
