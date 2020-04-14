package com.dili.alm.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dili.alm.cache.AlmCache;
import com.dili.ss.domain.BaseOutput;

@RequestMapping("/cacheApi")
@RestController
public class CacheApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheApi.class);

	@RequestMapping(value = "/clear.api")
	public BaseOutput<Object> alarm() {
		try {
			AlmCache.clearCache();
			return BaseOutput.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return BaseOutput.failure(e.getMessage());
		}
	}

}
