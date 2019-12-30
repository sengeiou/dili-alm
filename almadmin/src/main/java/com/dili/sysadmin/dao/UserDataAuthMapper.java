package com.dili.sysadmin.dao;

import java.util.List;

import com.dili.ss.base.MyMapper;
import com.dili.sysadmin.domain.UserDataAuth;
import com.dili.sysadmin.domain.dto.UserDataAuthDto;

public interface UserDataAuthMapper extends MyMapper<UserDataAuth> {

	List<UserDataAuthDto> listUserDataAuthDto();
}