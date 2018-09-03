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
	@Flag
	@FieldDescribe("应用名称")
	@Column(name = "xappName", length = JpaObject.length_96B)
	@CheckPersist(citationNotExists = {
			/* 验证不可重名 */
			@CitationNotExist(fields = "appName", type = AppInfo.class) }, allowEmpty = true)
	private String appName;

	@Flag
	@FieldDescribe("应用别名")
	@Column(name = "xappAlias", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String appAlias;

	@FieldDescribe("默认文档类型：信息 | 数据")
	@Column(name = "xdocumentType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String documentType = "信息";

	@FieldDescribe("应用信息排序号")
	@Column(name = "xappInfoSeq", length = JpaObject.length_96B)
	private String appInfoSeq;

	@FieldDescribe("应用信息说明")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description;

	@FieldDescribe("图标icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "xappIcon", length = JpaObject.length_1M)
	@CheckPersist(allowEmpty = true)
	private String appIcon;

	@FieldDescribe("图标主色调.")
	@Column(name = "xiconColor", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String iconColor;

	@FieldDescribe("备注信息")
	@Column(name = "xappMemo", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String appMemo;

	@FieldDescribe("创建人，可能为空，如果由系统创建。")
	@Column(name = "xcreatorPerson", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorPerson")
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	@FieldDescribe("创建人Identity，可能为空，如果由系统创建。")
	@Column(name = "xcreatorIdentity", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorIdentity")
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	@FieldDescribe("创建人组织，可能为空，如果由系统创建。")
	@Column(name = "xcreatorUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorUnitName")
	@CheckPersist(allowEmpty = true)
	private String creatorUnitName;

	@FieldDescribe("创建人顶层组织，可能为空，如果由系统创建。")
	@Column(name = "xcreatorTopUnitName", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_creatorTopUnitName")
	@CheckPersist(allowEmpty = true)
	private String creatorTopUnitName;

	@FieldDescribe("分类列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_categoryList", joinIndex = @Index(name = TABLE + "_categoryList_join"))
	@ElementColumn(length = JpaObject.length_id)
	@ElementIndex(name = TABLE + "_categoryList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> categoryList;

	@FieldDescribe("可见范围为所有人可见.")
	@Column(name = "xallPeopleView")
	@CheckPersist(allowEmpty = true)
	private Boolean allPeopleView = true;

	@FieldDescribe("发布范围栏目为所有人可发布.")
	@Column(name = "xallPeoplePublish")
	@CheckPersist(allowEmpty = true)
	private Boolean allPeoplePublish = true;

	@FieldDescribe("发布可见人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_viewablePersonList", joinIndex = @Index(name = TABLE + "_viewablePersonList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xviewablePersonList")
	@ElementIndex(name = TABLE + "_viewablePersonList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> viewablePersonList;

	@FieldDescribe("发布可见组织")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_viewableUnitList", joinIndex = @Index(name = TABLE + "_viewableUnitList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xviewableUnitList")
	@ElementIndex(name = TABLE + "_viewableUnitList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> viewableUnitList;

	@FieldDescribe("发布可见群组")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_viewableGroupList", joinIndex = @Index(name = TABLE + "_viewableGroupList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xviewableGroupList")
	@ElementIndex(name = TABLE + "_viewableGroupList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> viewableGroupList;

	@FieldDescribe("可发布人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_publishablePersonList", joinIndex = @Index(name = TABLE + "_publishablePersonList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xpublishablePersonList")
	@ElementIndex(name = TABLE + "_publishablePersonList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> publishablePersonList;

	@FieldDescribe("可发布组织")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_publishableUnitList", joinIndex = @Index(name = TABLE + "_publishableUnitList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xpublishableUnitList")
	@ElementIndex(name = TABLE + "_publishableUnitList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> publishableUnitList;

	@FieldDescribe("可发布群组")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_publishableGroupList", joinIndex = @Index(name = TABLE + "_publishableGroupList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xpublishableGroupList")
	@ElementIndex(name = TABLE + "_publishableGroupList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> publishableGroupList;

	@FieldDescribe("栏目可管理人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_manageablePersonList", joinIndex = @Index(name = TABLE + "_manageablePersonList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xmanageablePersonList")
	@ElementIndex(name = TABLE + "_manageablePersonList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> manageablePersonList;

	@FieldDescribe("栏目可管理组织")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_manageableUnitList", joinIndex = @Index(name = TABLE + "_manageableUnitList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xmanageableUnitList")
	@ElementIndex(name = TABLE + "_manageableUnitList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> manageableUnitList;

	@FieldDescribe("栏目可管理群组")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_manageableGroupList", joinIndex = @Index(name = TABLE + "_manageableGroupList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xmanageableGroupList")
	@ElementIndex(name = TABLE + "_manageableGroupList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> manageableGroupList;

	/**
	 * 获取应用名称
	 * 
	 * @return
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * 设置应用名称
	 * 
	 * @return
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * 获取应用别名
	 * 
	 * @return
	 */
	public String getAppAlias() {
		return appAlias;
	}

	/**
	 * 设置应用别名
	 * 
	 * @return
	 */
	public void setAppAlias(String appAlias) {
		this.appAlias = appAlias;
	}

	/**
	 * 获取应用排序号
	 * 
	 * @return
	 */
	public String getAppInfoSeq() {
		return appInfoSeq;
	}

	/**
	 * 设置应用排序号
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
	 * 获取应用说明
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置应用说明
	 * 
	 * @return
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取应用图标访问路径
	 * 
	 * @return
	 */
	public String getAppIcon() {
		return appIcon;
	}

	/**
	 * 设置应用图标访问路径
	 * 
	 * @return
	 */
	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}

	/**
	 * 获取应用信息备注
	 * 
	 * @return
	 */
	public String getAppMemo() {
		return appMemo;
	}

	/**
	 * 设置应用信息备注
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