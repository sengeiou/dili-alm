package com.dili.alm.domain.dto;

import java.io.Serializable;

public class UserDepartmentRoleQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6910318745874677983L;

	private Long userId;
	private Long departmentId;
	private Long roleId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

}
