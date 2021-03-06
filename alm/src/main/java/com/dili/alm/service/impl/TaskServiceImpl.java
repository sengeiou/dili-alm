package com.dili.alm.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskDetailsMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.dao.TeamMapper;
import com.dili.alm.domain.ActionDateType;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectAction;
import com.dili.alm.domain.ProjectActionRecord;
import com.dili.alm.domain.ProjectActionType;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectState;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.TaskEntity;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.TeamRole;
import com.dili.alm.domain.WorkDay;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.apply.ApplyMajorResource;
import com.dili.alm.domain.dto.apply.ApplyRelatedResource;
import com.dili.alm.exceptions.ApplicationException;
import com.dili.alm.exceptions.ProjectException;
import com.dili.alm.exceptions.ProjectVersionException;
import com.dili.alm.exceptions.TaskException;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectChangeService;
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
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.domain.dto.UserQuery;
import com.dili.uap.sdk.rpc.RoleRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;
import com.github.pagehelper.Page;

import tk.mybatis.mapper.entity.Example;

/**
 * ???MyBatis Generator?????????????????? This file was generated on 2017-11-23 10:23:05.
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
	@Autowired
	private TaskDetailsMapper taskDetailsMapper;

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
	public void addTask(Task task, Short planTime, Date startDateShow, Date endDateShow, Boolean flow, Long creatorId) throws TaskException {
		// ???????????????????????????????????????
		this.checkProjectState(task);
		// ????????????????????????????????????40
		if (planTime > 40) {
			throw new TaskException("?????????????????????????????????40");
		}
		// ???????????????????????????????????????
		if (!this.isThisProjectManager(task.getProjectId())) {
			throw new TaskException("???????????????????????????????????????????????????");
		}
		Project project = this.projectMapper.selectByPrimaryKey(task.getProjectId());
		if (project == null) {
			throw new TaskException("???????????????");
		}
		project.setTaskCount(project.getTaskCount() + 1);
		int rows = this.projectMapper.updateByPrimaryKey(project);
		if (rows <= 0) {
			throw new TaskException("????????????????????????");
		}

		task.setPlanTime(planTime);
		task.setStartDate(startDateShow);
		task.setEndDate(endDateShow);
		task.setFlow(flow);
		task.setCreateMemberId(creatorId);
		task.setCreated(new Date());
		task.setStatus(TaskStatus.NOTSTART.code);// ???????????????????????????0???????????????
		rows = this.insertSelective(task);
		if (rows <= 0) {
			throw new TaskException("??????????????????");
		}

		// ??????????????????
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
			throw new TaskException("???????????????");
		}
		Project project = this.projectMapper.selectByPrimaryKey(task.getProjectId());
		if (project == null) {
			throw new TaskException("???????????????");
		}
		if (!task.getOwner().equals(operatorId) && !project.getProjectManager().equals(operatorId)) {
			throw new TaskException("????????????????????????????????????");
		}
		Date now = new Date();
		task.setStatus(TaskStatus.COMPLETE.code);// ?????????????????????
		task.setModified(now);
		task.setModifyMemberId(operatorId);
		task.setFactEndDate(now);
		int rows = this.getActualDao().updateByPrimaryKeySelective(task);
		if (rows <= 0) {
			throw new TaskException("??????????????????");
		}
		project.setActualEndDate(now);
		rows = this.projectMapper.updateByPrimaryKeySelective(project);
		if (rows <= 0) {
			throw new TaskException("??????????????????");
		}
	}

	@Override
	public void deleteTask(Long id) throws TaskException {
		Task task = this.getActualDao().selectByPrimaryKey(id);

		this.checkProjectState(task);

		Project project = this.projectMapper.selectByPrimaryKey(task.getProjectId());
		if (project == null) {
			throw new RuntimeException("???????????????");
		}

		if (!this.isCreater(task)) {
			throw new TaskException("????????????????????????????????????????????????");
		}

		// ?????????????????????
		project.setTaskCount(project.getTaskCount() - 1);

		int rows = this.projectMapper.updateByPrimaryKey(project);
		if (rows <= 0) {
			throw new TaskException("??????????????????????????????");
		}

		// ????????????????????????
		ProjectActionRecord parQuery = DTOUtils.newDTO(ProjectActionRecord.class);
		parQuery.setTaskId(task.getId());
		this.parMapper.delete(parQuery);

		rows = this.getActualDao().deleteByPrimaryKey(id);
		if (rows <= 0) {
			throw new TaskException("??????????????????");
		}
	}

	public TaskMapper getActualDao() {
		return (TaskMapper) getDao();
	}

	/*
	 * ??????
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
	 * ??????????????????????????????
	 */
	@Override
	public boolean isCreater(Task task) {
		Task taskSelect = this.get(task.getId());
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("?????????");
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
	 * ???????????????????????????
	 */
	@Override
	public boolean isProjectManager(Long projectId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("?????????");
		}
		Team team = DTOUtils.newDTO(Team.class);
		team.setProjectId(projectId);
		team.setMemberId(userTicket.getId());
		List<Team> teamList = teamService.list(team);

		if (CollectionUtils.isEmpty(teamList)) {
			return false;
		}

		for (Team team2 : teamList) {
			if (team2.getRole().equalsIgnoreCase(TeamRole.PROJECT_MANAGER.getValue())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ????????????????????????????????????????????????8??????
	 */
	@Override
	public boolean isSetTask(Long id, short taskHour, String modified) {
		// ??????????????????????????????????????????
		int totalTaskHour = restTaskHour(id, modified);

		if (totalTaskHour != 0) {

			totalTaskHour += taskHour;
			// ???????????????????????????8??????
			if (totalTaskHour > 8) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ??????????????????
	 */
	@Override
	public boolean isThisProjectManager(Long projectId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("?????????");
		}
		Team team = DTOUtils.newDTO(Team.class);
		team.setMemberId(userTicket.getId());
		team.setProjectId(projectId);

		List<Team> teamList = teamService.list(team);

		if (CollectionUtils.isEmpty(teamList)) {
			return false;
		}

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
	public EasyuiPageOutput listByTeam(Task task) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("?????????");
		}

		if (!WebUtil.strIsEmpty(task.getName())) {
			String replaceAll = task.getName().replaceAll(" ", "");
			task.setName(replaceAll);
		}
		// List<Task> list = taskMapper.selectByTeam(task, userTicket.getId(), ids);
		// ????????????
		Page<Task> list = (Page<Task>) this.listByExample(task);
		@SuppressWarnings("unchecked")
		Map<String, Object> metadata = null == task.getMetadata() ? new HashMap<>() : task.getMetadata();

		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);

		JSONObject projectVersionProvider = new JSONObject();
		projectVersionProvider.put("provider", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("owner", memberProvider);

		JSONObject taskStateProvider = new JSONObject();
		taskStateProvider.put("provider", "taskStateProvider");
		metadata.put("status", taskStateProvider);

		JSONObject taskTypeProvider = new JSONObject();
		taskTypeProvider.put("provider", "taskTypeProvider");
		metadata.put("type", taskTypeProvider);

		task.mset(metadata);
		try {
			List<TaskEntity> results = this.TaskParseTaskSelectDto(list, false);// ??????????????????DTO
			List taskList = ValueProviderUtils.buildDataByProvider(task, results);
			EasyuiPageOutput taskEasyuiPageOutput = new EasyuiPageOutput(Long.valueOf(list.getTotal()), taskList);
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
		List<Task> list = this.listByExample(task);// ????????????
		Page<Task> page = (Page<Task>) list;
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = null == task.getMetadata() ? new HashMap<>() : task.getMetadata();

		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);

		JSONObject projectVersionProvider = new JSONObject();
		projectVersionProvider.put("projectVersion", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);

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
			List<TaskEntity> results = this.TaskParseTaskSelectDto(list, true);// ??????????????????DTO
			List taskList = ValueProviderUtils.buildDataByProvider(task, results);
			EasyuiPageOutput taskEasyuiPageOutput = new EasyuiPageOutput(page.getTotal(), taskList);
			return new EasyuiPageOutput(page.getTotal(), taskList);
		} catch (Exception e) {
			e.printStackTrace();
			return new EasyuiPageOutput(0L, Collections.emptyList());
		}
	}

	@Override
	public List<ProjectVersion> listProjectVersionByTeam() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("?????????");
		}
		return taskMapper.selectVersionByTeam(userTicket.getId());
	}

	@Override
	public List<Task> listTaskByProjectId(Long projectId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("?????????");
		}
		Task task = DTOUtils.newDTO(Task.class);
		task.setProjectId(projectId);
		return taskMapper.selectByTeam(task, userTicket.getId(), null);
	}

	/**
	 * ????????????ID????????????
	 */
	@Override
	public List<User> listUserByProjectId(Long projectId) {
		Team team = DTOUtils.newDTO(Team.class);
		team.setProjectId(projectId);
		List<Long> resultIds = teamMapper.selectByProjectId(projectId);
		List<User> userList = new ArrayList<User>();
		for (Long userId : resultIds) {
			User user = userRpc.findUserById(userId).getData();
			if(user != null) {
				userList.add(user);
			}
		}
		return userList;
	}

	@Override
	public List<User> listUserByTeam() {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("?????????");
		}

		List<User> userList = new ArrayList<User>();
		UserQuery user = DTOUtils.newDTO(UserQuery.class);
		user.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		userList = userRpc.listByExample(user).getData();

		return userList;
	}

	/**
	 * ?????????????????????
	 */
	@Override
	public void notComplateTask() {
		// ?????????????????? ????????????????????????
		Example example = new Example(Task.class);
		example.createCriteria().andEqualTo("status", TaskStatus.START.getCode());
		List<Task> taskList = this.getActualDao().selectByExample(example);
		long now = System.currentTimeMillis();
		for (Task taskDome : taskList) {
			// ?????????????????????????????????????????????
			// dateUtil ????????????????????????0???????????????????????????
			if (now - taskDome.getEndDate().getTime() >= 24 * 60 * 60 * 1000) {
				taskDome.setStatus(TaskStatus.NOTCOMPLETE.code);// ????????????????????????
				taskDome.setModified(new Date());
				this.getActualDao().updateByPrimaryKeySelective(taskDome);
				this.sendTimeoutMail(taskDome);
			}
		}
	}

	/**
	 * ?????????????????????????????????????????????????????????????????????flase ???????????????
	 */
	@Override
	public List<TaskDetails> otherProjectTaskHour(String updateDate, Long ownerId) {

		// ????????????????????????????????????detail
		List<TaskDetails> taskDetails = taskMapper.selectOtherTaskDetail(ownerId, updateDate);

		return taskDetails;
	}

	@Override
	public List<ProjectChange> projectChangeList(Long projectId) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("?????????");
		}
		return taskMapper.selectProjectChangeByTeam(userTicket.getId(), projectId);
	}

	/*
	 * ?????????????????????
	 */
	@Override
	public int restTaskHour(Long ownerId, String updateDate) {

		int dayTotal = 0;

		// ?????????????????????????????????
		List<TaskDetails> tdList = this.otherProjectTaskHour(updateDate, ownerId);

		// ????????????????????????
		if (tdList == null || tdList.size() == 0) {
			return 0;
		}
		// ????????????????????????????????????????????????????????????8??????
		for (TaskDetails taskDetails : tdList) {

			// int getInt = taskHoursPlus(taskDetails);
			dayTotal += taskDetails.getTaskHour();// ??????????????????????????????

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
				taskHour += entity.getTaskHour();// ??????????????????
				overHour += entity.getOverHour();// ??????????????????
			}
		}
		taskDetails.setTaskHour(taskHour);
		taskDetails.setOverHour(overHour);

		return taskDetails;
	}

	@Transactional(rollbackFor = ApplicationException.class)
	@Override
	public void startTask(Long taskId, Long modifierId) throws TaskException, ProjectVersionException, ProjectException {

		// ????????????
		Task task = this.getActualDao().selectByPrimaryKey(taskId);
		if (task == null) {
			throw new TaskException("???????????????");
		}

		// ?????????????????????
		task.setFactBeginDate(new Date());
		task.setStatus(TaskStatus.START.code);// ???????????????????????????
		Date now = new Date();

		// ????????????
		this.projectService.start(task.getProjectId());

		// ????????????
		this.projectVersionService.start(task.getVersionId());

		/*** ?????????????????????????????? ***/
		// ??????????????????project??????
		saveProjectProgress(task, false);

		// ???????????????????????????
		saveProjectVersion(task);

		/*** ?????????????????????????????? ***/

		int rows = this.update(task);
		if (rows <= 0) {
			throw new TaskException("??????????????????");
		}
	}

	@Override
	public Long submitWorkingHours(Long taskId, Long operator, Date taskDate, Short taskHour, Short overHour, String content) throws TaskException {

		// ???????????????????????????
		if (taskHour <= 0 && overHour <= 0) {
			throw new TaskException("???????????????");
		}

		// ??????????????????????????????
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date now = new Date();
		try {
			if (sdf.parse(sdf.format(taskDate)).getTime() > sdf.parse(sdf.format(now)).getTime()) {
				throw new TaskException("?????????????????????????????????");
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		Task task = this.getActualDao().selectByPrimaryKey(taskId);

		// ????????????????????????
		boolean isOwner = operator.equals(task.getOwner());
		// ?????????????????????
		boolean isManager = this.isProjectManager(task.getProjectId());

		// ?????????????????????????????????
		if (!isManager && !isOwner) {
			throw new TaskException("???????????????????????????????????????????????????????????????????????????");
		}

		// ????????????????????????
		boolean isCurrentWeek = this.isTaskTimeInCurrentWeek(taskId, taskDate);

		// ????????????????????????????????????????????????????????????????????????????????????????????????
		if (!isManager && !isCurrentWeek) {
			throw new TaskException("????????????????????????????????????");
		}

		String executeDateStr = sdf.format(taskDate);

		// ?????????8????????????
		if (taskHour > 0 && !this.isSetTask(task.getOwner(), taskHour, executeDateStr)) {
			throw new TaskException("???????????????????????????8?????????");
		}
		TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
		taskDetails.setTaskId(taskId);
		taskDetails.setTaskHour(taskHour);
		taskDetails.setOverHour(overHour);
		taskDetails.setDescribe(content);
		taskDetails.setCreateMemberId(operator);// ?????????
		taskDetails.setCreated(now);// ????????????????????????
		taskDetails.setModified(taskDate);// ?????????
		taskDetails.setModifyMemberId(task.getOwner());// ?????????
		this.updateTaskDetail(taskDetails, task);// ????????????
		return taskDetails.getId();
	}

	@Override
	public void updateTask(Task task, Long modifyMemberId, Short planTime, Date startDate, Date endDate, Boolean flow) throws TaskException {
		// ???????????????????????????????????????
		this.checkProjectState(task);
		// ????????????????????????????????????40
		if (planTime > 40) {
			throw new TaskException("?????????????????????????????????40");
		}
		if (!this.isCreater(task)) {
			throw new TaskException("????????????????????????????????????????????????");
		}
		// ??????????????????????????????????????????
		task.setModified(new Date());
		task.setModifyMemberId(modifyMemberId);
		task.setPlanTime(planTime);
		task.setStartDate(startDate);
		task.setEndDate(endDate);
		task.setFlow(flow);

		int rows = this.getActualDao().updateByPrimaryKeySelective(task);
		if (rows <= 0) {
			throw new TaskException("??????????????????");
		}

		// ??????????????????
		ProjectActionRecord parQuery = DTOUtils.newDTO(ProjectActionRecord.class);
		parQuery.setActionCode(ProjectAction.TASK_PLAN.getCode());
		parQuery.setActionType(ProjectActionType.TASK.getValue());
		parQuery.setTaskId(task.getId());
		ProjectActionRecord par = this.parMapper.selectOne(parQuery);
		par.setActionStartDate(startDate);
		par.setActionEndDate(endDate);
		rows = this.getActualDao().updateByPrimaryKeySelective(task);
		if (rows <= 0) {
			throw new TaskException("??????????????????????????????");
		}
	}

	@Override
	public boolean validateBeginAndEnd(Long projectId, Date startDate, Date endDate) {
		try {
			Project project = projectService.get(projectId);
			Date pstDate = project.getStartDate();// ??????????????????
			Date penDate = project.getEndDate();// ??????????????????
			int startCompareVal = Integer.parseInt(DateUtil.getDatePoor(pstDate, startDate));
			int endCompareVal = Integer.parseInt(DateUtil.getDatePoor(penDate, endDate));

			// ??????0?????????????????????0 ?????????
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
		// ???????????????????????????????????????????????????????????????
		boolean isComplete = false;
		// ??????????????????????????????????????????

		TaskDetails condtion = DTOUtils.newDTO(TaskDetails.class);

		condtion.setTaskId(task.getId());

		List<TaskDetails> list = this.taskDetailsService.list(condtion);

		int totalTaskHours = 0;

		for (TaskDetails entity : list) {

			totalTaskHours += entity.getTaskHour();
		}

		if (totalTaskHours >= task.getPlanTime()) {
			// ?????????????????????
			/* task.setStatus(TaskStatus.COMPLETE.code); */
			task.setFactEndDate(new Date());
			isComplete = true;
		}

		if (taskDetails.getTaskHour() == 0 && taskDetails.getOverHour() == 0) {// ????????????????????????????????????????????????????????????
			throw new TaskException("???????????????");
		}

		int rows = taskDetailsService.insert(taskDetails);
		if (rows <= 0) {
			throw new TaskException("????????????????????????");
		}

		// ??????????????????project??????
		saveProjectProgress(task, isComplete);

		// ???????????????????????????
		saveProjectVersion(task);

		rows = this.saveOrUpdate(task);
		if (rows <= 0) {
			throw new TaskException("????????????????????????");
		}

	}

	private void checkProjectState(Task task) throws TaskException {
		Project project = this.projectMapper.selectByPrimaryKey(task.getProjectId());
		if (project == null) {
			throw new TaskException("???????????????");
		}
		if (!project.getProjectState().equals(ProjectState.NOT_START.getValue()) && !project.getProjectState().equals(ProjectState.IN_PROGRESS.getValue())) {
			throw new TaskException("??????????????????????????????????????????");
		}
		ProjectVersion version = this.versionMapper.selectByPrimaryKey(task.getVersionId());
		if (version == null) {
			throw new TaskException("???????????????");
		}
		if (!version.getVersionState().equals(ProjectState.NOT_START.getValue()) && !version.getVersionState().equals(ProjectState.IN_PROGRESS.getValue())) {
			throw new TaskException("??????????????????????????????????????????");
		}
	}

	/**
	 * data ?????????String
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
		if (project.getProjectState().equals(PROJECT_STATE_SHUT) || project.getProjectState().equals(PROJECT_STATE_COMPLATE)) {
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
		if (tStart <= wStart && wStart <= taskTime && taskTime <= current && current <= tEnd && tEnd <= wEnd) {
			return true;
		}
		// ----------wStart----------tStart-------------taskTime-----------current-----------tEnd------------wEnd----------
		if (wStart <= tStart && tStart <= taskTime && taskTime <= current && current <= tEnd && tEnd <= wEnd) {
			return true;
		}
		// ----------wStart-----------tStart---------taskTime-------------current---------------wEnd-----------tEnd---------
		if (wStart <= tStart && tStart <= taskTime && taskTime <= current && current <= wEnd && wEnd <= tEnd) {
			return true;
		}
		// -------tStart--------wStart---------taskTime--------current----------wEnd----------tend-------------
		if (tStart <= wStart && wStart <= taskTime && taskTime <= current && current <= wEnd && wEnd <= tEnd) {
			return true;
		}
		// --------wStart--------tStart----------taskTime--------tEnd-----------current-------wEnd-----------
		if (wStart <= tStart && tStart <= taskTime && taskTime <= tEnd && tEnd <= current && current <= wEnd) {
			return true;
		}
		// ---------tStart--------wStart----------taskTime--------tEnd-----------current-------wEnd-----------
		if (tStart <= wStart && wStart <= taskTime && taskTime <= tEnd && tEnd <= current && current <= wEnd) {
			return true;
		}
		return false;
	}

	// ?????????????????????
	private void saveProjectProgress(Task task, boolean signComplate) throws TaskException {

		int progress = 0;

		Project project = projectService.get(task.getProjectId());
		// ????????????????????????
		ProjectApply projectApply = projectApplyService.get(project.getApplyId());

		ApplyMajorResource applyMajorResource = JSON.parseObject(Optional.ofNullable(projectApply.getResourceRequire()).orElse("{}"), ApplyMajorResource.class);

		// ????????????????????????
		double total = Optional.ofNullable(applyMajorResource.getMainWorkTime()).orElse(0) * 8;

		List<ApplyRelatedResource> list = Optional.ofNullable(applyMajorResource.getRelatedResources()).orElse(new ArrayList<ApplyRelatedResource>());

		for (int i = 0; i < list.size(); i++) {
			ApplyRelatedResource applyRelatedResource = list.get(i);
			total += Optional.ofNullable(applyRelatedResource.getRelatedWorkTime()).orElse(0) * 8;
		}

		// ???????????????????????????
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
		// ???????????????????????????????????????
		Long taskTimes = this.taskDetailsMapper.sumTaskHourByProject(task.getProjectId());
		taskTimes = taskTimes == null ? 0 : taskTimes;

		progress = (int) ((taskTimes / total) * 100);
		project.setCompletedProgress(progress);

		// ?????????true????????????????????????
		if (signComplate) {
			project.setActualEndDate(new Date());
		}
		int rows = projectService.saveOrUpdate(project);
		if (rows <= 0) {
			throw new TaskException("????????????????????????");
		}
	}

	// ??????????????????
	private void saveProjectVersion(Task task) throws TaskException {
		// ?????????????????? ??????????????????/??????????????????
		ProjectVersion projectVersion = projectVersionService.get(task.getVersionId());

		int progress = 0;
		double totalPlanTime = this.taskMapper.sumPlanTimeByVersion(task.getVersionId());
		Long totalTaskTime = this.taskDetailsMapper.sumTaskAndOverHourByVersion(task.getVersionId());
		totalTaskTime = totalTaskTime == null ? 0 : totalTaskTime;

		progress = (int) ((totalTaskTime / totalPlanTime) * 100);
		projectVersion.setCompletedProgress(progress);
		int rows = projectVersionService.saveOrUpdate(projectVersion);
		if (rows <= 0) {
			throw new TaskException("??????????????????????????????");
		}

	}

	private void sendTimeoutMail(Task task) {
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = new HashMap<>();

		metadata.put("projectId", "projectProvider");
		metadata.put("versionId", "projectVersionProvider");
		metadata.put("startDate", "dateProvider");
		metadata.put("endDate", "dateProvider");
		metadata.put("owner", "memberProvider");
		metadata.put("type", "taskTypeProvider");
		metadata.put("changeId", "projectChangeProvider");
		metadata.put("beforeTask", "taskProvider");

		try {
			Map<Long, User> userMap = AlmCache.getInstance().getUserMap();
			// ?????????????????????
			User taskExecutor = userMap.get(task.getOwner());
			// ??????????????????
			Project project = AlmCache.getInstance().getProjectMap().get(task.getProjectId());
			User projectManager = userMap.get(project.getProjectManager());
			Map viewModel = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(task)).get(0);
			// ??????????????????
			Template template = this.groupTemplate.getTemplate(this.contentTemplate);
			template.binding("task", viewModel);
			this.mailManager.sendMail(this.mailFrom, taskExecutor.getEmail(), template.render(), true, "??????????????????", null);
			this.mailManager.sendMail(this.mailFrom, projectManager.getEmail(), template.render(), true, "??????????????????", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * DTO??????
	 * 
	 * @param results
	 * @return
	 */
	private List<TaskEntity> TaskParseTaskSelectDto(List<Task> results, boolean isUpdateDetail) {
		List<TaskEntity> target = new ArrayList<>(results.size());
		for (Task task : results) {
			TaskEntity dto = new TaskEntity(task);
//			TaskEntity dto = BeanConver.copyBean(task, TaskEntity.class);
			// ?????????????????????????????????
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

			// ??????
			dto.setFlowStr(task.getFlow() ? "????????????" : "????????????");
			// ????????????
			String planDays = this.dateToString(task.getStartDate()) + "???" + this.dateToString(task.getEndDate());
			dto.setPlanDays(planDays);

			TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
			taskDetails.setTaskId(task.getId());
			// ???????????????
			List<TaskDetails> taskDetailsList = tdMapper.select(taskDetails);
			double totalTaskHour = 0;
			for (TaskDetails entity : taskDetailsList) {// ????????????
				if (entity.getTaskHour() != null) {// ???????????????????????????
					totalTaskHour += entity.getTaskHour();
				}
				/*
				 * if(entity.getOverHour() != null){ //???????????????????????????????????? totalTaskHour +=
				 * entity.getTaskHour(); }
				 */
			}
			// ??????=???????????????/????????????*100
			double taskHover = (totalTaskHour / task.getPlanTime()) * 100;
			dto.setProgress((int) taskHover);
			UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
			if (isProjeactComplate(task.getProjectId())) {// TODO:?????????????????????????????????
				dto.setUpdateDetail(false);
			} else {
				if (task.getStatus().equals(TaskStatus.COMPLETE.getCode())) {
					dto.setUpdateDetail(false);
				} else {
					if (this.isProjectManager(task.getProjectId()) || userTicket.getId().equals(task.getOwner())) {
						dto.setUpdateDetail(true);
					} else {
						dto.setUpdateDetail(false);
					}
				}
			}

			if (!isProjeactComplate(task.getProjectId()) && isProjectManager(task.getProjectId())) {
				dto.setCopyButton(true);
			} else {
				dto.setCopyButton(false);
			}
			target.add(dto);
		}
		return target;
	}

}
