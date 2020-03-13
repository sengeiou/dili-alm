package com.dili.alm.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dili.alm.domain.AlarmConfig;
import com.dili.alm.service.AlarmConfigService;
import com.dili.ss.domain.BaseOutput;

@RequestMapping("/alarmConfigApi")
@RestController
public class AlarmConfigApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlarmConfigApi.class);

	@Autowired
	private AlarmConfigService alarmConfigService;

	@RequestMapping(value = "/alarm.api")
	public BaseOutput<Object> alarm() {
		try {
			this.alarmConfigService.alarm();
			return BaseOutput.success();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return BaseOutput.failure();
		}
	}

}
