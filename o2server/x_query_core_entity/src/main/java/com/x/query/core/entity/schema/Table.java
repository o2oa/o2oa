package com.x.query.core.entity.schema;

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
import javax.persistence.OrderColumn;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.query.core.entity.PersistenceProperties;
import com.x.query.core.entity.Query;

@Entity
@ContainerEntity
@javax.persistence.Table(name = PersistenceProperties.Schema.Table.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Schema.Table.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Table extends SliceJpaObject {

	private static final long serialVersionUID = -5610293696763235753L;

	private static final String TABLE = PersistenceProperties.Schema.Table.table;

	public static final String STATUS_build = "build";

	public static final String STATUS_draft = "draft";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	/* 已经没有Statement使用Table了 */
	@CheckRemove(citationNotExists =
	/* 已经没有Stat使用View了 */
	@CitationNotExist(type = Statement.class, fields = Statement.table_FIELDNAME))
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	public void onPersist() throws Exception {
		if (StringUtils.isEmpty(this.getData())) {
			this.setData("{}");
		}
	}

	/* 更新运行方法 */

	public static final String query_FIELDNAME = "query";
	@FieldDescribe("所属查询.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + query_FIELDNAME)
	@Index(name = TABLE + ColumnNamePrefix + query_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Query.class) })
	private String query;

	public static final String name_FIELDNAME = "name";
	@Flag
	@FieldDescribe("表名,最大64个字符.")
	@Column(length = length_64B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("别名.")
	@Column(length = length_64B, name = ColumnNamePrefix + alias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String readPersonList_FIELDNAME = "readPersonList";
	@FieldDescribe("可以访问数据的用户.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + readPersonList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + readPersonList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readPersonList;

	public static final String readUnitList_FIELDNAME = "readUnitList";
	@FieldDescribe("可以访问数据的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + readUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + readUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readUnitList;

	public static final String editPersonList_FIELDNAME = "editPersonList";
	@FieldDescribe("可以修改数据的用户.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + editPersonList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + editPersonList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + editPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + editPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> editPersonList;

	public static final String editUnitList_FIELDNAME = "editUnitList";
	@FieldDescribe("可以修改数据的组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + editUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + editUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + editUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + editUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> editUnitList;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("表的创建者")
	@CheckPersist(allowEmpty = false)
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	private String creatorPerson;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("表的最后修改时间")
	@CheckPersist(allowEmpty = false)
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	private Date lastUpdateTime;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("表的最后修改者")
	@CheckPersist(allowEmpty = false)
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	private String lastUpdatePerson;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("表结构方案.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public static final String draftData_FIELDNAME = "draftData";
	@FieldDescribe("草稿表结构方案.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + draftData_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String draftData;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("状态")
	@CheckPersist(allowEmpty = false)
	@Column(length = length_32B, name = ColumnNamePrefix + status_FIELDNAME)
	private String status;

	public static final String buildSuccess_FIELDNAME = "buildSuccess";
	@FieldDescribe("是否编译成功.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + buildSuccess_FIELDNAME)
	private Boolean buildSuccess;

	public Boolean getBuildSuccess() {
		return buildSuccess;
	}

	public void setBuildSuccess(Boolean buildSuccess) {
		this.buildSuccess = buildSuccess;
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

	public List<String> getReadPersonList() {
		return readPersonList;
	}

	public void setReadPersonList(List<String> readPersonList) {
		this.readPersonList = readPersonList;
	}

	public List<String> getReadUnitList() {
		return readUnitList;
	}

	public void setReadUnitList(List<String> readUnitList) {
		this.readUnitList = readUnitList;
	}

	public List<String> getEditPersonList() {
		return editPersonList;
	}

	public void setEditPersonList(List<String> editPersonList) {
		this.editPersonList = editPersonList;
	}

	public List<String> getEditUnitList() {
		return editUnitList;
	}

	public void setEditUnitList(List<String> editUnitList) {
		this.editUnitList = editUnitList;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
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

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getDraftData() {
		return draftData;
	}

	public void setDraftData(String draftData) {
		this.draftData = draftData;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
