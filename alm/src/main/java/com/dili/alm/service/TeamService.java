package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.Team;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.UserDepartmentRoleQuery;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-24 14:31:10.
 */
public interface TeamService extends BaseService<Team, Long> {

	BaseOutput<Object> insertAfterCheck(Team team);

	BaseOutput<Object> updateAfterCheck(Team team);

	List<DataDictionaryValueDto> getTeamRoles();

	List<Team> listContainUserInfo(UserDepartmentRoleQuery dto);

	BaseOutput<Object> deleteAfterCheck(Long id);
}