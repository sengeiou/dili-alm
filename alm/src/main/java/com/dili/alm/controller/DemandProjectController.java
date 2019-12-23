package com.dili.alm.controller;

import com.dili.alm.domain.DemandProject;
import com.dili.alm.service.DemandProjectService;
import com.dili.ss.domain.BaseOutput;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ��MyBatis Generator�����Զ�����
 * This file was generated on 2019-12-23 17:47:02.
 */
@Api("/demandProject")
@Controller
@RequestMapping("/demandProject")
public class DemandProjectController {
    @Autowired
    DemandProjectService demandProjectService;

    @ApiOperation("��ת��DemandProjectҳ��")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "demandProject/index";
    }

    @ApiOperation(value="��ѯDemandProject", notes = "��ѯDemandProject�������б���Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="DemandProject", paramType="form", value = "DemandProject��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/list.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<DemandProject> list(DemandProject demandProject) {
        return demandProjectService.list(demandProject);
    }

    @ApiOperation(value="��ҳ��ѯDemandProject", notes = "��ҳ��ѯDemandProject������easyui��ҳ��Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="DemandProject", paramType="form", value = "DemandProject��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(DemandProject demandProject) throws Exception {
        return demandProjectService.listEasyuiPageByExample(demandProject, true).toString();
    }

    @ApiOperation("����DemandProject")
    @ApiImplicitParams({
		@ApiImplicitParam(name="DemandProject", paramType="form", value = "DemandProject��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(DemandProject demandProject) {
        demandProjectService.insertSelective(demandProject);
        return BaseOutput.success("�����ɹ�");
    }

    @ApiOperation("�޸�DemandProject")
    @ApiImplicitParams({
		@ApiImplicitParam(name="DemandProject", paramType="form", value = "DemandProject��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(DemandProject demandProject) {
        demandProjectService.updateSelective(demandProject);
        return BaseOutput.success("�޸ĳɹ�");
    }

    @ApiOperation("ɾ��DemandProject")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "DemandProject������", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        demandProjectService.delete(id);
        return BaseOutput.success("ɾ���ɹ�");
    }
}