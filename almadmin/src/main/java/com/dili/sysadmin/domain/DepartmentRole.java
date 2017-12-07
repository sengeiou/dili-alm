package com.dili.sysadmin.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-12-07 15:11:37.
 */
@Table(name = "`department_role`")
public class DepartmentRole extends BaseDomain {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 部门id
     */
    @Column(name = "`department_id`")
    private Long departmentId;

    /**
     * 角色id
     */
    @Column(name = "`role_id`")
    private Long roleId;

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
     * 获取角色id
     *
     * @return role_id - 角色id
     */
    @FieldDef(label="角色id")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getRoleId() {
        return roleId;
    }

    /**
     * 设置角色id
     *
     * @param roleId 角色id
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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