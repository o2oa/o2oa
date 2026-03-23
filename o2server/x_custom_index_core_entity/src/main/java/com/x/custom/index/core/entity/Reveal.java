package com.x.custom.index.core.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.query.core.express.index.Directory;

@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@javax.persistence.Table(name = PersistenceProperties.Reveal.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Reveal.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Reveal extends SliceJpaObject {

	private static final long serialVersionUID = -5610293696763235753L;

	private static final String TABLE = PersistenceProperties.Reveal.TABLE;

	public Reveal() {
		this.properties = new RevealProperties();
	}

	@PostLoad
	public void postLoad() {
		if ((null != this.properties)) {
			this.processPlatformList = this.getProperties().getProcessPlatformList();
			this.cmsList = this.getProperties().getCmsList();
			this.data = this.getProperties().getData();
		}
	}

	public RevealProperties getProperties() {
		if (null == this.properties) {
			this.properties = new RevealProperties();
		}
		return this.properties;
	}

	public void setProperties(RevealProperties properties) {
		this.properties = properties;
	}

	public static final String DATA_FIELDNAME = "data";
	@Transient
	private JsonElement data;

	public static final String PROCESSPLATFORMLIST_FIELDNAME = "processPlatformList";
	@Transient
	private List<Directory> processPlatformList;

	public static final String CMSLIST_FIELDNAME = "cmsList";
	@Transient
	private List<Directory> cmsList;

	public List<Directory> getProcessPlatformList() {
		if (null != processPlatformList) {
			return processPlatformList;
		} else {
			return this.getProperties().getProcessPlatformList();
		}
	}

	public List<Directory> getCmsList() {
		if (null != cmsList) {
			return cmsList;
		} else {
			return this.getProperties().getCmsList();
		}
	}

	public JsonElement getData() {
		if (null != data) {
			return this.data;
		} else {
			return this.getProperties().getData();
		}
	}

	public void setData(JsonElement data) {
		this.data = data;
		this.getProperties().setData(data);
	}

	public void setProcessPlatformList(List<Directory> processPlatformList) {
		this.processPlatformList = processPlatformList;
		this.getProperties().setProcessPlatformList(processPlatformList);
	}

	public void setCmsList(List<Directory> cmsList) {
		this.cmsList = cmsList;
		this.getProperties().setCmsList(cmsList);
	}

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

	@Override
	public void onPersist() throws Exception {
		// nothing
	}

	public static final String NAME_FIELDNAME = "name";
	@Flag
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + NAME_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = false, citationNotExists = @CitationNotExist(type = Reveal.class, fields = {
			"name", "id" }))
	private String name;

	public static final String ENABLE_FIELDNAME = "enable";
	@FieldDescribe("是否启用.")
	@Column(name = ColumnNamePrefix + ENABLE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean enable;

	public static final String CREATORPERSON_FIELDNAME = "creatorPerson";
	@FieldDescribe("创建人.")
	@Column(length = length_255B, name = ColumnNamePrefix + CREATORPERSON_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + CREATORPERSON_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String AVAILABLEPERSONLIST_FIELDNAME = "availablePersonList";
	@FieldDescribe("可见人员.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ AVAILABLEPERSONLIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ AVAILABLEPERSONLIST_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + AVAILABLEPERSONLIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + AVAILABLEPERSONLIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availablePersonList;

	public static final String AVAILABLEUNITLIST_FIELDNAME = "availableUnitList";
	@FieldDescribe("可见组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ AVAILABLEUNITLIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ AVAILABLEUNITLIST_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + AVAILABLEUNITLIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + AVAILABLEUNITLIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableUnitList;

	public static final String AVAILABLEGROUPLIST_FIELDNAME = "availableGroupList";
	@FieldDescribe("可见群组.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ AVAILABLEGROUPLIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ AVAILABLEGROUPLIST_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + AVAILABLEGROUPLIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + AVAILABLEGROUPLIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableGroupList;

	public static final String ORDERNUMBER_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号,升序排列,为空在最后")
	@Column(name = ColumnNamePrefix + ORDERNUMBER_FIELDNAME)
	private Integer orderNumber;

	public static final String IGNOREPERMISSION_FIELDNAME = "ignorePermission";
	@FieldDescribe("是否忽略权限")
	@Column(name = ColumnNamePrefix + IGNOREPERMISSION_FIELDNAME)
	private Boolean ignorePermission;

	public static final String PROPERTIES_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private RevealProperties properties;
	

	public Boolean getIgnorePermission() {
		return ignorePermission;
	}

	public void setIgnorePermission(Boolean ignorePermission) {
		this.ignorePermission = ignorePermission;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public List<String> getAvailablePersonList() {
		return availablePersonList;
	}

	public void setAvailablePersonList(List<String> availablePersonList) {
		this.availablePersonList = availablePersonList;
	}

	public List<String> getAvailableUnitList() {
		return availableUnitList;
	}

	public void setAvailableUnitList(List<String> availableUnitList) {
		this.availableUnitList = availableUnitList;
	}

	public List<String> getAvailableGroupList() {
		return availableGroupList;
	}

	public void setAvailableGroupList(List<String> availableGroupList) {
		this.availableGroupList = availableGroupList;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
