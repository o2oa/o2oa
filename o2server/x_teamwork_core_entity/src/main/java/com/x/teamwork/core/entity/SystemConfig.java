package com.x.teamwork.core.entity;

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
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.SystemConfig.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.SystemConfig.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class SystemConfig extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.SystemConfig.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	public void onPersist() throws Exception {
	}
	/*
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */

	/*
	 * =============================================================================
	 * ===== 以下为具体不同的业务及数据表字段要求
	 * =============================================================================
	 * =====
	 */
	@Flag
	@FieldDescribe("配置编码")
	@Index(name = TABLE + "_configCode")
	@Column(name = "xconfigCode", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = false)
	private String configCode;

	@Flag
	@FieldDescribe("配置名称")
	@Column(name = "xconfigName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = false)
	private String configName;

	@FieldDescribe("配置内容")
	@Column(name = "xconfigValue", length = JpaObject.length_255B)
	private String configValue;

	@FieldDescribe("值类型: select | identity | number | date | text")
	@Column(name = "xvalueType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String valueType = null;

	@FieldDescribe("可选值，和select配合使用，以‘|’号分隔")
	@Column(name = "xselectContent", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String selectContent = "--无--";

	@FieldDescribe("是否可以多值")
	@Column(name = "xisMultiple")
	@CheckPersist(allowEmpty = true)
	private Boolean isMultiple = false;

	@FieldDescribe("是否富文本、长文本")
	@Column(name = "xisLob")
	private Boolean isLob = false;

	@FieldDescribe("排序号")
	@Column(name = "xorderNumber")
	@CheckPersist(allowEmpty = true)
	private Integer orderNumber = 1;

	@FieldDescribe("备注说明")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description = null;

	public String getConfigCode() {
		return configCode;
	}

	public void setConfigCode(String configCode) {
		this.configCode = configCode;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
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

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsLob() {
		return isLob;
	}

	public void setIsLob(Boolean isLob) {
		this.isLob = isLob;
	}
}