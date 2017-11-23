package com.dili.sysadmin.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-11-23 11:01:55.
 */
@Table(name = "`position`")
public class Position extends BaseDomain {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 职务名称
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 部门id
     */
    @Column(name = "`department_id`")
    private Long departmentId;

    @Column(name = "`rank`")
    private String rank;

    /**
     * @return id
     */
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取职务名称
     *
     * @return name - 职务名称
     */
    @FieldDef(label="职务名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getName() {
        return name;
    }

    /**
     * 设置职务名称
     *
     * @param name 职务名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取部门id
     *
     * @return department_id - 部门id
     */
    @FieldDef(label="部门id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * 设置部门id
     *
     * @param departmentId 部门id
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * @return rank
     */
    @FieldDef(label="rank", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getRank() {
        return rank;
    }

    /**
     * @param rank
     */
    public void setRank(String rank) {
        this.rank = rank;
    }
}