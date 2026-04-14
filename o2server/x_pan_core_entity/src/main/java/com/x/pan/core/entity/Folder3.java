package com.x.pan.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.*;
import com.x.base.core.project.annotation.FieldDescribe;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import java.util.Date;

/**
 * 共享区及其下文件夹
 * @author sword
 */
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Folder3.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Folder3.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }),
		@UniqueConstraint(name = PersistenceProperties.Folder3.table + JpaObject.IndexNameMiddle
				+ Folder3.superior_FIELDNAME, columnNames = { JpaObject.ColumnNamePrefix+Folder3.superior_FIELDNAME,
				JpaObject.ColumnNamePrefix+Folder3.name_FIELDNAME, JpaObject.ColumnNamePrefix+Folder3.status_FIELDNAME})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Folder3 extends SliceJpaObject {

	private static final long serialVersionUID = 3609005127352051746L;
	private static final String TABLE = PersistenceProperties.Folder3.table;

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
	private String id;

	/* 以上为 JpaObject 默认字段 */

	@Override
	public void onPersist() {
	}

	public Folder3() {
	}

	public Folder3(String name, String person, String superior, String zoneId) {
		this.id = createId();
		this.name = name;
		this.superior = superior;
		this.status = FileStatusEnum.VALID.getName();
		if(StringUtils.isEmpty(zoneId)){
			this.zoneId = this.id;
		}else{
			this.zoneId = zoneId;
		}
		this.person = person;
		this.lastUpdatePerson = person;
		this.lastUpdateTime = new Date();
		this.hasSetPermission = false;
	}

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("创建用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String name;

	public static final String superior_FIELDNAME = "superior";
	@FieldDescribe("上级目录ID。")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + superior_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String superior;

	public static final String zoneId_FIELDNAME = "zoneId";
	@FieldDescribe("共享区ID。")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + zoneId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + zoneId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String zoneId;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("文件状态：正常|已删除")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status = FileStatusEnum.VALID.getName();

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("最后更新用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String hasSetPermission_FIELDNAME = "hasSetPermission";
	@FieldDescribe("是否设置权限.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + hasSetPermission_FIELDNAME)
	private Boolean hasSetPermission;

	public static final String capacity_FIELDNAME = "capacity";
	@FieldDescribe("容量(单位M)，0表示无限大.")
	@Column(name = ColumnNamePrefix + capacity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer capacity;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public Boolean getHasSetPermission() {
		return hasSetPermission;
	}

	public void setHasSetPermission(Boolean hasSetPermission) {
		this.hasSetPermission = hasSetPermission;
	}

	public Integer getCapacity() {
		return capacity == null ? 0 : capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
}
