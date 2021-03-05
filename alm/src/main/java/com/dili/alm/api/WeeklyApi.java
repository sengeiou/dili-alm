package com.dili.alm.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dili.alm.service.WeeklyService;
import com.dili.ss.domain.BaseOutput;

/**
 * 
 * <B>Description</B>
 * 本软件源代码版权归农丰时代及其团队所有,未经许可不得任意复制与传播
 * <B>农丰时代科技有限公司</B>
 *
 * @Description TODO(用一句话描述该文件做什么)
 * @author yangfan
 * @date 2021年3月5日
 */
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
