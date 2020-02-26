package com.dili.alm.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dili.alm.dao.ApproveMapper;
import com.dili.alm.dao.FilesMapper;
import com.dili.alm.dao.HardwareApplyOperationRecordMapper;
import com.dili.alm.dao.HardwareResourceApplyMapper;
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
import com.dili.alm.domain.dto.apply.ApplyMajorResource;
import com.dili.alm.rpc.AlmUserRpc;
import com.dili.alm.rpc.DepartmentALMRpc;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.User;

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
	private HardwareResourceApplyMapper hardwareResourceApplyMapper;

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
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProjectLeader.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almProjectLeader.getUserName()));
					return;
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
							return;
						}
						User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almUser.getUserName())).findFirst().orElse(null);
						if (uapUser == null) {
							LOGGER.warn(String.format("未找到username为%s的uap用户", almUser.getUserName()));
							return;
						}
						app.setUserId(uapUser.getId());
					}
				});
				a.setDescription(JSON.toJSONString(approveList));
			}
			this.approveMapper.updateByPrimaryKey(a);
		});

		// files表
		this.filesMapper.selectAll().forEach(f -> {
			// createMemberId
			final Long almCreateMemberId = f.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almCreateMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almCreateMember.getUserName()));
					return;
				}
				f.setCreateMemberId(uapUser.getId());
			}
			this.filesMapper.updateByPrimaryKey(f);
		});

		// hardwareApplyOerationRecord表
		this.hardwareApplyOperationRecordMapper.selectAll().forEach(h -> {
			// createMemberId
			final Long almOperatorId = h.getOperatorId();
			if (almOperatorId != null) {
				final AlmUser almOperator = almUsers.stream().filter(u -> u.getId().equals(almOperatorId)).findFirst().orElse(null);
				if (almOperator == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almOperatorId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almOperator.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almOperator.getUserName()));
					return;
				}
				h.setOperatorId(uapUser.getId());
			}
			this.hardwareApplyOperationRecordMapper.updateByPrimaryKey(h);
		});

		// hardwareResourceApply表
		this.hardwareResourceApplyMapper.selectAll().forEach(h -> {
			// applicantId
			final Long almApplicantId = h.getApplicantId();
			if (almApplicantId != null) {
				final AlmUser almApplicant = almUsers.stream().filter(u -> u.getId().equals(almApplicantId)).findFirst().orElse(null);
				if (almApplicant == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almApplicantId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almApplicant.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almApplicant.getUserName()));
					return;
				}
				h.setApplicantId(uapUser.getId());
			}
			this.hardwareResourceApplyMapper.updateByPrimaryKey(h);

			// executors
			if (StringUtils.isNotBlank(h.getExecutors())) {
				List<Long> executors = JSON.parseObject(h.getExecutors(), new TypeReference<List<Long>>() {
				});
				List<Long> target = new ArrayList<>(executors.size());
				executors.forEach(e -> {
					final AlmUser executor = almUsers.stream().filter(u -> u.getId().equals(e)).findFirst().orElse(null);
					if (executor == null) {
						LOGGER.warn(String.format("未找到id为%d的alm用户", e));
						return;
					}
					User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(executor.getUserName())).findFirst().orElse(null);
					if (uapUser == null) {
						LOGGER.warn(String.format("未找到username为%s的uap用户", executor.getUserName()));
						return;
					}
					target.add(executor.getId());
				});
				h.setExecutors(JSON.toJSONString(target));
			}
			this.hardwareResourceApplyMapper.updateByPrimaryKey(h);
		});

		// hardwareResource表
		this.hardwareResourceMapper.selectAll().forEach(h -> {
			// maintenanceOwnerId
			final Long almMaintenanceOwnerId = h.getMaintenanceOwner();
			if (almMaintenanceOwnerId != null) {
				final AlmUser almMaintenanceOwner = almUsers.stream().filter(u -> u.getId().equals(almMaintenanceOwnerId)).findFirst().orElse(null);
				if (almMaintenanceOwner == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almMaintenanceOwnerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almMaintenanceOwner.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almMaintenanceOwner.getUserName()));
					return;
				}
				h.setMaintenanceOwner(uapUser.getId());
			}
			this.hardwareResourceMapper.updateByPrimaryKey(h);
		});

		// log表
		this.logMapper.selectAll().forEach(l -> {
			// operatorId
			final Long almOperatorId = l.getOperatorId();
			if (almOperatorId != null) {
				final AlmUser almOperator = almUsers.stream().filter(u -> u.getId().equals(almOperatorId)).findFirst().orElse(null);
				if (almOperator == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almOperatorId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almOperator.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almOperator.getUserName()));
					return;
				}
				l.setOperatorId(uapUser.getId());
			}
			this.logMapper.updateByPrimaryKey(l);
		});

		// log表
		this.projectApplyMapper.selectAll().forEach(a -> {
			// businessOwnerId
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
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almCreateMember.getUserName()));
					return;
				}
				a.setCreateMemberId(uapUser.getId());
			}

			// developmentManager
			final Long almDevelopmentManagerId = a.getDevelopmentManager();
			if (almDevelopmentManagerId != null) {
				final AlmUser almDevelopmentManager = almUsers.stream().filter(u -> u.getId().equals(almDevelopmentManagerId)).findFirst().orElse(null);
				if (almDevelopmentManager == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almDevelopmentManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almDevelopmentManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almDevelopmentManager.getUserName()));
					return;
				}
				a.setDevelopmentManager(uapUser.getId());
			}

			// productManager
			final Long almgProductManagerId = a.getProductManager();
			if (almgProductManagerId != null) {
				final AlmUser almProductManager = almUsers.stream().filter(u -> u.getId().equals(almgProductManagerId)).findFirst().orElse(null);
				if (almProductManager == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almgProductManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProductManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almProductManager.getUserName()));
					return;
				}
				a.setProductManager(uapUser.getId());
			}

			// projectLeader
			final Long almProjectLeaderId = a.getProjectLeader();
			if (almProjectLeaderId != null) {
				final AlmUser almProjectLeader = almUsers.stream().filter(u -> u.getId().equals(almProjectLeaderId)).findFirst().orElse(null);
				if (almProjectLeader == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almProjectLeaderId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProjectLeader.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almProjectLeader.getUserName()));
					return;
				}
				a.setProjectLeader(uapUser.getId());
			}

			// testManager
			final Long almTestManagerId = a.getTestManager();
			if (almTestManagerId != null) {
				final AlmUser almTestManager = almUsers.stream().filter(u -> u.getId().equals(almTestManagerId)).findFirst().orElse(null);
				if (almTestManager == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almTestManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almTestManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almTestManager.getUserName()));
					return;
				}
				a.setTestManager(uapUser.getId());
			}

			// resourceRequire
			if (StringUtils.isNotBlank(a.getResourceRequire())) {
				ApplyMajorResource resourceRequire = JSON.parseObject(a.getResourceRequire(), ApplyMajorResource.class);
				Long almMainUserId = Long.valueOf(resourceRequire.getMainUser());
				if (almMainUserId != null) {
					final AlmUser almUser = almUsers.stream().filter(u -> u.getId().equals(almMainUserId)).findFirst().orElse(null);
					if (almUser == null) {
						LOGGER.warn(String.format("未找到id为%d的alm用户", almMainUserId));
						return;
					}
					User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almUser.getUserName())).findFirst().orElse(null);
					if (uapUser == null) {
						LOGGER.warn(String.format("未找到username为%s的uap用户", almUser.getUserName()));
						return;
					}
					resourceRequire.setMainUser(uapUser.getId());
				}
				resourceRequire.getRelatedResources().forEach(r -> {
					final Long almUserId = Long.valueOf(r.getRelatedUser());
					if (almUserId != null) {
						final AlmUser almUser = almUsers.stream().filter(u -> u.getId().equals(almUserId)).findFirst().orElse(null);
						if (almUser == null) {
							LOGGER.warn(String.format("未找到id为%d的alm用户", almUserId));
							return;
						}
						User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almUser.getUserName())).findFirst().orElse(null);
						if (uapUser == null) {
							LOGGER.warn(String.format("未找到username为%s的uap用户", almUser.getUserName()));
							return;
						}
						r.setRelatedUser(uapUser.getId().toString());
					}
				});
				a.setResourceRequire(JSON.toJSONString(resourceRequire));
			}

			this.projectApplyMapper.updateByPrimaryKey(a);
		});

		// projectChange表
		this.projectChangeMapper.selectAll().forEach(c -> {
			// createMemberId
			final Long almCreateMemberId = c.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almCreateMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almCreateMember.getUserName()));
					return;
				}
				c.setCreateMemberId(uapUser.getId());
			}

			this.projectChangeMapper.updateByPrimaryKey(c);
		});

		// projectComplete表
		this.projectCompleteMapper.selectAll().forEach(c -> {
			// createMemberId
			final Long almCreateMemberId = c.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almCreateMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almCreateMember.getUserName()));
					return;
				}
				c.setCreateMemberId(uapUser.getId());
			}

			this.projectCompleteMapper.updateByPrimaryKey(c);
		});

		// projectOnlineApply表
		this.projectOnlineApplyMapper.selectAll().forEach(p -> {
			// applicantId
			final Long almApplicantId = p.getApplicantId();
			if (almApplicantId != null) {
				final AlmUser almApplicant = almUsers.stream().filter(u -> u.getId().equals(almApplicantId)).findFirst().orElse(null);
				if (almApplicant == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almApplicantId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almApplicant.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almApplicant.getUserName()));
					return;
				}
				p.setApplicantId(uapUser.getId());
			}

			// businessOwnerId
			final Long almBusinessOwnerId = p.getBusinessOwnerId();
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
				p.setBusinessOwnerId(uapUser.getId());
			}

			// developmentManagerId
			final Long almDevelopmentManagerId = p.getDevelopmentManagerId();
			if (almDevelopmentManagerId != null) {
				final AlmUser almDevelopmentManager = almUsers.stream().filter(u -> u.getId().equals(almDevelopmentManagerId)).findFirst().orElse(null);
				if (almDevelopmentManager == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almDevelopmentManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almDevelopmentManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almDevelopmentManager.getUserName()));
					return;
				}
				p.setDevelopmentManagerId(uapUser.getId());
			}

			this.projectOnlineApplyMapper.updateByPrimaryKey(p);

			// productManagerId
			final Long almProductManagerId = p.getProductManagerId();
			if (almProductManagerId != null) {
				final AlmUser almProductManager = almUsers.stream().filter(u -> u.getId().equals(almProductManagerId)).findFirst().orElse(null);
				if (almProductManager == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almProductManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProductManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almProductManager.getUserName()));
					return;
				}
				p.setProductManagerId(uapUser.getId());
			}

			// projectManagerId
			final Long almProjectManagerId = p.getProjectManagerId();
			if (almProjectManagerId != null) {
				final AlmUser almProjectManager = almUsers.stream().filter(u -> u.getId().equals(almProjectManagerId)).findFirst().orElse(null);
				if (almProjectManager == null) {
					LOGGER.warn(String.format("未找到id为%d的alm用户", almProjectManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProjectManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("未找到username为%s的uap用户", almProjectManager.getUserName()));
					return;
				}
				p.setProductManagerId(uapUser.getId());
			}

			this.projectOnlineApplyMapper.updateByPrimaryKey(p);
		});
		LOGGER.info("数据迁移完毕........");
	}

}
