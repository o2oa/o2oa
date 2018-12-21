package com.x.report.core.entity;

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
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 汇报信息具体内容
 * 
 * @author O2LEE
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Report_I_Detail.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_I_Detail.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_I_Detail extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_I_Detail.table;

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
	@FieldDescribe("汇报信息ID")
	@Column(name = "xreportId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = false)
	@Index(name = TABLE + "_reportId")
	private String reportId;

	@FieldDescribe("汇报标题")
	@Column(name = "xtitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String title;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("读者列表:转为JSON的读者对象信息")
	@Column(name = "xreaders", length = JpaObject.length_1M)
	private String readers = "{}";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("作者列表：转为JSON的作者对象信息")
	@Column(name = "xauthors", length = JpaObject.length_1M)
	private String authors = "{}";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("汇报流程流转日志")
	@Column(name = "xworkflowLog", length = JpaObject.length_1M)
	private String workflowLog = "{}";

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("审批意见内容")
	@Column(name = "xopinions", length = JpaObject.length_1M)
	private String opinions = "{}";

	public String getReportId() {
		return reportId;
	}

	public String getTitle() {
		return title;
	}

	public String getWorkflowLog() {
		return workflowLog;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setWorkflowLog(String workflowLog) {
		this.workflowLog = workflowLog;
	}

	public String getReaders() {
		return readers;
	}

	public void setReaders(String readers) {
		this.readers = readers;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getOpinions() {
		return opinions;
	}

	public void setOpinions(String opinions) {
		this.opinions = opinions;
	}
}