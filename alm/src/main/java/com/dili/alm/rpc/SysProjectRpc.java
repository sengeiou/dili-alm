package com.dili.alm.rpc;

import com.dili.alm.domain.ProjectSysEntity;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;

import java.util.List;

@Restful("http://uap.diligrp.com")
public interface SysProjectRpc {

	@POST("/systemApi/list.api")
	BaseOutput<List<ProjectSysEntity>> list(@VOBody(required = false) ProjectSysEntity projectSys);

}
