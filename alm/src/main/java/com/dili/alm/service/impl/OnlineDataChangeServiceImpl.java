package com.dili.alm.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.component.BpmcUtil;
import com.dili.alm.component.MailManager;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.constant.BpmConsts;
import com.dili.alm.dao.EmailAddressMapper;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.OnlineDataChangeMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.TeamMapper;
import com.dili.alm.domain.EmailAddress;
import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.ProjectOnlineOperationRecord;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Team;
/*import com.dili.alm.domain.TaskDto;*/
/*import com.dili.alm.domain.TaskMapping;*/
import com.dili.alm.domain.dto.OnlineDataChangeBpmcDtoDto;
import com.dili.alm.exceptions.OnlineDataChangeException;
import com.dili.alm.service.OnlineDataChangeLogService;
import com.dili.alm.service.OnlineDataChangeService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.Assignment;
import com.dili.bpmc.sdk.dto.GroupUserDto;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.dto.TaskIdentityDto;
import com.dili.bpmc.sdk.rpc.RuntimeRpc;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.BeanConver;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.dto.RoleUserDto;
import com.dili.uap.sdk.rpc.DepartmentRpc;
import com.dili.uap.sdk.rpc.RoleRpc;
import com.dili.uap.sdk.rpc.UserRpc;
import com.dili.uap.sdk.session.SessionContext;

/**
 * MyBatis Generator This file was generated on 2019-12-25 18:22:44.
 */
//@Transactional(rollbackFor = Exception.class)
@Transactional(rollbackFor = OnlineDataChangeException.class)
@Service
public class OnlineDataChangeServiceImpl extends BaseServiceImpl<OnlineDataChange, Long> implements OnlineDataChangeService {

	@Autowired
	private TaskRpc tasksRpc;

	@Autowired
	private RuntimeRpc runtimeRpc;
	@Autowired
	private BpmcUtil bpmcUtil;

	@Autowired
	private UserRpc userRpc;

	@Autowired
	private RoleRpc roleRpc;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private OnlineDataChangeMapper onlineDataChangeMapper;
	

	
	private String contentTemplate;
	
	@Qualifier("mailContentTemplate")
	@Autowired
	private GroupTemplate groupTemplate;
	@Value("${spring.mail.username:}")
	private String mailFrom;
	@Autowired
	private MailManager mailManager;
	@Value("${uap.contextPath:http://uap.diligrp.com}")
	private String uapContextPath;
	
	@Autowired
	private TeamMapper teamMapper;
	@Autowired
	private DepartmentRpc deptRpc;
	@Autowired
	private EmailAddressMapper emailAddressMapper;
	@Autowired
	private   ProjectMapper  projectMapper;
	
	@Autowired
	private   OnlineDataChangeLogService    onlineDataChangeLogService;
	
    @Autowired
	ProjectVersionService projectVersionService;
    @Autowired
	private TaskRpc taskRpc;

	public OnlineDataChangeMapper getActualDao() {
		return (OnlineDataChangeMapper) getDao();
	}

