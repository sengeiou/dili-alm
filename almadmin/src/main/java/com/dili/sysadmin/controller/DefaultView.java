package com.dili.sysadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by asiamaster on 2017/12/13 0013.
 */
@Controller
public class DefaultView {

	@RequestMapping("/")
	public ModelAndView defaultView() {
		ModelAndView mav = new ModelAndView("redirect:http://alm.diligrp.com/main/index.html");
		return mav;
	}
}