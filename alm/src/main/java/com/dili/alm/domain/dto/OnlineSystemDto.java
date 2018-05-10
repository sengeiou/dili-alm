package com.dili.alm.domain.dto;

import com.dili.alm.domain.OnlineSystem;
import com.dili.ss.domain.BaseDomain;

public class OnlineSystemDto extends BaseDomain implements OnlineSystem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6617602235081740337L;
	private Long applyId;
	private Long sqlFileId;
	private String git;
	private Long startupScriptFileId;
	private String dependencySystem;
	private String otherDescription;

	@Override
	public Long getApplyId() {
		return applyId;
	}

	@Override
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}

	@Override
	public String getGit() {
		return git;
	}

	@Override
	public void setGit(String git) {
		this.git = git;
	}

	@Override
	public Long getSqlFileId() {
		return sqlFileId;
	}

	@Override
	public void setSqlFileId(Long sqlFileId) {
		this.sqlFileId = sqlFileId;
	}

	@Override
	public Long getStartupScriptFileId() {
		return startupScriptFileId;
	}

	@Override
	public void setStartupScriptFileId(Long startupScriptFileId) {
		this.startupScriptFileId = startupScriptFileId;
	}

	@Override
	public String getDependencySystem() {
		return dependencySystem;
	}

	@Override
	public void setDependencySystem(String dependencySystem) {
		this.dependencySystem = dependencySystem;
	}

	@Override
	public String getOtherDescription() {
		return otherDescription;
	}

	@Override
	public void setOtherDescription(String otherDescription) {
		this.otherDescription = otherDescription;
	}

}
