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
@Table(name = PersistenceProperties.AttendanceScheduleSetting.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceScheduleSetting extends SliceJpaObject {

	private static final long serialVersionUID = 4555094494086574586L;
	private static final String TABLE = PersistenceProperties.AttendanceScheduleSetting.table;

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
	@EntityFieldDescribe("公司名称")
	@Column(name="xcompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String companyName;
	
	@EntityFieldDescribe("部门名称")
	@Column(name="xorganizationName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String organizationName;

	@EntityFieldDescribe("部门编号")
	@Column(name="xorganizationOu", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String organizationOu;
	
	@EntityFieldDescribe("上班时间")
	@Column(name="xonDutyTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String onDutyTime;
	
	@EntityFieldDescribe("下班时间")
	@Column(name="xoffDutyTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String offDutyTime;
	
	@EntityFieldDescribe("迟到起算时间")
	@Column(name="xlateStartTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String lateStartTime;
	
	@EntityFieldDescribe("缺勤起算时间")
	@Column(name="xabsenceStartTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String absenceStartTime;
	
	@EntityFieldDescribe("早退起算时间")
	@Column(name="xleaveEarlyStartTime", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true )
	private String leaveEarlyStartTime;

	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getOrganizationOu() {
		return organizationOu;
	}
	public void setOrganizationOu(String organizationOu) {
		this.organizationOu = organizationOu;
	}
	public String getOnDutyTime() {
		return onDutyTime;
	}
	public void setOnDutyTime(String onDutyTime) {
		this.onDutyTime = onDutyTime;
	}
	public String getOffDutyTime() {
		return offDutyTime;
	}
	public void setOffDutyTime(String offDutyTime) {
		this.offDutyTime = offDutyTime;
	}
	public String getLateStartTime() {
		return lateStartTime;
	}
	public void setLateStartTime(String lateStartTime) {
		this.lateStartTime = lateStartTime;
	}
	public String getAbsenceStartTime() {
		return absenceStartTime;
	}
	public void setAbsenceStartTime(String absenceStartTime) {
		this.absenceStartTime = absenceStartTime;
	}
	public String getLeaveEarlyStartTime() {
		return leaveEarlyStartTime;
	}
	public void setLeaveEarlyStartTime(String leaveEarlyStartTime) {
		this.leaveEarlyStartTime = leaveEarlyStartTime;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
}