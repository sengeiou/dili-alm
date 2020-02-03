package com.dili.alm.rpc;

import java.util.List;

import com.dili.alm.domain.dto.UserDepartmentRole;
import com.dili.alm.domain.dto.UserDepartmentRoleQuery;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import com.dili.uap.sdk.domain.User;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Restful("http://uap.diligrp.com")
public interface UserDataAuthRpc {
	
	
	@POST("/userDataAuthApi/listUserDataAuthValue.api")
	BaseOutput<List<String>> listUserDataAuthValue(@VOBody Long userId);
}
