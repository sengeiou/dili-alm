package com.dili.sysadmin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.dao.DataAuthMapper;
import com.dili.sysadmin.dao.RoleDataAuthMapper;
import com.dili.sysadmin.dao.UserDataAuthMapper;
import com.dili.sysadmin.domain.DataAuth;
import com.dili.sysadmin.domain.RoleDataAuth;
import com.dili.sysadmin.domain.UserDataAuth;
import com.dili.sysadmin.domain.dto.DataAuthEditDto;
import com.dili.sysadmin.domain.dto.DataAuthTypeDto;
import com.dili.sysadmin.domain.dto.DataDictionaryDto;
import com.dili.sysadmin.domain.dto.DataDictionaryValueDto;
import com.dili.sysadmin.domain.dto.EditRoleDataAuthDto;
import com.dili.sysadmin.domain.dto.EditUserDataAuthDto;
import com.dili.sysadmin.domain.dto.UpdateRoleDataAuthDto;
import com.dili.sysadmin.domain.dto.UpdateUserDataAuthDto;
import com.dili.sysadmin.rpc.DataDictrionaryRPC;
import com.dili.sysadmin.sdk.session.SessionConstants;
import com.dili.sysadmin.sdk.util.ManageRedisUtil;
import com.dili.sysadmin.service.DataAuthService;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:51.
 */
@Service
public class DataAuthServiceImpl extends BaseServiceImpl<DataAuth, Long> implements DataAuthService {

	private final static Logger log = LoggerFactory.getLogger(DataAuthServiceImpl.class);

	public static final String DATA_AUTH_CODE = "data_auth_type";

	@Autowired
	private ManageRedisUtil redisUtil;
	@Autowired
	private DataAuthMapper dataAuthMapper;
	@Autowired
	private UserDataAuthMapper userDataAuthMapper;
	@Autowired
	private DataDictrionaryRPC dataDictrionaryRPC;
	@Autowired
	private RoleDataAuthMapper roleDataAuthMapper;

	public DataAuthMapper getActualDao() {
		return (DataAuthMapper) getDao();
	}

	@Override
	public int update(DataAuth dataAuth) {
		DataAuth up = this.getActualDao().selectByPrimaryKey(dataAuth.getId());
		if (up == null) {
			return 0;
		}
		BeanCopier copier = BeanCopier.create(DataAuth.class, DataAuth.class, false);
		copier.copy(up, dataAuth, null);
		return this.getActualDao().updateByPrimaryKey(up);
	}

	/**
	 * 获取用户的数据权限
	 * 
	 * @return
	 */
	public List<DataAuth> fetchUserDataAuth(Long userId) {
		return this.getActualDao().findByUserId(userId);
	}

	private String makeCurrentKey(Long UserId, String type) {
		return SessionConstants.USER_CURRENT_KEY + UserId + ":" + type;
	}

	/**
	 * 变更用户的当前数据权限
	 * 
	 * @param userId
	 * @param type
	 * @param dataId
	 */
	public void changeCurrentDataAuth(Long userId, String type, String dataId) {
		Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("type", type);
		map.put("dataId", dataId);
		List<DataAuth> list = this.getActualDao().findByUserIdAndTypeAndDataId(map);
		setCurrentDataAuth(makeCurrentKey(userId, type), list.size() > 0 ? list.get(0) : null);
	}

