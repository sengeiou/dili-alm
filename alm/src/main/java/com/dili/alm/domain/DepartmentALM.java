package com.dili.alm.domain;

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
public class DepartmentALM  {
   
    private Long id;

    private String name;

    private String code;
   
    private Long modifiedId;
   
    private Long createdId;
    
    private String description;
   
    private Long parentId;

    private Date created;

    
    private Date modified;


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

  

}