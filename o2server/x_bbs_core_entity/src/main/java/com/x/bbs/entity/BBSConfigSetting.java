package com.x.bbs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BBSConfigSetting", description = "论坛配置.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSConfigSetting.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.BBSConfigSetting.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSConfigSetting extends SliceJpaObject {

    public static final String BBS_CONFIG_USE_NICKNAME = "BBS_USE_NICKNAME";

    public static final String BBS_CONFIG_USE_NICKNAME_YES = "YES";

    private static final long serialVersionUID = 3856138316794473794L;
    private static final String TABLE = PersistenceProperties.BBSConfigSetting.table;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @FieldDescribe("数据库主键,自动生成.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    @Override
    public void onPersist() throws Exception {
        // nothing
    }
    /*
     * =========================================================================
     * ========= 以上为 JpaObject 默认字段
     * =========================================================================
     * =========
     */

    /*
     * =========================================================================
     * ========= 以下为具体不同的业务及数据表字段要求
     * =========================================================================
     * =========
     */

    public static final String configName_FIELDNAME = "configName";
    @FieldDescribe("系统配置名称")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + configName_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String configName = null;

    public static final String configCode_FIELDNAME = "configCode";
    @FieldDescribe("系统配置编码")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + configCode_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + configCode_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String configCode = null;

    public static final String configValue_FIELDNAME = "configValue";
    @FieldDescribe("配置值")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + configValue_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String configValue = null;

    public static final String valueType_FIELDNAME = "valueType";
    @FieldDescribe("值类型: select | identity | number | date | text")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + valueType_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String valueType = null;

    public static final String selectContent_FIELDNAME = "selectContent";
    @FieldDescribe("可选值，和select配合使用，以‘|’号分隔")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + selectContent_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String selectContent = "--无--";

    public static final String isMultiple_FIELDNAME = "isMultiple";
    @FieldDescribe("是否可以多值")
    @Column(name = ColumnNamePrefix + isMultiple_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private Boolean isMultiple = false;

    public static final String orderNumber_FIELDNAME = "orderNumber";
    @FieldDescribe("排序号")
    @Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private Integer orderNumber = 1;

    public static final String description_FIELDNAME = "description";
    @FieldDescribe("备注说明")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + description_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String description = null;

    /**
     * 获取配置名称
     *
     * @return
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * 设置配置名称
     *
     * @param configName
     */
    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     * 获取配置编码
     *
     * @return
     */
    public String getConfigCode() {
        return configCode;
    }

    /**
     * 设置配置编码
     *
     * @param configCode
     */
    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    /**
     * 获取配置值
     *
     * @return
     */
    public String getConfigValue() {
        return configValue;
    }

    /**
     * 设置配置值
     *
     * @param configValue
     */
    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    /**
     * 获取排序号
     *
     * @return
     */
    public Integer getOrderNumber() {
        return orderNumber;
    }

    /**
     * 设置排序号
     *
     * @param orderNumber
     */
    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * 获取备注说明信息
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置备注说明信息
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getSelectContent() {
        return selectContent;
    }

    public void setSelectContent(String selectContent) {
        this.selectContent = selectContent;
    }

    public Boolean getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(Boolean isMultiple) {
        this.isMultiple = isMultiple;
    }

}
