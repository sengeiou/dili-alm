package com.dili.alm.controller;

import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.service.OnlineDataChangeService;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.sdk.session.SessionContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ��MyBatis Generator�����Զ�����
 * This file was generated on 2019-12-25 18:22:44.
 */
@Api("/onlineDataChange")
@Controller
@RequestMapping("/onlineDataChange")
public class OnlineDataChangeController {
    @Autowired
    OnlineDataChangeService onlineDataChangeService;

    @ApiOperation("��ת��indexҳ��")
    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "onlineDataChange/index";
    }
	@ApiOperation("��ת��dataChangeҳ��")
	@RequestMapping(value = "/dataChange.html", method = RequestMethod.GET)
	public String projectOverview(ModelMap modelMap) {
		return "onlineDataChange/dataChange";
	}
	
	

    @ApiOperation(value="��ѯOnlineDataChange", notes = "��ѯOnlineDataChange�������б���Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="OnlineDataChange", paramType="form", value = "OnlineDataChange��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/list.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<OnlineDataChange> list(@ModelAttribute OnlineDataChange onlineDataChange) {
        return onlineDataChangeService.list(onlineDataChange);
    }

    @ApiOperation(value="��ҳ��ѯOnlineDataChange", notes = "��ҳ��ѯOnlineDataChange������easyui��ҳ��Ϣ")
    @ApiImplicitParams({
		@ApiImplicitParam(name="OnlineDataChange", paramType="form", value = "OnlineDataChange��form��Ϣ", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(@ModelAttribute OnlineDataChange onlineDataChange) throws Exception {
        return onlineDataChangeService.listEasyuiPageByExample(onlineDataChange, true).toString();
    }

    @ApiOperation("����OnlineDataChange")
    @ApiImplicitParams({
		@ApiImplicitParam(name="OnlineDataChange", paramType="form", value = "OnlineDataChange��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(@ModelAttribute OnlineDataChange onlineDataChange) {
    	Long  id=SessionContext.getSessionContext().getUserTicket().getId();
    	onlineDataChange.setApplyUserId(id);
        onlineDataChangeService.insertSelective(onlineDataChange);
        return BaseOutput.success("�����ɹ�");
    }

    @ApiOperation("�޸�OnlineDataChange")
    @ApiImplicitParams({
		@ApiImplicitParam(name="OnlineDataChange", paramType="form", value = "OnlineDataChange��form��Ϣ", required = true, dataType = "string")
	})
    @RequestMapping(value="/update.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(@ModelAttribute OnlineDataChange onlineDataChange) {
        onlineDataChangeService.updateSelective(onlineDataChange);
        return BaseOutput.success("�޸ĳɹ�");
    }

    @ApiOperation("ɾ��OnlineDataChange")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "OnlineDataChange������", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete.action", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        onlineDataChangeService.delete(id);
        return BaseOutput.success("ɾ���ɹ�");
    }
}