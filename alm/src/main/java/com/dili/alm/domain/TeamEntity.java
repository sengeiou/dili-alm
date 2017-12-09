package com.dili.alm.domain;

import java.util.Date;

import com.dili.ss.domain.BaseDomain;

public class TeamEntity extends BaseDomain implements Team {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8613523394983265460L;

	private Long projectId;
	private Long memberId;
	private String role;
	private Boolean deletable;
	private Date joinTime;
	private Date leaveTime;

	@Override
	public Long getProjectId() {
		return projectId;
	}

	@Override
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@Override
	public Long getMemberId() {
		return memberId;
	}

	@Override
	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	@Override
	public Date getJoinTime() {
		return joinTime;
	}

	@Override
	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}

	@Override
	public String getRole() {
		return role;
	}

	@Override
	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public Boolean getDeletable() {
		return deletable;
	}

	@Override
	public void setDeletable(Boolean deletable) {
		this.deletable = deletable;
	}

	@Override
	public Date getLeaveTime() {
		return leaveTime;
	}

	@Override
	public void setLeaveTime(Date leaveTime) {
		this.leaveTime = leaveTime;
	}

}
