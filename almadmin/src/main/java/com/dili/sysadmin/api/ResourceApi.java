package com.dili.sysadmin.api;

import io.swagger.annotations.Api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.ss.domain.BaseOutput;
import com.dili.sysadmin.domain.Role;
import com.dili.sysadmin.service.ResourceService;

@Api("/resource")
@Controller
@RequestMapping("/resourceApi")
public class ResourceApi {
	@Autowired
	private ResourceService resourceService;
	
	@RequestMapping(value = "/listResourceCodeByUserId")
    @ResponseBody
    public BaseOutput<List<String>> listResourceCodeByUserId(@RequestBody Long id) {
        return BaseOutput.success().setData(resourceService.findCodeByUserId(id));
    }
}
