package com.dili.alm.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.User;

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

}
