package com.dili.alm.controller;

import com.dili.alm.domain.DemandProject;
import com.dili.alm.domain.dto.DemandProjectDto;
import com.dili.alm.service.DemandProjectService;
import com.dili.ss.domain.BaseOutput;
/*import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;*/
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2019-12-23 17:47:02.
 */
/*@Api("/demandProject")*/
@Controller
@RequestMapping("/demandProject")
public class DemandProjectController {
    @Autowired
    DemandProjectService demandProjectService;

    /*@ApiOperation("跳转到DemandProject页面")*/
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "demandProject/index";
    }

/*    @ApiOperation(value="查询DemandProject", notes = "查询DemandProject，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="DemandProject", paramType="form", value = "DemandProject的form信息", required = false, dataType = "string")
	})*/
    @RequestMapping(value="/list.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<DemandProject> list(DemandProject demandProject) {
        return demandProjectService.list(demandProject);
    }

/*    @ApiOperation(value="分页查询DemandProject", notes = "分页查询DemandProject，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="DemandProject", paramType="form", value = "DemandProject的form信息", required = false, dataType = "string")
	})*/
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(DemandProject demandProject) throws Exception {
        return demandProjectService.listEasyuiPageByExample(demandProject, true).toString();
    }

/*    @ApiOperation("新增DemandProject")
    @ApiImplicitParams({
		@ApiImplicitParam(name="DemandProject", paramType="form", value = "DemandProject的form信息", required = true, dataType = "string")
	})*/
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(DemandProject demandProject) {
        demandProjectService.insertSelective(demandProject);
        return BaseOutput.success("新增成功");
    }

/*    @ApiOperation("修改DemandProject")
    @ApiImplicitParams({
		@ApiImplicitParam(name="DemandProject", paramType="form", value = "DemandProject的form信息", required = true, dataType = "string")
	})*/
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(DemandProject demandProject) {
        demandProjectService.updateSelective(demandProject);
        return BaseOutput.success("修改成功");
    }

/*    @ApiOperation("删除DemandProject")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "DemandProject的主键", required = true, dataType = "long")
	})*/
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        demandProjectService.delete(id);
        return BaseOutput.success("删除成功");
    }
    
   /* @ApiOperation("新增需求关联集合")*/
    @RequestMapping(value="/insertDemandProjectDto.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insertDemandProjectDto(DemandProjectDto demandProjectDto) {
    	return demandProjectService.insertDemandProjectDto(demandProjectDto);
         
    }
/*    @ApiOperation("删除关联根据项目id,版本id,工单id")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "DemandProject的主键", required = true, dataType = "long")
	})*/
    @RequestMapping(value="/deleteByProjectOrVersionOrWorkOrder.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput deleteByProjectOrVersionOrWorkOrder(Long applyId,Long versionId,Long workOrderId ) {
        
    	return demandProjectService.deleteByProjectOrVersionOrWorkOrder(applyId,versionId,workOrderId);
      
    }
    
}