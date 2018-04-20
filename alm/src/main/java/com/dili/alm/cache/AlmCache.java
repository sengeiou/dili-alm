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
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.HardwareResourceService;
import com.dili.alm.service.LogService;
import com.dili.alm.service.MessageService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.TaskService;
import com.dili.alm.service.TeamService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
public class AlmCache {

	// 缓存用户，key为主键id
	private static final Map<Long, User> USER_MAP = new ConcurrentHashMap<>();
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
	// 消息类型
	private static final Map<String, String> MESSAGE_TYPE_MAP = new ConcurrentHashMap<>();
	// 阶段名称
	private static final Map<String, String> PHASE_NAME_MAP = new ConcurrentHashMap<>();

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

	private static final AlmCache INSTANCE = new AlmCache();
	@Autowired
	private ProjectPhaseService phaseService;
	@Autowired
	private UserRpc userRpc;
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
	private MessageService messageService;

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
				Map<Object, Object> map = (Map<Object, Object>) obj;
				map.clear();
			}
			if (obj instanceof Collection) {
				Collection<Object> coll = (Collection<Object>) obj;
				coll.clear();
			}
		}
	}

	public Map<Long, User> getUserMap() {
		// 应用启动时初始化userMap
		if (AlmCache.USER_MAP.isEmpty()) {
			BaseOutput<List<User>> output = userRpc.list(new User());
			if (output.isSuccess()) {
				output.getData().forEach(user -> {
					AlmCache.USER_MAP.put(user.getId(), user);
				});
			}
		}
		return USER_MAP;
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

	public Map<String, String> getMessageTypeMap() {
		if (AlmCache.MESSAGE_TYPE_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = messageService.getMessageTypes();
			list.forEach(type -> {
				AlmCache.MESSAGE_TYPE_MAP.put(type.getValue(), type.getCode());
			});
		}
		return MESSAGE_TYPE_MAP;
	}

	public Map<String, String> getPhaseNameMap() {
		if (AlmCache.PHASE_NAME_MAP.isEmpty()) {
			List<DataDictionaryValueDto> list = phaseService.getPhaseNames();
			list.forEach(type -> {
				AlmCache.PHASE_NAME_MAP.put(type.getValue(), type.getCode());
			});
		}
		return PHASE_NAME_MAP;
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
			BaseOutput<List<Department>> output = this.departmentRpc.list(new Department());
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
			dto.getValues()
					.forEach(v -> AlmCache.RESOURCE_ENVIRONMENT_MAP.put(Integer.valueOf(v.getValue()), v.getCode()));
		}
		return RESOURCE_ENVIRONMENT_MAP;
	}

}
