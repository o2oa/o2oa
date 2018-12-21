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
@Table(name = PersistenceProperties.Report_I_Ext_ContentDetail.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_I_Ext_ContentDetail.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_I_Ext_ContentDetail extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_I_Ext_ContentDetail.table;

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

	@FieldDescribe("扩展信息ID")
	@Index(name = TABLE + "_contentId")
	@Column(name = "xcontentId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String contentId;

	@FieldDescribe("扩展信息类别: 员工关爱 | 服务客户 | 意见建议")
	@Index(name = TABLE + "_contentType")
	@Column(name = "xcontentType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String contentType;

	@Lob
	@FieldDescribe("内容")
	@Column(name = "xcontent", length = JpaObject.length_1M)
	private String content;

	@FieldDescribe("排序號")
	@Column(name = "xorderNumber")
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber;

	public String getProfileId() {
		return profileId;
	}

	public String getReportId() {
		return reportId;
	}

	public String getContent() {
		return content;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getContentId() {
		return contentId;
	}

	/**
	 * 扩展信息类别: 员工关爱 | 服务客户 | 意见建议
	 * 
	 * @return
	 */
	public String getContentType() {
		return contentType;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}