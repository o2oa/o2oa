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
 * 投票选项信息表
 * 
 * @author LIYI
 */
@Schema(name = "BBSVoteOption", description = "论坛投票选项信息.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSVoteOption.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.BBSVoteOption.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSVoteOption extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSVoteOption.table;

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
	private String forumId = "";

	public static final String sectionId_FIELDNAME = "sectionId";
	@FieldDescribe("版块ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + sectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String sectionId = "";

	public static final String mainSectionId_FIELDNAME = "mainSectionId";
	@FieldDescribe("主版块ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + mainSectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mainSectionId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String mainSectionId = "";

	public static final String subjectId_FIELDNAME = "subjectId";
	@FieldDescribe("主题ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + subjectId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + subjectId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String subjectId = "";

	public static final String optionContentType_FIELDNAME = "optionContentType";
	@FieldDescribe("选项类别:文字|图片")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + optionContentType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String optionContentType = "文字";

	public static final String optionGroupId_FIELDNAME = "optionGroupId";
	@FieldDescribe("选项组ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + optionGroupId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String optionGroupId = null;

	public static final String optionTextContent_FIELDNAME = "optionTextContent";
	@FieldDescribe("选项描述值:文字描述内容")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + optionTextContent_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String optionTextContent = null;

	public static final String optionPictureId_FIELDNAME = "optionPictureId";
	@FieldDescribe("选项图片ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + optionPictureId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String optionPictureId = "";

	public static final String chooseCount_FIELDNAME = "chooseCount";
	@FieldDescribe("选项被选择次数")
	@Column(name = ColumnNamePrefix + chooseCount_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer chooseCount = 0;

	public static final String creatorName_FIELDNAME = "creatorName";
	@FieldDescribe("创建人")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creatorName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorName = null;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber = 1;

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

	public String getOptionContentType() {
		return optionContentType;
	}

	public void setOptionContentType(String optionContentType) {
		this.optionContentType = optionContentType;
	}

	public String getOptionTextContent() {
		return optionTextContent;
	}

	public void setOptionTextContent(String optionTextContent) {
		this.optionTextContent = optionTextContent;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOptionGroupId() {
		return optionGroupId;
	}

	public String getOptionPictureId() {
		return optionPictureId;
	}

	public Integer getChooseCount() {
		return chooseCount;
	}

	public void setOptionGroupId(String optionGroupId) {
		this.optionGroupId = optionGroupId;
	}

	public void setOptionPictureId(String optionPictureId) {
		this.optionPictureId = optionPictureId;
	}

	public void setChooseCount(Integer chooseCount) {
		this.chooseCount = chooseCount;
	}

}