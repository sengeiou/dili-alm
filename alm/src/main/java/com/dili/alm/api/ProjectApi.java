package com.dili.alm.api;

import com.dili.alm.domain.Demand;
import com.dili.alm.domain.Project;
import com.dili.alm.service.ProjectService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;

import io.swagger.annotations.Api;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Api("/projectApi")
@Controller
@RequestMapping("/projectApi")
public class ProjectApi {
	@Autowired
	ProjectService projectService;

	@RequestMapping("/selectAll.api")
	public @ResponseBody BaseOutput<List<Project>> selectAll() {
		Project project=DTOUtils.newDTO(Project.class);
		List<Project> selectAll = this.projectService.list(project);
		return BaseOutput.success().setData(selectAll);
	}
	
	@RequestMapping("/get.api")
	public @ResponseBody BaseOutput<Project> get(@RequestBody Long id) {
		return BaseOutput.success().setData(this.projectService.get(id));
	}
	
	@RequestMapping("/selectByIds.api")
	public @ResponseBody BaseOutput<List<Project>> selectByIds(@RequestBody List<String> ids) {
		List<Long> idsList=new ArrayList<Long>();
		if(ids!=null&&ids.size()>0) {
			for (String string : ids) {
				if(!WebUtil.strIsEmpty(string)) {
					idsList.add(Long.valueOf(string));
				}
			}

			return BaseOutput.success().setData(this.projectService.selectByIds(idsList));
		}
		return BaseOutput.failure("参数为空");
	}
}