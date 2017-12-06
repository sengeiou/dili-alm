package com.dili.alm.controller;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectPhase;
import com.dili.alm.domain.ProjectVersion;
import com.dili.alm.domain.Task;
import com.dili.alm.domain.TaskDetails;
import com.dili.alm.service.ProjectService;
import com.dili.alm.service.ProjectVersionService;
import com.dili.alm.service.TaskDetailsService;
import com.dili.alm.service.TaskService;
import com.dili.alm.service.ProjectPhaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import com.mysql.fabric.xmlrpc.base.Data;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-23 10:23:05.
 */
@Api("/task")
@Controller
@RequestMapping("/task")
public class TaskController {
    @Autowired
    TaskService taskService;
    
    @Autowired
    ProjectService projectService;
    @Autowired
    ProjectVersionService projectVersionService;
    @Autowired
    ProjectPhaseService  projectPhaseService;
    @Autowired
    TaskDetailsService taskDetailsService;

    @ApiOperation("跳转到Task页面")
    @RequestMapping(value="/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        return "task/index";
    }

    @ApiOperation(value="查询Task", notes = "查询Task，返回列表信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Task", paramType="form", value = "Task的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/list", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody List<Task> list(Task task) {
    	
        return taskService.list(task);
    }

    @ApiOperation(value="分页查询Task", notes = "分页查询Task，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Task", paramType="form", value = "Task的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listPage", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listPage(Task task) throws Exception {
		//TODO:加入系统后放开 taskService.listEasyuiPageByExample(task, true).toString();
/*		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		task.setowner(userTicket.getId());*/
    	//TODO:修改任务负责人的
        return taskService.listPageSelectTaskDto(task).toString();
    }
    
    @ApiOperation(value="分页查询Task,首页显示", notes = "分页查询Task，返回easyui分页信息")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Task", paramType="form", value = "Task的form信息", required = false, dataType = "string")
	})
    @RequestMapping(value="/listTaskPageTab", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String listTaskPageTab(Task task) throws Exception {
		//TODO:加入系统后放开 taskService.listEasyuiPageByExample(task, true).toString();
/*		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		task.setowner(userTicket.getId());*/
    	//TODO:修改任务负责人的
        return taskService.listPageSelectTaskDto(task).toString();
    }
    
    @ApiOperation("新增Task")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Task", paramType="form", value = "Task的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/insert", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput insert(Task task,String planTimeStr) {

    	try {
        	
        	Short planTime =Short.parseShort(planTimeStr.trim());
        	
        	task.setPlanTime(planTime);
        	
		} catch (Exception e) {
			e.printStackTrace();
			return BaseOutput.failure("请正确填写工时");
		}
    	
		//TODO:加入系统后放开
        //设置任务创建人为当前登录用户
/*		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		task.setCreated(userTicket.getId());*/
    	task.setCreated(new Date());
    	task.setStatus((byte) 0);//新增的初始化状态为0未开始状态
        taskService.insertSelective(task); 

        return BaseOutput.success("新增成功");
    }


