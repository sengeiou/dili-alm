package com.dili.alm.dao;

import java.util.List;

import com.dili.alm.domain.OnlineDataChange;
import com.dili.ss.base.MyMapper;

public interface OnlineDataChangeMapper extends MyMapper<OnlineDataChange> {
	
	
	List<OnlineDataChange> selectList(OnlineDataChange onlineDataChange);
	 
}