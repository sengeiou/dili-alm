package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.UserDepartmentRole;
import com.dili.alm.domain.dto.UserDepartmentRoleQuery;
import com.dili.alm.rpc.UserRpc;
import com.dili.ss.domain.BaseOutput;

@RequestMapping("/member")
@Controller
public class MemberController {

	@Autowired
	private UserRpc userRPC;

	@RequestMapping(value = "/members", method = RequestMethod.GET)
	public String members(ModelMap modelMap, @RequestParam("textboxId") String textboxId, String dep) {
		modelMap.put("textboxId", textboxId);
		modelMap.put("dep", dep);
		if (StringUtils.isNotBlank(dep)) {
			AlmCache.getInstance().getDepMap().forEach((k, v) -> {
				if (Objects.equals(v.getCode(), dep)) {
					modelMap.put("dep", k);
				}
			});
		}
		return "member/members";
	}

	@ResponseBody
	@RequestMapping(value = "/members", method = {
			RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public List<UserDepartmentRole> membersJson(UserDepartmentRoleQuery user) {
		BaseOutput<List<UserDepartmentRole>> output = this.userRPC.findUserContainDepartmentAndRole(user);
		if (output.isSuccess()) {
			return output.getData();
		}
		return null;
	}

	@ResponseBody
	@RequestMapping("/loadUsersByDepartment")
	public List<User> loadUsersByDepartment(@RequestParam(required = false) List<Long> departmentId) {
		if (CollectionUtils.isEmpty(departmentId)) {
			return new ArrayList<>(AlmCache.getInstance().getUserMap().values().stream()
					.filter(u -> !u.getId().equals(1L)).collect(Collectors.toList()));
		}
		return AlmCache.getInstance().getUserMap().values().stream()
				.filter(u -> departmentId.contains(u.getDepartmentId())).collect(Collectors.toList());
	}
}
