package com.dili.alm.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants.ApproveType;
import com.dili.alm.constant.AlmConstants.MemberState;
import com.dili.alm.constant.AlmConstants.TaskStatus;
import com.dili.alm.dao.DataDictionaryValueMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskDetailsMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.TaskEntity;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.TaskSelectDto;
import com.dili.alm.domain.dto.apply.ApplyMajorResource;
import com.dili.alm.domain.dto.apply.ApplyRelatedResource;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.alm.service.TaskDetailsService;
import com.dili.alm.service.TaskService;
import com.dili.alm.utils.DateUtil;
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
	@Autowired
	DataDictionaryService dataDictionaryService;
	
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
    
    @Autowired
    private TaskDetailsService taskDetailsService;
	@Autowired
	ProjectVersionService projectVersionService;
	@Autowired
	ProjectPhaseService projectPhaseService;
	@Autowired
	ProjectService projectService;
	@Autowired
	ProjectApplyService projectApplyService;
	private static final String TASK_STATUS_CODE = "task_status";
	private static final String TASK_TYPE_CODE = "task_type";

	@Override
	public List<Project> selectByOwner(Long ownerId) {
		Project project =DTOUtils.newDTO(Project.class);
		return this.projectMapper.getProjectsByTaskOwner(project,ownerId);
	}
	
	@Override
	public EasyuiPageOutput listPageSelectTaskDto(Task task) throws Exception {
		List<Task> list = this.listByExample(task);//查询出来
		Page<Task> page = (Page<Task>) list;
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = null == task.getMetadata() ? new HashMap<>() : task.getMetadata();

		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);
		
		JSONObject  projectVersionProvider= new JSONObject();
		projectVersionProvider.put("projectVersion", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);
		
		JSONObject  projectPhaseProvider= new JSONObject();
		projectPhaseProvider.put("projectPhase", "projectPhaseProvider");
		metadata.put("phaseId", projectPhaseProvider);
		
		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("owner", memberProvider);
		
		JSONObject taskStateProvider = new JSONObject();
		taskStateProvider.put("provider", "taskStateProvider");
		metadata.put("status", taskStateProvider);
		
		JSONObject taskTypeProvider = new JSONObject();
		taskTypeProvider.put("provider", "taskTypeProvider");
		metadata.put("type", taskTypeProvider);
		
		task.setMetadata(metadata);
		try {
			List<TaskEntity> results = this.TaskParseTaskSelectDto(list);//转化为查询的DTO
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
	private List<TaskEntity> TaskParseTaskSelectDto(List<Task> results) {
		List<TaskEntity> target = new ArrayList<>(results.size());
		for (Task task : results) {
			TaskEntity dto = new TaskEntity(task);
			//流程
			dto.setFlowStr(task.getFlow()?"正常流程":"修改流程");
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
			target.add(dto);
		}
		return target;
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

	@Override
	public int updateTaskDetail(TaskDetails taskDetails,Task task) {
		//查询当前要修改的任务工时信息
		TaskDetails taskDetailsFromDatabase =  taskDetailsService.get(taskDetails.getId());
		//校验工时
		short taskHour = Optional.ofNullable(taskDetailsFromDatabase.getTaskHour()).orElse((short) 0);
		//校验加班工时
		short overHour = Optional.ofNullable(taskDetailsFromDatabase.getOverHour()).orElse((short) 0);
		/*task表基础数据更新*/
		//如果有工时就累加，如果没有就用当前填入数据
		if(taskHour==0){
			taskDetailsFromDatabase.setTaskHour(taskDetails.getTaskHour());
		}else{
			taskHour+=taskDetails.getTaskHour();
			taskDetailsFromDatabase.setTaskHour(taskHour);
		}
		
		if (overHour==0) {
			taskDetailsFromDatabase.setOverHour(taskDetails.getOverHour());
		}else{
			overHour+=taskDetails.getOverHour();
			taskDetailsFromDatabase.setTaskHour(overHour);
		}
		
		
		if(taskDetailsFromDatabase.getTaskHour()>=task.getPlanTime()){
			//更新状态为完成
			task.setStatus(TaskStatus.COMPLETE.code); 
		} 
		
		this.taskDetailsService.saveOrUpdate(taskDetailsFromDatabase);
		//进度总量写入project表中
		saveProjectProgress(task);
		
		//更新版本表中的进度
		saveProjectVersion(task);
		
		//更新阶段表中的进度
		saveProjectPhase(task);
		return 0;
	}
	
	
	//计算阶段进度
	private void saveProjectPhase(Task task) {
		//获取阶段信息  已执行的工时/计划工时累加
		ProjectPhase projectPhase = projectPhaseService.get(task.getPhaseId());
		int progress =0;
		double totalPlanTime = 0;
		double totalTaskTime = 0;
		Task  taskSelect  = DTOUtils.newDTO(Task.class);
		taskSelect.setPhaseId(task.getPhaseId());
		List<Task> taskList = this.list(taskSelect);
		for(Task taskResult:taskList){
			totalPlanTime += taskResult.getPlanTime();
			TaskDetails  taskDetailsSelect  = DTOUtils.newDTO(TaskDetails.class);
			taskDetailsSelect.setTaskId(taskResult.getId());
			List<TaskDetails> taskDetailsList = taskDetailsService.list(taskDetailsSelect);
			for(TaskDetails taskDetailsResult:taskDetailsList){
				totalTaskTime += taskDetailsResult.getTaskHour();
			}
		}
		
		 progress = (int) ((totalTaskTime/totalPlanTime)*100);
		 projectPhase.setCompletedProgress(progress);
		 projectPhaseService.saveOrUpdate(projectPhase);
	}

	//计算版本进度
	private void saveProjectVersion(Task task) {
		//获取阶段信息  已执行的工时/计划工时累加
		ProjectVersion projectVersion = projectVersionService.get(task.getVersionId());
		
		int progress =0;
		double totalPlanTime = 0;
		double totalTaskTime = 0;
		
		Task  taskSelect  = DTOUtils.newDTO(Task.class);
		taskSelect.setPhaseId(task.getVersionId());
		
		List<Task> taskList = this.list(taskSelect);
		
		for(Task taskResult:taskList){
			totalPlanTime += taskResult.getPlanTime();
			TaskDetails  taskDetailsSelect  = DTOUtils.newDTO(TaskDetails.class);
			taskDetailsSelect.setTaskId(taskResult.getId());
			List<TaskDetails> taskDetailsList = taskDetailsService.list(taskDetailsSelect);
			for(TaskDetails taskDetailsResult:taskDetailsList){
				totalTaskTime += taskDetailsResult.getTaskHour();
			}
		}
		
		 progress = (int) ((totalTaskTime/totalPlanTime)*100);
		 projectVersion.setCompletedProgress(progress);
		 projectVersionService.saveOrUpdate(projectVersion);
		
	}
	
	//计算项目总进度
	private void saveProjectProgress(Task task) {
		
		int progress = 0;
		
		Project project = projectService.get(task.getProjectId());
		//相对应的立项信息
		ProjectApply projectApply = projectApplyService.get(project.getApplyId());
		
		ApplyMajorResource applyMajorResource = JSON.parseObject(
				Optional.ofNullable(projectApply.getResourceRequire()).orElse("{}"),
				ApplyMajorResource.class);
		
		//整体项目预估时间
		double total = Optional.ofNullable(applyMajorResource.getMainWorkTime()).orElse(0);
		
		List<ApplyRelatedResource> list = Optional.ofNullable(applyMajorResource.getRelatedResources()).orElse(new ArrayList<ApplyRelatedResource>());
		
		for (int i = 0; i < list.size(); i++) {
			ApplyRelatedResource  applyRelatedResource = list.get(i);
			total += Optional.ofNullable(applyRelatedResource.getRelatedWorkTime()).orElse(0);
		}

		//任务计划中已完成的时间总和
		double taskTimes = 0;
		
		Task  taskSelect  = DTOUtils.newDTO(Task.class);
		taskSelect.setProjectId(task.getProjectId());
		//查询任务表里 项目下的所有任务
		List<Task> taskList = this.list(taskSelect);
		for (Task taskDome: taskList){
			//每个项目下的所有任务详情
			TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
			taskDetails.setTaskId(taskDome.getId());
			//任务下的工时累加
			List<TaskDetails> taskDetailsList = taskDetailsService.list(taskDetails);
			for (TaskDetails taskDetailsDome: taskDetailsList){
				taskTimes += taskDetailsDome.getTaskHour();
			}
		}
		progress = (int) ((1-taskTimes/total)*100);
		project.setCompletedProgress(progress);
		projectService.saveOrUpdate(project);
	}



	@Override
	public int startTask(Task task) {
		
		if(task.getStatus()!=2){
			TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
			taskDetails.setTaskId(task.getId());
			taskDetails.setCreated(new Date());
			taskDetails.setTaskHour((short) 0);
			taskDetails.setOverHour((short) 0);
			taskDetails.setTaskTime("0");
			/*taskDetails.setCreateMemberId(task.getModifyMemberId());*/
		    taskDetailsService.insert(taskDetails);
		}

	    task.setStatus(TaskStatus.START.code);// 更新状态为开始任务
	    return this.update(task);
	}

	@Override
	public int notComplateStatus(Task task) {
		int days = Integer.parseInt(DateUtil.getDatePoor(new Date(), task.getEndDate()).trim());
		if (days<=0) {
			task.setStatus(TaskStatus.NOTCOMPLETE.code);// 更新状态为未完成
			task.setModified(new Date());
			return this.update(task);
		}else{
			return 14;
		}
	}

	@Override
	public List<DataDictionaryValueDto> getTaskStates() {
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(TASK_STATUS_CODE);
		if (dto == null) {
			return null;
		}
		return dto.getValues();
	}

	@Override
	public List<DataDictionaryValueDto> getTaskTypes() {
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(TASK_TYPE_CODE);
		if (dto == null) {
			return null;
		}
		return dto.getValues();
	}
}
