package com.dili.alm.domain.dto;

import com.dili.alm.domain.Demand;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.domain.annotation.Operator;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import java.util.List;

import javax.persistence.*;


public class DemandDto extends Demand {
	@Operator(Operator.IN)
	@Column(name = "`id`")
	List<Long> ids;

	Long departmentId;
	String departmentName;
	String departmentFirstName;
	String userPhone;
	String firmName;
	String userName;
	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getDepartmentFirstName() {
		return departmentFirstName;
	}

	public void setDepartmentFirstName(String departmentFirstName) {
		this.departmentFirstName = departmentFirstName;
	}

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}