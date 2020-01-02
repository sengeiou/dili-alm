package com.dili.alm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.dao.ApproveMapper;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.HardwareApplyOperationRecordMapper;
import com.dili.alm.dao.HardwareResourceMapper;
import com.dili.alm.dao.LogMapper;
import com.dili.alm.dao.MoveLogTableMapper;
import com.dili.alm.dao.ProjectApplyMapper;
import com.dili.alm.dao.ProjectChangeMapper;
import com.dili.alm.dao.ProjectCompleteMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectOnlineApplyMapper;
import com.dili.alm.dao.ProjectOnlineOperationRecordMapper;
import com.dili.alm.dao.ProjectOnlineSubsystemMapper;
import com.dili.alm.dao.ProjectVersionMapper;
import com.dili.alm.dao.TaskDetailsMapper;
import com.dili.alm.dao.TaskMapper;
import com.dili.alm.dao.TeamMapper;
import com.dili.alm.dao.TravelCostApplyMapper;
import com.dili.alm.dao.VerifyApprovalMapper;
import com.dili.alm.dao.WeeklyMapper;
import com.dili.alm.dao.WorkOrderMapper;
import com.dili.alm.dao.WorkOrderOperationRecordMapper;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.HardwareApplyOperationRecord;
import com.dili.alm.domain.HardwareResource;
import com.dili.alm.domain.Log;
import com.dili.alm.domain.MoveLogTable;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectComplete;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.ProjectOnlineOperationRecord;
import com.dili.alm.domain.ProjectOnlineSubsystem;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.domain.Team;
import com.dili.alm.domain.TravelCostApply;
import com.dili.alm.domain.VerifyApproval;
import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.domain.WorkOrderOperationRecord;
import com.dili.alm.rpc.UapUserRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataMigrateService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.User;

import net.minidev.json.JSONArray;

@Service
public class DataMigrateImpl implements DataMigrateService {

	@Autowired
	private ApproveMapper approveMapper;

	@Autowired
	private FilesMapper filesMapper;

	@Autowired
	private HardwareApplyOperationRecordMapper hardwareApplyOperationRecordMapper;

	@Autowired
	private HardwareResourceMapper hardwareResourceMapper;

	@Autowired
	private LogMapper logMapper;

	@Autowired
	private ProjectMapper projectMapper;

	@Autowired
	private ProjectApplyMapper projectApplyMapper;

	@Autowired
	private ProjectChangeMapper projectChangeMapper;

	@Autowired
	private ProjectCompleteMapper projectCompleteMapper;

	@Autowired
	private ProjectOnlineApplyMapper projectOnlineApplyMapper;

	@Autowired
	private ProjectOnlineOperationRecordMapper projectOnlineOperationRecordMapper;

	@Autowired
	private ProjectOnlineSubsystemMapper projectOnlineSubsystemMapper;

	@Autowired
	private ProjectVersionMapper projectVersionMapper;

	@Autowired
	private TaskMapper taskMapper;

	@Autowired
	private TaskDetailsMapper taskDetailsMapper;

	@Autowired
	private TeamMapper teamMapper;

	@Autowired
	private TravelCostApplyMapper travelCostApplyMapper;

	@Autowired
	private VerifyApprovalMapper verifyApprovalMapper;

	@Autowired
	private WeeklyMapper weeklyMapper;

	@Autowired
	private WorkOrderMapper workOrderMapper;

	@Autowired
	private WorkOrderOperationRecordMapper workOrderOperationRecordMapper;

	@Autowired
	private MoveLogTableMapper moveLogTableMapper;

	@Autowired
	private UapUserRpc userRpc;
	
	@Autowired
	private  UserRpc  localUserRpc;

	@Override
	public int updateData(Long userId, Long uapUserId) {// userId 本地userId, uapUserId
		
		BaseOutput<User>  uapUser=userRpc.get(uapUserId);
		String  strRealName=uapUser.getData().getRealName();
		User  localUser= DTOUtils.newDTO(User.class);
		localUser.setRealName(strRealName);
	    BaseOutput<List<User>>  lolcalUserList=localUserRpc.listByExample(localUser);
		
	    userId=lolcalUserList.getData().get(0).getId();
	    //uapUserId 和本地userId转换 在页面
		
		int  num=getDataIsExistence(null,uapUserId);//再次查询，使用uapUserId 查询，看看是否迁移过的
        if(num==1) {//已经迁移过
        	return 1;
        }
		//

        
		MoveLogTable dto =new MoveLogTable();
		// approve
		Approve record = DTOUtils.newDTO(Approve.class);
		record.setCreateMemberId(userId);
		List<Approve> listCreateMember = approveMapper.select(record);

		for (Approve approve : listCreateMember) {

			dto =new MoveLogTable();
			dto.setFileId(approve.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("approve");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			approve.setCreateMemberId(uapUserId);
			approveMapper.updateByPrimaryKeySelective(approve);
		}

		record.setCreateMemberId(null);

		record.setModifyMemberId(uapUserId);
		List<Approve> listModifyMember = approveMapper.select(record);

		for (Approve approve : listModifyMember) {

			dto =new MoveLogTable();
			dto.setFileId(approve.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("approve");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			approve.setModifyMemberId(uapUserId);
			approveMapper.updateByPrimaryKeySelective(approve);
		}
		// files

		Files files = DTOUtils.newDTO(Files.class);
		files.setCreateMemberId(userId);
		List<Files> listFilesCreateMember = filesMapper.select(files);
		for (Files approve : listFilesCreateMember) {

			dto =new MoveLogTable();
			dto.setFileId(approve.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("files");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			approve.setCreateMemberId(uapUserId);
			filesMapper.updateByPrimaryKeySelective(approve);
		}

		files.setCreateMemberId(null);
		files.setModifyMemberId(userId);

		List<Files> lisFilestModifyMember = filesMapper.select(files);

		for (Files file : lisFilestModifyMember) {

			dto =new MoveLogTable();
			dto.setFileId(file.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("files");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			file.setModifyMemberId(uapUserId);
			filesMapper.updateByPrimaryKeySelective(file);
			
		}

		///// hardware_apply_operation_record

		HardwareApplyOperationRecord hardwareApplyOperationRecord = DTOUtils.newDTO(HardwareApplyOperationRecord.class);
		hardwareApplyOperationRecord.setOperatorId(userId);
		List<HardwareApplyOperationRecord> lisHardwareApplyOperationRecordCreateMember = hardwareApplyOperationRecordMapper
				.select(hardwareApplyOperationRecord);
		for (HardwareApplyOperationRecord object : lisHardwareApplyOperationRecordCreateMember) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("operator_id");// 设为常量
			dto.setTableName("hardware_apply_operation_record");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setOperatorId(uapUserId);
			hardwareApplyOperationRecordMapper.updateByPrimaryKeySelective(object);
		}

		// HardwareResource

		HardwareResource hardwareResource = DTOUtils.newDTO(HardwareResource.class);
		hardwareResource.setMaintenanceOwner(userId);
		List<HardwareResource> listHardwareResource = hardwareResourceMapper.select(hardwareResource);
		for (HardwareResource object : listHardwareResource) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("maintenance_owner");// 设为常量
			dto.setTableName("hardware_resource");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setMaintenanceOwner(uapUserId);
			hardwareResourceMapper.updateByPrimaryKeySelective(object);
		}

		// log
		Log log = DTOUtils.newDTO(Log.class);
		log.setOperatorId(userId);
		List<Log> listLog = logMapper.select(log);
		for (Log object : listLog) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("operator_id");// 设为常量
			dto.setTableName("log");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setOperatorId(uapUserId);
			logMapper.updateByPrimaryKeySelective(object);
		}

		//
		// Project
		Project project = DTOUtils.newDTO(Project.class);
		project.setProjectManager(userId);
		List<Project> listProjectManager = projectMapper.select(project);
		for (Project object : listProjectManager) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("project_manager");// 设为常量
			dto.setTableName("project");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setProjectManager(uapUserId);
			projectMapper.updateByPrimaryKeySelective(object);
			
			
		}

		project.setProjectManager(null);
		project.setDevelopManager(userId);
		List<Project> listDevelopManager = projectMapper.select(project);
		for (Project object : listDevelopManager) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("develop_manager");// 设为常量
			dto.setTableName("project");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setDevelopManager(uapUserId);
			projectMapper.updateByPrimaryKeySelective(object);
			
			
			
		}

