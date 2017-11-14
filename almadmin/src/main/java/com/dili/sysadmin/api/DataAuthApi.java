package com.dili.sysadmin.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.DataAuth;
import com.dili.sysadmin.domain.UserDataAuth;
import com.dili.sysadmin.service.DataAuthService;
import com.dili.sysadmin.service.UserDataAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 由MyBatis Generator工具自动生成 This file was generated on 2017-07-04 15:24:51.
 */
@Api("/dataAuth")
@Controller
@RequestMapping("/dataAuth")
public class DataAuthApi {
	@Autowired
	DataAuthService dataAuthService;

	@Autowired
	UserDataAuthService userDataAuthService;

	@ApiOperation("新增DataAuth")
	@ApiImplicitParams({ @ApiImplicitParam(name = "DataAuth", paramType = "form", value = "DataAuth的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/addDataAuth", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<Object> addDataAuth(@RequestBody DataAuth dataAuth) {
		dataAuthService.insertSelective(dataAuth);
		return BaseOutput.success("新增成功");
	}

	@ApiOperation("修改DataAuth的名称")
	@ApiImplicitParams({ @ApiImplicitParam(name = "DataAuth", paramType = "form", value = "DataAuth的form信息", required = true, dataType = "string") })
	@RequestMapping(value = "/updateDataAuth", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput updateDataAuth(@RequestBody DataAuth dataAuth) {
		DataAuth condition = new DataAuth();
		condition.setDataId(dataAuth.getDataId());
		condition.setType(dataAuth.getType());
		List<DataAuth> dataAuths = dataAuthService.list(condition);
		if (ListUtils.emptyIfNull(dataAuths).size() != 1) {
			return BaseOutput.failure("修改失败，dataId:" + dataAuth.getDataId() + "和type:" + dataAuth.getType() + ",不能找到唯一的数据权限");
		}
		dataAuth.setId(dataAuths.get(0).getId());
		dataAuthService.updateSelective(dataAuth);
		return BaseOutput.success("修改成功");
	}

	@ApiOperation("删除DataAuth")
	@ApiImplicitParams({ @ApiImplicitParam(name = "dataId", paramType = "form", value = "dataId", required = true, dataType = "long"),
			@ApiImplicitParam(name = "type", paramType = "form", value = "type", required = true, dataType = "string") })
	@RequestMapping(value = "/deleteDataAuth", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput deleteDataAuth(@RequestBody DataAuth dataAuth) {
		List<DataAuth> list = dataAuthService.list(dataAuth);
		if (ListUtils.emptyIfNull(list).size() != 1) {
			return BaseOutput.failure("删除失败，dataId:" + dataAuth.getDataId() + "和type:" + dataAuth.getType() + ",不能找到唯一的数据权限");
		}
		dataAuthService.delete(list.get(0).getId());
		return BaseOutput.success("删除成功");
	}

	@ApiOperation("添加用户数据权")
	@ApiImplicitParams({ @ApiImplicitParam(name = "id", paramType = "form", value = "DataAuth的主键", required = true, dataType = "long") })
	@ResponseBody
	@RequestMapping(value = "/addUserDataAuth", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseOutput<Object> addUserDataAuth(@RequestBody String json) {
		// @RequestParam Long userId, @RequestParam String dataId, @RequestParam String
		// type
		JSONObject jo = JSON.parseObject(json);
		DataAuth dataAuth = new DataAuth();
		dataAuth.setType(jo.getString("type"));
		dataAuth.setDataId(jo.getString("dataId"));
		List<DataAuth> dataAuths = dataAuthService.list(dataAuth);
		if (ListUtils.emptyIfNull(dataAuths).size() != 1) {
			return BaseOutput.failure("新增失败，dataId:" + jo.getString("dataId") + "和type:" + jo.getString("type") + ",不能找到唯一的数据权限");
		}
		UserDataAuth userDataAuth = new UserDataAuth();
		userDataAuth.setUserId(jo.getLong("userId"));
		userDataAuth.setDataAuthId(dataAuths.get(0).getId());
		if (userDataAuthService.list(userDataAuth).isEmpty()) {
			userDataAuthService.insertSelective(userDataAuth);
		}
		return BaseOutput.success("添加用户数据权成功");
	}

	@ResponseBody
	@RequestMapping(value = "/deleteUserDataAuth", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public BaseOutput<Object> deleteUserDataAuth(@RequestBody String json) {
		JSONObject jo = JSON.parseObject(json);
		DataAuth dataAuth = new DataAuth();
		dataAuth.setType(jo.getString("type"));
		dataAuth.setDataId(jo.getString("dataId"));
		List<DataAuth> dataAuths = dataAuthService.list(dataAuth);
		if (ListUtils.emptyIfNull(dataAuths).size() != 1) {
			return BaseOutput.failure("删除失败，dataId:" + jo.getString("dataId") + "和type:" + jo.getString("type") + ",不能找到唯一的数据权限");
		}
		UserDataAuth userDataAuth = new UserDataAuth();
		userDataAuth.setUserId(jo.getLong("userId"));
		userDataAuth.setDataAuthId(dataAuths.get(0).getId());
		userDataAuthService.delete(userDataAuth);
		return BaseOutput.success("删除用户数据权成功");
	}
}