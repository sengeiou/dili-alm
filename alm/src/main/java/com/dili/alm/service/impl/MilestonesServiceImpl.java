package com.dili.alm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dili.alm.dao.MilestonesMapper;
import com.dili.alm.domain.Files;
import com.dili.alm.domain.Milestones;
import com.dili.alm.service.FilesService;
import com.dili.alm.service.MilestonesService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.quartz.domain.QuartzConstants;
import com.dili.ss.quartz.domain.ScheduleJob;
import com.dili.ss.quartz.service.ScheduleJobService;
import com.dili.ss.util.CronDateUtils;
import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-10-20 11:02:17.
 */
@Service
public class MilestonesServiceImpl extends BaseServiceImpl<Milestones, Long> implements MilestonesService {

    @Autowired
    FilesService filesService;

    @Autowired
    ScheduleJobService scheduleJobService;

    public MilestonesMapper getActualDao() {
        return (MilestonesMapper)getDao();
    }

    @Override
    public int insertSelective(Milestones milestones) {
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        milestones.setPublishMemberId(userTicket.getId());
        milestones.setCreated(new Date());
        int rowCnt = super.insertSelective(milestones);
        //如果要通知，则生成调度信息
        if(milestones.getEmailNotice().equals(1)){
            ScheduleJob scheduleJob = DTOUtils.newDTO(ScheduleJob.class);
            scheduleJob.setJobStatus(QuartzConstants.JobStatus.NORMAL.getCode());
            scheduleJob.setIsConcurrent(QuartzConstants.Concurrent.Async.getCode());
            scheduleJob.setJobGroup("milestones");
            scheduleJob.setJobName(milestones.getCode());
            scheduleJob.setDescription("里程碑通知, code:"+milestones.getCode()+", version:"+milestones.getVersion() + ", market:" + milestones.getMarket());
            scheduleJob.setSpringId("emailNoticeJob");
            scheduleJob.setStartDelay(0);
            scheduleJob.setMethodName("scan");
            scheduleJob.setCronExpression(CronDateUtils.getCron(new Date(System.currentTimeMillis()+10000)));
            scheduleJob.setJobData(JSONObject.toJSONStringWithDateFormat(milestones, "yyyy-MM-dd HH:mm:ss"));
            scheduleJobService.insertSelective(scheduleJob);
        }
        return rowCnt;
    }

    @Override
    public int updateSelective(Milestones milestones) {
        UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
        milestones.setModifyMemberId(userTicket.getId());
        milestones.setModified(new Date());
        return super.updateSelective(milestones);
    }

    @Override
    public int delete(Long id) {
        Milestones milestones = DTOUtils.newDTO(Milestones.class);
        milestones.setParentId(id);
        List<Milestones> list = list(milestones);
        //没有子里程碑才能删除，并且删除所有里程碑下的文件
        if(list.isEmpty()) {
            Files files = DTOUtils.newDTO(Files.class);
            files.setMilestonesId(id);
            List<Files> filesList = filesService.list(files);
            for(Files file : filesList){
                filesService.delete(file);
            }
            return super.delete(id);
        }else{
            return 0;
        }
    }
}