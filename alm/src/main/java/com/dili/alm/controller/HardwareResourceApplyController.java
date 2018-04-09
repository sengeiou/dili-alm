package com.dili.alm.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import tk.mybatis.mapper.entity.Example;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.cache.AlmCache;
import com.dili.alm.constant.AlmConstants;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.domain.HardwareResourceApplyState;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.ResourceEnvironment;
import com.dili.alm.domain.User;
import com.dili.alm.domain.dto.HardwareConfigurationDto;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.alm.rpc.UserRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.DataDictionaryValueService;
import com.dili.alm.service.HardwareResourceApplyService;
import com.dili.alm.service.ProjectService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-03-20 17:22:08.
 */
@Api("/hardwareResourceApply")
@Controller
@RequestMapping("/hardwareResourceApply")
public class HardwareResourceApplyController {
    @Autowired
    HardwareResourceApplyService hardwareResourceApplyService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private UserRpc userRpc;
	@Autowired
	private DepartmentRpc deptRpc;
	@Autowired
	private DataDictionaryValueService ddvService;
	
	@Autowired
	private DataDictionaryService ddService;
	
	private static final String DATA_AUTH_TYPE = "Project";
	
    @ApiOperation("跳转到HardwareResourceApply页面")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "hardwareResourceApply/index";
    }
    
    
    @RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addView(ModelMap modelMap) {
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return null;
		}
		List<Long> projectIds = new ArrayList<>();
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("dataId").toString())));
		Example example = new Example(ProjectOnlineApply.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", projectIds);
		List<Project> projects = this.projectService.selectByExample(example);
		modelMap.addAttribute("projects", projects);
		
		/**查询 所有部门***/
		List<Department> departments =  this.deptRpc.list(new Department()).getData();
		modelMap.addAttribute("departments", departments);
		
		/**个人信息**/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("userInfo", userTicket);
		
		/***环境**/
		DataDictionaryValue dd = DTOUtils.newDTO(DataDictionaryValue.class);
		dd.setDdId((long) 23);
		List<DataDictionaryValue> ddValue = ddvService.list(dd);
		modelMap.addAttribute("ddValue", ddValue);
		/***运维部门下的所有人员查询 begin**/
		Department deptQuery = new Department();
		deptQuery.setCode(AlmConstants.OPERATION_DEPARTMENT_CODE);
		BaseOutput<List<Department>> deptOutput = this.deptRpc.list(deptQuery);
		if (deptOutput.isSuccess() && CollectionUtils.isNotEmpty(deptOutput.getData())) {
			Long departmentId = deptOutput.getData().get(0).getId();
			User userQuery = new User();
			userQuery.setDepartmentId(departmentId);
			BaseOutput<List<User>> userOutput = this.userRpc.list(userQuery);
			if (userOutput.isSuccess() && CollectionUtils.isNotEmpty(userOutput.getData())) {
				StringBuilder sb = new StringBuilder();
				userOutput.getData().forEach(u -> sb.append(u.getEmail()).append(";"));
				modelMap.addAttribute("opUsers", sb.substring(0, sb.length() - 1));
			}
		}
		/***运维部门下的所有人员查询 end**/
		return "hardwareResourceApply/add";
	}
    
    
    @RequestMapping(value = "/toUpdate", method = RequestMethod.GET)
	public String updateView(@RequestParam Long id, ModelMap modelMap) {
		@SuppressWarnings("rawtypes")
		List<Map> dataAuths = SessionContext.getSessionContext().dataAuth(DATA_AUTH_TYPE);
		if (CollectionUtils.isEmpty(dataAuths)) {
			return null;
		}
		List<Long> projectIds = new ArrayList<>();
		dataAuths.forEach(m -> projectIds.add(Long.valueOf(m.get("dataId").toString())));
		Example example = new Example(HardwareResourceApply.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", projectIds);
		List<Project> projects = this.projectService.selectByExample(example);
		modelMap.addAttribute("projects", projects);
		List<Project> plist = this.projectService.list(null);
		modelMap.addAttribute("plist", plist).addAttribute("ulist", AlmCache.USER_MAP.values());
		
		/**查询 所有部门***/
		List<Department> departments =  this.deptRpc.list(new Department()).getData();
		modelMap.addAttribute("departments", departments);
		
		/**个人信息**/
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		modelMap.addAttribute("userInfo", userTicket);
		
		/***环境**/
		DataDictionaryValue dd = DTOUtils.newDTO(DataDictionaryValue.class);
		dd.setDdId((long) 23);
		List<DataDictionaryValue> ddValue = ddvService.list(dd);
		modelMap.addAttribute("ddValue", ddValue);
		
		/***运维部门下的所有人员查询 begin**/
		Department deptQuery = new Department();
		deptQuery.setCode(AlmConstants.OPERATION_DEPARTMENT_CODE);
		BaseOutput<List<Department>> deptOutput = this.deptRpc.list(deptQuery);
		HardwareResourceApply dto = this.hardwareResourceApplyService.get(id);
		modelMap.addAttribute("apply", dto);
		String[] se=dto.getServiceEnvironment().split(";");
		modelMap.addAttribute("se", se);
		modelMap.addAttribute("submit",DateUtil.getDate(dto.getSubmitTime()));//转化时间
		return "hardwareResourceApply/edit";
	}
    
    @ApiOperation(value="查询HardwareResourceApply", notes = "查询HardwareResourceApply，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResourceApply", paramType="form", value = "HardwareResourceApply的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<HardwareResourceApply> list(HardwareResourceApply hardwareResourceApply) {
        return hardwareResourceApplyService.list(hardwareResourceApply);
    }

    @ApiOperation(value="分页查询HardwareResourceApply", notes = "分页查询HardwareResourceApply，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResourceApply", paramType="form", value = "HardwareResourceApply的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(HardwareResourceApply hardwareResourceApply) throws Exception {
        return hardwareResourceApplyService.listEasyuiPageByExample(hardwareResourceApply, true).toString();
    }

    @ApiOperation("新增HardwareResourceApply")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResourceApply", paramType="form", value = "HardwareResourceApply的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/save", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(HardwareResourceApply hardwareResourceApply,String[] serviceEnvironmentChk,String configurationRequirementJsonStr) {
    	
    	String serviceEnvironmentStr ="";
    	for (int i = 0; i < serviceEnvironmentChk.length; i++) {
    		serviceEnvironmentStr += serviceEnvironmentChk[i]+";";
		}
    	
    	hardwareResourceApply.setServiceEnvironment(serviceEnvironmentStr);
    	hardwareResourceApply.setCreated(new Date());
    	hardwareResourceApply.setApplyState(HardwareResourceApplyState.APPLING.getValue());
    	hardwareResourceApply.setConfigurationRequirement(configurationRequirementJsonStr);
        hardwareResourceApplyService.insertSelective(hardwareResourceApply);
        
        return BaseOutput.success("新增成功");
    }

    @ApiOperation("修改HardwareResourceApply")
    @ApiImplicitParams({
		@ApiImplicitParam(name="HardwareResourceApply", paramType="form", value = "HardwareResourceApply的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(HardwareResourceApply hardwareResourceApply,String[] serviceEnvironmentChk,List<HardwareConfigurationDto> configurationRequirementJsonStr) {
        hardwareResourceApplyService.updateSelective(hardwareResourceApply);
    	String serviceEnvironmentStr ="";
    	for (int i = 0; i < serviceEnvironmentChk.length; i++) {
    		serviceEnvironmentStr += serviceEnvironmentChk[i]+";";
		}
    	hardwareResourceApply.setServiceEnvironment(serviceEnvironmentStr);
    	hardwareResourceApply.setConfigurationRequirement("123125465");
        return BaseOutput.success("修改成功");
    }
    @ApiOperation("删除HardwareResourceApply")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "HardwareResourceApply的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/updateeeee", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput updateeeee(Long id) {
        hardwareResourceApplyService.delete(id);
        return BaseOutput.success("删除成功");
    }
    
    public static JSONArray ProLogList2Json(List<HardwareConfigurationDto> list){
        JSONArray json = new JSONArray();
        for(HardwareConfigurationDto pLog : list){
            JSONObject jo = new JSONObject();
            jo.put("CPU", pLog.getCPU());
            jo.put("DDR", pLog.getDDR());
             
            json.add(jo);
        }
        return json;
   }
    @ApiOperation("删除HardwareResourceApply")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "HardwareResourceApply的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        hardwareResourceApplyService.delete(id);
        return BaseOutput.success("删除成功");
    }
    
    @ApiOperation("删除HardwareResourceApply")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "HardwareResourceApply的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/submit", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput submit(Long id) {
    	HardwareResourceApply dto = DTOUtils.newDTO(HardwareResourceApply.class);
    	dto.setId(id);
    	dto=hardwareResourceApplyService.get(id);
    	dto.setApplyState(HardwareResourceApplyState.APPROVING.getValue());
    	hardwareResourceApplyService.saveOrUpdate(dto);
        return BaseOutput.success("提交成功");
    }
}