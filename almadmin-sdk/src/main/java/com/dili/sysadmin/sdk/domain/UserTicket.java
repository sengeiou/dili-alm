package com.dili.sysadmin.sdk.domain;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;

public class UserTicket {
	/**
	 * 用户ID
	 */
	private Long id;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 最后登录ip
	 */
	private String lastLoginIp;
	/**
	 * 最后登录时间
	 */
	private Timestamp lastLoginTime;
	/**
	 * 真实姓名
	 */
	private String realName;
	/**
	 * 部门ID
	 */
	private Long depId;
	/**
	 * 部门名称
	 */
	private String depName;
	/**
	 * 用户编号
	 */
	private String serialNumber;
	/**
	 * 固定电话
	 */
	private String fixedLineTelephone;
	/**
	 * 手机号码
	 */
	private String cellphone;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 创建时间
	 */
	@Column(name = "`created`")
	private Date created;

	/**
	 * 修改时间
	 */
	@Column(name = "`modified`")
	private Date modified;

	/**
	 * 有效时间开始点
	 */
	@Column(name = "`valid_time_begin`")
	private Date validTimeBegin;

	/**
	 * 有效时间结束点
	 */
	@Column(name = "`valid_time_end`")
	private Date validTimeEnd;

	private Long departmentId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLastLoginIp() {
		return lastLoginIp;
	}

	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public Timestamp getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Timestamp lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Long getDepId() {
		return depId;
	}

	public void setDepId(Long depId) {
		this.depId = depId;
	}

	public String getDepName() {
		return depName;
	}

	public void setDepName(String depName) {
		this.depName = depName;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getFixedLineTelephone() {
		return fixedLineTelephone;
	}

	public void setFixedLineTelephone(String fixedLineTelephone) {
		this.fixedLineTelephone = fixedLineTelephone;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Date getValidTimeBegin() {
		return validTimeBegin;
	}

	public void setValidTimeBegin(Date validTimeBegin) {
		this.validTimeBegin = validTimeBegin;
	}

	public Date getValidTimeEnd() {
		return validTimeEnd;
	}

	public void setValidTimeEnd(Date validTimeEnd) {
		this.validTimeEnd = validTimeEnd;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	@Override
	public String toString() {
		return "UserTicket [id=" + id + ", userName=" + userName + ", password=" + password + ", lastLoginIp="
				+ lastLoginIp + ", lastLoginTime=" + lastLoginTime + ", realName=" + realName + ", depId=" + depId
				+ ", depName=" + depName + ", serialNumber=" + serialNumber + ", fixedLineTelephone="
				+ fixedLineTelephone + ", cellphone=" + cellphone + ", email=" + email + ", created=" + created
				+ ", modified=" + modified + ", validTimeBegin=" + validTimeBegin + ", validTimeEnd=" + validTimeEnd
				+ "]";
	}

}
