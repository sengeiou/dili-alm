package com.dili.alm.service;

public interface DataMigrateService {

	
	int updateData(Long  userId,Long uapUserId);
	
	int getDataIsExistence (Long  userId,Long uapUserId);
	
	int updateUserIdStrData();
}
