package com.x.report.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Report_C_WorkPlan.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_C_WorkPlan.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_C_WorkPlan extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_C_WorkPlan.table;

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
	@FieldDescribe("概要文件信息ID")
	@Index(name = TABLE + "_profileId")
	@Column(name = "xprofileId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = false)
	private String profileId;

	@FieldDescribe("汇报信息ID")
	@Index(name = TABLE + "_reportId")
	@Column(name = "xreportId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = false)
	private String reportId;

	@FieldDescribe("计划期数标识：年+月+周数+日期")
	@Column(name = "xflag", length = JpaObject.length_64B)
	private String flag;

	@FieldDescribe("工作信息ID")
	@Index(name = TABLE + "_xworkInfoId")
	@Column(name = "xworkInfoId", length = JpaObject.length_id)
	private String workInfoId;

	@FieldDescribe("重点工作ID")
	@Index(name = TABLE + "_xkeyWorkId")
	@Column(name = "xkeyWorkId", length = JpaObject.length_id)
	private String keyWorkId;

	@Lob
	@FieldDescribe("计划标题：默认为工作标题")
	@Column(name = "xtitle", length = JpaObject.length_1M)
	private String title;

	@Lob
	@FieldDescribe("工作标题")
	@Column(name = "xworkTitle", length = JpaObject.length_1M)
	private String workTitle;

	@FieldDescribe("计划事项所属工作举措ID")
	@Index(name = TABLE + "_xmeasuresId")
	@Column(name = "xmeasuresId", length = JpaObject.length_id)
	private String measuresId;

	@FieldDescribe("计划事项所属工作举措标题")
	@Column(name = "xmeasuresTitle", length = JpaObject.length_255B)
	private String measuresTitle;

	@FieldDescribe("计划执行年份")
	@Index(name = TABLE + "_xyear")
	@Column(name = "xyear", length = JpaObject.length_16B)
	private String year;

	@FieldDescribe("计划执行月份")
	@Index(name = TABLE + "_xmonth")
	@Column(name = "xmonth", length = JpaObject.length_16B)
	private String month;

	@FieldDescribe("计划执行周数")
	@Column(name = "xweek", length = JpaObject.length_16B)
	private String week;

	@FieldDescribe("计划执行日期")
	@Column(name = "xdate")
	private Date date;

	@FieldDescribe("计划开始时间")
	@Column(name = "xstartTime")
	private Date startTime;

	@FieldDescribe("计划结束时间")
	@Column(name = "xendTime")
	private Date endTime;

	@FieldDescribe("执行者身份")
	@Column(name = "xtargetIdentity", length = JpaObject.length_255B)
	private String targetIdentity = null;

	@FieldDescribe("执行者标识")
	@Index(name = TABLE + "_xtargetPerson")
	@Column(name = "xtargetPerson", length = JpaObject.length_255B)
	private String targetPerson = null;

	@FieldDescribe("排序號")
	@Column(name = "xorderNumber")
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber;

	public String getReportId() {
		return reportId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getFlag() {
		return flag;
	}

	public String getYear() {
		return year;
	}

	public String getMonth() {
		return month;
	}

	public String getWeek() {
		return week;
	}

	public Date getDate() {
		return date;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getMeasuresId() {
		return measuresId;
	}

	public void setMeasuresId(String measuresId) {
		this.measuresId = measuresId;
	}

	public String getMeasuresTitle() {
		return measuresTitle;
	}

	public void setMeasuresTitle(String measuresTitle) {
		this.measuresTitle = measuresTitle;
	}

	public String getTargetIdentity() {
		return targetIdentity;
	}

	public void setTargetIdentity(String targetIdentity) {
		this.targetIdentity = targetIdentity;
	}

	public String getTargetPerson() {
		return targetPerson;
	}

	public void setTargetPerson(String targetPerson) {
		this.targetPerson = targetPerson;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWorkInfoId() {
		return workInfoId;
	}

	public String getKeyWorkId() {
		return keyWorkId;
	}

	public void setWorkInfoId(String workInfoId) {
		this.workInfoId = workInfoId;
	}

	public void setKeyWorkId(String keyWorkId) {
		this.keyWorkId = keyWorkId;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
}