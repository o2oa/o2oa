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

import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.PersistentMap;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.KeyColumn;
import org.apache.openjpa.persistence.jdbc.KeyIndex;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.program.center.core.entity.PersistenceProperties;

@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
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

	public void onPersist() throws Exception {
	}

	public MetaProperties getProperties() {
		if (null == this.properties) {
			this.properties = new MetaProperties();
		}
		return this.properties;
	}

	public void setProperties(MetaProperties properties) {
		this.properties = properties;
	}

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

	public static final String floatValue_FIELDNAME = "floatValue";
	@FieldDescribe("浮点数.")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + floatValue_FIELDNAME)
	@Column(name = ColumnNamePrefix + floatValue_FIELDNAME)
	private Double floatValue;

	public static final String doubleValue_FIELDNAME = "doubleValue";
	@FieldDescribe("双精度浮点数.")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + doubleValue_FIELDNAME)
	@Column(name = ColumnNamePrefix + doubleValue_FIELDNAME)
	private Double doubleValue;

	public static final String STRINGVALUELIST_FIELDNAME = "stringValueList";
	@FieldDescribe("List类型.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ STRINGVALUELIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + STRINGVALUELIST_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + STRINGVALUELIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + STRINGVALUELIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> stringValueList;

	public static final String DATEVALUELIST_FIELDNAME = "dateValueList";
	@FieldDescribe("List类型.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ DATEVALUELIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + DATEVALUELIST_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + DATEVALUELIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + DATEVALUELIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	@Temporal(TemporalType.DATE)
	private List<Date> dateValueList;

	public static final String DATETIMEVALUELIST_FIELDNAME = "dateTimeValueList";
	@FieldDescribe("List类型.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ DATETIMEVALUELIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ DATETIMEVALUELIST_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + DATETIMEVALUELIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + DATETIMEVALUELIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	@Temporal(TemporalType.TIMESTAMP)
	private List<Date> dateTimeValueList;

	public static final String STRINGVALUEMAP_FIELDNAME = "stringValueMap";
	@FieldDescribe("Map类型.")
	@CheckPersist(allowEmpty = true)
	@PersistentMap(fetch = FetchType.EAGER, elementType = String.class, keyType = String.class)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ STRINGVALUEMAP_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + STRINGVALUEMAP_FIELDNAME
					+ JoinIndexNameSuffix))
	@KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + STRINGVALUEMAP_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + STRINGVALUEMAP_FIELDNAME + ElementIndexNameSuffix)
	@KeyIndex(name = TABLE + IndexNameMiddle + STRINGVALUEMAP_FIELDNAME + KeyIndexNameSuffix)
	private LinkedHashMap<String, String> stringValueMap;

	public static final String DATEVALUEMAP_FIELDNAME = "dateValueMap";
	@FieldDescribe("Map类型.")
	@CheckPersist(allowEmpty = true)
	@PersistentMap(fetch = FetchType.EAGER, elementType = String.class, keyType = String.class)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ DATEVALUEMAP_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + DATEVALUEMAP_FIELDNAME
					+ JoinIndexNameSuffix))
	@KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + DATEVALUEMAP_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + DATEVALUEMAP_FIELDNAME + ElementIndexNameSuffix)
	@KeyIndex(name = TABLE + IndexNameMiddle + DATEVALUEMAP_FIELDNAME + KeyIndexNameSuffix)
	@Temporal(TemporalType.DATE)
	private LinkedHashMap<String, Date> dateValueMap;

	public static final String DATETIMEVALUEMAP_FIELDNAME = "dateTimeValueMap";
	@FieldDescribe("Map类型.")
	@CheckPersist(allowEmpty = true)
	@PersistentMap(fetch = FetchType.EAGER, elementType = String.class, keyType = String.class)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ DATETIMEVALUEMAP_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + DATETIMEVALUEMAP_FIELDNAME
					+ JoinIndexNameSuffix))
	@KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + DATETIMEVALUEMAP_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + DATETIMEVALUEMAP_FIELDNAME + ElementIndexNameSuffix)
	@KeyIndex(name = TABLE + IndexNameMiddle + DATETIMEVALUEMAP_FIELDNAME + KeyIndexNameSuffix)
	@Temporal(TemporalType.TIMESTAMP)
	private LinkedHashMap<String, Date> dateTimeValueMap;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private MetaProperties properties;

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public String getStringLobValue() {
		return stringLobValue;
	}

	public void setStringLobValue(String stringLobValue) {
		this.stringLobValue = stringLobValue;
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

	public Double getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(Double floatValue) {
		this.floatValue = floatValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public List<String> getStringValueList() {
		return stringValueList;
	}

	public void setStringValueList(List<String> stringValueList) {
		this.stringValueList = stringValueList;
	}

	public List<Date> getDateValueList() {
		return dateValueList;
	}

	public void setDateValueList(List<Date> dateValueList) {
		this.dateValueList = dateValueList;
	}

	public List<Date> getDateTimeValueList() {
		return dateTimeValueList;
	}

	public void setDateTimeValueList(List<Date> dateTimeValueList) {
		this.dateTimeValueList = dateTimeValueList;
	}

	public LinkedHashMap<String, String> getStringValueMap() {
		return stringValueMap;
	}

	public void setStringValueMap(LinkedHashMap<String, String> stringValueMap) {
		this.stringValueMap = stringValueMap;
	}

	public LinkedHashMap<String, Date> getDateValueMap() {
		return dateValueMap;
	}

	public void setDateValueMap(LinkedHashMap<String, Date> dateValueMap) {
		this.dateValueMap = dateValueMap;
	}

	public LinkedHashMap<String, Date> getDateTimeValueMap() {
		return dateTimeValueMap;
	}

	public void setDateTimeValueMap(LinkedHashMap<String, Date> dateTimeValueMap) {
		this.dateTimeValueMap = dateTimeValueMap;
	}

}