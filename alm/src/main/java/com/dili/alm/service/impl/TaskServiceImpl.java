package com.dili.alm.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants.TaskStatus;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskDetailsMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.TaskEntity;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.TeamRole;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.apply.ApplyMajorResource;
import com.dili.alm.domain.dto.apply.ApplyRelatedResource;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.alm.service.TaskDetailsService;
import com.dili.alm.service.TaskService;
import com.dili.alm.service.TeamService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.github.pagehelper.Page;



/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-23 10:23:05.
 */
@Service
public class TaskServiceImpl extends BaseServiceImpl<Task, Long> implements TaskService {
	@Autowired
	DataDictionaryService dataDictionaryService;
	@Autowired
	UserRpc userRpc;
    public TaskMapper getActualDao() {
        return (TaskMapper)getDao();
    }
    
    @Autowired
    private TaskMapper taskMapper;
    
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
	TeamService teamService;

	@Autowired
	ProjectApplyService projectApplyService;
	@Autowired
	private ProjectVersionMapper versionMapper;
	@Autowired
	private ProjectPhaseMapper phaseMapper;
	private static final String TASK_STATUS_CODE = "task_status";
	private static final String TASK_TYPE_CODE = "task_type";

	@Override
	public int insert(Task t) {
		Project project = this.projectMapper.selectByPrimaryKey(t.getProjectId());
		if (project == null) {
			throw new RuntimeException("项目不存在");
		}
		project.setTaskCount(project.getTaskCount() + 1);
		this.projectMapper.updateByPrimaryKey(project);
		return super.insert(t);
	}

	@Override
	public int insertSelective(Task t) {
		Project project = this.projectMapper.selectByPrimaryKey(t.getProjectId());
		if (project == null) {
			throw new RuntimeException("项目不存在");
		}
		project.setTaskCount(project.getTaskCount() + 1);
		this.projectMapper.updateByPrimaryKey(project);
		return super.insertSelective(t);
	}

	@Override
	public List<Project> selectByOwner(Long ownerId) {
		Project project = DTOUtils.newDTO(Project.class);
		return this.projectMapper.getProjectsByTaskOwner(project, ownerId);
	}

	@Override
	public EasyuiPageOutput listPageSelectTaskDto(Task task) throws Exception {
		List<Task> list = this.listByExample(task);// 查询出来
		Page<Task> page = (Page<Task>) list;
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = null == task.getMetadata() ? new HashMap<>() : task.getMetadata();

		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);

		JSONObject projectVersionProvider = new JSONObject();
		projectVersionProvider.put("projectVersion", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);

		JSONObject projectPhaseProvider = new JSONObject();
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
			List<TaskEntity> results = this.TaskParseTaskSelectDto(list);// 转化为查询的DTO
			List taskList = ValueProviderUtils.buildDataByProvider(task, results);
			EasyuiPageOutput taskEasyuiPageOutput = new EasyuiPageOutput(
					Integer.valueOf(Integer.parseInt(String.valueOf(page.getTotal()))), taskList);
			return new EasyuiPageOutput(Integer.valueOf(Integer.parseInt(String.valueOf(page.getTotal()))), taskList);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * DTO转化
	 * 
	 * @param results
	 * @return
	 */
	private List<TaskEntity> TaskParseTaskSelectDto(List<Task> results) {
		List<TaskEntity> target = new ArrayList<>(results.size());
		for (Task task : results) {
			TaskEntity dto = new TaskEntity(task);
			// 流程
			dto.setFlowStr(task.getFlow() ? "变更流程" : "正常流程");
			// 计划周期
			String planDays = this.dateToString(task.getStartDate()) + "至" + this.dateToString(task.getEndDate());
			dto.setPlanDays(planDays);

			
			
			TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
			taskDetails.setTaskId(task.getId());
			taskDetails = tdMapper.selectOne(taskDetails);
			// 进度=已完成工时/计划工时*100
			// 处理进度
			double taskHover = (short) 0;
			if (taskDetails != null && taskDetails.getTaskHour() != null) {
				taskHover = (double) taskDetails.getTaskHour();
				taskHover = (taskHover / (double) task.getPlanTime()) * 100;
			}
			dto.setProgress((int) taskHover);
			target.add(dto);
		}
		return target;
	}

