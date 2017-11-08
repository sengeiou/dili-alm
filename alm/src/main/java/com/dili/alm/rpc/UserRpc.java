package com.dili.alm.rpc;

import com.dili.alm.domain.User;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;

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
}
