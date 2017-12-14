package com.dili.alm.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectPhaseMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.dao.TeamMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectEntity;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.TeamRole;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectDto;
import com.dili.alm.domain.dto.ProjectListDto;
import com.dili.alm.domain.dto.UploadProjectFileDto;
import com.dili.alm.exceptions.ProjectException;
import com.dili.alm.provider.ProjectProvider;
import com.dili.alm.rpc.DataAuthRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.TeamService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.SystemConfigUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.github.pagehelper.Page;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-10-18 17:22:54.
 */
@Service
public class ProjectServiceImpl extends BaseServiceImpl<Project, Long> implements ProjectService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);
	private static final String PROJECT_TYPE_CODE = "project_type";
	private static final String PROJECT_STATE_CODE = "project_state";
	private static final String PROJECT_FILE_TYPE_CODE = "project_file_type";

	@Autowired
	DataDictionaryService dataDictionaryService;
	@Autowired
	private ProjectVersionMapper projectVersionMapper;
	@Autowired
	private TeamMapper teamMapper;
	@Autowired
	DataAuthRpc dataAuthRpc;
	@Autowired
	private TeamService teamService;
	@Autowired
	private TaskMapper taskMapper;
	@Autowired
	private ProjectPhaseMapper phaseMapper;
	@Autowired
	private FilesMapper fileMapper;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private UserRpc userRPC;

	public ProjectMapper getActualDao() {
		return (ProjectMapper) getDao();
	}

	@Override
	public int update(Project condtion) {
		// 同步更新缓存
		if (StringUtils.isNotBlank(condtion.getName())) {
			AlmCache.PROJECT_MAP.get(condtion.getId()).setName(condtion.getName());
		}
		dataAuthRpc.updateDataAuth(condtion.getId().toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT,
				condtion.getName());
		return super.update(condtion);
	}

	@Override
	public int insert(Project project) {
		int i = super.insert(project);
		// 同步更新缓存
		AlmCache.PROJECT_MAP.put(project.getId(), project);
		// 向权限系统中添加项目数据权限
		String parentId = project.getParentId() == null ? null : project.getParentId().toString();
		dataAuthRpc.addDataAuth(project.getId().toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT, project.getName(),
				parentId);
		return i;
	}

	@Override
	public int insertSelective(Project project) {
		int i = super.insertSelective(project);
		// 同步更新缓存
		AlmCache.PROJECT_MAP.put(project.getId(), project);
		// 向权限系统中添加项目数据权限
		String parentId = project.getParentId() == null ? null : project.getParentId().toString();
		dataAuthRpc.addDataAuth(project.getId().toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT, project.getName(),
				parentId);
		return i;
	}

	@Override
	public int delete(Long id) {
		AlmCache.PROJECT_MAP.remove(id);
		dataAuthRpc.deleteDataAuth(id.toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT);
		return super.delete(id);
	}

	@Override
	public int delete(List<Long> ids) {
		ids.forEach(id -> {
			AlmCache.PROJECT_MAP.remove(id);
			dataAuthRpc.deleteDataAuth(id.toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT);
		});
		return super.delete(ids);
	}

	@Override
	public List<DataDictionaryValueDto> getPojectTypes() {
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_TYPE_CODE);
		if (dto == null) {
			return null;
		}
		return dto.getValues();
	}

	@Transactional
	@Override
	public BaseOutput<Object> deleteBeforeCheck(Long projectId) {
		ProjectVersion record = DTOUtils.newDTO(ProjectVersion.class);
		record.setProjectId(projectId);
		int count = this.projectVersionMapper.selectCount(record);
		if (count > 0) {
			return BaseOutput.failure("项目关联了里程碑，不能删除");
		}
		Team teamQuery = DTOUtils.newDTO(Team.class);
		teamQuery.setProjectId(projectId);
		List<Team> teams = this.teamMapper.select(teamQuery);
		if (CollectionUtils.isNotEmpty(teams)) {
			teams.forEach(t -> {
				this.teamService.delete(t.getId());
			});
		}

		Project projectQuery = DTOUtils.newDTO(Project.class);
		projectQuery.setParentId(projectId);
		count = this.getActualDao().selectCount(projectQuery);
		if (count > 0) {
			return BaseOutput.failure("项目包含子项目，不能删除");
		}
		count = this.delete(projectId);
		if (count > 0) {
			return BaseOutput.success("删除成功");
		}
		return BaseOutput.failure("删除失败");
	}

	@Override
	public List<Project> getChildProjects(Long id) {
		return getActualDao().getChildProjects(id);
	}

	@Override
	public EasyuiPageOutput listEasyuiPageByExample(Project domain, boolean useProvider) throws Exception {
		SessionContext sessionContext = SessionContext.getSessionContext();
		if (sessionContext == null) {
			throw new RuntimeException("未登录");
		}
		UserTicket user = sessionContext.getUserTicket();
		if (user == null) {
			throw new RuntimeException("未登录");
		}
		List<Project> list = this.listByExample(domain);
		List<ProjectListDto> dtos = DTOUtils.as(list, ProjectListDto.class);
		dtos.forEach(p -> {
			Team record = DTOUtils.newDTO(Team.class);
			record.setProjectId(p.getId());
			record.setMemberId(user.getId());
			record.setRole(TeamRole.PROJECT_MANAGER.getValue());
			int count = this.teamMapper.selectCount(record);
			p.setManager(count > 0);
		});
		long total = list instanceof Page ? ((Page) list).getTotal() : list.size();
		List results = null;
		results = useProvider ? ValueProviderUtils.buildDataByProvider(domain, dtos) : dtos;
		return new EasyuiPageOutput(Integer.parseInt(String.valueOf(total)), results);
	}

	@Transactional(rollbackFor = ProjectException.class)
	@Override
	public BaseOutput<Object> insertAfterCheck(Project project) throws ProjectException {
		Project record = DTOUtils.newDTO(Project.class);
		record.setName(project.getName());
		int count = this.getActualDao().selectCount(record);
		if (count > 0) {
			return BaseOutput.failure("已存在项目相同的项目");
		}
		int result = this.insertSelective(project);
		Team t = new DTOUtils().newDTO(Team.class);
		t.setProjectId(project.getId());
		t.setMemberId(project.getProjectManager());
		count = this.teamMapper.selectCount(t);
		if (project.getProjectManager() != null && count <= 0) {
			Team tt = DTOUtils.newDTO(Team.class);
			Date now = new Date();
			tt.setJoinTime(now);
			tt.setMemberId(project.getProjectManager());
			tt.setProjectId(project.getId());
			BaseOutput<Object> output = this.teamService.insertAfterCheck(tt);
			if (!output.isSuccess()) {
				throw new ProjectException(output.getResult());
			}
		}
		t.setMemberId(project.getProductManager());
		count = this.teamMapper.selectCount(t);
		if (project.getProductManager() != null && count <= 0) {
			Team tt = DTOUtils.newDTO(Team.class);
			Date now = new Date();
			tt.setJoinTime(now);
			tt.setMemberId(project.getProductManager());
			tt.setProjectId(project.getId());
			BaseOutput<Object> output = this.teamService.insertAfterCheck(tt);
			if (!output.isSuccess()) {
				throw new ProjectException(output.getResult());
			}
		}
		t.setMemberId(project.getTestManager());
		count = this.teamMapper.selectCount(t);
		if (project.getTestManager() != null && count <= 0) {
			Team tt = DTOUtils.newDTO(Team.class);
			Date now = new Date();
			tt.setJoinTime(now);
			tt.setMemberId(project.getTestManager());
			tt.setProjectId(project.getId());
			BaseOutput<Object> output = this.teamService.insertAfterCheck(tt);
			if (!output.isSuccess()) {
				throw new ProjectException(output.getResult());
			}
		}
		if (result > 0) {
			return BaseOutput.success().setData(project);
		}
		return BaseOutput.failure();
	}

	@Transactional
	@Override
	public BaseOutput<Object> updateAfterCheck(Project project) {
		int result;
		Project record = DTOUtils.newDTO(Project.class);
		record.setName(project.getName());
		Project oldProject = this.getActualDao().selectOne(record);
		if (oldProject != null && !oldProject.getId().equals(project.getId())) {
			return BaseOutput.failure("存在相同名称的项目");
		}
		project.setModified(new Date());

		result = this.updateSelective(project);
		// 刷新projectProvider缓存
		AlmCache.PROJECT_MAP.put(project.getId(), project);

		dataAuthRpc.updateDataAuth(project.getId().toString(), AlmConstants.DATA_AUTH_TYPE_PROJECT, project.getName());
		if (result > 0) {
			return BaseOutput.success().setData(project);
		}
		return BaseOutput.failure();
	}

	@Override
	public List<DataDictionaryValueDto> getProjectStates() {
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_STATE_CODE);
		if (dto == null) {
			return null;
		}
		return dto.getValues();
	}

	public EasyuiPageOutput listPageMyProject(Project project) {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			throw new RuntimeException("未登录");
		}
		int total = this.getActualDao().getPageByProjectCount(userTicket.getId());
		List<Project> projectList = this.getActualDao().getProjectsByTaskOwner(userTicket.getId());
		List<ProjectDto> projectDtoList = new ArrayList<ProjectDto>();
		if (projectList != null && projectList.size() > 0) {
			for (Project project1 : projectList) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				ProjectDto projectDto = new ProjectDto();
				projectDto.setId(project1.getId());
				projectDto.setSerialNumber(project1.getSerialNumber());
				projectDto.setName(project1.getName());
				projectDto.setType(project1.getType());
				projectDto
						.setStartToEndDate(sdf.format(project1.getCreated()) + "至" + sdf.format(project1.getEndDate()));
				projectDto.setActualStartTime(sdf.format(project1.getActualStartDate()));
				projectDto.setProjectState(project1.getProjectState());
				projectDto.setTaskCount(project1.getTaskCount());
				projectDto.setMemberCount(project1.getMemberCount());
				projectDto.setCompletedProgress(project1.getCompletedProgress());
				projectDto.setOriginator(project1.getOriginator());
				projectDtoList.add(projectDto);
			}
		}
		@SuppressWarnings("unchecked")
		Map<Object, Object> metadata = null == project.getMetadata() ? new HashMap<>() : project.getMetadata();

		JSONObject projectStatusProvider = new JSONObject();
		projectStatusProvider.put("provider", "projectStatusProvider");
		metadata.put("status", projectStatusProvider);

		JSONObject projectTypeProvider = new JSONObject();
		projectTypeProvider.put("provider", "projectTypeProvider");
		metadata.put("type", projectTypeProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("originator", memberProvider);

		JSONObject provider = new JSONObject();
		provider.put("provider", "datetimeProvider");
		metadata.put("validTimeBegin", provider);
		metadata.put("validTimeEnd", provider);
		metadata.put("created", provider);
		metadata.put("modified", provider);
		project.setMetadata(metadata);
		try {
			List list = ValueProviderUtils.buildDataByProvider(project, projectDtoList);
			return new EasyuiPageOutput(total, list);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<DataDictionaryValueDto> getFileTypes() {
		DataDictionaryDto dto = this.dataDictionaryService.findByCode(PROJECT_FILE_TYPE_CODE);
		if (dto == null) {
			return null;
		}
		return dto.getValues();
	}

	@Override
	public Map<Object, Object> getDetailViewData(Long id) {
		Project project = this.getActualDao().selectByPrimaryKey(id);
		ProjectEntity viewData = DTOUtils.toEntity(project, ProjectEntity.class, true);
		Task taskQuery = DTOUtils.newDTO(Task.class);
		taskQuery.setProjectId(id);
		Integer taskCount = this.taskMapper.selectCount(taskQuery);
		viewData.setTaskCount(taskCount);
		Integer memberCount = this.teamMapper.countProjectMember(id);
		viewData.setMemberCount(memberCount);
		try {
			return ProjectProvider.parseEasyUiModel(viewData);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Transactional
	@Override
	public BaseOutput<Object> uploadFileAndSendMail(UploadProjectFileDto dto)
			throws MessagingException, InterruptedException {
		List<Files> target = new ArrayList<>(dto.getFileIds().size());
		dto.getFileIds().forEach(fid -> {
			Files files = this.fileMapper.selectByPrimaryKey(fid);
			files.setProjectId(dto.getProjectId());
			files.setVersionId(dto.getVersionId());
			files.setPhaseId(dto.getPhaseId());
			files.setType(dto.getType());
			files.setNotes(dto.getNotes());
			this.fileMapper.updateByPrimaryKey(files);
			target.add(files);
		});
		if (dto.getSendMail()) {
			this.sendMail(dto, target);
		}
		List<Map> listModel;
		try {
			listModel = this.parseEasyUiListModel(target);
		} catch (Exception e) {
			return BaseOutput.failure(e.getMessage());
		}
		return BaseOutput.success().setData(listModel);
	}

	private void sendMail(UploadProjectFileDto dto, List<Files> attachments)
			throws MessagingException, InterruptedException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		String from = SystemConfigUtils.getProperty("spring.mail.username");
		helper.setFrom(from);
		Project project = this.getActualDao().selectByPrimaryKey(dto.getProjectId());
		BaseOutput<User> output = this.userRPC.findUserById(dto.getReceiver());
		if (output == null || !output.isSuccess()) {
			throw new MessagingException("收件人不存在");
		}
		User receiver = output.getData();
		if (receiver == null) {
			throw new MessagingException("收件人不存在");
		}
		helper.setTo(receiver.getEmail());
		helper.setSubject("主题：" + project.getName() + "有新的文件上传");
		StringBuilder sb = new StringBuilder();
		ProjectVersion version = this.projectVersionMapper.selectByPrimaryKey(dto.getVersionId());
		ProjectPhase phase = this.phaseMapper.selectByPrimaryKey(dto.getPhaseId());
		sb.append("项目名称：").append(project.getName()).append(version.getVersion()).append("\r\n阶段名称：")
				.append(AlmCache.PHASE_NAME_MAP.get(phase.getName()));
		helper.setText(sb.toString());
		for (Files files : attachments) {
			FileSystemResource file = new FileSystemResource(
					new File(FilesService.MILESTONES_PATH_PREFIX + files.getName()));
			helper.addAttachment(files.getName(), file);
		}
		Thread.sleep(2000);
		mailSender.send(mimeMessage);
	}

	private List<Map> parseEasyUiListModel(List<? extends Files> list) throws Exception {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject fileTypeProvider = new JSONObject();
		fileTypeProvider.put("provider", "fileTypeProvider");
		metadata.put("type", fileTypeProvider);

		JSONObject projectVersionProvider = new JSONObject();
		projectVersionProvider.put("provider", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);

		JSONObject projectPhaseProvider = new JSONObject();
		projectPhaseProvider.put("provider", "projectPhaseProvider");
		metadata.put("phaseId", projectPhaseProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("createMemberId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("created", datetimeProvider);
		return ValueProviderUtils.buildDataByProvider(metadata, list);
	}
}