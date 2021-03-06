package com.dili.sysadmin.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.dao.DepartmentMapper;
import com.dili.sysadmin.dao.RoleMapper;
import com.dili.sysadmin.dao.UserDataAuthMapper;
import com.dili.sysadmin.dao.UserMapper;
import com.dili.sysadmin.dao.UserRoleMapper;
import com.dili.sysadmin.domain.Department;
import com.dili.sysadmin.domain.Role;
import com.dili.sysadmin.domain.User;
import com.dili.sysadmin.domain.UserDataAuth;
import com.dili.sysadmin.domain.UserRole;
import com.dili.sysadmin.domain.UserStatus;
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
import com.dili.sysadmin.manager.DataAuthManager;
import com.dili.sysadmin.manager.MenuManager;
import com.dili.sysadmin.manager.ResourceManager;
import com.dili.sysadmin.manager.SessionRedisManager;
import com.dili.sysadmin.manager.UserManager;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.ManageConfig;
import com.dili.sysadmin.sdk.session.SessionConstants;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.dili.sysadmin.sdk.util.ManageRedisUtil;
import com.dili.sysadmin.service.UserService;
import com.dili.sysadmin.service.ValidatePwdService;
import com.dili.sysadmin.utils.MD5Util;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * ???MyBatis Generator?????????????????? This file was generated on 2017-07-04 15:24:50.
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

	private final static Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	@Resource
	private MD5Util md5Utils;
	@Autowired
	private ManageRedisUtil redisUtil;
	@Value("${pwd.error.check:false}")
	private Boolean pwdErrorCheck;
	@Value("${pwd.error.range:600000}")
	private Long pwdErrorRange;
	@Value("${pwd.error.count:3}")
	private Integer pwdErrorCount;
	@Autowired
	private MenuManager menuManager;
	@Autowired
	private ResourceManager resourceManager;
	@Autowired
	private DataAuthManager dataAuthManager;
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private UserRoleMapper userRoleMapper;
	@Autowired
	private SessionRedisManager sessionManager;
	@Autowired
	private ManageConfig manageConfig;
	@Autowired
	private ValidatePwdService validatePwdService;
	@Autowired
	private UserManager userManager;
	@Autowired
	private DepartmentMapper departmentMapper;
	@Autowired
	private UserDataAuthMapper userDataAuthMapper;
	@Autowired
	private RoleMapper roleMapper;

	public UserMapper getActualDao() {
		return (UserMapper) getDao();
	}

	@Override
	public UserLoginResultDto doLogin(UserLoginDto dto) {
		User record = new User();
		record.setUserName(dto.getUsername());
		User user = this.userMapper.selectOne(record);
		if (user == null || !StringUtils.equalsIgnoreCase(user.getPassword(), this.encryptPwd(dto.getPassword()))) {
			clockUser(user);
			return UserLoginResultDto.failure("????????????????????????");
		}
		if (user.getStatus().equals(UserStatus.DISABLE.getValue())) {
			return UserLoginResultDto.failure("??????????????????, ??????????????????!");
		}
		// ????????????url
		this.menuManager.initUserMenuUrlsInRedis(user.getId());
		// ????????????resource
		this.resourceManager.initUserResourceCodeInRedis(user.getId());
		// ????????????????????????
		this.dataAuthManager.initUserDataAuthsInRedis(user.getId());

		user.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
		user.setLastLoginIp(dto.getRemoteIP());
		if (this.userMapper.updateByPrimaryKey(user) <= 0) {
			LOG.error("????????????????????????????????????");
			return UserLoginResultDto.failure("??????????????????, ??????????????????!");
		}
		LOG.info(String.format("??????????????????????????????[%s] | ??????IP[%s]", dto.getUsername(), dto.getRemoteIP()));
		// ???????????? ?????? ???????????????
		jamUser(user);
		String sessionId = UUID.randomUUID().toString();
		makeRedisTag(user, sessionId);
		return UserLoginResultDto.success("????????????", user, sessionId);
	}

	@Override
	public void disableUser(Long userId) throws UserException {
		User user = this.userMapper.selectByPrimaryKey(userId);
		if (user == null) {
			throw new UserException("???????????????");
		}
		user.setStatus(UserStatus.DISABLE.getValue());
		this.userMapper.updateByPrimaryKey(user);
		this.userManager.clearUserSession(userId);
	}

	@Transactional
	@Override
	public BaseOutput<Object> deleteAfterCheck(Long userId) {
		if (userId.equals(1L)) {
			return BaseOutput.failure("???????????????????????????");
		}
		UserDataAuth record = new UserDataAuth();
		record.setUserId(userId);
		int count = this.userDataAuthMapper.selectCount(record);
		if (count > 0) {
			return BaseOutput.failure("???????????????????????????????????????");
		}
		User user = this.userMapper.selectByPrimaryKey(userId);
		if (user == null) {
			return BaseOutput.failure("???????????????");
		}
		count = this.userMapper.deleteByPrimaryKey(userId);
		if (count <= 0) {
			return BaseOutput.failure("????????????");
		}
		UserRole userRole = new UserRole();
		userRole.setUserId(userId);
		this.userRoleMapper.delete(userRole);
		this.userManager.clearUserSession(userId);
		return BaseOutput.success();
	}

	@Transactional(rollbackFor = UserException.class)
	@Override
	public UserDepartmentDto update(UpdateUserDto dto) throws UserException {
		if (dto.getId().equals(1L)) {
			throw new UserException("???????????????????????????");
		}
		User user = this.userMapper.selectByPrimaryKey(dto.getId());
		if (user == null) {
			throw new UserException("???????????????");
		}
		String password = user.getPassword();
		BeanCopier copier = BeanCopier.create(UpdateUserDto.class, User.class, true);
		copier.copy(dto, user, (v, c, m) -> {
			if (m.equals("setPassword") && null != v) {
				return this.md5Utils.getMD5ofStr(v.toString()).substring(6, 24);
			}
			return v;
		});

		// ?????????????????????????????????????????????????????????????????????
		if (null == user.getPassword()) {
			user.setPassword(password);
		}

		int result = this.userMapper.updateByPrimaryKey(user);
		if (result <= 0) {
			throw new UserException("????????????????????????");
		}

		// ????????????
		UserRole userRole = new UserRole();
		userRole.setUserId(user.getId());
		this.userRoleMapper.delete(userRole);
		for (Long roleId : dto.getRoleId()) {
			userRole.setId(null);
			userRole.setRoleId(roleId);
			this.userRoleMapper.insertSelective(userRole);
		}
		this.userManager.clearUserSession(dto.getId());
		user = this.userMapper.selectByPrimaryKey(dto.getId());
		if (user == null) {
			throw new UserException("???????????????");
		}
		UserDepartmentDto userDto = new UserDepartmentDto(user);
		List<Department> departments = this.departmentMapper.findByUserId(user.getId());
		userDto.setDepartment(departments.get(0));
		return userDto;
	}

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = UserException.class)
	@Override
	public BaseOutput<Object> add(AddUserDto dto) {
		User record = new User();
		record.setUserName(dto.getUserName());
		int count = this.userMapper.selectCount(record);
		if (count > 0) {
			return BaseOutput.failure("??????????????????");
		}
		record = new User();
		record.setSerialNumber(dto.getSerialNumber());
		count = this.getActualDao().selectCount(record);
		if (count > 0) {
			return BaseOutput.failure("?????????????????????");
		}
		User user = new User();
		BeanCopier copier = BeanCopier.create(AddUserDto.class, User.class, true);
		copier.copy(dto, user, (v, c, m) -> {
			if (m.equals("setPassword")) {
				return md5Utils.getMD5ofStr(v.toString()).substring(6, 24);
			}
			return v;
		});
		int result = this.userMapper.insertSelective(user);
		if (result <= 0) {
			return BaseOutput.failure("??????????????????");
		}
		if (CollectionUtils.isNotEmpty(dto.getRoleId())) {
			UserRole userRole = new UserRole();
			userRole.setUserId(user.getId());
			for (Long roleId : dto.getRoleId()) {
				userRole.setId(null);
				userRole.setRoleId(roleId);
				this.userRoleMapper.insertSelective(userRole);
			}
		}

		user = this.userMapper.selectByPrimaryKey(user.getId());
		if (user == null) {
			return BaseOutput.failure("???????????????");
		}
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = UserService.getMetadata();

		try {
			@SuppressWarnings("unchecked")
			List<UserDepartmentDto> results = this.parseToUserDepartmentDto(Arrays.asList(user));
			List users = ValueProviderUtils.buildDataByProvider(metadata, results);
			return BaseOutput.success().setData(users.get(0));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * ???????????? ?????? ???????????????
	 * 
	 * @param user
	 */
	private void jamUser(User user) {
		if (this.manageConfig.getUserLimitOne()
				&& this.sessionManager.existUserIdSessionDataKey(user.getId().toString())) {
			String oldSessionId = this.userManager.clearUserSession(user.getId());
			// ????????????
			this.sessionManager.addKickSessionKey(oldSessionId);
		}
	}

	private void makeRedisTag(User user, String sessionId) {
		Map<String, Object> sessionData = new HashMap<>();
		sessionData.put(SessionConstants.LOGGED_USER, JSON.toJSONString(user));

		LOG.debug("--- Save Session Data To Redis ---");
		this.redisUtil.set(SessionConstants.SESSION_KEY_PREFIX + sessionId, JSON.toJSONString(sessionData),
				SessionConstants.SESSION_TIMEOUT);
		// redis: sessionId - userID
		this.sessionManager.setSessionUserIdKey(sessionId, user.getId().toString());
		// redis: userID - sessionId
		this.sessionManager.setUserIdSessionDataKey(user, sessionId);
		LOG.debug("UserName: " + user.getUserName() + " | SessionId:" + sessionId + " | SessionData:" + sessionData);
	}

	private String encryptPwd(String passwd) {
		return md5Utils.getMD5ofStr(passwd).substring(6, 24);
	}

	private void clockUser(User user) {
		if (!pwdErrorCheck) {
			return;
		}
		if (user == null) {
			return;
		}
		String key = "manage:user_pwd_error:" + user.getId();
		BoundListOperations<Object, Object> ops = redisUtil.getRedisTemplate().boundListOps(key);
		while (true) {
			String s = ops.index(-1).toString();
			if (s == null) {
				break;
			}
			Long t = Long.valueOf(s);
			if (t == 0) {
				break;
			}
			Long nt = System.currentTimeMillis() - t;
			if (nt < pwdErrorRange) {
				break;
			}
			ops.rightPop();
		}
		ops.leftPush(String.valueOf(System.currentTimeMillis()));
		if (ops.size() < pwdErrorCount) {
			return;
		}

		if (user.getStatus() != 0) {
			switchStatus(user.getId());
		}
	}

	private void switchStatus(Long id) {
		User user = this.userMapper.selectByPrimaryKey(id);
		Integer status = user.getStatus();
		status = status == null ? 1 : (status + 1) % 2;
		user.setStatus(status);
		this.userMapper.updateByPrimaryKey(user);
	}

	@Override
	public void logout(String sessionId) {
		this.userManager.clearSession(sessionId);
	}

	@Override
	public List<User> findUserByRole(Long roleId) {
		return this.userMapper.findUserByRole(roleId);
	}

	@Override
	public void enableUser(Long userId) throws UserException {
		User user = this.userMapper.selectByPrimaryKey(userId);
		if (user == null) {
			throw new UserException("???????????????");
		}
		user.setStatus(UserStatus.ENABLE.getValue());
		int result = this.userMapper.updateByPrimaryKey(user);
		if (result <= 0) {
			throw new UserException("????????????????????????");
		}
	}

	@Override
	public BaseOutput<Object> updateUserPassword(UpdateUserPasswordDto dto) {
		try {
			validatePwdService.validatePwd(dto.getNewPassword());
		} catch (IllegalArgumentException e) {
			return BaseOutput.failure(e.getMessage());
		}
		SessionContext sc = SessionContext.getSessionContext();
		UserTicket ut = sc.getUserTicket();
		User user = this.userMapper.selectByPrimaryKey(ut.getId());
		String rawpwd = md5Utils.getMD5ofStr(dto.getOldPassword()).substring(6, 24);
		if (user == null || !StringUtils.equalsIgnoreCase(user.getPassword(), rawpwd)) {
			LOG.info(String.format("??????????????????????????????[%s]", user.getUserName()));
			return BaseOutput.failure("??????????????????");
		}
		user.setPassword(md5Utils.getMD5ofStr(dto.getNewPassword()).substring(6, 24));
		this.userMapper.updateByPrimaryKey(user);
		return BaseOutput.success();
	}

	@Override
	public UserTicket fetchLoginUserInfo(Long userId) {
		String str = this.sessionManager.getUserIdSessionDataKey(userId.toString());
		if (StringUtils.isBlank(str)) {
			return null;
		}
		return JSON.parseObject(JSON.parseObject(str).get("user").toString(), UserTicket.class);
	}

	@Async
	@Override
	public void refreshUserPermission(Long userId) {
		User user = this.userMapper.selectByPrimaryKey(userId);
		if (user == null) {
			return;
		}
		// ????????????url
		this.menuManager.initUserMenuUrlsInRedis(user.getId());
		// ????????????resource
		this.resourceManager.initUserResourceCodeInRedis(user.getId());
		// ????????????????????????
		this.dataAuthManager.initUserDataAuthsInRedis(user.getId());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map> listOnlineUsers(User user) throws Exception {
		List<User> userList = new ArrayList<>();
		Long userId = user.getId();
		if (userId != null) {
			String jsonRst = redisUtil.get(SessionConstants.USERID_SESSION_KEY + userId, String.class);
			if (jsonRst != null) {
				Map<String, String> r = JSON.parseObject(jsonRst, new TypeReference<HashMap<String, String>>() {
				});
				userList.add(JSON.parseObject(r.get("user"), User.class));
			}
		} else {
			Set<String> set = redisUtil.getRedisTemplate().keys(SessionConstants.USERID_SESSION_KEY + "*");
			List<String> queryList = new ArrayList<>();
			queryList.addAll(set);
			List<String> rstList = redisUtil.getRedisTemplate().opsForValue().multiGet(queryList);
			for (String json : rstList) {
				Map<String, String> r = JSON.parseObject(json, new TypeReference<HashMap<String, String>>() {
				});
				User userIter = JSON.parseObject(r.get("user"), User.class);
				String userName = user.getUserName();
				if (StringUtils.isNoneBlank(userName) && !userName.equals(userIter.getUserName()))
					continue;
				String realName = user.getRealName();
				if (StringUtils.isNoneBlank(realName) && !realName.equals(userIter.getRealName()))
					continue;
				userList.add(userIter);
			}
		}
		return ValueProviderUtils.buildDataByProvider(user, userList);

	}

	@Override
	public void kickUserOffline(Long userId) {
		this.userManager.clearUserSession(userId);
	}

	@Override
	public EasyuiPageOutput listPageUserDto(UserDepartmentQuery user) {
		if (user.getDepartmentId() != null) {
			List<Department> depts = this.departmentMapper.getChildDepartments(user.getDepartmentId());
			if (CollectionUtils.isNotEmpty(depts)) {
				depts.forEach(d -> {
					user.getDepartmentIds().add(d.getId());
				});
			}
		}
		Integer page = user.getPage();
		page = (page == null) ? Integer.valueOf(1) : page;
		if (user.getRows() != null && user.getRows() >= 1) {
			// ??????????????????,???????????????????????????????????????
			PageHelper.startPage(page, user.getRows());
		}
		List<UserDepartmentRole> list = this.getActualDao().selectByCondition(user);
		Page<UserDepartmentRole> resultPage = (Page<UserDepartmentRole>) list;
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = UserService.getMetadata();

		user.setMetadata(metadata);
		try {
			List users = ValueProviderUtils.buildDataByProvider(user, list);
			return new EasyuiPageOutput(Integer.valueOf(Integer.parseInt(String.valueOf(resultPage.getTotal()))),
					users);
		} catch (Exception e) {
			return null;
		}
	}

	private List<UserDepartmentDto> parseToUserDepartmentDto(List<User> results) {
		List<UserDepartmentDto> target = new ArrayList<>(results.size());
		Iterator<User> it = results.iterator();
		while (it.hasNext()) {
			User user = it.next();
			UserDepartmentDto dto = new UserDepartmentDto(user);
			Department dept = this.departmentMapper.selectByPrimaryKey(user.getDepartmentId());
			dto.setDepartment(dept);
			List<Role> roles = this.roleMapper.findByUserId(user.getId());
			dto.setDepartment(dept);
			dto.setRoles(roles);
			target.add(dto);
		}
		return target;
	}

	@Override
	public List<UserDepartmentRole> findUserContainDepartmentAndRole(UserDepartmentRoleQuery query) {
		if (query.getDepartmentId() != null && query.getDepartmentId() > 0) {
			List<Department> depts = this.departmentMapper.getChildDepartments(query.getDepartmentId());
			if (CollectionUtils.isNotEmpty(depts)) {
				List<Long> ids = new ArrayList<>(depts.size());
				depts.forEach(d -> ids.add(d.getId()));
				query.setDepartmentIds(ids);
			}
		}
		return this.getActualDao().findUserContainDepartmentAndRole(query);
	}

}