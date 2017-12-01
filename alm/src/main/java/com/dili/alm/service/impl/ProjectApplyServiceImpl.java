package com.dili.alm.service.impl;

import com.dili.alm.cache.ProjectNumberGenerator;
import com.dili.alm.dao.ProjectApplyMapper;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.service.ProjectApplyService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2017-11-21 16:19:02.
 */
@Service
public class ProjectApplyServiceImpl extends BaseServiceImpl<ProjectApply, Long> implements ProjectApplyService {

    @Autowired
    private ProjectNumberGenerator projectNumberGenerator;
    @Autowired
    private DataDictionaryService dataDictionaryService;

    private static final String APPLY_PLAN_PHASE_CODE = "apply_plan_phase";

    public ProjectApplyMapper getActualDao() {
        return (ProjectApplyMapper) getDao();
    }

    @Override
    public int insertApply(ProjectApply apply) {
        apply.setNumber(getProjectNumber(apply));
        apply.setName(apply.getNumber());
        return getActualDao().insertSelective(apply);
    }

    @Override
    public List<DataDictionaryValueDto> getPlanPhase() {
        DataDictionaryDto dto = this.dataDictionaryService.findByCode(APPLY_PLAN_PHASE_CODE);
        if (dto == null) {
            return null;
        }
        return dto.getValues();
    }

    /**
     * 生成规则方式为[级别编号+项目级别]-[2位年]-[4位自增数]
     *
     * @param apply
     * @return
     */
    private String getProjectNumber(ProjectApply apply) {
        String number = apply.getType() + (apply.getPid() == null ? "1" : "2");
        String yearLast = new SimpleDateFormat("yy", Locale.CHINESE).format(Calendar.getInstance().getTime());
        return number + "-" + yearLast + "-" + projectNumberGenerator.get();
    }
}