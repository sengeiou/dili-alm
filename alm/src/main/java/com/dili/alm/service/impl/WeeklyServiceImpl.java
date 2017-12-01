package com.dili.alm.service.impl;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dili.alm.dao.WeeklyMapper;
import com.dili.alm.domain.Weekly;
import com.dili.alm.domain.dto.WeeklyPara;
import com.dili.alm.service.WeeklyService;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.EasyuiPageOutput;

/**
 * ��MyBatis Generator�����Զ�����
 * This file was generated on 2017-11-29 14:08:40.
 */
@Service
public class WeeklyServiceImpl extends BaseServiceImpl<Weekly, Long> implements WeeklyService {
	
	@Autowired
	WeeklyMapper weeklyMapper;
	
    public WeeklyMapper getActualDao() {
        return (WeeklyMapper)getDao();
    }

	@Override
	public EasyuiPageOutput getListPage(WeeklyPara weeklyPara) {
		
		//查询总页数
		int   total=weeklyMapper.selectPageByWeeklyParaCount(weeklyPara);
		//查询list
		CopyOnWriteArrayList<WeeklyPara>   list=weeklyMapper.selectListPageByWeeklyPara(weeklyPara);
		CopyOnWriteArrayList<WeeklyPara>   copyList=new 		CopyOnWriteArrayList();
		if(list!=null&&list.size()>0){
			for (WeeklyPara weeklyPara2 : list) {
				
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
				
			
						
				weeklyPara2.setDate(weeklyPara2.getStartDate()+" 到 "+weeklyPara2.getEndDate());
				if(weeklyPara2.getProjectType().equalsIgnoreCase("I")){
					weeklyPara2.setProjectType("内部项目");
				}else if(weeklyPara2.getProjectType().equalsIgnoreCase("K")){
					weeklyPara2.setProjectType("重点项目");
				}else if(weeklyPara2.getProjectType().equalsIgnoreCase("R")){
					weeklyPara2.setProjectType("预约项目");
				}else if(weeklyPara2.getProjectType().equalsIgnoreCase("G")){
					weeklyPara2.setProjectType("一般项目");
				}
				
				copyList.add(weeklyPara2);
				
			}
		}
	    
		
	
		EasyuiPageOutput   out=new EasyuiPageOutput(total,list);
		return out;
	}

	
}