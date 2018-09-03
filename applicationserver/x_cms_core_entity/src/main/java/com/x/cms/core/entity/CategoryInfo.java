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
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;

/**
 * 内容管理应用目录分类信息
 * 
 * @author 李义
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.CategoryInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.CategoryInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CategoryInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.CategoryInfo.table;

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
	@FieldDescribe("分类名称")
	@Column(name = "xcategoryName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String categoryName;

	@FieldDescribe("分类所属应用ID")
	@Column(name = "xappId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String appId;

	@FieldDescribe("应用名称")
	@Column(name = "xappName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String appName;

	@FieldDescribe("默认文档类型：信息 | 数据")
	@Column(name = "xdocumentType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String documentType = "信息";

	@FieldDescribe("上级分类ID")
	@Column(name = "xparentId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String parentId;

	@FieldDescribe("分类别名")
	@Column(name = "xcategoryAlias", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String categoryAlias;

	@FieldDescribe("流程类型：禁用审批流|自由审批流|固定审批流")
	@Column(name = "xworkflowType", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = true)
	private String workflowType = "禁用审批流";

	@FieldDescribe("流程应用ID")
	@Column(name = "xworkflowAppId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String workflowAppId;

	@FieldDescribe("流程应用名称")
	@Column(name = "xworkflowAppName", length = JpaObject.length_128B)
	@CheckPersist(allowEmpty = true)
	private String workflowAppName;

	@FieldDescribe("流程名称")
	@Column(name = "xworkflowName", length = JpaObject.length_128B)
	@CheckPersist(allowEmpty = true)
	private String workflowName;

	@FieldDescribe("流程ID")
	@Column(name = "xworkflowFlag", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String workflowFlag;

	@FieldDescribe("绑定的编辑表单模板ID")
	@Column(name = "xformId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String formId;

	@FieldDescribe("绑定的编辑表单模板名称")
	@Column(name = "xformName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String formName;

	@FieldDescribe("绑定的阅读表单模板ID")
	@Column(name = "xreadFormId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String readFormId;

	@FieldDescribe("绑定的阅读表单模板名称")
	@Column(name = "xreadFormName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String readFormName;

	@FieldDescribe("默认视图ID")
	@Column(name = "xdefaultViewId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String defaultViewId;

	@FieldDescribe("默认视图名称")
	@Column(name = "xdefaultViewName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = true)
	private String defaultViewName;

	@FieldDescribe("分类信息排序号")
	@Column(name = "xcategorySeq", length = JpaObject.length_96B)
	private String categorySeq;

	@FieldDescribe("分类信息说明")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description;

	@FieldDescribe("图标icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "xcategoryIcon", length = JpaObject.length_32K)
	private String categoryIcon;

	@FieldDescribe("备注信息")
	@Column(name = "xcategoryMemo", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String categoryMemo;

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

	@FieldDescribe("可见范围为所有人可见.")
	@Column(name = "xallPeopleView")
	@CheckPersist(allowEmpty = true)
	private Boolean allPeopleView = true;

	@FieldDescribe("发布范围栏目为所有人可发布.")
	@Column(name = "xallPeoplePublish")
	@CheckPersist(allowEmpty = true)
	private Boolean allPeoplePublish = true;

	@FieldDescribe("是数据导入绑定的数据视图应用ID.")
	@Column(name = "ximportViewAppId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String importViewAppId = null;

	@FieldDescribe("是数据导入绑定的数据视图ID.")
	@Column(name = "ximportViewId", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String importViewId = null;

	@FieldDescribe("是数据导入绑定的数据视图名称.")
	@Column(name = "ximportViewName", length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	private String importViewName = null;

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

	@FieldDescribe("分类可管理人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_manageablePersonList", joinIndex = @Index(name = TABLE + "_manageablePersonList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xmanageablePersonList")
	@ElementIndex(name = TABLE + "_manageablePersonList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> manageablePersonList;

	@FieldDescribe("分类可管理组织")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_manageableUnitList", joinIndex = @Index(name = TABLE + "_manageableUnitList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xmanageableUnitList")
	@ElementIndex(name = TABLE + "_manageableUnitList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> manageableUnitList;

	@FieldDescribe("分类可管理群组")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE
			+ "_manageableGroupList", joinIndex = @Index(name = TABLE + "_manageableGroupList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xmanageableGroupList")
	@ElementIndex(name = TABLE + "_manageableGroupList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> manageableGroupList;

	/**
	 * 获取分类名称
	 * 
	 * @return
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * 设置分类名称
	 * 
	 * @param categoryName
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * 获取分类所属应用ID
	 * 
	 * @return
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * 设置分类所属应用ID
	 * 
	 * @param appId
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * 获取上级分类ID
	 * 
	 * @return
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * 设置上级分类ID
	 * 
	 * @param parentId
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * 获取分类别名
	 * 
	 * @return
	 */
	public String getCategoryAlias() {
		return categoryAlias;
	}

	/**
	 * 设置分类别名
	 * 
	 * @param categoryAlias
	 */
	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}

	/**
	 * 获取分类信息排序号
	 * 
	 * @return
	 */
	public String getCategorySeq() {
		return categorySeq;
	}

	/**
	 * 设置分类排序号
	 * 
	 * @param categorySeq
	 */
	public void setCategorySeq(String categorySeq) {
		try {
			if (Integer.parseInt(categorySeq) < 10) {
				this.categorySeq = "0" + Integer.parseInt(categorySeq);
			} else {
				this.categorySeq = categorySeq;
			}
		} catch (Exception e) {
			this.categorySeq = "999";
		}
	}

	/**
	 * 获取分类说明信息
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置分类说明信息
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取分类图标
	 * 
	 * @return
	 */
	public String getCategoryIcon() {
		return categoryIcon;
	}

	/**
	 * 设置分类图标
	 * 
	 * @param categoryIcon
	 */
	public void setCategoryIcon(String categoryIcon) {
		this.categoryIcon = categoryIcon;
	}

	/**
	 * 获取分类备注
	 * 
	 * @return
	 */
	public String getCategoryMemo() {
		return categoryMemo;
	}

	/**
	 * 设置分类备注
	 * 
	 * @param categoryMemo
	 */
	public void setCategoryMemo(String categoryMemo) {
		this.categoryMemo = categoryMemo;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
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

	public String getReadFormId() {
		return readFormId;
	}

	public void setReadFormId(String readFormId) {
		this.readFormId = readFormId;
	}

	public String getReadFormName() {
		return readFormName;
	}

	public void setReadFormName(String readFormName) {
		this.readFormName = readFormName;
	}

	public String getDefaultViewId() {
		return defaultViewId;
	}

	public String getDefaultViewName() {
		return defaultViewName;
	}

	public void setDefaultViewId(String defaultViewId) {
		this.defaultViewId = defaultViewId;
	}

	public void setDefaultViewName(String defaultViewName) {
		this.defaultViewName = defaultViewName;
	}

	public String getWorkflowType() {
		return workflowType;
	}

	public String getWorkflowAppName() {
		return workflowAppName;
	}

	public String getWorkflowFlag() {
		return workflowFlag;
	}

	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}

	public void setWorkflowAppName(String workflowAppName) {
		this.workflowAppName = workflowAppName;
	}

	public void setWorkflowFlag(String workflowFlag) {
		this.workflowFlag = workflowFlag;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getWorkflowAppId() {
		return workflowAppId;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowAppId(String workflowAppId) {
		this.workflowAppId = workflowAppId;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getCreatorUnitName() {
		return creatorUnitName;
	}

	public void setCreatorUnitName(String creatorUnitName) {
		this.creatorUnitName = creatorUnitName;
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
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
	}

	public void setPublishableUnitList(List<String> publishableUnitList) {
		this.publishableUnitList = publishableUnitList;
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
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
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
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

	public String getImportViewId() {
		return importViewId;
	}

	public void setImportViewId(String importViewId) {
		this.importViewId = importViewId;
	}

	public String getImportViewName() {
		return importViewName;
	}

	public void setImportViewName(String importViewName) {
		this.importViewName = importViewName;
	}

	public String getImportViewAppId() {
		return importViewAppId;
	}

	public void setImportViewAppId(String importViewAppId) {
		this.importViewAppId = importViewAppId;
	}
}