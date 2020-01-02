package com.dili.alm.domain;
import javax.persistence.Column;

import com.dili.ss.domain.annotation.Like;
import com.dili.uap.sdk.domain.Systems;

/**
 * 用于查询系统信息对象
 * Created by asiam on 2018/5/22 0022.
 */
public interface SystemDto extends Systems {
    @Column(name = "`name`")
    @Like(Like.BOTH)
    String getLikeName();
    void setLikeName(String likeName);
}