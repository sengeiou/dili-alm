package com.dili.sysadmin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dili.sysadmin.domain.Menu;
import com.dili.sysadmin.exception.MenuException;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.dili.sysadmin.service.MenuService;
import com.dili.sysadmin.utils.MenuTreeUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("/main")
@Controller
@RequestMapping("/main")
public class MainController {

	public static final String INDEX_PATH = "main/index";
	public static final String REDIRECT_INDEX_PAGE = "redirect:/main/index.html";

	public static final String USERDETAIL_PATH = "main/userDetail";
	public static final String CHANGEPWD_PATH = "main/changePwd";

	@Autowired
	private MenuService menuService;
	
	@ApiOperation("跳转到Main页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) throws MenuException {
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket != null) {
			List<Menu> findUserMenu = this.menuService.findUserMenu(userTicket.getId().toString());
			List<Object> setMenuTree = new MenuTreeUtil().setMenuTree(findUserMenu);
			modelMap.put("userid", userTicket.getId());
			modelMap.put("username", userTicket.getRealName());
			modelMap.put("menu",setMenuTree );
			return INDEX_PATH;
		} else {
			return LoginController.REDIRECT_INDEX_PAGE;
		}
	}

	@ApiOperation("跳转到信息页面")
	@RequestMapping(value = "/userDetail.html", method = RequestMethod.GET)
	public String userDetail(ModelMap modelMap) {
		return USERDETAIL_PATH;
	}

	@ApiOperation("跳转到修改密码页面")
	@RequestMapping(value = "/changePwd.html", method = RequestMethod.GET)
	public String changePwd(ModelMap modelMap) {
		return CHANGEPWD_PATH;
	}

}
