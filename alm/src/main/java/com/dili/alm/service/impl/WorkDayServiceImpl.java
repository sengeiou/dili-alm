package com.dili.alm.service.impl;

import java.text.ParseException;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dili.alm.dao.WorkDayMapper;
import com.dili.alm.domain.WorkDay;
import com.dili.alm.domain.WorkDayEntity;
import com.dili.alm.service.WorkDayService;
import com.dili.alm.utils.DateUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
@Service
public class WorkDayServiceImpl extends BaseServiceImpl<WorkDay, Long> implements WorkDayService{
	public WorkDayMapper getActualDao() {
        return (WorkDayMapper)getDao();
    }
	
	public static final Long WORKDAYID=1L;
	
	
	//凌晨一点执行
		@Scheduled(cron = "0 0 1 * * ?")
//		@Scheduled(cron = "0 */1 * * * ?")
		 
		@Override
		public void updateNextWeeklyWorkDays() {
			Date as = new Date(new Date().getTime()-24*60*60*1000);
			WorkDay selectByPrimaryKey = this.getActualDao().selectByPrimaryKey(WORKDAYID);
			if(selectByPrimaryKey!=null){
				int i = DateUtil.differentDaysByMillisecond(as, selectByPrimaryKey.getWorkEndTime());
				if(i<=0){
					Date nextFriDay = DateUtil.getNextFriDay();
					Date nextMonDay = DateUtil.getNextMonDay();
					WorkDayEntity workDayEntity= new WorkDayEntity();
					workDayEntity.setId(WORKDAYID);
					workDayEntity.setWorkStartTime(nextMonDay);
					workDayEntity.setWorkEndTime(nextFriDay);
					this.getActualDao().updateByPrimaryKey(workDayEntity);
				}
			}else{
				Date startTime = DateUtil.getThisMonDay();
				Date endTime = DateUtil.getThisFriDay();
				WorkDayEntity workDayEntity= new WorkDayEntity();
				workDayEntity.setId(WORKDAYID);
				workDayEntity.setWorkStartTime(startTime);
				workDayEntity.setWorkEndTime(endTime);
				this.getActualDao().insert(workDayEntity);
				
			}
		}
}
