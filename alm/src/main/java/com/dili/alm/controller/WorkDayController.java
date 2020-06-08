package com.dili.alm.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dili.alm.domain.dto.WorkDayRoleDto;
import com.dili.alm.service.WorkDayService;
import com.dili.alm.utils.DateUtil;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;




@Controller
@RequestMapping("/workDay")
public class WorkDayController {
	@Autowired
	private WorkDayService workDayService;


    @RequestMapping(value="/setWorkDay", method = RequestMethod.GET)
    public String workDay(ModelMap modelMap) {
        return "workDay/index";
    }

    /**
     * 判断是不是最后一天
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/isWorkEndDayDate", method = {RequestMethod.GET, RequestMethod.POST})
    public int isWorkEndDayDate() {
    	UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();

		if (userTicket == null) {
			return 2;
		}
		BaseOutput<WorkDayRoleDto> showWorkDay = workDayService.showWorkDay(userTicket.getId());
		if("false".equals(showWorkDay.getCode())){
			return 3;
		}
		WorkDayRoleDto workDayRoleDto = showWorkDay.getData();
		if(workDayRoleDto.getId()==null){
			return 1;
		}
        int differentDaysByMillisecond = DateUtil.differentDaysByMillisecond(new Date(), workDayRoleDto.getWorkEndTime());
        if(differentDaysByMillisecond==0){
        	return 4;
        }
       return 0;
    }
    
    
	/**
	 * 上传ecxel解析导入mysql
	 * 
	 * @param file
	 * @param year
	 * @return
	 */
	@RequestMapping(value = "/uploadWordDayDate", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput uploadWordDayDate(@RequestParam("file") MultipartFile file, String year) {
		return workDayService.upload(file, year);
	}

	/**
	 * 查询导入的所有年份
	 * 
	 * @return
	 */
	@RequestMapping(value = "/selectWorkDayYear", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<String> selectWorkDayYear() {
		return workDayService.getWorkDayYaers();
	}

	/**
	 * 查询当前工作时间
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getWorkDay", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput<WorkDayRoleDto> getWorkDay(String userId) {
		if (WebUtil.strIsEmpty(userId)) {
			throw new RuntimeException("未登录");
		}
		BaseOutput<WorkDayRoleDto> showWorkDay = workDayService.showWorkDay(Long.parseLong(userId));
		if ("false".equals(showWorkDay.getCode())) {
			return BaseOutput.failure("需要导入新的工作日").setData(showWorkDay.getData());
		} else {
			return BaseOutput.success().setData(showWorkDay.getData());
		}

	}

	/**
	 * 查询当前年份
	 * 
	 * @return
	 */
	@RequestMapping(value = "/isHasWorkDayYear", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody boolean isHasWorkDayYear(String year) {
		List<String> workDayYaers = workDayService.getWorkDayYaers();
		return workDayYaers.contains(year);
	}
}
