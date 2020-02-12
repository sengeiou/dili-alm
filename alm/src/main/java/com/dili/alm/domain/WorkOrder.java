package com.dili.alm.domain;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-08-27 15:34:56.
 */
@Table(name = "`work_order`")
public interface WorkOrder extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`serial_number`")
    @FieldDef(label="工单编号", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = true)
    String getSerialNumber();

    void setSerialNumber(String serialNumber);

    @Column(name = "`work_order_name`")
    @FieldDef(label="工单名称", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getWorkOrderName();

    void setWorkOrderName(String workOrderName);

    @Column(name = "`work_order_type`")
    @FieldDef(label="工单类型")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getWorkOrderType();

    void setWorkOrderType(Integer workOrderType);

    @Column(name = "`priority`")
    @FieldDef(label="工单优先级")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getPriority();

    void setPriority(Integer priority);

    @Column(name = "`channel`")
    @FieldDef(label="渠道", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getChannel();

    void setChannel(String channel);

    @Column(name = "`acceptor_id`")
    @FieldDef(label="受理人")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getAcceptorId();

    void setAcceptorId(Long acceptorId);

    @Column(name = "`copy_user_id`")
    @FieldDef(label="抄送人id,jsonarray存储", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getCopyUserId();

    void setCopyUserId(String copyUserId);

    @Column(name = "`description`")
    @FieldDef(label="描述", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getDescription();

    void setDescription(String description);

    @Column(name = "`attachment_file_id`")
    @FieldDef(label="附件id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getAttachmentFileId();

    void setAttachmentFileId(Long attachmentFileId);

    @Column(name = "`work_order_state`")
    @FieldDef(label="工单申请状态")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getWorkOrderState();

    void setWorkOrderState(Integer workOrderState);

    @Column(name = "`submit_time`")
    @FieldDef(label="申请提交时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getSubmitTime();

    void setSubmitTime(Date submitTime);

    @Column(name = "`close_time`")
    @FieldDef(label="关闭时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getCloseTime();

    void setCloseTime(Date closeTime);

    @Column(name = "`creation_time`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    Date getCreationTime();

    void setCreationTime(Date creationTime);

    @Column(name = "`modify_time`")
    @FieldDef(label="修改时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getModifyTime();

    void setModifyTime(Date modifyTime);

    @Column(name = "`applicant_id`")
    @FieldDef(label="申请人id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getApplicantId();

    void setApplicantId(Long applicantId);

    @Column(name = "`executor_id`")
    @FieldDef(label="执行人id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getExecutorId();

    void setExecutorId(Long executorId);

    @Column(name = "`work_order_source`")
    @FieldDef(label="工单来源")
    @EditMode(editor = FieldEditor.Number, required = true)
    Integer getWorkOrderSource();

    void setWorkOrderSource(Integer workOrderSource);
    
    
/*    
    @Column(name = "`process_instance_id`")
    @FieldDef(label="流程实例id", maxLength = 80)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getProcessInstanceId();

    void setProcessInstanceId(String processInstanceId);*/
    
 
}