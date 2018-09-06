package com.dili.alm.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.ProjectActionRecordMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.ActionDateType;
import com.dili.alm.domain.FileType;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectAction;
import com.dili.alm.domain.ProjectActionRecord;
import com.dili.alm.domain.ProjectActionType;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.ProjectVersionFormDto;
import com.dili.alm.domain.ProjectVersionState;
import com.dili.alm.domain.Task;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ProjectVersionException;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.alm.service.TeamService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.quartz.service.ScheduleJobService;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import tk.mybatis.mapper.entity.Example;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-20 11:02:17.
 */
@Transactional(rollbackFor = ApplicationException.class)
@Service
public class ProjectVersionServiceImpl extends BaseServiceImpl<ProjectVersion, Long> implements ProjectVersionService {

	@Autowired
	FilesService filesService;

	@Autowired
	ScheduleJobService scheduleJobService;
	@Autowired
	private FilesMapper filesMapper;
	@Autowired
	private ProjectPhaseMapper phaseMapper;
	@Autowired
	private TaskMapper taskMapper;
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private ProjectActionRecordMapper parMapper;
	@Autowired
	private TeamService teamService;

	public ProjectVersionMapper getActualDao() {
		return (ProjectVersionMapper) getDao();
	}

	@Transactional
	@Override
	public void addProjectVersion(ProjectVersionFormDto dto) throws ProjectVersionException {
		// 检查项目状态是否为进行中
		Project project = this.projectMapper.selectByPrimaryKey(dto.getProjectId());
		if (!project.getProjectState().equals(ProjectState.NOT_START.getValue())
				&& !project.getProjectState().equals(ProjectState.IN_PROGRESS.getValue())) {
			throw new ProjectVersionException("项目不在进行中，不能编辑");
		}
		ProjectVersion query = DTOUtils.newDTO(ProjectVersion.class);
		query.setProjectId(dto.getProjectId());
		query.setVersion(dto.getVersion());
		int count = this.getActualDao().selectCount(query);
		if (count > 0) {
			throw new ProjectVersionException("版本已存在");
		}
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		dto.setCreatorId(userTicket.getId());
		dto.setCreated(new Date());
		int rows = this.getActualDao().insertSelective(dto);
		if (rows <= 0) {
			throw new ProjectVersionException("新增项目版本失败");
		}
		if (CollectionUtils.isNotEmpty(dto.getFileIds())) {
			dto.getFileIds().forEach(id -> {
				Files file = this.filesService.get(id);
				file.setProjectId(dto.getProjectId());
				file.setVersionId(dto.getId());
				file.setType(FileType.PROJECT_INTRODUCE.getValue());
				this.filesService.updateSelective(file);
			});
		}
		// 插入项目进程记录版本计划
		ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.VERSION_PLAN.getCode());
		par.setActionDateType(ActionDateType.PERIOD.getValue());
		par.setActionEndDate(dto.getPlannedEndDate());
		par.setActionStartDate(dto.getPlannedStartDate());
		par.setActionType(ProjectActionType.VERSION.getValue());
		par.setVersionId(dto.getId());
		par.setActionCode(ProjectAction.VERSION_PLAN.getCode());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ProjectVersionException("插入项目进程记录失败");
		}

	}

	@Transactional
	@Override
	public void updateProjectVersion(ProjectVersionFormDto dto) throws ProjectVersionException {
		// 检查项目状态是否为进行中
		Project project = this.projectMapper.selectByPrimaryKey(dto.getProjectId());
		if (!project.getProjectState().equals(ProjectState.NOT_START.getValue())
				&& !project.getProjectState().equals(ProjectState.IN_PROGRESS.getValue())) {
			throw new ProjectVersionException("项目不在进行中，不能编辑");
		}
		ProjectVersion query = DTOUtils.newDTO(ProjectVersion.class);
		query.setProjectId(dto.getProjectId());
		query.setVersion(dto.getVersion());
		ProjectVersion version = this.getActualDao().selectOne(query);
		if (version != null && !version.getId().equals(dto.getId())) {
			throw new ProjectVersionException("版本已存在");
		}
		version = this.getActualDao().selectByPrimaryKey(dto.getId());
		// 检查状态是否是未开始
		if (!version.getVersionState().equals(ProjectVersionState.NOT_START.getValue())) {
			throw new ProjectVersionException("当前状态不能编辑！");
		}
		version = this.getActualDao().selectByPrimaryKey(dto.getId());
		version.setVersion(dto.getVersion());
		version.setPlannedStartDate(dto.getPlannedStartDate());
		version.setPlannedEndDate(dto.getPlannedEndDate());
		version.setGit(dto.getGit());
		version.setVisitUrl(dto.getVisitUrl());
		version.setPlannedOnlineDate(dto.getPlannedOnlineDate());
		version.setHost(dto.getHost());
		version.setPort(dto.getPort());
		version.setRedmineUrl(dto.getRedmineUrl());
		version.setNotes(dto.getNotes());
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		version.setModifierId(userTicket.getId());
		version.setModified(new Date());
		int rows = this.getActualDao().updateByPrimaryKey(version);
		if (rows <= 0) {
			throw new ProjectVersionException("修改版本失败");
		}
		if (CollectionUtils.isNotEmpty(dto.getFileIds())) {
			dto.getFileIds().forEach(id -> {
				Files file = this.filesService.get(id);
				file.setProjectId(dto.getProjectId());
				file.setVersionId(dto.getId());
				file.setType(FileType.PROJECT_INTRODUCE.getValue());
				this.filesService.updateSelective(file);
			});
		}
		// 更新项目进程记录版本计划
		ProjectActionRecord parQuery = DTOUtils.newDTO(ProjectActionRecord.class);
		parQuery.setVersionId(version.getId());
		parQuery.setActionCode(ProjectAction.VERSION_PLAN.getCode());
		ProjectActionRecord par = this.parMapper.selectOne(parQuery);
		if (par == null) {
			throw new ProjectVersionException("项目进程记录不存在");
		}
		par.setActionStartDate(version.getPlannedStartDate());
		par.setActionEndDate(version.getPlannedEndDate());
		rows = this.parMapper.updateByPrimaryKey(par);
		if (rows <= 0) {
			throw new ProjectVersionException("修改项目进程记录失败");
		}
	}

	@Transactional(rollbackFor = ApplicationException.class)
	@Override
	public void deleteProjectVersion(Long id) throws ProjectVersionException {
		// 检查项目状态是否为进行中
		ProjectVersion version = this.getActualDao().selectByPrimaryKey(id);
		Project project = this.projectMapper.selectByPrimaryKey(version.getProjectId());
		if (!project.getProjectState().equals(ProjectState.NOT_START.getValue())
				&& !project.getProjectState().equals(ProjectState.IN_PROGRESS.getValue())) {
			throw new ProjectVersionException("项目不在进行中，不能删除");
		}
		ProjectPhase phaseQuery = DTOUtils.newDTO(ProjectPhase.class);
		phaseQuery.setVersionId(id);
		int count = this.phaseMapper.selectCount(phaseQuery);
		if (count > 0) {
			throw new ProjectVersionException("该版本包含阶段信息不能删除");
		}
		Task taskQuery = DTOUtils.newDTO(Task.class);
		taskQuery.setVersionId(id);
		count = this.taskMapper.selectCount(taskQuery);
		if (count > 0) {
			throw new ProjectVersionException("该版本下包含任务不能删除");
		}
		Files files = DTOUtils.newDTO(Files.class);
		files.setVersionId(id);
		List<Files> filesList = filesService.list(files);
		filesList.forEach(f -> {
			this.filesMapper.delete(f);
		});
		ProjectVersion projectVersion = this.getActualDao().selectByPrimaryKey(id);
		if (projectVersion == null) {
			throw new ProjectVersionException("版本不存在");
		}
		int rows = this.getActualDao().deleteByPrimaryKey(id);
		if (rows <= 0) {
			throw new ProjectVersionException("删除版本失败");
		}
		// 删除项目进程记录
		ProjectActionRecord parQuery = DTOUtils.newDTO(ProjectActionRecord.class);
		parQuery.setVersionId(id);
		this.parMapper.delete(parQuery);
	}

	@Override
	public List<Files> listFiles(Long versionId) {
		Example example = new Example(Files.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("versionId", versionId).andIsNull("phaseId");
		return this.filesMapper.selectByExample(example);
	}

	@Override
	public void pause(Long id, Long operatorId) throws ProjectVersionException {
		// 判断版本是否存在
		ProjectVersion version = this.getActualDao().selectByPrimaryKey(id);
		if (version == null) {
			throw new ProjectVersionException("版本不存在");
		}

		// 判断状态
		if (!version.getVersionState().equals(ProjectVersionState.IN_PROGRESS.getValue())) {
			throw new ProjectVersionException("当前状态不能执行暂停操作");
		}

		// 检查权限，只有项目经理可以变更状态
		if (!this.teamService.teamMemberIsProjectManager(operatorId, version.getProjectId())) {
			throw new ProjectVersionException("只有项目经理才能变更版本状态");
		}

		version.setVersionState(ProjectVersionState.PAUSED.getValue());
		int rows = this.getActualDao().updateByPrimaryKeySelective(version);
		if (rows <= 0) {
			throw new ProjectVersionException("更新版本状态失败");
		}

		// 插入项目进程记录
		ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.VERSION_PAUSE.getCode());
		par.setActionDate(new Date());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.VERSION.getValue());
		par.setVersionId(version.getId());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ProjectVersionException("插入项目进程记录失败");
		}
	}

	@Override
	public void resume(Long id, Long operatorId) throws ProjectVersionException {
		// 判断版本是否存在
		ProjectVersion version = this.getActualDao().selectByPrimaryKey(id);
		if (version == null) {
			throw new ProjectVersionException("版本不存在");
		}

		// 判断状态
		if (!version.getVersionState().equals(ProjectVersionState.PAUSED.getValue())) {
			throw new ProjectVersionException("当前状态不能执行重启操作");
		}

		// 检查权限，只有项目经理可以变更状态
		if (!this.teamService.teamMemberIsProjectManager(operatorId, version.getProjectId())) {
			throw new ProjectVersionException("只有项目经理才能变更版本状态");
		}

		version.setVersionState(ProjectVersionState.IN_PROGRESS.getValue());
		int rows = this.getActualDao().updateByPrimaryKeySelective(version);
		if (rows <= 0) {
			throw new ProjectVersionException("更新版本状态失败");
		}
		// 插入项目进程记录
		ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.VERSION_RESUME.getCode());
		par.setActionDate(new Date());
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.VERSION.getValue());
		par.setVersionId(version.getId());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ProjectVersionException("插入项目进程记录失败");
		}
	}

	@Override
	public void complete(Long id, Long operatorId) throws ProjectVersionException {
		// 判断版本是否存在
		ProjectVersion version = this.getActualDao().selectByPrimaryKey(id);
		if (version == null) {
			throw new ProjectVersionException("版本不存在");
		}

		// 判断状态
		if (version.getVersionState().equals(ProjectVersionState.NOT_START.getValue())) {
			throw new ProjectVersionException("当前状态不能执行完成操作");
		}

		// 检查权限，只有项目经理可以变更状态
		if (!this.teamService.teamMemberIsProjectManager(operatorId, version.getProjectId())) {
			throw new ProjectVersionException("只有项目经理才能变更版本状态");
		}
		Date now = new Date();

		version.setVersionState(ProjectVersionState.COMPLETED.getValue());
		version.setActualEndDate(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(version);
		if (rows <= 0) {
			throw new ProjectVersionException("更新版本状态失败");
		}

		// 插入项目进程记录
		ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.VERSION_COMPLETE.getCode());
		par.setActionDate(now);
		par.setActionDateType(ActionDateType.POINT.getValue());
		par.setActionType(ProjectActionType.VERSION.getValue());
		par.setVersionId(version.getId());
		rows = this.parMapper.insertSelective(par);
		if (rows <= 0) {
			throw new ProjectVersionException("插入项目进程记录失败");
		}
	}

	@Override
	public void start(Long id) throws ProjectVersionException {
		ProjectVersion version = this.getActualDao().selectByPrimaryKey(id);
		if (version == null) {
			throw new ProjectVersionException("版本不存在");
		}
		if (version.getVersionState().equals(ProjectVersionState.IN_PROGRESS.getValue())) {
			return;
		}
		if (version.getVersionState().equals(ProjectVersionState.NOT_START.getValue())) {
			version.setVersionState(ProjectVersionState.IN_PROGRESS.getValue());
			Date now = new Date();
			version.setActualStartDate(now);
			int rows = this.getActualDao().updateByPrimaryKeySelective(version);
			if (rows <= 0) {
				throw new ProjectVersionException("更新版本状态失败");
			}

			// 插入项目版本进程-版本开始
			ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
			par.setActionCode(ProjectAction.VERSION_START.getCode());
			par.setActionDateType(ActionDateType.POINT.getValue());
			par.setActionDate(now);
			par.setActionType(ProjectActionType.VERSION.getValue());
			par.setVersionId(id);
			rows = this.parMapper.insertSelective(par);
			if (rows <= 0) {
				throw new ProjectVersionException("插入项目版本进程记录失败");
			}
			return;
		}
		throw new ProjectVersionException("当前状态不能开始");
	}

}