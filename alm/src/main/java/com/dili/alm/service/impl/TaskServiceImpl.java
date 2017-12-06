package com.dili.alm.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dili.alm.dao.DataDictionaryValueMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskDetailsMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.dto.TaskSelectDto;
import com.dili.alm.service.TaskService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.github.pagehelper.Page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:23:05.
 */
@Service
public class TaskServiceImpl extends BaseServiceImpl<Task, Long> implements TaskService {
	
    public TaskMapper getActualDao() {
        return (TaskMapper)getDao();
    }
    @Autowired
    private ProjectMapper projectMapper;
    
    @Autowired
    private ProjectPhaseMapper ppMapper;
    
    @Autowired
    private ProjectVersionMapper pvMapper;
    
    @Autowired
    private TaskDetailsMapper tdMapper;
    


	@Override
	public List<Project> selectByOwner(Long ownerId) {
		return this.projectMapper.getProjectsByTaskOwner(ownerId);
	}
	
	@Override
	public EasyuiPageOutput listPageSelectTaskDto(Task task) throws Exception {
		List<Task> list = this.listByExample(task);//查询出来
		Page<Task> page = (Page<Task>) list;
		try {
			
			List<TaskSelectDto> results = this.TaskParseTaskSelectDto(list);//转化为查询的DTO
			List taskList = ValueProviderUtils.buildDataByProvider(task, results);
			return new EasyuiPageOutput(Integer.valueOf(Integer.parseInt(String.valueOf(page.getTotal()))), taskList);
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * DTO转化
	 * @param results
	 * @return
	 */
	private List<TaskSelectDto> TaskParseTaskSelectDto(List<Task> results) {
		List<TaskSelectDto> target = new ArrayList<>(results.size());
		for (Task task : results) {
			TaskSelectDto dto = new TaskSelectDto(task);
			
			dto.setProjectId(task.getProjectId());
			dto.setVersionId(task.getVersionId());
			dto.setPhaseId(task.getPhaseId());
			dto.setStartDate(task.getStartDate());
			dto.setEndDate(task.getEndDate());
			dto.setBeforeTask(task.getBeforeTask());
			dto.setFlow(task.getFlow());
			dto.setOwner(task.getOwner());
			dto.setPlanTime(task.getPlanTime());
			dto.setDescribe(task.getDescribe());
			dto.setType(task.getType());
			
			//ID
			dto.setId(task.getId());
			//任务名称
			dto.setName(task.getName());
			//项目名称
			Project project = DTOUtils.newDTO(Project.class);
			project.setId(task.getProjectId());
			String projectName =this.isNotRightValue(this.projectMapper.selectOne(project).getName());
			dto.setProjectName(projectName);

			
			//所属阶段
			ProjectPhase projectPhase = DTOUtils.newDTO(ProjectPhase.class);
			projectPhase.setId(task.getPhaseId());
			dto.setPhaseStr(this.isNotRightValue(this.ppMapper.selectOne(projectPhase).getName()));
			//所属版本
			ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
			projectVersion.setId(task.getVersionId());
			dto.setVersionStr(this.isNotRightValue(this.pvMapper.selectOne(projectVersion).getVersion()));
			
			//流程
			dto.setFlowStr(task.getFlow()?"正常流程":"修改流程");
			
			//状态
			int status=task.getStatus();
			dto.setStatus(status);
			dto.setStatusStr(status);
			//计划周期
			String planDays=this.dateToString(task.getStartDate())+
					                               "至"+this.dateToString(task.getEndDate());
			dto.setPlanDays(planDays);
			
			TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
			taskDetails.setTaskId(task.getId());
			taskDetails=tdMapper.selectOne(taskDetails);
			//进度=已完成工时/计划工时*100
			//处理进度
			double taskHover=(short)0;
			if(taskDetails!=null&&taskDetails.getTaskHour()!=null){
				taskHover = (double)taskDetails.getTaskHour();
				taskHover = (taskHover/(double)task.getPlanTime())*100;
			}
			dto.setProgress((int)taskHover);
			//TODO:负责人
			target.add(dto);
		}
		return target;
	}
	
	private  String isNotRightValue(String value){
		if(value==null||value.equals("")){
			return "无效值";
		}
		return value;
	}
	
    /**
     * data 转化为String
     * @param date
     * @return
     */
	private  String  dateToString(Date date){
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    	return sdf.format(date);
    }
    

}
