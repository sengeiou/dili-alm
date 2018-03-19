package com.dili.alm.service;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import com.dili.alm.domain.Project;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.UploadProjectFileDto;
import com.dili.alm.exceptions.ProjectException;
import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;

public interface StatisticalService extends BaseService<Project, Long> {

}