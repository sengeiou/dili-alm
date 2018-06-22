package com.dili.alm.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dili.alm.dao.ProjectActionRecordMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.domain.ActionDateType;
import com.dili.alm.domain.ProjectActionRecord;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.dto.ProjectActionRecordGanttDto;
import com.dili.alm.domain.dto.ProjectActionRecordGanttValueDto;
import com.dili.alm.provider.ProjectActionProvider;
import com.dili.alm.service.ProjectActionRecordService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;

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

	public ProjectActionRecordMapper getActualDao() {
		return (ProjectActionRecordMapper) getDao();
	}

	@Override
	public List<ProjectActionRecordGanttDto> getGanntData(Long projectId) {
		ProjectActionRecord query = DTOUtils.newDTO(ProjectActionRecord.class);
		query.setProjectId(projectId);
		ProjectVersion versionQuery = DTOUtils.newDTO(ProjectVersion.class);
		versionQuery.setProjectId(projectId);
		List<ProjectVersion> versions = this.versionMapper.select(versionQuery);
		Example example = new Example(ProjectActionRecord.class);
		Criteria criteria = example.createCriteria().andEqualTo("projectId", projectId);
		if (CollectionUtils.isNotEmpty(versions)) {
			List<Long> versionIds = new ArrayList<>(versions.size());
			versions.forEach(v -> versionIds.add(v.getId()));
			criteria.orIn("versionId", versionIds);
		}
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
				valueDto.setCustomClass(
						"开始时间:" + df.format(r.getActionStartDate()) + " 结束时间：" + df.format(r.getActionEndDate()));
			} else {
				valueDto.setFrom(r.getActionDate().getTime());
				valueDto.setTo(r.getActionDate().getTime());
				dto.setDesc(dtf.format(r.getActionDate()));
				valueDto.setLabel(dtf.format(r.getActionDate()));
				valueDto.setCustomClass(dtf.format(r.getActionDate()));
			}
			valueDto.setDataObj(r);
			dto.setValues(Arrays.asList(valueDto));
			targetList.add(dto);
		});
		return targetList;
	}
}