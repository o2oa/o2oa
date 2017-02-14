package com.x.attendance.entity;

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

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceStatisticRequireLog.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceStatisticRequireLog extends SliceJpaObject {

	private static final long serialVersionUID = 7681675551507448290L;
	private static final String TABLE = PersistenceProperties.AttendanceStatisticRequireLog.table;

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
	@EntityFieldDescribe("统计名称")
	@Column(name="xstatisticName", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = false)
	private String statisticName = "";

	@EntityFieldDescribe("统计类型:PERSON_PER_MONTH|DEPARTMENT_PER_MONTH|COMPANY_PER_MONTH|DEPARTMENT_PER_DAY|COMPANY_PER_DAY")
	@Column(name="xstatisticType", length = JpaObject.length_96B )
	@CheckPersist(simplyString = true, allowEmpty = false )
	private String statisticType = "";
	
	@EntityFieldDescribe("统计键值")
	@Column(name="xstatisticKey", length = JpaObject.length_96B )
	@CheckPersist( simplyString = true, allowEmpty = false )
	private String statisticKey = "";
	
	@EntityFieldDescribe("统计年月")
	@Column( name="xstatisticYear", length = JpaObject.length_96B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String statisticYear = "";
	
	@EntityFieldDescribe("统计月份")
	@Column(  name="xstatisticMonth",length = JpaObject.length_96B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String statisticMonth = "";
	
	@EntityFieldDescribe("统计日期")
	@Column( name="xstatisticDay", length = JpaObject.length_96B )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String statisticDay = "";
	
	@EntityFieldDescribe("处理时间")
	@Column(name="xprocessTime")
	@CheckPersist(simplyString = true, allowEmpty = true)
	private Date processTime = null;
	
	@EntityFieldDescribe("处理状态")
	@Column(name="xprocessStatus")
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String processStatus = "WAITING";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe("说明备注")
	@Column(name="xdescription", length = JpaObject.length_2K )
	@CheckPersist( simplyString = true, allowEmpty = true )
	private String description = "";

	public String getStatisticName() {
		return statisticName;
	}
	public void setStatisticName(String statisticName) {
		this.statisticName = statisticName;
	}
	/**
	 * 统计类型:PERSON_PER_MONTH|DEPARTMENT_PER_MONTH|COMPANY_PER_MONTH|DEPARTMENT_PER_DAY|COMPANY_PER_DAY
	 * @return
	 */
	public String getStatisticType() {
		return statisticType;
	}
	/**
	 * 统计类型:PERSON_PER_MONTH|DEPARTMENT_PER_MONTH|COMPANY_PER_MONTH|DEPARTMENT_PER_DAY|COMPANY_PER_DAY
	 * @param statisticType
	 */
	public void setStatisticType(String statisticType) {
		this.statisticType = statisticType;
	}
	public String getStatisticKey() {
		return statisticKey;
	}
	public void setStatisticKey(String statisticKey) {
		this.statisticKey = statisticKey;
	}
	public Date getProcessTime() {
		return processTime;
	}
	public void setProcessTime(Date processTime) {
		this.processTime = processTime;
	}
	/**
	 * WAITING|PROCESSING|COMPLETE|ERROR
	 * @return
	 */
	public String getProcessStatus() {
		return processStatus;
	}
	/**
	 * WAITING|PROCESSING|COMPLETE|ERROR
	 * @param processStatus
	 */
	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatisticYear() {
		return statisticYear;
	}
	public void setStatisticYear(String statisticYear) {
		this.statisticYear = statisticYear;
	}
	public String getStatisticMonth() {
		return statisticMonth;
	}
	public void setStatisticMonth(String statisticMonth) {
		this.statisticMonth = statisticMonth;
	}
	public String getStatisticDay() {
		return statisticDay;
	}
	public void setStatisticDay(String statisticDay) {
		this.statisticDay = statisticDay;
	}
}