package com.dili.alm.controller;

import com.dili.alm.domain.dto.UserDepartmentRole;
import com.dili.alm.domain.dto.UserDepartmentRoleQuery;
import com.dili.alm.rpc.UserRpc;
import com.dili.ss.domain.BaseOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping("/member")
@Controller
public class MemberController {

	@Autowired
	private UserRpc userRPC;

	@RequestMapping(value = "/members.html", method = RequestMethod.GET)
	public String members(ModelMap modelMap, @RequestParam("textboxId") String textboxId) {
		modelMap.put("textboxId", textboxId);
		return "member/members";
	}

	@ResponseBody
	@RequestMapping(value = "/members", method = { RequestMethod.GET, RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<UserDepartmentRole> membersJson(UserDepartmentRoleQuery user) {
		BaseOutput<List<UserDepartmentRole>> output = this.userRPC.findUserContainDepartmentAndRole(user);
		if (output.isSuccess()) {
			return output.getData();
		}
		return null;
	}
}
