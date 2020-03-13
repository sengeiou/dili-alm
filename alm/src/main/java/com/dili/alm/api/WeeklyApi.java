package com.dili.alm.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dili.alm.service.WeeklyService;
import com.dili.ss.domain.BaseOutput;

@RequestMapping("/weeklyApi")
@RestController
public class WeeklyApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(WeeklyApi.class);

	@Autowired
	private WeeklyService weeklyService;

	@RequestMapping(value = "/submitWeekly.api")
	public BaseOutput<Object> submitWeekly() {
		try {
			this.weeklyService.submitWeekly();
			return BaseOutput.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return BaseOutput.failure();
		}
	}

}
