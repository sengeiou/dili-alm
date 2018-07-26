package com.dili.sysadmin.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.User;
import com.dili.sysadmin.domain.dto.UserLoginDto;
import com.dili.sysadmin.domain.dto.UserLoginResultDto;
import com.dili.sysadmin.exception.UserException;
import com.dili.sysadmin.sdk.session.ManageConfig;
import com.dili.sysadmin.sdk.util.WebContent;
import com.dili.sysadmin.service.UserService;
import com.dili.sysadmin.utils.WebUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * <B>Description</B>地利后台管理系统登录 <B>Copyright</B> Copyright (c) 2014 www.dili7
 * All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 * 
 * @createTime 2014-7-4 上午11:09:00
 * @author Nick
 */
@Api("/login")
@Controller
@RequestMapping("/login")
public class LoginController {

	private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

	@Resource
	private UserService userService;

	public static final String INDEX_PATH = "login/index";
	public static final String REDIRECT_INDEX_PAGE = "redirect:/login/index.html";

	public static final String COOKIE_SESSION_ID = "SessionId";

	private Logger log = LoggerFactory.getLogger(LoginController.class);

	@ApiOperation("跳转到Login页面")
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return INDEX_PATH;
	}

	@ApiOperation("执行login请求，跳转到Main页面或者返回login页面")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "action", paramType = "form", value = "用户信息", required = false, dataType = "string") })
	@RequestMapping(value = "/loginAction", method = { RequestMethod.GET, RequestMethod.POST })
	public String loginAction(HttpServletRequest request, HttpServletResponse response, UserLoginDto dto,
			ModelMap modelMap) {
		// String requestPath = WebUtil.fetchReferer(request);
		try {
			dto.setRemoteIP(WebUtil.getRemoteIP(request));
			UserLoginResultDto resultDto = userService.doLogin(dto);
			if (null != resultDto && ResultCode.OK == resultDto.getStatus()) {
				makeCookieTag(resultDto.getUserInfo(), resultDto.getSessionId());
				return MainController.REDIRECT_INDEX_PAGE;
			}

			modelMap.put("msg", null == resultDto ? "登录失败,未知异常,userService doLogin() 返回 null" : resultDto.getMessage());

		} catch (UserException e) {
			modelMap.put("msg", e.getMessage());
		}

		return INDEX_PATH;
	}

	@ApiOperation("执行logout请求，跳转login页面或者弹出错误")
	@RequestMapping(value = "/logoutAction", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public @ResponseBody BaseOutput logoutAction(ModelMap modelMap) {
		String sessionId = WebContent.getPC().getSessionId();
		this.userService.logout(sessionId);
		try {
			WebContent.setCookie(COOKIE_SESSION_ID, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BaseOutput.success();
	}

	@RequestMapping(value = "/login.do")
	public String login(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		log.debug("--- Login Page ---");
		UUID uuid = UUID.randomUUID();
		Map<String, Object> map = new HashMap<>();
		map.put("UUID", uuid);
		model.put("model", map);
		return "login/index";
	}

	/**
	 * 跨域上传支持检测
	 * 
	 * @param req
	 * @param resp
	 */
	@RequestMapping(value = "/loginPanel.do", method = { RequestMethod.OPTIONS })
	public void upload(HttpServletRequest req, HttpServletResponse resp) {
		String domain = req.getHeader("Origin");
		resp.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Content-Range, Content-Disposition, Content-Description, Authorization, Accept,X-Requested-With");
		resp.setHeader("Access-Control-Request-Method", "POST,OPTIONS");
		resp.setHeader("Access-Control-Allow-Origin", domain);
		resp.setHeader("Access-Control-Allow-Credentials", "true");
	}

	@RequestMapping(value = "/loginPanel.do")
	@ResponseBody
	public String loginPanel(@RequestParam(required = false) String returnUrl,
			@RequestParam(value = "callback", required = false) String callback, HttpServletRequest request,
			HttpServletResponse response) {
		log.debug("--- Login Page ---");
		// UUID uuid = UUID.randomUUID();
		//
		// Map<String, Object> map = this.groupTemplate.getSharedVars();
		// map.put("UUID", uuid);
		// map.put("returnUrl", returnUrl);
		//
		// for (Map.Entry<String, Object> ent :
		// viewResolver.getAttributesMap().entrySet()) {
		// map.put(ent.getKey(), ent.getValue());
		// }
		//
		// Template t = this.groupTemplate.getTemplate("login/loginPanel.vm");
		//
		// String tmp = t.render();
		//
		// response.setHeader("Access-Control-Allow-Origin",
		// request.getHeader("Origin"));
		// response.setHeader("Access-Control-Allow-Credentials", "true");
		//
		// if (StringUtils.isEmpty(callback)) {
		// return tmp;
		// } else {
		// tmp = tmp.replace('\r', ' ');
		// tmp = tmp.replace('\n', ' ');
		// return callback + "(\"" + tmp + "\")";
		// }
		return null;
	}

	@RequestMapping(value = "/doLogin.html")
	public ModelAndView dologin(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "authcode", required = false) String authcode,
			@RequestParam(value = "uuid", required = false) String uuid,
			@RequestParam(required = false) String returnUrl, @Valid UserLoginDto command, BindingResult br) {
		log.debug("--- doLogin Process ---");
		String loginViewName = WebUtil.fetchReferer(request);

		if (br.hasErrors()) {
			ModelAndView mav = new ModelAndView();
			mav.addObject("msg", br.getFieldError().getDefaultMessage());
			mav.addObject("success", false);
			mav.setViewName(loginViewName);
			return mav;

		}

		// if (verifyImg) {
		// if (!securityUtils.verifyImgCode(uuid, authcode)) {
		// loginVM.addObject("success", false);
		// loginVM.addObject("msg", "验证码不正确!");
		// return loginVM;
		// }
		// }

		ModelAndView mav = new ModelAndView();
		UserLoginResultDto result = null;
		try {
			command.setRemoteIP(WebUtil.getRemoteIP(request));
			result = userService.doLogin(command);
			if (result == null) {
				mav.addObject("msg", "登录失败,未知异常,userService doLogin() 返回 null");
				mav.addObject("success", false);
				mav.setViewName(loginViewName);
				return mav;
			}

			if (ResultCode.OK != result.getStatus()) {
				mav.addObject("msg", result.getMessage());
				mav.addObject("success", false);
				mav.setViewName("login/index");
				return mav;
			}

			String sessionId = result.getSessionId();
			makeCookieTag(result.getUserInfo(), sessionId);

			if (StringUtils.isBlank(returnUrl)) {
				mav.setViewName(MainController.REDIRECT_INDEX_PAGE);
			} else {
				String sub = "";
				if (returnUrl.lastIndexOf("returnUrl") >= 0) {
					sub = returnUrl.substring(returnUrl.lastIndexOf("returnUrl"));
				} else {
					sub = returnUrl;
				}
				if (sub.indexOf("?") > 0) {
					returnUrl += "&";
				} else {
					returnUrl += "?";
				}
				returnUrl += COOKIE_SESSION_ID + "=" + sessionId;
				mav.setViewName("redirect:" + returnUrl);
			}
			// validatePwdService.validatePwd(command.getPassword());
		} catch (IllegalArgumentException e) {
			mav.addObject("msg", e.getMessage());
			mav.addObject("success", false);
			mav.setViewName("redirect:/user/preUpdatePwd.do" + "?returnUrl=" + "/login/index.html");
		} catch (UserException e) {
			mav.addObject("msg", e.getMessage());
			mav.addObject("success", false);
			mav.setViewName("redirect:/user/preUpdatePwd.do" + "?returnUrl=" + "/login/index.html");
		}
		log.debug("--- Login Process Finish ---");
		return mav;
	}

	/**
	 * 接口方式登录
	 *
	 * @param request
	 * @param tag
	 * @return
	 */
	@RequestMapping(value = "/doLoginAPP.do")
	@ResponseBody
	public String dologinApp(@RequestParam(value = "tag", required = false) String tag,
			@RequestParam(value = "callback", required = false) String callback, @Valid UserLoginDto command,
			BindingResult br, HttpServletRequest request) {
		log.debug("--- doLogin Process ---");
		String val = null;
		if (br.hasErrors()) {
			val = JSON.toJSONString(BaseOutput.failure(br.getFieldError().getDefaultMessage()));
		} else {
			try {
				command.setRemoteIP(WebUtil.getRemoteIP(request));
				UserLoginResultDto result = userService.doLogin(command);
				if (result != null) {
					val = JSON.toJSONString(BaseOutput.success("登陆成功"));
				}
				User userInfo = result.getUserInfo();
				String sessionId = result.getSessionId();
				log.debug("--- Save Session Data To Redis ---");
				Map<String, Object> map = new HashMap<>();
				map.put("sessionId", sessionId);
				map.put("userId", userInfo.getId());
				val = JSON.toJSONString(BaseOutput.success("登陆成功！").setData(map));
			} catch (UserException e) {
				LOG.error(e.getMessage());
				val = JSON.toJSONString(BaseOutput.failure(e.getMessage()));
			}
		}
		if (StringUtils.isNotBlank(callback)) {
			return callback + "(" + val + ")";
		}
		return val;
	}

	private void makeCookieTag(User user, String sessionId) {
		String referer = WebUtil.fetchReferer(WebContent.getRequest());
		WebContent.setCookie(COOKIE_SESSION_ID, sessionId);
		WebContent.setCookie("u", user.getId().toString());
		WebContent.setCookie("n", user.getUserName());
		WebContent.setCookie("loginPath", referer);
	}

	@ResponseBody
	@RequestMapping(value = "/logout.json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseOutput<Object> dologout(
			@RequestParam(required = false, defaultValue = "/login/index.html") String returnUrl) {
		log.debug("--- doLogout Process ---");
		Map<String, Object> map = new HashMap<>();
		String loginPath = WebContent.getCookie("loginPath");
		if (StringUtils.isNotBlank(loginPath)) {
			try {
				returnUrl = URLDecoder.decode(loginPath, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.warn("登录地址转换出错!");
			}
		}
		String sessionId = WebContent.getPC().getSessionId();
		this.userService.logout(sessionId);
		try {
			WebContent.setCookie(COOKIE_SESSION_ID, null);
		} catch (Exception e) {
			log.error("登出设置cookie域名失败" + e);
		}
		map.put("returnUrl", returnUrl);
		return BaseOutput.success().setData(map);
	}

}