		project.setDevelopManager(null);
		project.setTestManager(userId);
		List<Project> listDevelopManagerr = projectMapper.select(project);
		for (Project object : listDevelopManagerr) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("test_manager");// 设为常量
			dto.setTableName("project");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setTestManager(uapUserId);
			projectMapper.updateByPrimaryKeySelective(object);
			
		}

		project.setTestManager(null);
		project.setProductManager(userId);
		List<Project> listProductManager = projectMapper.select(project);
		for (Project object : listProductManager) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("product_manager");// 设为常量
			dto.setTableName("project");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			

			object.setProductManager(uapUserId);
			projectMapper.updateByPrimaryKeySelective(object);
			
		}

		project.setProductManager(null);
		project.setOriginator(userId);
		List<Project> listOriginator = projectMapper.select(project);
		for (Project object : listOriginator) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("originator");// 设为常量
			dto.setTableName("project");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setOriginator(uapUserId);
			projectMapper.updateByPrimaryKeySelective(object);
			
			
		}

		project.setOriginator(null);
		project.setDep(userId);
		List<Project> listDep = projectMapper.select(project);
		for (Project object : listDep) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("dep");// 设为常量
			dto.setTableName("project");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setDep(uapUserId);
			projectMapper.updateByPrimaryKeySelective(object);
			
		}

		project.setDep(null);
		project.setBusinessOwner(userId);
		List<Project> listBusinessOwner = projectMapper.select(project);
		for (Project object : listBusinessOwner) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("business_owner");// 设为常量
			dto.setTableName("project");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setBusinessOwner(uapUserId);
			projectMapper.updateByPrimaryKeySelective(object);
			
		}

		// project_apply:立项申请表
		ProjectApply projectApply = DTOUtils.newDTO(ProjectApply.class);
		projectApply.setProjectLeader(userId);
		List<ProjectApply> listProjectApply = projectApplyMapper.select(projectApply);
		for (ProjectApply object : listProjectApply) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("project_leader");// 设为常量
			dto.setTableName("project_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setProjectLeader(uapUserId);
			projectApplyMapper.updateByPrimaryKeySelective(object);
			
			
			
		}

		projectApply.setProjectLeader(null);
		projectApply.setProductManager(userId);
		List<ProjectApply> listProjectApplyProductManager = projectApplyMapper.select(projectApply);
		for (ProjectApply object : listProjectApplyProductManager) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("product_manager");// 设为常量
			dto.setTableName("project_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			object.setProductManager(uapUserId);
			projectApplyMapper.updateByPrimaryKeySelective(object);
			
		}

		projectApply.setProductManager(null);
		projectApply.setDevelopmentManager(userId);
		List<ProjectApply> listProjectApplyDevelopmentManager = projectApplyMapper.select(projectApply);
		for (ProjectApply object : listProjectApplyDevelopmentManager) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("development_manager");// 设为常量
			dto.setTableName("project_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setDevelopmentManager(uapUserId);
			projectApplyMapper.updateByPrimaryKeySelective(object);
		}

		projectApply.setDevelopmentManager(null);
		projectApply.setTestManager(userId);
		List<ProjectApply> listProjectApplyTestManager = projectApplyMapper.select(projectApply);
		for (ProjectApply object : listProjectApplyTestManager) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("test_manager");// 设为常量
			dto.setTableName("project_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setTestManager(uapUserId);
			projectApplyMapper.updateByPrimaryKeySelective(object);
		}

		projectApply.setTestManager(null);
		projectApply.setBusinessOwner(userId);
		List<ProjectApply> listProjectBusinessOwner = projectApplyMapper.select(projectApply);
		for (ProjectApply object : listProjectBusinessOwner) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("business_owner");// 设为常量
			dto.setTableName("project_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setBusinessOwner(uapUserId);
			projectApplyMapper.updateByPrimaryKeySelective(object);
		}

		///////// project_change

		ProjectChange projectChange = DTOUtils.newDTO(ProjectChange.class);
		projectChange.setCreateMemberId(userId);
		List<ProjectChange> listProjectChange = projectChangeMapper.select(projectChange);
		for (ProjectChange object : listProjectChange) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("project_change");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setCreateMemberId(uapUserId);
			projectChangeMapper.updateByPrimaryKeySelective(object);
			
		}

		projectChange.setCreateMemberId(null);
		projectChange.setModifyMemberId(userId);
		List<ProjectChange> listModifyMemberId = projectChangeMapper.select(projectChange);
		for (ProjectChange object : listModifyMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("project_change");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setModifyMemberId(uapUserId);
			projectChangeMapper.updateByPrimaryKeySelective(object);
		}

		////// project_complete:结项申请表

		ProjectComplete projectComplete = DTOUtils.newDTO(ProjectComplete.class);
		projectComplete.setCreateMemberId(userId);
		List<ProjectComplete> listProjectCompleteCreateMember = projectCompleteMapper.select(projectComplete);

		for (ProjectComplete object : listProjectCompleteCreateMember) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("project_complete");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			object.setCreateMemberId(uapUserId);
			projectCompleteMapper.updateByPrimaryKeySelective(object);
			
			
		}

		projectComplete.setCreateMemberId(null);
		projectComplete.setModifyMemberId(userId);

		List<ProjectComplete> listProjectCompleteModifyMember = projectCompleteMapper.select(projectComplete);
		for (ProjectComplete object : listProjectCompleteModifyMember) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("project_complete");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setModifyMemberId(uapUserId);
			projectCompleteMapper.updateByPrimaryKeySelective(object);
		}

		// project_oneline_apply

		ProjectOnlineApply projectOnlineApply = DTOUtils.newDTO(ProjectOnlineApply.class);
		projectOnlineApply.setBusinessOwnerId(userId);
		List<ProjectOnlineApply> listProjectOnlineApplyProjectOnlineApply = projectOnlineApplyMapper
				.select(projectOnlineApply);

		for (ProjectOnlineApply object : listProjectOnlineApplyProjectOnlineApply) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("business_owner_id");// 设为常量
			dto.setTableName("project_online_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setBusinessOwnerId(uapUserId);
			projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
			
		}

		projectOnlineApply.setBusinessOwnerId(null);
		projectOnlineApply.setProductManagerId(userId);
		List<ProjectOnlineApply> listProjectOnlineApplyProductManagerId = projectOnlineApplyMapper
				.select(projectOnlineApply);
		for (ProjectOnlineApply object : listProjectOnlineApplyProductManagerId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("product_manager_id");// 设为常量
			dto.setTableName("project_online_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setProductManagerId(uapUserId);
			projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
			
		}

		projectOnlineApply.setProductManagerId(null);
		projectOnlineApply.setTestManagerId(userId);
		List<ProjectOnlineApply> listProjectOnlineApplyTestManagerId = projectOnlineApplyMapper
				.select(projectOnlineApply);
		for (ProjectOnlineApply object : listProjectOnlineApplyTestManagerId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("test_manager_id");// 设为常量
			dto.setTableName("project_online_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setTestManagerId(uapUserId);
			projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
			
		}

		projectOnlineApply.setTestManagerId(null);
		projectOnlineApply.setDevelopmentManagerId(userId);
		List<ProjectOnlineApply> listProjectOnlineApplyDevelopmentManagerId = projectOnlineApplyMapper
				.select(projectOnlineApply);
		for (ProjectOnlineApply object : listProjectOnlineApplyDevelopmentManagerId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("development_manager_id");// 设为常量
			dto.setTableName("project_online_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setDevelopmentManagerId(uapUserId);
			projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
		}

		projectOnlineApply.setDevelopmentManagerId(null);
		projectOnlineApply.setApplicantId(userId);
		List<ProjectOnlineApply> listProjectOnlineApplyApplicantId = projectOnlineApplyMapper
				.select(projectOnlineApply);
		for (ProjectOnlineApply object : listProjectOnlineApplyApplicantId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("applicant_id");// 设为常量
			dto.setTableName("project_online_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setApplicantId(uapUserId);
			projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
		}
		/////// 需要重新写

		projectOnlineApply.setApplicantId(null);

		List<ProjectOnlineApply> listProjectOnlinetExecutorId = projectOnlineApplyMapper.selectProjectOnlineApplyByExecutorId(userId);
		for (ProjectOnlineApply object : listProjectOnlinetExecutorId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("executor_id");// 设为常量
			dto.setTableName("project_online_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			if(object.getExecutorId()!=null) {
				String[] strList=object.getExecutorId().split(",");
				for (int i = 0; i < strList.length; i++) {
					 if(strList[i].equals(userId+"")) {
						 strList[i]= uapUserId+"--";
					 }
				}
				object.setExecutorId(strList.toString());//带处理
				
			}
		
			projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
		}

		////// project_online_operation_record
		ProjectOnlineOperationRecord projectOnlineOperationRecord = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
		projectOnlineOperationRecord.setOperatorId(userId);
		List<ProjectOnlineOperationRecord> listprojectOnlineOperationRecord = projectOnlineOperationRecordMapper
				.select(projectOnlineOperationRecord);
		for (ProjectOnlineOperationRecord object : listprojectOnlineOperationRecord) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("operator_id");// 设为常量
			dto.setTableName("project_online_operation_record");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setOperatorId(uapUserId);
			projectOnlineOperationRecordMapper.updateByPrimaryKeySelective(object);
		}

		//// project_online_subsystem:

		ProjectOnlineSubsystem projectOnlineSubsystem = DTOUtils.newDTO(ProjectOnlineSubsystem.class);
		projectOnlineSubsystem.setManagerId(userId);

		List<ProjectOnlineSubsystem> listprojectOnlineSubsystem = projectOnlineSubsystemMapper.select(projectOnlineSubsystem);
		for (ProjectOnlineSubsystem object : listprojectOnlineSubsystem) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("manager_id");// 设为常量
			dto.setTableName("project_online_subsystem");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setManagerId(uapUserId);
			projectOnlineSubsystemMapper.updateByPrimaryKeySelective(object);
		}

		///// manager_name
		/////// 需要重新写
		projectOnlineSubsystem.setManagerId(null);
		BaseOutput<User> userTempManagerIdLocal = localUserRpc.findUserById(userId);
		projectOnlineSubsystem.setManagerName(userTempManagerIdLocal.getData().getRealName());
		
		BaseOutput<User> userTempManagerIdUapRpc= userRpc.get(uapUserId);
		List<ProjectOnlineSubsystem> listprojectManagerName = projectOnlineSubsystemMapper.select(projectOnlineSubsystem);
		for (ProjectOnlineSubsystem object : listprojectManagerName) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("manager_name");// 设为常量
			dto.setTableName("project_online_subsystem");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			if(object.getManagerName()!=null) {
				String[] strList=object.getManagerName().split(",");
				for (int i = 0; i < strList.length; i++) {
					 if(strList[i].equals(userTempManagerIdLocal.getData().getRealName())) {
						 strList[i]= userTempManagerIdUapRpc.getData().getRealName()+"--";
					 }
				}
				object.setManagerName(strList.toString());//带处理
				
			}
			//object.setManagerName(object.getManagerName().replace(object.getManagerName(), userTempManagerId.getData().getRealName()));
			projectOnlineSubsystemMapper.updateByPrimaryKeySelective(object);
		}

		/////// project_version:项目版本

		ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setCreatorId(userId);

		List<ProjectVersion> listprojectProjectVersion = projectVersionMapper.select(projectVersion);
		for (ProjectVersion object : listprojectProjectVersion) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("creator_id");// 设为常量
			dto.setTableName("project_version");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setCreatorId(uapUserId);
			projectVersionMapper.updateByPrimaryKeySelective(object);
		}

		projectVersion.setCreatorId(null);
		projectVersion.setModifierId(userId);
		List<ProjectVersion> listprojectModifierId = projectVersionMapper.select(projectVersion);
		for (ProjectVersion object : listprojectModifierId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modifier_id");// 设为常量
			dto.setTableName("project_version");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setModifierId(uapUserId);
			projectVersionMapper.updateByPrimaryKeySelective(object);
		}

		//////

		Task task = DTOUtils.newDTO(Task.class);
		task.setOwner(userId);
		List<Task> listTaskOwner = taskMapper.select(task);
		for (Task object : listTaskOwner) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("owner");// 设为常量
			dto.setTableName("task");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setOwner(uapUserId);
			taskMapper.updateByPrimaryKeySelective(object);
		}

		task.setOwner(null);
		task.setCreateMemberId(userId);
		List<Task> listTaskCreateMemberId = taskMapper.select(task);
		for (Task object : listTaskCreateMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("task");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			object.setCreateMemberId(uapUserId);
			taskMapper.updateByPrimaryKeySelective(object);
		}

		task.setCreateMemberId(null);
		task.setModifyMemberId(userId);
		List<Task> listTaskModifyMemberId = taskMapper.select(task);
		for (Task object : listTaskModifyMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("task");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setModifyMemberId(uapUserId);
			taskMapper.updateByPrimaryKeySelective(object);
			
		}
		//// task_details

		TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
		taskDetails.setCreateMemberId(userId);
		List<TaskDetails> listtaskDetailsCreateMemberId = taskDetailsMapper.select(taskDetails);
		for (TaskDetails object : listtaskDetailsCreateMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("task_details");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setCreateMemberId(uapUserId);
			taskDetailsMapper.updateByPrimaryKeySelective(object);
		}

		taskDetails.setCreateMemberId(null);
		taskDetails.setModifyMemberId(userId);
		List<TaskDetails> listtaskDetailsModifyMemberId = taskDetailsMapper.select(taskDetails);
		for (TaskDetails object : listtaskDetailsModifyMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("task_details");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setModifyMemberId(uapUserId);
			taskDetailsMapper.updateByPrimaryKeySelective(object);
			
		}

		// team

		Team team = DTOUtils.newDTO(Team.class);
		team.setMemberId(userId);
		List<Team> listteam = teamMapper.select(team);
		for (Team object : listteam) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("member_id");// 设为常量
			dto.setTableName("team");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setMemberId(uapUserId);
			teamMapper.updateByPrimaryKeySelective(object);
		}
		///// travel_cost_apply
		TravelCostApply travelCostApply = DTOUtils.newDTO(TravelCostApply.class);
		travelCostApply.setApplicantId(userId);
		List<TravelCostApply> listtravelCostApply = travelCostApplyMapper.select(travelCostApply);
		for (TravelCostApply object : listtravelCostApply) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("applicant_id");// 设为常量
			dto.setTableName("travel_cost_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setApplicantId(uapUserId);
			travelCostApplyMapper.updateByPrimaryKeySelective(object);
		}

		// 需要重新写

		BaseOutput<User> userTemp = userRpc.get(userId);

		travelCostApply.setApplicantId(userId);
		travelCostApply.setRootDepartemntId(userTemp.getData().getDepartmentId());
		List<TravelCostApply> listtraveRootDepartemntId = travelCostApplyMapper.select(travelCostApply);
		for (TravelCostApply object : listtraveRootDepartemntId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("root_departemnt_id");// 设为常量
			dto.setTableName("travel_cost_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			object.setDepartmentId(userTemp.getData().getDepartmentId());
			travelCostApplyMapper.updateByPrimaryKeySelective(object);
		}

		/// 需要重新写
		travelCostApply.setRootDepartemntId(null);
		travelCostApply.setDepartmentId(userTemp.getData().getDepartmentId());
		List<TravelCostApply> travelCostApplyRootDepartemntId = travelCostApplyMapper.select(travelCostApply);
		for (TravelCostApply object : travelCostApplyRootDepartemntId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("department_id");// 设为常量
			dto.setTableName("travel_cost_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setDepartmentId(userTemp.getData().getDepartmentId());
			travelCostApplyMapper.updateByPrimaryKeySelective(object);
		}

		////// verify_approval

		VerifyApproval verifyApprova = DTOUtils.newDTO(VerifyApproval.class);
		verifyApprova.setApproveId(userId);
		List<VerifyApproval> listVerifyApproval = verifyApprovalMapper.select(verifyApprova);
		for (VerifyApproval object : listVerifyApproval) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("approve_id");// 设为常量
			dto.setTableName("verify_approval");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setApproveId(uapUserId);
			verifyApprovalMapper.updateByPrimaryKeySelective(object);
		}

		verifyApprova.setApproveId(null);
		verifyApprova.setCreateMemberId(userId);

		List<VerifyApproval> listVerifyApprovalCreateMemberId = verifyApprovalMapper.select(verifyApprova);
		for (VerifyApproval object : listVerifyApprovalCreateMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("verify_approval");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setCreateMemberId(uapUserId);
			verifyApprovalMapper.updateByPrimaryKeySelective(object);
		}

		verifyApprova.setCreateMemberId(null);
		verifyApprova.setModifyMemberId(userId);

		List<VerifyApproval> listVerifyApprovaltModifyMemberId = verifyApprovalMapper.select(verifyApprova);
		for (VerifyApproval object : listVerifyApprovaltModifyMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("verify_approval");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setModifyMemberId(uapUserId);
			verifyApprovalMapper.updateByPrimaryKeySelective(object);
		}

		////// weekly
		Weekly weekly = DTOUtils.newDTO(Weekly.class);
		weekly.setCreateMemberId(userId);
		List<Weekly> listWeeklyCreateMemberId = weeklyMapper.select(weekly);
		for (Weekly object : listWeeklyCreateMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("weekly");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setCreateMemberId(uapUserId);
			weeklyMapper.updateByPrimaryKeySelective(object);
			
		}

		weekly.setCreateMemberId(null);
		weekly.setModifyMemberId(userId);
		List<Weekly> listWeeklyModifyMemberId = weeklyMapper.select(weekly);
		for (Weekly object : listWeeklyModifyMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("weekly");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setModifyMemberId(uapUserId);
			weeklyMapper.updateByPrimaryKeySelective(object);
		}
		/////// work_order

		WorkOrder weeklyOrder = DTOUtils.newDTO(WorkOrder.class);
		weeklyOrder.setAcceptorId(userId);
		List<WorkOrder> listWeeklyOrderAcceptorId = workOrderMapper.select(weeklyOrder);
		for (WorkOrder object : listWeeklyOrderAcceptorId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("acceptor_id");// 设为常量
			dto.setTableName("work_order");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setAcceptorId(uapUserId);
			workOrderMapper.updateByPrimaryKeySelective(object);
		}

		/// 需要重新写 完成
		weeklyOrder.setAcceptorId(null);
		weeklyOrder.setCopyUserId(userId + "");
		List<WorkOrder> listWeeklyCopyUserId = workOrderMapper.selectWorkOrdeByCopyUserId(userId);
		for (WorkOrder object : listWeeklyCopyUserId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("copy_user_id");// 设为常量
			dto.setTableName("work_order");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			String str=object.getCopyUserId();
			if(object.getCopyUserId()!=null) {
				List<String> list = JSONObject.parseArray(str,String.class);
				
				for (int j = 0; j < list.size(); j++) {
					if(list.get(j).equals(userId+"")) {
						list.set(j, uapUserId+"--");
					}
				}
				
				com.alibaba.fastjson.JSONArray  studentJsonArray = JSON.parseArray(JSONObject.toJSONString(list));
				object.setCopyUserId(studentJsonArray.toString());//带处理
				
			}
			//object.setCopyUserId(str.replace(userId+"", uapUserId+""));
			workOrderMapper.updateByPrimaryKeySelective(object);
			
		}

		weeklyOrder.setAcceptorId(null);
		weeklyOrder.setApplicantId(userId);
		List<WorkOrder> listWeeklyOrderOrderApplicantId = workOrderMapper.select(weeklyOrder);
		for (WorkOrder object : listWeeklyOrderOrderApplicantId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("applicant_id");// 设为常量
			dto.setTableName("work_order");// 设为常量AcceptorId
			moveLogTableMapper.insertSelective(dto);
			
			object.setApplicantId(uapUserId);
			workOrderMapper.updateByPrimaryKeySelective(object);
		}

		weeklyOrder.setApplicantId(null);
		weeklyOrder.setExecutorId(userId);
		List<WorkOrder> listWeeklyOrderOrdeExecutorId = workOrderMapper.select(weeklyOrder);
		for (WorkOrder object : listWeeklyOrderOrdeExecutorId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("executor_id");// 设为常量
			dto.setTableName("work_order");// 设为常量AcceptorId
			moveLogTableMapper.insertSelective(dto);
			
			object.setExecutorId(uapUserId);
			workOrderMapper.updateByPrimaryKeySelective(object);
		}

		/////// work_order_operation_record

		WorkOrderOperationRecord workOrderOperationRecord = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		workOrderOperationRecord.setOperatorId(userId);

		List<WorkOrderOperationRecord> listworkOrderOperationRecord = workOrderOperationRecordMapper
				.select(workOrderOperationRecord);
		for (WorkOrderOperationRecord object : listworkOrderOperationRecord) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("operator_id");// 设为常量
			dto.setTableName("work_order_operation_record");// 设为常量
			moveLogTableMapper.insertSelective(dto);
			
			object.setOperatorId(uapUserId);
			workOrderOperationRecordMapper.updateByPrimaryKeySelective(object);
		}

		/*
		 * approve:审批表，包含立项审批，变更审批，结项审批的数据 project_leader->用户 business_owner->用户
		 * 
		 * ApproveMapper
		 * 
		 * dep->部门 create_member_id->用户 // modify_member_id->用户 //
		 * 
		 * 
		 * files:文件表，包含上传文件信息 create_member_id->用户// modify_member_id->用户// FilesMapper
		 * 
		 * hardware_apply_operation_record：IT资源申请审批记录表 operator_id->用户 //
		 * 
		 * HardwareApplyOperationRecordMapper
		 * 
		 * 
		 * hardware_resource:IT资源维护表 maintenance_owner->用户 //
		 * 
		 * HardwareResourceMapper
		 * 
		 * 
		 * log:日志表 operator_id->用户//
		 * 
		 * LogMapper
		 * 
		 * 
		 * project:项目表 project_manager->用户 develop_manager->用户 test_manager->用户
		 * product_manager->用户 originator->用户 ProjectMapper
		 * 
		 * 
		 * dep->部门 business_owner->用户
		 * 
		 * project_apply:立项申请表 project_leader->用户 product_manager->用户
		 * development_manager->用户 test_manager->用户 business_owner->用户 dep->部门
		 * create_member_id->用户 modify_member_id->用户
		 * 
		 * ProjectApplyMapper
		 * 
		 * project_change:项目变更表 create_member_id->用户 modify_member_id->用户
		 * 
		 * ProjectChangeMapper
		 * 
		 * 
		 * project_complete:结项申请表 create_member_id->用户 modify_member_id->用户
		 * ProjectCompleteMapper
		 * 
		 * project_oneline_apply:上线申请表 business_owner_id->用户 project_manager_id->用户
		 * test_manager_id->用户 development_manager_id->用户 applicant_id->用户
		 * executor_id->用户 ProjectOnlineApplyMapper
		 * 
		 * project_online_operation_record:上线申请记录 operator_id->用户
		 * 
		 * ProjectOnlineOperationRecordMapper
		 * 
		 * project_online_subsystem:上线申请影响系统 manager_id->用户 manager_name->用户真实姓名
		 * ProjectOnlineSubsystemMapper
		 * 
		 * project_version:项目版本 creator_id->用户 modifier_id->用户 ProjectVersionMapper
		 * 
		 * 
		 * task:任务表 owner->用户 create_member_id->用户 modify_member_id->用户 TaskMapper
		 * 
		 * task_details:任务工时表 create_member_id->用户 modify_member_id->用户
		 * TaskDetailsMapper
		 * 
		 * team:团队表 member_id->用户 TeamMapper
		 * 
		 * 
		 * travel_cost_apply:差旅申请 applicant_id->用户 root_departemnt_id->部门
		 * department_id->部门 TravelCostApplyMapper
		 * 
		 * 
		 * verify_approval：项目变更验证审批表 approver->用户 create_member_id->用户
		 * modify_member_id->用户
		 * 
		 * VerifyApprovalMapper
		 * 
		 * weekly：周报 create_member_id->用户 modify_member_id->用户 WeeklyMapper
		 * 
		 * work_order：工单表 acceptor_id->用户 copy_user_id->用户，json数组需要先解析 applicant_id->用户
		 * executor_id->用户 WorkOrderMapper
		 * 
		 * work_order_operation_record：工单操作记录表 operator_id->用户
		 * 
		 * WorkOrderOperationRecordMapper
		 * 
		 * 
		 */

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDataIsExistence(Long userId, Long uapUserId) {
		//这个时候应收uapUserId ，因为它不变
		MoveLogTable dto = new MoveLogTable();
		List<MoveLogTable> approveList = null;

		
		/////// work_order_operation_record

		WorkOrderOperationRecord workOrderOperationRecord = DTOUtils.newDTO(WorkOrderOperationRecord.class);
		workOrderOperationRecord.setOperatorId(uapUserId);

		List<WorkOrderOperationRecord> listworkOrderOperationRecord = workOrderOperationRecordMapper.select(workOrderOperationRecord);
		for (WorkOrderOperationRecord object : listworkOrderOperationRecord) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("operator_id");// 设为常量
			dto.setTableName("work_order_operation_record");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}
		
		
		
		Approve record = DTOUtils.newDTO(Approve.class);
		record.setProjectLeader(uapUserId);
		List<Approve> listCreateMember = approveMapper.select(record);

		for (Approve approve : listCreateMember) {
			dto =new MoveLogTable();
			dto.setTableName("approve");
			dto.setFileField("project_leader");
			dto.setId(approve.getId());
			approveList = null;
			approveList = moveLogTableMapper.select(dto);
			if (approveList != null && approveList.size() > 0) {
				return 1;
			}

		}
		record.setProjectLeader(null);
		record.setBusinessOwner(uapUserId);
		listCreateMember = approveMapper.select(record);
		for (Approve approve : listCreateMember) {
			dto =new MoveLogTable();
			dto.setTableName("approve");
			dto.setFileField("business_owner");
			dto.setId(approve.getId());
			approveList = null;
			approveList = moveLogTableMapper.select(dto);
			if (approveList != null && approveList.size() > 0) {
				return 1;
			}

		}

		record.setBusinessOwner(null);
		record.setDep(uapUserId);
		listCreateMember = approveMapper.select(record);
		for (Approve approve : listCreateMember) {
			dto =new MoveLogTable();
			dto.setTableName("approve");
			dto.setId(approve.getId());
			dto.setFileField("dep");
			approveList = null;
			approveList = moveLogTableMapper.select(dto);
			if (approveList != null && approveList.size() > 0) {
				return 1;
			}

		}

		record.setDep(null);
		record.setCreateMemberId(uapUserId);
		listCreateMember = approveMapper.select(record);
		for (Approve approve : listCreateMember) {
			dto =new MoveLogTable();
			dto.setTableName("approve");
			dto.setId(approve.getId());
			approveList = null;
			dto.setFileField("create_member_id");
			approveList = moveLogTableMapper.select(dto);
			if (approveList != null && approveList.size() > 0) {
				return 1;
			}

		}

		record.setCreateMemberId(null);
		record.setModifyMemberId(uapUserId);
		listCreateMember = approveMapper.select(record);
		for (Approve approve : listCreateMember) {
			dto =new MoveLogTable();
			dto.setTableName("approve");
			dto.setId(approve.getId());
			approveList = null;
			dto.setFileField("modify_member_id");
			approveList = moveLogTableMapper.select(dto);
			if (approveList != null && approveList.size() > 0) {
				return 1;
			}

		}

		// files

		Files files = DTOUtils.newDTO(Files.class);
		files.setCreateMemberId(uapUserId);
		List<Files> filesList = filesMapper.select(files);

		for (Files filesObject : filesList) {

			dto =new MoveLogTable();
			dto.setId(filesObject.getId());
			dto.setTableName("files");
			dto.setFileField("create_member_id");
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}

		}

		files.setCreateMemberId(null);
		files.setModifyMemberId(uapUserId);
		filesList = filesMapper.select(files);

		for (Files filesObject : filesList) {

			dto =new MoveLogTable();
			dto.setId(filesObject.getId());
			dto.setTableName("files");
			dto.setFileField("modify_member_id");
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}

		}

		// hardware_apply_operation_record
		HardwareApplyOperationRecord hardwareApplyOperationRecord = DTOUtils.newDTO(HardwareApplyOperationRecord.class);
		hardwareApplyOperationRecord.setOperatorId(uapUserId);
		List<HardwareApplyOperationRecord> hardwareApplyOperationRecordList = hardwareApplyOperationRecordMapper.select(hardwareApplyOperationRecord);
		for (HardwareApplyOperationRecord hardwareApplyOperationRecordObject : hardwareApplyOperationRecordList) {

			dto =new MoveLogTable();
			dto.setId(hardwareApplyOperationRecordObject.getId());
			dto.setTableName("hardware_apply_operation_record");
			dto.setFileField("operator_id");
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}

		}

		// hardware_resource

		HardwareResource hardwareResource = DTOUtils.newDTO(HardwareResource.class);
		hardwareResource.setMaintenanceOwner(uapUserId);
		List<HardwareResource> hardwareResourceList = hardwareResourceMapper.select(hardwareResource);
		for (HardwareResource hardwareResourceObject : hardwareResourceList) {

			dto =new MoveLogTable();
			dto.setId(hardwareResourceObject.getId());
			dto.setTableName("hardware_resource");
			dto.setFileField("maintenance_owner");
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}

		}
		// LOG

		Log log = DTOUtils.newDTO(Log.class);
		log.setOperatorId(uapUserId);
		List<Log> logList = logMapper.select(log);
		for (Log logListObject : logList) {

			dto =new MoveLogTable();
			dto.setId(logListObject.getId());
			dto.setTableName("log");
			dto.setFileField("operator_id");
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}

		}

		//// project:项目表

		Project project = DTOUtils.newDTO(Project.class);
		project.setProjectManager(uapUserId);
		List<Project> listProjectManager = projectMapper.select(project);
		for (Project object : listProjectManager) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("project_manager");// 设为常量
			dto.setTableName("project");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		project.setProjectManager(null);
		project.setDevelopManager(uapUserId);
		List<Project> listDevelopManager = projectMapper.select(project);
		for (Project object : listDevelopManager) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("develop_manager");// 设为常量
			dto.setTableName("project");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		project.setDevelopManager(null);
		project.setTestManager(uapUserId);
		List<Project> listDevelopManagerr = projectMapper.select(project);
		for (Project object : listDevelopManagerr) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("test_manager");// 设为常量
			dto.setTableName("project");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		project.setTestManager(null);
		project.setProductManager(uapUserId);
		List<Project> listProductManager = projectMapper.select(project);
		for (Project object : listProductManager) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("product_manager");// 设为常量
			dto.setTableName("project");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		project.setProductManager(null);
		project.setOriginator(uapUserId);
		List<Project> listOriginator = projectMapper.select(project);
		for (Project object : listOriginator) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("originator");// 设为常量
			dto.setTableName("project");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		project.setOriginator(null);
		project.setDep(uapUserId);
		List<Project> listDep = projectMapper.select(project);
		for (Project object : listDep) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("dep");// 设为常量
			dto.setTableName("project");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		project.setDep(null);
		project.setBusinessOwner(uapUserId);
		List<Project> listBusinessOwner = projectMapper.select(project);
		for (Project object : listBusinessOwner) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("business_owner");// 设为常量
			dto.setTableName("project");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}
		// project_apply:立项申请表
		ProjectApply projectApply = DTOUtils.newDTO(ProjectApply.class);
		projectApply.setProjectLeader(uapUserId);
		List<ProjectApply> listProjectApply = projectApplyMapper.select(projectApply);
		for (ProjectApply object : listProjectApply) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("project_leader");// 设为常量
			dto.setTableName("project_apply");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		projectApply.setProjectLeader(null);
		projectApply.setProductManager(uapUserId);
		List<ProjectApply> listProjectApplyProductManager = projectApplyMapper.select(projectApply);
		for (ProjectApply object : listProjectApplyProductManager) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("product_manager");// 设为常量
			dto.setTableName("project_apply");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		projectApply.setProductManager(null);
		projectApply.setDevelopmentManager(uapUserId);
		List<ProjectApply> listProjectApplyDevelopmentManager = projectApplyMapper.select(projectApply);
		for (ProjectApply object : listProjectApplyDevelopmentManager) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("development_manager");// 设为常量
			dto.setTableName("project_apply");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		projectApply.setDevelopmentManager(null);
		projectApply.setTestManager(uapUserId);
		List<ProjectApply> listProjectApplyTestManager = projectApplyMapper.select(projectApply);
		for (ProjectApply object : listProjectApplyTestManager) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("test_manager");// 设为常量
			dto.setTableName("project_apply");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		projectApply.setTestManager(null);
		projectApply.setBusinessOwner(uapUserId);
		List<ProjectApply> listProjectBusinessOwner = projectApplyMapper.select(projectApply);
		for (ProjectApply object : listProjectBusinessOwner) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("business_owner");// 设为常量
			dto.setTableName("project_apply");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		///////// project_change

		ProjectChange projectChange = DTOUtils.newDTO(ProjectChange.class);
		projectChange.setCreateMemberId(uapUserId);
		List<ProjectChange> listProjectChange = projectChangeMapper.select(projectChange);
		for (ProjectChange object : listProjectChange) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("project_change");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		projectChange.setCreateMemberId(null);
		projectChange.setModifyMemberId(uapUserId);
		List<ProjectChange> listModifyMemberId = projectChangeMapper.select(projectChange);
		for (ProjectChange object : listModifyMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("project_change");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		////// project_complete:结项申请表

		ProjectComplete projectComplete = DTOUtils.newDTO(ProjectComplete.class);
		projectComplete.setCreateMemberId(uapUserId);
		List<ProjectComplete> listProjectCompleteCreateMember = projectCompleteMapper.select(projectComplete);

		for (ProjectComplete object : listProjectCompleteCreateMember) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("project_complete");// 设为常量
			moveLogTableMapper.insertSelective(dto);
		}

		projectComplete.setCreateMemberId(null);
		projectComplete.setModifyMemberId(uapUserId);

		List<ProjectComplete> listProjectCompleteModifyMember = projectCompleteMapper.select(projectComplete);
		for (ProjectComplete object : listProjectCompleteModifyMember) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("project_complete");// 设为常量
			moveLogTableMapper.insertSelective(dto);
		}

		// project_oneline_apply

		ProjectOnlineApply projectOnlineApply = DTOUtils.newDTO(ProjectOnlineApply.class);
		projectOnlineApply.setBusinessOwnerId(uapUserId);
		List<ProjectOnlineApply> listProjectOnlineApplyProjectOnlineApply = projectOnlineApplyMapper
				.select(projectOnlineApply);

		for (ProjectOnlineApply object : listProjectOnlineApplyProjectOnlineApply) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("business_owner_id");// 设为常量
			dto.setTableName("project_online_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
		}

		projectOnlineApply.setBusinessOwnerId(null);
		projectOnlineApply.setProductManagerId(uapUserId);
		List<ProjectOnlineApply> listProjectOnlineApplyProductManagerId = projectOnlineApplyMapper
				.select(projectOnlineApply);
		for (ProjectOnlineApply object : listProjectOnlineApplyProductManagerId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("product_manager_id");// 设为常量
			dto.setTableName("project_online_apply");// 设为常量
			moveLogTableMapper.insertSelective(dto);
		}

		projectOnlineApply.setProductManagerId(null);
		projectOnlineApply.setTestManagerId(uapUserId);
		List<ProjectOnlineApply> listProjectOnlineApplyTestManagerId = projectOnlineApplyMapper
				.select(projectOnlineApply);
		for (ProjectOnlineApply object : listProjectOnlineApplyTestManagerId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("test_manager_id");// 设为常量
			dto.setTableName("project_online_apply");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		projectOnlineApply.setTestManagerId(null);
		projectOnlineApply.setDevelopmentManagerId(uapUserId);
		List<ProjectOnlineApply> listProjectOnlineApplyDevelopmentManagerId = projectOnlineApplyMapper
				.select(projectOnlineApply);
		for (ProjectOnlineApply object : listProjectOnlineApplyDevelopmentManagerId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("development_manager_id");// 设为常量
			dto.setTableName("project_online_apply");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		projectOnlineApply.setDevelopmentManagerId(null);
		projectOnlineApply.setApplicantId(uapUserId);
		List<ProjectOnlineApply> listProjectOnlineApplyApplicantId = projectOnlineApplyMapper
				.select(projectOnlineApply);
		for (ProjectOnlineApply object : listProjectOnlineApplyApplicantId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("applicant_id");// 设为常量
			dto.setTableName("project_online_apply");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}
		/////// 需要重新写

		projectOnlineApply.setApplicantId(null);

		List<ProjectOnlineApply> listProjectOnlinetExecutorId = projectOnlineApplyMapper.selectProjectOnlineApplyByExecutorId(uapUserId);
		for (ProjectOnlineApply object : listProjectOnlinetExecutorId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("executor_id");// 设为常量
			dto.setTableName("project_online_apply");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		////// project_online_operation_record
		ProjectOnlineOperationRecord projectOnlineOperationRecord = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
		projectOnlineOperationRecord.setOperatorId(uapUserId);
		List<ProjectOnlineOperationRecord> listprojectOnlineOperationRecord = projectOnlineOperationRecordMapper
				.select(projectOnlineOperationRecord);
		for (ProjectOnlineOperationRecord object : listprojectOnlineOperationRecord) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("operator_id");// 设为常量
			dto.setTableName("project_online_operation_record");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		//// project_online_subsystem:

		ProjectOnlineSubsystem projectOnlineSubsystem = DTOUtils.newDTO(ProjectOnlineSubsystem.class);
		projectOnlineSubsystem.setManagerId(uapUserId);

		List<ProjectOnlineSubsystem> listprojectOnlineSubsystem = projectOnlineSubsystemMapper
				.select(projectOnlineSubsystem);
		for (ProjectOnlineSubsystem object : listprojectOnlineSubsystem) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("manager_id");// 设为常量
			dto.setTableName("project_online_subsystem");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		///// manager_name
		/////// 需要重新写
		projectOnlineSubsystem.setManagerId(null);
		BaseOutput<User> userTempManagerId = userRpc.get(uapUserId);
		projectOnlineSubsystem.setManagerName(userTempManagerId.getData().getRealName());
		List<ProjectOnlineSubsystem> listprojectManagerName = projectOnlineSubsystemMapper
				.select(projectOnlineSubsystem);
		for (ProjectOnlineSubsystem object : listprojectManagerName) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("manager_name");// 设为常量
			dto.setTableName("project_online_subsystem");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		/////// project_version:项目版本

		ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		projectVersion.setCreatorId(uapUserId);

		List<ProjectVersion> listprojectProjectVersion = projectVersionMapper.select(projectVersion);
		for (ProjectVersion object : listprojectProjectVersion) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("creator_id");// 设为常量
			dto.setTableName("project_version");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		projectVersion.setCreatorId(null);
		projectVersion.setModifierId(uapUserId);
		List<ProjectVersion> listprojectModifierId = projectVersionMapper.select(projectVersion);
		for (ProjectVersion object : listprojectModifierId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modifier_id");// 设为常量
			dto.setTableName("project_version");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		//////

		Task task = DTOUtils.newDTO(Task.class);
		task.setOwner(uapUserId);
		List<Task> listTaskOwner = taskMapper.select(task);
		for (Task object : listTaskOwner) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("owner");// 设为常量
			dto.setTableName("task");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		task.setOwner(null);
		task.setCreateMemberId(uapUserId);
		List<Task> listTaskCreateMemberId = taskMapper.select(task);
		for (Task object : listTaskCreateMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("task");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		task.setCreateMemberId(null);
		task.setModifyMemberId(uapUserId);
		List<Task> listTaskModifyMemberId = taskMapper.select(task);
		for (Task object : listTaskModifyMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("task");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}
		//// task_details

		TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
		taskDetails.setCreateMemberId(uapUserId);
		List<TaskDetails> listtaskDetailsCreateMemberId = taskDetailsMapper.select(taskDetails);
		for (TaskDetails object : listtaskDetailsCreateMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("task_details");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		taskDetails.setCreateMemberId(null);
		taskDetails.setModifyMemberId(uapUserId);
		List<TaskDetails> listtaskDetailsModifyMemberId = taskDetailsMapper.select(taskDetails);
		for (TaskDetails object : listtaskDetailsModifyMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("task_details");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		// team

		Team team = DTOUtils.newDTO(Team.class);
		team.setMemberId(uapUserId);
		List<Team> listteam = teamMapper.select(team);
		for (Team object : listteam) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("member_id");// 设为常量
			dto.setTableName("team");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}
		///// travel_cost_apply
		TravelCostApply travelCostApply = DTOUtils.newDTO(TravelCostApply.class);
		travelCostApply.setApplicantId(uapUserId);
		List<TravelCostApply> listtravelCostApply = travelCostApplyMapper.select(travelCostApply);
		for (TravelCostApply object : listtravelCostApply) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("applicant_id");// 设为常量
			dto.setTableName("travel_cost_apply");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		// 需要重新写

		BaseOutput<User> userTemp = userRpc.get(uapUserId);

		travelCostApply.setApplicantId(uapUserId);
		travelCostApply.setRootDepartemntId(userTemp.getData().getDepartmentId());
		List<TravelCostApply> listtraveRootDepartemntId = travelCostApplyMapper.select(travelCostApply);
		for (TravelCostApply object : listtraveRootDepartemntId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("root_departemnt_id");// 设为常量
			dto.setTableName("travel_cost_apply");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		/// 需要重新写
		travelCostApply.setRootDepartemntId(null);
		travelCostApply.setDepartmentId(userTemp.getData().getDepartmentId());
		List<TravelCostApply> travelCostApplyRootDepartemntId = travelCostApplyMapper.select(travelCostApply);
		for (TravelCostApply object : travelCostApplyRootDepartemntId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("department_id");// 设为常量
			dto.setTableName("travel_cost_apply");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		////// verify_approval

		VerifyApproval verifyApprova = DTOUtils.newDTO(VerifyApproval.class);
		verifyApprova.setApproveId(uapUserId);
		List<VerifyApproval> listVerifyApproval = verifyApprovalMapper.select(verifyApprova);
		for (VerifyApproval object : listVerifyApproval) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("approve_id");// 设为常量
			dto.setTableName("verify_approval");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		verifyApprova.setApproveId(null);
		verifyApprova.setCreateMemberId(uapUserId);

		List<VerifyApproval> listVerifyApprovalCreateMemberId = verifyApprovalMapper.select(verifyApprova);
		for (VerifyApproval object : listVerifyApprovalCreateMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("verify_approval");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		verifyApprova.setCreateMemberId(null);
		verifyApprova.setModifyMemberId(uapUserId);

		List<VerifyApproval> listVerifyApprovaltModifyMemberId = verifyApprovalMapper.select(verifyApprova);
		for (VerifyApproval object : listVerifyApprovaltModifyMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("verify_approval");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		////// weekly
		Weekly weekly = DTOUtils.newDTO(Weekly.class);
		weekly.setCreateMemberId(uapUserId);
		List<Weekly> listWeeklyCreateMemberId = weeklyMapper.select(weekly);
		for (Weekly object : listWeeklyCreateMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("create_member_id");// 设为常量
			dto.setTableName("weekly");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		weekly.setCreateMemberId(null);
		weekly.setModifyMemberId(uapUserId);
		List<Weekly> listWeeklyModifyMemberId = weeklyMapper.select(weekly);
		for (Weekly object : listWeeklyModifyMemberId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("modify_member_id");// 设为常量
			dto.setTableName("weekly");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}
		/////// work_order

		WorkOrder weeklyOrder = DTOUtils.newDTO(WorkOrder.class);
		weeklyOrder.setAcceptorId(uapUserId);
		List<WorkOrder> listWeeklyOrderAcceptorId = workOrderMapper.select(weeklyOrder);
		for (WorkOrder object : listWeeklyOrderAcceptorId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("acceptor_id");// 设为常量
			dto.setTableName("work_order");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		/// 需要重新写 完成
		weeklyOrder.setAcceptorId(null);
		weeklyOrder.setCopyUserId(userId + "");
		List<WorkOrder> listWeeklyCopyUserId = workOrderMapper.selectWorkOrdeByCopyUserId(uapUserId);
		for (WorkOrder object : listWeeklyCopyUserId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("copy_user_id");// 设为常量
			dto.setTableName("work_order");// 设为常量
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		weeklyOrder.setAcceptorId(null);
		weeklyOrder.setApplicantId(uapUserId);
		List<WorkOrder> listWeeklyOrderOrderApplicantId = workOrderMapper.select(weeklyOrder);
		for (WorkOrder object : listWeeklyOrderOrderApplicantId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("applicant_id");// 设为常量
			dto.setTableName("work_order");// 设为常量AcceptorId
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

		weeklyOrder.setApplicantId(null);
		weeklyOrder.setExecutorId(userId);
		List<WorkOrder> listWeeklyOrderOrdeExecutorId = workOrderMapper.select(weeklyOrder);
		for (WorkOrder object : listWeeklyOrderOrdeExecutorId) {

			dto =new MoveLogTable();
			dto.setFileId(object.getId());
			dto.setFileField("executor_id");// 设为常量
			dto.setTableName("work_order");// 设为常量AcceptorId
			List<MoveLogTable> list = moveLogTableMapper.select(dto);
			if (list != null && list.size() > 0) {
				return 1;
			}
		}

	

		/*
		 * /// project_apply:立项申请表
		 * 
		 * dto.setTableName("project_apply"); dto.setFileField("project_manager"); list
		 * = moveLogTableMapper.selectByExample(dto); if (list != null && list.size() >
		 * 0) { return 1; } dto.setTableName("project_apply");
		 * dto.setFileField("develop_manager"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 * 
		 * dto.setTableName("project_apply"); dto.setFileField("test_manager"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 * 
		 * dto.setTableName("project_apply"); dto.setFileField("product_manager"); list
		 * = moveLogTableMapper.selectByExample(dto); if (list != null && list.size() >
		 * 0) { return 1; } dto.setTableName("project_apply");
		 * dto.setFileField("business_owner"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 * 
		 * //// project_change:项目变更表 dto.setTableName("project_apply");
		 * dto.setFileField("project_manager"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } dto.setTableName("project_apply");
		 * dto.setFileField("develop_manager"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 * 
		 * dto.setTableName("project_apply"); dto.setFileField("test_manager"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 * 
		 * ///// project_change:项目变更表 dto.setTableName("project_apply");
		 * dto.setFileField("create_member_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 * 
		 * dto.setTableName("project_apply"); dto.setFileField("modify_member_id"); list
		 * = moveLogTableMapper.selectByExample(dto); if (list != null && list.size() >
		 * 0) { return 1; }
		 * 
		 * // project_complete:结项申请表
		 * 
		 * dto.setTableName("project_complete"); dto.setFileField("create_member_id");
		 * list = moveLogTableMapper.selectByExample(dto); if (list != null &&
		 * list.size() > 0) { return 1; }
		 * 
		 * dto.setTableName("project_complete"); dto.setFileField("modify_member_id");
		 * list = moveLogTableMapper.selectByExample(dto); if (list != null &&
		 * list.size() > 0) { return 1; }
		 * 
		 * // project_oneline_apply::结项申请表
		 * 
		 * dto.setTableName("project_oneline_apply");
		 * dto.setFileField("business_owner_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } dto.setTableName("project_oneline_apply");
		 * dto.setFileField("project_manager_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 * 
		 * dto.setTableName("project_oneline_apply");
		 * dto.setFileField("test_manager_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 * 
		 * dto.setTableName("project_oneline_apply");
		 * dto.setFileField("development_manager_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } dto.setTableName("project_oneline_apply");
		 * dto.setFileField("applicant_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } dto.setTableName("project_oneline_apply");
		 * dto.setFileField("executor_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } ///// project_online_operation_record:上线申请记录
		 * dto.setTableName("project_online_operation_record");
		 * dto.setFileField("operator_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 * 
		 * // project_online_subsystem:上线申请影响系统
		 * 
		 * dto.setTableName("project_online_subsystem"); dto.setFileField("manager_id");
		 * list = moveLogTableMapper.selectByExample(dto); if (list != null &&
		 * list.size() > 0) { return 1; }
		 * 
		 * dto.setTableName("project_online_subsystem");
		 * dto.setFileField("manager_name"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 * 
		 * /// project_version:项目版本
		 * 
		 * dto.setTableName("project_version"); dto.setFileField("creator_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 * 
		 * dto.setTableName("project_version"); dto.setFileField("modifier_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } //// task:任务表 dto.setTableName("task");
		 * dto.setFileField("owner"); list = moveLogTableMapper.selectByExample(dto); if
		 * (list != null && list.size() > 0) { return 1; }
		 * 
		 * dto.setTableName("task"); dto.setFileField("create_member_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } dto.setTableName("task"); dto.setFileField("modify_member_id");
		 * list = moveLogTableMapper.selectByExample(dto); if (list != null &&
		 * list.size() > 0) { return 1; } //// task_details:任务工时表
		 * 
		 * dto.setTableName("task_details"); dto.setFileField("create_member_id"); list
		 * = moveLogTableMapper.selectByExample(dto); if (list != null && list.size() >
		 * 0) { return 1; } dto.setTableName("task_details");
		 * dto.setFileField("modify_member_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } //// team:团队表 dto.setTableName("team");
		 * dto.setFileField("member_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } //// travel_cost_apply:差旅申请
		 * dto.setTableName("travel_cost_apply"); dto.setFileField("applicant_id"); list
		 * = moveLogTableMapper.selectByExample(dto); if (list != null && list.size() >
		 * 0) { return 1; } dto.setTableName("travel_cost_apply");
		 * dto.setFileField("root_departemnt_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } dto.setTableName("travel_cost_apply");
		 * dto.setFileField("department_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } // verify_approval：项目变更验证审批表
		 * dto.setTableName("verify_approval"); dto.setFileField("approver"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } dto.setTableName("verify_approval");
		 * dto.setFileField("create_member_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } dto.setTableName("verify_approval");
		 * dto.setFileField("modify_member_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 * 
		 * // weekly：周报 dto.setTableName("weekly");
		 * dto.setFileField("create_member_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } dto.setTableName("weekly");
		 * dto.setFileField("modify_member_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } // work_order：工单表 dto.setTableName("work_order");
		 * dto.setFileField("acceptor_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } dto.setTableName("work_order");
		 * dto.setFileField("copy_user_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } dto.setTableName("work_order");
		 * dto.setFileField("applicant_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } dto.setTableName("work_order");
		 * dto.setFileField("executor_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; } /// work_order_operation_record：工单操作记录表
		 * dto.setTableName("work_order_operation_record");
		 * dto.setFileField("operator_id"); list =
		 * moveLogTableMapper.selectByExample(dto); if (list != null && list.size() > 0)
		 * { return 1; }
		 */

		return 0;
	}

	@Override
	public int updateUserIdStrData() {
		
		
		List<ProjectOnlineApply> listProjectOnlinetExecutorId = projectOnlineApplyMapper.select(null);
		for (ProjectOnlineApply object : listProjectOnlinetExecutorId) {
			
			
			if(object.getExecutorId()!=null) {
				String strList=object.getExecutorId().replace("--", "");
				object.setExecutorId(strList.toString());//带处理
			}
		
			projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
		}
		
		List<WorkOrder> listWeeklyCopyUserId = workOrderMapper.select(null);
		for (WorkOrder object : listWeeklyCopyUserId) {
		
			String str=object.getCopyUserId();
			object.setCopyUserId(str.replace("--",""));
			workOrderMapper.updateByPrimaryKeySelective(object);
			
		}
		
	
		List<ProjectOnlineSubsystem> listprojectManagerName = projectOnlineSubsystemMapper.select(null);
		for (ProjectOnlineSubsystem object : listprojectManagerName) {
			object.setManagerName(object.getManagerName().replace("--", ""));
			projectOnlineSubsystemMapper.updateByPrimaryKeySelective(object);
		}
		
		
		
		return 1;
	}
	public static void main(String[] args) {
		String str="1634,163";
		String[] strList=str.split(",");
		for (int i = 0; i < strList.length; i++) {
			 if(strList[i].equals("163")) {
				 strList[i]="1227acc";
			 }
		}
		
		for (String string : strList) {
			System.out.println(string);
		}
	}

}
