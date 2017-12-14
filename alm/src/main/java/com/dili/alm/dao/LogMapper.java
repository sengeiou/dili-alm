package com.dili.alm.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dili.alm.domain.Log;
import com.dili.ss.base.MyMapper;

public interface LogMapper extends MyMapper<Log> {
	int logLikeListCount(@Param("log")Log log, @Param("beginTime")String beginTime,@Param("endTime")String endTime);
	List<Log> logLikeList(@Param("log")Log log, @Param("beginTime")String beginTime, @Param("endTime")String endTime);
}