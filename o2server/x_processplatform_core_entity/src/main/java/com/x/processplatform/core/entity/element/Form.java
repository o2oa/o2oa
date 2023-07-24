package com.x.processplatform.core.entity.element;

import java.util.Date;
import java.util.Objects;

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
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.entity.annotation.RestrictFlag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "Form", description = "流程平台表单.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Element.Form.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.Form.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Form extends SliceJpaObject {

	private static final long serialVersionUID = 3263767038182121907L;
	private static final String TABLE = PersistenceProperties.Element.Form.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	@CheckRemove(citationNotExists = {
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Agent.class, fields = Agent.form_FIELDNAME),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Begin.class, fields = Begin.form_FIELDNAME),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Cancel.class, fields = Cancel.form_FIELDNAME),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Choice.class, fields = Choice.form_FIELDNAME),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Delay.class, fields = Delay.form_FIELDNAME),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Embed.class, fields = Embed.form_FIELDNAME),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = End.class, fields = End.form_FIELDNAME),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Invoke.class, fields = Invoke.form_FIELDNAME),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Manual.class, fields = Manual.form_FIELDNAME),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Merge.class, fields = Merge.form_FIELDNAME),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Parallel.class, fields = Parallel.form_FIELDNAME),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Service.class, fields = Service.form_FIELDNAME),
			/* 检查不存在表单应用 */
			@CitationNotExist(type = Split.class, fields = Split.form_FIELDNAME) })
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	public void onPersist() throws Exception {
		// nothing
	}

	public Form() {
		this.properties = new FormProperties();
	}

	public FormProperties getProperties() {
		if (null == this.properties) {
			this.properties = new FormProperties();
		}
		return this.properties;
	}

	public void setProperties(FormProperties properties) {
		this.properties = properties;
	}

	public String getDataOrMobileData() {
		if (StringUtils.isNotEmpty(this.getData())) {
			return this.getData();
		} else if (StringUtils.isNotEmpty(this.getMobileData())) {
			return this.getMobileData();
		}
		return null;
	}

	public String getMobileDataOrData() {
		if (StringUtils.isNotEmpty(this.getMobileData())) {
			return this.getMobileData();
		} else if (StringUtils.isNotEmpty(this.getData())) {
			return this.getData();
		}
		return null;
	}

	public String getCategory() {
		return Objects.toString(this.category, "");
	}

	public static final String name_FIELDNAME = "name";
	@RestrictFlag
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationNotExists =
	/* 同一个应用下不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Form.class, equals = @Equal(property = "application", field = "application")))
	private String name;

	public static final String category_FIELDNAME = "category";
	@FieldDescribe("分类")
	@Column(length = length_255B, name = ColumnNamePrefix + category_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + category_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false)
	private String category;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("表单别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false, citationNotExists =
	/* 同一个应用下不可重名 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Form.class, equals = @Equal(property = "application", field = "application")))
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("表单所属应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Application.class) })
	@IdReference(Application.class)
	private String application;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("最后的编辑者.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ lastUpdatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后的编辑时间.")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String icon;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
//	@Persistent(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public static final String mobileData_FIELDNAME = "mobileData";
	@FieldDescribe("移动端文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	// @Persistent(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + mobileData_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mobileData;

	public static final String hasMobile_FIELDNAME = "hasMobile";
	@FieldDescribe("是否有移动端内容.")
	@Column(name = ColumnNamePrefix + hasMobile_FIELDNAME)
	private Boolean hasMobile;

	public static final String properties_FIELDNAME = "properties";
	// @Basic(fetch = FetchType.EAGER)
	@FieldDescribe("属性对象存储字段.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private FormProperties properties;

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getMobileData() {
		return mobileData;
	}

	public void setMobileData(String mobileData) {
		this.mobileData = mobileData;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Boolean getHasMobile() {
		return hasMobile;
	}

	public void setHasMobile(Boolean hasMobile) {
		this.hasMobile = hasMobile;
	}

}