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
@Table(name = PersistenceProperties.AttendanceStatisticalCycle.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceStatisticalCycle extends SliceJpaObject {

	private static final long serialVersionUID = 3253441204302907992L;
	private static final String TABLE = PersistenceProperties.AttendanceStatisticalCycle.table;

	public AttendanceStatisticalCycle(){}
	
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
	@EntityFieldDescribe("公司名称")
	@Column(name="xcompanyName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String companyName;
	
	@EntityFieldDescribe("部门名称")
	@Column(name="xdepartmentName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true )
	private String departmentName;
	
	@EntityFieldDescribe("统计周期年份")
	@Column(name="xcycleYear", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String cycleYear;
	
	@EntityFieldDescribe("统计周期月份")
	@Column(name="xcycleMonth", length = JpaObject.length_16B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String cycleMonth;
	
	@EntityFieldDescribe("月周期开始日期")
	@Column(name="xcycleStartDateString", length = JpaObject.length_32B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String cycleStartDateString;
	
	@EntityFieldDescribe("月周期结束日期")
	@Column(name="xcycleEndDateString", length = JpaObject.length_32B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String cycleEndDateString;
	
	@EntityFieldDescribe("月周期开始日期")
	@Column(name="xcycleStartDate")
	@CheckPersist(allowEmpty = true)
	private Date cycleStartDate;
	
	@EntityFieldDescribe("月周期结束日期")
	@Column(name="xcycleEndDate")
	@CheckPersist(allowEmpty = true)
	private Date cycleEndDate;

	@EntityFieldDescribe("说明备注")
	@Column(name="xdescription", length = JpaObject.length_255B)
	@CheckPersist(simplyString = true, allowEmpty = true )
	private String description;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getCycleYear() {
		return cycleYear;
	}

	public void setCycleYear(String cycleYear) {
		this.cycleYear = cycleYear;
	}

	public String getCycleMonth() {
		return cycleMonth;
	}

	public void setCycleMonth(String cycleMonth) {
		this.cycleMonth = cycleMonth;
	}

	public String getCycleStartDateString() {
		return cycleStartDateString;
	}

	public void setCycleStartDateString(String cycleStartDateString) {
		this.cycleStartDateString = cycleStartDateString;
	}

	public String getCycleEndDateString() {
		return cycleEndDateString;
	}

	public void setCycleEndDateString(String cycleEndDateString) {
		this.cycleEndDateString = cycleEndDateString;
	}

	public Date getCycleStartDate() {
		if( cycleStartDate == null){
			if( cycleStartDateString != null ){
				try {
					return (new DateOperation()).getDateFromString(cycleEndDateString);
				} catch (Exception e) {
					return null;
				}
			}
		}
		return cycleStartDate;
	}

	public void setCycleStartDate(Date cycleStartDate) {
		this.cycleStartDate = cycleStartDate;
	}

	public Date getCycleEndDate() {
		if( cycleEndDate == null){
			if( cycleEndDateString != null ){
				try {
					return (new DateOperation()).getDateFromString(cycleEndDateString);
				} catch (Exception e) {
					return null;
				}
			}
		}
		return cycleEndDate;
	}

	public void setCycleEndDate(Date cycleEndDate) {
		this.cycleEndDate = cycleEndDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
}