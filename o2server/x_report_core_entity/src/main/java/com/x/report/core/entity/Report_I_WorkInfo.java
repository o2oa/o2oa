package com.x.report.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Report_I_WorkInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_I_WorkInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_I_WorkInfo extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_I_WorkInfo.table;

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
	@CheckPersist(allowEmpty = true)
	private String profileId;

	@FieldDescribe("汇报信息ID")
	@Index(name = TABLE + "_reportId")
	@Column(name = "xreportId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String reportId;

	@FieldDescribe("工作信息ID")
	@Index(name = TABLE + "_keyWorkId")
	@Column(name = "xkeyWorkId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String keyWorkId;

	@Lob
	@FieldDescribe("工作信息标题( 2018-06-20 由255B改为Lob_1M，适应业务数据过大的场景 )")
	@Column(name = "xworkTitle", length = JpaObject.length_1M)
	@CheckPersist(allowEmpty = true)
	private String workTitle;

	@FieldDescribe("工作创建者")
	@Column(name = "xworkCreator", length = AbstractPersistenceProperties.organization_name_length)
	private String workCreator;

	@FieldDescribe("工作类别:部门重点工作|个人工作")
	@Column(name = "xworkType", length = JpaObject.length_32B)
	private String workType = "部门重点工作";

	@FieldDescribe("工作月份标识:THISMONTH|NEXTMONTH")
	@Column(name = "xworkMonthFlag", length = JpaObject.length_16B)
	private String workMonthFlag = "THISMONTH";

	@FieldDescribe("工作标签:自定义标签")
	@Column(name = "xworkTag", length = JpaObject.length_255B)
	private String workTag = "部门重点工作";

	@FieldDescribe("工作所属组织名称")
	@Column(name = "xworkUnit", length = AbstractPersistenceProperties.organization_name_length)
	private String workUnit;

	@FieldDescribe("工作年份")
	@Index(name = TABLE + "_workYear")
	@Column(name = "xworkYear", length = JpaObject.length_8B)
	private String workYear;

	@FieldDescribe("工作汇报年份")
	@Index(name = TABLE + "_workReportYear")
	@Column(name = "xworkReportYear", length = JpaObject.length_8B)
	private String workReportYear;

	@FieldDescribe("工作汇报月份")
	@Index(name = TABLE + "_workReportMonth")
	@Column(name = "xworkReportMonth", length = JpaObject.length_8B)
	private String workReportMonth;

	@FieldDescribe("排序號")
	@Column(name = "xorderNumber")
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber = 0;

	@FieldDescribe("是否允许修改")
	@Column(name = "xmodifyAble")
	private Boolean modifyAble = true;

	@FieldDescribe("本次汇报工作关联举措ID列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + "_measuresList", joinIndex = @Index(name = TABLE + "_measuresList_join"))
	@ElementColumn(length = JpaObject.length_id, name = "xmeasuresList")
	@ElementIndex(name = TABLE + "_measuresList_element")
	private List<String> measuresList;

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getKeyWorkId() {
		return keyWorkId;
	}

	public void setKeyWorkId(String keyWorkId) {
		this.keyWorkId = keyWorkId;
	}

	public List<String> getMeasuresList() {
		return measuresList;
	}

	public void setMeasuresList(List<String> measuresList) {
		this.measuresList = measuresList;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getWorkCreator() {
		return workCreator;
	}

	public void setWorkCreator(String workCreator) {
		this.workCreator = workCreator;
	}

	public String getWorkUnit() {
		return workUnit;
	}

	public void setWorkUnit(String workUnit) {
		this.workUnit = workUnit;
	}

	public String getWorkYear() {
		return workYear;
	}

	public void setWorkYear(String workYear) {
		this.workYear = workYear;
	}

	public String getWorkReportYear() {
		return workReportYear;
	}

	public void setWorkReportYear(String workReportYear) {
		this.workReportYear = workReportYear;
	}

	public String getWorkReportMonth() {
		return workReportMonth;
	}

	public void setWorkReportMonth(String workReportMonth) {
		this.workReportMonth = workReportMonth;
	}

	public String getWorkType() {
		return workType;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public String getWorkTag() {
		return workTag;
	}

	public void setWorkTag(String workTag) {
		this.workTag = workTag;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getWorkMonthFlag() {
		return workMonthFlag;
	}

	public void setWorkMonthFlag(String workMonthFlag) {
		this.workMonthFlag = workMonthFlag;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Boolean getModifyAble() {
		return modifyAble;
	}

	public void setModifyAble(Boolean modifyAble) {
		this.modifyAble = modifyAble;
	}

}