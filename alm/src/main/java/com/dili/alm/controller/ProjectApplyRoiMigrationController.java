package com.dili.alm.controller;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dili.alm.service.ProjectApplyService;

@RequestMapping("/projectApply")
@Controller
public class ProjectApplyRoiMigrationController {

	@Autowired
	private ProjectApplyService projectApplyService;

	@RequestMapping("/migrate")
	public String migrate() throws ParseException {
		this.projectApplyService.migrate();
		return "projectApply/migrate";
	}
}
