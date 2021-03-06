package com.dili.alm.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.dili.alm.rpc.dto.AlmDepartmentDto;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.dto.UserQuery;
import com.dili.uap.sdk.rpc.DepartmentRpc;
import com.dili.uap.sdk.rpc.UserRpc;

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
	private AlmUserRpc localUserRpc;

	@Autowired
	private UserRpc userRpc;

	@Autowired
	private DepartmentRpc departmentUapRpc;
	@Autowired
	private DepartmentALMRpc departmentALMRpc;

	@Value("${com.dili.alm.userDataConvertSwitch:false}")
	private Boolean userDataConvertSwitch;

	@Transactional
	@Override
	public void afterPropertiesSet() throws Exception {
		if (!this.userDataConvertSwitch) {
			return;
		}
		LOGGER.info("??????????????????........");
		BaseOutput<List<User>> uapOutput = this.userRpc.listByExample(DTOUtils.newDTO(UserQuery.class));
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

		Department uapDepQuery = DTOUtils.newDTO(Department.class);
		uapDepQuery.setFirmCode("szpt");
		BaseOutput<List<Department>> uapDepOutput = this.departmentUapRpc.listByDepartment(uapDepQuery);
		if (!uapDepOutput.isSuccess()) {
			LOGGER.error(uapDepOutput.getMessage());
			return;
		}
		List<Department> uapDepartments = uapDepOutput.getData();

		BaseOutput<List<AlmDepartmentDto>> almDepOutput = this.departmentALMRpc.list(new AlmDepartmentDto());
		if (!almDepOutput.isSuccess()) {
			LOGGER.error(almDepOutput.getMessage());
			return;
		}
		List<AlmDepartmentDto> almDepartments = almDepOutput.getData();

		LOGGER.info("????????????approve???.......");
		// approve???
		this.approveMapper.selectAll().forEach(a -> {

			// businessOwner
			final Long almBusinessOwnerId = a.getBusinessOwner();
			if (almBusinessOwnerId != null) {
				final AlmUser almBusinessOwner = almUsers.stream().filter(u -> u.getId().equals(almBusinessOwnerId)).findFirst().orElse(null);
				if (almBusinessOwner == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almBusinessOwnerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almBusinessOwner.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almBusinessOwner.getUserName()));
					return;
				}
				a.setBusinessOwner(uapUser.getId());
			}

			// createMemberId
			final Long almCreateMemberId = a.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almCreateMemberId));
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almCreateMember.getUserName()));
				}
				a.setCreateMemberId(uapUser.getId());
			}

			// projectLeader
			final Long almProjectLeaderId = a.getProjectLeader();
			if (almProjectLeaderId != null) {
				final AlmUser almProjectLeader = almUsers.stream().filter(u -> u.getId().equals(almProjectLeaderId)).findFirst().orElse(null);
				if (almProjectLeader == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almProjectLeaderId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProjectLeader.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almProjectLeader.getUserName()));
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
							LOGGER.warn(String.format("?????????id???%d???alm??????", almUserId));
							return;
						}
						User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almUser.getUserName())).findFirst().orElse(null);
						if (uapUser == null) {
							LOGGER.warn(String.format("?????????username???%s???uap??????", almUser.getUserName()));
							return;
						}
						app.setUserId(uapUser.getId());
					}
				});
				a.setDescription(JSON.toJSONString(approveList));
			}

			// dep
			final Long almDepId = a.getDep();
			if (almDepId != null) {
				AlmDepartmentDto almDep = almDepartments.stream().filter(d -> d.getId().equals(almDepId)).findFirst().orElse(null);
				if (almDep == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almDepId));
					return;
				}
				Department uapDep = uapDepartments.stream().filter(d -> d.getCode().equals(almDep.getCode())).findFirst().orElse(null);
				if (uapDep == null) {
					LOGGER.warn(String.format("?????????code???%s???uap??????", almDep.getCode()));
					return;
				}
				a.setDep(uapDep.getId());
			}

			this.approveMapper.updateByPrimaryKey(a);
		});

		LOGGER.info("????????????files???.......");
		// files???
		this.filesMapper.selectAll().forEach(f -> {
			// createMemberId
			final Long almCreateMemberId = f.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almCreateMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almCreateMember.getUserName()));
					return;
				}
				f.setCreateMemberId(uapUser.getId());
			}
			this.filesMapper.updateByPrimaryKey(f);
		});

		LOGGER.info("????????????hardwareApplyOerationRecord???.......");
		// hardwareApplyOerationRecord???
		this.hardwareApplyOperationRecordMapper.selectAll().forEach(h -> {
			// createMemberId
			final Long almOperatorId = h.getOperatorId();
			if (almOperatorId != null) {
				final AlmUser almOperator = almUsers.stream().filter(u -> u.getId().equals(almOperatorId)).findFirst().orElse(null);
				if (almOperator == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almOperatorId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almOperator.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almOperator.getUserName()));
					return;
				}
				h.setOperatorId(uapUser.getId());
			}
			this.hardwareApplyOperationRecordMapper.updateByPrimaryKey(h);
		});

		LOGGER.info("????????????hardwareResourceApply???.......");
		// hardwareResourceApply???
		this.hardwareResourceApplyMapper.selectAll().forEach(h -> {
			// applicantId
			final Long almApplicantId = h.getApplicantId();
			if (almApplicantId != null) {
				final AlmUser almApplicant = almUsers.stream().filter(u -> u.getId().equals(almApplicantId)).findFirst().orElse(null);
				if (almApplicant == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almApplicantId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almApplicant.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almApplicant.getUserName()));
					return;
				}
				h.setApplicantId(uapUser.getId());
			}

			// projectManagerId
			final Long almProjectManagerId = h.getProjectManagerId();
			if (almProjectManagerId != null) {
				final AlmUser almApplicant = almUsers.stream().filter(u -> u.getId().equals(almProjectManagerId)).findFirst().orElse(null);
				if (almApplicant == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almProjectManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almApplicant.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almApplicant.getUserName()));
					return;
				}
				h.setProjectManagerId(uapUser.getId());
			}

			// dep
			final Long almDepId = h.getApplicationDepartmentId();
			if (almDepId != null) {
				AlmDepartmentDto almDep = almDepartments.stream().filter(d -> d.getId().equals(almDepId)).findFirst().orElse(null);
				if (almDep == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almDepId));
					return;
				}
				Department uapDep = uapDepartments.stream().filter(d -> d.getCode().equals(almDep.getCode())).findFirst().orElse(null);
				if (uapDep == null) {
					LOGGER.warn(String.format("?????????code???%s???uap??????", almDep.getCode()));
					return;
				}
				h.setApplicationDepartmentId(uapDep.getId());
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
						LOGGER.warn(String.format("?????????id???%d???alm??????", e));
						return;
					}
					User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(executor.getUserName())).findFirst().orElse(null);
					if (uapUser == null) {
						LOGGER.warn(String.format("?????????username???%s???uap??????", executor.getUserName()));
						return;
					}
					target.add(uapUser.getId());
				});
				h.setExecutors(JSON.toJSONString(target));
			}
			this.hardwareResourceApplyMapper.updateByPrimaryKey(h);
		});

		LOGGER.info("????????????hardwareResource???.......");
		// hardwareResource???
		this.hardwareResourceMapper.selectAll().forEach(h -> {
			// maintenanceOwnerId
			final Long almMaintenanceOwnerId = h.getMaintenanceOwner();
			if (almMaintenanceOwnerId != null) {
				final AlmUser almMaintenanceOwner = almUsers.stream().filter(u -> u.getId().equals(almMaintenanceOwnerId)).findFirst().orElse(null);
				if (almMaintenanceOwner == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almMaintenanceOwnerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almMaintenanceOwner.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almMaintenanceOwner.getUserName()));
					return;
				}
				h.setMaintenanceOwner(uapUser.getId());
			}
			this.hardwareResourceMapper.updateByPrimaryKey(h);
		});

		LOGGER.info("????????????log???.......");
		// log???
		this.logMapper.selectAll().forEach(l -> {
			// operatorId
			final Long almOperatorId = l.getOperatorId();
			if (almOperatorId != null) {
				final AlmUser almOperator = almUsers.stream().filter(u -> u.getId().equals(almOperatorId)).findFirst().orElse(null);
				if (almOperator == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almOperatorId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almOperator.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almOperator.getUserName()));
					return;
				}
				l.setOperatorId(uapUser.getId());
			}
			this.logMapper.updateByPrimaryKey(l);
		});

		LOGGER.info("????????????project???.......");
		// project???
		this.projectMapper.selectAll().forEach(p -> {
			// businessOwnerId
			final Long almBusinessOwnerId = p.getBusinessOwner();
			if (almBusinessOwnerId != null) {
				final AlmUser almBusinessOwner = almUsers.stream().filter(u -> u.getId().equals(almBusinessOwnerId)).findFirst().orElse(null);
				if (almBusinessOwner == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almBusinessOwnerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almBusinessOwner.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almBusinessOwner.getUserName()));
					return;
				}
				p.setBusinessOwner(uapUser.getId());
			}

			// developmentManager
			final Long almDevelopmentManagerId = p.getDevelopManager();
			if (almDevelopmentManagerId != null) {
				final AlmUser almDevelopmentManager = almUsers.stream().filter(u -> u.getId().equals(almDevelopmentManagerId)).findFirst().orElse(null);
				if (almDevelopmentManager == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almDevelopmentManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almDevelopmentManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almDevelopmentManager.getUserName()));
					return;
				}
				p.setDevelopManager(uapUser.getId());
			}

			// productManager
			final Long almgProductManagerId = p.getProductManager();
			if (almgProductManagerId != null) {
				final AlmUser almProductManager = almUsers.stream().filter(u -> u.getId().equals(almgProductManagerId)).findFirst().orElse(null);
				if (almProductManager == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almgProductManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProductManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almProductManager.getUserName()));
					return;
				}
				p.setProductManager(uapUser.getId());
			}

			// projectManager
			final Long almProjectManagerId = p.getProjectManager();
			if (almProjectManagerId != null) {
				final AlmUser almProjectManager = almUsers.stream().filter(u -> u.getId().equals(almProjectManagerId)).findFirst().orElse(null);
				if (almProjectManager == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almProjectManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProjectManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almProjectManager.getUserName()));
					return;
				}
				p.setProjectManager(uapUser.getId());
			}

			// testManager
			final Long almTestManagerId = p.getTestManager();
			if (almTestManagerId != null) {
				final AlmUser almTestManager = almUsers.stream().filter(u -> u.getId().equals(almTestManagerId)).findFirst().orElse(null);
				if (almTestManager == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almTestManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almTestManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almTestManager.getUserName()));
					return;
				}
				p.setTestManager(uapUser.getId());
			}

			// testManager
			final Long almOriginatorId = p.getOriginator();
			if (almOriginatorId != null) {
				final AlmUser almOriginator = almUsers.stream().filter(u -> u.getId().equals(almOriginatorId)).findFirst().orElse(null);
				if (almOriginator == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almOriginatorId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almOriginator.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almOriginator.getUserName()));
					return;
				}
				p.setOriginator(uapUser.getId());
			}

			// dep
			final Long almDepId = p.getDep();
			if (almDepId != null) {
				AlmDepartmentDto almDep = almDepartments.stream().filter(d -> d.getId().equals(almDepId)).findFirst().orElse(null);
				if (almDep == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almDepId));
					return;
				}
				Department uapDep = uapDepartments.stream().filter(d -> d.getCode().equals(almDep.getCode())).findFirst().orElse(null);
				if (uapDep == null) {
					LOGGER.warn(String.format("?????????code???%s???uap??????", almDep.getCode()));
					return;
				}
				p.setDep(uapDep.getId());
			}

			this.projectMapper.updateByPrimaryKey(p);
		});

		LOGGER.info("????????????projectApply???.......");
		// projectApply???
		this.projectApplyMapper.selectAll().forEach(a -> {
			// businessOwnerId
			final Long almBusinessOwnerId = a.getBusinessOwner();
			if (almBusinessOwnerId != null) {
				final AlmUser almBusinessOwner = almUsers.stream().filter(u -> u.getId().equals(almBusinessOwnerId)).findFirst().orElse(null);
				if (almBusinessOwner == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almBusinessOwnerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almBusinessOwner.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almBusinessOwner.getUserName()));
					return;
				}
				a.setBusinessOwner(uapUser.getId());
			}

			// createMemberId
			final Long almCreateMemberId = a.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almCreateMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almCreateMember.getUserName()));
					return;
				}
				a.setCreateMemberId(uapUser.getId());
			}

			// developmentManager
			final Long almDevelopmentManagerId = a.getDevelopmentManager();
			if (almDevelopmentManagerId != null) {
				final AlmUser almDevelopmentManager = almUsers.stream().filter(u -> u.getId().equals(almDevelopmentManagerId)).findFirst().orElse(null);
				if (almDevelopmentManager == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almDevelopmentManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almDevelopmentManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almDevelopmentManager.getUserName()));
					return;
				}
				a.setDevelopmentManager(uapUser.getId());
			}

			// productManager
			final Long almgProductManagerId = a.getProductManager();
			if (almgProductManagerId != null) {
				final AlmUser almProductManager = almUsers.stream().filter(u -> u.getId().equals(almgProductManagerId)).findFirst().orElse(null);
				if (almProductManager == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almgProductManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProductManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almProductManager.getUserName()));
					return;
				}
				a.setProductManager(uapUser.getId());
			}

			// projectLeader
			final Long almProjectLeaderId = a.getProjectLeader();
			if (almProjectLeaderId != null) {
				final AlmUser almProjectLeader = almUsers.stream().filter(u -> u.getId().equals(almProjectLeaderId)).findFirst().orElse(null);
				if (almProjectLeader == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almProjectLeaderId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProjectLeader.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almProjectLeader.getUserName()));
					return;
				}
				a.setProjectLeader(uapUser.getId());
			}

			// testManager
			final Long almTestManagerId = a.getTestManager();
			if (almTestManagerId != null) {
				final AlmUser almTestManager = almUsers.stream().filter(u -> u.getId().equals(almTestManagerId)).findFirst().orElse(null);
				if (almTestManager == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almTestManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almTestManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almTestManager.getUserName()));
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
						LOGGER.warn(String.format("?????????id???%d???alm??????", almMainUserId));
						return;
					}
					User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almUser.getUserName())).findFirst().orElse(null);
					if (uapUser == null) {
						LOGGER.warn(String.format("?????????username???%s???uap??????", almUser.getUserName()));
						return;
					}
					resourceRequire.setMainUser(uapUser.getId());
				}
				if (CollectionUtils.isNotEmpty(resourceRequire.getRelatedResources())) {
					resourceRequire.getRelatedResources().forEach(r -> {
						if (r.getRelatedUser() == null) {
							LOGGER.warn(String.format("?????????????????????????????????%s", JSON.toJSONString(r)));
							return;
						}
						final Long almUserId = Long.valueOf(r.getRelatedUser());
						if (almUserId != null) {
							final AlmUser almUser = almUsers.stream().filter(u -> u.getId().equals(almUserId)).findFirst().orElse(null);
							if (almUser == null) {
								LOGGER.warn(String.format("?????????id???%d???alm??????", almUserId));
								return;
							}
							User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almUser.getUserName())).findFirst().orElse(null);
							if (uapUser == null) {
								LOGGER.warn(String.format("?????????username???%s???uap??????", almUser.getUserName()));
								return;
							}
							r.setRelatedUser(uapUser.getId().toString());
						}
					});
				}
				a.setResourceRequire(JSON.toJSONString(resourceRequire));
			}

			// dep
			final Long almDepId = a.getDep();
			if (almDepId != null) {
				AlmDepartmentDto almDep = almDepartments.stream().filter(d -> d.getId().equals(almDepId)).findFirst().orElse(null);
				if (almDep == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almDepId));
					return;
				}
				Department uapDep = uapDepartments.stream().filter(d -> d.getCode().equals(almDep.getCode())).findFirst().orElse(null);
				if (uapDep == null) {
					LOGGER.warn(String.format("?????????code???%s???uap??????", almDep.getCode()));
					return;
				}
				a.setDep(uapDep.getId());
			}

			this.projectApplyMapper.updateByPrimaryKey(a);
		});

		LOGGER.info("????????????projectChange???.......");
		// projectChange???
		this.projectChangeMapper.selectAll().forEach(c -> {
			// createMemberId
			final Long almCreateMemberId = c.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almCreateMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almCreateMember.getUserName()));
					return;
				}
				c.setCreateMemberId(uapUser.getId());
			}

			this.projectChangeMapper.updateByPrimaryKey(c);
		});

		LOGGER.info("????????????projectComplete???.......");
		// projectComplete???
		this.projectCompleteMapper.selectAll().forEach(c -> {
			// createMemberId
			final Long almCreateMemberId = c.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almCreateMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almCreateMember.getUserName()));
					return;
				}
				c.setCreateMemberId(uapUser.getId());
			}

			this.projectCompleteMapper.updateByPrimaryKey(c);
		});

		LOGGER.info("????????????projectOnlineApply???.......");
		// projectOnlineApply???
		this.projectOnlineApplyMapper.selectAll().forEach(p -> {

			// applicantId
			final Long almApplicantId = p.getApplicantId();
			if (almApplicantId != null) {
				final AlmUser almApplicant = almUsers.stream().filter(u -> u.getId().equals(almApplicantId)).findFirst().orElse(null);
				if (almApplicant == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almApplicantId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almApplicant.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almApplicant.getUserName()));
					return;
				}
				p.setApplicantId(uapUser.getId());
			}

			// businessOwnerId
			final Long almBusinessOwnerId = p.getBusinessOwnerId();
			if (almBusinessOwnerId != null) {
				final AlmUser almBusinessOwner = almUsers.stream().filter(u -> u.getId().equals(almBusinessOwnerId)).findFirst().orElse(null);
				if (almBusinessOwner == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almBusinessOwnerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almBusinessOwner.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almBusinessOwner.getUserName()));
					return;
				}
				p.setBusinessOwnerId(uapUser.getId());
			}

			// developmentManagerId
			final Long almDevelopmentManagerId = p.getDevelopmentManagerId();
			if (almDevelopmentManagerId != null) {
				final AlmUser almDevelopmentManager = almUsers.stream().filter(u -> u.getId().equals(almDevelopmentManagerId)).findFirst().orElse(null);
				if (almDevelopmentManager == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almDevelopmentManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almDevelopmentManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almDevelopmentManager.getUserName()));
					return;
				}
				p.setDevelopmentManagerId(uapUser.getId());
			}

			// productManagerId
			final Long almProductManagerId = p.getProductManagerId();
			if (almProductManagerId != null) {
				final AlmUser almProductManager = almUsers.stream().filter(u -> u.getId().equals(almProductManagerId)).findFirst().orElse(null);
				if (almProductManager == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almProductManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProductManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almProductManager.getUserName()));
					return;
				}
				p.setProductManagerId(uapUser.getId());
			}

			// projectManagerId
			final Long almProjectManagerId = p.getProjectManagerId();
			if (almProjectManagerId != null) {
				final AlmUser almProjectManager = almUsers.stream().filter(u -> u.getId().equals(almProjectManagerId)).findFirst().orElse(null);
				if (almProjectManager == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almProjectManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almProjectManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almProjectManager.getUserName()));
					return;
				}
				p.setProjectManagerId(uapUser.getId());
			}

			// testManagerId
			final Long almTestManagerId = p.getTestManagerId();
			if (almTestManagerId != null) {
				final AlmUser almTestManager = almUsers.stream().filter(u -> u.getId().equals(almTestManagerId)).findFirst().orElse(null);
				if (almTestManager == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almTestManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almTestManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almTestManager.getUserName()));
					return;
				}
				p.setTestManagerId(uapUser.getId());
			}

			// executors
			if (StringUtils.isNotBlank(p.getExecutorId())) {
				List<String> executors = Arrays.asList(p.getExecutorId().split(","));
				StringBuilder sb = new StringBuilder();
				executors.forEach(e -> {
					final AlmUser almExecutor = almUsers.stream().filter(u -> u.getId().equals(Long.valueOf(e))).findFirst().orElse(null);
					if (almExecutor == null) {
						LOGGER.warn(String.format("?????????id???%s???alm??????", e));
						return;
					}
					User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almExecutor.getUserName())).findFirst().orElse(null);
					if (uapUser == null) {
						LOGGER.warn(String.format("?????????username???%s???uap??????", almExecutor.getUserName()));
						return;
					}
					sb.append(uapUser.getId()).append(",");
				});
				p.setExecutorId(sb.substring(0, sb.length() - 1));
			}

			this.projectOnlineApplyMapper.updateByPrimaryKey(p);
		});

		LOGGER.info("????????????projectOnlineOperationRecord???.......");
		// projectOnlineOperationRecord???
		this.projectOnlineOperationRecordMapper.selectAll().forEach(p -> {
			// operatorId
			final Long almOperatorId = p.getOperatorId();
			if (almOperatorId != null) {
				final AlmUser almOperator = almUsers.stream().filter(u -> u.getId().equals(almOperatorId)).findFirst().orElse(null);
				if (almOperator == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almOperatorId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almOperator.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almOperator.getUserName()));
					return;
				}
				p.setOperatorId(uapUser.getId());
			}

			this.projectOnlineOperationRecordMapper.updateByPrimaryKey(p);
		});

		LOGGER.info("????????????projectOnlineSubsystem???.......");
		// projectOnlineSubsystem???
		this.projectOnlineSubsystemMapper.selectAll().forEach(p -> {
			// managerId
			final Long almManagerId = p.getManagerId();
			if (almManagerId != null) {
				final AlmUser almManager = almUsers.stream().filter(u -> u.getId().equals(almManagerId)).findFirst().orElse(null);
				if (almManager == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almManagerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almManager.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almManager.getUserName()));
					return;
				}
				p.setManagerId(uapUser.getId());
			}

			this.projectOnlineSubsystemMapper.updateByPrimaryKey(p);
		});

		LOGGER.info("????????????projectVersion???.......");
		// projectVersion???
		this.projectVersionMapper.selectAll().forEach(p -> {
			// creatorId
			final Long almCreatorId = p.getCreatorId();
			if (almCreatorId != null) {
				final AlmUser almCreator = almUsers.stream().filter(u -> u.getId().equals(almCreatorId)).findFirst().orElse(null);
				if (almCreator == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almCreatorId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreator.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almCreator.getUserName()));
					return;
				}
				p.setCreatorId(uapUser.getId());
			}

			// creatorId
			final Long almModifierId = p.getModifierId();
			if (almModifierId != null) {
				final AlmUser almModifier = almUsers.stream().filter(u -> u.getId().equals(almModifierId)).findFirst().orElse(null);
				if (almModifier == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almModifierId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almModifier.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almModifier.getUserName()));
					return;
				}
				p.setModifierId(uapUser.getId());
			}

			this.projectVersionMapper.updateByPrimaryKey(p);
		});

		LOGGER.info("????????????task???.......");
		// task???
		this.taskMapper.selectAll().forEach(t -> {
			// createMemberId
			final Long almCreateMemberId = t.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almCreateMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almCreateMember.getUserName()));
					return;
				}
				t.setCreateMemberId(uapUser.getId());
			}

			// modifyMemberId
			final Long almModifyMemberId = t.getModifyMemberId();
			if (almModifyMemberId != null) {
				final AlmUser almModifyMember = almUsers.stream().filter(u -> u.getId().equals(almModifyMemberId)).findFirst().orElse(null);
				if (almModifyMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almModifyMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almModifyMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almModifyMember.getUserName()));
					return;
				}
				t.setModifyMemberId(uapUser.getId());
			}

			// owner
			final Long almOwnerId = t.getOwner();
			if (almOwnerId != null) {
				final AlmUser almOwner = almUsers.stream().filter(u -> u.getId().equals(almOwnerId)).findFirst().orElse(null);
				if (almOwner == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almOwnerId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almOwner.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almOwner.getUserName()));
					return;
				}
				t.setOwner(uapUser.getId());
			}

			this.taskMapper.updateByPrimaryKey(t);
		});

		LOGGER.info("????????????taskDetails???.......");
		// taskDetails???
		this.taskDetailsMapper.selectAll().forEach(t -> {
			// createMemberId
			final Long almCreateMemberId = t.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almCreateMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almCreateMember.getUserName()));
					return;
				}
				t.setCreateMemberId(uapUser.getId());
			}

			// modifyMemberId
			final Long almModifyMemberId = t.getModifyMemberId();
			if (almModifyMemberId != null) {
				final AlmUser almModifyMember = almUsers.stream().filter(u -> u.getId().equals(almModifyMemberId)).findFirst().orElse(null);
				if (almModifyMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almModifyMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almModifyMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almModifyMember.getUserName()));
					return;
				}
				t.setModifyMemberId(uapUser.getId());
			}

			this.taskDetailsMapper.updateByPrimaryKey(t);
		});

		LOGGER.info("????????????team???.......");
		// team???
		this.teamMapper.selectAll().forEach(t -> {
			// memberId
			final Long almMemberId = t.getMemberId();
			if (almMemberId != null) {
				final AlmUser almMember = almUsers.stream().filter(u -> u.getId().equals(almMemberId)).findFirst().orElse(null);
				if (almMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almMember.getUserName()));
					return;
				}
				t.setMemberId(uapUser.getId());
			}

			this.teamMapper.updateByPrimaryKey(t);
		});

		LOGGER.info("????????????travelCostApply???.......");
		// travelCostApply???
		this.travelCostApplyMapper.selectAll().forEach(t -> {
			// applicantId
			final Long almApplicantId = t.getApplicantId();
			if (almApplicantId != null) {
				final AlmUser almApplicant = almUsers.stream().filter(u -> u.getId().equals(almApplicantId)).findFirst().orElse(null);
				if (almApplicant == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almApplicantId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almApplicant.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almApplicant.getUserName()));
					return;
				}
				t.setApplicantId(uapUser.getId());
			}

			// departmentId
			final Long almDepId = t.getDepartmentId();
			if (almDepId != null) {
				AlmDepartmentDto almDep = almDepartments.stream().filter(d -> d.getId().equals(almDepId)).findFirst().orElse(null);
				if (almDep == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almDepId));
					return;
				}
				Department uapDep = uapDepartments.stream().filter(d -> d.getCode().equals(almDep.getCode())).findFirst().orElse(null);
				if (uapDep == null) {
					LOGGER.warn(String.format("?????????code???%s???uap??????", almDep.getCode()));
					return;
				}
				t.setDepartmentId(uapDep.getId());
			}

			// departmentId
			final Long almRootDepId = t.getRootDepartemntId();
			if (almRootDepId != null) {
				AlmDepartmentDto almDep = almDepartments.stream().filter(d -> d.getId().equals(almRootDepId)).findFirst().orElse(null);
				if (almDep == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almRootDepId));
					return;
				}
				Department uapDep = uapDepartments.stream().filter(d -> d.getCode().equals(almDep.getCode())).findFirst().orElse(null);
				if (uapDep == null) {
					LOGGER.warn(String.format("?????????code???%s???uap??????", almDep.getCode()));
					return;
				}
				t.setRootDepartemntId(uapDep.getId());
			}

			this.travelCostApplyMapper.updateByPrimaryKey(t);
		});

		LOGGER.info("????????????verifyApproval???.......");
		// verifyApproval???
		this.verifyApprovalMapper.selectAll().forEach(v -> {
			// approver
			final Long almApproverId = v.getApprover();
			if (almApproverId != null) {
				final AlmUser almApprover = almUsers.stream().filter(u -> u.getId().equals(almApproverId)).findFirst().orElse(null);
				if (almApprover == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almApproverId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almApprover.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almApprover.getUserName()));
					return;
				}
				v.setApprover(uapUser.getId());
			}

			// createMemberId
			final Long almCreateMemberId = v.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almCreateMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almCreateMember.getUserName()));
					return;
				}
				v.setCreateMemberId(uapUser.getId());
			}

			this.verifyApprovalMapper.updateByPrimaryKey(v);
		});

		LOGGER.info("????????????weekly???.......");
		// weekly???
		this.weeklyMapper.selectAll().forEach(w -> {
			// createMemberId
			final Long almCreateMemberId = w.getCreateMemberId();
			if (almCreateMemberId != null) {
				final AlmUser almCreateMember = almUsers.stream().filter(u -> u.getId().equals(almCreateMemberId)).findFirst().orElse(null);
				if (almCreateMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almCreateMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almCreateMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almCreateMember.getUserName()));
					return;
				}
				w.setCreateMemberId(uapUser.getId());
			}

			// modifyMemberId
			final Long almModifyMemberId = w.getModifyMemberId();
			if (almModifyMemberId != null) {
				final AlmUser almModifyMember = almUsers.stream().filter(u -> u.getId().equals(almModifyMemberId)).findFirst().orElse(null);
				if (almModifyMember == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almModifyMemberId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almModifyMember.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almModifyMember.getUserName()));
					return;
				}
				w.setModifyMemberId(uapUser.getId());
			}

			this.weeklyMapper.updateByPrimaryKey(w);
		});

		LOGGER.info("????????????workOrder???.......");
		// workOrder???
		this.workOrderMapper.selectAll().forEach(w -> {
			// acceptorId
			final Long almAcceptorId = w.getAcceptorId();
			if (almAcceptorId != null) {
				final AlmUser almAcceptor = almUsers.stream().filter(u -> u.getId().equals(almAcceptorId)).findFirst().orElse(null);
				if (almAcceptor == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almAcceptorId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almAcceptor.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almAcceptor.getUserName()));
					return;
				}
				w.setAcceptorId(uapUser.getId());
			}

			// applicantId
			final Long almApplicantId = w.getApplicantId();
			if (almApplicantId != null) {
				final AlmUser almApplicant = almUsers.stream().filter(u -> u.getId().equals(almApplicantId)).findFirst().orElse(null);
				if (almApplicant == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almApplicantId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almApplicant.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almApplicant.getUserName()));
					return;
				}
				w.setApplicantId(uapUser.getId());
			}

			// executorId
			final Long almExecutorId = w.getExecutorId();
			if (almExecutorId != null) {
				final AlmUser almExecutor = almUsers.stream().filter(u -> u.getId().equals(almExecutorId)).findFirst().orElse(null);
				if (almExecutor == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almExecutorId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almExecutor.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almExecutor.getUserName()));
					return;
				}
				w.setExecutorId(uapUser.getId());
			}

			// projectLeader
			if (StringUtils.isNotBlank(w.getCopyUserId())) {
				List<Long> copyUserIds = JSON.parseObject(w.getCopyUserId(), new TypeReference<List<Long>>() {
				});
				List<Long> target = new ArrayList<>(copyUserIds.size());
				copyUserIds.forEach(cp -> {
					final AlmUser almUser = almUsers.stream().filter(u -> u.getId().equals(cp)).findFirst().orElse(null);
					if (almUser == null) {
						LOGGER.warn(String.format("?????????id???%d???alm??????", cp));
						return;
					}
					User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almUser.getUserName())).findFirst().orElse(null);
					if (uapUser == null) {
						LOGGER.warn(String.format("?????????username???%s???uap??????", almUser.getUserName()));
						return;
					}
					target.add(uapUser.getId());
				});
				w.setCopyUserId(JSON.toJSONString(target));
			}
			this.workOrderMapper.updateByPrimaryKey(w);
		});

		LOGGER.info("????????????workOrderOperationRecord???.......");
		// workOrderOperationRecord???
		this.workOrderOperationRecordMapper.selectAll().forEach(w -> {
			// operatorId
			final Long almOperatorId = w.getOperatorId();
			if (almOperatorId != null) {
				final AlmUser almOperator = almUsers.stream().filter(u -> u.getId().equals(almOperatorId)).findFirst().orElse(null);
				if (almOperator == null) {
					LOGGER.warn(String.format("?????????id???%d???alm??????", almOperatorId));
					return;
				}
				User uapUser = uapUsers.stream().filter(u -> u.getUserName().equals(almOperator.getUserName())).findFirst().orElse(null);
				if (uapUser == null) {
					LOGGER.warn(String.format("?????????username???%s???uap??????", almOperator.getUserName()));
					return;
				}
				w.setOperatorId(uapUser.getId());
			}

			this.workOrderOperationRecordMapper.updateByPrimaryKey(w);
		});

		LOGGER.info("??????????????????........");
	}

}
