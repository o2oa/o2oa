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
 * 工作详细描述信息管理实体类
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkDetailInfo.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkDetailInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkDetailInfo.table;

	/**
	 * 获取工作详细描述信息ID：与工作信息的ID一致
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置工作详细描述信息ID：与工作信息的ID一致
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
	@CheckPersist( allowEmpty = false)
	private String centerId = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "工作详细描述, 事项分解" )
	@Column(name="xworkDetail", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String workDetail = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "职责描述" )
	@Column(name="xdutyDescription", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String dutyDescription = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "具体行动举措" )
	@Column(name="xprogressAction", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String progressAction = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "里程碑标志说明" )
	@Column(name="xlandmarkDescription", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String landmarkDescription = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "交付成果说明" )
	@Column(name="xresultDescription", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String resultDescription = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "重点事项说明" )
	@Column(name="xmajorIssuesDescription", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String majorIssuesDescription = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "进展计划时限说明" )
	@Column(name="xprogressPlan", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String progressPlan = "";

	@EntityFieldDescribe( "处理状态：正常|已删除" )
	@Column(name="xstatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = false )
	private String status = "正常";
	
	/**
	 * 获取中心工作ID
	 * @return
	 */
	public String getCenterId() {
		return centerId;
	}
	/**
	 * 设置中心工作ID
	 * @param centerId
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	/**
	 * 获取工作详细描述信息
	 * @return
	 */
	public String getWorkDetail() {
		return workDetail;
	}
	/**
	 * 设置工作详细描述信息
	 * @param workDetail
	 */
	public void setWorkDetail(String workDetail) {
		this.workDetail = workDetail;
	}
	/**
	 * 获取工作职责描述
	 * @return
	 */
	public String getDutyDescription() {
		return dutyDescription;
	}
	/**
	 * 设置工作职责描述
	 * @param dutyDescription
	 */
	public void setDutyDescription(String dutyDescription) {
		this.dutyDescription = dutyDescription;
	}
	/**
	 * 获取工作具体行动举措
	 * @return
	 */
	public String getProgressAction() {
		return progressAction;
	}
	/**
	 * 设置工作具体行动举措
	 * @param progressAction
	 */
	public void setProgressAction(String progressAction) {
		this.progressAction = progressAction;
	}
	/**
	 * 获取工作里程碑标志说明
	 * @return
	 */
	public String getLandmarkDescription() {
		return landmarkDescription;
	}
	/**
	 * 设置工作交付成果说明
	 * @param landmarkDescription
	 */
	public void setLandmarkDescription(String landmarkDescription) {
		this.landmarkDescription = landmarkDescription;
	}
	/**
	 * 获取工作交付成果说明
	 * @return
	 */
	public String getResultDescription() {
		return resultDescription;
	}
	/**
	 * 设置工作交付成果说明
	 * @param resultDescription
	 */
	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}
	/**
	 * 获取工作重点事项说明
	 * @return
	 */
	public String getMajorIssuesDescription() {
		return majorIssuesDescription;
	}
	/**
	 * 设置工作重点事项说明
	 * @param majorIssuesDescription
	 */
	public void setMajorIssuesDescription(String majorIssuesDescription) {
		this.majorIssuesDescription = majorIssuesDescription;
	}
	/**
	 * 获取工作进展计划时限说明（后续工作计划说明）
	 * @return
	 */
	public String getProgressPlan() {
		return progressPlan;
	}
	/**
	 * 设置工作进展计划时限说明（后续工作计划说明）
	 * @param progressPlan
	 */
	public void setProgressPlan(String progressPlan) {
		this.progressPlan = progressPlan;
	}
	/**
	 * 获取信息状态：正常|已删除
	 * @return
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * 设置信息状态：正常|已删除
	 * @param status
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}