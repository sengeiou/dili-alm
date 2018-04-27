package com.dili.alm.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.AlmConstants.TaskStatus;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskDetailsMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.dao.TeamMapper;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Role;
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
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.TaskException;
import com.dili.alm.rpc.RoleRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectChangeService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.alm.service.TaskDetailsService;
import com.dili.alm.service.TaskService;
import com.dili.alm.service.TeamService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.github.pagehelper.Page;

import tk.mybatis.mapper.entity.Example;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-11-23 10:23:05.
 */
@Transactional(rollbackFor = ApplicationException.class)
@Service
public class TaskServiceImpl extends BaseServiceImpl<Task, Long> implements TaskService {
	@Autowired
	DataDictionaryService dataDictionaryService;
	@Autowired
	UserRpc userRpc;
	@Autowired
	RoleRpc roleRpc;

	@Autowired
	private TeamMapper teamMapper;

	public TaskMapper getActualDao() {
		return (TaskMapper) getDao();
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
	@Autowired
	private ProjectChangeService projectChangeService;

	private static final String TASK_STATUS_CODE = "task_status";
	private static final String TASK_TYPE_CODE = "task_type";
	private static final Integer PROJECT_STATE_SHUT = 4;
	private static final Integer PROJECT_STATE_COMPLATE = 2;

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
	public EasyuiPageOutput listPageSelectTaskDto(Task task) throws Exception {
		task.setOrder("desc");
		task.setSort("created");
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
			List<TaskEntity> results = this.TaskParseTaskSelectDto(list, true);// 转化为查询的DTO
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
	private List<TaskEntity> TaskParseTaskSelectDto(List<Task> results, boolean isUpdateDetail) {
		List<TaskEntity> target = new ArrayList<>(results.size());
		for (Task task : results) {
			TaskEntity dto = new TaskEntity(task);
			// 项目和版本是否在进行中
			Project project = this.projectMapper.selectByPrimaryKey(task.getProjectId());
			boolean inProgress = project.getProjectState().equals(ProjectState.NOT_START.getValue());
			inProgress = inProgress ? true : project.getProjectState().equals(ProjectState.IN_PROGRESS.getValue());
			if (inProgress) {
				ProjectVersion version = this.versionMapper.selectByPrimaryKey(task.getVersionId());
				inProgress = version.getVersionState().equals(ProjectState.NOT_START.getValue());
				inProgress = inProgress ? true : version.getVersionState().equals(ProjectState.IN_PROGRESS.getValue());
			}
			dto.setCanOperation(inProgress);

			// 流程
			dto.setFlowStr(task.getFlow() ? "变更流程" : "正常流程");
			// 计划周期
			String planDays = this.dateToString(task.getStartDate()) + "至" + this.dateToString(task.getEndDate());
			dto.setPlanDays(planDays);

			TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
			taskDetails.setTaskId(task.getId());
			// 查询出列表
			List<TaskDetails> taskDetailsList = tdMapper.select(taskDetails);
			double totalTaskHour = 0;
			for (TaskDetails entity : taskDetailsList) {// 循环累加
				if (entity.getTaskHour() != null) {// 排除只填写加班工时
					totalTaskHour += entity.getTaskHour();
				}
				/*
				 * if(entity.getOverHour() != null){ //排除只填写任务工时的情况 totalTaskHour +=
				 * entity.getTaskHour(); }
				 */
			}
			// 进度=已完成工时/计划工时*100
			double taskHover = (totalTaskHour / task.getPlanTime()) * 100;
			dto.setProgress((int) taskHover);
			UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
			if (isProjeactComplate(task.getProjectId())) {// TODO:项目已完成、关闭都置灰
				dto.setUpdateDetail(false);
			} else {
				if (task.getStatus() != 3 && this.isManager(task.getProjectId())) {// 不是完成状态或者是项目经理或者是任务责任人
					dto.setUpdateDetail(true);
				} else if (task.getStatus() != 3 && userTicket.getId().equals(task.getOwner())) {
					dto.setUpdateDetail(true);
				} else {
					dto.setUpdateDetail(false);
				}
			}

			if (!isProjeactComplate(task.getProjectId()) && isProjectManager()) {
				dto.setCopyButton(true);
			} else {
				dto.setCopyButton(false);
			}
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

	private boolean isProjeactComplate(Long id) {
		Project project = projectService.get(id);
		if (project.getProjectState().equals(PROJECT_STATE_SHUT)
				|| project.getProjectState().equals(PROJECT_STATE_COMPLATE)) {
			return true;
		}
		return false;
	}

	@Override
	public int updateTaskDetail(TaskDetails taskDetails, Task task) {
		// 标识任务已完成，需要写入项目中实际完成时间
		boolean isComplete = false;
		// 查询当前要修改的任务工时信息
		/*
		 * TaskDetails taskDetailsFromDatabase =
		 * taskDetailsService.get(taskDetails.getId()); // 校验前台工时 int taskHour
		 * =(int)Optional.ofNullable(taskDetails.getTaskHour()).orElse((short) 0); //
		 * 校验前台加班工时 int overHour
		 * =(int)Optional.ofNullable(taskDetails.getOverHour()).orElse((short) 0);
		 * 
		 * int saveHour = taskHour;//用作保存每次存储的工时值 task表基础数据更新
		 * 
		 * int baseTaskHour =(int)taskDetailsFromDatabase.getTaskHour();
		 * baseTaskHour+=taskHour;
		 * taskDetailsFromDatabase.setTaskHour((short)baseTaskHour);
		 * 
		 * 
		 * int baseOverHour =(int)taskDetailsFromDatabase.getOverHour();
		 * baseOverHour+=overHour;
		 * taskDetailsFromDatabase.setOverHour((short)baseOverHour);
		 * 
		 * // 工时信息填写 taskDetailsFromDatabase.setTaskTime(this.taskHoursMapAdd(
		 * taskDetailsFromDatabase.getTaskTime(), saveHour));
		 * 
		 * taskDetailsFromDatabase.setModified(new Date());
		 * 
		 * 
		 * if (taskDetailsFromDatabase.getTaskHour() >= task.getPlanTime()) {
		 */
		// 更新状态为完成
		/*
		 * task.setStatus(TaskStatus.COMPLETE.code); task.setFactEndDate(new Date());
		 * isComplete = true; }
		 * 
		 * 
		 * this.taskDetailsService.insert(taskDetailsFromDatabase);
		 */

		TaskDetails condtion = DTOUtils.newDTO(TaskDetails.class);

		condtion.setTaskId(task.getId());

		List<TaskDetails> list = this.taskDetailsService.list(condtion);

		int totalTaskHours = 0;

		for (TaskDetails entity : list) {

			totalTaskHours += entity.getTaskHour();
		}

		if (totalTaskHours >= task.getPlanTime()) {
			// 更新状态为完成
			/* task.setStatus(TaskStatus.COMPLETE.code); */
			task.setFactEndDate(new Date());
			isComplete = true;
		}

		if (taskDetails.getTaskHour() == 0 && taskDetails.getOverHour() == 0) {// 判断如果加班工时和任务工时同时都没有填写
			return 0;
		}

		int insert = taskDetailsService.insert(taskDetails);

		try {
			// 进度总量写入project表中
			saveProjectProgress(task, isComplete);

			// 更新版本表中的进度
			saveProjectVersion(task);

			// 更新阶段表中的进度
			saveProjectPhase(task);

			this.saveOrUpdate(task);

		} catch (Exception e) {// 有异常返回不成功
			return 0;
		}
		return insert;

	}

	/**
	 * 读取填入的工时记录
	 * 
	 * @param jsonMapStr
	 * @param taskHour
	 * @return
	 */
	private String taskHoursMapAdd(String jsonMapStr, int taskHour) {
		try {
			// StringParseMap
			Map<String, Integer> jsonMap = (Map<String, Integer>) JSON.parse(jsonMapStr);
			if (jsonMap.size() > 0 && jsonMap.get(this.dateToString(new Date()) + 1) == null) {
				// 已经存有值，但没有今日的值
				jsonMap.put(this.dateToString(new Date()) + 1, taskHour);

			} else if (jsonMap.size() == 0) {// 没有开始填写值

				jsonMap.put(this.dateToString(new Date()) + 1, taskHour);

			} else if (jsonMap.size() > 0 && jsonMap.get(this.dateToString(new Date()) + 1) != null) {// 已经存有值，且已存入今日值

				for (int i = 0; i < 8; i++) {
					int keyId = i + 1;
					if (jsonMap.get(this.dateToString(new Date()) + keyId) == null) {// 找到没有存值的key里

						jsonMap.put(this.dateToString(new Date()) + keyId, taskHour);

						break;

					}
				}

			}
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
				totalTaskTime += taskDetailsResult.getOverHour();
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
				totalTaskTime += taskDetailsResult.getOverHour();
			}
		}

		progress = (int) ((totalTaskTime / totalPlanTime) * 100);
		projectVersion.setCompletedProgress(progress);
		projectVersionService.saveOrUpdate(projectVersion);

	}

	// 计算项目总进度
	private void saveProjectProgress(Task task, boolean signComplate) {

		int progress = 0;

		Project project = projectService.get(task.getProjectId());
		// 相对应的立项信息
		ProjectApply projectApply = projectApplyService.get(project.getApplyId());

		ApplyMajorResource applyMajorResource = JSON.parseObject(
				Optional.ofNullable(projectApply.getResourceRequire()).orElse("{}"), ApplyMajorResource.class);

		// 整体项目预估时间
		double total = Optional.ofNullable(applyMajorResource.getMainWorkTime()).orElse(0) * 8;

		List<ApplyRelatedResource> list = Optional.ofNullable(applyMajorResource.getRelatedResources())
				.orElse(new ArrayList<ApplyRelatedResource>());

		for (int i = 0; i < list.size(); i++) {
			ApplyRelatedResource applyRelatedResource = list.get(i);
			total += Optional.ofNullable(applyRelatedResource.getRelatedWorkTime()).orElse(0) * 8;
		}

		// 查询项目变更的累加
		ProjectChange projectChange = DTOUtils.newDTO(ProjectChange.class);
		projectChange.setProjectId(task.getProjectId());
		List<ProjectChange> projectChangeList = projectChangeService.list(projectChange);
		if (projectChangeList != null && projectChangeList.size() > 0) {
			for (ProjectChange projectChange2 : projectChangeList) {
				if (!WebUtil.strIsEmpty(projectChange2.getWorkingHours())) {
					total += Double.parseDouble(projectChange2.getWorkingHours());
				}
			}
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
				taskTimes += taskDetailsDome.getOverHour();
			}
		}
		progress = (int) ((taskTimes / total) * 100);
		project.setCompletedProgress(progress);

		// 接收到true的标识就写入日期
		if (signComplate) {
			project.setActualEndDate(new Date());
		}
		projectService.saveOrUpdate(project);
	}

	@Transactional
	@Override
	public int startTask(Task task) {

		/*
		 * TaskDetails taskDetails=DTOUtils.newDTO(TaskDetails.class);
		 * taskDetails.setTaskId(task.getId()); List<TaskDetails> byExample =
		 * taskDetailsService.list(taskDetails); int size = byExample.size();
		 * 
		 * if (task.getStatus() != 2) {
		 * 
		 * //如果大于0判定为重复提交 if(size>0){ return 0 ; }
		 * 
		 * taskDetails.setTaskId(task.getId()); taskDetails.setCreated(new Date());
		 * taskDetails.setTaskHour((short) 0); taskDetails.setOverHour((short) 0);
		 * taskDetails.setTaskTime("{}");
		 * taskDetails.setCreateMemberId(task.getModifyMemberId());
		 * taskDetailsService.insert(taskDetails); }
		 */
		// 只更新任务状态
		task.setFactBeginDate(new Date());
		task.setStatus(TaskStatus.START.code);// 更新状态为开始任务
		Project project = this.projectMapper.selectByPrimaryKey(task.getProjectId());
		if (project == null) {
			throw new RuntimeException("项目不存在");
		}
		Date now = new Date();
		if (project.getProjectState().equals(ProjectState.NOT_START.getValue())
				|| project.getActualStartDate() == null) {
			project.setActualStartDate(now);
			project.setProjectState(ProjectState.IN_PROGRESS.getValue());
			this.projectMapper.updateByPrimaryKey(project);
		}
		ProjectVersion version = this.versionMapper.selectByPrimaryKey(task.getVersionId());
		if (version.getVersionState().equals(ProjectState.NOT_START.getValue()) || version.getActualEndDate() == null) {
			version.setActualStartDate(now);
			version.setVersionState(ProjectState.IN_PROGRESS.getValue());
			this.versionMapper.updateByPrimaryKey(version);
		}
		ProjectPhase phase = this.phaseMapper.selectByPrimaryKey(task.getPhaseId());
		if (phase.getActualStartDate() == null) {
			phase.setActualStartDate(now);
			this.phaseMapper.updateByPrimaryKey(phase);
		}

		try {

			/*** 初始化相关内容的进度 ***/
			// 进度总量写入project表中
			saveProjectProgress(task, false);

			// 更新版本表中的进度
			saveProjectVersion(task);

			// 更新阶段表中的进度
			saveProjectPhase(task);
			/*** 初始化相关内容的进度 ***/

		} catch (Exception e) {
			return 0;
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
			// 只判断未开始，已开始状态的任务
			if (taskDome.getStatus() == TaskStatus.START.code || taskDome.getStatus() == TaskStatus.NOTSTART.code) {
				// dateUtil 计算相差天数大于0，更新状态为未完成
				int days = Integer.parseInt(DateUtil.getDatePoor(new Date(), taskDome.getEndDate()).trim());
				if (days > 0) {
					taskDome.setStatus(TaskStatus.NOTCOMPLETE.code);// 更新状态为未完成
					taskDome.setModified(new Date());
					this.update(taskDome);
					this.startTask(taskDome);
				}
			}
		}
	}

	/*
	 * 字典
	 */
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

	/**
	 * 判断今日所有项目累加总和是否超过8小时
	 */
	@Override
	public boolean isSetTask(Long id, short taskHour, String modified) {
		// 获取今日填写所有项目的总工时
		int totalTaskHour = restTaskHour(id, modified);

		if (totalTaskHour != 0) {

			totalTaskHour += taskHour;
			// 加上此次添加的超过8小时
			if (totalTaskHour > 8) {
				return false;
			}
		}
		return true;
	}

	/*
	 * 
	 * 已经填写的工时
	 *
	 */
	private int taskHoursPlus(TaskDetails taskDetails) {

		int dayTotal = 0;

		Map<String, Integer> taskHourMap = new HashMap<String, Integer>();

		String taskUpdateDate = this.dateToString(taskDetails.getModified());

		taskHourMap = (Map<String, Integer>) JSON.parse(taskDetails.getTaskTime());
		// 获取最后跟新的日期，用日期在map里取值，一天最多能存8次值
		for (int i = 0; i < 8; i++) {
			if (taskHourMap.get(taskUpdateDate + (i + 1)) == null)
				break;
			dayTotal += taskHourMap.get(taskUpdateDate + (i + 1));
		}
		return dayTotal;
	}

	@Override
	public EasyuiPageOutput listByTeam(Task task, String phaseName) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		ProjectPhase projectPhase = DTOUtils.newDTO(ProjectPhase.class);
		List<Long> ids = null;
		if (phaseName != null) {
			projectPhase.setName(phaseName);
			List<ProjectPhase> projectPhaseList = projectPhaseService.listByExample(projectPhase);
			ids = new ArrayList<Long>();
			for (ProjectPhase projectPhase2 : projectPhaseList) {
				ids.add(projectPhase2.getId());
			}
		}

		if (!WebUtil.strIsEmpty(task.getName())) {
			String replaceAll = task.getName().replaceAll(" ", "");
			task.setName(replaceAll);
		}
		// List<Task> list = taskMapper.selectByTeam(task, userTicket.getId(), ids);
		// 查询出来
		Page<Task> list = (Page<Task>) this.listByExample(task);
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
			List<TaskEntity> results = this.TaskParseTaskSelectDto(list, false);// 转化为查询的DTO
			List taskList = ValueProviderUtils.buildDataByProvider(task, results);
			EasyuiPageOutput taskEasyuiPageOutput = new EasyuiPageOutput(Long.valueOf(list.getTotal()).intValue(),
					taskList);
			return taskEasyuiPageOutput;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * 执行任务权限2
	 */
	@Override
	public boolean isManager(Long projectId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		Team team = DTOUtils.newDTO(Team.class);
		team.setProjectId(projectId);
		team.setMemberId(userTicket.getId());
		List<Team> teamList = teamService.list(team);
		for (Team team2 : teamList) {
			if (team2.getRole().equalsIgnoreCase(TeamRole.PROJECT_MANAGER.getValue())) {
				return true;
			}
			/*
			 * if (team2.getRole().equalsIgnoreCase(TeamRole.PRODUCT_MANAGER.getValue())) {
			 * return true; } if
			 * (team2.getRole().equalsIgnoreCase(TeamRole.TEST_MANAGER.getValue())) { return
			 * true; } if
			 * (team2.getRole().equalsIgnoreCase(TeamRole.DEVELOP_MANAGER.getValue())) {
			 * return true; }
			 */
		}
		return false;
	}

	@Override
	public boolean validateBeginAndEnd(Long projectId, Date startDate, Date endDate) {
		try {
			Project project = projectService.get(projectId);
			Date pstDate = project.getStartDate();// 计划开始时间
			Date penDate = project.getEndDate();// 计划结束时间
			int startCompareVal = Integer.parseInt(DateUtil.getDatePoor(pstDate, startDate));
			int endCompareVal = Integer.parseInt(DateUtil.getDatePoor(penDate, endDate));

			// 大于0，后者晚，小于0 后者早
			if (startCompareVal > 0) {
				return true;
			}
			if (endCompareVal < 0) {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	@Override
	public List<ProjectChange> projectChangeList(Long projectId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		return taskMapper.selectProjectChangeByTeam(userTicket.getId(), projectId);
	}

	@Override
	public List<Project> projectList() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(AlmConstants.DATA_AUTH_TYPE_PROJECT);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return null;
		}
		List<Long> projectIds = new ArrayList<>(dataAuths.size());
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("dataId").toString())));
		Example example = new Example(Project.class);
		example.createCriteria().andIn("id", projectIds);
		List<Project> listProject = projectMapper.selectByExample(example);
		return listProject;
	}

