package com.dili.sysadmin.service;

import java.util.List;
import java.util.Map;

import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.sysadmin.domain.User;
import com.dili.sysadmin.domain.dto.AddUserDto;
import com.dili.sysadmin.domain.dto.UpdateUserDto;
import com.dili.sysadmin.domain.dto.UpdateUserPasswordDto;
import com.dili.sysadmin.domain.dto.UserDepartmentDto;
import com.dili.sysadmin.domain.dto.UserLoginDto;
import com.dili.sysadmin.domain.dto.UserLoginResultDto;
import com.dili.sysadmin.exception.UserException;
import com.dili.sysadmin.sdk.domain.UserTicket;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:50.
 */
public interface UserService extends BaseService<User, Long> {

	UserLoginResultDto doLogin(UserLoginDto command) throws UserException;

	void disableUser(Long userId) throws UserException;

	void enableUser(Long userId) throws UserException;

	BaseOutput<Object> logicDelete(Long userId);

	BaseOutput<Object> add(AddUserDto dto);

	UserDepartmentDto update(UpdateUserDto dto) throws UserException;

	void logout(String sessionId);

	List<User> findUserByRole(Long roleId);

	BaseOutput<Object> updateUserPassword(UpdateUserPasswordDto dto);

	UserTicket fetchLoginUserInfo(Long userId);

	void refreshUserPermission(Long userId);

	List<Map> listOnlineUsers(User user) throws Exception;

	void kickUserOffline(Long userId);

	EasyuiPageOutput listPageUserDto(User user);

}