package com.dili.sysadmin.domain.dto;

import java.io.Serializable;
import java.util.List;

public class UserDepartmentRoleQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6910318745874677983L;

	private Long userId;
	private List<Long> departmentId;
	private Long roleId;
	private String realName;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<Long> getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(List<Long> departmentId) {
		this.departmentId = departmentId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

}
