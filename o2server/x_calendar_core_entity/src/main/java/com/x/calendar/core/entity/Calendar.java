package com.x.calendar.core.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 日历信息
 * 
 * @author O2LEE
 *
 */
@Schema(name = "Calendar", description = "日程日历信息.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Calendar.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Calendar.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Calendar extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Calendar.table;

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
	public static final String name_FIELDNAME = "name";
	@FieldDescribe("日历名称")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("日历类别: PERSON | UNIT")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + type_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String target_FIELDNAME = "target";
	@FieldDescribe("人员标识或者组织标识")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + target_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + target_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String target;

	public static final String color_FIELDNAME = "color";
	@FieldDescribe("显示颜色")
	@Column(length = JpaObject.length_8B, name = ColumnNamePrefix + color_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String color = "#1462be";

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("备注70个字")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String source_FIELDNAME = "source";
	@FieldDescribe("信息来源: LEADER | PERSON | UNIT | MEETING | BUSINESS_TRIP | HOLIDAY")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + source_FIELDNAME)
	private String source;

	public static final String createor_FIELDNAME = "createor";
	@FieldDescribe("创建者")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ createor_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + createor_FIELDNAME)
	private String createor = null;

	public static final String isPublic_FIELDNAME = "isPublic";
	@FieldDescribe("是否公开的日历")
	@Column(name = ColumnNamePrefix + isPublic_FIELDNAME)
	private Boolean isPublic = false;

	public static final String manageablePersonList_FIELDNAME = "manageablePersonList";
	@FieldDescribe("可管理人员列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ manageablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ manageablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ manageablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + manageablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> manageablePersonList;

	public static final String followers_FIELDNAME = "followers";
	@FieldDescribe("关注者列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + followers_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + followers_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ followers_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + followers_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> followers;

	public static final String viewablePersonList_FIELDNAME = "viewablePersonList";
	@FieldDescribe("可见人员列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ viewablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ viewablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ viewablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + viewablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> viewablePersonList;

	public static final String viewableUnitList_FIELDNAME = "viewableUnitList";
	@FieldDescribe("可见组织列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ viewableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + viewableUnitList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ viewableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + viewableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> viewableUnitList;

	public static final String viewableGroupList_FIELDNAME = "viewableGroupList";
	@FieldDescribe("可见群组列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ viewableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ viewableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ viewableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + viewableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> viewableGroupList;

	public static final String publishablePersonList_FIELDNAME = "publishablePersonList";
	@FieldDescribe("可发布人员列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ publishablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ publishablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ publishablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + publishablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> publishablePersonList;

	public static final String publishableUnitList_FIELDNAME = "publishableUnitList";
	@FieldDescribe("可发布组织列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ publishableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ publishableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ publishableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + publishableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> publishableUnitList;

	public static final String publishableGroupList_FIELDNAME = "publishableGroupList";
	@FieldDescribe("可发布群组列表")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ publishableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ publishableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ publishableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + publishableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> publishableGroupList;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("日历状态：OPEN|CLOSE")
	@Index(name = TABLE + IndexNameMiddle + status_FIELDNAME)
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status = "OPEN";

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getColor() {
		return color;
	}

	public String getDescription() {
		return description;
	}

	public String getSource() {
		return source;
	}

	public String getCreateor() {
		return createor;
	}

	public List<String> getManageablePersonList() {
		if (this.manageablePersonList == null) {
			this.manageablePersonList = new ArrayList<>();
		}
		return manageablePersonList;
	}

	public List<String> getViewablePersonList() {
		if (this.viewablePersonList == null) {
			this.viewablePersonList = new ArrayList<>();
		}
		return viewablePersonList;
	}

	public List<String> getViewableUnitList() {
		if (this.viewableUnitList == null) {
			this.viewableUnitList = new ArrayList<>();
		}
		return viewableUnitList;
	}

	public List<String> getViewableGroupList() {
		if (this.viewableGroupList == null) {
			this.viewableGroupList = new ArrayList<>();
		}
		return viewableGroupList;
	}

	public List<String> getPublishablePersonList() {
		if (this.publishablePersonList == null) {
			this.publishablePersonList = new ArrayList<>();
		}
		return publishablePersonList;
	}

	public List<String> getPublishableUnitList() {
		if (this.publishableUnitList == null) {
			this.publishableUnitList = new ArrayList<>();
		}
		return publishableUnitList;
	}

	public List<String> getPublishableGroupList() {
		if (this.publishableGroupList == null) {
			this.publishableGroupList = new ArrayList<>();
		}
		return publishableGroupList;
	}

	public String getStatus() {
		return status;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setCreateor(String createor) {
		this.createor = createor;
	}

	public void setManageablePersonList(List<String> manageablePersonList) {
		this.manageablePersonList = manageablePersonList;
	}

	public void setViewablePersonList(List<String> viewablePersonList) {
		this.viewablePersonList = viewablePersonList;
	}

	public void setViewableUnitList(List<String> viewableUnitList) {
		this.viewableUnitList = viewableUnitList;
	}

	public void setViewableGroupList(List<String> viewableGroupList) {
		this.viewableGroupList = viewableGroupList;
	}

	public void setPublishablePersonList(List<String> publishablePersonList) {
		this.publishablePersonList = publishablePersonList;
	}

	public void setPublishableUnitList(List<String> publishableUnitList) {
		this.publishableUnitList = publishableUnitList;
	}

	public void setPublishableGroupList(List<String> publishableGroupList) {
		this.publishableGroupList = publishableGroupList;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public List<String> getFollowers() {
		return followers;
	}

	public void setFollowers(List<String> followers) {
		this.followers = followers;
	}

	public List<String> addFollower(String followerName) {
		this.followers = addStringToList(followerName, this.followers);
		return this.followers;
	}

	public List<String> addViewPerson(String viewPerson) {
		this.viewablePersonList = addStringToList(viewPerson, this.viewablePersonList);
		return this.viewablePersonList;
	}

	public List<String> addStringToList(String stringValue, List<String> stringList) {
		if (stringList == null) {
			stringList = new ArrayList<>();
		}
		if (!stringList.contains(stringValue)) {
			stringList.add(stringValue);
		}
		return stringList;
	}

}