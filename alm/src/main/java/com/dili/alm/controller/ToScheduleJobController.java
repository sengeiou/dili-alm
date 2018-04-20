package com.dili.alm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/schedule")
public class ToScheduleJobController {

	@RequestMapping(value = "/toSchedule.html", method = RequestMethod.GET)
	public String toScehduleJob() {
		return "schedule";
	}
}
