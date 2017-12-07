package com.dili.alm.domain.dto;

import com.dili.alm.domain.TeamEntity;

public class TeamListViewDto extends TeamEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8809760684650430121L;

	// 权限系统的角色，在表格里面显示为职务
	private String adminRoles;
	private Long departmentId;
	private String departmentName;
	private String contact;
	private String email;

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getAdminRoles() {
		return adminRoles;
	}

	public void setAdminRoles(String adminRoles) {
		this.adminRoles = adminRoles;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
