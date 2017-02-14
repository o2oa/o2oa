package com.x.attendance.entity;

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

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceWorkDayConfig.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceWorkDayConfig extends SliceJpaObject {

	private static final long serialVersionUID = -7226058083321275722L;
	private static final String TABLE = PersistenceProperties.AttendanceWorkDayConfig.table;

	/**
	 * 获取明细记录ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置明细记录ID
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

	@EntityFieldDescribe( "数据库主键,自动生成." )
	@Id
	@Column( name="xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe( "创建时间,自动生成." )
	@Index(name = TABLE + "_createTime" )
	@Column( name="xcreateTime")
	private Date createTime;

	@EntityFieldDescribe( "修改时间,自动生成." )
	@Index(name = TABLE + "_updateTime" )
	@Column( name="xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe( "列表序号, 由创建时间以及ID组成.在保存时自动生成." )
	@Column( name="xsequence", length = AbstractPersistenceProperties.length_sequence )
	@Index(name = TABLE + "_sequence" )
	private String sequence;
	
	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
	@PrePersist
	public void prePersist() {
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;
		if (null == this.sequence) {
			this.sequence = StringUtils.join( DateTools.compact(this.getCreateTime()), this.getId() );
		}
		this.onPersist();
	}
	/**
	 * 在对实体数据进行数据库更新操作之前，调用实体的 @PreUpdate 回调方法。
	 */
	@PreUpdate
	public void preUpdate() {
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() {
	}
	/* ==================================================================================
	 *                             以上为 JpaObject 默认字段
	 * ================================================================================== */
	
	
	/* ==================================================================================
	 *                             以下为具体不同的业务及数据表字段要求
	 * ================================================================================== */
	@EntityFieldDescribe("配置项名称")
	@Column(name="xconfigName", length = JpaObject.length_96B )
	@CheckPersist( simplyString = true, allowEmpty = false )
	private String configName = "";
	
	@EntityFieldDescribe("配置年份")
	@Column(name="xconfigYear", length = JpaObject.length_16B )
	@CheckPersist( simplyString = true, allowEmpty = false )
	private String configYear = "2016";
	
	@EntityFieldDescribe("配置月份")
	@Column(name="xconfigMonth", length = JpaObject.length_16B )
	@CheckPersist( simplyString = true, allowEmpty = false )
	private String configMonth = "";
	
	@EntityFieldDescribe("配置日期")
	@Column(name="xconfigDate", length = JpaObject.length_32B)
	@CheckPersist( allowEmpty = false)
	private String configDate = "";

	@EntityFieldDescribe("配置类型：Holiday|Workday")
	@Column(name="xconfigType", length = JpaObject.length_16B)
	@CheckPersist(simplyString = true, allowEmpty = false )
	private String configType = "Holiday";	
	
	@EntityFieldDescribe("配置说明")
	@Column(name="xdescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String description = "";

	public String getConfigDate() {
		return configDate;
	}
	public void setConfigDate(String configDate) {
		this.configDate = configDate;
	}
	public String getConfigType() {
		return configType;
	}
	public void setConfigType(String configType) {
		this.configType = configType;
	}
	
	public String getConfigName() {
		return configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getConfigYear() {
		return configYear;
	}
	public void setConfigYear(String configYear) {
		this.configYear = configYear;
	}
	public String getConfigMonth() {
		return configMonth;
	}
	public void setConfigMonth(String configMonth) {
		this.configMonth = configMonth;
	}

	
}