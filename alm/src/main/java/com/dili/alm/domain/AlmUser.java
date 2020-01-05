package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-01-18 15:59:47.
 */
@Table(name = "`user`")
public class AlmUser extends BaseDomain {
	/**
	 * 主键
	 */
	@Id
	@Column(name = "`id`")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 用户名
	 */
	@Column(name = "`user_name`")
	private String userName;

	/**
	 * 密码
	 */
	@Column(name = "`password`")
	private String password;

	/**
	 * 最后登录ip
	 */
	@Column(name = "`last_login_ip`")
	private String lastLoginIp;

	/**
	 * 最后登录时间
	 */
	@Column(name = "`last_login_time`")
	private Date lastLoginTime;

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
	 * 状态##状态##{data:[{value:1,text:"启用"},{value:0,text:"停用"}]}
	 */
	@Column(name = "`status`")
	private Integer status;

	/**
	 * 有效性##数据有效性，用于逻辑删除##{data:[{value:0, text:"无效"},{value:1, text:"有效"}]}
	 */
	@Column(name = "`yn`")
	private Integer yn = 1;

	/**
	 * 真实姓名
	 */
	@Column(name = "`real_name`")
	private String realName;

	/**
	 * 用户编号
	 */
	@Column(name = "`serial_number`")
	private String serialNumber;

	/**
	 * 固定电话
	 */
	@Column(name = "`fixed_line_telephone`")
	private String fixedLineTelephone;

	/**
	 * 手机号码
	 */
	@Column(name = "`cellphone`")
	private String cellphone;

	/**
	 * 邮箱
	 */
	@Column(name = "`email`")
	private String email;

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

	@Column(name = "`department_id`")
	private Long departmentId;

	/**
	 * 获取主键
	 *
	 * @return id - 主键
	 */
	@FieldDef(label = "主键")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getId() {
		return id;
	}

	/**
	 * 设置主键
	 *
	 * @param id
	 *            主键
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 获取用户名
	 *
	 * @return user_name - 用户名
	 */
	@FieldDef(label = "用户名", maxLength = 50)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getUserName() {
		return userName;
	}

	/**
	 * 设置用户名
	 *
	 * @param userName
	 *            用户名
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 获取密码
	 *
	 * @return password - 密码
	 */
	@FieldDef(label = "密码", maxLength = 128)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getPassword() {
		return password;
	}

	/**
	 * 设置密码
	 *
	 * @param password
	 *            密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 获取最后登录ip
	 *
	 * @return last_login_ip - 最后登录ip
	 */
	@FieldDef(label = "最后登录ip", maxLength = 20)
	@EditMode(editor = FieldEditor.Text, required = false)
	public String getLastLoginIp() {
		return lastLoginIp;
	}

	/**
	 * 设置最后登录ip
	 *
	 * @param lastLoginIp
	 *            最后登录ip
	 */
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	/**
	 * 获取最后登录时间
	 *
	 * @return last_login_time - 最后登录时间
	 */
	@FieldDef(label = "最后登录时间")
	@EditMode(editor = FieldEditor.Datetime, required = true)
	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	/**
	 * 设置最后登录时间
	 *
	 * @param lastLoginTime
	 *            最后登录时间
	 */
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	/**
	 * 获取创建时间
	 *
	 * @return created - 创建时间
	 */
	@FieldDef(label = "创建时间")
	@EditMode(editor = FieldEditor.Datetime, required = true)
	public Date getCreated() {
		return created;
	}

	/**
	 * 设置创建时间
	 *
	 * @param created
	 *            创建时间
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * 获取修改时间
	 *
	 * @return modified - 修改时间
	 */
	@FieldDef(label = "修改时间")
	@EditMode(editor = FieldEditor.Datetime, required = true)
	public Date getModified() {
		return modified;
	}

	/**
	 * 设置修改时间
	 *
	 * @param modified
	 *            修改时间
	 */
	public void setModified(Date modified) {
		this.modified = modified;
	}

	/**
	 * 获取状态##状态##{data:[{value:1,text:"启用"},{value:0,text:"停用"}]}
	 *
	 * @return status - 状态##状态##{data:[{value:1,text:"启用"},{value:0,text:"停用"}]}
	 */
	@FieldDef(label = "状态")
	@EditMode(editor = FieldEditor.Combo, required = true, params = "{\"data\":[{\"text\":\"启用\",\"value\":1},{\"text\":\"停用\",\"value\":0}]}")
	public Integer getStatus() {
		return status;
	}

