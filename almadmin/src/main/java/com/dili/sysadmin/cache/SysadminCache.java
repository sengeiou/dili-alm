package com.dili.sysadmin.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SysadminCache {

	public static final Map<String, String> RANK_MAP = new ConcurrentHashMap<>();
}
