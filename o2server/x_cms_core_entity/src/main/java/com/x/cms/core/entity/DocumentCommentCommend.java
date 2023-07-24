package com.x.cms.core.entity;

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
 * 文档评论点赞
 * 
 * @author O2LEE
 *
 */
@Schema(name = "DocumentCommentCommend", description = "内容管理文档评论点赞.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.DocumentCommentCommend.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.DocumentCommentCommend.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DocumentCommentCommend extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.DocumentCommentCommend.table;

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
	public static final String documentId_FIELDNAME = "documentId";
	@FieldDescribe("文档ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + documentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + documentId_FIELDNAME)
	private String documentId;

	public static final String commentId_FIELDNAME = "commentId";
	@FieldDescribe("文档评论ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + commentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + documentId_FIELDNAME)
	private String commentId;

	public static final String commendPerson_FIELDNAME = "commendPerson";
	@FieldDescribe("点赞者")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + commendPerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	@Index(name = TABLE + IndexNameMiddle + commendPerson_FIELDNAME)
	private String commendPerson;

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getCommendPerson() {
		return commendPerson;
	}

	public void setCommendPerson(String commendPerson) {
		this.commendPerson = commendPerson;
	}
}