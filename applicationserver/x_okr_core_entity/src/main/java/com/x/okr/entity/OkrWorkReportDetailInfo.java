package com.x.okr.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 工作汇报详细信息管理实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkReportDetailInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrWorkReportDetailInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkReportDetailInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkReportDetailInfo.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	public void onPersist() throws Exception {
	}
	/*
	 * =============================================================================
	 * ===== 以上为 JpaObject 默认字段
	 * =============================================================================
	 * =====
	 */

	/*
	 * =============================================================================
	 * ===== 以下为具体不同的业务及数据表字段要求
	 * =============================================================================
	 * =====
	 */

	@FieldDescribe("所属中心工作ID")
	@Column(name = "xcenterId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = false)
	private String centerId = null;

	@FieldDescribe("所属工作ID")
	@Column(name = "xworkId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = false)
	private String workId = null;

	@FieldDescribe("工作汇报标题")
	@Column(name = "xtitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String title = null;

	@FieldDescribe("工作汇报短标题")
	@Column(name = "xshortTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String shortTitle = null;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("截止当前完成情况")
	@Column(name = "xprogressDescription", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String progressDescription = "";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("后续工作计划")
	@Column(name = "xworkPlan", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String workPlan = "";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("工作汇报备注信息")
	@Column(name = "xmemo", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String memo = "";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("工作要点及需求")
	@Column(name = "xworkPointAndRequirements", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String workPointAndRequirements = "";

	@Lob
	@FieldDescribe("管理员督办信息")
	@Column(name = "xadminSuperviseInfo", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String adminSuperviseInfo = "";

	@FieldDescribe("处理状态：正常|已删除")
	@Column(name = "xstatus", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String status = "正常";

	/**
	 * 获取汇报所属中心工作ID
	 * 
	 * @return
	 */
	public String getCenterId() {
		return centerId;
	}

	/**
	 * 设置汇报所属中心工作ID
	 * 
	 * @param centerId
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	/**
	 * 获取汇报所属工作ID
	 * 
	 * @return
	 */
	public String getWorkId() {
		return workId;
	}

	/**
	 * 设置汇报所属工作ID
	 * 
	 * @param workId
	 */
	public void setWorkId(String workId) {
		this.workId = workId;
	}

	/**
	 * 获取汇报标题
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * 设置汇报标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 获取汇报简要标题
	 * 
	 * @return
	 */
	public String getShortTitle() {
		return shortTitle;
	}

	/**
	 * 设置汇报简要标题
	 * 
	 * @param shortTitle
	 */
	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	/**
	 * 获取工作进展情况说明
	 * 
	 * @return
	 */
	public String getProgressDescription() {
		return progressDescription;
	}

	/**
	 * 设置工作进展情况说明
	 * 
	 * @param progressDescription
	 */
	public void setProgressDescription(String progressDescription) {
		this.progressDescription = progressDescription;
	}

	/**
	 * 获取下一步工作计划
	 * 
	 * @return
	 */
	public String getWorkPlan() {
		return workPlan;
	}

	/**
	 * 设置下一步工作计划
	 * 
	 * @param workPlan
	 */
	public void setWorkPlan(String workPlan) {
		this.workPlan = workPlan;
	}

	/**
	 * 获取备注说明信息
	 * 
	 * @return
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * 设置备注说明信息
	 * 
	 * @param memo
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * 获取信息状态：正常|已删除
	 * 
	 * @return
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置信息状态：正常|已删除
	 * 
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