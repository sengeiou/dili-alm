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
	public static final Map<Long, User> userMap = new ConcurrentHashMap<>();
	// 缓存项目，key为主键id
	public static final Map<Long, Project> projectMap = new ConcurrentHashMap<>();
	// 缓存项目类型
	public static final Map<String, String> projectTypeMap = new ConcurrentHashMap<>();
	// 项目状态
	public static final Map<String, String> projectStateMap = new ConcurrentHashMap<>();

    // 阶段名称
    public static final Map<String, String> phaseNameMap = new ConcurrentHashMap<>();

    public static final Map<String, String> APPLY_PLAN_PHASE_MAP = new ConcurrentHashMap<>();

    public static final Map<String, String> PROJECT_IMPORTANCE_MAP = new ConcurrentHashMap<>();
    public static final Map<String, String> KIND_RISK_MAP = new ConcurrentHashMap<>();

    public static final Map<Long, Department> DEP_MAP = new ConcurrentHashMap<>();


}
