package com.x.attendance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceWorkPlace", description = "考勤工作场所配置.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceWorkPlace.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.AttendanceWorkPlace.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceWorkPlace extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AttendanceWorkPlace.table;

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
	public static final String placeName_FIELDNAME = "placeName";
	@FieldDescribe("场所名称")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ placeName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String placeName = "";

	public static final String placeAlias_FIELDNAME = "placeAlias";
	@FieldDescribe("场所别名")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ placeAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String placeAlias = "";

	public static final String creator_FIELDNAME = "creator";
	@FieldDescribe("创建人")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creator_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creator = "";

	public static final String longitude_FIELDNAME = "longitude";
	@FieldDescribe("经度")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + longitude_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String longitude = null;

	public static final String latitude_FIELDNAME = "latitude";
	@FieldDescribe("纬度")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + latitude_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String latitude = null;

	public static final String errorRange_FIELDNAME = "errorRange";
	@FieldDescribe("误差范围")
	@Column(name = ColumnNamePrefix + errorRange_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer errorRange = 200;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("说明备注")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = null;

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getPlaceAlias() {
		return placeAlias;
	}

	public void setPlaceAlias(String placeAlias) {
		this.placeAlias = placeAlias;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getErrorRange() {
		return errorRange;
	}

	public void setErrorRange(Integer errorRange) {
		this.errorRange = errorRange;
	}

}