package com.dili.alm.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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
import com.dili.alm.domain.AlmUser;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.DepartmentALM;
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
import com.dili.alm.domain.dto.apply.ApplyApprove;
import com.dili.alm.domain.dto.apply.ApplyApproveMove;
import com.dili.alm.domain.dto.apply.ApplyMajorResource;
import com.dili.alm.domain.dto.apply.ApplyMajorResourceMove;
import com.dili.alm.domain.dto.apply.ApplyRelatedResource;
import com.dili.alm.rpc.AlmUserRpc;
import com.dili.alm.rpc.DepartmentALMRpc;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataMigrateService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.PageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.session.SessionContext;

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
	private AlmUserRpc   localUserRpc;
	
	@Autowired
	private  UserRpc  userRpc ;
	
    @Autowired
	private DepartmentRpc  departmentUapRpc;
    @Autowired
	private  DepartmentALMRpc  departmentALMRpc;
	@Override
	@Transactional
	public int updateData(Long userId, Long uapUserId) {// userId 本地userId, uapUserId
		
		try {
			BaseOutput<User>  uapUser=userRpc.findUserById(uapUserId);
		//	uapUser.getData().set
			String  phone=uapUser.getData().getCellphone();
			String  realName=uapUser.getData().getUserName();
			AlmUser  localUser= new AlmUser();
			localUser.setUserName(realName);
			//localUser.setCellphone(phone);
			BaseOutput<List<AlmUser>> lolcalUserList=localUserRpc.list(localUser);
			
			if(lolcalUserList.getCode().endsWith("5000")) {
				return 8;	
			}
			if(lolcalUserList.getData()!=null&&lolcalUserList.getData().size()>0) {
				 userId=lolcalUserList.getData().get(0).getId();
			}else {
				return 2;	
			}
   
			//uapUserId 和本地userId转换 在页面
			
			/*int  num=getDataIsExistence(uapUserId);//再次查询，使用uapUserId 查询，看看是否迁移过的
			if(num==1) {//已经迁移过
				return 1;
			}*/
			//

			
			//List<Department>  userIdDept=departmentALMRpc.findByUserId(userId).getData();
			//List<Department>  uapUserIdDept=departmentUapRpc.findByUserId(uapUserId).getData();
			Department depRPC= DTOUtils.newDTO(Department.class);
			depRPC.setFirmCode("szpt");
			
			DepartmentALM localDept= new DepartmentALM();
			List<DepartmentALM>  listAlmRpc=departmentALMRpc.list(localDept).getData();
			List<Department>  listUapRpc=  departmentUapRpc.listByDepartment(depRPC).getData();
			
			
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
				if(moveLogTableMapper.select(dto).size()==0) {
					moveLogTableMapper.insertSelective(dto);
					approve.setCreateMemberId(uapUserId);
					approveMapper.updateByPrimaryKeySelective(approve);
				}
				
			}

			record.setCreateMemberId(null);
			record.setModifyMemberId(userId);
			List<Approve> listModifyMember = approveMapper.select(record);

			for (Approve approve : listModifyMember) {

				dto =new MoveLogTable();
				dto.setFileId(approve.getId());
				dto.setFileField("modify_member_id");// 设为常量
				dto.setTableName("approve");// 设为常量
				
				approve.setModifyMemberId(uapUserId);
				if(moveLogTableMapper.select(dto).size()==0) {
					moveLogTableMapper.insertSelective(dto);
				    approveMapper.updateByPrimaryKeySelective(approve);
				}
				
			
			}
			record.setModifyMemberId(null);
			record.setBusinessOwner(userId);
			List<Approve> lisbusiness_owner = approveMapper.select(record);
			for (Approve approve : lisbusiness_owner) {

				dto =new MoveLogTable();
				dto.setFileId(approve.getId());
				dto.setFileField("business_owner");// 设为常量
				dto.setTableName("approve");// 设为常量
				
				approve.setBusinessOwner(uapUserId);
				if(moveLogTableMapper.select(dto).size()==0) {
					moveLogTableMapper.insertSelective(dto);
				    approveMapper.updateByPrimaryKeySelective(approve);
				}
				
			
			}
			
			record.setBusinessOwner(null);
			record.setProjectLeader(userId);
			List<Approve> project_leader = approveMapper.select(record);
			for (Approve approve : project_leader) {

				dto =new MoveLogTable();
				dto.setFileId(approve.getId());
				dto.setFileField("project_leader");// 设为常量
				dto.setTableName("approve");// 设为常量
				
				approve.setProjectLeader(uapUserId);
				if(moveLogTableMapper.select(dto).size()==0) {
					moveLogTableMapper.insertSelective(dto);
				    approveMapper.updateByPrimaryKeySelective(approve);
				}
				
			
			}
			
			record.setProjectLeader(null);
			
		
			
			
			//record.setDep(userIdDept.get(0).getId());
			List<Approve> projectDept = approveMapper.selectAll();
			for (Approve approve : projectDept) {

				dto =new MoveLogTable();
				dto.setFileId(approve.getId());
				dto.setFileField("dep");// 设为常量
				dto.setTableName("approve");// 设为常量
				  if(moveLogTableMapper.select(dto).size()==0) {
					  if(approve.getDep()!=null) {
					    Long  uapDeId=getUapDept(listAlmRpc, listUapRpc, approve.getDep().toString());
						approve.setDep(uapDeId);
					    moveLogTableMapper.insertSelective(dto);
				        approveMapper.updateByPrimaryKeySelective(approve);
					  }
					
				  }
			
			}	
		
			
			
			
			//sgq
			record.setDep(null);
			List<Approve> approveDescription = approveMapper.selectAll();
			for (Approve approve : approveDescription) {
				if (StringUtils.isNotBlank(approve.getDescription())) {
					List<ApplyApproveMove> approveList = JSON.parseArray(approve.getDescription(), ApplyApproveMove.class);
					for (ApplyApproveMove applyApprove : approveList) {
						 if(applyApprove.getUserId().equals(userId.toString())) {
							 applyApprove.setUserId(uapUserId+"--"); 
						 }
					}
					approve.setDescription(JSON.toJSONString(approveList));
					approveMapper.updateByPrimaryKeySelective(approve);
				}
				
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
				if(moveLogTableMapper.select(dto).size()==0) {
					approve.setCreateMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    filesMapper.updateByPrimaryKeySelective(approve);
				}
			}

			files.setCreateMemberId(null);
			files.setModifyMemberId(userId);

			List<Files> lisFilestModifyMember = filesMapper.select(files);

			for (Files file : lisFilestModifyMember) {

				dto =new MoveLogTable();
				dto.setFileId(file.getId());
				dto.setFileField("modify_member_id");// 设为常量
				dto.setTableName("files");// 设为常量
				
				
				if(moveLogTableMapper.select(dto).size()==0) {
					file.setModifyMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    filesMapper.updateByPrimaryKeySelective(file);
				}
				
			}
			

			///// hardware_apply_operation_record
				
			HardwareApplyOperationRecord hardwareApplyOperationRecord = DTOUtils.newDTO(HardwareApplyOperationRecord.class);
			hardwareApplyOperationRecord.setOperatorId(userId);
			List<HardwareApplyOperationRecord> lisHardwareApplyOperationRecordCreateMember = hardwareApplyOperationRecordMapper.select(hardwareApplyOperationRecord);
			for (HardwareApplyOperationRecord object : lisHardwareApplyOperationRecordCreateMember) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("operator_id");// 设为常量
				dto.setTableName("hardware_apply_operation_record");// 设为常量
				object.setOperatorId(uapUserId);
				if(moveLogTableMapper.select(dto).size()==0) {
					moveLogTableMapper.insertSelective(dto);
				    hardwareApplyOperationRecordMapper.updateByPrimaryKeySelective(object);
				}
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
				object.setMaintenanceOwner(uapUserId);
				if(moveLogTableMapper.select(dto).size()==0) {
					moveLogTableMapper.insertSelective(dto);
				   hardwareResourceMapper.updateByPrimaryKeySelective(object);
				}
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
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setOperatorId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    logMapper.updateByPrimaryKeySelective(object);
				}
			}
			
			
			// Project
			Project project = DTOUtils.newDTO(Project.class);
			project.setProjectManager(userId);
			List<Project> listProjectManager = projectMapper.select(project);
			for (Project object : listProjectManager) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("project_manager");// 设为常量
				dto.setTableName("project");// 设为常量
			
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setProjectManager(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectMapper.updateByPrimaryKeySelective(object);
				}
				
				
			}

			project.setProjectManager(null);
			project.setDevelopManager(userId);
			List<Project> listDevelopManager = projectMapper.select(project);
			for (Project object : listDevelopManager) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("develop_manager");// 设为常量
				dto.setTableName("project");// 设为常量
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setDevelopManager(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectMapper.updateByPrimaryKeySelective(object);
				}
				
				
			}

			project.setDevelopManager(null);
			project.setTestManager(userId);
			List<Project> listDevelopManagerr = projectMapper.select(project);
			for (Project object : listDevelopManagerr) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("test_manager");// 设为常量
				dto.setTableName("project");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setTestManager(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectMapper.updateByPrimaryKeySelective(object);
				}
				
			}

			project.setTestManager(null);
			project.setProductManager(userId);
			List<Project> listProductManager = projectMapper.select(project);
			for (Project object : listProductManager) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("product_manager");// 设为常量
				dto.setTableName("project");// 设为常量

				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setProductManager(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectMapper.updateByPrimaryKeySelective(object);
				}
				
			}

			project.setProductManager(null);
			project.setOriginator(userId);
			List<Project> listOriginator = projectMapper.select(project);
			for (Project object : listOriginator) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("originator");// 设为常量
				dto.setTableName("project");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setOriginator(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectMapper.updateByPrimaryKeySelective(object);
				}
				
				
			}

			project.setOriginator(null);
			

			List<Project> listDep = projectMapper.selectAll();
			for (Project object : listDep) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("dep");// 设为常量
				dto.setTableName("project");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					if(object.getDep()!=null) {
				        Long  uapDeId=getUapDept(listAlmRpc, listUapRpc, object.getDep().toString());
				        object.setDep(uapDeId);
					   moveLogTableMapper.insertSelective(dto);
				       projectMapper.updateByPrimaryKeySelective(object);
					}
				}
			}
				
	
			
			
			
		    project.setDep(null);
			project.setBusinessOwner(userId);
			List<Project> listBusinessOwner = projectMapper.select(project);
			for (Project object : listBusinessOwner) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("business_owner");// 设为常量
				dto.setTableName("project");// 设为常量
				
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setBusinessOwner(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectMapper.updateByPrimaryKeySelective(object);
				}
				
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
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setProjectLeader(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectApplyMapper.updateByPrimaryKeySelective(object);
				}
				
				
			}

			projectApply.setProjectLeader(null);
			projectApply.setProductManager(userId);
			List<ProjectApply> listProjectApplyProductManager = projectApplyMapper.select(projectApply);
			for (ProjectApply object : listProjectApplyProductManager) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("product_manager");// 设为常量
				dto.setTableName("project_apply");// 设为常量
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setProductManager(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				   projectApplyMapper.updateByPrimaryKeySelective(object);
				}
				
			}

			projectApply.setProductManager(null);
			projectApply.setDevelopmentManager(userId);
			List<ProjectApply> listProjectApplyDevelopmentManager = projectApplyMapper.select(projectApply);
			for (ProjectApply object : listProjectApplyDevelopmentManager) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("development_manager");// 设为常量
				dto.setTableName("project_apply");// 设为常量
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setDevelopmentManager(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectApplyMapper.updateByPrimaryKeySelective(object);
				}
			}

			projectApply.setDevelopmentManager(null);
			projectApply.setTestManager(userId);
			List<ProjectApply> listProjectApplyTestManager = projectApplyMapper.select(projectApply);
			for (ProjectApply object : listProjectApplyTestManager) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("test_manager");// 设为常量
				dto.setTableName("project_apply");// 设为常量
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setTestManager(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectApplyMapper.updateByPrimaryKeySelective(object);
				}
			}

			projectApply.setTestManager(null);
			projectApply.setBusinessOwner(userId);
			List<ProjectApply> listProjectBusinessOwner = projectApplyMapper.select(projectApply);
			for (ProjectApply object : listProjectBusinessOwner) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("business_owner");// 设为常量
				dto.setTableName("project_apply");// 设为常量
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setBusinessOwner(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectApplyMapper.updateByPrimaryKeySelective(object);
				}
			}

			
			projectApply.setBusinessOwner(null);
		
			List<ProjectApply> listDepApp = projectApplyMapper.select(projectApply);
			for (ProjectApply object : listDepApp) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("dep");// 设为常量
				dto.setTableName("project");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					if(object.getDep()!=null) {
				        Long  uapDeId=getUapDept(listAlmRpc, listUapRpc, object.getDep().toString());
				        object.setDep(uapDeId);
					   moveLogTableMapper.insertSelective(dto);
					   projectApplyMapper.updateByPrimaryKeySelective(object);
					}
					
				}
				
			}
		
			projectApply.setDep(null);
			projectApply.setCreateMemberId(userId);
			List<ProjectApply> listProjectCreateMemberId = projectApplyMapper.select(projectApply);
			for (ProjectApply object : listProjectCreateMemberId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("create_member_id");// 设为常量
				dto.setTableName("project_apply");// 设为常量
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setCreateMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectApplyMapper.updateByPrimaryKeySelective(object);
				}
			}
			
			
			projectApply.setCreateMemberId(null);
			projectApply.setModifyMemberId(userId);
			List<ProjectApply> listProjectModifyMemberId = projectApplyMapper.select(projectApply);
			for (ProjectApply object : listProjectModifyMemberId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("modify_member_id");// 设为常量
				dto.setTableName("project_apply");// 设为常量
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setModifyMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectApplyMapper.updateByPrimaryKeySelective(object);
				}
			}
			
			
			projectApply.setModifyMemberId(null);
			List<ProjectApply> listProjectAll = projectApplyMapper.selectAll();
			for (ProjectApply object : listProjectAll) {
    
				if(object.getResourceRequire()!=null) {
					ApplyMajorResourceMove resourceRequire = JSON.parseObject(object.getResourceRequire(), ApplyMajorResourceMove.class);
					if(resourceRequire.getMainUser().equals(userId.toString())) {
						resourceRequire.setMainUser(uapUserId+"--");
					}
					if(resourceRequire.getRelatedResources()!=null) {
						List<ApplyRelatedResource> list=resourceRequire.getRelatedResources();
						if(list!=null&&list.size()>0) {
						   for (ApplyRelatedResource applyRelatedResource : list) {
							
							if(null!=applyRelatedResource.getRelatedUser()&&applyRelatedResource.getRelatedUser().equals(userId.toString())) {
								applyRelatedResource.setRelatedUser(uapUserId+"--");
						  	}
						  }
						}
						resourceRequire.setRelatedResources(list);
					}
					object.setResourceRequire(JSON.toJSONString(resourceRequire));
					projectApplyMapper.updateByPrimaryKeySelective(object);
				}
			
			}
			
			/*///////// project_change

			ProjectChange projectChange = DTOUtils.newDTO(ProjectChange.class);
			projectChange.setCreateMemberId(userId);
			List<ProjectChange> listProjectChange = projectChangeMapper.select(projectChange);
			for (ProjectChange object : listProjectChange) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("create_member_id");// 设为常量
				dto.setTableName("project_change");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setCreateMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectChangeMapper.updateByPrimaryKeySelective(object);
				}
				
			}

			projectChange.setCreateMemberId(null);
			projectChange.setModifyMemberId(userId);
			List<ProjectChange> listModifyMemberId = projectChangeMapper.select(projectChange);
			for (ProjectChange object : listModifyMemberId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("modify_member_id");// 设为常量
				dto.setTableName("project_change");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setModifyMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				   projectChangeMapper.updateByPrimaryKeySelective(object);
				 }
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
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setCreateMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectCompleteMapper.updateByPrimaryKeySelective(object);
				}
				
				
			}

			projectComplete.setCreateMemberId(null);
			projectComplete.setModifyMemberId(userId);

			List<ProjectComplete> listProjectCompleteModifyMember = projectCompleteMapper.select(projectComplete);
			for (ProjectComplete object : listProjectCompleteModifyMember) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("modify_member_id");// 设为常量
				dto.setTableName("project_complete");// 设为常量
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setModifyMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectCompleteMapper.updateByPrimaryKeySelective(object);
				}
			}

			// project_oneline_apply

			ProjectOnlineApply projectOnlineApply = DTOUtils.newDTO(ProjectOnlineApply.class);
			projectOnlineApply.setBusinessOwnerId(userId);
			List<ProjectOnlineApply> listProjectOnlineApplyProjectOnlineApply = projectOnlineApplyMapper.select(projectOnlineApply);

			for (ProjectOnlineApply object : listProjectOnlineApplyProjectOnlineApply) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("business_owner_id");// 设为常量
				dto.setTableName("project_online_apply");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setBusinessOwnerId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
				}
				
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
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setProductManagerId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
				}
				
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
				
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setTestManagerId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
				}
				
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
				
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setDevelopmentManagerId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				  projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
				}
			}

			projectOnlineApply.setDevelopmentManagerId(null);
			projectOnlineApply.setApplicantId(userId);
			List<ProjectOnlineApply> listProjectOnlineApplyApplicantId = projectOnlineApplyMapper.select(projectOnlineApply);
			for (ProjectOnlineApply object : listProjectOnlineApplyApplicantId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("applicant_id");// 设为常量
				dto.setTableName("project_online_apply");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setApplicantId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
				}
			}
			/////// 

			projectOnlineApply.setApplicantId(null);

			List<ProjectOnlineApply> listProjectOnlinetExecutorId = projectOnlineApplyMapper.selectProjectOnlineApplyByExecutorId(userId);
			for (ProjectOnlineApply object : listProjectOnlinetExecutorId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("executor_id");// 设为常量
				dto.setTableName("project_online_apply");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					
					if(object.getExecutorId()!=null) {
						String[] strList=object.getExecutorId().split(",");
						for (int i = 0; i < strList.length; i++) {
							 if(strList[i].equals(userId.toString())) {
								 strList[i]= uapUserId+"--";
							 }
						}
						object.setExecutorId(strList.toString());//带处理
						
					}
					
					moveLogTableMapper.insertSelective(dto);
				    projectOnlineApplyMapper.updateByPrimaryKeySelective(object);
				}
			}

			////// project_online_operation_record
			ProjectOnlineOperationRecord projectOnlineOperationRecord = DTOUtils.newDTO(ProjectOnlineOperationRecord.class);
			projectOnlineOperationRecord.setOperatorId(userId);
			List<ProjectOnlineOperationRecord> listprojectOnlineOperationRecord = projectOnlineOperationRecordMapper.select(projectOnlineOperationRecord);
			for (ProjectOnlineOperationRecord object : listprojectOnlineOperationRecord) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("operator_id");// 设为常量
				dto.setTableName("project_online_operation_record");// 设为常量
				//moveLogTableMapper.insertSelective(dto);
				
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setOperatorId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectOnlineOperationRecordMapper.updateByPrimaryKeySelective(object);
				}
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
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setManagerId(uapUserId);
					object.setManagerName(uapUser.getData().getRealName());
					moveLogTableMapper.insertSelective(dto);
				    projectOnlineSubsystemMapper.updateByPrimaryKeySelective(object);
				}
			}

			///// manager_name
			/////// sgq
			projectOnlineSubsystem.setManagerId(null);
			BaseOutput<AlmUser> userTempManagerIdLocal = localUserRpc.findByUserId(userId);
			projectOnlineSubsystem.setManagerName(userTempManagerIdLocal.getData().getRealName());
			
			BaseOutput<User> userTempManagerIdUapRpc= userRpc.findUserById(uapUserId);
			List<ProjectOnlineSubsystem> listprojectManagerName = projectOnlineSubsystemMapper.select(projectOnlineSubsystem);
			for (ProjectOnlineSubsystem object : listprojectManagerName) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("manager_name");// 设为常量
				dto.setTableName("project_online_subsystem");// 设为常量
				
				//object.setManagerName(object.getManagerName().replace(object.getManagerName(), userTempManagerId.getData().getRealName()));
				if(moveLogTableMapper.select(dto).size()==0) {
					
					if(object.getManagerName()!=null) {
						String[] strList=object.getManagerName().split(",");
						for (int i = 0; i < strList.length; i++) {
							 if(strList[i].equals(userTempManagerIdLocal.getData().getRealName())) {
								 strList[i]= userTempManagerIdUapRpc.getData().getRealName()+"--";
							 }
						}
						object.setManagerName(strList.toString());//带处理
						
					}
					
					moveLogTableMapper.insertSelective(dto);
				    projectOnlineSubsystemMapper.updateByPrimaryKeySelective(object);
				}
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
				
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setCreatorId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    projectVersionMapper.updateByPrimaryKeySelective(object);
				}
			}

			projectVersion.setCreatorId(null);
			projectVersion.setModifierId(userId);
			List<ProjectVersion> listprojectModifierId = projectVersionMapper.select(projectVersion);
			for (ProjectVersion object : listprojectModifierId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("modifier_id");// 设为常量
				dto.setTableName("project_version");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setModifierId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				   projectVersionMapper.updateByPrimaryKeySelective(object);
				}
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
				//moveLogTableMapper.insertSelective(dto);
				
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setOwner(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				   taskMapper.updateByPrimaryKeySelective(object);
				}
			}

			task.setOwner(null);
			task.setCreateMemberId(userId);
			List<Task> listTaskCreateMemberId = taskMapper.select(task);
			for (Task object : listTaskCreateMemberId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("create_member_id");// 设为常量
				dto.setTableName("task");// 设为常量
				//moveLogTableMapper.insertSelective(dto);
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setCreateMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    taskMapper.updateByPrimaryKeySelective(object);
				}
			}

			task.setCreateMemberId(null);
			task.setModifyMemberId(userId);
			List<Task> listTaskModifyMemberId = taskMapper.select(task);
			for (Task object : listTaskModifyMemberId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("modify_member_id");// 设为常量
				dto.setTableName("task");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setModifyMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    taskMapper.updateByPrimaryKeySelective(object);
				}
				
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
				
				if(moveLogTableMapper.select(dto).size()==0) {
					 object.setCreateMemberId(uapUserId);
					 moveLogTableMapper.insertSelective(dto);
				     taskDetailsMapper.updateByPrimaryKeySelective(object);
				}
			}

			taskDetails.setCreateMemberId(null);
			taskDetails.setModifyMemberId(userId);
			List<TaskDetails> listtaskDetailsModifyMemberId = taskDetailsMapper.select(taskDetails);
			for (TaskDetails object : listtaskDetailsModifyMemberId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("modify_member_id");// 设为常量
				dto.setTableName("task_details");// 设为常量
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setModifyMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				   taskDetailsMapper.updateByPrimaryKeySelective(object);
				}
				
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
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    teamMapper.updateByPrimaryKeySelective(object);
				}
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
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setApplicantId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    travelCostApplyMapper.updateByPrimaryKeySelective(object);
				}
			}
			
			// 需要重新写
			/// 需要重新写
			//List<Department>  userIdDept=departmentALMRpc.findByUserId(userId).getData();
			//List<Department>  uapUserIdDept=departmentUapRpc.findByUserId(uapUserId).getData();
			
			
		
			
		//	travelCostApply.setRootDepartemntId(userIdDept.get(0).getParentId());
			List<TravelCostApply> listtraveRootDepartemntId = travelCostApplyMapper.selectAll();
			for (TravelCostApply object : listtraveRootDepartemntId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("root_departemnt_id");// 设为常量
				dto.setTableName("travel_cost_apply");// 设为常量
				if(moveLogTableMapper.select(dto).size()==0) {
					if(object.getRootDepartemntId()!=null) {
						if(object.getRootDepartemntId()!=null) {
						  Long  uapDeId=getUapDept(listAlmRpc, listUapRpc, object.getRootDepartemntId().toString());
						   object.setRootDepartemntId(uapDeId);
						}
					}
					moveLogTableMapper.insertSelective(dto);
				    travelCostApplyMapper.updateByPrimaryKeySelective(object);
				}
			}
  
		
			travelCostApply.setRootDepartemntId(null);
			
		
			List<TravelCostApply> travelCostApplyDepartemntId = travelCostApplyMapper.selectAll();
			for (TravelCostApply object : travelCostApplyDepartemntId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("department_id");// 设为常量
				dto.setTableName("travel_cost_apply");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					
					if(object.getDepartmentId()!=null) {
						if(object.getDepartmentId()!=null) {
						  Long  uapDeId=getUapDept(listAlmRpc, listUapRpc, object.getDepartmentId().toString());
						   object.setDepartmentId(uapDeId);
						}
						
					}
					moveLogTableMapper.insertSelective(dto);
				    travelCostApplyMapper.updateByPrimaryKeySelective(object);
				}
			}
			

			////// verify_approval

			VerifyApproval verifyApprova = DTOUtils.newDTO(VerifyApproval.class);
			verifyApprova.setApproveId(userId);
			List<VerifyApproval> listVerifyApproval = verifyApprovalMapper.select(verifyApprova);
			for (VerifyApproval object : listVerifyApproval) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("approver");// 设为常量
				dto.setTableName("verify_approval");// 设为常量
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setApproveId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				  verifyApprovalMapper.updateByPrimaryKeySelective(object);
				}
			}

			verifyApprova.setApproveId(null);
			verifyApprova.setCreateMemberId(userId);

			List<VerifyApproval> listVerifyApprovalCreateMemberId = verifyApprovalMapper.select(verifyApprova);
			for (VerifyApproval object : listVerifyApprovalCreateMemberId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("create_member_id");// 设为常量
				dto.setTableName("verify_approval");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setCreateMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				   verifyApprovalMapper.updateByPrimaryKeySelective(object);
				}
			}

			verifyApprova.setCreateMemberId(null);
			verifyApprova.setModifyMemberId(userId);

			List<VerifyApproval> listVerifyApprovaltModifyMemberId = verifyApprovalMapper.select(verifyApprova);
			for (VerifyApproval object : listVerifyApprovaltModifyMemberId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("modify_member_id");// 设为常量
				dto.setTableName("verify_approval");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setModifyMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    verifyApprovalMapper.updateByPrimaryKeySelective(object);
				}
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
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setCreateMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    weeklyMapper.updateByPrimaryKeySelective(object);
				}
			}

			weekly.setCreateMemberId(null);
			weekly.setModifyMemberId(userId);
			List<Weekly> listWeeklyModifyMemberId = weeklyMapper.select(weekly);
			for (Weekly object : listWeeklyModifyMemberId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("modify_member_id");// 设为常量
				dto.setTableName("weekly");// 设为常量
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setModifyMemberId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
			  	    weeklyMapper.updateByPrimaryKeySelective(object);
				}
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
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setAcceptorId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    workOrderMapper.updateByPrimaryKeySelective(object);
				}
			}

			/// sgq需要重新写 
			weeklyOrder.setAcceptorId(null);
			weeklyOrder.setCopyUserId(userId + "");
			List<WorkOrder> listWeeklyCopyUserId = workOrderMapper.selectWorkOrdeByCopyUserId(userId);
			for (WorkOrder object : listWeeklyCopyUserId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("copy_user_id");// 设为常量
				dto.setTableName("work_order");// 设为常量
				String str=object.getCopyUserId();
				if(object.getCopyUserId()!=null) {
					List<String> list = JSONObject.parseArray(str,String.class);
					
					for (int j = 0; j < list.size(); j++) {
						if(list.get(j).equals(userId.toString())) {
							list.set(j, uapUserId+"--");
						}
					}
					com.alibaba.fastjson.JSONArray  studentJsonArray = JSON.parseArray(JSONObject.toJSONString(list));
					object.setCopyUserId(studentJsonArray.toString());//带处理
					workOrderMapper.updateByPrimaryKeySelective(object);
				}
			  
				if(moveLogTableMapper.select(dto).size()==0) {
					moveLogTableMapper.insertSelective(dto);
				
				}
				
			}

			weeklyOrder.setAcceptorId(null);
			weeklyOrder.setApplicantId(userId);
			List<WorkOrder> listWeeklyOrderOrderApplicantId = workOrderMapper.select(weeklyOrder);
			for (WorkOrder object : listWeeklyOrderOrderApplicantId) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("applicant_id");// 设为常量
				dto.setTableName("work_order");// 设为常量AcceptorId
				//moveLogTableMapper.insertSelective(dto);
				
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setApplicantId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    workOrderMapper.updateByPrimaryKeySelective(object);
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
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setExecutorId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    workOrderMapper.updateByPrimaryKeySelective(object);
				}
			}

			/////// work_order_operation_record

			WorkOrderOperationRecord workOrderOperationRecord = DTOUtils.newDTO(WorkOrderOperationRecord.class);
			workOrderOperationRecord.setOperatorId(userId);

			List<WorkOrderOperationRecord> listworkOrderOperationRecord = workOrderOperationRecordMapper.select(workOrderOperationRecord);
			for (WorkOrderOperationRecord object : listworkOrderOperationRecord) {

				dto =new MoveLogTable();
				dto.setFileId(object.getId());
				dto.setFileField("operator_id");// 设为常量
				dto.setTableName("work_order_operation_record");// 设为常量
				
				
				if(moveLogTableMapper.select(dto).size()==0) {
					object.setOperatorId(uapUserId);
					moveLogTableMapper.insertSelective(dto);
				    workOrderOperationRecordMapper.updateByPrimaryKeySelective(object);
				}
			}
*/
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return -1;
		}
	
	}

	private Long getUapDept(List<DepartmentALM> listAlmRpc, List<Department> listUapRpc, String object) {
		for (DepartmentALM department : listAlmRpc) {
			if(department.getId().toString().equals(object)) {
				for (Department uapDep : listUapRpc) {
					if(department.getCode().equals(uapDep.getCode())) {
						return  uapDep.getId();
					}
					
				}
			}
		}
		
		return null;
	}

	@Override
	public int getDataIsExistence(Long uapUserId) {
		/*//这个时候应收uapUserId ，因为它不变
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
		List<Approve> listCreateMember;
		record.setProjectLeader(uapUserId);
		 listCreateMember = approveMapper.select(record);

		for (Approve approve : listCreateMember) {
			dto =new MoveLogTable();
			dto.setTableName("approve");
			dto.setFileField("project_leader");
			dto.setFileId(approve.getId());
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
			dto.setFileId(approve.getId());
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
			dto.setFileId(approve.getId());
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
			dto.setFileId(approve.getId());
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
			dto.setFileId(approve.getId());
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
			dto.setFileId(filesObject.getId());
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
			dto.setFileId(filesObject.getId());
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
			dto.setFileId(hardwareApplyOperationRecordObject.getId());
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
			dto.setFileId(hardwareResourceObject.getId());
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
			dto.setFileId(logListObject.getId());
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
		BaseOutput<User> userTempManagerId = userRpc.findUserById(uapUserId);
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

		BaseOutput<User> userTemp = userRpc.findUserById(uapUserId);

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
		weeklyOrder.setCopyUserId(uapUserId + "");
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
		weeklyOrder.setExecutorId(uapUserId);
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
		
		List<Approve> listApprove = approveMapper.selectAll();
		Approve approve ;
		for (Approve object : listApprove) {
			if(object.getDescription()!=null) {
				String strList=object.getDescription().replace("--", "");
				//object.setDescription(strList.toString());//带处理
				approve = DTOUtils.newDTO(Approve.class);
				approve.setId(object.getId());
				approve.setDescription(strList.toString());//带处理
				approveMapper.updateByPrimaryKeySelective(approve);
				
			}
		
		}
		
		List<ProjectOnlineApply> listProjectOnlinetExecutorId = projectOnlineApplyMapper.selectAll();
		ProjectOnlineApply   projectOnlineApply;
		for (ProjectOnlineApply object : listProjectOnlinetExecutorId) {
			
			
			if(object.getExecutorId()!=null) {
				String strList=object.getExecutorId().replace("--", "");
				//object.setExecutorId(strList.toString());//带处理
				
				projectOnlineApply = DTOUtils.newDTO(ProjectOnlineApply.class);
				projectOnlineApply.setId(object.getId());
				projectOnlineApply.setExecutorId(strList.toString());//带处理
				projectOnlineApplyMapper.updateByPrimaryKeySelective(projectOnlineApply);
			}
		
			
		}
		
		List<WorkOrder> listWeeklyCopyUserId = workOrderMapper.select(null);
		for (WorkOrder object : listWeeklyCopyUserId) {
		
			String str=object.getCopyUserId();
			if(object.getCopyUserId()!=null) {
			  object.setCopyUserId(str.replace("--",""));
			  workOrderMapper.updateByPrimaryKeySelective(object);
			}
		}
		
	
		List<ProjectOnlineSubsystem> listprojectManagerName = projectOnlineSubsystemMapper.selectAll();
		for (ProjectOnlineSubsystem object : listprojectManagerName) {
			if(object.getManagerName()!=null) {
			  object.setManagerName(object.getManagerName().replace("--", ""));
			  projectOnlineSubsystemMapper.updateByPrimaryKeySelective(object);
			}
		}
		List<ProjectApply> listProjectAll = projectApplyMapper.selectAll();
		ProjectApply projectApply;
		for (ProjectApply object : listProjectAll) {
			String str=object.getResourceRequire();
			//object.setResourceRequire(str.replace("--",""));
			if(str!=null) {
				projectApply = DTOUtils.newDTO(ProjectApply.class);
				projectApply.setId(object.getId());
				projectApply.setResourceRequire(str.replace("--",""));
				projectApplyMapper.updateByPrimaryKeySelective(projectApply);
			}
		}
		
		
		return 1;
	}
//	public static void main(String[] args) {
//		String str="1634,163";
//		String[] strList=str.split(",");
//		for (int i = 0; i < strList.length; i++) {
//			 if(strList[i].equals("163")) {
//				 strList[i]="1227acc";
//			 }
//		}
//		
//		for (String string : strList) {
//			System.out.println(string);
//		}
//	}

}
