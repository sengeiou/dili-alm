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

	// 阶段名称
	public static final Map<String, String> PHASE_NAME_MAP = new ConcurrentHashMap<>();

	public static final Map<String, String> APPLY_PLAN_PHASE_MAP = new ConcurrentHashMap<>();

	public static final Map<String, String> PROJECT_IMPORTANCE_MAP = new ConcurrentHashMap<>();
	public static final Map<String, String> KIND_RISK_MAP = new ConcurrentHashMap<>();

	public static final Map<Long, Department> DEP_MAP = new ConcurrentHashMap<>();

	// 文件类型
	public static final Map<Integer, String> FILE_TYPE_MAP = new ConcurrentHashMap<>();

}
