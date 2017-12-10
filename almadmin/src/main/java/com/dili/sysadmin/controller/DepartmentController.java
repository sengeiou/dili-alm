package com.dili.sysadmin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.sysadmin.domain.Department;
import com.dili.sysadmin.domain.dto.DepartmentTree;
import com.dili.sysadmin.domain.dto.DepartmentUserCountDto;
import com.dili.sysadmin.service.DepartmentService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-09-07 09:48:51.
 */
@Api("/department")
@Controller
@RequestMapping("/department")
public class DepartmentController {
	@Autowired
	DepartmentService departmentService;

	@ApiOperation("跳转到Department页面")
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "department/index";
	}

	@ApiOperation(value = "查询Department", notes = "查询Department，返回列表信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Department", paramType = "form", value = "Department的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/list", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<Department> list(Department department) {
		List<Department> list = this.departmentService.list(department);
		return list;
	}

	@ResponseBody
	@RequestMapping(value = "/listDepartmentUserCount", method = { RequestMethod.GET, RequestMethod.POST })
	public String listDepartmentUserCount(Department department) {
		EasyuiPageOutput e = new EasyuiPageOutput();
		List<DepartmentUserCountDto> list = departmentService.listDepartmentUserCount(department);
		e.setRows(list);
		e.setTotal(list.size());
		return e.toString();
	}

	@ResponseBody
	@RequestMapping(value = "/listTree.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Department> listTree(Department department) {
		List<Department> list = this.departmentService.list(department);
		return list;
	}

	@ApiOperation(value = "分页查询Department", notes = "分页查询Department，返回easyui分页信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Department", paramType = "form", value = "Department的form信息", required = false, dataType = "string") })
	@RequestMapping(value = "/listPage", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(@ModelAttribute Department department) throws Exception {
		return departmentService.listEasyuiPageByExample(department, true).toString();
	}

	@ApiOperation("新增Department")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Department", paramType = "form", value = "Department的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/insert", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> insert(@ModelAttribute Department department) {
		return departmentService.insertAfterCheck(department);
	}

	@ApiOperation("修改Department")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Department", paramType = "form", value = "Department的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/update", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> update(@ModelAttribute Department department) {
		return departmentService.updateAfterCheck(department);
	}

	@ApiOperation("删除Department")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "form", value = "Department的主键", required = true, dataType = "long") })
	@RequestMapping(value = "/delete", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> delete(Long id) {
		return departmentService.checkBeforeDelete(id);
	}

	@RequestMapping(value = "/updateDepartment.html", method = RequestMethod.GET)
	public String updateDepartmentView(@RequestParam Long departmentId, ModelMap map) {
		Department dept = this.departmentService.findById(departmentId);
		map.addAttribute("model", dept);
		return "department/updateDepartment";
	}

	@RequestMapping(value = "/orgStructure", method = RequestMethod.GET)
	public String orgStructure(ModelMap map) {
		List<DepartmentTree> depts = this.departmentService.listTree();
		map.addAttribute("depts", depts);
		return "department/orgStructure";
	}
}