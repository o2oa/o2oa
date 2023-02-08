package com.x.cms.core.entity.element;

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
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FormField", description = "内容管理表单字段.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Element.FormField.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.FormField.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class FormField extends SliceJpaObject {

	private static final long serialVersionUID = 7769671518373251078L;

	private static final String TABLE = PersistenceProperties.Element.FormField.table;

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

	/* 更新运行方法 */

	/* flag标志位 */
	/* Entity 默认字段结束 */

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("名称.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String name;

	public static final String dataType_FIELDNAME = "dataType";
	@FieldDescribe("字段数据类型.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + dataType_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String dataType;

	public static final String appId_FIELDNAME = "appId";
	@IdReference(AppInfo.class)
	@FieldDescribe("字段所属栏目.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appId_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = AppInfo.class) })
	private String appId;

	public static final String form_FIELDNAME = "form";
	@IdReference(Form.class)
	@FieldDescribe("字段所属表单.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + form_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + form_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Form.class) })
	private String form;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

}