	/**
	 * 判断是否是项目经理
	 */
	@Override
	public boolean isProjectManager() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		Team team = DTOUtils.newDTO(Team.class);
		team.setMemberId(userTicket.getId());
		List<Team> teamList = teamService.list(team);

		for (Team team2 : teamList) {

			if (team2.getRole().equalsIgnoreCase(TeamRole.PROJECT_MANAGER.getValue())) {
				return true;
			}
			if (team2.getRole().equalsIgnoreCase(TeamRole.PRODUCT_MANAGER.getValue())) {
				return true;
			}
			if (team2.getRole().equalsIgnoreCase(TeamRole.TEST_MANAGER.getValue())) {
				return true;
			}
			if (team2.getRole().equalsIgnoreCase(TeamRole.DEVELOP_MANAGER.getValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<User> listUserByTeam() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}

		List<User> userList = new ArrayList<User>();

		/* if (isNoTeam()||isCommittee()) { */
		userList = userRpc.list(new User()).getData();
		/*
		 * }else{ List<Long> userIdList =
		 * taskMapper.selectUserByTeam(userTicket.getId()); userList =new
		 * ArrayList<User>(); for (Long userId : userIdList) { User user =
		 * userRpc.findUserById(userId).getData(); userList.add(user); } }
		 */

		return userList;
	}

	@Override
	public List<ProjectVersion> listProjectVersionByTeam() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		return taskMapper.selectVersionByTeam(userTicket.getId());
	}

