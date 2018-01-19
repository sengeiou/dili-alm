package com.dili.sysadmin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.sysadmin.domain.User;
import com.dili.sysadmin.domain.dto.AddUserDto;
import com.dili.sysadmin.domain.dto.UpdateUserDto;
import com.dili.sysadmin.domain.dto.UpdateUserPasswordDto;
import com.dili.sysadmin.domain.dto.UserDepartmentDto;
import com.dili.sysadmin.domain.dto.UserDepartmentQuery;
import com.dili.sysadmin.domain.dto.UserDepartmentRole;
import com.dili.sysadmin.domain.dto.UserDepartmentRoleQuery;
import com.dili.sysadmin.domain.dto.UserLoginDto;
import com.dili.sysadmin.domain.dto.UserLoginResultDto;
import com.dili.sysadmin.exception.UserException;
import com.dili.sysadmin.sdk.domain.UserTicket;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:50.
 */
public interface UserService extends BaseService<User, Long> {

	public static Map<Object, Object> getMetadata() {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject departmentProvider = new JSONObject();
		departmentProvider.put("provider", "departmentProvider");
		metadata.put("departmentId", departmentProvider);

		JSONObject userStatusProvider = new JSONObject();
		userStatusProvider.put("provider", "userStatusProvider");
		metadata.put("status", userStatusProvider);
		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("validTimeBegin", datetimeProvider);
		metadata.put("validTimeEnd", datetimeProvider);
		metadata.put("created", datetimeProvider);
		metadata.put("modified", datetimeProvider);
		metadata.put("lastLoginTime", datetimeProvider);
		return metadata;
	}

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

	EasyuiPageOutput listPageUserDto(UserDepartmentQuery query);

	List<UserDepartmentRole> findUserContainDepartmentAndRole(UserDepartmentRoleQuery query);

}