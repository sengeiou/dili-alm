package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;

/**
 * ��MyBatis Generator�����Զ�����
 * 
 * This file was generated on 2019-12-24 10:20:34.
 */
@Table(name = "`demand`")
public class Demand extends BaseDomain {
    /**
     * �������
     */
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * �����ţ���������ĸ+������+ʱ��+001��������
     */
    @Column(name = "`serial_number`")
    private String serialNumber;

    /**
     * ��������
     */
    @Column(name = "`name`")
    private String name;

    /**
     * ����������
     */
    @Column(name = "`user_id`")
    private Long userId;

    /**
     * ����ϵͳ
     */
    @Column(name = "`belong_pro_id`")
    private Long belongProId;

    /**
     * �������ͣ������ֵ䣩
     */
    @Column(name = "`type`")
    private Byte type;

    /**
     * ����״̬�������ֵ䣩
     */
    @Column(name = "`status`")
    private Byte status;

    /**
     * ��������ʵ��ʱ��(������)
     */
    @Column(name = "`finish_date`")
    private Date finishDate;

    /**
     * ��ظ���
     */
    @Column(name = "`document_url`")
    private String documentUrl;

    /**
     * ����ʱ��
     */
    @Column(name = "`create_date`")
    private Date createDate;

    /**
     * �ύʱ�䣨�������̣�
     */
    @Column(name = "`submit_date`")
    private Date submitDate;

    /**
     * ��������
     */
    @Column(name = "`content`")
    private String content;

    /**
     * ���󱳾�
     */
    @Column(name = "`reason`")
    private String reason;

    /**
     * ��ȡ�������
     *
     * @return id - �������
     */
    @FieldDef(label="�������")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getId() {
        return id;
    }

    /**
     * �����������
     *
     * @param id �������
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * ��ȡ�����ţ���������ĸ+������+ʱ��+001��������
     *
     * @return serial_number - �����ţ���������ĸ+������+ʱ��+001��������
     */
    @FieldDef(label="�����ţ���������ĸ+������+ʱ��+001��������", maxLength = 25)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * ���������ţ���������ĸ+������+ʱ��+001��������
     *
     * @param serialNumber �����ţ���������ĸ+������+ʱ��+001��������
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * ��ȡ��������
     *
     * @return name - ��������
     */
    @FieldDef(label="��������", maxLength = 25)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getName() {
        return name;
    }

    /**
     * ������������
     *
     * @param name ��������
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * ��ȡ����������
     *
     * @return user_id - ����������
     */
    @FieldDef(label="����������")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getUserId() {
        return userId;
    }

    /**
     * ��������������
     *
     * @param userId ����������
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * ��ȡ����ϵͳ
     *
     * @return belong_pro_id - ����ϵͳ
     */
    @FieldDef(label="����ϵͳ")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getBelongProId() {
        return belongProId;
    }

    /**
     * ��������ϵͳ
     *
     * @param belongProId ����ϵͳ
     */
    public void setBelongProId(Long belongProId) {
        this.belongProId = belongProId;
    }

    /**
     * ��ȡ�������ͣ������ֵ䣩
     *
     * @return type - �������ͣ������ֵ䣩
     */
    @FieldDef(label="�������ͣ������ֵ䣩")
    @EditMode(editor = FieldEditor.Text, required = true)
    public Byte getType() {
        return type;
    }

    /**
     * �����������ͣ������ֵ䣩
     *
     * @param type �������ͣ������ֵ䣩
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * ��ȡ����״̬�������ֵ䣩
     *
     * @return status - ����״̬�������ֵ䣩
     */
    @FieldDef(label="����״̬�������ֵ䣩")
    @EditMode(editor = FieldEditor.Text, required = true)
    public Byte getStatus() {
        return status;
    }

    /**
     * ��������״̬�������ֵ䣩
     *
     * @param status ����״̬�������ֵ䣩
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * ��ȡ��������ʵ��ʱ��(������)
     *
     * @return finish_date - ��������ʵ��ʱ��(������)
     */
    @FieldDef(label="��������ʵ��ʱ��(������)")
    @EditMode(editor = FieldEditor.Date, required = true)
    public Date getFinishDate() {
        return finishDate;
    }

    /**
     * ������������ʵ��ʱ��(������)
     *
     * @param finishDate ��������ʵ��ʱ��(������)
     */
    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    /**
     * ��ȡ��ظ���
     *
     * @return document_url - ��ظ���
     */
    @FieldDef(label="��ظ���", maxLength = 120)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getDocumentUrl() {
        return documentUrl;
    }

    /**
     * ������ظ���
     *
     * @param documentUrl ��ظ���
     */
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    /**
     * ��ȡ����ʱ��
     *
     * @return create_date - ����ʱ��
     */
    @FieldDef(label="����ʱ��")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * ���ô���ʱ��
     *
     * @param createDate ����ʱ��
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * ��ȡ�ύʱ�䣨�������̣�
     *
     * @return submit_date - �ύʱ�䣨�������̣�
     */
    @FieldDef(label="�ύʱ�䣨�������̣�")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Date getSubmitDate() {
        return submitDate;
    }

    /**
     * �����ύʱ�䣨�������̣�
     *
     * @param submitDate �ύʱ�䣨�������̣�
     */
    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    /**
     * ��ȡ��������
     *
     * @return content - ��������
     */
    @FieldDef(label="��������")
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getContent() {
        return content;
    }

    /**
     * ������������
     *
     * @param content ��������
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * ��ȡ���󱳾�
     *
     * @return reason - ���󱳾�
     */
    @FieldDef(label="���󱳾�")
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getReason() {
        return reason;
    }

    /**
     * �������󱳾�
     *
     * @param reason ���󱳾�
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
}