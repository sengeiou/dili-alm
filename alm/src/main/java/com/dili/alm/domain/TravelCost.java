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
 * This file was generated on 2018-04-24 16:44:07.
 */
@Table(name = "`travel_cost`")
public interface TravelCost extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`apply_id`")
    @FieldDef(label="差旅费申请id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getApplyId();

    void setApplyId(Long applyId);

    @Column(name = "`travel_day_amount`")
    @FieldDef(label="出差天数")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getTravelDayAmount();

    void setTravelDayAmount(Integer travelDayAmount);

    @Column(name = "`set_out_place`")
    @FieldDef(label="出发地id，数据字典")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getSetOutPlace();

    void setSetOutPlace(Long setOutPlace);

    @Column(name = "`destination_place`")
    @FieldDef(label="到达地id，数据字典")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getDestinationPlace();

    void setDestinationPlace(Long destinationPlace);
}