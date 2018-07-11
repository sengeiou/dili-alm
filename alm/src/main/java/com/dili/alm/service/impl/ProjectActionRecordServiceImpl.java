package com.dili.alm.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.dili.alm.domain.ProjectActionRecord;
import com.dili.alm.domain.ProjectActionRecordConfig;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.dto.ProjectActionRecordGanttDto;
import com.dili.alm.domain.dto.ProjectActionRecordGanttValueDto;
import com.dili.alm.provider.ProjectActionProvider;
import com.dili.alm.service.ProjectActionRecordService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.util.DateUtils;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

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
		Example example = new Example(ProjectActionRecord.class);
		Criteria criteria = example.createCriteria().andEqualTo("projectId", projectId).andIn("actionCode",
				actionCodes);
		if (CollectionUtils.isNotEmpty(versions)) {
			List<Long> versionIds = new ArrayList<>(versions.size());
			versions.forEach(v -> versionIds.add(v.getId()));
			criteria.orIn("versionId", versionIds);
		}
		if (CollectionUtils.isNotEmpty(tasks)) {
			List<Long> taskIds = new ArrayList<>(tasks.size());
			tasks.forEach(t -> taskIds.add(t.getId()));
			criteria.orIn("taskId", taskIds);
		}
		example.setOrderByClause("action_date,actual_end_date,actual_start_date asc");
		List<ProjectActionRecord> recordList = this.getActualDao().selectByExample(example);
		List<ProjectActionRecordGanttDto> targetList = new ArrayList<>(recordList.size());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		recordList.forEach(r -> {
			ProjectActionRecordGanttDto dto = new ProjectActionRecordGanttDto();
			dto.setName(this.actionProvider.getDisplayText(r.getActionCode(), null, null));
			ProjectActionRecordGanttValueDto valueDto = new ProjectActionRecordGanttValueDto();
			if (r.getActionDateType().equals(ActionDateType.PERIOD.getValue())) {
				valueDto.setFrom(r.getActionStartDate().getTime());
				valueDto.setTo(r.getActionEndDate().getTime());
				valueDto.setLabel(
						"开始时间:" + df.format(r.getActionStartDate()) + " 结束时间：" + df.format(r.getActionEndDate()));
			} else {
				valueDto.setFrom(r.getActionDate().getTime());
				valueDto.setTo(r.getActionDate().getTime());
				dto.setDesc(dtf.format(r.getActionDate()));
				valueDto.setLabel(dtf.format(r.getActionDate()));
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
}