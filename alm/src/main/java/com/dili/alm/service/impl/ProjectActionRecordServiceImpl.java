package com.dili.alm.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dili.alm.constant.AlmConstants.ApproveType;
import com.dili.alm.dao.ApproveMapper;
import com.dili.alm.dao.ProjectActionRecordConfigMapper;
import com.dili.alm.dao.ProjectActionRecordMapper;
import com.dili.alm.dao.ProjectApplyMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.ActionDateType;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectAction;
import com.dili.alm.domain.ProjectActionRecord;
import com.dili.alm.domain.ProjectActionRecordConfig;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.dto.ProjectActionRecordExportDto;
import com.dili.alm.domain.dto.ProjectActionRecordGanttDto;
import com.dili.alm.domain.dto.ProjectActionRecordGanttValueDto;
import com.dili.alm.provider.ProjectActionProvider;
import com.dili.alm.provider.ProjectVersionProvider;
import com.dili.alm.provider.TaskProvider;
import com.dili.alm.service.ProjectActionRecordService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.util.DateUtils;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2018-06-21 12:00:46.
 */
@Service
public class ProjectActionRecordServiceImpl extends BaseServiceImpl<ProjectActionRecord, Long>
		implements ProjectActionRecordService {

	@Autowired
	private ProjectVersionMapper versionMapper;
	@Autowired
	private ProjectActionProvider actionProvider;
	@Qualifier("mailContentTemplate")
	@Autowired
	private GroupTemplate groupTemplate;
	@Autowired
	private ProjectActionRecordConfigMapper parcMapper;
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private ProjectApplyMapper projectApplyMapper;
	@Autowired
	private ApproveMapper approveMapper;
	@Autowired
	private TaskMapper taskMapper;
	@Autowired
	private ProjectVersionProvider versionProvider;
	@Autowired
	private TaskProvider taskProvider;

	public ProjectActionRecordMapper getActualDao() {
		return (ProjectActionRecordMapper) getDao();
	}

	@Override
	public List<ProjectActionRecordGanttDto> getGanntData(Long projectId, List<String> actionCodes) {
		if (CollectionUtils.isEmpty(actionCodes)) {
			return new ArrayList<>(0);
		}
		ProjectActionRecord query = DTOUtils.newDTO(ProjectActionRecord.class);
		query.setProjectId(projectId);
		ProjectVersion versionQuery = DTOUtils.newDTO(ProjectVersion.class);
		versionQuery.setProjectId(projectId);
		List<ProjectVersion> versions = this.versionMapper.select(versionQuery);
		Task taskQuery = DTOUtils.newDTO(Task.class);
		taskQuery.setProjectId(projectId);
		List<Task> tasks = this.taskMapper.select(taskQuery);
		List<Long> versionIds = new ArrayList<>(versions.size());
		List<Long> taskIds = new ArrayList<>(tasks.size());
		if (CollectionUtils.isNotEmpty(versions)) {
			versions.forEach(v -> versionIds.add(v.getId()));
		}
		if (CollectionUtils.isNotEmpty(tasks)) {
			tasks.forEach(t -> taskIds.add(t.getId()));
		}
		List<ProjectActionRecord> recordList = this.getActualDao().ganttQuery(projectId, versionIds, taskIds,
				actionCodes);
		List<ProjectActionRecordGanttDto> targetList = new ArrayList<>(recordList.size());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		recordList.forEach(r -> {
			ProjectActionRecordGanttDto dto = new ProjectActionRecordGanttDto();
			setActionName(r, dto);
			ProjectActionRecordGanttValueDto valueDto = new ProjectActionRecordGanttValueDto();
			if (r.getActionDateType().equals(ActionDateType.PERIOD.getValue())) {
				valueDto.setFrom(r.getActionStartDate().getTime());
				valueDto.setTo(r.getActionEndDate().getTime());
				dto.setDesc("");
				valueDto.setLabel(
						"开始时间:" + df.format(r.getActionStartDate()) + " 结束时间：" + df.format(r.getActionEndDate()));
			} else {
				valueDto.setFrom(r.getActionDate().getTime());
				valueDto.setTo(r.getActionDate().getTime());
				dto.setDesc(df.format(r.getActionDate()));
			}
			// 获取列表展示配置
			ProjectActionRecordConfig parcQuery = DTOUtils.newDTO(ProjectActionRecordConfig.class);
			parcQuery.setActionCode(r.getActionCode());
			ProjectActionRecordConfig conf = this.parcMapper.selectOne(parcQuery);
			if (conf != null) {
				valueDto.setCustomClass(conf.getColor());
				if (conf.getHint() && StringUtils.isNotBlank(conf.getHintMessage())) {
					Template tmp = this.groupTemplate.getTemplate(conf.getHintMessage());
					loadModel(r, tmp);
					tmp.binding("data", r);
					valueDto.setDesc(tmp.render());
				}
			}
			valueDto.setDataObj(r);
			dto.setValues(Arrays.asList(valueDto));
			targetList.add(dto);
		});
		return targetList;
	}

	private void loadModel(ProjectActionRecord record, Template template) {
		if (record.getVersionId() != null) {
			loadVersion(record, template);
		}
		if (record.getProjectId() != null) {
			loadProject(record, template);
		}
	}

	private void loadProject(ProjectActionRecord record, Template template) {
		// 读取项目
		Project project = this.projectMapper.selectByPrimaryKey(record.getProjectId());
		template.binding("project1", project);
		// 读取立项申请
		ProjectApply apply = this.projectApplyMapper.selectByPrimaryKey(project.getApplyId());
		template.binding("apply", apply);
		// 读取审批记录
		Approve approveQuery = DTOUtils.newDTO(Approve.class);
		approveQuery.setType(ApproveType.APPLY.getCode());
		approveQuery.setProjectApplyId(apply.getId());
		Approve approve = this.approveMapper.selectByPrimaryKey(approveQuery);
		template.binding("approve", approve);
	}

	private void loadVersion(ProjectActionRecord record, Template template) {
		ProjectVersion version = this.versionMapper.selectByPrimaryKey(record.getVersionId());
		template.binding("version", version);
	}

	@Override
	public List<ProjectActionRecordExportDto> getExportData(Long projectId, List<String> actionCodes) {
		if (CollectionUtils.isEmpty(actionCodes)) {
			return new ArrayList<>(0);
		}
		ProjectActionRecord query = DTOUtils.newDTO(ProjectActionRecord.class);
		query.setProjectId(projectId);
		ProjectVersion versionQuery = DTOUtils.newDTO(ProjectVersion.class);
		versionQuery.setProjectId(projectId);
		List<ProjectVersion> versions = this.versionMapper.select(versionQuery);
		Task taskQuery = DTOUtils.newDTO(Task.class);
		taskQuery.setProjectId(projectId);
		List<Task> tasks = this.taskMapper.select(taskQuery);
		List<Long> versionIds = new ArrayList<>(versions.size());
		if (CollectionUtils.isNotEmpty(versions)) {
			versions.forEach(v -> versionIds.add(v.getId()));
		}
		List<Long> taskIds = new ArrayList<>(tasks.size());
		if (CollectionUtils.isNotEmpty(tasks)) {
			tasks.forEach(t -> taskIds.add(t.getId()));
		}
		List<ProjectActionRecord> recordList = this.getActualDao().ganttQuery(projectId, versionIds, taskIds,
				actionCodes);
		List<ProjectActionRecordExportDto> targetList = new ArrayList<>(recordList.size());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		recordList.forEach(r -> {
			ProjectActionRecordExportDto dto = new ProjectActionRecordExportDto();
			this.setActionName(r, dto);
			dto.setStartDate(r.getActionStartDate() != null ? df.format(r.getActionStartDate()) : null);
			dto.setEndDate(r.getActionEndDate() != null ? df.format(r.getActionEndDate()) : null);
			dto.setActualStartDate(r.getActualStartDate() != null ? df.format(r.getActualStartDate()) : null);
			dto.setActualEndDate(r.getActualEndDate() != null ? df.format(r.getActualEndDate()) : null);
			dto.setActionDate(r.getActionDate() != null ? dtf.format(r.getActionDate()) : null);
			if (r.getActionCode().equals(ProjectAction.PROJECT_COMPLETE.getCode())) {
				Project project = this.projectMapper.selectByPrimaryKey(r.getProjectId());
				Date startDate = project.getStartDate();
				Date endDate = project.getActualEndDate();
				dto.setOverDays(DateUtils.differentDays(startDate, endDate) + "");
			}
			if (r.getActionCode().equals(ProjectAction.VERSION_COMPLETE.getCode())) {
				ProjectVersion version = this.versionMapper.selectByPrimaryKey(r.getVersionId());
				Date startDate = version.getPlannedStartDate();
				Date endDate = version.getActualEndDate();
				dto.setOverDays("逾期" + DateUtils.differentDays(startDate, endDate) + "天");
			}
			targetList.add(dto);
		});
		return targetList;
	}

	private void setActionName(ProjectActionRecord record, ProjectActionRecordExportDto dto) {
		if (record.getActionCode().equals(ProjectAction.VERSION_COMPLETE.getCode())) {
			dto.setActionName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "完成");
		} else if (record.getActionCode().equals(ProjectAction.VERSION_ONLINE.getCode())) {
			dto.setActionName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "上线");
		} else if (record.getActionCode().equals(ProjectAction.VERSION_ONLINE_APPLY.getCode())) {
			dto.setActionName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "上线申请");
		} else if (record.getActionCode().equals(ProjectAction.VERSION_PAUSE.getCode())) {
			dto.setActionName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "暂停");
		} else if (record.getActionCode().equals(ProjectAction.VERSION_PLAN.getCode())) {
			dto.setActionName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "规划");
		} else if (record.getActionCode().equals(ProjectAction.VERSION_RESUME.getCode())) {
			dto.setActionName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "暂停");
		} else if (record.getActionCode().equals(ProjectAction.VERSION_START.getCode())) {
			dto.setActionName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "开始");
		} else if (record.getActionCode().equals(ProjectAction.TASK_PLAN.getCode())) {
			dto.setActionName("任务" + this.taskProvider.getDisplayText(record.getTaskId(), null, null) + "规划");
		} else {
			dto.setActionName(this.actionProvider.getDisplayText(record.getActionCode(), null, null));
		}
	}

	private void setActionName(ProjectActionRecord record, ProjectActionRecordGanttDto dto) {
		if (record.getActionCode().equals(ProjectAction.VERSION_COMPLETE.getCode())) {
			dto.setName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "完成");
		} else if (record.getActionCode().equals(ProjectAction.VERSION_ONLINE.getCode())) {
			dto.setName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "上线");
		} else if (record.getActionCode().equals(ProjectAction.VERSION_ONLINE_APPLY.getCode())) {
			dto.setName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "上线申请");
		} else if (record.getActionCode().equals(ProjectAction.VERSION_PAUSE.getCode())) {
			dto.setName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "暂停");
		} else if (record.getActionCode().equals(ProjectAction.VERSION_PLAN.getCode())) {
			dto.setName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "规划");
		} else if (record.getActionCode().equals(ProjectAction.VERSION_RESUME.getCode())) {
			dto.setName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "重启");
		} else if (record.getActionCode().equals(ProjectAction.VERSION_START.getCode())) {
			dto.setName("版本" + this.versionProvider.getDisplayText(record.getVersionId(), null, null) + "开始");
		} else if (record.getActionCode().equals(ProjectAction.TASK_PLAN.getCode())) {
			dto.setName("任务" + this.taskProvider.getDisplayText(record.getTaskId(), null, null) + "规划");
		} else {
			dto.setName(this.actionProvider.getDisplayText(record.getActionCode(), null, null));
		}
	}
}