package com.dili.alm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dili.alm.dao.ApproveMapper;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.HardwareApplyOperationRecordMapper;
import com.dili.alm.dao.HardwareResourceMapper;
import com.dili.alm.dao.LogMapper;
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
import com.dili.alm.service.DataMigrateService;

@Service
public class DataMigrateImpl implements DataMigrateService {

	@Autowired
	private ApproveMapper approveMapper;

	// dep->部门
	// create_member_id->用户 //
	// modify_member_id->用户 //

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

	// dep->部门
	// business_owner->用户

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

	@Override
	public int updateData(Long id) {

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

}
