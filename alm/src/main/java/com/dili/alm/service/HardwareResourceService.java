package com.dili.alm.service;

import java.util.List;
import java.util.Map;

import com.dili.alm.domain.HardwareResource;
import com.dili.alm.domain.Project;
import com.dili.uap.sdk.domain.User;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.exceptions.HardwareResourceException;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-03-20 17:22:08.
 */
public interface HardwareResourceService extends BaseService<HardwareResource, Long> {

	List<User> listUserByDepartment();

	List<Project> projectList();

	BaseOutput insertHardwareResourceSelective(List<HardwareResource> hardwareResourceList);

	void insertHardwareResource(HardwareResource hardwareResource) throws HardwareResourceException;

	void submit(Long projectId, Long userId);

	List<DataDictionaryValueDto> getEnvironments();

	List<DataDictionaryValueDto> getRegionals();

	Map<String, String> projectNumById(String id);

	void updateHardwareResource(HardwareResource hardwareResource) throws HardwareResourceException;

}