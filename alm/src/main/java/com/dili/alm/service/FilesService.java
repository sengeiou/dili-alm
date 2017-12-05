package com.dili.alm.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dili.alm.domain.Files;
import com.dili.ss.base.BaseService;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-20 14:47:50.
 */
public interface FilesService extends BaseService<Files, Long> {

	static final String MILESTONES_PATH_PREFIX = "fileupload";

	/**
	 * 根据files的文件名和路径删除文件
	 * 
	 * @param files
	 * @return
	 */
	int delete(Files files);

	List<Files> uploadFile(MultipartFile[] files, Long projectId);
}