package com.dili.alm.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.MailManager;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.AlmConstants.TaskStatus;
import com.dili.alm.dao.ProjectActionRecordMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskDetailsMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.dao.TeamMapper;
import com.dili.alm.domain.ActionDateType;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectAction;
import com.dili.alm.domain.ProjectActionRecord;
import com.dili.alm.domain.ProjectActionType;
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
import com.dili.alm.domain.WorkDay;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.apply.ApplyMajorResource;
import com.dili.alm.domain.dto.apply.ApplyRelatedResource;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ProjectException;
import com.dili.alm.exceptions.ProjectVersionException;
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
import com.dili.alm.service.WorkDayService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.quartz.domain.ScheduleMessage;
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
	private static final Integer PROJECT_STATE_COMPLATE = 2;
	private static final Integer PROJECT_STATE_SHUT = 4;
	private static final String TASK_STATUS_CODE = "task_status";

	private static final String TASK_TYPE_CODE = "task_type";

	@Autowired
	DataDictionaryService dataDictionaryService;

	@Autowired
	ProjectApplyService projectApplyService;

	@Autowired
	ProjectPhaseService projectPhaseService;

	@Autowired
	ProjectService projectService;

	@Autowired
	ProjectVersionService projectVersionService;

	@Autowired
	RoleRpc roleRpc;

	@Autowired
	TeamService teamService;
	@Autowired
	UserRpc userRpc;
	private String contentTemplate;
	@Qualifier("mailContentTemplate")
	@Autowired
	private GroupTemplate groupTemplate;

	@Value("${spring.mail.username:}")
	private String mailFrom;
	@Autowired
	private MailManager mailManager;
	@Autowired
	private ProjectActionRecordMapper parMapper;

	@Autowired
	private ProjectPhaseMapper phaseMapper;
	@Autowired
	private ProjectChangeService projectChangeService;
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private TaskDetailsService taskDetailsService;
	@Autowired
	private TaskMapper taskMapper;
	@Autowired
	private TaskDetailsMapper tdMapper;
	@Autowired
	private TeamMapper teamMapper;
	@Autowired
	private ProjectVersionMapper versionMapper;
	@Autowired
	private WorkDayService workDayService;

	public TaskServiceImpl() {
		super();
		InputStream in = null;
		try {
			Resource res = new ClassPathResource("conf/taskTimeoutMailContentTemplate.html");
			in = res.getInputStream();
			this.contentTemplate = IOUtils.toString(in, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
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
		Project project = this.projectMapper.selectByPrimaryKey(task.getProjectId());
		if (project == null) {
			throw new TaskException("项目不存在");
		}
		project.setTaskCount(project.getTaskCount() + 1);
		int rows = this.projectMapper.updateByPrimaryKey(project);
		if (rows <= 0) {
			throw new TaskException("更新项目信息失败");
		}

		task.setPlanTime(planTime);
		task.setStartDate(startDateShow);
		task.setEndDate(endDateShow);
		task.setFlow(flow);
		task.setCreateMemberId(creatorId);
		task.setCreated(new Date());
		task.setStatus(TaskStatus.NOTSTART.code);// 新增的初始化状态为0未开始状态
		rows = this.insertSelective(task);
		if (rows <= 0) {
			throw new TaskException("新增任务失败");
		}

		// 插入任务规划
		ProjectActionRecord par = DTOUtils.newDTO(ProjectActionRecord.class);
		par.setActionCode(ProjectAction.TASK_PLAN.getCode());
		par.setActionDateType(ActionDateType.PERIOD.getValue());
		par.setActionStartDate(task.getStartDate());
		par.setActionEndDate(task.getEndDate());
		par.setActionType(ProjectActionType.TASK.getValue());
		par.setTaskId(task.getId());
		rows = this.parMapper.insertSelective(par);
	}

	@Override
	public void completeTask(Long taskId, Long operatorId) throws TaskException {
		Task task = this.getActualDao().selectByPrimaryKey(taskId);
		if (task == null) {
			throw new TaskException("任务不存在");
		}
		Project project = this.projectMapper.selectByPrimaryKey(task.getProjectId());
		if (project == null) {
			throw new TaskException("项目不存在");
		}
		if (!task.getOwner().equals(operatorId) && !project.getProjectManager().equals(operatorId)) {
			throw new TaskException("该用户没有权限执行该操作");
		}
		Date now = new Date();
		task.setStatus(TaskStatus.COMPLETE.code);// 更新状态为完成
		task.setModified(now);
		task.setModifyMemberId(operatorId);
		task.setFactEndDate(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(task);
		if (rows <= 0) {
			throw new TaskException("更新任务失败");
		}
		project.setActualEndDate(now);
		rows = this.projectMapper.updateByPrimaryKeySelective(project);
		if (rows <= 0) {
			throw new TaskException("更新项目失败");
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

		// 更新项目任务数
		project.setTaskCount(project.getTaskCount() - 1);

		int rows = this.projectMapper.updateByPrimaryKey(project);
		if (rows <= 0) {
			throw new TaskException("更新项目信息任务失败");
		}

		// 删除任务进程记录
		ProjectActionRecord parQuery = DTOUtils.newDTO(ProjectActionRecord.class);
		parQuery.setTaskId(task.getId());
		this.parMapper.delete(parQuery);

		rows = this.getActualDao().deleteByPrimaryKey(id);
		if (rows <= 0) {
			throw new TaskException("删除任务失败");
		}
	}

	public TaskMapper getActualDao() {
		return (TaskMapper) getDao();
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

	@Override
	public boolean isInCurrentWeek(Long taskId) {
		Task task = this.getActualDao().selectByPrimaryKey(taskId);
		WorkDay workDay = this.workDayService.getNowWeeklyWorkDay();
		long wStart = workDay.getWorkStartTime().getTime();
		long wEnd = workDay.getWorkEndTime().getTime();
		long tStart = task.getStartDate().getTime();
		long tEnd = task.getEndDate().getTime();
		// -------tStart-------wStart-------------tEnd---------------wEnd--------------
		if (wStart >= tStart && wStart <= tEnd && wEnd > tEnd) {
			return true;
		}
		// ----------wStart----------tStart--------------tEnd------------wEnd----------
		if (wStart <= tStart && wEnd >= tEnd) {
			return true;
		}
		// ----------wStart-------------tStart-------------wEnd-----------tEnd---------
		if (wStart <= tStart && wEnd >= tStart && wEnd <= tEnd) {
			return true;
		}
		// -------tStart--------wStart-------------------wEnd----------tend-------------
		if (tStart <= wStart && tEnd >= wEnd) {
			return true;
		}
		return false;
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
		}
		return false;
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
		}
		return false;
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
			LOGGER.error(e.getMessage(), e);
			return null;
		}
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

	@Override
	public List<ProjectVersion> listProjectVersionByTeam() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		return taskMapper.selectVersionByTeam(userTicket.getId());
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
	public List<User> listUserByTeam() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}

		List<User> userList = new ArrayList<User>();

		userList = userRpc.list(new User()).getData();

		return userList;
	}

	/**
	 * 定时刷过期任务
	 */
	@Override
	public void notComplateTask(ScheduleMessage msg) {
		// 查询任务表里 项目下的所有任务
		Example example = new Example(Task.class);
		example.createCriteria().andEqualTo("status", TaskStatus.START.getCode());
		List<Task> taskList = this.getActualDao().selectByExample(example);
		long now = System.currentTimeMillis();
		for (Task taskDome : taskList) {
			// 只判断未开始，已开始状态的任务
			// dateUtil 计算相差天数大于0，更新状态为未完成
			if (now - taskDome.getEndDate().getTime() >= 24 * 60 * 60 * 1000) {
				taskDome.setStatus(TaskStatus.NOTCOMPLETE.code);// 更新状态为未完成
				taskDome.setModified(new Date());
				this.getActualDao().updateByPrimaryKeySelective(taskDome);
				this.sendTimeoutMail(taskDome);
			}
		}
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

	@Override
	public List<ProjectChange> projectChangeList(Long projectId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		return taskMapper.selectProjectChangeByTeam(userTicket.getId(), projectId);
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
		if (CollectionUtils.isNotEmpty(list)) {
			for (TaskDetails entity : list) {
				taskHour += entity.getTaskHour();// 累加任务工时
				overHour += entity.getOverHour();// 累加加班工时
			}
		}
		taskDetails.setTaskHour(taskHour);
		taskDetails.setOverHour(overHour);

		return taskDetails;
	}

	@Transactional(rollbackFor = ApplicationException.class)
	@Override
	public void startTask(Long taskId, Long modifierId)
			throws TaskException, ProjectVersionException, ProjectException {

		// 查询任务
		Task task = this.getActualDao().selectByPrimaryKey(taskId);
		if (task == null) {
			throw new TaskException("任务不存在");
		}

		// 只更新任务状态
		task.setFactBeginDate(new Date());
		task.setStatus(TaskStatus.START.code);// 更新状态为开始任务
		Date now = new Date();

		// 项目开始
		this.projectService.start(task.getProjectId());

		// 版本开始
		this.projectVersionService.start(task.getVersionId());
		ProjectPhase phase = this.phaseMapper.selectByPrimaryKey(task.getPhaseId());
		if (phase.getActualStartDate() == null) {
			phase.setActualStartDate(now);
			int rows = this.phaseMapper.updateByPrimaryKey(phase);
			if (rows <= 0) {
				throw new TaskException("更新阶段信息失败");
			}
		}

		/*** 初始化相关内容的进度 ***/
		// 进度总量写入project表中
		saveProjectProgress(task, false);

		// 更新版本表中的进度
		saveProjectVersion(task);

		// 更新阶段表中的进度
		saveProjectPhase(task);
		/*** 初始化相关内容的进度 ***/

		int rows = this.update(task);
		if (rows <= 0) {
			throw new TaskException("更新任务失败");
		}
	}

	@Override
	public Long submitWorkingHours(Long taskId, Long operator, Date taskDate, Short taskHour, Short overHour,
			String content) throws TaskException {

		// 判断是否填写了工时
		if (taskHour <= 0 && overHour <= 0) {
			throw new TaskException("请填写工时");
		}

		// 判断日期是否小于当天
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date now = new Date();
		try {
			if (sdf.parse(sdf.format(taskDate)).getTime() > sdf.parse(sdf.format(taskDate)).getTime()) {
				throw new TaskException("不能填写大于当天的任务");
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		Task task = this.getActualDao().selectByPrimaryKey(taskId);

		// 是否是任务责任人
		boolean isOwner = operator.equals(task.getOwner());
		// 是否是项目经理
		boolean isManager = this.isManager(task.getProjectId());

		// 判断是否有权限填写工时
		if (!isManager && !isOwner) {
			throw new TaskException("只有本项目的项目经理或者任务所有者才可以填写工时！");
		}

		// 判断是否是当前周
		boolean isCurrentWeek = this.isTaskTimeInCurrentWeek(taskId, taskDate);

		// 如果是任务责任人但不是项目经理，那么只能补填当前一周工作日的工时
		if (!isManager && !isCurrentWeek) {
			throw new TaskException("只有项目经理才能补填工时");
		}

		String executeDateStr = sdf.format(taskDate);

		// 未超过8小时判断
		if (taskHour > 0 && !this.isSetTask(task.getOwner(), taskHour, executeDateStr)) {
			throw new TaskException("今日工时已填写超过8小时！");
		}
		TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
		taskDetails.setTaskId(taskId);
		taskDetails.setTaskHour(taskHour);
		taskDetails.setOverHour(overHour);
		taskDetails.setDescribe(content);
		taskDetails.setCreateMemberId(operator);// 填写人
		taskDetails.setCreated(now);// 实际修改填写日期
		taskDetails.setModified(taskDate);// 任务日
		taskDetails.setModifyMemberId(task.getOwner());// 责任人
		this.updateTaskDetail(taskDetails, task);// 保存任务
		return taskDetails.getId();
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

		// 更新任务规划
		ProjectActionRecord parQuery = DTOUtils.newDTO(ProjectActionRecord.class);
		parQuery.setActionCode(ProjectAction.TASK_PLAN.getCode());
		parQuery.setActionType(ProjectActionType.TASK.getValue());
		parQuery.setTaskId(task.getId());
		ProjectActionRecord par = this.parMapper.selectOne(parQuery);
		par.setActionStartDate(startDate);
		par.setActionEndDate(endDate);
		rows = this.getActualDao().updateByPrimaryKeySelective(task);
		if (rows <= 0) {
			throw new TaskException("更新项目进程记录失败");
		}
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

	protected void updateTaskDetail(TaskDetails taskDetails, Task task) throws TaskException {
		// 标识任务已完成，需要写入项目中实际完成时间
		boolean isComplete = false;
		// 查询当前要修改的任务工时信息

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
			throw new TaskException("请填写工时");
		}

		int rows = taskDetailsService.insert(taskDetails);
		if (rows <= 0) {
			throw new TaskException("插入任务详情失败");
		}

		// 进度总量写入project表中
		saveProjectProgress(task, isComplete);

		// 更新版本表中的进度
		saveProjectVersion(task);

		// 更新阶段表中的进度
		saveProjectPhase(task);

		rows = this.saveOrUpdate(task);
		if (rows <= 0) {
			throw new TaskException("更新任务信息失败");
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

	private boolean isTaskTimeInCurrentWeek(Long taskId, Date taskDate) {
		WorkDay workDay = this.workDayService.getNowWeeklyWorkDay();
		if (workDay == null) {
			return false;
		}
		Task task = this.getActualDao().selectByPrimaryKey(taskId);
		long wStart = workDay.getWorkStartTime().getTime();
		long wEnd = workDay.getWorkEndTime().getTime();
		long tStart = task.getStartDate().getTime();
		long tEnd = task.getEndDate().getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		long current = 0;
		try {
			current = df.parse(df.format(new Date())).getTime();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		long taskTime = taskDate.getTime();
		// -------tStart---------wStart-------taskTime---------current---------tEnd----------wEnd-----------
		if (wStart >= tStart && wStart <= tEnd && wEnd >= tEnd && taskTime >= wStart && taskTime <= tEnd
				&& current >= taskTime && current <= tEnd) {
			return true;
		}
		// ----------wStart----------tStart--------------tEnd------------wEnd----------
		if (wStart <= tStart && wEnd >= tEnd && taskTime >= tStart && taskTime <= tEnd && current >= taskTime
				&& current <= tEnd) {
			return true;
		}
		// ----------wStart-------------tStart-------------wEnd-----------tEnd---------
		if (wStart <= tStart && wEnd >= tStart && wEnd <= tEnd && taskTime >= tStart && taskTime <= wEnd
				&& current >= taskTime && current <= wEnd) {
			return true;
		}
		// -------tStart--------wStart---------taskTime--------current----------wEnd----------tend-------------
		if (tStart <= wStart && tEnd >= wEnd && taskTime >= wStart && taskTime <= wEnd && current >= taskTime
				&& current <= wEnd) {
			return true;
		}
		return false;
	}

	// 计算阶段进度
	private void saveProjectPhase(Task task) throws TaskException {
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
		int rows = projectPhaseService.saveOrUpdate(projectPhase);
		if (rows <= 0) {
			throw new TaskException("更新阶段进度失败");
		}
	}

	// 计算项目总进度
	private void saveProjectProgress(Task task, boolean signComplate) throws TaskException {

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
		int rows = projectService.saveOrUpdate(project);
		if (rows <= 0) {
			throw new TaskException("更新项目进度失败");
		}
	}

	// 计算版本进度
	private void saveProjectVersion(Task task) throws TaskException {
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
		int rows = projectVersionService.saveOrUpdate(projectVersion);
		if (rows <= 0) {
			throw new TaskException("更新项目版本进度失败");
		}

	}

	private void sendTimeoutMail(Task task) {
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = new HashMap<>();

		metadata.put("projectId", "projectProvider");
		metadata.put("versionId", "projectVersionProvider");
		metadata.put("phaseId", "projectPhaseProvider");
		metadata.put("startDate", "dateProvider");
		metadata.put("endDate", "dateProvider");
		metadata.put("owner", "memberProvider");
		metadata.put("type", "taskTypeProvider");
		metadata.put("changeId", "projectChangeProvider");
		metadata.put("beforeTask", "taskProvider");

		try {
			Map<Long, User> userMap = AlmCache.getInstance().getUserMap();
			// 通知任务执行人
			User taskExecutor = userMap.get(task.getOwner());
			// 通知项目经理
			Project project = AlmCache.getInstance().getProjectMap().get(task.getProjectId());
			User projectManager = userMap.get(project.getProjectManager());
			Map viewModel = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(task)).get(0);
			// 构建邮件内容
			Template template = this.groupTemplate.getTemplate(this.contentTemplate);
			template.binding("task", viewModel);
			this.mailManager.sendMail(this.mailFrom, taskExecutor.getEmail(), template.render(), true, "任务超时提醒", null);
			this.mailManager.sendMail(this.mailFrom, projectManager.getEmail(), template.render(), true, "任务超时提醒",
					null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			Project project = AlmCache.getInstance().getProjectMap().get(task.getProjectId());
			dto.setProjectManagerId(project.getProjectManager());
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

}
