package com.x.report.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 汇报可见权限记录表
 * 
 * @author O2LEE
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Report_P_Permission.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_P_Permission.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_P_Permission extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Report_P_Permission.table;

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

	@FieldDescribe("汇报ID")
	@Column(name = "xreportId", length = JpaObject.length_id)
	@Index(name = TABLE + "_xreportId")
	@CheckPersist(allowEmpty = false)
	private String reportId;

	@FieldDescribe("概要文件信息ID")
	@Index(name = TABLE + "_xprofileId")
	@Column(name = "xprofileId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = false)
	private String profileId;

	@FieldDescribe("汇报标题")
	@Column(name = "xtitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String title = "无标题";

	@FieldDescribe("汇报周期类别：MONTH|WEEK|DAILY")
	@Column(name = "xreportType", length = JpaObject.length_32B)
	@Index(name = TABLE + "_xreportType")
	@CheckPersist(allowEmpty = false)
	private String reportType;

	@FieldDescribe("汇报年份")
	@Index(name = TABLE + "_xyear")
	@Column(name = "xyear", length = JpaObject.length_8B)
	private String year;

	@FieldDescribe("汇报月份")
	@Index(name = TABLE + "_xmonth")
	@Column(name = "xmonth", length = JpaObject.length_16B)
	private String month;

	@FieldDescribe("汇报周数")
	@Column(name = "xweek", length = JpaObject.length_16B)
	private String week;

	@FieldDescribe("汇报日期")
	@Column(name = "xreportDate")
	private Date reportDate;

	@FieldDescribe("汇报日期字符串")
	@Column(name = "xreportDateString", length = JpaObject.length_32B)
	private String reportDateString;

	@FieldDescribe("汇报对象类别: PERSON | UNIT")
	@Index(name = TABLE + "_xreportObjType")
	@Column(name = "xreportObjType", length = JpaObject.length_16B)
	private String reportObjType;

	@FieldDescribe("汇报所属组织")
	@Index(name = TABLE + "_xtargetUnit")
	@Column(name = "xtargetUnit", length = JpaObject.length_255B)
	private String targetUnit = null;

	@FieldDescribe("部门管理员")
	@Index(name = TABLE + "_xunitAdmin")
	@Column(name = "xunitAdmin", length = JpaObject.length_255B)
	private String unitAdmin = null;

	@FieldDescribe("当前审核环节")
	@Index(name = TABLE + "_xactivityName")
	@Column(name = "xactivityName", length = JpaObject.length_255B)
	private String activityName = null;

	@FieldDescribe("权限类别：阅读|管理")
	@Column(name = "xpermission", length = JpaObject.length_16B)
	@Index(name = TABLE + "_permission")
	private String permission = "所有人";

	@FieldDescribe("使用者类别：所有人|组织|人员|群组|角色")
	@Column(name = "xpermissionObjectType", length = JpaObject.length_16B)
	@Index(name = TABLE + "_permissionObjectType")
	private String permissionObjectType = "人员";

	@FieldDescribe("使用者编码：所有人|组织编码|人员UID|群组编码|角色编码")
	@Column(name = "xpermissionObjectCode", length = JpaObject.length_255B)
	@Index(name = TABLE + "_permissionObjectCode")
	private String permissionObjectCode = "人员UID";

	@FieldDescribe("使用者名称：所有人|组织名称|人员名称|群组名称|角色名称")
	@Column(name = "xpermissionObjectName", length = JpaObject.length_255B)
	private String permissionObjectName = "人员名称";

	@FieldDescribe("更新标识")
	@Index(name = TABLE + "_updateFlag")
	@Column(name = "xupdateFlag", length = JpaObject.length_32B)
	private String updateFlag = null;

	@FieldDescribe("汇报日期字符串")
	@Index(name = TABLE + "_createDateString")
	@Column(name = "xcreateDateString", length = JpaObject.length_32B)
	private String createDateString;

	@FieldDescribe("汇报信息状态：审核中|已完成")
	@Index(name = TABLE + "_reportStatus")
	@Column(name = "xreportStatus", length = JpaObject.length_32B)
	private String reportStatus = "审核中";

	@FieldDescribe("流程流转状态：待启动|流转中|已完成|错误")
	@Index(name = TABLE + "_xwfProcessStatus")
	@Column(name = "xwfProcessStatus", length = JpaObject.length_32B)
	private String wfProcessStatus = "待启动";

	public String getReportId() {
		return reportId;
	}

	public String getTitle() {
		return title;
	}

	public String getReportType() {
		return reportType;
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

	public String getActivityName() {
		return activityName;
	}

	public String getPermission() {
		return permission;
	}

	public String getPermissionObjectType() {
		return permissionObjectType;
	}

	public String getPermissionObjectCode() {
		return permissionObjectCode;
	}

	public String getPermissionObjectName() {
		return permissionObjectName;
	}

	public String getUpdateFlag() {
		return updateFlag;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
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

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public void setPermissionObjectType(String permissionObjectType) {
		this.permissionObjectType = permissionObjectType;
	}

	public void setPermissionObjectCode(String permissionObjectCode) {
		this.permissionObjectCode = permissionObjectCode;
	}

	public void setPermissionObjectName(String permissionObjectName) {
		this.permissionObjectName = permissionObjectName;
	}

	public void setUpdateFlag(String updateFlag) {
		this.updateFlag = updateFlag;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getReportObjType() {
		return reportObjType;
	}

	public void setReportObjType(String reportObjType) {
		this.reportObjType = reportObjType;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public String getReportDateString() {
		return reportDateString;
	}

	public String getCreateDateString() {
		return createDateString;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public void setReportDateString(String reportDateString) {
		this.reportDateString = reportDateString;
	}

	public void setCreateDateString(String createDateString) {
		this.createDateString = createDateString;
	}

	public String getTargetUnit() {
		return targetUnit;
	}

	public void setTargetUnit(String targetUnit) {
		this.targetUnit = targetUnit;
	}

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	public String getWfProcessStatus() {
		return wfProcessStatus;
	}

	public void setWfProcessStatus(String wfProcessStatus) {
		this.wfProcessStatus = wfProcessStatus;
	}

	public String getUnitAdmin() {
		return unitAdmin;
	}

	public void setUnitAdmin(String unitAdmin) {
		this.unitAdmin = unitAdmin;
	}
}