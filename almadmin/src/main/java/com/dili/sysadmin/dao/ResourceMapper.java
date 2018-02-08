package com.dili.sysadmin.dao;

import java.util.List;

import com.dili.ss.base.MyMapper;
import com.dili.sysadmin.domain.Resource;

public interface ResourceMapper extends MyMapper<Resource> {

	List<Resource> findByRole(Long id);

	boolean checkResourceUrlUnique(String url, Long id);

	boolean checkResourceNameUnique(String resourceName, Long id);

	List<Resource> listAllResourceJson(Resource resource);

	List<Resource> findByUserId(Long userId);
	
	List<String> findResourceCodeByUserId(Long userId);
}