package com.dili.sysadmin.service;

import java.util.List;

import com.dili.ss.base.BaseService;
import com.dili.sysadmin.domain.UserDataAuth;
import com.dili.sysadmin.domain.dto.UserDataAuthDto;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:51.
 */
public interface UserDataAuthService extends BaseService<UserDataAuth, Long> {

	int delete(UserDataAuth userDataAuth);

	List<UserDataAuthDto> listUserDataAuthDto();

	void updateUserDataAuthList();
}