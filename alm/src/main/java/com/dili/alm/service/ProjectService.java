package com.dili.alm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectCostStatisticDto;
import com.dili.alm.domain.dto.UploadProjectFileDto;
import com.dili.alm.domain.dto.UserDataDto;
import com.dili.alm.exceptions.ProjectException;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-18 17:22:54.
 */
public interface ProjectService extends BaseService<Project, Long> {

	List<DataDictionaryValueDto> getPojectTypes();

	BaseOutput<Object> deleteBeforeCheck(Long id);

	/**
	 * 获取子项目，包含当前参数id
	 * 
	 * @param id
	 * @return
	 */
	List<Project> getChildProjects(Long id);

	BaseOutput<Object> insertAfterCheck(Project project) throws ProjectException;

	BaseOutput<Object> updateAfterCheck(Project project);

	List<DataDictionaryValueDto> getProjectStates();

	EasyuiPageOutput listPageMyProject(Project project);

	List<DataDictionaryValueDto> getFileTypes();

	Map<Object, Object> getDetailViewData(Long id);

	BaseOutput<Object> uploadFileAndSendMail(UploadProjectFileDto dto) throws MessagingException, InterruptedException;

	/**
	 * 暂停项目
	 * 
	 * @param id 项目id
	 * @throws ProjectException
	 */
	void pause(Long id) throws ProjectException;

	/**
	 * 重启项目
	 * 
	 * @param id 项目id
	 * @throws ProjectException
	 */
	void resume(Long id) throws ProjectException;

	List<Map<String, String>> projectStateList();

	/**
	 * 项目开始
	 * 
	 * @param projectId
	 * @throws ProjectException
	 */
	void start(Long projectId) throws ProjectException;

	/**
	 * 项目成本统计
	 * 
	 * @param projectId 项目id
	 * @return
	 */
	List<ProjectCostStatisticDto> projectCostStatistic(Long projectId);

	List<UserDataDto> listUserDataAuth();

	List<UserDataDto> listUserDataAuthByIds(List<Long> idsList);

}