package com.x.cms.core.entity.element;

import java.util.Date;
import java.util.List;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.OrderColumn;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.View.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.View.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class View extends SliceJpaObject {

	private static final long serialVersionUID = 3263767038182121907L;
	private static final String TABLE = PersistenceProperties.Element.View.table;

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
		if (this.getCreateTime() == null) {
			this.setCreateTime(new Date());
		}
		if (this.getId() == null) {
			this.setId(View.createId());
		}
	}

	/* ==============================================以上为 JpaObject 默认字段 */

	/*
	 * =====================================更新运行方法
	 * =======================================
	 */

	public static final String name_FIELDNAME = "name";
	@Flag
	@FieldDescribe("名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("列表别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ alias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = "";

	public static final String formId_FIELDNAME = "formId";
	@FieldDescribe("列表所属表单.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + formId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + formId_FIELDNAME)
	@CheckPersist(citationExists = @CitationExist(type = Form.class), allowEmpty = true)
	private String formId;

	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("列表所属栏目Id.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appId_FIELDNAME)
	@CheckPersist(citationExists = @CitationExist(type = AppInfo.class), allowEmpty = true)
	private String appId;

	public static final String editor_FIELDNAME = "editor";
	@FieldDescribe("最后的编辑者.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + editor_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String editor;

	public static final String corderField_FIELDNAME = "orderField";
	@FieldDescribe("排序列名.")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + corderField_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String orderField = "createTime";

	public static final String orderFieldType_FIELDNAME = "orderFieldType";
	@FieldDescribe("列数据类型string|datetime.")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + orderFieldType_FIELDNAME)
	private String orderFieldType = "datetime";

	public static final String orderType_FIELDNAME = "orderType";
	@FieldDescribe("排序方式(ASC|DESC).")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + orderType_FIELDNAME)
	private String orderType;

	public static final String pageSize_FIELDNAME = "pageSize";
	@FieldDescribe("每页显示行数")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + pageSize_FIELDNAME)
	private Integer pageSize = 12;

	public static final String content_FIELDNAME = "content";
	@FieldDescribe("代码内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + content_FIELDNAME)
	private String content;

	public static final String fieldConfigList_FIELDNAME = "fieldConfigList";
	@FieldDescribe("展示列配置列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + fieldConfigList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + fieldConfigList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ fieldConfigList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + fieldConfigList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> fieldConfigList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getOrderField() {
		return orderField;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getFieldConfigList() {
		return fieldConfigList;
	}

	public void setFieldConfigList(List<String> fieldConfigList) {
		this.fieldConfigList = fieldConfigList;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getOrderFieldType() {
		return orderFieldType;
	}

	public void setOrderFieldType(String orderFieldType) {
		this.orderFieldType = orderFieldType;
	}

}