package com.dili.sysadmin.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.domain.annotation.Like;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-07-10 17:58:29.
 */
@Table(name = "`role`")
public class Role extends BaseDomain {
	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = 6784663384096954308L;

	/**
	 * 主键
	 */
	@Id
	@Column(name = "`id`")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 角色名
	 */
	@Like(Like.BOTH)
	@Column(name = "`role_name`")
	private String roleName;

	/**
	 * 角色描述
	 */
	@Column(name = "`description`")
	private String description;

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
	 * 获取主键
	 *
	 * @return id - 主键
	 */
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
	 * 获取角色名
	 *
	 * @return role_name - 角色名
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * 设置角色名
	 *
	 * @param roleName
	 *            角色名
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * 获取角色描述
	 *
	 * @return description - 角色描述
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置角色描述
	 *
	 * @param description
	 *            角色描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取创建时间
	 *
	 * @return created - 创建时间
	 */
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}