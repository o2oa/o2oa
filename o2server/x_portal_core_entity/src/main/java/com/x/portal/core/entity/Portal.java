package com.x.portal.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.persistence.OrderColumn;

import com.x.base.core.entity.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.*;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.jdbc.Index;

/**
 * 门户平台应用
 * @author sword
 */
@Schema(name = "Portal", description = "门户.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Portal.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Portal.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Portal extends SliceJpaObject {

	private static final long serialVersionUID = -7520516033901189347L;
	private static final String TABLE = PersistenceProperties.Portal.table;
	public static final String CATEGORY_DEFAULT = "未分类";

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
		this.portalCategory = StringUtils.isBlank(this.portalCategory) ? CATEGORY_DEFAULT : StringUtils.trimToEmpty(this.portalCategory);
		this.firstPage = StringUtils.trimToEmpty(this.firstPage);

	}

	@PostLoad
	public void postLoad() {
		this.portalCategory = StringUtils.isBlank(this.portalCategory) ? CATEGORY_DEFAULT : this.portalCategory;
		this.cornerMarkScript = this.getProperties().getCornerMarkScript();
		this.cornerMarkScriptText = this.getProperties().getCornerMarkScriptText();
	}

	/* flag标志位 */
	/* Entity 默认字段结束 */

	public static final String name_FIELDNAME = "name";
	@Flag
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
	@CitationNotExist(type = Portal.class, fields = { JpaObject.id_FIELDNAME, "name", "alias" }))
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	@CitationNotExist(type = Portal.class, fields = { "id", "name", "alias" }))
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String availableIdentityList_FIELDNAME = "availableIdentityList";
	@FieldDescribe("可访问的身份.")
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
	@FieldDescribe("可访问的组织.")
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
	@FieldDescribe("可访问的群组.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ availableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
			+ availableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + availableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + availableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableGroupList;

	public static final String portalCategory_FIELDNAME = "portalCategory";
	@FieldDescribe("应用分类.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + portalCategory_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + portalCategory_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true)
	private String portalCategory;

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String icon;

	public static final String firstPage_FIELDNAME = "firstPage";
	@FieldDescribe("默认首页.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + firstPage_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String firstPage;

	public static final String controllerList_FIELDNAME = "controllerList";
	@FieldDescribe("应用管理者。")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + controllerList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + controllerList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + controllerList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + controllerList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> controllerList;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("应用的创建者。")
	@CheckPersist(allowEmpty = false)
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME)
	private String creatorPerson;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("应用的最后修改时间。")
	@CheckPersist(allowEmpty = false)
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	private Date lastUpdateTime;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("应用的最后修改者")
	@CheckPersist(allowEmpty = false)
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdatePerson_FIELDNAME)
	private String lastUpdatePerson;

	public static final String pcClient_FIELDNAME = "pcClient";
	@FieldDescribe("是否在pc终端显示.")
	@Column(name = ColumnNamePrefix + pcClient_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean pcClient;

	public static final String mobileClient_FIELDNAME = "mobileClient";
	@FieldDescribe("是否在移动设备显示.")
	@Column(name = ColumnNamePrefix + mobileClient_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean mobileClient;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private PortalProperties properties;

	/**
	 * 以下为非持久化属性，定义在PortalProperties中
	 */
	public static final String cornerMarkScript_FIELDNAME = "cornerMarkScript";
	@FieldDescribe("角标关联门户脚本.")
	@Transient
	private String cornerMarkScript;

	public static final String cornerMarkScriptText_FIELDNAME = "cornerMarkScriptText";
	@FieldDescribe("角标脚本文本.")
	@Transient
	private String cornerMarkScriptText;

	public String getCornerMarkScript() {
		return cornerMarkScript;
	}

	public void setCornerMarkScript(String cornerMarkScript) {
		this.getProperties().setCornerMarkScript(cornerMarkScript);
		this.cornerMarkScript = cornerMarkScript;
	}

	public String getCornerMarkScriptText() {
		return cornerMarkScriptText;
	}

	public void setCornerMarkScriptText(String cornerMarkScriptText) {
		this.getProperties().setCornerMarkScriptText(cornerMarkScriptText);
		this.cornerMarkScriptText = cornerMarkScriptText;
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

	public String getPortalCategory() {
		return portalCategory;
	}

	public void setPortalCategory(String portalCategory) {
		this.portalCategory = portalCategory;
	}

	public String getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(String firstPage) {
		this.firstPage = firstPage;
	}

	public List<String> getAvailableUnitList() {
		return availableUnitList;
	}

	public void setAvailableUnitList(List<String> availableUnitList) {
		this.availableUnitList = availableUnitList;
	}

	public Boolean getPcClient() {
		return pcClient;
	}

	public void setPcClient(Boolean pcClient) {
		this.pcClient = pcClient;
	}

	public Boolean getMobileClient() {
		return mobileClient;
	}

	public void setMobileClient(Boolean mobileClient) {
		this.mobileClient = mobileClient;
	}

	public List<String> getAvailableGroupList() {
		return availableGroupList;
	}

	public void setAvailableGroupList(List<String> availableGroupList) {
		this.availableGroupList = availableGroupList;
	}

	public PortalProperties getProperties() {
		if (null == this.properties) {
			this.properties = new PortalProperties();
		}
		return this.properties;
	}

	public void setProperties(PortalProperties properties) {
		this.properties = properties;
	}
}
