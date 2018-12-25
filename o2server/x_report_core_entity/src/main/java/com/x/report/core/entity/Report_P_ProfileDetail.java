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

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Report_P_ProfileDetail.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_P_ProfileDetail.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_P_ProfileDetail extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_P_ProfileDetail.table;

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

	@FieldDescribe("详细信息对应的概要文件ID.")
	@Index(name = TABLE + "_xprofileId")
	@Column(name = "xprofileId", length = JpaObject.length_id)
	private String profileId = null;

	@FieldDescribe("应用模块")
	@Column(name = "xreportModule", length = JpaObject.length_16B)
	@Index(name = TABLE + "_xreportModule")
	@CheckPersist(allowEmpty = false)
	private String reportModule;

	@FieldDescribe("应用模块名称")
	@Column(name = "xreportModuleName", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = false)
	private String reportModuleName;

	@FieldDescribe("快照信息类别, 如:涉及个人列表|涉及组织列表|战略工作信息|战略信息配置")
	@Index(name = TABLE + "_xsnapType")
	@Column(name = "xsnapType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = false)
	private String snapType;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("快照信息内容")
	@Column(name = "xsnapContent", length = JpaObject.length_10M)
	private String snapContent = "{}";

	public String getReportModule() {
		return reportModule;
	}

	public String getReportModuleName() {
		return reportModuleName;
	}

	public String getSnapType() {
		return snapType;
	}

	public String getSnapContent() {
		return snapContent;
	}

	public void setReportModule(String reportModule) {
		this.reportModule = reportModule;
	}

	public void setReportModuleName(String reportModuleName) {
		this.reportModuleName = reportModuleName;
	}

	public void setSnapType(String snapType) {
		this.snapType = snapType;
	}

	public void setSnapContent(String snapContent) {
		this.snapContent = snapContent;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
}