package com.dili.alm.service;

import java.util.List;

import com.dili.alm.domain.HardwareResource;
import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-03-20 17:22:08.
 */
public interface HardwareResourceService extends BaseService<HardwareResource, Long> {

	List<User> listUserByDepartment();

	List<Project> projectList();

	BaseOutput insertHardwareResourceSelective(
			List<HardwareResource> hardwareResourceList);

	boolean isSubmit(Long id);

	EasyuiPageOutput listEasyuiPageByExample(HardwareResource domain,
			List<Long> projectIds, boolean useProvider) throws Exception;

	boolean isOperation(Long id);

	BaseOutput insertOneSelective(HardwareResource hardwareResource);

	void submit(Long projectId, Long userId);

	List<DataDictionaryValueDto> getEnvironments();

	List<DataDictionaryValueDto> getRegionals();

}