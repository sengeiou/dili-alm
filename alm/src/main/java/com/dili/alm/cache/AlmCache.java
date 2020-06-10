package com.dili.alm.cache;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.AreaMapper;
import com.dili.alm.dao.ProjectApplyMapper;
import com.dili.alm.dao.ProjectCostMapper;
import com.dili.alm.dao.ProjectEarningMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.RoiMapper;
import com.dili.alm.domain.Area;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectCost;
import com.dili.alm.domain.ProjectEarning;
import com.dili.alm.domain.Roi;
import com.dili.alm.domain.SystemDto;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.RoiDto;
import com.dili.alm.rpc.SysProjectRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.HardwareResourceService;
import com.dili.alm.service.LogService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.TaskService;
import com.dili.alm.service.TeamService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.Firm;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.dto.FirmDto;
import com.dili.uap.sdk.rpc.DepartmentRpc;
import com.dili.uap.sdk.rpc.FirmRpc;
import com.dili.uap.sdk.rpc.UserRpc;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
public class AlmCache {

	// 缓存用户，key为主键id
	private static final Map<Long, User> USER_MAP = new ConcurrentHashMap<>();
	// 缓存所有用户，key为主键id
	private static final Map<Long, User> USER_ALL_MAP = new ConcurrentHashMap<>();
	// 缓存项目，key为主键id
	private static final Map<Long, Project> PROJECT_MAP = new ConcurrentHashMap<>();
	// 缓存项目类型
	private static final Map<String, String> PROJECT_TYPE_MAP = new ConcurrentHashMap<>();
	// 项目状态
	private static final Map<String, String> PROJECT_STATE_MAP = new ConcurrentHashMap<>();

	// 任务状态
	private static final Map<String, String> TASK_STATE_MAP = new ConcurrentHashMap<>();
	// 使用环境
	private static final Map<String, String> ENVIRONMENT_MAP = new ConcurrentHashMap<>();
	// 地域
	private static final Map<String, String> REGIONAL_MAP = new ConcurrentHashMap<>();

	// 任务类型
	private static final Map<String, String> TASK_TYPE_MAP = new ConcurrentHashMap<>();

	private static final Map<String, String> APPLY_PLAN_PHASE_MAP = new ConcurrentHashMap<>();

	private static final Map<String, String> PROJECT_IMPORTANCE_MAP = new ConcurrentHashMap<>();
	private static final Map<String, String> KIND_RISK_MAP = new ConcurrentHashMap<>();
	private static final Map<String, String> CHANGE_TYPE = new ConcurrentHashMap<>();

	private static final Map<Long, Department> DEP_MAP = new ConcurrentHashMap<>();
	// 缓存日志操作模块
	private static final Map<String, String> LOG_MODULE_MAP = new ConcurrentHashMap<>();
	// 文件类型
	private static final Map<Integer, String> FILE_TYPE_MAP = new ConcurrentHashMap<>();
	// 团队角色
	private static final Map<String, String> TEAM_ROLE_MAP = new ConcurrentHashMap<>();

	// 资源环境
	private static final Map<Integer, String> RESOURCE_ENVIRONMENT_MAP = new ConcurrentHashMap<>();

	private static final Map<String, String> TRAVEL_COST_ITEM_MAP = new ConcurrentHashMap<>();

	// 城市缓存
	private static final Map<Long, Area> AREA_MAP = new ConcurrentHashMap<>();

	// 工单类型缓存
	private static final Map<String, String> WORK_ORDER_TYPE_MAP = new ConcurrentHashMap<>();

	// 工单优先级缓存
	private static final Map<String, String> WORK_ORDER_PRIORITY_MAP = new ConcurrentHashMap<>();

	private static final AlmCache INSTANCE = new AlmCache();
	// 市场缓存
	private static final Map<String, String> MARKET_MAP = new ConcurrentHashMap<>();
	// 立项申请roi缓存
	private static final Map<Long, RoiDto> ROI_MAP = new ConcurrentHashMap<>();

