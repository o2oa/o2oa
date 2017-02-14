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

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceDetailMobile.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceDetailMobile extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AttendanceDetailMobile.table;

	public AttendanceDetailMobile(){}
	
	/**
	 * 获取明细记录ID
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置明细记录ID
	 */
	public void setId( String id ) {
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
		if ( null == this.createTime ) {
			this.createTime = date;
		}
		this.updateTime = date;
		if (null == this.sequence) {
			this.sequence = StringUtils.join( this.empName, this.recordDateString, this.getId() );
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
	@EntityFieldDescribe("员工号")
	@Column(name="xempNo", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String empNo;

	@EntityFieldDescribe("员工姓名")
	@Column(name="xempName", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String empName;
	
	@EntityFieldDescribe("打卡记录日期字符串")
	@Column(name="xrecordDateString", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String recordDateString;
	
	@EntityFieldDescribe("打卡记录日期")
	@Column(name="xrecordDate" )
	@CheckPersist( allowEmpty = true )
	private Date recordDate;
	
	@EntityFieldDescribe("打卡时间")
	@Column(name="xsignTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String signTime;
	
	@EntityFieldDescribe("打卡说明")
	@Column(name="xsignDescription", length = JpaObject.length_128B )
	@CheckPersist( allowEmpty = true )
	private String signDescription;
	
	@EntityFieldDescribe("其他说明备注")
	@Column(name="xdescription", length = JpaObject.length_255B)
	@CheckPersist( allowEmpty = true )
	private String description;
	
	@EntityFieldDescribe("打卡地点描述")
	@Column(name="xrecordAddress", length = JpaObject.length_255B)
	@CheckPersist( allowEmpty = true )
	private String recordAddress;
	
	@EntityFieldDescribe("经度")
	@Column(name="xlongitude", length = JpaObject.length_32B)
	@CheckPersist( allowEmpty = true )
	private String longitude;
	
	@EntityFieldDescribe("纬度")
	@Column(name="xlatitude", length = JpaObject.length_32B)
	@CheckPersist( allowEmpty = true )
	private String latitude;
	
	@EntityFieldDescribe("操作设备类别：手机品牌|PAD|PC|其他")
	@Column(name="xoptMachineType", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String optMachineType;
	
	@EntityFieldDescribe("操作设备类别：Mac|Windows|IOS|Android|其他")
	@Column(name="xoptSystemName", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String optSystemName;
	
	@EntityFieldDescribe("记录状态：0-未分析 1-已分析")
	@Column(name="xrecordStatus")
	private Integer recordStatus = 0;

	public String getEmpNo() {
		return empNo;
	}

	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getRecordDateString() {
		return recordDateString;
	}

	public void setRecordDateString(String recordDateString) {
		this.recordDateString = recordDateString;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public String getSignTime() {
		return signTime;
	}

	public void setSignTime(String signTime) {
		this.signTime = signTime;
	}

	public String getSignDescription() {
		return signDescription;
	}

	public void setSignDescription(String signDescription) {
		this.signDescription = signDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRecordAddress() {
		return recordAddress;
	}

	public void setRecordAddress(String recordAddress) {
		this.recordAddress = recordAddress;
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

	public String getOptMachineType() {
		return optMachineType;
	}

	public void setOptMachineType(String optMachineType) {
		this.optMachineType = optMachineType;
	}

	public String getOptSystemName() {
		return optSystemName;
	}

	public void setOptSystemName(String optSystemName) {
		this.optSystemName = optSystemName;
	}

	public Integer getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(Integer recordStatus) {
		this.recordStatus = recordStatus;
	}
	
}