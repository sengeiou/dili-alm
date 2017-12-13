package com.dili.alm.domain.dto;

import com.dili.alm.domain.Project;

public interface ProjectListDto extends Project {

	Boolean getManager();

	void setManager(Boolean manager);
}
