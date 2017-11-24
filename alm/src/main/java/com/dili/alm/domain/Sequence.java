package com.dili.alm.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-11-23 15:32:18.
 */
@Table(name = "`sequence`")
public interface Sequence extends IBaseDomain {
    @Column(name = "`number`")
    @FieldDef(label="number")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getNumber();

    void setNumber(Integer number);
}