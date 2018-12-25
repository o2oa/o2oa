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

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 工作详细描述信息管理实体类
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkDetailInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrWorkDetailInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkDetailInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkDetailInfo.table;

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
	public static final String centerId_FIELDNAME = "centerId";
	@FieldDescribe("中心工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + centerId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String centerId = "";

	public static final String workDetail_FIELDNAME = "workDetail";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("工作详细描述, 事项分解")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + workDetail_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workDetail = "";

	public static final String dutyDescription_FIELDNAME = "dutyDescription";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("职责描述")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + dutyDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dutyDescription = "";

	public static final String progressAction_FIELDNAME = "progressAction";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("具体行动举措")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + progressAction_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String progressAction = "";

	public static final String landmarkDescription_FIELDNAME = "landmarkDescription";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("里程碑标志说明")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + landmarkDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String landmarkDescription = "";

	public static final String resultDescription_FIELDNAME = "resultDescription";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("交付成果说明")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + resultDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String resultDescription = "";

	public static final String majorIssuesDescription_FIELDNAME = "majorIssuesDescription";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("重点事项说明")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + majorIssuesDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String majorIssuesDescription = "";

	public static final String progressPlan_FIELDNAME = "progressPlan";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("进展计划时限说明")
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + progressPlan_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String progressPlan = "";

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("处理状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status = "正常";

	/**
	 * 获取中心工作ID
	 * 
	 * @return
	 */
	public String getCenterId() {
		return centerId;
	}

	/**
	 * 设置中心工作ID
	 * 
	 * @param centerId
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	/**
	 * 获取工作详细描述信息
	 * 
	 * @return
	 */
	public String getWorkDetail() {
		return workDetail;
	}

	/**
	 * 设置工作详细描述信息
	 * 
	 * @param workDetail
	 */
	public void setWorkDetail(String workDetail) {
		this.workDetail = workDetail;
	}

	/**
	 * 获取工作职责描述
	 * 
	 * @return
	 */
	public String getDutyDescription() {
		return dutyDescription;
	}

	/**
	 * 设置工作职责描述
	 * 
	 * @param dutyDescription
	 */
	public void setDutyDescription(String dutyDescription) {
		this.dutyDescription = dutyDescription;
	}

	/**
	 * 获取工作具体行动举措
	 * 
	 * @return
	 */
	public String getProgressAction() {
		return progressAction;
	}

	/**
	 * 设置工作具体行动举措
	 * 
	 * @param progressAction
	 */
	public void setProgressAction(String progressAction) {
		this.progressAction = progressAction;
	}

	/**
	 * 获取工作里程碑标志说明
	 * 
	 * @return
	 */
	public String getLandmarkDescription() {
		return landmarkDescription;
	}

	/**
	 * 设置工作交付成果说明
	 * 
	 * @param landmarkDescription
	 */
	public void setLandmarkDescription(String landmarkDescription) {
		this.landmarkDescription = landmarkDescription;
	}

	/**
	 * 获取工作交付成果说明
	 * 
	 * @return
	 */
	public String getResultDescription() {
		return resultDescription;
	}

	/**
	 * 设置工作交付成果说明
	 * 
	 * @param resultDescription
	 */
	public void setResultDescription(String resultDescription) {
		this.resultDescription = resultDescription;
	}

	/**
	 * 获取工作重点事项说明
	 * 
	 * @return
	 */
	public String getMajorIssuesDescription() {
		return majorIssuesDescription;
	}

	/**
	 * 设置工作重点事项说明
	 * 
	 * @param majorIssuesDescription
	 */
	public void setMajorIssuesDescription(String majorIssuesDescription) {
		this.majorIssuesDescription = majorIssuesDescription;
	}

	/**
	 * 获取工作进展计划时限说明（后续工作计划说明）
	 * 
	 * @return
	 */
	public String getProgressPlan() {
		return progressPlan;
	}

	/**
	 * 设置工作进展计划时限说明（后续工作计划说明）
	 * 
	 * @param progressPlan
	 */
	public void setProgressPlan(String progressPlan) {
		this.progressPlan = progressPlan;
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
}