	// 需求状态缓存
	private static final Map<String, String> DAMAND_STATUS_MAP = new ConcurrentHashMap<>();
	// 需求类型缓存
	private static final Map<String, String> DAMAND_TYPE_MAP = new ConcurrentHashMap<>();
	// 系统缓存
	private static final Map<Long, SystemDto> PROJECT_SYS_MAP = new ConcurrentHashMap<>();
	// 需求流程状态
	private static final Map<String, String> DAMAND_PROCESS_STATUS_MAP = new ConcurrentHashMap<>();
	
	
	// 市场信息firm
    private static final Map<String, Firm> FIRM_MAP = new ConcurrentHashMap<>();
	
    @Autowired
	private UserRpc userRpc;
	@Autowired
	private SysProjectRpc sysProjectRpc;
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private DataDictionaryService ddService;
	@Autowired
	private ProjectApplyService projectApplyService;
	@Autowired
	private DepartmentRpc departmentRpc;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private LogService logService;
	@Autowired
	private HardwareResourceService hardwareResourceService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private AreaMapper areaMapper;
	@Autowired
	private ProjectApplyMapper projectApplyMapper;
	@Autowired
	private ProjectEarningMapper projectEarningMapper;
	@Autowired
	private ProjectCostMapper projectCostMapper;
	@Autowired
	private RoiMapper roiMapper;
	@Autowired
	private FirmRpc firmRpc;

	public static AlmCache getInstance() {
		return INSTANCE;
	}

	private AlmCache() {
	}

	public static void clearCache() throws IllegalArgumentException, IllegalAccessException {
		for (Field field : AlmCache.class.getDeclaredFields()) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			if (!Modifier.isFinal(field.getModifiers())) {
				continue;
			}
			if (!Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			Object obj = field.get(null);
			if (obj instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<Object, Object> map = (Map<Object, Object>) obj;
				map.clear();
			}
			if (obj instanceof Collection) {
				@SuppressWarnings("unchecked")
				Collection<Object> coll = (Collection<Object>) obj;
				coll.clear();
			}
		}
	}

	public Map<Long, User> getUserMap() {
		// 应用启动时初始化userMap
		if (AlmCache.USER_MAP.isEmpty()) {
			User newDTO = DTOUtils.newDTO(User.class);
			newDTO.setFirmCode(AlmConstants.ALM_FIRM_CODE);
			BaseOutput<List<User>> output = userRpc.listByExample(newDTO);
			if (output.isSuccess()) {
				output.getData().forEach(user -> {
					AlmCache.USER_MAP.put(user.getId(), user);
				});
			}
		}
		return USER_MAP;
	}

