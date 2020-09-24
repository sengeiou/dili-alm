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
 * This file was generated on 2020-02-07 13:03:04.
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

    @Column(name = "`belong_sys_id`")
    private Long belongSysId;

    /**
     * 需求类型（数据字典）
     */
    @Column(name = "`type`")
    private Integer type;

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
     * 需求处理人
     */
    @Column(name = "`reciprocate_id`")
    private Long reciprocateId;
    /**
     * 需求处理人
     */
    @Column(name = "`feedback_id`")
    private Long feedbackId;
    /**
     * 反馈文件
     */
    @Column(name = "`feedback_file`")
    private String feedbackFile;

    @Column(name = "`process_instance_id`")
    private String processInstanceId;

    @Column(name = "`process_definition_id`")
    private String processDefinitionId;

    @Column(name = "`process_type`")
    private String processType;

    @Column(name = "`modification_time`")
    private Date modificationTime;

    @Column(name = "`content`")
    private String content;

    @Column(name = "`reason`")
    private String reason;

    /**
     * 审核意见
     */
    @Column(name = "`approve_content`")
    private String approveContent;

    /**
     * 反馈方案
     */
    @Column(name = "`feedback_content`")
    private String feedbackContent;
    
    @Column(name = "`imperative`")
    private Integer imperative;

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
     * @return belong_pro_id - 所属系统
     */
    @FieldDef(label="所属系统")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getBelongProId() {
        return belongProId;
    }

    /**
     * 设置所属系统
     *
     * @param belongProId 所属系统
     */
    public void setBelongProId(Long belongProId) {
        this.belongProId = belongProId;
    }

    /**
     * @return belong_sys_id
     */
    @FieldDef(label="belongSysId")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getBelongSysId() {
        return belongSysId;
    }

    /**
     * @param belongSysId
     */
    public void setBelongSysId(Long belongSysId) {
        this.belongSysId = belongSysId;
    }

    /**
     * 获取需求类型（数据字典）
     *
     * @return type - 需求类型（数据字典）
     */
    @FieldDef(label="需求类型（数据字典）")
    @EditMode(editor = FieldEditor.Text, required = true)
    public Integer getType() {
        return type;
    }

    /**
     * 设置需求类型（数据字典）
     *
     * @param type 需求类型（数据字典）
     */
    public void setType(Integer type) {
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
    @EditMode(editor = FieldEditor.Datetime, required = false)
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
     * 获取需求处理人
     *
     * @return reciprocate_id - 需求处理人
     */
    @FieldDef(label="需求处理人")
    @EditMode(editor = FieldEditor.Text, required = false)
    public Long getReciprocateId() {
        return reciprocateId;
    }

    /**
     * 设置需求处理人
     *
     * @param reciprocateId 需求处理人
     */
    public void setReciprocateId(Long reciprocateId) {
        this.reciprocateId = reciprocateId;
    }

    
    /**
     * 获取需求处理人
     *
     * @return reciprocate_id - 需求处理人
     */
    @FieldDef(label="需求处理人")
    @EditMode(editor = FieldEditor.Text, required = false)
    public Long getFeedbackId() {
        return feedbackId;
    }

    /**
     * 设置需求处理人
     *
     * @param reciprocateId 需求处理人
     */
    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }
    /**
     * 获取反馈文件
     *
     * @return feedback_file - 反馈文件
     */
    @FieldDef(label="反馈文件", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getFeedbackFile() {
        return feedbackFile;
    }

    /**
     * 设置反馈文件
     *
     * @param feedbackFile 反馈文件
     */
    public void setFeedbackFile(String feedbackFile) {
        this.feedbackFile = feedbackFile;
    }

    /**
     * @return process_instance_id
     */
    @FieldDef(label="processInstanceId", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    /**
     * @param processInstanceId
     */
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    /**
     * @return process_definition_id
     */
    @FieldDef(label="processDefinitionId", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    /**
     * @param processDefinitionId
     */
    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    /**
     * @return process_type
     */
    @FieldDef(label="processType", maxLength = 30)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getProcessType() {
        return processType;
    }

    /**
     * @param processType
     */
    public void setProcessType(String processType) {
        this.processType = processType;
    }

    /**
     * @return modification_time
     */
    @FieldDef(label="modificationTime")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    public Date getModificationTime() {
        return modificationTime;
    }

    /**
     * @param modificationTime
     */
    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    /**
     * @return content
     */
    @FieldDef(label="content")
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getContent() {
        return content;
    }

    /**
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return reason
     */
    @FieldDef(label="reason")
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getReason() {
        return reason;
    }

    /**
     * @param reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 获取审核意见
     *
     * @return approve_content - 审核意见
     */
    @FieldDef(label="审核意见")
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getApproveContent() {
        return approveContent;
    }

    /**
     * 设置审核意见
     *
     * @param approveContent 审核意见
     */
    public void setApproveContent(String approveContent) {
        this.approveContent = approveContent;
    }

    /**
     * 获取反馈方案
     *
     * @return feedback_content - 反馈方案
     */
    @FieldDef(label="反馈方案")
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getFeedbackContent() {
        return feedbackContent;
    }

    /**
     * 设置反馈方案
     *
     * @param feedbackContent 反馈方案
     */
    public void setFeedbackContent(String feedbackContent) {
        this.feedbackContent = feedbackContent;
    }

	public Integer getImperative() {
		return imperative;
	}

	public void setImperative(Integer imperative) {
		this.imperative = imperative;
	}
    
    
}