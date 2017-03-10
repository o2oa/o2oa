package com.x.bbs.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

/**
 * 系统配置信息管理实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.BBSConfigSetting.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSConfigSetting extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSConfigSetting.table;

	/**
	 * 获取记录ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置记录ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取信息创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 设置信息创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取信息更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 设置信息更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * 获取信息记录排序号
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * 设置信息记录排序号
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	@EntityFieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(name = "xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe("创建时间,自动生成.")
	@Index(name = TABLE + "_createTime")
	@Column(name = "xcreateTime")
	private Date createTime;

	@EntityFieldDescribe("修改时间,自动生成.")
	@Index(name = TABLE + "_updateTime")
	@Column(name = "xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe("列表序号, 由创建时间以及ID组成.在保存时自动生成.")
	@Column(name = "xsequence", length = AbstractPersistenceProperties.length_sequence)
	@Index(name = TABLE + "_sequence")
	private String sequence;

	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
	@PrePersist
	public void prePersist() throws Exception {
		
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;

		// 序列号信息的组成，与排序有关
		if (null == this.sequence) {
			this.sequence = StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId());
		}
		this.onPersist();
	}

	/**
	 * 在对实体数据进行数据库更新操作之前，调用实体的 @PreUpdate 回调方法。
	 */
	@PreUpdate
	public void preUpdate() throws Exception {
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() throws Exception {
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

	@EntityFieldDescribe("系统配置名称")
	@Column(name = "xconfigName", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String configName = null;

	@EntityFieldDescribe("系统配置编码")
	@Column(name = "xconfigCode", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = false)
	private String configCode = null;

	@EntityFieldDescribe("配置值")
	@Column(name = "xconfigValue", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String configValue = null;

	@EntityFieldDescribe("值类型: select | identity | number | date | text")
	@Column(name = "xvalueType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String valueType = null;

	@EntityFieldDescribe("可选值，和select配合使用，以‘|’号分隔")
	@Column(name = "xselectContent", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String selectContent = "--无--";

	@EntityFieldDescribe("是否可以多值")
	@Column(name = "xisMultiple")
	@CheckPersist(allowEmpty = true)
	private Boolean isMultiple = false;

	@EntityFieldDescribe("排序号")
	@Column(name = "xorderNumber")
	@CheckPersist(allowEmpty = true)
	private Integer orderNumber = 1;

	@EntityFieldDescribe("备注说明")
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