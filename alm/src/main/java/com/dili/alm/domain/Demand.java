package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2019-12-24 10:20:34.
 */
@Table(name = "`demand`")
public class Demand extends BaseDomain {
    /**
     * 自增编号
     */
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 需求编号，区域首字母+年月日+时分+001自增数字
     */
    @Column(name = "`serial_number`")
    private String serialNumber;

    /**
     * 需求名称
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 需求申请人
     */
    @Column(name = "`user_id`")
    private Long userId;

    /**
     * 所属系统
     */
    @Column(name = "`belong_pro_id`")
    private Long belongProId;
    /**
     * 所属系统
     */
    @Column(name = "`belong_sys_id`")
    private Long belongSysId;

    /**
     * 需求类型（数据字典）
     */
    @Column(name = "`type`")
    private Byte type;

    /**
     * 需求状态（数据字典）
     */
    @Column(name = "`status`")
    private Byte status;

    /**
     * 需求期望实现时间(年月日)
     */
    @Column(name = "`finish_date`")
    private Date finishDate;

    /**
     * 相关附件
     */
    @Column(name = "`document_url`")
    private String documentUrl;

    /**
     * 创建时间
     */
    @Column(name = "`create_date`")
    private Date createDate;

    /**
     * 提交时间（开启流程）
     */
    @Column(name = "`submit_date`")
    private Date submitDate;

    /**
     * 需求内容
     */
    @Column(name = "`content`")
    private String content;

    /**
     * 需求背景
     */
    @Column(name = "`reason`")
    private String reason;

    /**
     * 获取自增编号
     *
     * @return id - 自增编号
     */
    @FieldDef(label="自增编号")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getId() {
        return id;
    }

    /**
     * 设置自增编号
     *
     * @param id 自增编号
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取需求编号，区域首字母+年月日+时分+001自增数字
     *
     * @return serial_number - 需求编号，区域首字母+年月日+时分+001自增数字
     */
    @FieldDef(label="需求编号，区域首字母+年月日+时分+001自增数字", maxLength = 25)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * 设置需求编号，区域首字母+年月日+时分+001自增数字
     *
     * @param serialNumber 需求编号，区域首字母+年月日+时分+001自增数字
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * 获取需求名称
     *
     * @return name - 需求名称
     */
    @FieldDef(label="需求名称", maxLength = 25)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getName() {
        return name;
    }

    /**
     * 设置需求名称
     *
     * @param name 需求名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取需求申请人
     *
     * @return user_id - 需求申请人
     */
    @FieldDef(label="需求申请人")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置需求申请人
     *
     * @param userId 需求申请人
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取所属系统
     *
     * @return belong_pro_id - 所属项目
     */
    @FieldDef(label="所属系统")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getBelongProId() {
        return belongProId;
    }

    /**
     * 设置所属系统
     *
     * @param belongProId 所属项目
     */
    public void setBelongProId(Long belongProId) {
        this.belongProId = belongProId;
    }
    
    /**
     * 获取所属系统
     *
     * @return belong_pro_id - 所属项目
     */
    @FieldDef(label="所属系统")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getBelongSysId() {
        return belongProId;
    }

    /**
     * 设置所属系统
     *
     * @param belongProId 所属项目
     */
    public void setBelongSysId(Long belongSysId) {
        this.belongSysId =belongSysId;
    }

    /**
     * 获取需求类型（数据字典）
     *
     * @return type - 需求类型（数据字典）
     */
    @FieldDef(label="需求类型（数据字典）")
    @EditMode(editor = FieldEditor.Text, required = true)
    public Byte getType() {
        return type;
    }

    /**
     * 设置需求类型（数据字典）
     *
     * @param type 需求类型（数据字典）
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取需求状态（数据字典）
     *
     * @return status - 需求状态（数据字典）
     */
    @FieldDef(label="需求状态（数据字典）")
    @EditMode(editor = FieldEditor.Text, required = true)
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置需求状态（数据字典）
     *
     * @param status 需求状态（数据字典）
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取需求期望实现时间(年月日)
     *
     * @return finish_date - 需求期望实现时间(年月日)
     */
    @FieldDef(label="需求期望实现时间(年月日)")
    @EditMode(editor = FieldEditor.Date, required = true)
    public Date getFinishDate() {
        return finishDate;
    }

    /**
     * 设置需求期望实现时间(年月日)
     *
     * @param finishDate 需求期望实现时间(年月日)
     */
    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    /**
     * 获取相关附件
     *
     * @return document_url - 相关附件
     */
    @FieldDef(label="相关附件", maxLength = 120)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getDocumentUrl() {
        return documentUrl;
    }

    /**
     * 设置相关附件
     *
     * @param documentUrl 相关附件
     */
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    /**
     * 获取创建时间
     *
     * @return create_date - 创建时间
     */
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 设置创建时间
     *
     * @param createDate 创建时间
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 获取提交时间（开启流程）
     *
     * @return submit_date - 提交时间（开启流程）
     */
    @FieldDef(label="提交时间（开启流程）")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Date getSubmitDate() {
        return submitDate;
    }

    /**
     * 设置提交时间（开启流程）
     *
     * @param submitDate 提交时间（开启流程）
     */
    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    /**
     * 获取需求内容
     *
     * @return content - 需求内容
     */
    @FieldDef(label="需求内容")
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getContent() {
        return content;
    }

    /**
     * 设置需求内容
     *
     * @param content 需求内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取需求背景
     *
     * @return reason - 需求背景
     */
    @FieldDef(label="需求背景")
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getReason() {
        return reason;
    }

    /**
     * 设置需求背景
     *
     * @param reason 需求背景
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
}