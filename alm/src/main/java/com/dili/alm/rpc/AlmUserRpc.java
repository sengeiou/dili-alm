package com.dili.alm.rpc;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;

import com.dili.alm.domain.AlmUser;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import com.dili.uap.sdk.domain.User;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
@Restful("http://alm.diligrp.com")
public interface  AlmUserRpc {
	
	@POST("/userApi/list")
	BaseOutput<List<AlmUser>> list(@VOBody AlmUser user);
	
	@POST("/userApi/listByExample")
	PageOutput<List<User>> listByExample(User user) ;
	
	@POST("/userApi/findByUserId")
	BaseOutput<AlmUser> findByUserId( Long userId);
}

