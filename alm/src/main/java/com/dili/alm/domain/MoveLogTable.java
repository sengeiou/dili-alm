package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2019-12-27 16:48:24.
 */
@Table(name = "`move_log_table`")
public class MoveLogTable extends BaseDomain {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`table_name`")
    private String tableName;

    @Column(name = "`file_id`")
    private Long fileId;

    @Column(name = "`file_field`")
    private String fileField;

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
     * @return table_name
     */
    @FieldDef(label="tableName", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return file_id
     */
    @FieldDef(label="fileId")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getFileId() {
        return fileId;
    }

    /**
     * @param fileId
     */
    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    /**
     * @return file_field
     */
    @FieldDef(label="fileField", maxLength = 60)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getFileField() {
        return fileField;
    }

    /**
     * @param fileField
     */
    public void setFileField(String fileField) {
        this.fileField = fileField;
    }
}