package com.x.processplatform.core.entity.element;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 流程平台应用
 *
 * @author sword
 */
@Schema(name = "Application", description = "流程平台应用.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Element.Application.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.Application.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Application extends SliceJpaObject {

	private static final long serialVersionUID = -7520516033901189347L;
	private static final String TABLE = PersistenceProperties.Element.Application.table;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	@Override
	public void onPersist() throws Exception {
		this.applicationCategory = StringUtils.trimToEmpty(this.applicationCategory);
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			this.defaultForm = this.getProperties().getDefaultForm();
			this.maintenanceIdentity = this.getProperties().getMaintenanceIdentity();
		}
	}

	public ApplicationProperties getProperties() {
		if (null == this.properties) {
			this.properties = new ApplicationProperties();
		}
		return this.properties;
	}

	public void setProperties(ApplicationProperties properties) {
		this.properties = properties;
	}

	public Application() {
		this.properties = new ApplicationProperties();
	}

	/* Entity 默认字段结束 */

	public static final String DEFAULTFORM_FIELDNAME = "defaultForm";
	@FieldDescribe("应用默认表单.")
	@Transient
	private String defaultForm;

	public String getDefaultForm() {
		if (StringUtils.isNotBlank(this.defaultForm)) {
			return this.defaultForm;
		} else {
			return this.getProperties().getDefaultForm();
		}
	}

	public void setDefaultForm(String defaultForm) {
		this.defaultForm = defaultForm;
		this.getProperties().setDefaultForm(defaultForm);
	}

	public static final String MAINTENANCEIDENTITY_FIELDNAME = "maintenanceIdentity";
	@FieldDescribe("流程维护身份,如果无法找到处理身份默认的流程处理身份.")
	@Transient
	private String maintenanceIdentity;

	public String getMaintenanceIdentity() {
		return maintenanceIdentity;
	}

	public void setMaintenanceIdentity(String maintenanceIdentity) {
		this.maintenanceIdentity = maintenanceIdentity;
		this.getProperties().setMaintenanceIdentity(maintenanceIdentity);
	}

	public static final String name_FIELDNAME = "name";
	@Flag
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists = @CitationNotExist(type = Application.class, fields = {
			"id", "name", "alias" }))
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("应用别名,如果有必须唯一.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists = @CitationNotExist(type = Application.class, fields = {
			"id", "name", "alias" }))
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String availableIdentityList_FIELDNAME = "availableIdentityList";
	@FieldDescribe("可见应用的身份.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ availableIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ availableIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + availableIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + availableIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableIdentityList;

	public static final String availableUnitList_FIELDNAME = "availableUnitList";
	@FieldDescribe("可见应用的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ availableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ availableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + availableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + availableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableUnitList;

	public static final String availableGroupList_FIELDNAME = "availableGroupList";
	@FieldDescribe("可见应用的群组,目前无效,没有对组织的设置接口.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ availableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ availableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + availableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + availableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableGroupList;

	public static final String applicationCategory_FIELDNAME = "applicationCategory";
	@FieldDescribe("应用分类.")
	@Column(length = length_255B, name = ColumnNamePrefix + applicationCategory_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true)
	private String applicationCategory;

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String icon;

	public static final String iconHue_FIELDNAME = "iconHue";
	@FieldDescribe("icon的主色调")
	@Column(length = JpaObject.length_8B, name = ColumnNamePrefix + iconHue_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String iconHue;

	public static final String controllerList_FIELDNAME = "controllerList";
	@FieldDescribe("应用管理者.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ controllerList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + controllerList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + controllerList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + controllerList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> controllerList;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("应用的创建者。")
	@CheckPersist(allowEmpty = true)
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	private String creatorPerson;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("应用的最后修改时间。")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	private Date lastUpdateTime;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("应用的最后修改者")
	@CheckPersist(allowEmpty = true)
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	private String lastUpdatePerson;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ApplicationProperties properties;

	public String getApplicationCategory() {
		return Objects.toString(this.applicationCategory, "");
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

	public List<String> getAvailableIdentityList() {
		return availableIdentityList;
	}

	public void setAvailableIdentityList(List<String> availableIdentityList) {
		this.availableIdentityList = availableIdentityList;
	}

	public List<String> getAvailableUnitList() {
		return availableUnitList;
	}

	public void setAvailableUnitList(List<String> availableUnitList) {
		this.availableUnitList = availableUnitList;
	}

	public void setApplicationCategory(String applicationCategory) {
		this.applicationCategory = applicationCategory;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<String> getControllerList() {
		return controllerList;
	}

	public void setControllerList(List<String> controllerList) {
		this.controllerList = controllerList;
	}

	public static String getTable() {
		return TABLE;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public String getIconHue() {
		return iconHue;
	}

	public void setIconHue(String iconHue) {
		this.iconHue = iconHue;
	}

	public List<String> getAvailableGroupList() {
		return availableGroupList;
	}

	public void setAvailableGroupList(List<String> availableGroupList) {
		this.availableGroupList = availableGroupList;
	}
}