    @ApiOperation("修改Task")
    @ApiImplicitParams({
		@ApiImplicitParam(name="Task", paramType="form", value = "Task的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/update", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput update(Task task,String planTimeStr) {

    	try {
        	
        	Short planTime =Short.parseShort(planTimeStr.trim());
        	
        	task.setPlanTime(planTime);
        	//TODO:加入系统后放开
            //设置任务修改人为当前登录用户
    /*		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
    		task.setModifyMemberId(userTicket.getId());*/
        	task.setModified(new Date());
    		
		} catch (Exception e) {
			
			return BaseOutput.failure("请正确填写工时");
		}
        taskService.updateSelective(task);
        return BaseOutput.success("修改成功");
    }

    @ApiOperation("删除Task")
    @ApiImplicitParams({
		@ApiImplicitParam(name="id", paramType="form", value = "Task的主键", required = true, dataType = "long")
	})
    @RequestMapping(value="/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput delete(Long id) {
        taskService.delete(id);
        return BaseOutput.success("删除成功");
    }
    
    //查询
	@ResponseBody
	@RequestMapping(value = "/listTree.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Task> listTree(Task task) {//changeID 执行人
		//TODO:加入系统后放开
/*		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		task.setChangeId(userTicket.getId());*/
		System.out.println(task.toString());
		List<Task> list = this.taskService.list(task);
		
		return list;
	}

    //查询前置任务
	@ResponseBody
	@RequestMapping(value = "/getTask.json", method = { RequestMethod.GET, RequestMethod.POST })
	public Task listTree(long id) {//changeID 执行人
		Task task =this.taskService.get(id);
		return task;
	}
	
	//查询项目
	@ResponseBody
	@RequestMapping(value = "/listTreeProject.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<Project> listProject() {
		Project project = DTOUtils.newDTO(Project.class);
		List<Project> list = projectService.list(project);
		return list;
	}
	
	//查询版本
	@ResponseBody
	@RequestMapping(value = "/listTreeVersion.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<ProjectVersion> listVersion() {
		ProjectVersion projectVersion = DTOUtils.newDTO(ProjectVersion.class);
		List<ProjectVersion> list = projectVersionService.list(projectVersion);
		return list;
	}
	
	//查询阶段
	@ResponseBody
	@RequestMapping(value = "/listTreePhase.json", method = { RequestMethod.GET, RequestMethod.POST })
	public List<ProjectPhase> listPhase() {
		ProjectPhase projectPhase = DTOUtils.newDTO(ProjectPhase.class);
		List<ProjectPhase> list = projectPhaseService.list(projectPhase);
		return list;
	}
	
	//查询任务详细信息
	@ResponseBody
	@RequestMapping(value = "/listTaskDetail.json", method = { RequestMethod.GET, RequestMethod.POST })
	public TaskDetails listTaskDetail(Long id) {
		TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
		taskDetails.setTaskId(id);
		List<TaskDetails> list = taskDetailsService.list(taskDetails);

		if (list==null||list.size()==0) {
			return null;
		}
		return list.get(0);
	}
	
	//更新任务信息
    @ApiOperation("填写任务工时")
    @ApiImplicitParams({
		@ApiImplicitParam(name="updateTaskDetails", paramType="form", value = "TaskDetails的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/updateTaskDetails", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput updateTaskDetails(TaskDetails taskDetails,String planTimeStr,String overHourStr,String taskHourStr) {
        /*		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		task.setModifyMemberId(userTicket.getId());*/
    	taskHourStr=taskHourStr.trim();
    	overHourStr=overHourStr.trim();
    	//转数据类型
        taskDetails.setCreated(new Date());
        if (taskHourStr!=null&&taskHourStr!="") {
        	taskDetails.setTaskHour(Short.parseShort(taskHourStr));
		}else{
			taskDetails.setTaskHour((short)0);
		}
        
        if (overHourStr!=null&&overHourStr!="") {
        	 taskDetails.setOverHour(Short.parseShort(overHourStr));
        }else{
			taskDetails.setOverHour((short)0);
		}
        if(planTimeStr.trim().equals(taskHourStr)){
        	Task task = taskService.get(taskDetails.getTaskId());
        	task.setStatus((byte)3);//更新状态为完成
            taskService.update(task);
        }
		//taskDetails.setCreateMemberId(userTicket.getId());
        taskDetailsService.update(taskDetails);//保存任务
        return BaseOutput.success("修改成功");
    }
    
	//开始执行任务
    @ApiOperation("开始执行任务")
    @ApiImplicitParams({
		@ApiImplicitParam(name="updateTaskStatus", paramType="form", value = "TaskDetails的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/updateTaskStatus", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput updateTaskDetails(Long id) {
        /*		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		task.setModifyMemberId(userTicket.getId());*/
    	Task task = taskService.get(id);
    	//暂停转为更新的
    	if (task.getStatus()!=2) {
            TaskDetails taskDetails = DTOUtils.newDTO(TaskDetails.class);
    		taskDetails.setTaskId(id);
    		taskDetails.setCreated(new Date());
    		taskDetails.setTaskHour((short)0);
    		taskDetails.setOverHour((short)0);
    		taskDetails.setTaskTime("0");
    		//taskDetails.setCreateMemberId(userTicket.getId());
    		taskDetailsService.insert(taskDetails);
		}
    	
    	task.setStatus((byte)1);//更新状态为开始任务
        taskService.update(task);
        

        return BaseOutput.success("修改成功");
    }
    
	//暂停任务
    @ApiOperation("暂停执行任务")
    @ApiImplicitParams({
		@ApiImplicitParam(name="pauseTaskStatus", paramType="form", value = "TaskDetails的form信息", required = true, dataType = "string")
	})
    @RequestMapping(value="/pauseTaskStatus", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody BaseOutput pauseTask(Long id) {
        /*		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		task.setModifyMemberId(userTicket.getId());*/
    	System.out.println(id);
    	Task task = taskService.get(id);
    	task.setStatus((byte)2);//更新状态为开始任务
    	task.setModified(new Date());
    	//task.setModifyMemberId(userTicket.getId());
        taskService.update(task);
        return BaseOutput.success("已暂停任务");
    }
    
    
}