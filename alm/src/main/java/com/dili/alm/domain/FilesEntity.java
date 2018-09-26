package com.dili.alm.domain;

import java.util.Date;

import com.dili.ss.domain.BaseDomain;

public class FilesEntity extends BaseDomain implements Files {

	/**
	 * 
	 */
	private static final long serialVersionUID = -140649343496233842L;
	private String name;
	private String suffix;
	private Long length;
	private String url;
	private Date created;
	private Date modified;
	private Long createMemberId;
	private Integer type;
	private Long modifyMemberId;
	private Long projectId;
	private Long versionId;
	private Long recordId;
	private String notes;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getSuffix() {
		return suffix;
	}

	@Override
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public Long getLength() {
		return length;
	}

	@Override
	public void setLength(Long length) {
		this.length = length;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public Date getModified() {
		return modified;
	}

	@Override
	public void setModified(Date modified) {
		this.modified = modified;
	}

	@Override
	public Long getCreateMemberId() {
		return createMemberId;
	}

	@Override
	public void setCreateMemberId(Long createMemberId) {
		this.createMemberId = createMemberId;
	}

	@Override
	public Long getModifyMemberId() {
		return modifyMemberId;
	}

	@Override
	public void setModifyMemberId(Long modifyMemberId) {
		this.modifyMemberId = modifyMemberId;
	}

	@Override
	public Integer getType() {
		return type;
	}

	@Override
	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public Long getProjectId() {
		return projectId;
	}

	@Override
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@Override
	public Long getVersionId() {
		return versionId;
	}

	@Override
	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	@Override
	public Long getRecordId() {
		return recordId;
	}

	@Override
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	@Override
	public String getNotes() {
		return notes;
	}

	@Override
	public void setNotes(String notes) {
		this.notes = notes;
	}

}
