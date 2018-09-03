package com.x.okr.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 系统配置信息管理实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrConfigSystem.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrConfigSystem.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrConfigSystem extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrConfigSystem.table;

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

	@FieldDescribe("系统配置名称")
	@Column(name = "xconfigName", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String configName = null;

	@FieldDescribe("系统配置编码")
	@Column(name = "xconfigCode", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = false)
	private String configCode = null;

	@FieldDescribe("配置值")
	@Column(name = "xconfigValue", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String configValue = null;

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

	@FieldDescribe("排序号")
	@Column(name = "xorderNumber")
	@CheckPersist(allowEmpty = true)
	private Integer orderNumber = 1;

	@FieldDescribe("备注说明")
	@Column(name = "xdescription", length = JpaObject.length_255B)
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