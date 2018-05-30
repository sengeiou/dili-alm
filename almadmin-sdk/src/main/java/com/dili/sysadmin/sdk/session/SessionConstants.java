package com.dili.sysadmin.sdk.session;

public class SessionConstants {

	public static final String SESSION_KEY_PREFIX = "DILI_MANAGE_SESSION_";
	public static final String CACHED_RESOURCE_LIST_KEY = "DILI_MANAGE_RESOURCE_LIST";
	// COMMON begin
	public static final String LOGGED_USER = "common:loggedUser";
	public static final String RESOURCE = "common:resource";
	public static final String SESSION_ID = "SessionId";
	public static final String AUTH_KEY = "authKey";

	public static final String MANAGE_PERMISSION_CONTEXT = "manage.permission_context";

	public static Long SESSION_TIMEOUT = 60 * 60L; // 30分钟
	public static Long SESSIONID_USERID_TIMEOUT = 60 * 60 * 24L;
	public static Integer COOKIE_TIMEOUT = SESSION_TIMEOUT.intValue() * 48;
	public static Integer NEVER_EXPIRE = -1;

	// 新的redis关系 - kv定义表 - START
	// userRole(用户角色) redis的key
	public static final String USER_ROLES_KEY = "manage:userRole:userId:";
	public static final String USER_DATAAUTH_KEY = "manage:userDataAuth:userId:";
	public static final String USER_CURRENT_KEY = "manage:current:userId:";
	// roleUrl(角色和菜单URL关系)
	public static final String ROLE_URL_KEY = "manage:roleUrl:roleId:";
	// userUrl(用户和菜单URL关系)
	public static final String USER_MENU_URL_KEY = "manage:userMenuUrl:userId:";
	public static final String USER_RESOURCE_CODE_KEY = "manage:userResourceCode:userId";
	// res(单个资源)
	public static final String RES_GRP_KEY = "manage:resGrpId:";
	// sessionId - userId
	public static final String SESSION_USERID_KEY = "manage:sessionUserId:sessionId";
	// userId - sessionId
	public static final String USERID_SESSION_KEY = "manage:userIdSession:userId:";
	// roleUser(角色用户)
	public static final String ROLE_USER_KEY = "manage:roleUser:roleId:";
	public static final String RES_ROLE_KEY = "manage:resRole:resId:";
	// 数据权限
	public static final String DATA_AUTH_KEY = "manage:dataAuth:";

	public static final String URL_RESGRP_KEY = "manage:urlResGrp:urlstr:";
	// 新的redis关系 - kv定义表 - END

	// 限制用户唯一登陆 - START
	public static final String KICK_OLDSESSIONID_KEY = "manage:kickOldSessionId:";
	// 限制用户唯一登陆 - END
}