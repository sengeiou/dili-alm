package com.dili.sysadmin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.dili.sysadmin.sdk.session.ManageConfig;

/**
 * Created by asiamaster on 2017/12/13 0013.
 */
@Controller
public class DefaultView {

	@Autowired
	private ManageConfig config;

	@RequestMapping("/")
	public ModelAndView defaultView() {
		ModelAndView mav = new ModelAndView("redirect:http://alm.diligrp.com/main/index.html");
		return mav;
	}

	@RequestMapping("/noAccess.do")
	public String noAccess(ModelMap modelMap) {
		modelMap.addAttribute("domain", config.getDomain());
		return "noAccess";
	}
}