	/**
	 * 根据项目ID查询人员
	 */
	@Override
	public List<User> listUserByProjectId(Long projectId) {
		Team team = DTOUtils.newDTO(Team.class);
		team.setProjectId(projectId);
		List<Long> resultIds = teamMapper.selectByProjectId(projectId);
		List<User> userList = new ArrayList<User>();
		for (Long userId : resultIds) {
			User user = userRpc.findUserById(userId).getData();
			userList.add(user);
		}
		return userList;
	}

	@Override
	public List<Task> listTaskByProjectId(Long projectId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		Task task = DTOUtils.newDTO(Task.class);
		task.setProjectId(projectId);
		return taskMapper.selectByTeam(task, userTicket.getId(), null);
	}

	/**
	 * 项目经理判断
	 */
	@Override
	public boolean isThisProjectManger(Long projectId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		Team team = DTOUtils.newDTO(Team.class);
		team.setMemberId(userTicket.getId());
		team.setProjectId(projectId);

		List<Team> teamList = teamService.list(team);

		for (Team team2 : teamList) {
			if (team2.getRole().equalsIgnoreCase(TeamRole.PROJECT_MANAGER.getValue())) {
				return true;
			}
			if (team2.getRole().equalsIgnoreCase(TeamRole.PRODUCT_MANAGER.getValue())) {
				return true;
			}
			if (team2.getRole().equalsIgnoreCase(TeamRole.TEST_MANAGER.getValue())) {
				return true;
			}
			if (team2.getRole().equalsIgnoreCase(TeamRole.DEVELOP_MANAGER.getValue())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否是任务创建者
	 */
	@Override
	public boolean isCreater(Task task) {
		Task taskSelect = this.get(task.getId());
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		if (taskSelect.getCreateMemberId().equals(userTicket.getId())) {
			return true;
		}
		return false;
	}

	/**
	 * 判断其他项目的填写工时是否超过，没有填写工时填flase 任务责任人
	 */
	@Override
	public List<TaskDetails> otherProjectTaskHour(String updateDate, Long ownerId) {

		// 查询本日修改的其他项目的detail
		List<TaskDetails> taskDetails = taskMapper.selectOtherTaskDetail(ownerId, updateDate);

		return taskDetails;
	}

	/*
	 * 已经填写的工时
	 */
	@Override
	public int restTaskHour(Long ownerId, String updateDate) {

		// String updateDate = this.dateToString(new Date());

		int dayTotal = 0;

		// 本日更新其他项目的工时
		List<TaskDetails> tdList = this.otherProjectTaskHour(updateDate, ownerId);

		// 今日没有填写工时
		if (tdList == null || tdList.size() == 0) {
			return 0;
		}
		// 累加做比较，计算本日填写的总工时是否超过8小时
		for (TaskDetails taskDetails : tdList) {

			// int getInt = taskHoursPlus(taskDetails);
			dayTotal += taskDetails.getTaskHour();// 其他项目填写工时总和

		}

		return dayTotal;
	}

	@Override
	public TaskDetails selectDetails(Long taskId) {
		TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
		taskDetails.setTaskId(taskId);
		List<TaskDetails> list = taskDetailsService.list(taskDetails);
		short taskHour = 0;
		short overHour = 0;
		if (list != null || list.size() != 0) {
			for (TaskDetails entity : list) {
				taskHour += entity.getTaskHour();// 累加任务工时
				overHour += entity.getOverHour();// 累加加班工时
			}
		}
		taskDetails.setTaskHour(taskHour);
		taskDetails.setOverHour(overHour);

		return taskDetails;
	}

	@Override
	public boolean isNoTeam() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		Team team = DTOUtils.newDTO(Team.class);
		team.setMemberId(userTicket.getId());

		List<Team> teamList = teamMapper.select(team);
		if (teamList.size() == 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isCommittee() {
		DataDictionaryDto code = dataDictionaryService.findByCode(AlmConstants.ROLE_CODE);
		List<DataDictionaryValueDto> values = code.getValues();
		String roleId = values.stream().filter(v -> Objects.equals(v.getCode(), AlmConstants.ROLE_CODE_WYH)).findFirst()
				.map(DataDictionaryValue::getValue).orElse(null);

		String roleId2 = values.stream().filter(v -> Objects.equals(v.getCode(), AlmConstants.ROLE_CODE_WYH_LEADER))
				.findFirst().map(DataDictionaryValue::getValue).orElse(null);
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		List<Role> currentUsercurrent = roleRpc.listRoleByUserId(userTicket.getId()).getData();
		for (Role role : currentUsercurrent) {
			// 避免ID不一致的问题，用真实名字比对
			if (role.getId().toString().equals(roleId) || role.getId().toString().equals(roleId2)) {// 委员会&委员会组长
				return true;
			}
		}
		return false;
	}

	@Override
	public void addTask(Task task, Short planTime, Date startDateShow, Date endDateShow, Boolean flow, Long creatorId)
			throws TaskException {
		// 判断项目和阶段是否在进行中
		this.checkProjectState(task);
		// 判断是否是本项目的项目经理
		if (!this.isThisProjectManger(task.getProjectId())) {
			throw new TaskException("只有本项目的项目经理可以添加项目！");
		}
		task.setPlanTime(planTime);
		task.setStartDate(startDateShow);
		task.setEndDate(endDateShow);
		task.setFlow(flow);
		task.setCreateMemberId(creatorId);
		task.setCreated(new Date());
		task.setStatus(TaskStatus.NOTSTART.code);// 新增的初始化状态为0未开始状态
		int rows = this.insertSelective(task);
		if (rows <= 0) {
			throw new TaskException("新增任务失败");
		}
	}

	private void checkProjectState(Task task) throws TaskException {
		Project project = this.projectMapper.selectByPrimaryKey(task.getProjectId());
		if (project == null) {
			throw new TaskException("项目不存在");
		}
		if (!project.getProjectState().equals(ProjectState.NOT_START.getValue())
				&& !project.getProjectState().equals(ProjectState.IN_PROGRESS.getValue())) {
			throw new TaskException("项目不在进行中，不能创建任务");
		}
		ProjectVersion version = this.versionMapper.selectByPrimaryKey(task.getVersionId());
		if (version == null) {
			throw new TaskException("版本不存在");
		}
		if (!version.getVersionState().equals(ProjectState.NOT_START.getValue())
				&& !version.getVersionState().equals(ProjectState.IN_PROGRESS.getValue())) {
			throw new TaskException("版本不在进行中，不能创建任务");
		}
	}

	@Override
	public void updateTask(Task task, Long modifyMemberId, Short planTime, Date startDate, Date endDate, Boolean flow)
			throws TaskException {
		// 判断项目和阶段是否在进行中
		this.checkProjectState(task);
		if (!this.isCreater(task)) {
			throw new TaskException("不是本项目的创建者，不能进行编辑");
		}
		// 设置任务修改人为当前登录用户
		task.setModified(new Date());
		task.setModifyMemberId(modifyMemberId);
		task.setPlanTime(planTime);
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		task.setFlow(flow);

		int rows = this.getActualDao().updateByPrimaryKeySelective(task);
		if (rows <= 0) {
			throw new TaskException("更新任务失败");
		}
	}

	@Override
	public void deleteTask(Long id) throws TaskException {
		Task task = this.getActualDao().selectByPrimaryKey(id);
		
		this.checkProjectState(task);
		
		Project project = this.projectMapper.selectByPrimaryKey(task.getProjectId());
		if (project == null) {
			throw new RuntimeException("项目不存在");
		}
		
		if (!this.isCreater(task)) {
			throw new TaskException("不是本项目的创建者，不能进行删除");
		}
		int rows = this.getActualDao().deleteByPrimaryKey(id);

		project.setTaskCount(project.getTaskCount() - 1);
		
		this.projectMapper.updateByPrimaryKey(project);
		if (rows <= 0) {
			throw new TaskException("更新任务失败");
		}
	}

}
