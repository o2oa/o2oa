package com.x.bbs.entity;

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

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 系统操作日志信息表
 * 
 * @author LIYI
 */
@Schema(name = "BBSOperationRecord", description = "论坛操作日志.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSOperationRecord.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.BBSOperationRecord.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSOperationRecord extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSOperationRecord.table;

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
	public static final String forumId_FIELDNAME = "forumId";
	@FieldDescribe("论坛ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + forumId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + forumId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String forumId = "";

	public static final String forumName_FIELDNAME = "forumName";
	@FieldDescribe("论坛名称")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + forumName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String forumName = "";

	public static final String sectionId_FIELDNAME = "sectionId";
	@FieldDescribe("版块ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + sectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sectionId = "";

	public static final String sectionName_FIELDNAME = "sectionName";
	@FieldDescribe("版块名称")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + sectionName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sectionName = "";

	public static final String mainSectionId_FIELDNAME = "mainSectionId";
	@FieldDescribe("主版块ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + mainSectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mainSectionId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mainSectionId = "";

	public static final String mainSectionName_FIELDNAME = "mainSectionName";
	@FieldDescribe("主版块名称")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + mainSectionName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mainSectionName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mainSectionName = "";

	public static final String subjectId_FIELDNAME = "subjectId";
	@FieldDescribe("主题ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + subjectId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + subjectId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String subjectId = "";

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("主题名称：标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title = "";

	public static final String objectId_FIELDNAME = "objectId";
	@FieldDescribe("操作对象ID")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + objectId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + objectId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String objectId = "";

	public static final String objectName_FIELDNAME = "objectName";
	@FieldDescribe("操作对象名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + objectName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + objectName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String objectName = "";

	public static final String objectType_FIELDNAME = "objectType";
	@FieldDescribe("操作对象类别：论坛、版块、主题、回复")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + objectType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + objectType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String objectType = "";

	public static final String optType_FIELDNAME = "optType";
	@FieldDescribe("操作方式：登入，登出，新增，修改，删除，查看")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + optType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + optType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String optType = "未知";

	public static final String operatorName_FIELDNAME = "operatorName";
	@FieldDescribe("操作人姓名")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + operatorName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + operatorName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String operatorName = "";

	public static final String hostname_FIELDNAME = "hostname";
	@FieldDescribe("主机名称")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + hostname_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + hostname_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String hostname = "";

	public static final String hostIp_FIELDNAME = "hostIp";
	@FieldDescribe("主机IP地址")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + hostIp_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + hostname_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String hostIp = "";

	public String getForumName() {
		return forumName;
	}

	public void setForumName(String forumName) {
		this.forumName = forumName;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getForumId() {
		return forumId;
	}

	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMainSectionId() {
		return mainSectionId;
	}

	public void setMainSectionId(String mainSectionId) {
		this.mainSectionId = mainSectionId;
	}

	public String getMainSectionName() {
		return mainSectionName;
	}

	public void setMainSectionName(String mainSectionName) {
		this.mainSectionName = mainSectionName;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
}