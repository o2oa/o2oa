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
@Table(name = PersistenceProperties.AttendanceWorkPlace.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceWorkPlace extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AttendanceWorkPlace.table;

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
	public void prePersist() throws Exception { 
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
	public void preUpdate() throws Exception{
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() throws Exception{
	}
	/* ==================================================================================
	 *                             以上为 JpaObject 默认字段
	 * ================================================================================== */
	
	
	/* ==================================================================================
	 *                             以下为具体不同的业务及数据表字段要求
	 * ================================================================================== */
	@EntityFieldDescribe("场所名称")
	@Column(name="xplaceName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true)
	private String placeName = "";

	@EntityFieldDescribe("场所别名")
	@Column(name="xplaceAlias", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist( allowEmpty = true )
	private String placeAlias = "";
	
	@EntityFieldDescribe("创建人")
	@Column(name="xcreator", length = AbstractPersistenceProperties.organization_name_length )
	@CheckPersist( allowEmpty = false )
	private String creator = "";	

	@EntityFieldDescribe("经度")
	@Column(name="xlongitude", length = JpaObject.length_32B)
	@CheckPersist( allowEmpty = true )
	private String longitude = null;
	
	@EntityFieldDescribe("纬度")
	@Column(name="xlatitude", length = JpaObject.length_32B)
	@CheckPersist( allowEmpty = true )
	private String latitude = null;
	
	@EntityFieldDescribe("误差范围")
	@Column(name="xerrorRange" )
	@CheckPersist( allowEmpty = true )
	private Integer errorRange = 200;
	 
	@EntityFieldDescribe("说明备注")
	@Column(name="xdescription", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = true )
	private String description = null;

	public String getPlaceName() {
		return placeName;
	}
	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}
	public String getPlaceAlias() {
		return placeAlias;
	}
	public void setPlaceAlias(String placeAlias) {
		this.placeAlias = placeAlias;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getErrorRange() {
		return errorRange;
	}
	public void setErrorRange(Integer errorRange) {
		this.errorRange = errorRange;
	}
	
}