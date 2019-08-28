package com.x.cms.core.entity;

import java.util.ArrayList;
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
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;

/**
 * 栏目信息
 * @author O2LEE
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AppInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.AppInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AppInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AppInfo.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Flag
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	public void onPersist() throws Exception {
	}
	/*
	 * =========================================================================
	 * ========= 以上为 JpaObject 默认字段
	 * =========================================================================
	 * =========
	 */

	/*
	 * =========================================================================
	 * ========= 以下为具体不同的业务及数据表字段要求
	 * =========================================================================
	 * =========
	 */

	/* 更新运行方法 */
	// public static String[] FLA GS = new String[] { "id", "appAlias", "appName" };
	public static final String appName_FIELDNAME = "appName";
	@Flag
	@FieldDescribe("栏目名称")
	@Column( length = JpaObject.length_96B, name = ColumnNamePrefix + appName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appName_FIELDNAME)
	@CheckPersist(citationNotExists = {
			/* 验证不可重名 */
			@CitationNotExist(fields = "appName", type = AppInfo.class) }, allowEmpty = true)
	private String appName;

	public static final String appAlias_FIELDNAME = "appAlias";
	@Flag
	@FieldDescribe("栏目别名")
	@Column( length = JpaObject.length_96B, name = ColumnNamePrefix + appAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appAlias;

	public static final String appType_FIELDNAME = "appType";
	@FieldDescribe("栏目类别")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + appType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appType = "未分类";
	
	public static final String documentType_FIELDNAME = "documentType";
	@FieldDescribe("默认文档类型：信息 | 数据")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + documentType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + documentType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String documentType = "信息";

	public static final String appInfoSeq_FIELDNAME = "appInfoSeq";
	@FieldDescribe("栏目信息排序号")
	@Column( length = JpaObject.length_96B, name = ColumnNamePrefix + appInfoSeq_FIELDNAME)
	private String appInfoSeq;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("栏目信息说明")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String appIcon_FIELDNAME = "appIcon";
	@FieldDescribe("图标icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_1M, name = ColumnNamePrefix + appIcon_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appIcon;

	public static final String iconColor_FIELDNAME = "iconColor";
	@FieldDescribe("图标主色调.")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + iconColor_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String iconColor;

	public static final String appMemo_FIELDNAME = "appMemo";
	@FieldDescribe("备注信息")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + appMemo_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appMemo;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("创建人，可能为空，如果由系统创建。")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("创建人Identity，可能为空，如果由系统创建。")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	public static final String creatorUnitName_FIELDNAME = "creatorUnitName";
	@FieldDescribe("创建人组织，可能为空，如果由系统创建。")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + creatorUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnitName;

	public static final String creatorTopUnitName_FIELDNAME = "creatorTopUnitName";
	@FieldDescribe("创建人顶层组织，可能为空，如果由系统创建。")
	@Column( length =AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + creatorTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorTopUnitName;
	
	public static final String anonymousAble_FIELDNAME = "anonymousAble";
	@FieldDescribe("是否允许匿名访问.")
	@Column( name = ColumnNamePrefix + anonymousAble_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + anonymousAble_FIELDNAME)
	private Boolean anonymousAble = true;
	
	public static final String allPeopleView_FIELDNAME = "allPeopleView";
	@FieldDescribe("可见范围为所有人可见.")
	@Column( name = ColumnNamePrefix + allPeopleView_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + allPeopleView_FIELDNAME)
	private Boolean allPeopleView = true;

	public static final String allPeoplePublish_FIELDNAME = "allPeoplePublish";
	@FieldDescribe("发布范围栏目为所有人可发布.")
	@Column( name = ColumnNamePrefix + allPeoplePublish_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + allPeoplePublish_FIELDNAME)
	private Boolean allPeoplePublish = true;
	
	public static final String categoryList_FIELDNAME = "categoryList";
	@FieldDescribe("分类列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + categoryList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + categoryList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + categoryList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + categoryList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> categoryList;

	public static final String viewablePersonList_FIELDNAME = "viewablePersonList";
	@FieldDescribe("发布可见人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + viewablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + viewablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + viewablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + viewablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> viewablePersonList;

	public static final String viewableUnitList_FIELDNAME = "viewableUnitList";
	@FieldDescribe("发布可见组织")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + viewableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + viewableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + viewableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + viewableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> viewableUnitList;

	public static final String viewableGroupList_FIELDNAME = "viewableGroupList";
	@FieldDescribe("发布可见群组")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + viewableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + viewableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + viewableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + viewableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> viewableGroupList;

	public static final String publishablePersonList_FIELDNAME = "publishablePersonList";
	@FieldDescribe("可发布人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + publishablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + publishablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + publishablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + publishablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> publishablePersonList;

	public static final String publishableUnitList_FIELDNAME = "publishableUnitList";
	@FieldDescribe("可发布组织")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + publishableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + publishableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + publishableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + publishableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> publishableUnitList;

	public static final String publishableGroupList_FIELDNAME = "publishableGroupList";
	@FieldDescribe("可发布群组")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + publishableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + publishableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + publishableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + publishableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> publishableGroupList;

	public static final String manageablePersonList_FIELDNAME = "manageablePersonList";
	@FieldDescribe("栏目可管理人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + manageablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + manageablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + manageablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + manageablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> manageablePersonList;

	public static final String manageableUnitList_FIELDNAME = "manageableUnitList";
	@FieldDescribe("栏目可管理组织")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + manageableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + manageableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + manageableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + manageableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> manageableUnitList;

	public static final String manageableGroupList_FIELDNAME = "manageableGroupList";
	@FieldDescribe("栏目可管理群组")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + manageableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + manageableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + manageableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + manageableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> manageableGroupList;

	
	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	/**
	 * 获取栏目名称
	 * 
	 * @return
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * 设置栏目名称
	 * 
	 * @return
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * 获取栏目别名
	 * 
	 * @return
	 */
	public String getAppAlias() {
		return appAlias;
	}

	/**
	 * 设置栏目别名
	 * 
	 * @return
	 */
	public void setAppAlias(String appAlias) {
		this.appAlias = appAlias;
	}

	/**
	 * 获取栏目排序号
	 * 
	 * @return
	 */
	public String getAppInfoSeq() {
		return appInfoSeq;
	}

	/**
	 * 设置栏目排序号
	 * 
	 * @return
	 */
	public void setAppInfoSeq(String appInfoSeq) {
		try {
			if (Integer.parseInt(appInfoSeq) < 10) {
				this.appInfoSeq = "0" + Integer.parseInt(appInfoSeq);
			} else {
				this.appInfoSeq = appInfoSeq;
			}
		} catch (Exception e) {
			this.appInfoSeq = "999";
		}
	}

	/**
	 * 获取栏目说明
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置栏目说明
	 * 
	 * @return
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取栏目图标访问路径
	 * 
	 * @return
	 */
	public String getAppIcon() {
		return appIcon;
	}

	/**
	 * 设置栏目图标访问路径
	 * 
	 * @return
	 */
	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}

	/**
	 * 获取栏目信息备注
	 * 
	 * @return
	 */
	public String getAppMemo() {
		return appMemo;
	}

	/**
	 * 设置栏目信息备注
	 * 
	 * @return
	 */
	public void setAppMemo(String appMemo) {
		this.appMemo = appMemo;
	}

	public List<String> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<String> categoryList) {
		this.categoryList = categoryList;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public String getCreatorIdentity() {
		return creatorIdentity;
	}

	public void setCreatorIdentity(String creatorIdentity) {
		this.creatorIdentity = creatorIdentity;
	}

	public String getCreatorUnitName() {
		return creatorUnitName;
	}

	public void setCreatorUnitName(String creatorUnitName) {
		this.creatorUnitName = creatorUnitName;
	}

	public String getIconColor() {
		return iconColor;
	}

	public void setIconColor(String iconColor) {
		this.iconColor = iconColor;
	}

	public String getCreatorTopUnitName() {
		return creatorTopUnitName;
	}

	public void setCreatorTopUnitName(String creatorTopUnitName) {
		this.creatorTopUnitName = creatorTopUnitName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public List<String> getViewablePersonList() {
		return viewablePersonList == null ? new ArrayList<>() : viewablePersonList;
	}

	public List<String> getViewableUnitList() {
		return viewableUnitList == null ? new ArrayList<>() : viewableUnitList;
	}

	public List<String> getViewableGroupList() {
		return viewableGroupList == null ? new ArrayList<>() : viewableGroupList;
	}

	public List<String> getPublishablePersonList() {
		return publishablePersonList == null ? new ArrayList<>() : publishablePersonList;
	}

	public List<String> getPublishableUnitList() {
		return publishableUnitList == null ? new ArrayList<>() : publishableUnitList;
	}

	public List<String> getPublishableGroupList() {
		return publishableGroupList == null ? new ArrayList<>() : publishableGroupList;
	}

	public List<String> getManageablePersonList() {
		return manageablePersonList == null ? new ArrayList<>() : manageablePersonList;
	}

	public List<String> getManageableUnitList() {
		return manageableUnitList == null ? new ArrayList<>() : manageableUnitList;
	}

	public List<String> getManageableGroupList() {
		return manageableGroupList == null ? new ArrayList<>() : manageableGroupList;
	}

	public void setViewablePersonList(List<String> viewablePersonList) {
		this.viewablePersonList = viewablePersonList;
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
	}

	public void setViewableUnitList(List<String> viewableUnitList) {
		this.viewableUnitList = viewableUnitList;
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
	}

	public void setViewableGroupList(List<String> viewableGroupList) {
		this.viewableGroupList = viewableGroupList;
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
	}

	public void setPublishablePersonList(List<String> publishablePersonList) {
		this.publishablePersonList = publishablePersonList;
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
	}

	public void setPublishableUnitList(List<String> publishableUnitList) {
		this.publishableUnitList = publishableUnitList;
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
	}

	public void setPublishableGroupList(List<String> publishableGroupList) {
		this.publishableGroupList = publishableGroupList;
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
	}

	public void setManageablePersonList(List<String> manageablePersonList) {
		this.manageablePersonList = manageablePersonList;
	}

	public void setManageableUnitList(List<String> manageableUnitList) {
		this.manageableUnitList = manageableUnitList;
	}

	public void setManageableGroupList(List<String> manageableGroupList) {
		this.manageableGroupList = manageableGroupList;
	}

	public void addViewablePerson(String personName) {
		addStringToList(this.viewablePersonList, personName);
	}

	public void addViewableUnit(String unitName) {
		addStringToList(this.viewableUnitList, unitName);
	}

	public void addViewableGroup(String groupName) {
		addStringToList(this.viewableGroupList, groupName);
	}

	public void addPublishablePerson(String personName) {
		addStringToList(this.publishablePersonList, personName);
	}

	public void addPublishableUnit(String unitName) {
		addStringToList(this.publishableUnitList, unitName);
	}

	public void addPublishableGroup(String groupName) {
		addStringToList(this.publishableGroupList, groupName);
	}

	public void addManageablePerson(String personName) {
		addStringToList(this.manageablePersonList, personName);
	}

	public void addManageableUnit(String unitName) {
		addStringToList(this.manageableUnitList, unitName);
	}

	public void addManageableGroup(String groupName) {
		addStringToList(this.manageableGroupList, groupName);
	}

	public void removeViewablePerson(String personName) {
		removeStringFromList(this.viewablePersonList, personName);
	}

	public void removeViewableUnit(String unitName) {
		removeStringFromList(this.viewableUnitList, unitName);
	}

	public void removeViewableGroup(String groupName) {
		removeStringFromList(this.viewableGroupList, groupName);
	}

	public void removePublishablePerson(String personName) {
		removeStringFromList(this.publishablePersonList, personName);
	}

	public void removePublishableUnit(String unitName) {
		removeStringFromList(this.publishableUnitList, unitName);
	}

	public void removePublishableGroup(String groupName) {
		removeStringFromList(this.publishableGroupList, groupName);
	}

	public void removeManageablePerson(String personName) {
		removeStringFromList(this.manageablePersonList, personName);
	}

	public void removeManageableUnit(String unitName) {
		removeStringFromList(this.manageableUnitList, unitName);
	}

	public void removeManageableGroup(String groupName) {
		removeStringFromList(this.manageableGroupList, groupName);
	}

	public Boolean getAnonymousAble() {
		return anonymousAble;
	}

	public void setAnonymousAble(Boolean anonymousAble) {
		this.anonymousAble = anonymousAble;
	}

	private List<String> addStringToList(List<String> sourceList, String targetString) {
		if (sourceList == null) {
			sourceList = new ArrayList<>();
		}
		if (!sourceList.contains(targetString)) {
			sourceList.add(targetString);
		}
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
		return sourceList;
	}

	private List<String> removeStringFromList(List<String> sourceList, String targetString) {
		if (sourceList == null) {
			sourceList = new ArrayList<>();
		}
		if (sourceList.contains(targetString)) {
			sourceList.remove(targetString);
		}
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
		return sourceList;
	}

	public Boolean getAllPeopleView() {
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			allPeopleView = true;
		} else {
			allPeopleView = false;
		}
		return allPeopleView;
	}

	public Boolean getAllPeoplePublish() {
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
		return allPeoplePublish;
	}

	public void setAllPeopleView(Boolean allPeopleView) {
		this.allPeopleView = allPeopleView;
	}

	public void setAllPeoplePublish(Boolean allPeoplePublish) {
		this.allPeoplePublish = allPeoplePublish;
	}

}