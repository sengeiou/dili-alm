package com.dili.alm.domain.dto;

import org.springframework.web.multipart.MultipartFile;

import com.dili.alm.domain.OnlineDataChange;

public class OnlineDataChangeDto extends OnlineDataChange{
	private  MultipartFile sqlFile;

	public MultipartFile getSqlFile() {
		return sqlFile;
	}

	public void setSqlFile(MultipartFile sqlFile) {
		this.sqlFile = sqlFile;
	}
	

}
