package com.x.attendance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceAdmin.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceAdmin.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceAdmin extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AttendanceAdmin.table;

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
	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("组织名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ unitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unitName = "";

	public static final String unitOu_FIELDNAME = "unitOu";
	@FieldDescribe("组织编号")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + unitOu_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unitOu_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unitOu = "";

	public static final String adminName_FIELDNAME = "adminName";
	@FieldDescribe("管理员姓名")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + adminName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + adminName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String adminName = "";

	public static final String adminLevel_FIELDNAME = "adminLevel";
	@FieldDescribe("管理级别:UNIT|TOPUNIT")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + adminLevel_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String adminLevel = "TOPUNIT";

	public String getUnitName() {
		return unitName;
	}

	public String getUnitOu() {
		return unitOu;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public void setUnitOu(String unitOu) {
		this.unitOu = unitOu;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getAdminLevel() {
		return adminLevel;
	}

	public void setAdminLevel(String adminLevel) {
		this.adminLevel = adminLevel;
	}
}