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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceAppealInfo.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceAppealInfo.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceAppealInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AttendanceAppealInfo.table;

	public AttendanceAppealInfo() {
	}

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
	@FieldDescribe("申诉的打卡记录ID.")
	@Column(name = "xdetailId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String detailId;

	@FieldDescribe("申诉员工姓名")
	@Column(name = "xempName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String empName;

	@FieldDescribe("顶层组织名称")
	@Column(name = "xtopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String topUnitName;

	@FieldDescribe("组织名称")
	@Column(name = "xunitName", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String unitName;

	@FieldDescribe("申诉年份")
	@Column(name = "xyearString", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String yearString;

	@FieldDescribe("申诉月份")
	@Column(name = "xmonthString", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String monthString;

	@FieldDescribe("申诉日期字符串")
	@Column(name = "xappealDateString", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String appealDateString;

	@FieldDescribe("记录日期字符串")
	@Column(name = "xrecordDateString", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String recordDateString;

	@FieldDescribe("记录日期")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "xrecordDate")
	@CheckPersist(allowEmpty = true)
	private Date recordDate;

	@FieldDescribe("审批状态:0-待处理，1-审批通过，-1-审批不能过，2-需要下一次审批")
	@Column(name = "xstatus")
	@CheckPersist(allowEmpty = true)
	private Integer status = 0;

	@FieldDescribe("开始时间")
	@Column(name = "xstartTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String startTime;

	@FieldDescribe("结束时间")
	@Column(name = "xendTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String endTime;

	@FieldDescribe("申诉原因")
	@Column(name = "xappealReason", length = JpaObject.length_64B)
	@CheckPersist(allowEmpty = true)
	private String appealReason;

	@FieldDescribe("请假类型")
	@Column(name = "xselfHolidayType", length = JpaObject.length_64B)
	@CheckPersist(allowEmpty = true)
	private String selfHolidayType;

	@FieldDescribe("地址")
	@Column(name = "xaddress", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String address;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("申诉事由")
	@Column(name = "xreason", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String reason;

	@Lob
	@FieldDescribe("申诉详细说明")
	@Column(name = "xappealDescription", length = JpaObject.length_2K)
	@CheckPersist(allowEmpty = true)
	private String appealDescription;

	@FieldDescribe("当前审核人")
	@Column(name = "xcurrentProcessor", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String currentProcessor;

	@FieldDescribe("审批人一")
	@Column(name = "xprocessPerson1", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String processPerson1;

	@FieldDescribe("审批人组织一")
	@Column(name = "xprocessPersonUnit1", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String processPersonUnit1;

	@FieldDescribe("审批人顶层组织一")
	@Column(name = "xprocessPersonTopUnit1", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String processPersonTopUnit1;

	@FieldDescribe("审批意见一")
	@Column(name = "xopinion1", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String opinion1;

	@FieldDescribe("审批日期一")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "xprocessTime1")
	@CheckPersist(allowEmpty = true)
	private Date processTime1;

	@FieldDescribe("审批人二")
	@Column(name = "xprocessPerson2", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String processPerson2;

	@FieldDescribe("审批人组织二")
	@Column(name = "xprocessPersonUnit2", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String processPersonUnit2;

	@FieldDescribe("审批人顶层组织二")
	@Column(name = "xprocessPersonTopUnit2", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String processPersonTopUnit2;

	@FieldDescribe("审批意见二")
	@Column(name = "xopinion2", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String opinion2;

	@FieldDescribe("审批日期二")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "xprocessTime2")
	@CheckPersist(allowEmpty = true)
	private Date processTime2;

	@FieldDescribe("归档时间")
	@Column(name = "xarchiveTime", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String archiveTime;

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getYearString() {
		return yearString;
	}

	public void setYearString(String yearString) {
		this.yearString = yearString;
	}

	public String getMonthString() {
		return monthString;
	}

	public void setMonthString(String monthString) {
		this.monthString = monthString;
	}

	public String getRecordDateString() {
		return recordDateString;
	}

	public void setRecordDateString(String recordDateString) {
		this.recordDateString = recordDateString;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getAppealReason() {
		return appealReason;
	}

	public void setAppealReason(String appealReason) {
		this.appealReason = appealReason;
	}

	public String getAppealDescription() {
		return appealDescription;
	}

	public void setAppealDescription(String appealDescription) {
		this.appealDescription = appealDescription;
	}

	public String getProcessPerson1() {
		return processPerson1;
	}

	public void setProcessPerson1(String processPerson1) {
		this.processPerson1 = processPerson1;
	}

	public String getProcessPersonUnit1() {
		return processPersonUnit1;
	}

	public void setProcessPersonUnit1(String processPersonUnit1) {
		this.processPersonUnit1 = processPersonUnit1;
	}

	public String getProcessPersonTopUnit1() {
		return processPersonTopUnit1;
	}

	public void setProcessPersonTopUnit1(String processPersonTopUnit1) {
		this.processPersonTopUnit1 = processPersonTopUnit1;
	}

	public String getOpinion1() {
		return opinion1;
	}

	public void setOpinion1(String opinion1) {
		this.opinion1 = opinion1;
	}

	public Date getProcessTime1() {
		return processTime1;
	}

	public void setProcessTime1(Date processTime1) {
		this.processTime1 = processTime1;
	}

	public String getAppealDateString() {
		return appealDateString;
	}

	public void setAppealDateString(String appealDateString) {
		this.appealDateString = appealDateString;
	}

	public String getProcessPerson2() {
		return processPerson2;
	}

	public void setProcessPerson2(String processPerson2) {
		this.processPerson2 = processPerson2;
	}

	public String getProcessPersonUnit2() {
		return processPersonUnit2;
	}

	public void setProcessPersonUnit2(String processPersonUnit2) {
		this.processPersonUnit2 = processPersonUnit2;
	}

	public String getProcessPersonTopUnit2() {
		return processPersonTopUnit2;
	}

	public void setProcessPersonTopUnit2(String processPersonTopUnit2) {
		this.processPersonTopUnit2 = processPersonTopUnit2;
	}

	public String getOpinion2() {
		return opinion2;
	}

	public void setOpinion2(String opinion2) {
		this.opinion2 = opinion2;
	}

	public Date getProcessTime2() {
		return processTime2;
	}

	public void setProcessTime2(Date processTime2) {
		this.processTime2 = processTime2;
	}

	public String getCurrentProcessor() {
		return currentProcessor;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setCurrentProcessor(String currentProcessor) {
		this.currentProcessor = currentProcessor;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSelfHolidayType() {
		return selfHolidayType;
	}

	public void setSelfHolidayType(String selfHolidayType) {
		this.selfHolidayType = selfHolidayType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getArchiveTime() {
		return archiveTime;
	}

	public void setArchiveTime(String archiveTime) {
		this.archiveTime = archiveTime;
	}
}