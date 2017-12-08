package com.dili.sysadmin.api;

import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.Role;
import com.dili.sysadmin.service.RoleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;


@Api("/role")
@Controller
@RequestMapping("/roleApi")
public class RoleApi {
    @Autowired
    RoleService roleService;

    @RequestMapping(value = "listRoleByUserId")
    @ResponseBody
    public BaseOutput<List<Role>> listRoleByUserId(@RequestBody Long id) {
        return BaseOutput.success().setData(roleService.findByUserId(id));
    }

    @RequestMapping(value = "listRoleNameByUserId")
    @ResponseBody
    public BaseOutput<String> listRoleNameByUserId(@RequestBody Long id) {
        List<Role> byUserId = roleService.findByUserId(id);
        return BaseOutput.success().setData(byUserId.stream().map(Role::getRoleName).collect(Collectors.joining(",")));
    }

}