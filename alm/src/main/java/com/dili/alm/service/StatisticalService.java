package com.dili.alm.service;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;

import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.ProjectTypeCountDTO;
import com.dili.alm.domain.dto.UploadProjectFileDto;
import com.dili.alm.exceptions.ProjectException;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;

public interface StatisticalService {

	Map<String,ProjectTypeCountDTO> getProjectTypeCountDTO(String startTime,
			String endTime);
	
}