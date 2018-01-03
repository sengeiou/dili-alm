package com.dili.alm.domain.dto;

import java.util.List;
import java.util.Map;

import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectVersion;

public class ProjectVersionChangeStateViewDto {

	private ProjectVersion version;
	private List<Map> changes;

	public ProjectVersion getVersion() {
		return version;
	}

	public void setVersion(ProjectVersion version) {
		this.version = version;
	}

	public List<Map> getChanges() {
		return changes;
	}

	public void setChanges(List<Map> changes) {
		this.changes = changes;
	}

}