	/**
	 * 设置状态##状态##{data:[{value:1,text:"启用"},{value:0,text:"停用"}]}
	 *
	 * @param status
	 *            状态##状态##{data:[{value:1,text:"启用"},{value:0,text:"停用"}]}
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * 获取有效性##数据有效性，用于逻辑删除##{data:[{value:0, text:"无效"},{value:1, text:"有效"}]}
	 *
	 * @return yn - 有效性##数据有效性，用于逻辑删除##{data:[{value:0, text:"无效"},{value:1,
	 *         text:"有效"}]}
	 */
	@FieldDef(label = "有效性")
	@EditMode(editor = FieldEditor.Combo, required = true, params = "{\"data\":[{\"text\":\"无效\",\"value\":0},{\"text\":\"有效\",\"value\":1}]}")
	public Integer getYn() {
		return yn;
	}

	/**
	 * 设置有效性##数据有效性，用于逻辑删除##{data:[{value:0, text:"无效"},{value:1, text:"有效"}]}
	 *
	 * @param yn
	 *            有效性##数据有效性，用于逻辑删除##{data:[{value:0, text:"无效"},{value:1,
	 *            text:"有效"}]}
	 */
	public void setYn(Integer yn) {
		this.yn = yn;
	}

	/**
	 * 获取真实姓名
	 *
	 * @return real_name - 真实姓名
	 */
	@FieldDef(label = "真实姓名", maxLength = 64)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getRealName() {
		return realName;
	}

	/**
	 * 设置真实姓名
	 *
	 * @param realName
	 *            真实姓名
	 */
	public void setRealName(String realName) {
		this.realName = realName;
	}

	/**
	 * 获取用户编号
	 *
	 * @return serial_number - 用户编号
	 */
	@FieldDef(label = "用户编号", maxLength = 128)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * 设置用户编号
	 *
	 * @param serialNumber
	 *            用户编号
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * 获取固定电话
	 *
	 * @return fixed_line_telephone - 固定电话
	 */
	@FieldDef(label = "固定电话", maxLength = 24)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getFixedLineTelephone() {
		return fixedLineTelephone;
	}

	/**
	 * 设置固定电话
	 *
	 * @param fixedLineTelephone
	 *            固定电话
	 */
	public void setFixedLineTelephone(String fixedLineTelephone) {
		this.fixedLineTelephone = fixedLineTelephone;
	}

	/**
	 * 获取手机号码
	 *
	 * @return cellphone - 手机号码
	 */
	@FieldDef(label = "手机号码", maxLength = 24)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getCellphone() {
		return cellphone;
	}

	/**
	 * 设置手机号码
	 *
	 * @param cellphone
	 *            手机号码
	 */
	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	/**
	 * 获取邮箱
	 *
	 * @return email - 邮箱
	 */
	@FieldDef(label = "邮箱", maxLength = 64)
	@EditMode(editor = FieldEditor.Text, required = true)
	public String getEmail() {
		return email;
	}

	/**
	 * 设置邮箱
	 *
	 * @param email
	 *            邮箱
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 获取有效时间开始点
	 *
	 * @return valid_time_begin - 有效时间开始点
	 */
	@FieldDef(label = "有效时间开始点")
	@EditMode(editor = FieldEditor.Datetime, required = true)
	public Date getValidTimeBegin() {
		return validTimeBegin;
	}

	/**
	 * 设置有效时间开始点
	 *
	 * @param validTimeBegin
	 *            有效时间开始点
	 */
	public void setValidTimeBegin(Date validTimeBegin) {
		this.validTimeBegin = validTimeBegin;
	}

	/**
	 * 获取有效时间结束点
	 *
	 * @return valid_time_end - 有效时间结束点
	 */
	@FieldDef(label = "有效时间结束点")
	@EditMode(editor = FieldEditor.Datetime, required = false)
	public Date getValidTimeEnd() {
		return validTimeEnd;
	}

	/**
	 * 设置有效时间结束点
	 *
	 * @param validTimeEnd
	 *            有效时间结束点
	 */
	public void setValidTimeEnd(Date validTimeEnd) {
		this.validTimeEnd = validTimeEnd;
	}

	/**
	 * @return department_id
	 */
	@FieldDef(label = "departmentId")
	@EditMode(editor = FieldEditor.Number, required = true)
	public Long getDepartmentId() {
		return departmentId;
	}

	/**
	 * @param departmentId
	 */
	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}
}