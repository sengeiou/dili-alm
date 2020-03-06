package com.dili.alm.domain.dto;

import com.dili.uap.sdk.domain.dto.UserDepartmentRoleQuery;

public class TeamDepartmentRoleQuery extends UserDepartmentRoleQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9673638104352793L;

	private Long projectId;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getMemberId() {
		return this.getUserId();
	}

	public void setMemberId(Long memberId) {
		this.setUserId(memberId);
	}
}