	public Map<Long, User> getAllUserMap() {
		// 应用启动时初始化userMap
		if (AlmCache.USER_ALL_MAP.isEmpty()) {
			User newDTO = DTOUtils.newDTO(User.class);
			BaseOutput<List<User>> output = userRpc.listByExample(newDTO);
			if (output.isSuccess()) {
				output.getData().forEach(user -> {
					AlmCache.USER_ALL_MAP.put(user.getId(), user);
				});
			}
		}
		return USER_ALL_MAP;
	}
	public Map<String, String> getTravelCostItemMap() {
		if (TRAVEL_COST_ITEM_MAP.isEmpty()) {
			DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.TRAVEL_COST_DETAIL_CONFIG_CODE);
			dd.getValues().forEach(v -> {
				TRAVEL_COST_ITEM_MAP.put(v.getValue(), v.getCode());
			});
		}
		return TRAVEL_COST_ITEM_MAP;
	}

	public Map<Long, Project> getProjectMap() {
		if (AlmCache.PROJECT_MAP.isEmpty()) {
			List<Project> list = this.projectMapper.select(DTOUtils.newDTO(Project.class));
			list.forEach(project -> {
				AlmCache.PROJECT_MAP.put(project.getId(), project);
			});
		}
		return PROJECT_MAP;
	}

	public Map<String, String> getProjectTypeMap() {
		if (AlmCache.PROJECT_TYPE_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = this.ddService.findByCode(AlmConstants.PROJECT_TYPE_CODE).getValues();
			list.forEach(type -> {
				AlmCache.PROJECT_TYPE_MAP.put(type.getValue(), type.getCode());
			});
		}
		return PROJECT_TYPE_MAP;
	}

	public Map<String, String> getProjectStateMap() {
		if (AlmCache.PROJECT_STATE_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = projectService.getProjectStates();
			list.forEach(type -> {
				AlmCache.PROJECT_STATE_MAP.put(type.getValue(), type.getCode());
			});
		}
		return PROJECT_STATE_MAP;
	}

	public Map<String, String> getTaskStateMap() {
		if (AlmCache.TASK_STATE_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = taskService.getTaskStates();
			list.forEach(type -> {
				AlmCache.TASK_STATE_MAP.put(type.getValue(), type.getCode());
			});
		}
		return TASK_STATE_MAP;
	}

	public Map<String, String> getEnvironmentMap() {
		if (AlmCache.ENVIRONMENT_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = hardwareResourceService.getEnvironments();
			list.forEach(type -> {
				AlmCache.ENVIRONMENT_MAP.put(type.getValue(), type.getCode());
			});
		}
		return ENVIRONMENT_MAP;
	}

	public Map<String, String> getRegionalMap() {
		if (AlmCache.REGIONAL_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = hardwareResourceService.getRegionals();
			list.forEach(type -> {
				AlmCache.REGIONAL_MAP.put(type.getValue(), type.getCode());
			});
		}
		return REGIONAL_MAP;
	}

	public Map<String, String> getTaskTypeMap() {
		if (AlmCache.TASK_TYPE_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = taskService.getTaskTypes();
			list.forEach(type -> {
				AlmCache.TASK_TYPE_MAP.put(type.getValue(), type.getCode());
			});
		}
		return TASK_TYPE_MAP;
	}

	public Map<String, String> getApplyPlanPhaseMap() {
		if (AlmCache.APPLY_PLAN_PHASE_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = projectApplyService.getPlanPhase();
			list.forEach(type -> {
				AlmCache.APPLY_PLAN_PHASE_MAP.put(type.getValue(), type.getCode());
			});
		}
		return APPLY_PLAN_PHASE_MAP;
	}

	public Map<String, String> getProjectImportanceMap() {
		if (AlmCache.PROJECT_IMPORTANCE_MAP.isEmpty()) {
			DataDictionaryDto code = ddService.findByCode("project_ importance");
			List<DataDictionaryValueDto> list = code != null ? code.getValues() : null;
			list.forEach(type -> {
				AlmCache.PROJECT_IMPORTANCE_MAP.put(type.getValue(), type.getCode());
			});
		}
		return PROJECT_IMPORTANCE_MAP;
	}

	public Map<String, String> getKindRiskMap() {
		if (AlmCache.KIND_RISK_MAP.isEmpty()) {
			DataDictionaryDto code = ddService.findByCode("kind_risk");
			List<DataDictionaryValueDto> list = code != null ? code.getValues() : null;
			list.forEach(type -> {
				AlmCache.KIND_RISK_MAP.put(type.getValue(), type.getCode());
			});
		}
		return KIND_RISK_MAP;
	}

	public Map<String, String> getChangeType() {
		if (AlmCache.CHANGE_TYPE.isEmpty()) {
			DataDictionaryDto code = ddService.findByCode("change_type");
			List<DataDictionaryValueDto> list = code != null ? code.getValues() : null;
			list.forEach(type -> AlmCache.CHANGE_TYPE.put(type.getValue(), type.getCode()));
		}
		return CHANGE_TYPE;
	}

	public Map<Long, Department> getDepMap() {
		if (AlmCache.DEP_MAP.isEmpty()) {
			BaseOutput<List<Department>> output = this.departmentRpc.listByDepartment(DTOUtils.newInstance(Department.class));
			if (output.isSuccess()) {
				output.getData().forEach(dep -> {
					AlmCache.DEP_MAP.put(dep.getId(), dep);
				});
			}
		}
		return DEP_MAP;
	}

	public Map<String, String> getLogModuleMap() {
		if (AlmCache.LOG_MODULE_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = logService.getLogModules();
			list.forEach(type -> {
				AlmCache.LOG_MODULE_MAP.put(type.getValue(), type.getCode());
			});
		}
		return LOG_MODULE_MAP;
	}

	public Map<Integer, String> getFileTypeMap() {
		if (AlmCache.FILE_TYPE_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = projectService.getFileTypes();
			list.forEach(type -> {
				AlmCache.FILE_TYPE_MAP.put(Integer.valueOf(type.getValue()), type.getCode());
			});
		}
		return FILE_TYPE_MAP;
	}

	public Map<String, String> getTeamRoleMap() {
		if (AlmCache.TEAM_ROLE_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = teamService.getTeamRoles();
			list.forEach(type -> {
				AlmCache.TEAM_ROLE_MAP.put(type.getValue(), type.getCode());
			});
		}
		return TEAM_ROLE_MAP;
	}

	public Map<Integer, String> getResourceEnvironmentMap() {
		if (AlmCache.RESOURCE_ENVIRONMENT_MAP.isEmpty()) {
			DataDictionaryDto dto = this.ddService.findByCode(AlmConstants.RESOURCE_ENVIRONMENT_CODE);
			if (dto == null) {
				return RESOURCE_ENVIRONMENT_MAP;
			}
			if (CollectionUtils.isEmpty(dto.getValues())) {
				return RESOURCE_ENVIRONMENT_MAP;
			}
			dto.getValues().forEach(v -> AlmCache.RESOURCE_ENVIRONMENT_MAP.put(Integer.valueOf(v.getValue()), v.getCode()));
		}
		return RESOURCE_ENVIRONMENT_MAP;
	}

	public Map<Long, Area> getAreaMap() {
		if (AlmCache.AREA_MAP.isEmpty()) {
			List<Area> list = this.areaMapper.select(DTOUtils.newDTO(Area.class));
			if (CollectionUtils.isEmpty(list)) {
				return AREA_MAP;
			}
			list.forEach(v -> AlmCache.AREA_MAP.put(v.getId(), v));
		}
		return AREA_MAP;
	}

	public Map<String, String> getWorkOrderTypeMap() {
		if (AlmCache.WORK_ORDER_TYPE_MAP.isEmpty()) {
			DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.WORK_ORDER_TYPE_CODE);
			if (CollectionUtils.isEmpty(dd.getValues())) {
				return WORK_ORDER_TYPE_MAP;
			}
			dd.getValues().forEach(v -> WORK_ORDER_TYPE_MAP.put(v.getValue(), v.getCode()));
		}
		return WORK_ORDER_TYPE_MAP;
	}

	public Map<String, String> getWorkOrderPriorityMap() {
		if (AlmCache.WORK_ORDER_PRIORITY_MAP.isEmpty()) {
			DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.WORK_ORDER_PRIORITY_CODE);
			if (CollectionUtils.isEmpty(dd.getValues())) {
				return WORK_ORDER_PRIORITY_MAP;
			}
			dd.getValues().forEach(v -> WORK_ORDER_PRIORITY_MAP.put(v.getValue(), v.getCode()));
		}
		return WORK_ORDER_PRIORITY_MAP;
	}

	public Map<String, String> getMarketMap() {
		if (AlmCache.MARKET_MAP.isEmpty()) {
			DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.MARKET_CODE);
			if (CollectionUtils.isEmpty(dd.getValues())) {
				return MARKET_MAP;
			}
			dd.getValues().forEach(v -> MARKET_MAP.put(v.getValue(), v.getCode()));
		}
		return MARKET_MAP;
	}

	public Map<Long, RoiDto> getProjectApplyRois() {
		if (AlmCache.ROI_MAP.isEmpty()) {
			List<ProjectApply> applyList = this.projectApplyMapper.select(null);
			if (CollectionUtils.isEmpty(applyList)) {
				return null;
			}
			applyList.forEach(a -> {
				Roi roiQuery = DTOUtils.newDTO(Roi.class);
				roiQuery.setApplyId(a.getId());
				Roi roi = this.roiMapper.selectOne(roiQuery);
				if (roi == null) {
					return;
				}
				RoiDto roiDto = DTOUtils.toEntity(roi, RoiDto.class, false);
				ProjectEarning peQuery = DTOUtils.newDTO(ProjectEarning.class);
				peQuery.setApplyId(a.getId());
				List<ProjectEarning> earnings = this.projectEarningMapper.select(peQuery);
				roiDto.setEarnings(earnings);
				ProjectCost pcQuery = DTOUtils.newDTO(ProjectCost.class);
				pcQuery.setApplyId(a.getId());
				List<ProjectCost> costs = this.projectCostMapper.select(pcQuery);
				roiDto.setCosts(costs);
				AlmCache.ROI_MAP.put(a.getId(), roiDto);
			});
		}
		return ROI_MAP;
	}

	public Map<String, String> getDemandStetusMap() {
		if (AlmCache.DAMAND_STATUS_MAP.isEmpty()) {
			DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.DEMAND_STATUS);
			if (CollectionUtils.isEmpty(dd.getValues())) {
				return DAMAND_STATUS_MAP;
			}
			dd.getValues().forEach(v -> DAMAND_STATUS_MAP.put(v.getValue(), v.getCode()));
		}
		return DAMAND_STATUS_MAP;
	}

	public Map<String, String> getDemandTypeMap() {
		if (AlmCache.DAMAND_TYPE_MAP.isEmpty()) {
			DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.DEMAND_TYPE);
			if (CollectionUtils.isEmpty(dd.getValues())) {
				return DAMAND_TYPE_MAP;
			}
			dd.getValues().forEach(v -> DAMAND_TYPE_MAP.put(v.getValue(), v.getCode()));
		}
		return DAMAND_TYPE_MAP;
	}

	public Map<Long, SystemDto> getProjectSysMap() {
		// 应用启动时初始化userMap
		if (AlmCache.PROJECT_SYS_MAP.isEmpty()) {
			BaseOutput<List<SystemDto>> output = sysProjectRpc.list(DTOUtils.newDTO(SystemDto.class));
			if (output.isSuccess()) {
				output.getData().forEach(entity -> {
					AlmCache.PROJECT_SYS_MAP.put(entity.getId(), entity);
				});
			}
		}
		return PROJECT_SYS_MAP;
	}

	/**
	 * 流程类型缓存
	 * 
	 * @return
	 */
	public Map<String, String> getDemandProcessStetusMap() {
		if (AlmCache.DAMAND_PROCESS_STATUS_MAP.isEmpty()) {
			DataDictionaryDto dd = this.ddService.findByCode(AlmConstants.DEMAND_PROCESS_TYPE);
			if (CollectionUtils.isEmpty(dd.getValues())) {
				return DAMAND_PROCESS_STATUS_MAP;
			}
			dd.getValues().forEach(v -> DAMAND_PROCESS_STATUS_MAP.put(v.getValue(), v.getCode()));
		}
		return DAMAND_PROCESS_STATUS_MAP;
	}
	
	/**
	 * 市场缓存
	 * 
	 * @return
	 */
	public Map<String, Firm> getFirmMap() {
		if (AlmCache.FIRM_MAP.isEmpty()) {
			BaseOutput<List<Firm>> output = this.firmRpc.listByExample(DTOUtils.newInstance(FirmDto.class));
			if (output.isSuccess()) {
				output.getData().forEach(f -> {
					AlmCache.FIRM_MAP.put(f.getCode(), f);
				});
			}
		}
		return FIRM_MAP;
	}
}
