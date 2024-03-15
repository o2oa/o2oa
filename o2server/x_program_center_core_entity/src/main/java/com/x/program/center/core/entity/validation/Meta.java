package com.x.program.center.core.entity.validation;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "Meta", description = "服务管理验证数据结构.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Validation.Meta.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Validation.Meta.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Meta extends SliceJpaObject {

	private static final long serialVersionUID = 6387104721461689291L;

	public Meta() {

	}

	public Meta(String className, String application, String cron) {

	}

	private static final String TABLE = PersistenceProperties.Validation.Meta.TABLE;

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
		// nothing
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

	public static final String STRINGVALUE_FIELDNAME = "stringValue";
	@FieldDescribe("String 值.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + STRINGVALUE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + STRINGVALUE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue;

	public static final String STRINGLOBVALUE_FIELDNAME = "stringLobValue";
	@FieldDescribe("String Lob 值.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + STRINGLOBVALUE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringLobValue;

	public static final String INTEGERVALUE_FIELDNAME = "integerValue";
	@FieldDescribe("Integer 值.")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + INTEGERVALUE_FIELDNAME)
	@Column(name = ColumnNamePrefix + INTEGERVALUE_FIELDNAME)
	private Integer integerValue;

	public static final String LONGVALUE_FIELDNAME = "longValue";
	@FieldDescribe("Long 值.")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + LONGVALUE_FIELDNAME)
	@Column(name = ColumnNamePrefix + LONGVALUE_FIELDNAME)
	private Long longValue;

	public static final String FLOATVALUE_FIELDNAME = "floatValue";
	@FieldDescribe("Float 值.")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + FLOATVALUE_FIELDNAME)
	@Column(name = ColumnNamePrefix + FLOATVALUE_FIELDNAME)
	private Float floatValue;

	public static final String DOUBLEVALUE_FIELDNAME = "doubleValue";
	@FieldDescribe("Double 值.")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + DOUBLEVALUE_FIELDNAME)
	@Column(name = ColumnNamePrefix + DOUBLEVALUE_FIELDNAME)
	private Double doubleValue;

	public static final String DATETIMEVALUE_FIELDNAME = "dateTimeValue";
	@FieldDescribe("DateTime 值.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + DATETIMEVALUE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + DATETIMEVALUE_FIELDNAME)
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateTimeValue;

	public static final String DATEVALUE_FIELDNAME = "dateValue";
	@FieldDescribe("Date 值.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + DATEVALUE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + DATEVALUE_FIELDNAME)
	@Temporal(TemporalType.DATE)
	private Date dateValue;

	public static final String TIMEVALUE_FIELDNAME = "timeValue";
	@FieldDescribe("Time 值.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + TIMEVALUE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + TIMEVALUE_FIELDNAME)
	@Temporal(TemporalType.TIME)
	private Date timeValue;

	public static final String BOOLEANVALUE_FIELDNAME = "booleanValue";
	@FieldDescribe("Boolean 值.")
	@Column(name = ColumnNamePrefix + BOOLEANVALUE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean booleanValue;

	public static final String STRINGVALUELIST_FIELDNAME = "stringValueList";
	@FieldDescribe("String List 值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ STRINGVALUELIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + STRINGVALUELIST_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + STRINGVALUELIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + STRINGVALUELIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> stringValueList;

	public static final String INTEGERVALUELIST_FIELDNAME = "integerValueList";
	@FieldDescribe("Integer List 值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ INTEGERVALUELIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + INTEGERVALUELIST_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(name = ColumnNamePrefix + INTEGERVALUELIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + INTEGERVALUELIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<Integer> integerValueList;

	public static final String LONGVALUELIST_FIELDNAME = "longValueList";
	@FieldDescribe("Long List类型.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ LONGVALUELIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + LONGVALUELIST_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(name = ColumnNamePrefix + LONGVALUELIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + LONGVALUELIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<Long> longValueList;

	public static final String FLOATVALUELIST_FIELDNAME = "floatValueList";
	@FieldDescribe("Float List 值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ FLOATVALUELIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + FLOATVALUELIST_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(name = ColumnNamePrefix + FLOATVALUELIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + FLOATVALUELIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<Float> floatValueList;

	public static final String DOUBLEVALUELIST_FIELDNAME = "doubleValueList";
	@FieldDescribe("Double List 值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ DOUBLEVALUELIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + DOUBLEVALUELIST_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(name = ColumnNamePrefix + DOUBLEVALUELIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + DOUBLEVALUELIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<Double> doubleValueList;

	public static final String DATETIMEVALUELIST_FIELDNAME = "dateTimeValueList";
	@FieldDescribe("DateTime List 值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ DATETIMEVALUELIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ DATETIMEVALUELIST_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(name = ColumnNamePrefix + DATETIMEVALUELIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + DATETIMEVALUELIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<Date> dateTimeValueList;

	public static final String BOOLEANVALUELIST_FIELDNAME = "booleanValueList";
	@FieldDescribe("Boolean List 值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ BOOLEANVALUELIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + BOOLEANVALUELIST_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(name = ColumnNamePrefix + BOOLEANVALUELIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + BOOLEANVALUELIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<Boolean> booleanValueList;

	public static final String STRINGVALUEMAP_FIELDNAME = "stringValueMap";
	@FieldDescribe("String Map 值.")
	@CheckPersist(allowEmpty = true)
	@PersistentMap(fetch = FetchType.EAGER, elementType = String.class, keyType = String.class)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ STRINGVALUEMAP_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + STRINGVALUEMAP_FIELDNAME
					+ JoinIndexNameSuffix))
	@KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + STRINGVALUEMAP_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + STRINGVALUEMAP_FIELDNAME + ElementIndexNameSuffix)
	@KeyIndex(name = TABLE + IndexNameMiddle + STRINGVALUEMAP_FIELDNAME + KeyIndexNameSuffix)
	private Map<String, String> stringValueMap;

	public static final String INTEGERVALUEMAP_FIELDNAME = "integerValueMap";
	@FieldDescribe("Integer Map 值.")
	@CheckPersist(allowEmpty = true)
	@PersistentMap(fetch = FetchType.EAGER, elementType = String.class, keyType = String.class)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ INTEGERVALUEMAP_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + INTEGERVALUEMAP_FIELDNAME
					+ JoinIndexNameSuffix))
	@KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
	@ElementColumn(name = ColumnNamePrefix + INTEGERVALUEMAP_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + INTEGERVALUEMAP_FIELDNAME + ElementIndexNameSuffix)
	@KeyIndex(name = TABLE + IndexNameMiddle + INTEGERVALUEMAP_FIELDNAME + KeyIndexNameSuffix)
	private Map<String, Integer> integerValueMap;

	public static final String LONGVALUEMAP_FIELDNAME = "longValueMap";
	@FieldDescribe("Long Map 值.")
	@CheckPersist(allowEmpty = true)
	@PersistentMap(fetch = FetchType.EAGER, elementType = String.class, keyType = String.class)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ LONGVALUEMAP_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + LONGVALUEMAP_FIELDNAME
					+ JoinIndexNameSuffix))
	@KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
	@ElementColumn(name = ColumnNamePrefix + LONGVALUEMAP_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + LONGVALUEMAP_FIELDNAME + ElementIndexNameSuffix)
	@KeyIndex(name = TABLE + IndexNameMiddle + LONGVALUEMAP_FIELDNAME + KeyIndexNameSuffix)
	private Map<String, Long> longValueMap;

	public static final String FLOATVALUEMAP_FIELDNAME = "floatValueMap";
	@FieldDescribe("Float Map 值.")
	@CheckPersist(allowEmpty = true)
	@PersistentMap(fetch = FetchType.EAGER, elementType = String.class, keyType = String.class)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ FLOATVALUEMAP_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + FLOATVALUEMAP_FIELDNAME
					+ JoinIndexNameSuffix))
	@KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
	@ElementColumn(name = ColumnNamePrefix + FLOATVALUEMAP_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + FLOATVALUEMAP_FIELDNAME + ElementIndexNameSuffix)
	@KeyIndex(name = TABLE + IndexNameMiddle + FLOATVALUEMAP_FIELDNAME + KeyIndexNameSuffix)
	private Map<String, Float> floatValueMap;

	public static final String DOUBLEVALUEMAP_FIELDNAME = "doubleValueMap";
	@FieldDescribe("Double Map 值.")
	@CheckPersist(allowEmpty = true)
	@PersistentMap(fetch = FetchType.EAGER, elementType = String.class, keyType = String.class)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ DOUBLEVALUEMAP_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + DOUBLEVALUEMAP_FIELDNAME
					+ JoinIndexNameSuffix))
	@KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
	@ElementColumn(name = ColumnNamePrefix + DOUBLEVALUEMAP_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + DOUBLEVALUEMAP_FIELDNAME + ElementIndexNameSuffix)
	@KeyIndex(name = TABLE + IndexNameMiddle + DOUBLEVALUEMAP_FIELDNAME + KeyIndexNameSuffix)
	private Map<String, Double> doubleValueMap;

	public static final String DATETIMEVALUEMAP_FIELDNAME = "dateTimeValueMap";
	@FieldDescribe("DateTime Map 值.")
	@CheckPersist(allowEmpty = true)
	@PersistentMap(fetch = FetchType.EAGER, elementType = String.class, keyType = String.class)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ DATETIMEVALUEMAP_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + DATETIMEVALUEMAP_FIELDNAME
					+ JoinIndexNameSuffix))
	@KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
	@ElementColumn(name = ColumnNamePrefix + DATETIMEVALUEMAP_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + DATETIMEVALUEMAP_FIELDNAME + ElementIndexNameSuffix)
	@KeyIndex(name = TABLE + IndexNameMiddle + DATETIMEVALUEMAP_FIELDNAME + KeyIndexNameSuffix)
	private Map<String, Date> dateTimeValueMap;

	public static final String BOOLEANVALUEMAP_FIELDNAME = "booleanValueMap";
	@FieldDescribe("Boolean Map值.")
	@CheckPersist(allowEmpty = true)
	@PersistentMap(fetch = FetchType.EAGER, elementType = String.class, keyType = String.class)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ BOOLEANVALUEMAP_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + BOOLEANVALUEMAP_FIELDNAME
					+ JoinIndexNameSuffix))
	@KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
	@ElementColumn(name = ColumnNamePrefix + BOOLEANVALUEMAP_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + BOOLEANVALUEMAP_FIELDNAME + ElementIndexNameSuffix)
	@KeyIndex(name = TABLE + IndexNameMiddle + BOOLEANVALUEMAP_FIELDNAME + KeyIndexNameSuffix)
	private Map<String, Boolean> booleanValueMap;

	public static final String PROPERTIES_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
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

	public Float getFloatValue() {
		return floatValue;
	}

	public void setFloatValue(Float floatValue) {
		this.floatValue = floatValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
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

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public List<String> getStringValueList() {
		return stringValueList;
	}

	public void setStringValueList(List<String> stringValueList) {
		this.stringValueList = stringValueList;
	}

	public List<Integer> getIntegerValueList() {
		return integerValueList;
	}

	public void setIntegerValueList(List<Integer> integerValueList) {
		this.integerValueList = integerValueList;
	}

	public List<Long> getLongValueList() {
		return longValueList;
	}

	public void setLongValueList(List<Long> longValueList) {
		this.longValueList = longValueList;
	}

	public List<Float> getFloatValueList() {
		return floatValueList;
	}

	public void setFloatValueList(List<Float> floatValueList) {
		this.floatValueList = floatValueList;
	}

	public List<Double> getDoubleValueList() {
		return doubleValueList;
	}

	public void setDoubleValueList(List<Double> doubleValueList) {
		this.doubleValueList = doubleValueList;
	}

	public List<Date> getDateTimeValueList() {
		return dateTimeValueList;
	}

	public void setDateTimeValueList(List<Date> dateTimeValueList) {
		this.dateTimeValueList = dateTimeValueList;
	}

	public List<Boolean> getBooleanValueList() {
		return booleanValueList;
	}

	public void setBooleanValueList(List<Boolean> booleanValueList) {
		this.booleanValueList = booleanValueList;
	}

	public Map<String, String> getStringValueMap() {
		return stringValueMap;
	}

	public void setStringValueMap(Map<String, String> stringValueMap) {
		this.stringValueMap = stringValueMap;
	}

	public Map<String, Integer> getIntegerValueMap() {
		return integerValueMap;
	}

	public void setIntegerValueMap(Map<String, Integer> integerValueMap) {
		this.integerValueMap = integerValueMap;
	}

	public Map<String, Long> getLongValueMap() {
		return longValueMap;
	}

	public void setLongValueMap(Map<String, Long> longValueMap) {
		this.longValueMap = longValueMap;
	}

	public Map<String, Float> getFloatValueMap() {
		return floatValueMap;
	}

	public void setFloatValueMap(Map<String, Float> floatValueMap) {
		this.floatValueMap = floatValueMap;
	}

	public Map<String, Double> getDoubleValueMap() {
		return doubleValueMap;
	}

	public void setDoubleValueMap(Map<String, Double> doubleValueMap) {
		this.doubleValueMap = doubleValueMap;
	}

	public Map<String, Date> getDateTimeValueMap() {
		return dateTimeValueMap;
	}

	public void setDateTimeValueMap(Map<String, Date> dateTimeValueMap) {
		this.dateTimeValueMap = dateTimeValueMap;
	}

	public Map<String, Boolean> getBooleanValueMap() {
		return booleanValueMap;
	}

	public void setBooleanValueMap(Map<String, Boolean> booleanValueMap) {
		this.booleanValueMap = booleanValueMap;
	}

}