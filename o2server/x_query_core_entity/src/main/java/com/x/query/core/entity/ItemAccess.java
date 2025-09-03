package com.x.query.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

@Schema(name = "ItemAccess", description = "业务字段可见配置.")
@Entity
@ContainerEntity(dumpSize = 10, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.soft)
@Table(name = PersistenceProperties.ItemAccess.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.ItemAccess.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.ColumnNamePrefix + ItemAccess.itemCategoryId_FIELDNAME,
				JpaObject.ColumnNamePrefix + ItemAccess.path_FIELDNAME }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ItemAccess extends SliceJpaObject {

	private static final long serialVersionUID = 3586573657619339181L;

	private static final String TABLE = PersistenceProperties.ItemAccess.table;

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
		// nothing
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			this.readActivityList = this.properties.getReadActivityList();
			this.editActivityList = this.properties.getEditActivityList();
			this.readerList = this.properties.getReaderList();
			this.editorList = this.properties.getEditorList();
		}
	}

	public static final String itemCategoryId_FIELDNAME = "itemCategoryId";
	@FieldDescribe("流程ID或者内容管理分类ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + itemCategoryId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String itemCategoryId;

	public static final String itemCategory_FIELDNAME = "itemCategory";
	@Enumerated(EnumType.STRING)
	@Column(length = ItemCategory.length, name = ColumnNamePrefix + itemCategory_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ItemCategory itemCategory;

	public static final String path_FIELDNAME = "path";
	@FieldDescribe("字段路径.")
	@Column(length = length_255B, name = ColumnNamePrefix + path_FIELDNAME)
	@Index(name = TABLE + ColumnNamePrefix + path_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String path;

	public String getItemCategoryId() {
		return itemCategoryId;
	}

	public void setItemCategoryId(String itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}

	public ItemCategory getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(ItemCategory itemCategory) {
		this.itemCategory = itemCategory;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static final String properties_FIELDNAME = "properties";
	@Schema(description = "属性存储字段.")
	@FieldDescribe("属性存储字段.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ItemAccessProperties properties;

	public ItemAccessProperties getProperties() {
		if (null == this.properties) {
			this.properties = new ItemAccessProperties();
		}
		return this.properties;
	}

	public void setProperties(ItemAccessProperties properties) {
		this.properties = properties;
	}

	@FieldDescribe("可查看对象DN列表：人员、组织、群组、角色.")
	@Transient
	private List<String> readerList;

	@FieldDescribe("可查看流程活动列表.")
	@FieldTypeDescribe(fieldType = "class", fieldTypeName = "ItemAccessActivity",
			fieldValue = "{'activity':'活动ID','activityName':'活动名称','activityAlias':'活动别名','activityType':'活动类型'}")
	@Transient
	private List<ItemAccessActivity> readActivityList;

	@FieldDescribe("可编辑对象DN列表：人员、组织、群组、角色.")
	@Transient
	private List<String> editorList;

	@FieldDescribe("可编辑流程活动列表.")
	@FieldTypeDescribe(fieldType = "class", fieldTypeName = "ItemAccessActivity",
			fieldValue = "{'activity':'活动ID','activityName':'活动名称','activityAlias':'活动别名','activityType':'活动类型'}")
	@Transient
	private List<ItemAccessActivity> editActivityList;

	public List<String> getReaderList() {
		if ((null == this.readerList) && (null != this.properties)) {
			this.readerList = this.properties.getReaderList();
		}
		return readerList;
	}

	public void setReaderList(List<String> readerList) {
		this.readerList = readerList;
		this.getProperties().setReaderList(readerList);
	}

	public List<ItemAccessActivity> getReadActivityList() {
		if ((null == this.readActivityList) && (null != this.properties)) {
			this.readActivityList = this.properties.getReadActivityList();
		}
		return readActivityList;
	}

	public void setReadActivityList(
			List<ItemAccessActivity> readActivityList) {
		this.readActivityList = readActivityList;
		this.getProperties().setReadActivityList(readActivityList);
	}

	public List<String> getEditorList() {
		if ((null == this.editorList) && (null != this.properties)) {
			this.editorList = this.properties.getEditorList();
		}
		return editorList;
	}

	public void setEditorList(List<String> editorList) {
		this.editorList = editorList;
		this.getProperties().setEditorList(editorList);
	}

	public List<ItemAccessActivity> getEditActivityList() {
		if ((null == this.editActivityList) && (null != this.properties)) {
			this.editActivityList = this.properties.getEditActivityList();
		}
		return editActivityList;
	}

	public void setEditActivityList(
			List<ItemAccessActivity> editActivityList) {
		this.editActivityList = editActivityList;
		this.getProperties().setEditActivityList(editActivityList);
	}
}
