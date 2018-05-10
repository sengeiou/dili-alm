package com.dili.alm.domain.dto;

import com.dili.alm.domain.WorkDayEntity;

public class WorkDayRoleDto extends WorkDayEntity{
	private int isRole;

	public int getIsRole() {
		return isRole;
	}

	public void setIsRole(int isRole) {
		this.isRole = isRole;
	}
	
}
