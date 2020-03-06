package com.dili.alm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.rpc.DepartmentRpc;

@Controller
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentRpc departmentRpc;


    @RequestMapping(value = "/departments.html", method = RequestMethod.GET)
    public String members(ModelMap modelMap, @RequestParam("textboxId") String textboxId) {
        modelMap.put("textboxId", textboxId);
        return "dep/dep";
    }

    @ResponseBody
    @RequestMapping(value = "/departments", method = { RequestMethod.GET, RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Department> departmentJson(Department department){
        BaseOutput<List<Department>> baseOutput = departmentRpc.listByDepartment(department);
        return baseOutput.getData();
    }

}
