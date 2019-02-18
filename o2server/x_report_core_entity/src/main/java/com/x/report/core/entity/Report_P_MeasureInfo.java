package com.x.report.core.entity;

import java.util.Date;
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

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Report_P_MeasureInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Report_P_MeasureInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Report_P_MeasureInfo extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Report_P_MeasureInfo.table;

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
	@FieldDescribe("战略举措标题")
	@Column(name = "xtitle", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = false)
	private String title;

	public static final String parentId_FIELDNAME = "parentId";
	@FieldDescribe("战略部署ID，父文档ID")
	@Column(name = ColumnNamePrefix + parentId_FIELDNAME, length = JpaObject.length_id)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + parentId_FIELDNAME)
	private String parentId;

	public static final String year_FIELDNAME = "year";
	@FieldDescribe("战略举措年份")
	@Column(name = ColumnNamePrefix + year_FIELDNAME, length = JpaObject.length_8B)
	@Index(name = TABLE + IndexNameMiddle + year_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String year;

	@FieldDescribe("状态")
	@Column(name = "xstatus", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = true)
	private String status;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("序号")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer orderNumber;

	@FieldDescribe("战略举措使用范围（部门列表）")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + "_unitList", joinIndex = @Index(name = TABLE + "_unitList_join"))
	@ElementColumn(length = JpaObject.length_255B, name = "xdeptList")
	@ElementIndex(name = TABLE + "_unitList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> unitList;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public List<String> getUnitList() {
		return unitList;
	}

	public void setUnitList(List<String> unitList) {
		this.unitList = unitList;
	}
}