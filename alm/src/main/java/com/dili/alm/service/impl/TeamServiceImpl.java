package com.dili.alm.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.TeamMapper;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.UserDepartmentRole;
import com.dili.alm.domain.dto.UserDepartmentRoleQuery;
import com.dili.alm.rpc.DataAuthRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.TeamService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-24 14:31:10.
 */
@Service
public class TeamServiceImpl extends BaseServiceImpl<Team, Long> implements TeamService {

	private static final String TEAM_ROLE_CODE = "team_role";
	@Autowired
	DataAuthRpc dataAuthRpc;
	@Autowired
	private DataDictionaryService dataDictionaryService;
	@Autowired
	private UserRpc userRPC;

	public TeamMapper getActualDao() {
		return (TeamMapper) getDao();
	}

	@Override
	public int insert(Team team) {
		int i = super.insert(team);
		dataAuthRpc.addUserDataAuth(team.getMemberId(), team.getProjectId().toString(),
				AlmConstants.DATA_AUTH_TYPE_PROJECT);
		return i;
	}

	@Override
	public int delete(Long id) {
		Team team = get(id);
		dataAuthRpc.deleteUserDataAuth(team.getMemberId(), team.getProjectId().toString(),
				AlmConstants.DATA_AUTH_TYPE_PROJECT);
		return super.delete(id);
	}

	@Override
	public int delete(List<Long> ids) {
		ids.forEach(id -> {
			dataAuthRpc.deleteUserDataAuth(id, get(id).getProjectId().toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT);
		});
		return super.delete(ids);
	}

	@Override
	public BaseOutput<Object> deleteAfterCheck(Long id) {
		Team team = this.getActualDao().selectByPrimaryKey(id);
		if (!team.getDeletable()) {
			return BaseOutput.failure("该数据不能删除");
		}
		return this.delete(id) > 0 ? BaseOutput.success() : BaseOutput.failure();
	}

	@Override
	public int insertSelective(Team t) {
		Date now = new Date();
		t.setJoinTime(now);
		dataAuthRpc.addUserDataAuth(t.getMemberId(), t.getProjectId().toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT);
		return super.insertSelective(t);
	}

	@Transactional
	@Override
	public BaseOutput<Object> insertAfterCheck(Team team) {
		Team record = DTOUtils.newDTO(Team.class);
		record.setProjectId(team.getProjectId());
		record.setMemberId(team.getMemberId());
		record.setRole(team.getRole());
		int count = this.getActualDao().selectCount(record);
		if (count > 0) {
			return BaseOutput.failure("项目和团队已存在，不能重复添加");
		}
		int result = this.insertSelective(team);
		if (result > 0) {
			this.parse2ListView(team);
			try {
				return BaseOutput.success().setData(team);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				return BaseOutput.failure();
			}
		}
		return BaseOutput.failure("新增失败");
	}

	@Override
	public BaseOutput<Object> updateAfterCheck(Team team) {
		int result;
		Team oldRecord = this.getActualDao().selectByPrimaryKey(team.getId());
		if (oldRecord.getProjectId().equals(team.getProjectId())
				&& oldRecord.getMemberId().equals(team.getMemberId())) {
			result = this.updateSelective(team);
		} else {
			Team record = DTOUtils.newDTO(Team.class);
			record.setProjectId(team.getProjectId());
			record.setMemberId(team.getMemberId());
			int count = this.getActualDao().selectCount(record);
			if (count > 0) {
				return BaseOutput.failure("项目和团队已存在，不能重复添加");
			}
			result = this.updateSelective(team);
		}
		if (result > 0) {
			this.parse2ListView(team);
			try {
				return BaseOutput.success().setData(this.parseEasyUiModel(team));
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				return BaseOutput.failure();
			}
		}
		return BaseOutput.failure("修改失败");
	}

	@Override
	public List<DataDictionaryValueDto> getTeamRoles() {
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(TEAM_ROLE_CODE);
		if (dto == null) {
			return null;
		}
		return dto.getValues();
	}

	@Override
	public List<Team> listContainUserInfo(UserDepartmentRoleQuery dto) {
		Team team = DTOUtils.newDTO(Team.class);
		team.setMemberId(dto.getUserId());
		List<Team> teams = this.list(team);
		if (CollectionUtils.isEmpty(teams)) {
			return null;
		}
		Iterator<Team> it = teams.iterator();
		while (it.hasNext()) {
			Team t = it.next();
			BaseOutput<List<UserDepartmentRole>> output = this.userRPC.findUserContainDepartmentAndRole(dto);
			if (output == null && !output.isSuccess()) {
				return null;
			}
			List<UserDepartmentRole> udrList = output.getData();
			if (CollectionUtils.isEmpty(udrList)) {
				it.remove();
				continue;
			}
			t.aset("userInfo", udrList.get(0));
		}
		return teams;
	}

	private void parse2ListView(Team team) {
		UserDepartmentRoleQuery dto = new UserDepartmentRoleQuery();
		dto.setUserId(team.getMemberId());
		BaseOutput<List<UserDepartmentRole>> output = this.userRPC.findUserContainDepartmentAndRole(dto);
		if (output == null && !output.isSuccess()) {
			return;
		}
		List<UserDepartmentRole> udrList = output.getData();
		if (CollectionUtils.isEmpty(udrList)) {
			return;
		}
		team.aset("userInfo", udrList.get(0));
	}

	private Map<Object, Object> parseEasyUiModel(Team team) throws Exception {
		List<Map> listMap = this.parseEasyUiModelList(Arrays.asList(team));
		return listMap.get(0);
	}

	private List<Map> parseEasyUiModelList(List<? extends Team> list) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("memberId", memberProvider);

		JSONObject teamTypeProvider = new JSONObject();
		teamTypeProvider.put("provider", "teamTypeProvider");
		metadata.put("type", teamTypeProvider);

		JSONObject memberStateProvider = new JSONObject();
		memberStateProvider.put("provider", "memberStateProvider");
		metadata.put("memberState", memberStateProvider);

		JSONObject teamRoleProvider = new JSONObject();
		teamRoleProvider.put("provider", "teamRoleProvider");
		metadata.put("role", teamRoleProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("joinTime", datetimeProvider);
		metadata.put("leaveTime", datetimeProvider);

		return ValueProviderUtils.buildDataByProvider(metadata, list);
	}

}