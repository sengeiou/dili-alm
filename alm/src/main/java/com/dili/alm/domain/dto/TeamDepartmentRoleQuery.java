package com.dili.alm.domain.dto;

public class TeamDepartmentRoleQuery extends UserDepartmentRoleQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9673638104352793L;

	public Long getMemberId() {
		return this.getUserId();
	}

	public void setMemberId(Long memberId) {
		this.setUserId(memberId);
	}
}
