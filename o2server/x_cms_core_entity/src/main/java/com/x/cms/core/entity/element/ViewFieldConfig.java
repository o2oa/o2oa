package com.x.cms.core.entity.element;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.cms.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ViewFieldConfig", description = "内容管理视图字段配置.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Element.ViewFieldConfig.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.ViewFieldConfig.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ViewFieldConfig extends SliceJpaObject {

	private static final long serialVersionUID = 3263767038182121907L;
	private static final String TABLE = PersistenceProperties.Element.ViewFieldConfig.table;

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
		this.editor = StringUtils.trimToEmpty(this.editor);
	}

	public static final String editor_FIELDNAME = "editor";
	@FieldDescribe("最后的编辑者.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + editor_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String editor;

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	/* ==============================================以上为 JpaObject 默认字段 */

	/*
	 * =====================================更新运行方法
	 * =======================================
	 */

	public static final String viewId_FIELDNAME = "viewId";
	@FieldDescribe("列表ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + viewId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + viewId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String viewId;

	public static final String fieldTitle_FIELDNAME = "fieldTitle";
	@FieldDescribe("列标题.")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + fieldTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fieldTitle;

	public static final String fieldName_FIELDNAME = "fieldName";
	@FieldDescribe("列（属性）名称.")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + fieldName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fieldName;

	public static final String showSequence_FIELDNAME = "showSequence";
	@FieldDescribe("显示顺序")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + showSequence_FIELDNAME)
	private Integer showSequence = 0;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getFieldTitle() {
		return fieldTitle;
	}

	public void setFieldTitle(String fieldTitle) {
		this.fieldTitle = fieldTitle;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public int getShowSequence() {
		return showSequence;
	}

	public void setShowSequence(int showSequence) {
		this.showSequence = showSequence;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}