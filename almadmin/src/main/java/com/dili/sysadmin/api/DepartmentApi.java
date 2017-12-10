package com.dili.sysadmin.api;

import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.Department;
import com.dili.sysadmin.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentApi {

	@Autowired
	private DepartmentService departmentService;

	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> list(@RequestBody(required = false) Department department) {
		List<Department> list = this.departmentService.listByExample(department);
		return BaseOutput.success().setData(list);
	}

	@RequestMapping(value = "/findByUserId", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<List<Department>> list(@RequestBody Long userId) {
		List<Department> list = this.departmentService.findByUserId(userId);
		return BaseOutput.success().setData(list);
	}

}
