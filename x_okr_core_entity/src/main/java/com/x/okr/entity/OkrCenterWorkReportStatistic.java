package com.x.okr.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
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
 * 中心工作汇报情况统计信息实体类
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrCenterWorkReportStatistic.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrCenterWorkReportStatistic extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrCenterWorkReportStatistic.table;
	private Integer workCount = 0;
	
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

	@EntityFieldDescribe( "数据库主键,自动生成." )
	@Id
	@Column( name="xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe( "创建时间,自动生成." )
	@Index(name = TABLE + "_createTime" )
	@Column( name="xcreateTime" )
	private Date createTime;

	@EntityFieldDescribe( "修改时间,自动生成." )
	@Index(name = TABLE + "_updateTime" )
	@Column( name="xupdateTime" )
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
		
		//序列号信息的组成，与排序有关
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
	@EntityFieldDescribe( "中心工作ID" )
	@Column( name="xcenterId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = true)
	private String centerId = "";
	
	@EntityFieldDescribe( "中心标题" )
	@Column( name="xcenterTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false)
	private String centerTitle = "";
	
	@EntityFieldDescribe( "中心工作默认工作类别" )
	@Column( name="xdefaultWorkType", length = JpaObject.length_32B )
	@CheckPersist(allowEmpty = true )
	private String defaultWorkType = "";
	
	@EntityFieldDescribe( "中心工作默认工作级别" )
	@Column( name="xdefaultWorkLevel", length = JpaObject.length_32B )
	@CheckPersist(allowEmpty = true )
	private String defaultWorkLevel = "";
	
	@EntityFieldDescribe( "统计周期：每周统计|每月统计" )
	@Column( name="xstatisticCycle", length = JpaObject.length_32B )
	@CheckPersist( allowEmpty = true)
	private String statisticCycle = "";

	@EntityFieldDescribe( "统计时间." )
	@Column( name="xstatisticTime" )
	@CheckPersist( allowEmpty = true)
	private Date statisticTime = null;

	@EntityFieldDescribe( "统计年份" )
	@Column( name="xyear" )
	@CheckPersist( allowEmpty = true)
	private Integer year = 0;
	
	@EntityFieldDescribe( "统计月份" )
	@Column( name="xmonth" )
	@CheckPersist( allowEmpty = true)
	private Integer month = null;
	
	@EntityFieldDescribe( "统计周数" )
	@Column( name="xweek" )
	@CheckPersist( allowEmpty = true)
	private Integer week = null;
	
	@EntityFieldDescribe( "处理状态：正常|已删除" )
	@Column( name="xstatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = true )
	private String status = "正常";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "中心工作汇报统计内容" )
	@Column( name="xreportStatistic", length = JpaObject.length_10M )
	@CheckPersist( allowEmpty = true )
	private String reportStatistic = "";

	public String getCenterId() {
		return centerId;
	}
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReportStatistic() {
		return reportStatistic;
	}
	public void setReportStatistic(String reportStatistic) {
		this.reportStatistic = reportStatistic;
	}
	public Date getStatisticTime() {
		return statisticTime;
	}
	public void setStatisticTime(Date statisticTime) {
		this.statisticTime = statisticTime;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Integer getMonth() {
		return month;
	}
	public void setMonth(Integer month) {
		this.month = month;
	}
	public Integer getWeek() {
		return week;
	}
	public void setWeek(Integer week) {
		this.week = week;
	}
	public String getStatisticCycle() {
		return statisticCycle;
	}
	public void setStatisticCycle(String statisticCycle) {
		this.statisticCycle = statisticCycle;
	}
	public String getDefaultWorkType() {
		return defaultWorkType;
	}
	public void setDefaultWorkType(String defaultWorkType) {
		this.defaultWorkType = defaultWorkType;
	}
	public String getDefaultWorkLevel() {
		return defaultWorkLevel;
	}
	public void setDefaultWorkLevel(String defaultWorkLevel) {
		this.defaultWorkLevel = defaultWorkLevel;
	}
	public String getCenterTitle() {
		return centerTitle;
	}
	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}
	public Integer getWorkCount() {
		return workCount;
	}
	public void setWorkCount(Integer workCount) {
		this.workCount = workCount;
	}
}