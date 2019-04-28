package com.x.program.center.core.entity.validation;

import java.util.Date;
import java.util.LinkedHashMap;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.PersistentMap;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.KeyColumn;
import org.apache.openjpa.persistence.jdbc.KeyIndex;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.program.center.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Validation.Meta.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Validation.Meta.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Meta extends SliceJpaObject {

	private static final long serialVersionUID = 6387104721461689291L;

	public Meta() {

	}

	public Meta(String className, String application, String cron) {

	}

	private static final String TABLE = PersistenceProperties.Validation.Meta.table;

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

	/* 更新运行方法 */

	public static final String stringValue_FIELDNAME = "stringValue";
	@FieldDescribe("文本字段.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + stringValue_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue;

	public static final String stringLobValue_FIELDNAME = "stringLobValue";
	@FieldDescribe("长文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + stringLobValue_FIELDNAME)
	private String stringLobValue;

	public static final String booleanValue_FIELDNAME = "booleanValue";
	@FieldDescribe("布尔值.")
	@Column(name = ColumnNamePrefix + booleanValue_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + booleanValue_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean booleanValue;

	public static final String dateTimeValue_FIELDNAME = "dateTimeValue";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("日期和时间值.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + dateTimeValue_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue_FIELDNAME)
	private Date dateTimeValue;

	public static final String dateValue_FIELDNAME = "dateValue";
	@Temporal(TemporalType.DATE)
	@FieldDescribe("日期值.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + dateValue_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateValue_FIELDNAME)
	private Date dateValue;

	public static final String timeValue_FIELDNAME = "timeValue";
	@Temporal(TemporalType.TIME)
	@FieldDescribe("时间值.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + timeValue_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + timeValue_FIELDNAME)
	private Date timeValue;

	public static final String integerValue_FIELDNAME = "integerValue";
	@FieldDescribe("整型.")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + integerValue_FIELDNAME)
	@Column(name = ColumnNamePrefix + integerValue_FIELDNAME)
	private Integer integerValue;

	public static final String longValue_FIELDNAME = "longValue";
	@FieldDescribe("长整型.")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + longValue_FIELDNAME)
	@Column(name = ColumnNamePrefix + longValue_FIELDNAME)
	private Long longValue;

	public static final String doubleValue_FIELDNAME = "doubleValue";
	@FieldDescribe("双精度浮点数.")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + doubleValue_FIELDNAME)
	@Column(name = ColumnNamePrefix + doubleValue_FIELDNAME)
	private Double doubleValue;

	public static final String listValueList_FIELDNAME = "listValueList";
	@FieldDescribe("List类型.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + listValueList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + listValueList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + listValueList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + listValueList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> listValueList;

	public static final String mapValueMap_FIELDNAME = "mapValueMap";
	@FieldDescribe("Map类型.")
	@CheckPersist(allowEmpty = true)
	@PersistentMap(fetch = FetchType.EAGER, elementType = String.class, keyType = String.class)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + mapValueMap_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + mapValueMap_FIELDNAME + JoinIndexNameSuffix))
	@KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + mapValueMap_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + mapValueMap_FIELDNAME + ElementIndexNameSuffix)
	@KeyIndex(name = TABLE + IndexNameMiddle + mapValueMap_FIELDNAME + KeyIndexNameSuffix)
	private LinkedHashMap<String, String> mapValueMap;

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public Date getDateTimeValue() {
		return dateTimeValue;
	}

	public void setDateTimeValue(Date dateTimeValue) {
		this.dateTimeValue = dateTimeValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Date getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(Date timeValue) {
		this.timeValue = timeValue;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public Long getLongValue() {
		return longValue;
	}

	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	public List<String> getListValueList() {
		return listValueList;
	}

	public void setListValueList(List<String> listValueList) {
		this.listValueList = listValueList;
	}

	public static String getDoublevalueFieldname() {
		return doubleValue_FIELDNAME;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public String getStringLobValue() {
		return stringLobValue;
	}

	public void setStringLobValue(String stringLobValue) {
		this.stringLobValue = stringLobValue;
	}

	public LinkedHashMap<String, String> getMapValueMap() {
		return mapValueMap;
	}

	public void setMapValueMap(LinkedHashMap<String, String> mapValueMap) {
		this.mapValueMap = mapValueMap;
	}

}