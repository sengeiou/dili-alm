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
 * This file was generated on 2018-07-18 16:17:50.
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
    @FieldDef(label="出发地", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getSetOutPlace();

    void setSetOutPlace(String setOutPlace);

    @Column(name = "`destination_place`")
    @FieldDef(label="到达地", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getDestinationPlace();

    void setDestinationPlace(String destinationPlace);

    @Column(name = "`total_amount`")
    @FieldDef(label="总费用")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getTotalAmount();

    void setTotalAmount(Long totalAmount);
}