package com.x.query.core.entity;

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

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "ImportRecordItem", description = "数据中心导入记录条目.")
@Entity
@ContainerEntity(dumpSize = 10, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.soft)
@Table(name = PersistenceProperties.Import.ImportRecordItem.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Import.ImportRecordItem.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ImportRecordItem extends SliceJpaObject {

	private static final long serialVersionUID = 7214636398640246193L;

	private static final String TABLE = PersistenceProperties.Import.ImportRecordItem.table;

	public static final String STATUS_SUCCESS = "导入成功";

	public static final String STATUS_FAILED = "导入失败";

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

	/* 以上为 JpaObject 默认字段 */

	public void onPersist() throws Exception {
	}

	public static final String docTitle_FIELDNAME = "docTitle";
	@FieldDescribe("导入文档标题.")
	@Column(length = length_255B, name = ColumnNamePrefix + docTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String docTitle;

	public static final String docId_FIELDNAME = "docId";
	@FieldDescribe("导入文档ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + docId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String docId;

	public static final String docType_FIELDNAME = "docType";
	@FieldDescribe("导入文档类型.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + docType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String docType;

	public static final String modelId_FIELDNAME = "modelId";
	@FieldDescribe("所属导入模型.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + modelId_FIELDNAME)
	@Index(name = TABLE + ColumnNamePrefix + modelId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String modelId;

	public static final String recordId_FIELDNAME = "recordId";
	@FieldDescribe("所属导入记录.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + recordId_FIELDNAME)
	@Index(name = TABLE + ColumnNamePrefix + recordId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String recordId;

	public static final String query_FIELDNAME = "query";
	@FieldDescribe("所属查询.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + query_FIELDNAME)
	@Index(name = TABLE + ColumnNamePrefix + query_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String query;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("状态.")
	@Column(length = length_255B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status;

	public static final String distribution_FIELDNAME = "distribution";
	@FieldDescribe("导入结果描述.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_4K, name = ColumnNamePrefix + distribution_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String distribution;

	public static final String srcData_FIELDNAME = "srcData";
	@FieldDescribe("原始数据.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + srcData_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String srcData;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("导入数据.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDocTitle() {
		return docTitle;
	}

	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}

	public String getSrcData() {
		return srcData;
	}

	public void setSrcData(String srcData) {
		this.srcData = srcData;
	}
}