	/**
	 * data 转化为String
	 * 
	 * @param date
	 * @return
	 */
	private String dateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	@Override
	public int updateTaskDetail(TaskDetails taskDetails, Task task) {
		// 查询当前要修改的任务工时信息
		TaskDetails taskDetailsFromDatabase = taskDetailsService.get(taskDetails.getId());
		// 校验工时
		short taskHour = Optional.ofNullable(taskDetailsFromDatabase.getTaskHour()).orElse((short) 0);
		// 校验加班工时
		short overHour = Optional.ofNullable(taskDetailsFromDatabase.getOverHour()).orElse((short) 0);
		/* task表基础数据更新 */

		if (taskHour == 0) {
			taskDetailsFromDatabase.setTaskHour(taskDetails.getTaskHour());
		} else {
			taskHour += taskDetails.getTaskHour();
			taskDetailsFromDatabase.setTaskHour(taskHour);
		}

		if (overHour == 0) {
			taskDetailsFromDatabase.setOverHour(taskDetails.getOverHour());
		} else {
			overHour += taskDetails.getOverHour();
			taskDetailsFromDatabase.setTaskHour(overHour);
		}
		// 工时信息填写
		taskDetailsFromDatabase.setTaskTime(this.taskHoursMapAdd(taskDetailsFromDatabase.getTaskTime(), taskHour));

		if (taskDetailsFromDatabase.getTaskHour() >= task.getPlanTime()) {
			// 更新状态为完成
			task.setStatus(TaskStatus.COMPLETE.code);
			task.setFactEndDate(new Date());
		}
		taskDetailsFromDatabase.setModified(new Date());
		
		this.taskDetailsService.saveOrUpdate(taskDetailsFromDatabase);
		this.saveOrUpdate(task);
		// 进度总量写入project表中
		saveProjectProgress(task);

		// 更新版本表中的进度
		saveProjectVersion(task);

		// 更新阶段表中的进度
		saveProjectPhase(task);
		return 0;
	}

