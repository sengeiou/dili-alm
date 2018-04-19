package com.dili.alm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.cache.AlmCache;
import com.dili.ss.domain.BaseOutput;

@Controller
@RequestMapping("/cache")
public class CacheController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheController.class);

	@ResponseBody
	@RequestMapping("/clear")
	public BaseOutput<Object> clearCache() {
		try {
			AlmCache.clearCache();
			return BaseOutput.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return BaseOutput.failure();
		}
	}
}
