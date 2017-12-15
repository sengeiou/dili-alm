package com.dili.alm.service.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.service.FilesService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-20 14:47:50.
 */
@Service
public class FilesServiceImpl extends BaseServiceImpl<Files, Long> implements FilesService {

	private final Logger LOGGER = LoggerFactory.getLogger(FilesServiceImpl.class);

	public FilesMapper getActualDao() {
		return (FilesMapper) getDao();
	}

	@Override
	public int delete(Long aLong) {
		// 先根据路径+文件名删除文件
		Files files = getActualDao().selectByPrimaryKey(aLong);
		if (files == null)
			return 0;
		File dest = new File(files.getUrl() + files.getName());
		dest.delete();
		return super.delete(aLong);
	}

	@Override
	public int delete(Files files) {
		// 先根据路径+文件名删除文件
		if (files == null)
			return 0;
		File dest = new File(files.getUrl() + files.getName());
		dest.delete();
		return super.delete(files.getId());
	}

	@Override
	public List<Files> uploadFile(MultipartFile[] files, Long projectId) {
		if (files.length <= 0) {
			return null;
		}
		// 指定当前项目的相对路径
		String path = FilesService.MILESTONES_PATH_PREFIX + "/";
		File pathFile = new File(path);
		// 父目录不存在则创建
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		List<Files> returnFiles = new ArrayList<>(files.length);
		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			int size = (int) file.getSize();
			if (StringUtils.isBlank(fileName) || size == 0) {
				continue;
			}
			System.out.println(fileName + "-->" + size);
			File dest = new File(path + fileName);
			BufferedOutputStream buffStream = null;
			try {
				byte[] bytes = file.getBytes();
				buffStream = new BufferedOutputStream(new FileOutputStream(dest));
				buffStream.write(bytes);
				Files record = DTOUtils.newDTO(Files.class);
				record.setName(file.getOriginalFilename());
				Files tmpFiles = this.getActualDao().selectOne(record);
				UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
				if (tmpFiles == null) {
					tmpFiles = DTOUtils.newDTO(Files.class);
					if (userTicket != null) {
						tmpFiles.setCreateMemberId(userTicket.getId());
					}
					tmpFiles.setProjectId(projectId);
					tmpFiles.setName(file.getOriginalFilename());
					tmpFiles.setLength(file.getSize());
					tmpFiles.setSuffix(
							file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
					tmpFiles.setUrl(path);
					tmpFiles.setCreated(new Date());
					this.getActualDao().insertSelective(tmpFiles);
				} else {
					if (userTicket != null) {
						tmpFiles.setModifyMemberId(userTicket.getId());
					}
					tmpFiles.setProjectId(projectId);
					tmpFiles.setName(file.getOriginalFilename());
					tmpFiles.setLength(file.getSize());
					tmpFiles.setSuffix(
							file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
					tmpFiles.setUrl(path);
					tmpFiles.setModified(new Date());
					this.getActualDao().updateByPrimaryKey(tmpFiles);
				}
				returnFiles.add(tmpFiles);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			} finally {
				if (buffStream != null) {
					try {
						buffStream.close();
					} catch (IOException e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		}
		return returnFiles;
	}

}