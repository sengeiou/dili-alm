package com.dili.alm.cache;

import com.dili.alm.domain.Department;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by asiamaster on 2017/10/19 0019.
 */
public class AlmCache {
	// 缓存用户，key为主键id
	public static final Map<Long, User> USER_MAP = new ConcurrentHashMap<>();
	// 缓存项目，key为主键id
	public static final Map<Long, Project> PROJECT_MAP = new ConcurrentHashMap<>();
	// 缓存项目类型
	public static final Map<String, String> PROJECT_TYPE_MAP = new ConcurrentHashMap<>();
	// 项目状态
	public static final Map<String, String> PROJECT_STATE_MAP = new ConcurrentHashMap<>();

	// 任务状态
	public static final Map<String, String> TASK_STATE_MAP = new ConcurrentHashMap<>();
	// 任务类型
	public static final Map<String, String> TASK_TYPE_MAP = new ConcurrentHashMap<>();
	// 消息类型
	public static final Map<String, String> MESSAGE_TYPE_MAP = new ConcurrentHashMap<>();
	// 阶段名称
	public static final Map<String, String> PHASE_NAME_MAP = new ConcurrentHashMap<>();

	public static final Map<String, String> APPLY_PLAN_PHASE_MAP = new ConcurrentHashMap<>();

	public static final Map<String, String> PROJECT_IMPORTANCE_MAP = new ConcurrentHashMap<>();
	public static final Map<String, String> KIND_RISK_MAP = new ConcurrentHashMap<>();
	public static final Map<String, String> CHANGE_TYPE = new ConcurrentHashMap<>();

	public static final Map<Long, Department> DEP_MAP = new ConcurrentHashMap<>();
	// 缓存日志操作模块
	public static final Map<String, String> LOG_MODULE_MAP = new ConcurrentHashMap<>();
	// 文件类型
	public static final Map<Integer, String> FILE_TYPE_MAP = new ConcurrentHashMap<>();
	// 团队角色
	public static final Map<String, String> TEAM_ROLE_MAP = new ConcurrentHashMap<>();

	// 告警类型
	public static final Map<String, String> ALARM_TYPE_MAP = new ConcurrentHashMap<>();

	// 资源环境
	public static final Map<Integer, String> RESOURCE_ENVIRONMENT_MAP = new ConcurrentHashMap<>();

}
