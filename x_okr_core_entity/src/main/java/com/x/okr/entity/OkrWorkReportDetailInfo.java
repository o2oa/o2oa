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
 * 工作汇报详细信息管理实体类
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table( name = PersistenceProperties.OkrWorkReportDetailInfo.table )
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public class OkrWorkReportDetailInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkReportDetailInfo.table;

	/**
	 * 获取记录ID：与汇报基础信息ID一致
	 */
	public String getId() {
		return id;
	}
	/**
	 * 设置记录ID：与汇报基础信息ID一致
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
	public void setCreateTime( Date createTime ) {
		this.createTime = createTime;
	}
	/**
	 * 获取信息更新时间
	 */
	public void setUpdateTime( Date updateTime ) {
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
	
	@EntityFieldDescribe( "所属中心工作ID" )
	@Column( name="xcenterId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = false)
	private String centerId = null;
	
	@EntityFieldDescribe( "所属工作ID" )
	@Column( name="xworkId", length = JpaObject.length_id )
	@CheckPersist( allowEmpty = false)
	private String workId = null;
	
	@EntityFieldDescribe( "工作汇报标题" )
	@Column(name="xtitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false)
	private String title = null;
	
	@EntityFieldDescribe( "工作汇报短标题" )
	@Column(name="xshortTitle", length = JpaObject.length_255B )
	@CheckPersist( allowEmpty = false)
	private String shortTitle = null;
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "截止当前完成情况" )
	@Column(name="xprogressDescription", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String progressDescription = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "后续工作计划" )
	@Column(name="xworkPlan", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String workPlan = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "工作汇报备注信息" )
	@Column(name="xmemo", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String memo = "";
	
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@EntityFieldDescribe( "工作要点及需求" )
	@Column(name="xworkPointAndRequirements", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String workPointAndRequirements  = "";

	@Lob
	@EntityFieldDescribe( "管理员督办信息" )
	@Column(name="xadminSuperviseInfo", length = JpaObject.length_2K )
	@CheckPersist( allowEmpty = true)
	private String adminSuperviseInfo = "";
	
	@EntityFieldDescribe( "处理状态：正常|已删除" )
	@Column(name="xstatus", length = JpaObject.length_16B )
	@CheckPersist( allowEmpty = false )
	private String status = "正常";	
	
	/**
	 * 获取汇报所属中心工作ID
	 * @return
	 */
	public String getCenterId() {
		return centerId;
	}
	/**
	 * 设置汇报所属中心工作ID
	 * @param centerId
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}
	/**
	 * 获取汇报所属工作ID
	 * @return
	 */
	public String getWorkId() {
		return workId;
	}
	/**
	 * 设置汇报所属工作ID
	 * @param workId
	 */
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	/**
	 * 获取汇报标题
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * 设置汇报标题
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 获取汇报简要标题
	 * @return
	 */
	public String getShortTitle() {
		return shortTitle;
	}
	/**
	 * 设置汇报简要标题
	 * @param shortTitle
	 */
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}
	/**
	 * 获取工作进展情况说明
	 * @return
	 */
	public String getProgressDescription() {
		return progressDescription;
	}
	/**
	 * 设置工作进展情况说明
	 * @param progressDescription
	 */
	public void setProgressDescription(String progressDescription) {
		this.progressDescription = progressDescription;
	}
	/**
	 * 获取下一步工作计划
	 * @return
	 */
	public String getWorkPlan() {
		return workPlan;
	}
	/**
	 * 设置下一步工作计划
	 * @param workPlan
	 */
	public void setWorkPlan(String workPlan) {
		this.workPlan = workPlan;
	}
	/**
	 * 获取备注说明信息
	 * @return
	 */
	public String getMemo() {
		return memo;
	}
	/**
	 * 设置备注说明信息
	 * @param memo
	 */
	public void setMemo(String memo) {
		this.memo = memo;
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
	public String getWorkPointAndRequirements() {
		return workPointAndRequirements;
	}
	public void setWorkPointAndRequirements(String workPointAndRequirements) {
		this.workPointAndRequirements = workPointAndRequirements;
	}
	public String getAdminSuperviseInfo() {
		return adminSuperviseInfo;
	}
	public void setAdminSuperviseInfo(String adminSuperviseInfo) {
		this.adminSuperviseInfo = adminSuperviseInfo;
	}
}