	public OnlineDataChangeServiceImpl() {
		InputStream in = null;
		try {
			Resource res = new ClassPathResource("conf/dataChange.html");
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
	public void sendMail(OnlineDataChange onlineDataChange, String subject, Set<String> emails) {
		
	    OnlineDataChange  odc=  this.get(onlineDataChange.getId());
		// 构建邮件内容
		Template template = this.groupTemplate.getTemplate(this.contentTemplate);
		template.binding("odc", buildApplyViewModel( odc));
  		template.binding("applyUserIdName",userRpc.findUserById(odc.getApplyUserId()).getData().getRealName());
  	  
	    Project  proId=  projectMapper.selectByPrimaryKey(odc.getProjectId());
	    ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setProjectId(odc.getProjectId());
		List<ProjectVersion> list = projectVersionService.list(projectVersion);
		if(list!=null&&list.size()>0) {
			for (ProjectVersion projectVersion2 : list) {
				// if(projectVersion2.getVersion().equals(odc.getVersionId())) {
				     template.binding("versionTask",projectVersion2.getVersion());
					 break;
				// }
			}
		}
		if(proId!=null)
			 template.binding("proName",proId.getName());
		
		template.binding("uapContextPath", this.uapContextPath);
		String content = template.render();
		// 发送
		emails.forEach(s -> {
			try {
				this.mailManager.sendMail(this.mailFrom, s, content, true, subject, null);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		});
	}
	private Set<String> getEmailsByUserId(Long userId  ) {
		if(userId==null) {
			 return null;
		}
		// 给运维部发
		//Set<String> emails = this.getOperationDepartmentUserEmails(onlineDataChange);
		Set<String> emails =new HashSet<String>();
		BaseOutput<User> userMail=userRpc.get(userId);
		
		if(userMail!=null &&userMail.getData()!=null) {
			emails.add( userMail.getData().getEmail());
		    //emails.add("sunguangqiang@diligrp.com");
		}
		return emails;
	}


	/*private Set<String> getOperationDepartmentUserEmails(OnlineDataChange onlineDataChange) {
		Set<String> emailStrs = new HashSet<>();
		// 默认邮件发送列表，运维组和项目组成员
		// 运维部成员
		Department deptQuery = DTOUtils.newDTO(Department.class);
		deptQuery.setCode(AlmConstants.OPERATION_DEPARTMENT_CODE);
		deptQuery.setFirmCode(AlmConstants.ALM_FIRM_CODE);
		BaseOutput<List<Department>> deptOutput = this.deptRpc.listByDepartment(deptQuery);
		if (deptOutput.isSuccess() && CollectionUtils.isNotEmpty(deptOutput.getData())) {
			Long departmentId = deptOutput.getData().get(0).getId();
			User userQuery = DTOUtils.newDTO(User.class);
			userQuery.setDepartmentId(departmentId);
			userQuery.setFirmCode(AlmConstants.ALM_FIRM_CODE);
			BaseOutput<List<User>> userOutput = this.userRpc.listByExample(userQuery);
			if (userOutput.isSuccess() && CollectionUtils.isNotEmpty(userOutput.getData())) {
				userOutput.getData().forEach(u -> emailStrs.add(u.getEmail()));
			}
		}
		
		return emailStrs;
	}
	*/
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> buildApplyViewModel(OnlineDataChange onlineDataChange) {
		Map<Object, Object> metadata = new HashMap<>();

		JSONObject dateProvider = new JSONObject();
		dateProvider.put("provider", "dateProvider");
		metadata.put("applyDate", dateProvider);
		metadata.put("updateDate", dateProvider);

		try {
			@SuppressWarnings("rawtypes")
			List<Map> viewModelList = ValueProviderUtils.buildDataByProvider(metadata, Arrays.asList(onlineDataChange));
			if (CollectionUtils.isEmpty(viewModelList)) {
				return null;
			}
			return viewModelList.get(0);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}


	@Override
	public void insertOnLineData(OnlineDataChange onlineDataChange, Long id) throws OnlineDataChangeException {
		onlineDataChange.setApplyUserId(id);
		onlineDataChange.setIsSubmit((byte) 1);
		onlineDataChange.setDataStatus((byte) 2);
		onlineDataChange.setCreateDate(new Date());
		

		try {
			this.insertSelective(onlineDataChange);
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("dataId", onlineDataChange.getId() + "");

			Project pro = projectService.get(onlineDataChange.getProjectId());
		//	map.put("dept", pro.getProjectManager() + "");
			map.put(BpmConsts.OnlineDataChangeProcessConstant.dept.getName(), pro.getProjectManager() + "");
			// map.put("dept","1");
			
			BaseOutput<ProcessInstanceMapping> object = runtimeRpc.startProcessInstanceByKey(BpmConsts.ONLINEDATACHANGE_PROCESS, onlineDataChange.getId().toString(), id + "", map);
			if (object.getCode().equals("5000")) {
				throw new OnlineDataChangeException("runtimeRpc失败");
			}

			onlineDataChange.setProcessInstanceId(object.getData().getProcessInstanceId());
			onlineDataChange.setProcessDefinitionId(object.getData().getProcessDefinitionId());
			onlineDataChange.setId(onlineDataChange.getId());

			update(onlineDataChange);
			
		/*	Set<String> emails = this.getEmailsByUserId(pro.getProjectManager());
			this.sendMail(onlineDataChange, "线上数据申请审批", emails);*/
			
			
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println(e);
			  throw new OnlineDataChangeException("失败");
		}
	}

	@Override
	public void updateOnlineDate(OnlineDataChange onlineDataChange, Long id) throws OnlineDataChangeException {
		OnlineDataChange odc = this.get(onlineDataChange.getId());
		if (odc.getProcessInstanceId() == null) {

			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("dataId", onlineDataChange.getId() + "");
				Project pro = projectService.get(onlineDataChange.getProjectId());
				//map.put("dept", pro.getProjectManager() + "");
				map.put(BpmConsts.OnlineDataChangeProcessConstant.dept.getName(), pro.getProjectManager() + "");
				BaseOutput<ProcessInstanceMapping> object = runtimeRpc.startProcessInstanceByKey(BpmConsts.ONLINEDATACHANGE_PROCESS, onlineDataChange.getId().toString(), id + "", map);
				// System.out.println(object.getCode()+object.getData()+object.getErrorData());
				if (object.getCode().equals("5000")) {
					throw new OnlineDataChangeException("失败");
				}

				OnlineDataChange onlineData = new OnlineDataChange();
				onlineData.setProcessInstanceId(object.getData().getProcessInstanceId());
				onlineData.setProcessDefinitionId(object.getData().getProcessDefinitionId());
				onlineData.setId(onlineDataChange.getId());
				onlineData.setDataStatus((byte) 2);
				onlineData.setIsSubmit((byte) 1);
				this.updateSelective(onlineData);
				
				Set<String> emails = this.getEmailsByUserId(pro.getProjectManager());
				this.sendMail(onlineDataChange, "线上数据申请审批", emails);
			} catch (Exception e) {
				e.printStackTrace();
				throw new OnlineDataChangeException("失败");
				//System.out.println(e);
			}
		} else {
			try {
				ArrayList list = new ArrayList<String>();
				list.add(odc.getProcessInstanceId());
				TaskDto tdo = DTOUtils.newDTO(TaskDto.class);
				tdo.setProcessInstanceIds(list);
				BaseOutput<List<TaskMapping>> task = tasksRpc.listTaskMapping(tdo);
				String taskId = task.getData().get(0).getId();
				onlineDataChange.setDataStatus((byte) 2);
				onlineDataChange.setIsSubmit((byte) 1);
				this.updateSelective(onlineDataChange);
				Map<String, Object> map = new HashMap<>();
				map.put("approved", "true");
			
				tasksRpc.complete(taskId);
				Project pro = projectService.get(onlineDataChange.getProjectId());
				Set<String> emails = this.getEmailsByUserId(pro.getProjectManager());
				this.sendMail(onlineDataChange, "线上数据申请审批", emails);
			} catch (Exception e) {

				throw new OnlineDataChangeException("失败");
			}

		}

	}

	@Override
	public void agreeDeptOnlineDataChange(String taskId, Boolean isNeedClaim,String description) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 3);

	
		try {
			
			this.updateSelective(onlineDataChange);
			OnlineDataChange onlineDataChangeTemp = this.get(Long.parseLong(dataId));
			Map<String, String> map = new HashMap<String,String>();
			map.put("approved", "true");
			Project pro = projectService.get(onlineDataChangeTemp.getProjectId());

			User uprojectser = DTOUtils.newDTO(User.class);
			uprojectser.setId(pro.getTestManager());
			uprojectser.setFirmCode(AlmConstants.ALM_FIRM_CODE);
			BaseOutput<List<User>> listUserByExample = userRpc.listByExample(uprojectser);

			Assignment record = DTOUtils.newDTO(Assignment.class);
			record.setAssignee(pro.getTestManager().toString());// 项目测试人员的ID
			List<User> listUsernName = listUserByExample.getData();//// 项目测试人员组的ID
			if (listUsernName != null && listUsernName.size() > 0) {
				record.setAssignee(listUsernName.get(0).getId().toString());
				//map.put("test", listUsernName.get(0).getId().toString() + "");
			    map.put(BpmConsts.OnlineDataChangeProcessConstant.test.getName(), listUsernName.get(0).getId().toString() + "");
			}
			
			
			if (isNeedClaim) {
				BaseOutput<String> output = tasksRpc.claim(taskId, listUsernName.get(0).getId().toString() + "");
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new OnlineDataChangeException("任务签收失败");
				}
			}
			tasksRpc.complete(taskId, map);
			//AlmConstants
			
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGEMANAGER.getCode(), 1,description);
		//	insertDataExeLog(dataId);
			
			Set<String> emails = this.getEmailsByUserId(pro.getTestManager());
			this.sendMail(onlineDataChange, "线上数据申请审批", emails);
			
		} catch (Exception e) {
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGEMANAGER.getCode(), 0,description);
			LOGGER.error("任务签收失败");
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	
	@Override
	public void notAgreeDeptOnlineDataChange(String taskId, Boolean isNeedClaim,String description) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 1);
	
		
		try {
			this.updateSelective(onlineDataChange);
			OnlineDataChange onlineDataChangeTemp = this.get(Long.parseLong(dataId));
			Map<String, String> map = new HashMap<String,String>();
			map.put("approved", "false");
		//	map.put("submit", "" + onlineDataChangeTemp.getApplyUserId());
			map.put(BpmConsts.OnlineDataChangeProcessConstant.submit.getName(), "" + onlineDataChangeTemp.getApplyUserId());
			
			
			if (isNeedClaim) {
				BaseOutput<String> output = tasksRpc.claim(taskId, onlineDataChangeTemp.getApplyUserId() + "");
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new OnlineDataChangeException("任务签收失败");
				}
			}

			tasksRpc.complete(taskId, map);
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGEMANAGER.getCode(), 2,description);
		
			Set<String> emails = this.getEmailsByUserId(onlineDataChangeTemp.getApplyUserId());
			this.sendMail(onlineDataChange, "线上数据申请审批駁回", emails);
		} catch (Exception e) {
			LOGGER.error("任务签收失败");
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGEMANAGER.getCode(), 0,description);
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	@Override
	public void agreeTestOnlineDataChange(String taskId, Boolean isNeedClaim,String description) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 4);
	
		try {
			
			this.updateSelective(onlineDataChange);
			Map<String, String> map = new HashMap<String,String>();
			map.put("approved", "true");
			
			tasksRpc.complete(taskId, map);
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGETEST.getCode(), 1,description);
			
			//dba 組
			//Set<String> emails = this.getEmailsByUserId(pro.getTestManager());
			//this.sendMail(onlineDataChange, "线上数据申请审批", emails);
			Set<String> emails = getGroupEmail(dataId);
			this.sendMail(onlineDataChange, "线上数据申请dba执行", emails);
		
		} catch (Exception e) {
			LOGGER.error("任务签收失败");
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGETEST.getCode(), 0,description);
			throw new OnlineDataChangeException("任务签收失败");
		}

	}



	@Override
	public void notAgreeTestOnlineDataChange(String taskId, Boolean isNeedClaim,String description) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 1);
		
		try {
			onlineDataChangeMapper.updateByPrimaryKeySelective(onlineDataChange);
			Map<String, String> map = new HashMap<String,String>();
			map.put("approved", "false");

			OnlineDataChange onlineDataChangeTemp = this.get(Long.parseLong(dataId));
		//	map.put("submit", "" + onlineDataChangeTemp.getApplyUserId());
			map.put(BpmConsts.OnlineDataChangeProcessConstant.submit.getName(), "" + onlineDataChangeTemp.getApplyUserId());
			
			
			if (isNeedClaim) {
				BaseOutput<String> output = tasksRpc.claim(taskId, onlineDataChangeTemp.getApplyUserId() + "");
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new OnlineDataChangeException("任务签收失败");
				}
			}
			tasksRpc.complete(taskId, map);
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGETEST.getCode(), 2,description);
		
			Set<String> emails = this.getEmailsByUserId(onlineDataChangeTemp.getApplyUserId());
			this.sendMail(onlineDataChange, "线上数据申请审批駁回", emails);
		} catch (Exception e) {
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGETEST.getCode(), 0,description);
			LOGGER.error("任务签收失败");
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	@Override
	public void agreeDBAOnlineDataChange(String taskId, Boolean isNeedClaim,String description) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 5);
		
		// 签收任务
		
		try {
			this.updateSelective(onlineDataChange);
			Map<String, String> map = new HashMap<String,String>();
			map.put("approved", "true");
			Long id = SessionContext.getSessionContext().getUserTicket().getId();
			
			if (isNeedClaim) {
				BaseOutput<String> output = tasksRpc.claim(taskId, id.toString());
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new OnlineDataChangeException("任务签收失败");
				}
			}
			tasksRpc.complete(taskId, map);
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGEDBA.getCode(), 1,description);
			
			//測試組
			//Set<String> emails = this.getEmailsByUserId(pro.getTestManager());
			//this.sendMail(onlineDataChange, "线上数据申请审批", emails);
			Set<String> emails = getGroupEmail(dataId);
			this.sendMail(onlineDataChange, "线上数据申请测试人员执行", emails);
			
		
		} catch (Exception e) {
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.DATACHANGEDBA.getCode(), 0,description);
			LOGGER.error("任务签收失败");
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	@Override
	public void agreeOnlineDataChange(String taskId, Boolean isNeedClaim,String description) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 6);
	
		
		try {
			
			this.updateSelective(onlineDataChange);
			onlineDataChange=this.get(Long.parseLong(dataId));
			Map<String, Object> map = new HashMap<>();
			map.put("approved", "true");
			//map.put("submit", onlineDataChange.getApplyUserId());
			map.put(BpmConsts.OnlineDataChangeProcessConstant.submit.getName(), onlineDataChange.getApplyUserId());
			Long id = SessionContext.getSessionContext().getUserTicket().getId();
			
			if (isNeedClaim) {
				BaseOutput<String> output = tasksRpc.claim(taskId, id.toString() + "");
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new OnlineDataChangeException("任务签收失败");
				}
			}
			
			tasksRpc.complete(taskId);
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.ONLINEDBADATACHANGE.getCode(), 1,description);
			
			//任務完成  發給申請人
		 	Set<String> emails = this.getEmailsByUserId(onlineDataChange.getApplyUserId());
		 	this.sendMail(onlineDataChange, "线上数据申请审批完畢", emails);
		
		
		} catch (Exception e) {
			LOGGER.error("任务签收失败");
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.ONLINEDBADATACHANGE.getCode(), 0,description);
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	@Override
	public void notAgreeOnlineDataChange(String taskId, Boolean isNeedClaim,String description) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		OnlineDataChange onlineDataChange = new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 4);

		
		try {
			
			onlineDataChangeMapper.updateByPrimaryKeySelective(onlineDataChange);
			Map<String, String> map = new HashMap<>();
			map.put("approved", "false");

			 onlineDataChange = this.get(Long.parseLong(dataId));
			
			if (isNeedClaim) {
				Long id = SessionContext.getSessionContext().getUserTicket().getId();
				BaseOutput<String> output = tasksRpc.claim(taskId, id.toString());
				if (!output.isSuccess()) {
					LOGGER.error(output.getMessage());
					throw new OnlineDataChangeException("任务签收失败");
				}
			}
			tasksRpc.complete(taskId, map);
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.ONLINEDBADATACHANGE.getCode(), 2,description);
			Set<String> emails = getGroupEmail(dataId);
			this.sendMail(onlineDataChange, "线上数据申请dba执行", emails);
		
		} catch (Exception e) {
			LOGGER.error("任务签收失败");
			onlineDataChangeLogService.insertDataExeLog(dataId, AlmConstants.OnlineDataChangeLogChangeState.ONLINEDBADATACHANGE.getCode(), 0,description);
			throw new OnlineDataChangeException("任务签收失败");
		}

	}
	@Override
	public void indexOnlineDataChange(String taskId, OnlineDataChange onlineDataChange) throws OnlineDataChangeException {
		BaseOutput<Map<String, Object>> mapId = tasksRpc.getVariables(taskId);
		String dataId = (String) mapId.getData().get("businessKey");
		// OnlineDataChange onlineDataChange=new OnlineDataChange();
		onlineDataChange.setId(Long.parseLong(dataId));
		onlineDataChange.setDataStatus((byte) 2);
	
		try {
			this.updateSelective(onlineDataChange);
			Map<String, Object> map = new HashMap<>();
			map.put("approved", "true");
			
			tasksRpc.complete(taskId);
			onlineDataChange=this.get(Long.parseLong(dataId));
			Project pro = projectService.get(onlineDataChange.getProjectId());
			Set<String> emails = this.getEmailsByUserId(pro.getProjectManager());
			this.sendMail(onlineDataChange, "线上数据申请审批", emails);
			
		} catch (Exception e) {
			LOGGER.error("任务签收失败");
			throw new OnlineDataChangeException("任务签收失败");
		}

	}

	@Override
	public String listPageOnlineData(OnlineDataChange onlineDataChange, String projectIdcc, Long id) {
		onlineDataChange.setSort("create_date");
		onlineDataChange.setOrder("desc");
		// onlineDataChange.setApplyUserId(id);
		if (NumberUtils.isNumber(projectIdcc)) {
			onlineDataChange.setProjectId(Long.parseLong(projectIdcc));
		}

		List<OnlineDataChange> list = onlineDataChangeMapper.selectList(onlineDataChange);
		// Page<OnlineDataChange> page = (Page<OnlineDataChange>) list;
		Map<Object, Object> metadata = null == onlineDataChange.getMetadata() ? new HashMap<>() : onlineDataChange.getMetadata();
		JSONObject projectProvider = new JSONObject();
		projectProvider.put("provider", "projectProvider");
		metadata.put("projectId", projectProvider);
		JSONObject projectVersionProvider = new JSONObject();
		projectVersionProvider.put("provider", "projectVersionProvider");
		metadata.put("versionId", projectVersionProvider);

		JSONObject memberProvider = new JSONObject();
		memberProvider.put("provider", "memberProvider");
		metadata.put("applyUserId", memberProvider);

		JSONObject datetimeProvider = new JSONObject();
		datetimeProvider.put("provider", "datetimeProvider");
		metadata.put("createDate", datetimeProvider);
		
		JSONObject dateProvider = new JSONObject();
		dateProvider.put("provider", "dateProvider");
		metadata.put("applyDate", dateProvider);
		metadata.put("updateDate", dateProvider);
		
		try {
			List<OnlineDataChangeBpmcDtoDto> targetList = BeanConver.copyList(list, OnlineDataChangeBpmcDtoDto.class);

			bpmcUtil.fitLoggedUserIsCanHandledProcess(targetList);

			/*
			 * Set<Long> dbaRoleIds = new HashSet<>(); dbaRoleIds.add(Long.parseLong("75"));
			 * 
			 * Set<Long> onlingRoleIds = new HashSet<>();
			 * onlingRoleIds.add(Long.parseLong("44"));
			 */
			Project pro;
			/*
			 * List<Long> dbaList; List<Long> onLingList;
			 */
			for (OnlineDataChangeBpmcDtoDto odcData : targetList) {
				pro = null;
				pro = projectService.get(odcData.getProjectId());
				if (pro != null) {
					odcData.setTestManager(pro.getTestManager());// "test负责人
					odcData.setProjectManager(pro.getProjectManager());// "项目负责人
				}
			}

			List onlineDataChangeList = ValueProviderUtils.buildDataByProvider(metadata, targetList);
			EasyuiPageOutput taskEasyuiPageOutput = new EasyuiPageOutput(Integer.valueOf(Integer.parseInt(String.valueOf(list.size()))), onlineDataChangeList);
			return taskEasyuiPageOutput.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/*
	 * @Override public void notAgreeDBAOnlineDataChange(String taskId, Boolean
	 * isNeedClaim) throws OnlineDataChangeException { // TODO Auto-generated method
	 * stub BaseOutput<Map<String, Object>> mapId=tasksRpc.getVariables(taskId);
	 * String dataId = (String) mapId.getData().get("businessKey"); OnlineDataChange
	 * onlineDataChange=new OnlineDataChange();
	 * onlineDataChange.setId(Long.parseLong(dataId));
	 * onlineDataChange.setDataStatus((byte)1);
	 * onlineDataChangeMapper.updateByPrimaryKeySelective(onlineDataChange);
	 * Map<String, Object> map=new HashMap<>(); map.put("approved", "false");
	 * 
	 * OnlineDataChange onlineDataChangeTemp=this.get(Long.parseLong(dataId));
	 * 
	 * Project pro=projectService.get(onlineDataChangeTemp.getProjectId());
	 * map.put("test", ""+pro.getTestManager()); if (isNeedClaim) {
	 * BaseOutput<String> output
	 * =tasksRpc.claim(taskId,onlineDataChangeTemp.getApplyUserId()+""); if
	 * (!output.isSuccess()) { LOGGER.error(output.getMessage()); throw new
	 * OnlineDataChangeException("任务签收失败"); } } tasksRpc.complete(taskId, map);
	 * 
	 * }
	 */

	
	private Set<String> getGroupEmail(String dataId) {
		Set<String> processInstanceIds = new HashSet<>();
		processInstanceIds.add(this.get(Long.parseLong(dataId)).getProcessInstanceId()+"");
		BaseOutput<List<TaskIdentityDto>> tiOutput = this.taskRpc.listTaskIdentityByProcessInstanceIds(new ArrayList<String>(processInstanceIds));
		if (!tiOutput.isSuccess()) {
			return  null;
		}
		if (CollectionUtils.isEmpty(tiOutput.getData())) {
			return null;
		}
		
		Set<String> emails =new HashSet<String>();
		GroupUserDto  gu=tiOutput.getData().get(0).getGroupUsers().get(0);
		List<Long> dbaList;
		if(StringUtils.isNotBlank(gu.getGroupId())){
			dbaList=new ArrayList<Long>();
			  dbaList.add(Long.parseLong(gu.getGroupId()));
			  List<RoleUserDto>  dbaDto  =roleRpc.listRoleUserByRoleIds(dbaList).getData();
			  for (RoleUserDto roleUserDto : dbaDto) {
				  List<User> lsitUser=roleUserDto.getUsers();
				  for (User  object : lsitUser){ 
					  emails.add(object.getEmail()+"");
				  }
		      }
		}
		return emails;
	}

}