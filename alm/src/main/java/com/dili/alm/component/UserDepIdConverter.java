package com.dili.alm.component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
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
import com.dili.alm.domain.dto.apply.ApplyApprove;
import com.dili.alm.rpc.AlmUserRpc;
import com.dili.alm.rpc.DepartmentALMRpc;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.session.SessionContext;

@Component
public class UserDepIdConverter implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDepIdConverter.class);

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
	private AlmUserRpc localUserRpc;

	@Autowired
	private UserRpc userRpc;

	@Autowired
	private DepartmentRpc departmentUapRpc;
	@Autowired
	private DepartmentALMRpc departmentALMRpc;

	@Transactional
	@Override
	public void afterPropertiesSet() throws Exception {
		LOGGER.info("开始数据迁移........");
		BaseOutput<List<User>> uapOutput = this.userRpc.listByExample(DTOUtils.newDTO(User.class));
		if (!uapOutput.isSuccess()) {
			LOGGER.error(uapOutput.getMessage());
			return;
		}
		List<User> uapUsers = uapOutput.getData();
		BaseOutput<List<AlmUser>> almOutput = this.localUserRpc.list(new AlmUser());
		if (!almOutput.isSuccess()) {
			LOGGER.error(almOutput.getMessage());
			return;
		}
		List<AlmUser> almUsers = almOutput.getData();

		// approve表
		this.approveMapper.selectAll().forEach(a -> {

			// businessOwner
			final Long almBusinessOwnerId = a.getBusinessOwner();
			if (almBusinessOwnerId != null) {
				final AlmUser almBusinessOwner = almUsers.stream().filter(u -> u.getId().equals(almBusinessOwnerId)).findFirst().orElse(null);
				if (almBusinessOwner == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almBusinessOwnerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almBusinessOwner.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almBusinessOwner.getUserName()));
					return;
				}
				a.setBusinessOwner(uapUser.getId());
			}

			// createMemberId
			final Long almCreateMemberId = a.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almCreateMemberId));
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almCreateMember.getUserName()));
				}
				a.setCreateMemberId(uapUser.getId());
			}

			// projectLeader
			final Long almProjectLeaderId = a.getProjectLeader();
			if (almProjectLeaderId != null) {
				final AlmUser almProjectLeader = almUsers.stream().filter(u -> u.getId().equals(almProjectLeaderId)).findFirst().orElse(null);
				if (almProjectLeader == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almProjectLeaderId));
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProjectLeader.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almProjectLeader.getUserName()));
				}
				a.setProjectLeader(uapUser.getId());
			}

			// projectLeader
			if (StringUtils.isNotBlank(a.getDescription())) {
				List<ApplyApprove> approveList = JSON.parseArray(a.getDescription(), ApplyApprove.class);
				approveList.forEach(app -> {
					final Long almUserId = app.getUserId();
					if (almUserId != null) {
						final AlmUser almUser = almUsers.stream().filter(u -> u.getId().equals(almUserId)).findFirst().orElse(null);
						if (almUser == null) {
							LOGGER.warn(String.format("未找到id为%d的alm用户", almUserId));
						}
						User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almUser.getUserName())).findFirst().orElse(null);
						if (uapUser == null) {
							LOGGER.warn(String.format("未找到username为%s的uap用户", almUser.getUserName()));
						}
						app.setUserId(uapUser.getId());
					}
				});
				a.setDescription(JSON.toJSONString(approveList));
			}
			this.approveMapper.updateByPrimaryKey(a);
		});
		LOGGER.info("数据迁移完毕........");
	}

}