	public DataAuth fetchCurrentDataAuth(Long userId, String type) {
		DataAuth da = fetchCurrentDateAuth(makeCurrentKey(userId, type));
		if (da != null) {
			return da;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("type", type);
		List<DataAuth> das = this.getActualDao().findByUserIdAndTypeAndDataId(map);
		if (das.size() <= 0) {
			return null;
		}
		return das.get(0);
	}

	private DataAuth fetchCurrentDateAuth(String key) {
		String val = redisUtil.get(key, String.class);
		if (val == null) {
			return null;
		}
		return JSON.parseObject(val, DataAuth.class);
	}

	@SuppressWarnings("unchecked")
	private DataAuth setCurrentDataAuth(String key, DataAuth dataAuth) {
		redisUtil.getRedisTemplate().boundSetOps(key).add(JSON.toJSONString(dataAuth));
		return dataAuth;
	}

	@Override
	public EditUserDataAuthDto queryEditUserDataAuth(Long userId, String type) {
		DataAuth record = new DataAuth();
		record.setType(type);
		List<DataAuth> allDataAuths = this.dataAuthMapper.select(record);
		Map<String, Object> map = new HashMap<>();
		map.put("type", type);
		map.put("userId", userId);
		List<DataAuth> userDataAuths = this.dataAuthMapper.findByUserIdAndTypeAndDataId(map);
		EditUserDataAuthDto dto = new EditUserDataAuthDto();
		dto.setUserId(userId);
		dto.setAllDataAuths(this.parseDataAuthEditDto(allDataAuths));
		dto.setUserDataAuths(this.parseDataAuthEditDto(userDataAuths));
		return dto;
	}

	private List<DataAuthEditDto> parseDataAuthEditDto(List<DataAuth> dataAuths) {
		if (CollectionUtils.isEmpty(dataAuths)) {
			return null;
		}
		List<DataAuthEditDto> target = new ArrayList<>(dataAuths.size());
		for (DataAuth dataAuth : dataAuths) {
			DataAuthEditDto dto = new DataAuthEditDto();
			dto.setId(dataAuth.getId());
			dto.setDataId(Long.valueOf(dataAuth.getDataId()));
			dto.setName(dataAuth.getName());
			dto.setParentId(dataAuth.getParentDataId());
			target.add(dto);
		}
		return target;
	}

	@Transactional
	@Override
	public BaseOutput<Object> updateUserDataAuth(UpdateUserDataAuthDto dto) {
		Map<String, Object> map = new HashMap<>();
		map.put("userId", dto.getUserId());
		map.put("type", dto.getType());
		List<DataAuth> userDataAuths = this.dataAuthMapper.findByUserIdAndTypeAndDataId(map);
		UserDataAuth userDataRecord = new UserDataAuth();
		if (CollectionUtils.isNotEmpty(userDataAuths)) {
			userDataRecord.setUserId(dto.getUserId());
			for (DataAuth dataAuth : userDataAuths) {
				userDataRecord.setDataAuthId(dataAuth.getId());
				this.userDataAuthMapper.delete(userDataRecord);
			}
		}
		if (CollectionUtils.isNotEmpty(dto.getDataAuthIds())) {
			for (Long dataAuthId : dto.getDataAuthIds()) {
				userDataRecord.setId(null);
				userDataRecord.setDataAuthId(dataAuthId);
				this.userDataAuthMapper.insert(userDataRecord);
			}
		}
		return BaseOutput.success("保存成功");
	}

	@Override
	public List<DataAuthTypeDto> fetchDataAuthType() {
		BaseOutput<DataDictionaryDto> output = this.dataDictrionaryRPC.findDataDictionaryByCode(DATA_AUTH_CODE);
		log.info("收到返回:getit:" + output);
		if (!output.getCode().equals(ResultCode.OK)) {
			log.error(output.getResult());
			return null;
		}
		DataDictionaryDto dataDictionary = output.getData();
		if (dataDictionary == null || CollectionUtils.isEmpty(dataDictionary.getValues())) {
			return null;
		}
		List<DataAuthTypeDto> target = new ArrayList<>(dataDictionary.getValues().size());
		for (DataDictionaryValueDto value : dataDictionary.getValues()) {
			DataAuthTypeDto dto = new DataAuthTypeDto();
			dto.setName(value.getCode());
			dto.setType(value.getValue());
			target.add(dto);
		}
		log.info("获取到target:" + target);
		return target;
	}

	@Override
	public EditRoleDataAuthDto queryEditRoleDataAuth(Long roleId, String type) {
		DataAuth record = new DataAuth();
		record.setType(type);
		List<DataAuth> allDataAuths = this.dataAuthMapper.select(record);
		Map<String, Object> map = new HashMap<>();
		map.put("type", type);
		map.put("roleId", roleId);
		List<DataAuth> roleDataAuths = this.dataAuthMapper.findByRoleIdAndType(map);
		EditRoleDataAuthDto dto = new EditRoleDataAuthDto();
		dto.setRoleId(roleId);
		dto.setAllDataAuths(this.parseDataAuthEditDto(allDataAuths));
		dto.setRoleDataAuths(this.parseDataAuthEditDto(roleDataAuths));
		return dto;
	}

	@Override
	public BaseOutput<Object> updateRoleDataAuth(UpdateRoleDataAuthDto dto) {
		int result = 0;
		Map<String, Object> map = new HashMap<>();
		map.put("roleId", dto.getRoleId());
		map.put("type", dto.getType());
		List<DataAuth> roleDataAuths = this.dataAuthMapper.findByRoleIdAndType(map);
		RoleDataAuth roleDataRecord = new RoleDataAuth();
		roleDataRecord.setRoleId(dto.getRoleId());
		for (DataAuth dataAuth : roleDataAuths) {
			roleDataRecord.setDataAuthId(dataAuth.getId());
			result += this.roleDataAuthMapper.delete(roleDataRecord);
		}
		for (Long dataAuthId : dto.getDataAuthIds()) {
			roleDataRecord.setId(null);
			roleDataRecord.setDataAuthId(dataAuthId);
			result += this.roleDataAuthMapper.insertSelective(roleDataRecord);
		}
		return BaseOutput.success("保存成功");
	}

}