	/**
	 * 读取填入的工时记录
	 * @param jsonMapStr
	 * @param taskHour
	 * @return
	 */
	private String taskHoursMapAdd(String jsonMapStr, int taskHour) {
		try {
			// StringParseMap
			Map<String, Integer> jsonMap = (Map<String, Integer>) JSON.parse(jsonMapStr);
			if (jsonMap.size() > 0) {
				jsonMap.put(this.dateToString(new Date()) + (jsonMap.size() + 1), taskHour);
			} else {
				jsonMap.put(this.dateToString(new Date()) + 1, taskHour);
			}
			// MapParseString
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(jsonMap);

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";
	}

	// 计算阶段进度
	private void saveProjectPhase(Task task) {
		// 获取阶段信息 已执行的工时/计划工时累加
		ProjectPhase projectPhase = projectPhaseService.get(task.getPhaseId());
		int progress = 0;
		double totalPlanTime = 0;
		double totalTaskTime = 0;
		Task taskSelect = DTOUtils.newDTO(Task.class);
		taskSelect.setPhaseId(task.getPhaseId());
		List<Task> taskList = this.list(taskSelect);
		for (Task taskResult : taskList) {
			totalPlanTime += taskResult.getPlanTime();
			TaskDetails taskDetailsSelect = DTOUtils.newDTO(TaskDetails.class);
			taskDetailsSelect.setTaskId(taskResult.getId());
			List<TaskDetails> taskDetailsList = taskDetailsService.list(taskDetailsSelect);
			for (TaskDetails taskDetailsResult : taskDetailsList) {
				totalTaskTime += taskDetailsResult.getTaskHour();
			}
		}

		progress = (int) ((totalTaskTime / totalPlanTime) * 100);
		projectPhase.setCompletedProgress(progress);
		projectPhaseService.saveOrUpdate(projectPhase);
	}

	// 计算版本进度
	private void saveProjectVersion(Task task) {
		// 获取阶段信息 已执行的工时/计划工时累加
		ProjectVersion projectVersion = projectVersionService.get(task.getVersionId());

		int progress = 0;
		double totalPlanTime = 0;
		double totalTaskTime = 0;

		Task taskSelect = DTOUtils.newDTO(Task.class);
		taskSelect.setVersionId(task.getVersionId());

		List<Task> taskList = this.list(taskSelect);

		for (Task taskResult : taskList) {
			totalPlanTime += taskResult.getPlanTime();
			TaskDetails taskDetailsSelect = DTOUtils.newDTO(TaskDetails.class);
			taskDetailsSelect.setTaskId(taskResult.getId());
			List<TaskDetails> taskDetailsList = taskDetailsService.list(taskDetailsSelect);
			for (TaskDetails taskDetailsResult : taskDetailsList) {
				totalTaskTime += taskDetailsResult.getTaskHour();
			}
		}

		progress = (int) ((totalTaskTime / totalPlanTime) * 100);
		projectVersion.setCompletedProgress(progress);
		projectVersionService.saveOrUpdate(projectVersion);

	}

	// 计算项目总进度
	private void saveProjectProgress(Task task) {

		int progress = 0;

		Project project = projectService.get(task.getProjectId());
		// 相对应的立项信息
		ProjectApply projectApply = projectApplyService.get(project.getApplyId());

		ApplyMajorResource applyMajorResource = JSON.parseObject(
				Optional.ofNullable(projectApply.getResourceRequire()).orElse("{}"), ApplyMajorResource.class);

		// 整体项目预估时间
		double total = Optional.ofNullable(applyMajorResource.getMainWorkTime()).orElse(0);

		List<ApplyRelatedResource> list = Optional.ofNullable(applyMajorResource.getRelatedResources())
				.orElse(new ArrayList<ApplyRelatedResource>());

		for (int i = 0; i < list.size(); i++) {
			ApplyRelatedResource applyRelatedResource = list.get(i);
			total += Optional.ofNullable(applyRelatedResource.getRelatedWorkTime()).orElse(0);
		}

		// 任务计划中已完成的时间总和
		double taskTimes = 0;

		Task taskSelect = DTOUtils.newDTO(Task.class);
		taskSelect.setProjectId(task.getProjectId());
		// 查询任务表里 项目下的所有任务
		List<Task> taskList = this.list(taskSelect);
		for (Task taskDome : taskList) {
			// 每个项目下的所有任务详情
			TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
			taskDetails.setTaskId(taskDome.getId());
			// 任务下的工时累加
			List<TaskDetails> taskDetailsList = taskDetailsService.list(taskDetails);
			for (TaskDetails taskDetailsDome : taskDetailsList) {
				taskTimes += taskDetailsDome.getTaskHour();
			}
		}
		progress = (int) (( taskTimes / total) -1* 100);
		project.setCompletedProgress(progress);
		projectService.saveOrUpdate(project);
	}

	@Transactional
	@Override
	public int startTask(Task task) {
		
		TaskDetails taskDetails=DTOUtils.newDTO(TaskDetails.class);
		taskDetails.setTaskId(task.getId());
		List<TaskDetails> byExample = taskDetailsService.list(taskDetails);
		int size = byExample.size();
		if(size!=0){
			return 0 ;
		}
		
		if (task.getStatus() != 2) {
			taskDetails.setTaskId(task.getId());
			taskDetails.setCreated(new Date());
			taskDetails.setTaskHour((short) 0);
			taskDetails.setOverHour((short) 0);
			taskDetails.setTaskTime("{}");
			taskDetails.setCreateMemberId(task.getModifyMemberId());
			taskDetailsService.insert(taskDetails);
		}

		task.setStatus(TaskStatus.START.code);// 更新状态为开始任务
		Project project = this.projectMapper.selectByPrimaryKey(task.getProjectId());
		if (project == null) {
			throw new RuntimeException("项目不存在");
		}
		Date now = new Date();
		if (project.getProjectState().equals(ProjectState.NOT_START.getValue())) {
			project.setActualStartDate(now);
			project.setProjectState(ProjectState.IN_PROGRESS.getValue());
			this.projectMapper.updateByPrimaryKey(project);
		}
		ProjectVersion version = this.versionMapper.selectByPrimaryKey(task.getVersionId());
		if (version.getVersionState().equals(ProjectState.NOT_START.getValue())) {
			version.setActualStartDate(now);
			version.setVersionState(ProjectState.IN_PROGRESS.getValue());
			this.versionMapper.updateByPrimaryKey(version);
		}
		ProjectPhase phase = this.phaseMapper.selectByPrimaryKey(task.getPhaseId());
		if (phase.getActualStartDate() == null) {
			phase.setActualStartDate(now);
			this.phaseMapper.updateByPrimaryKey(phase);
		}
		return this.update(task);
	}

	/**
	 * 定时刷过期任务
	 */
	@Scheduled(cron = "0 0 0 * * ? ")
	@Override
	public void notComplateTask() {
		Task taskSelect = DTOUtils.newDTO(Task.class);
		// 查询任务表里 项目下的所有任务
		List<Task> taskList = this.list(taskSelect);
		for (Task taskDome : taskList) {
			// dateUtil 计算相差天数小于0，更新状态为未完成
			int days = Integer.parseInt(DateUtil.getDatePoor(new Date(), taskDome.getEndDate()).trim());
			if (days <= 0) {
				taskDome.setStatus(TaskStatus.NOTCOMPLETE.code);// 更新状态为未完成
				taskDome.setModified(new Date());
				this.update(taskDome);
			}
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

	@Override
	public boolean isSetTask(Long id, short taskHour) {

		Map<String, Integer> taskHourMap = new HashMap<String, Integer>();
		String taskUpdateDate = null;
		int dayTotal = 0;
		// 校验一天内的工时是否超过8小时
		TaskDetails taskDetails = this.taskDetailsService.get(id);
		taskHourMap = (Map<String, Integer>) JSON.parse(taskDetails.getTaskTime());

		if (taskHourMap.size() == 0) {
			return true;
		} else {
			taskUpdateDate = this.dateToString(taskDetails.getModified()).equals(this.dateToString(new Date()))
					? this.dateToString(taskDetails.getModified())
					: this.dateToString(new Date());// 获取最后跟新的日期，用日期在map里取值，一天最多能存8次值

			for (int i = 0; i < 8; i++) {
				if (taskHourMap.get(taskUpdateDate + (i + 1)) == null)
					break;
				dayTotal += taskHourMap.get(taskUpdateDate + (i + 1));
			}

			// 再加上填写的时间
			dayTotal += taskHour;

			if (dayTotal >= 8) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public EasyuiPageOutput listByTeam(Task task, String phaseName) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		ProjectPhase projectPhase=DTOUtils.newDTO(ProjectPhase.class);
		List<Long> ids = null;
		if (phaseName!=null) {
			projectPhase.setName(phaseName);
			List<ProjectPhase> projectPhaseList = projectPhaseService.listByExample(projectPhase);
			ids = new ArrayList<Long>();
			for (ProjectPhase projectPhase2 : projectPhaseList) {
				ids.add(projectPhase2.getId());
			}
		}
		List<Task> list = taskMapper.selectByTeam(task, userTicket.getId(), ids);// 查询出来
		int count = taskMapper.selectByTeamCount(task, userTicket.getId(), ids);
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = null == task.getMetadata() ? new HashMap<>() : task.getMetadata();

		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);

		JSONObject projectVersionProvider = new JSONObject();
		projectVersionProvider.put("projectVersion", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);

		JSONObject projectPhaseProvider = new JSONObject();
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
			List<TaskEntity> results = this.TaskParseTaskSelectDto(list);// 转化为查询的DTO
			List taskList = ValueProviderUtils.buildDataByProvider(task, results);
			EasyuiPageOutput taskEasyuiPageOutput = new EasyuiPageOutput(count, taskList);
			return taskEasyuiPageOutput;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isManager(Long managerId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		Team team = DTOUtils.newDTO(Team.class);
		team.setProjectId(managerId);
		team.setMemberId(userTicket.getId());
		List<Team> teamList = teamService.list(team);
		for (Team team2 : teamList) {
			if (team2.getRole().equalsIgnoreCase(TeamRole.PROJECT_MANAGER.getValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean validateBeginAndEnd(Task task) {
		try {
			Project project = projectService.get(task.getProjectId());
			Date pstDate = project.getStartDate();// 开始时间
			Date penDate = project.getEndDate();// 结束时间

			int startCompareVal = DateUtil.compare_date(DateUtil.getDateStr(pstDate),
					DateUtil.getDateStr(task.getStartDate()));
			int endCompareVal = DateUtil.compare_date(DateUtil.getDateStr(penDate),
					DateUtil.getDateStr(task.getEndDate()));

			// 合法值是0,1
			if (startCompareVal != 1) {
				return false;
			}
			if (endCompareVal != -1) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	@Override
	public List<Project> projectList() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		return taskMapper.selectProjectByTeam(userTicket.getId());
	}

	/**
	 * 判断是否是项目经理
	 */
	@Override
	public boolean isProjectManager() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		Team team = DTOUtils.newDTO(Team.class);
		team.setMemberId(userTicket.getId());
		List<Team> teamList = teamService.list(team);
		
		for (Team team2 : teamList) {
			if (team2.getRole().equalsIgnoreCase(TeamRole.PROJECT_MANAGER.getValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<User> listUserByTeam() {
		 UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		 List<Long> userIdList = taskMapper.selectUserByTeam(userTicket.getId());
		 List<User> userList =new ArrayList<User>();
		 for (Long userId : userIdList) {
			 User user = userRpc.findUserById(userId).getData();
			 userList.add(user);
		 }
		 
		return userList;
	}

	@Override
	public List<ProjectVersion> listProjectVersionByTeam() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		return taskMapper.selectVersionByTeam(userTicket.getId());
	}

	/**
	 * 根据项目ID查询人员
	 */
	@Override
	public List<User> listUserByProjectId(Long projectId) {
		Team team = DTOUtils.newDTO(Team.class);
		team.setProjectId(projectId);
		List<Team> result = teamService.list(team);
		List<User> userList =new ArrayList<User>();
		for (Team entity : result) {
			 User user = userRpc.findUserById(entity.getMemberId()).getData();
			 userList.add(user);
		}
		return userList;
	}


	@Override
	public List<Task> listTaskByProjectId(Long projectId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		Task task = DTOUtils.newDTO(Task.class);
		task.setProjectId(projectId);
		return taskMapper.selectByTeam(task, userTicket.getId(), null);
	}
	
	
}
