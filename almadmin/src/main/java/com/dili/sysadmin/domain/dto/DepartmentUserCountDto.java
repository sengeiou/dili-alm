package com.dili.sysadmin.domain.dto;

import java.io.Serializable;
import java.util.Date;

public class DepartmentUserCountDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2900694985273019656L;

	private Long id;

	/**
	 * 部门名
	 */
	private String name;

	/**
	 * 编号
	 */
	private String code;

	/**
	 * 操作员id
	 */
	private Long operatorId;

	/**
	 * 备注
	 */
	private String notes;

	private Long parentId;

	/**
	 * 创建时间
	 */
	private Date created;

	/**
	 * 操作时间
	 */
	private Date modified;

	private Integer userCount = 0;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
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

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

}
