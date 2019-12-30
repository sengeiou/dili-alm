package com.dili.sysadmin.service.impl;

import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.dao.UserDataAuthMapper;
import com.dili.sysadmin.domain.UserDataAuth;
import com.dili.sysadmin.domain.dto.UserDataAuthDto;
import com.dili.sysadmin.rpc.DataAuthRpc;
import com.dili.sysadmin.service.UserDataAuthService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:51.
 */
@Service
public class UserDataAuthServiceImpl extends BaseServiceImpl<UserDataAuth, Long> implements UserDataAuthService {

//	private final static Logger LOG = LoggerFactory.getLogger(UserDataAuthServiceImpl.class);

	public UserDataAuthMapper getActualDao() {
		return (UserDataAuthMapper) getDao();
	}

	@Autowired
	private DataAuthRpc dadaAuthRpc;
	@Override
	public int delete(UserDataAuth userDataAuth) {
		return getActualDao().delete(userDataAuth);
	}


	@Override
	public List<UserDataAuthDto> listUserDataAuthDto() {
		return this.getActualDao().listUserDataAuthDto();
	}


	@Override
	public void updateUserDataAuthList() {
		List<UserDataAuthDto> listUserDataAuthDto = this.getActualDao().listUserDataAuthDto();
		for (UserDataAuthDto userDataAuthDto : listUserDataAuthDto) {
			BaseOutput<Object> addUserDataAuth = dadaAuthRpc.addUserDataAuth(userDataAuthDto.getUserId(), userDataAuthDto.getDataId().toString(), userDataAuthDto.getType());
			if(addUserDataAuth.getCode()!=ResultCode.OK) {
				throw new RuntimeException("添加失败，value:" + userDataAuthDto.getDataId().toString() + "和refCode:" + userDataAuthDto.getType() + "和userId:" + userDataAuthDto.getUserId()+ ",不能找到唯一的数据权限");
			}
		}
		
	}
}