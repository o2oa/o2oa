package com.x.strategydeploy.core.entity;

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

/**
 * 战略措施信息
 * 
 * @author WUSHUTAO
 **/

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.MeasuresInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.MeasuresInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MeasuresInfo extends SliceJpaObject {
	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.MeasuresInfo.table;

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
	 * =============================以上为 JpaObject
	 * 默认字段============================================
	 */
	@FieldDescribe("战略举措标题")
	@Column(name = "xmeasuresinfotitle", length = JpaObject.length_255B)
	@Index(name = TABLE + "_xmeasuresinfotitle")
	@CheckPersist(allowEmpty = false)
	private String measuresinfotitle;

	@FieldDescribe("战略部署ID，父文档ID")
	@Column(name = "xmeasuresinfoparentid", length = JpaObject.length_id)
	@Index(name = TABLE + "_xmeasuresinfoparentid")
	@CheckPersist(allowEmpty = true)
	private String measuresinfoparentid;

	@FieldDescribe("战略举措年份")
	@Column(name = "xmeasuresinfoyear", length = JpaObject.length_64B)
	@Index(name = TABLE + "_xmeasuresinfoyear")
	@CheckPersist(allowEmpty = true)
	private String measuresinfoyear;

	@FieldDescribe("战略举措所属组织")
	@Column(name = "xmeasuresinfounit", length = JpaObject.length_255B)
	@Index(name = TABLE + "_xmeasuresinfounit")
	@CheckPersist(allowEmpty = true)
	private String measuresinfounit;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("战略举措描述")
	@Column(name = "xmeasuresinfodescribe", length = JpaObject.length_1M)
	@CheckPersist(allowEmpty = true)
	private String measuresinfodescribe;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("战略举措目标值")
	@Column(name = "xmeasuresinfotargetvalue", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String measuresinfotargetvalue;

	@FieldDescribe("战略举措创建者")
	@Column(name = "xmeasuresinfocreator", length = JpaObject.length_255B)
	@Index(name = TABLE + "_xmeasuresinfocreator")
	@CheckPersist(allowEmpty = true)
	private String measuresinfocreator;

	@FieldDescribe("战略举措使用范围（部门列表），牵头部门")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + "_deptList", joinIndex = @Index(name = TABLE + "_deptList_join"))
	@ElementColumn(length = JpaObject.length_255B, name = "xdeptList")
	@ElementIndex(name = TABLE + "_deptList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> deptlist;

	public static final String measuresdutydept_FIELDNAME = "measuresdutydept";
	@FieldDescribe("战略举措责任部门（2018年1月2日新增字段）")
	@Column(name = ColumnNamePrefix + measuresdutydept_FIELDNAME, length = JpaObject.length_255B)
	@Index(name = TABLE + IndexNameMiddle + measuresdutydept_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String measuresdutydept;

	@FieldDescribe("战略举措支持部门（2018年3月2日新增字段）")
	@Column(name = "xmeasuressupportdepts", length = JpaObject.length_255B)
	@CheckPersist(allowEmpty = true)
	private String measuressupportdepts;

	@FieldDescribe("状态")
	@Column(name = "xstatus", length = JpaObject.length_255B)
	@Index(name = TABLE + "_status")
	@CheckPersist(allowEmpty = true)
	private String status;

	@FieldDescribe("章节序号，类似：1.1,1.2,1.10")
	@Column(name = "xsequencenumber", length = JpaObject.length_255B)
	@Index(name = TABLE + "_sequencenumber")
	@CheckPersist(allowEmpty = true)
	// private Integer sequencenumber;
	private String sequencenumber;

	@FieldDescribe("实际存储章节序号，")
	@Column(name = "xformatsequencenumber", length = JpaObject.length_255B)
	// @Index(name = TABLE + "_sequencenumber")
	@CheckPersist(allowEmpty = true)
	// private Integer sequencenumber;
	private Double formatsequencenumber;

	public String getMeasuresinfotitle() {
		return measuresinfotitle;
	}

	public void setMeasuresinfotitle(String measuresinfotitle) {
		this.measuresinfotitle = measuresinfotitle;
	}

	public String getMeasuresinfoparentid() {
		return measuresinfoparentid;
	}

	public void setMeasuresinfoparentid(String measuresinfoparentid) {
		this.measuresinfoparentid = measuresinfoparentid;
	}

	public String getMeasuresinfoyear() {
		return measuresinfoyear;
	}

	public void setMeasuresinfoyear(String measuresinfoyear) {
		this.measuresinfoyear = measuresinfoyear;
	}

	public String getMeasuresinfounit() {
		return measuresinfounit;
	}

	public void setMeasuresinfounit(String measuresinfounit) {
		this.measuresinfounit = measuresinfounit;
	}

	public String getMeasuresinfodescribe() {
		return measuresinfodescribe;
	}

	public void setMeasuresinfodescribe(String measuresinfodescribe) {
		this.measuresinfodescribe = measuresinfodescribe;
	}

	public String getMeasuresinfotargetvalue() {
		return measuresinfotargetvalue;
	}

	public void setMeasuresinfotargetvalue(String measuresinfotargetvalue) {
		this.measuresinfotargetvalue = measuresinfotargetvalue;
	}

	public String getMeasuresinfocreator() {
		return measuresinfocreator;
	}

	public void setMeasuresinfocreator(String measuresinfocreator) {
		this.measuresinfocreator = measuresinfocreator;
	}

	public List<String> getDeptlist() {
		return deptlist;
	}

	public void setDeptlist(List<String> deptlist) {
		this.deptlist = deptlist;
	}

	public String getMeasuresdutydept() {
		return measuresdutydept;
	}

	public void setMeasuresdutydept(String measuresdutydept) {
		this.measuresdutydept = measuresdutydept;
	}

	public String getMeasuressupportdepts() {
		return measuressupportdepts;
	}

	public void setMeasuressupportdepts(String measuressupportdepts) {
		this.measuressupportdepts = measuressupportdepts;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSequencenumber() {
		return sequencenumber;
	}

	public void setSequencenumber(String sequencenumber) {
		this.sequencenumber = sequencenumber;
	}

	public Double getFormatsequencenumber() {
		return formatsequencenumber;
	}

	public void setFormatsequencenumber(Double formatsequencenumber) {
		this.formatsequencenumber = formatsequencenumber;
	}

}
