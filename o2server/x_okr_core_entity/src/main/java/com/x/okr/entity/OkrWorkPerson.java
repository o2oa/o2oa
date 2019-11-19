package com.x.okr.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 工作人力资源管理表
 * 
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.OkrWorkPerson.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OkrWorkPerson.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OkrWorkPerson extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.OkrWorkPerson.table;

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
	@FieldDescribe("所属中心工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + centerId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + centerId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerId = null;

	public static final String centerTitle_FIELDNAME = "centerTitle";
	@FieldDescribe("中心工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + centerTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String centerTitle = "";

	public static final String workId_FIELDNAME = "workId";
	@FieldDescribe("所属工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workId = "";

	public static final String workTitle_FIELDNAME = "workTitle";
	@FieldDescribe("工作标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + workTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workTitle = "";

	public static final String deployYear_FIELDNAME = "deployYear";
	@FieldDescribe("工作部署年份")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + deployYear_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + deployYear_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployYear = "";

	public static final String parentWorkId_FIELDNAME = "parentWorkId";
	@FieldDescribe("上级工作ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + parentWorkId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + parentWorkId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String parentWorkId = "";

	public static final String workDateTimeType_FIELDNAME = "workDateTimeType";
	@FieldDescribe("工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + workDateTimeType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workDateTimeType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workDateTimeType = "长期工作";

	public static final String workType_FIELDNAME = "workType";
	@FieldDescribe("工作类别")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + workType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workType = "";

	public static final String workLevel_FIELDNAME = "workLevel";
	@FieldDescribe("工作级别")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + workLevel_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + workLevel_FIELDNAME)
	private String workLevel = "";

	public static final String workProcessStatus_FIELDNAME = "workProcessStatus";
	@FieldDescribe("工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + workProcessStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + workProcessStatus_FIELDNAME)
	private String workProcessStatus = "草稿";

	public static final String isOverTime_FIELDNAME = "isOverTime";
	@FieldDescribe("工作是否已超期")
	@Column(name = ColumnNamePrefix + isOverTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + isOverTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isOverTime = true;

	public static final String isCompleted_FIELDNAME = "isCompleted";
	@FieldDescribe("工作是否已完成")
	@Column(name = ColumnNamePrefix + isCompleted_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + isCompleted_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isCompleted = true;

	public static final String deployMonth_FIELDNAME = "deployMonth";
	@FieldDescribe("工作部署月份")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + deployMonth_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + deployMonth_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployMonth = "";

	public static final String workCreateDateStr_FIELDNAME = "workCreateDateStr";
	@FieldDescribe("工作创建日期-字符串，显示用：yyyy-mm-dd")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + workCreateDateStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workCreateDateStr = "";

	public static final String deployDateStr_FIELDNAME = "deployDateStr";
	@FieldDescribe("工作部署日期-字符串，显示用：yyyy-mm-dd")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + deployDateStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String deployDateStr = "";

	public static final String completeDateLimit_FIELDNAME = "completeDateLimit";
	@FieldDescribe("工作完成日期")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + completeDateLimit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date completeDateLimit = null;

	public static final String completeDateLimitStr_FIELDNAME = "completeDateLimitStr";
	@FieldDescribe("工作完成日期-字符串，显示用：yyyy-mm-dd")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + completeDateLimitStr_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String completeDateLimitStr = "";

	public static final String employeeName_FIELDNAME = "employeeName";
	@FieldDescribe("员工姓名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ employeeName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + employeeName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String employeeName = null;

	public static final String employeeIdentity_FIELDNAME = "employeeIdentity";
	@FieldDescribe("员工身份")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ employeeIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + employeeIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String employeeIdentity = null;

	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("员工所属组织名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ unitName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unitName = null;

	public static final String topUnitName_FIELDNAME = "topUnitName";
	@FieldDescribe("员工所属顶层组织名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ topUnitName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + topUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String topUnitName = null;

	public static final String processIdentity_FIELDNAME = "processIdentity";
	@FieldDescribe("员工处理身份：部署者，责任者，协助者，阅知者")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + processIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + processIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processIdentity = null;

	public static final String isDelegateTarget_FIELDNAME = "isDelegateTarget";
	@FieldDescribe("是否受托人")
	@Column(name = ColumnNamePrefix + isDelegateTarget_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + isDelegateTarget_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean isDelegateTarget = false;

	public static final String viewTime_FIELDNAME = "viewTime";
	@FieldDescribe("阅读时间：员工阅读工作内容的时间，如果未读则为空字符串")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + viewTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String viewTime = "";

	public static final String authorizeRecordId_FIELDNAME = "authorizeRecordId";
	@FieldDescribe("影响记录的授权信息ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + authorizeRecordId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + authorizeRecordId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String authorizeRecordId = "";

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("处理状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + status_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String status = "正常";

	public static final String discription_FIELDNAME = "discription";
	@FieldDescribe("备注说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + discription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String discription = null;

	public static final String recordType_FIELDNAME = "recordType";
	@FieldDescribe("记录的类别：中心工作|具体工作")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + recordType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String recordType = "具体工作";

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
	 * 获取工作ID
	 * 
	 * @return
	 */
	public String getWorkId() {
		return workId;
	}

	/**
	 * 设置工作ID
	 * 
	 * @param workId
	 */
	public void setWorkId(String workId) {
		this.workId = workId;
	}

	/**
	 * 获取员工姓名
	 * 
	 * @return
	 */
	public String getEmployeeName() {
		return employeeName;
	}

	/**
	 * 设置员工姓名
	 * 
	 * @param employeeName
	 */
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	/**
	 * 获取员工所属组织名称
	 * 
	 * @return
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * 设置员工所属组织名称
	 * 
	 * @param unitName
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	/**
	 * 获取员工所属顶层组织名称
	 * 
	 * @return
	 */
	public String getTopUnitName() {
		return topUnitName;
	}

	/**
	 * 设置员工所属顶层组织名称
	 * 
	 * @param topUnitName
	 */
	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	/**
	 * 获取员工处理身份：部署者，责任者，协助者，阅知者
	 * 
	 * @return
	 */
	public String getProcessIdentity() {
		return processIdentity;
	}

	/**
	 * 设置员工处理身份：部署者，责任者，协助者，阅知者
	 * 
	 * @param processIdentity
	 */
	public void setProcessIdentity(String processIdentity) {
		this.processIdentity = processIdentity;
	}

	/**
	 * 获取工作阅读时间：员工阅读工作内容的时间，如果未读则为空字符串
	 * 
	 * @return
	 */
	public String getViewTime() {
		return viewTime;
	}

	/**
	 * 设置工作阅读时间：员工阅读工作内容的时间，如果未读则为空字符串
	 * 
	 * @param viewTime
	 */
	public void setViewTime(String viewTime) {
		this.viewTime = viewTime;
	}

	/**
	 * 获取是否受托者
	 * 
	 * @return
	 */
	public Boolean getIsDelegateTarget() {
		return isDelegateTarget;
	}

	/**
	 * 设置是否受托者
	 * 
	 * @param isDelegateTarget
	 */
	public void setIsDelegateTarget(Boolean isDelegateTarget) {
		this.isDelegateTarget = isDelegateTarget;
	}

	/**
	 * 获取工作部署年份
	 * 
	 * @return
	 */
	public String getDeployYear() {
		return deployYear;
	}

	/**
	 * 设置工作部署年份
	 * 
	 * @param deployYear
	 */
	public void setDeployYear(String deployYear) {
		this.deployYear = deployYear;
	}

	/**
	 * 获取工作部署月份
	 * 
	 * @return
	 */
	public String getDeployMonth() {
		return deployMonth;
	}

	/**
	 * 设置工作部署月份
	 * 
	 * @param deployMonth
	 */
	public void setDeployMonth(String deployMonth) {
		this.deployMonth = deployMonth;
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

	/**
	 * 获取中心工作标题
	 * 
	 * @return
	 */
	public String getCenterTitle() {
		return centerTitle;
	}

	/**
	 * 设置中心工作标题
	 * 
	 * @param centerTitle
	 */
	public void setCenterTitle(String centerTitle) {
		this.centerTitle = centerTitle;
	}

	/**
	 * 获取上级工作ID
	 * 
	 * @return
	 */
	public String getParentWorkId() {
		return parentWorkId;
	}

	/**
	 * 设置上级工作ID
	 * 
	 * @param parentWorkId
	 */
	public void setParentWorkId(String parentWorkId) {
		this.parentWorkId = parentWorkId;
	}

	/**
	 * 获取工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消
	 * 
	 * @return
	 */
	public String getWorkProcessStatus() {
		return workProcessStatus;
	}

	/**
	 * 设置工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消
	 * 
	 * @param workProcessStatus
	 */
	public void setWorkProcessStatus(String workProcessStatus) {
		this.workProcessStatus = workProcessStatus;
	}

	/**
	 * 获取工作类别：工作类别由工作类别配置表决定
	 * 
	 * @return
	 */
	public String getWorkType() {
		return workType;
	}

	/**
	 * 设置工作类别：工作类别由工作类别配置表决定
	 * 
	 * @param workType
	 */
	public void setWorkType(String workType) {
		this.workType = workType;
	}

	/**
	 * 获取工作级别：工作级别由工作级别配置表决定
	 * 
	 * @return
	 */
	public String getWorkLevel() {
		return workLevel;
	}

	/**
	 * 设置工作级别：工作级别由工作级别配置表决定
	 * 
	 * @param workLevel
	 */
	public void setWorkLevel(String workLevel) {
		this.workLevel = workLevel;
	}

	/**
	 * 获取工作是否已超期
	 * 
	 * @return
	 */
	public Boolean getIsOverTime() {
		return isOverTime;
	}

	/**
	 * 设置工作是否已超期
	 * 
	 * @param isOverTime
	 */
	public void setIsOverTime(Boolean isOverTime) {
		this.isOverTime = isOverTime;
	}

	/**
	 * 获取工作是否已完成
	 * 
	 * @return
	 */
	public Boolean getIsCompleted() {
		return isCompleted;
	}

	/**
	 * 设置工作是否已完成
	 * 
	 * @param isCompleted
	 */
	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	/**
	 * 获取工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）
	 * 
	 * @return
	 */
	public String getWorkDateTimeType() {
		return workDateTimeType;
	}

	/**
	 * 设置工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）
	 * 
	 * @param workDateTimeType
	 */
	public void setWorkDateTimeType(String workDateTimeType) {
		this.workDateTimeType = workDateTimeType;
	}

	/**
	 * 获取工作标题
	 * 
	 * @return
	 */
	public String getWorkTitle() {
		return workTitle;
	}

	/**
	 * 设置工作标题
	 * 
	 * @param workTitle
	 */
	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getEmployeeIdentity() {
		return employeeIdentity;
	}

	public void setEmployeeIdentity(String employeeIdentity) {
		this.employeeIdentity = employeeIdentity;
	}

	public String getAuthorizeRecordId() {
		return authorizeRecordId;
	}

	public void setAuthorizeRecordId(String authorizeRecordId) {
		this.authorizeRecordId = authorizeRecordId;
	}

	public String getDiscription() {
		return discription;
	}

	public void setDiscription(String discription) {
		this.discription = discription;
	}

	public String getDeployDateStr() {
		return deployDateStr;
	}

	public Date getCompleteDateLimit() {
		return completeDateLimit;
	}

	public String getCompleteDateLimitStr() {
		return completeDateLimitStr;
	}

	public void setDeployDateStr(String deployDateStr) {
		this.deployDateStr = deployDateStr;
	}

	public void setCompleteDateLimit(Date completeDateLimit) {
		this.completeDateLimit = completeDateLimit;
	}

	public void setCompleteDateLimitStr(String completeDateLimitStr) {
		this.completeDateLimitStr = completeDateLimitStr;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getWorkCreateDateStr() {
		return workCreateDateStr;
	}

	public void setWorkCreateDateStr(String workCreateDateStr) {
		this.workCreateDateStr = workCreateDateStr;
	}
}