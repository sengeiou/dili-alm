package com.dili.sysadmin.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.domain.annotation.Like;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

import javax.persistence.*;
import java.util.Date;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-09-07 12:02:50.
 */
@Table(name = "`department`")
public class Department extends BaseDomain {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 部门名
     */
    @Like(Like.BOTH)
    @Column(name = "`name`")
    private String name;

    /**
     * 编号
     */
    @Column(name = "`code`")
    private String code;

    
    @Column(name = "`modified_id`")
    private Long modifiedId;
    
    
    @Column(name = "`created_id`")
    private Long createdId;
    /**
     * 备注
     */
    @Column(name = "`description`")
    private String description;

    @Column(name = "`parent_id`")
    private Long parentId;

    /**
     * 创建时间
     */
    @Column(name = "`created`")
    private Date created;

    /**
     * 操作时间
     */
    @Column(name = "`modified`")
    private Date modified;

    /**
     * @return id
     */
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取部门名
     *
     * @return name - 部门名
     */
    @FieldDef(label="部门名", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getName() {
        return name;
    }

    /**
     * 设置部门名
     *
     * @param name 部门名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取编号
     *
     * @return code - 编号
     */
    @FieldDef(label="编号", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getCode() {
        return code;
    }

    /**
     * 设置编号
     *
     * @param code 编号
     */
    public void setCode(String code) {
        this.code = code;
    }
   
    public Long getModifiedId() {
		return modifiedId;
	}

	public void setModifiedId(Long modifiedId) {
		this.modifiedId = modifiedId;
	}

	public Long getCreatedId() {
		return createdId;
	}

	public void setCreatedId(Long createdId) {
		this.createdId = createdId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
     * @return parent_id
     */
    @FieldDef(label="parentId")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取创建时间
     *
     * @return created - 创建时间
     */
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Date getCreated() {
        return created;
    }

    /**
     * 设置创建时间
     *
     * @param created 创建时间
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * 获取操作时间
     *
     * @return modified - 操作时间
     */
    @FieldDef(label="操作时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Date getModified() {
        return modified;
    }

    /**
     * 设置操作时间
     *
     * @param modified 操作时间
     */
    public void setModified(Date modified) {
        this.modified = modified;
    }
}