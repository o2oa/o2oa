package com.x.attendance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.AttendanceWorkDayConfig.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.AttendanceWorkDayConfig.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceWorkDayConfig extends SliceJpaObject {

	private static final long serialVersionUID = -7226058083321275722L;
	private static final String TABLE = PersistenceProperties.AttendanceWorkDayConfig.table;

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
	@FieldDescribe("配置项名称")
	@Column(name = "xconfigName", length = JpaObject.length_96B)
	@CheckPersist(allowEmpty = false)
	private String configName = "";

	@FieldDescribe("配置年份")
	@Column(name = "xconfigYear", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String configYear = "2016";

	@FieldDescribe("配置月份")
	@Column(name = "xconfigMonth", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String configMonth = "";

	@FieldDescribe("配置日期")
	@Column(name = "xconfigDate", length = JpaObject.length_32B)
	@CheckPersist(allowEmpty = false)
	private String configDate = "";

	@FieldDescribe("配置类型：Holiday|Workday")
	@Column(name = "xconfigType", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String configType = "Holiday";

	@FieldDescribe("配置说明")
	@Column(name = "xdescription", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String description = "";

	public String getConfigDate() {
		return configDate;
	}

	public void setConfigDate(String configDate) {
		this.configDate = configDate;
	}

	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConfigYear() {
		return configYear;
	}

	public void setConfigYear(String configYear) {
		this.configYear = configYear;
	}

	public String getConfigMonth() {
		return configMonth;
	}

	public void setConfigMonth(String configMonth) {
		this.configMonth = configMonth;
	}

}