package com.x.okr.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
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
	@FieldDescribe("所属中心工作ID")
	@Column(name = "xcenterId", length = JpaObject.length_id)
	@Index(name = TABLE + "_centerId")
	@CheckPersist(allowEmpty = true)
	private String centerId = null;

	@FieldDescribe("中心工作标题")
	@Column(name = "xcenterTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String centerTitle = "";

	@FieldDescribe("所属工作ID")
	@Column(name = "xworkId", length = JpaObject.length_id)
	@Index(name = TABLE + "_workId")
	@CheckPersist(allowEmpty = true)
	private String workId = "";

	@FieldDescribe("工作标题")
	@Column(name = "xworkTitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String workTitle = "";

	@FieldDescribe("工作部署年份")
	@Column(name = "xdeployYear", length = JpaObject.length_16B)
	@Index(name = TABLE + "_deployYear")
	@CheckPersist(allowEmpty = true)
	private String deployYear = "";

	@FieldDescribe("上级工作ID")
	@Column(name = "xparentWorkId", length = JpaObject.length_id)
	@Index(name = TABLE + "_parentWorkId")
	@CheckPersist(allowEmpty = true)
	private String parentWorkId = "";

	@FieldDescribe("工作期限类型:短期工作(不需要自动启动定期汇报) | 长期工作（需要自动启动定期汇报）")
	@Column(name = "xworkDateTimeType", length = JpaObject.length_16B)
	@Index(name = TABLE + "_workDateTimeType")
	@CheckPersist(allowEmpty = true)
	private String workDateTimeType = "长期工作";

	@FieldDescribe("工作类别")
	@Column(name = "xworkType", length = JpaObject.length_32B)
	@Index(name = TABLE + "_workType")
	@CheckPersist(allowEmpty = true)
	private String workType = "";

	@FieldDescribe("工作级别")
	@Column(name = "xworkLevel", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + "_workLevel")
	private String workLevel = "";

	@FieldDescribe("工作处理状态：草稿|待确认|执行中|已超期|已完成|已撤消")
	@Column(name = "xworkProcessStatus", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + "_workProcessStatus")
	private String workProcessStatus = "草稿";

	@FieldDescribe("工作是否已超期")
	@Column(name = "xisOverTime")
	@Index(name = TABLE + "_isOverTime")
	@CheckPersist(allowEmpty = true)
	private Boolean isOverTime = true;

	@FieldDescribe("工作是否已完成")
	@Column(name = "xisCompleted")
	@Index(name = TABLE + "_isCompleted")
	@CheckPersist(allowEmpty = true)
	private Boolean isCompleted = true;

	@FieldDescribe("工作部署月份")
	@Column(name = "xdeployMonth", length = JpaObject.length_16B)
	@Index(name = TABLE + "_deployMonth")
	@CheckPersist(allowEmpty = true)
	private String deployMonth = "";

	@FieldDescribe("工作创建日期-字符串，显示用：yyyy-mm-dd")
	@Column(name = "xworkCreateDateStr", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String workCreateDateStr = "";

	@FieldDescribe("工作部署日期-字符串，显示用：yyyy-mm-dd")
	@Column(name = "xdeployDateStr", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String deployDateStr = "";

	@FieldDescribe("工作完成日期")
	@Column(name = "xcompleteDateLimit")
	@CheckPersist(allowEmpty = true)
	private Date completeDateLimit = null;

	@FieldDescribe("工作完成日期-字符串，显示用：yyyy-mm-dd")
	@Column(name = "xcompleteDateLimitStr", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String completeDateLimitStr = "";

	@FieldDescribe("员工姓名")
	@Column(name = "xemployeeName", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_employeeName")
	@CheckPersist(allowEmpty = true)
	private String employeeName = null;

	@FieldDescribe("员工身份")
	@Column(name = "xemployeeIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_employeeIdentity")
	@CheckPersist(allowEmpty = true)
	private String employeeIdentity = null;

	@FieldDescribe("员工所属组织名称")
	@Column(name = "xunitName", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_unitName")
	@CheckPersist(allowEmpty = true)
	private String unitName = null;

	@FieldDescribe("员工所属顶层组织名称")
	@Column(name = "xtopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_topUnitName")
	@CheckPersist(allowEmpty = true)
	private String topUnitName = null;

	@FieldDescribe("员工处理身份：部署者，责任者，协助者，阅知者")
	@Column(name = "xprocessIdentity", length = JpaObject.length_32B)
	@Index(name = TABLE + "_processIdentity")
	@CheckPersist(allowEmpty = true)
	private String processIdentity = null;

	@FieldDescribe("是否受托人")
	@Column(name = "xisDelegateTarget")
	@Index(name = TABLE + "_isDelegateTarget")
	@CheckPersist(allowEmpty = false)
	private Boolean isDelegateTarget = false;

	@FieldDescribe("阅读时间：员工阅读工作内容的时间，如果未读则为空字符串")
	@Column(name = "xviewTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String viewTime = "";

	@FieldDescribe("影响记录的授权信息ID")
	@Column(name = "xauthorizeRecordId", length = JpaObject.length_id)
	@Index(name = TABLE + "_authorizeRecordId")
	@CheckPersist(allowEmpty = true)
	private String authorizeRecordId = "";

	@FieldDescribe("处理状态：正常|已删除")
	@Column(name = "xstatus", length = JpaObject.length_16B)
	@Index(name = TABLE + "_status")
	@CheckPersist(allowEmpty = true)
	private String status = "正常";

	@FieldDescribe("备注说明")
	@Column(name = "xdiscription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String discription = null;

	@FieldDescribe("记录的类别：中心工作|具体工作")
	@Column(name = "xrecordType", length = JpaObject.length_32B)
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