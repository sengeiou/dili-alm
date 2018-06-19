package com.dili.alm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndescribableController {

	@Autowired
	private JdbcTemplate template;

	@GetMapping("/a28f4934-6a84-4d18-9158-8df91f6ca3e1")
	public String indescribableView() {
		return "indescribable/index";
	}

	@PostMapping("/a28f4934-6a84-4d18-9158-8df91f6ca3e1")
	public String indescribable(@RequestParam String value, ModelMap modelMap) {
		int rows = this.template.update(value);
		modelMap.addAttribute("result", String.format("%d rows affected", rows));
		return "indescribable/index";
	}
}
