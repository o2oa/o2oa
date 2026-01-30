package com.x.pan.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.MD5Tool;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import java.util.Date;

/**
 * 共享区权限
 * @author sword
 */
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.ZonePermission.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.ZonePermission.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }),
		@UniqueConstraint(name = PersistenceProperties.ZonePermission.table + JpaObject.IndexNameMiddle
		+ ZonePermission.zoneId_FIELDNAME, columnNames = {JpaObject.ColumnNamePrefix+ZonePermission.zoneId_FIELDNAME,
				JpaObject.ColumnNamePrefix+ZonePermission.name_FIELDNAME})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ZonePermission extends SliceJpaObject {

	private static final long serialVersionUID = 8256324740621393506L;
	private static final String TABLE = PersistenceProperties.ZonePermission.table;

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
	public void onPersist() {
	}

	public ZonePermission() {
	}

	public ZonePermission(String name, String role, String zoneId, String lastUpdatePerson) {
		this.name = name;
		this.role = role;
		this.zoneId = zoneId;
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("权限对象（人、组织或群组）.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String role_FIELDNAME = "role";
	@FieldDescribe("角色.")
	@Column(length = length_255B, name = ColumnNamePrefix + role_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String role;

	public static final String zoneId_FIELDNAME = "zoneId";
	@FieldDescribe("共享区ID或目录ID。")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + zoneId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String zoneId;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("最后更新人员.")
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

}
