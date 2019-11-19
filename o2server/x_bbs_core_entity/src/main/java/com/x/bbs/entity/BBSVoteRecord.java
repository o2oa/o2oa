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

/**
 * 用户投票记录
 * 
 * @author LIYI
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.BBSVoteRecord.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.BBSVoteRecord.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSVoteRecord extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSVoteRecord.table;

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
	@CheckPersist(allowEmpty = false)
	private String forumId = null;

	public static final String sectionId_FIELDNAME = "sectionId";
	@FieldDescribe("版块ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + sectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String sectionId = null;

	public static final String mainSectionId_FIELDNAME = "mainSectionId";
	@FieldDescribe("主版块ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + mainSectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mainSectionId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String mainSectionId = null;

	public static final String subjectId_FIELDNAME = "subjectId";
	@FieldDescribe("主题ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + subjectId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + subjectId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String subjectId = null;

	public static final String optionGroupId_FIELDNAME = "optionGroupId";
	@FieldDescribe("选项组ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + optionGroupId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String optionGroupId = null;

	public static final String optionId_FIELDNAME = "optionId";
	@FieldDescribe("用户投票结果选择项Id")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + optionId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String optionId = "";

	public static final String optionValue_FIELDNAME = "optionValue";
	@FieldDescribe("用户投票结果选择项,对应选择项属性optionValue")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + optionValue_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String optionValue = "";

	public static final String votorName_FIELDNAME = "votorName";
	@FieldDescribe("投票人")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + votorName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String votorName = "";

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

	public String getMainSectionId() {
		return mainSectionId;
	}

	public void setMainSectionId(String mainSectionId) {
		this.mainSectionId = mainSectionId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}

	public String getVotorName() {
		return votorName;
	}

	public void setVotorName(String votorName) {
		this.votorName = votorName;
	}

	public String getOptionId() {
		return optionId;
	}

	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}

	public String getOptionGroupId() { return this.optionGroupId; }

	public void setOptionGroupId(final String optionGroupId) { this.optionGroupId = optionGroupId; }
}