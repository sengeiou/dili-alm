package com.dili.sysadmin.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.Department;
import com.dili.sysadmin.service.DepartmentService;

@RestController
@RequestMapping("/api/department")
public class DepartmentApi {

	@Autowired
	private DepartmentService departmentService;

	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> list(@RequestBody(required = false) Department department) {
		List<Department> list = this.departmentService.list(department);
		return BaseOutput.success().setData(list);
	}
}