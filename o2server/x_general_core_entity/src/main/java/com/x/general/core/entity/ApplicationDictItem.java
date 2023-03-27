package com.x.general.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.dataitem.*;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import java.util.Date;

/**
 * @author sword
 */
@Schema(name = "ApplicationDictItem", description = "O2平台数据字典条目.")
@Entity
@ContainerEntity(dumpSize = 100, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.ApplicationDictItem.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.ApplicationDictItem.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ApplicationDictItem extends DataItem {

	private static final long serialVersionUID = -1746876041116570648L;
	private static final String TABLE = PersistenceProperties.ApplicationDictItem.table;

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

	@Override
	public void onPersist() {
		this.path0 = StringUtils.trimToEmpty(this.path0);
		this.path1 = StringUtils.trimToEmpty(this.path1);
		this.path2 = StringUtils.trimToEmpty(this.path2);
		this.path3 = StringUtils.trimToEmpty(this.path3);
		this.path4 = StringUtils.trimToEmpty(this.path4);
		this.path5 = StringUtils.trimToEmpty(this.path5);
		this.path6 = StringUtils.trimToEmpty(this.path6);
		this.path7 = StringUtils.trimToEmpty(this.path7);
		this.path0Location = NumberUtils.toInt(this.path0, -1);
		this.path1Location = NumberUtils.toInt(this.path1, -1);
		this.path2Location = NumberUtils.toInt(this.path2, -1);
		this.path3Location = NumberUtils.toInt(this.path3, -1);
		this.path4Location = NumberUtils.toInt(this.path4, -1);
		this.path5Location = NumberUtils.toInt(this.path5, -1);
		this.path6Location = NumberUtils.toInt(this.path6, -1);
		this.path7Location = NumberUtils.toInt(this.path7, -1);
	}

	@Column(length = DataItem.pathLength, name = ColumnNamePrefix + path0_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path0_FIELDNAME)
	private String path0 = "";

	@Column(length = DataItem.pathLength, name = ColumnNamePrefix + path1_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path1_FIELDNAME)
	private String path1 = "";

	@Column(length = DataItem.pathLength, name = ColumnNamePrefix + path2_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path2_FIELDNAME)
	private String path2 = "";

	@Column(length = DataItem.pathLength, name = ColumnNamePrefix + path3_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path3_FIELDNAME)
	private String path3 = "";

	@Column(length = DataItem.pathLength, name = ColumnNamePrefix + path4_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path4_FIELDNAME)
	private String path4 = "";

	@Column(length = DataItem.pathLength, name = ColumnNamePrefix + path5_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path5_FIELDNAME)
	private String path5 = "";

	@Column(length = DataItem.pathLength, name = ColumnNamePrefix + path6_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path6_FIELDNAME)
	private String path6 = "";

	@Column(length = DataItem.pathLength, name = ColumnNamePrefix + path7_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path7_FIELDNAME)
	private String path7 = "";

	@Column(name = ColumnNamePrefix + path0Location_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path0Location_FIELDNAME)
	private Integer path0Location;

	@Column(name = ColumnNamePrefix + path1Location_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path1Location_FIELDNAME)
	private Integer path1Location;

	@Column(name = ColumnNamePrefix + path2Location_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path2Location_FIELDNAME)
	private Integer path2Location;

	@Column(name = ColumnNamePrefix + path3Location_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path3Location_FIELDNAME)
	private Integer path3Location;

	@Column(name = ColumnNamePrefix + path4Location_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path4Location_FIELDNAME)
	private Integer path4Location;

	@Column(name = ColumnNamePrefix + path5Location_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path5Location_FIELDNAME)
	private Integer path5Location;

	@Column(name = ColumnNamePrefix + path6Location_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path6Location_FIELDNAME)
	private Integer path6Location;

	@Column(name = ColumnNamePrefix + path7Location_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path7Location_FIELDNAME)
	private Integer path7Location;

	@Enumerated(EnumType.STRING)
	@Column(length = ItemCategory.length, name = ColumnNamePrefix + itemCategory_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + itemCategory_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ItemCategory itemCategory;

	@Enumerated(EnumType.STRING)
	@Column(length = ItemType.length, name = ColumnNamePrefix + itemType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + itemType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ItemType itemType;

	@Enumerated(EnumType.STRING)
	@Column(length = ItemPrimitiveType.length, name = ColumnNamePrefix + itemPrimitiveType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + itemPrimitiveType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ItemPrimitiveType itemPrimitiveType;

	@Enumerated(EnumType.STRING)
	@Column(length = ItemStringValueType.length, name = ColumnNamePrefix + itemStringValueType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + itemStringValueType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ItemStringValueType itemStringValueType;

	@FieldDescribe("数据标识")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + bundle_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + bundle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String bundle;

	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + stringShortValue_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringShortValue_FIELDNAME)
	private String stringShortValue;

	@Column(length = JpaObject.length_100M, name = ColumnNamePrefix + stringLongValue_FIELDNAME)
	@Lob
	@Basic(fetch = FetchType.EAGER)
	private String stringLongValue;

	@Column(name = ColumnNamePrefix + numberValue_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + numberValue_FIELDNAME)
	private Double numberValue;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + dateTimeValue_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue_FIELDNAME)
	private Date dateTimeValue;

	@Temporal(TemporalType.DATE)
	@Column(name = ColumnNamePrefix + dateValue_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateValue_FIELDNAME)
	private Date dateValue;

	@Temporal(TemporalType.TIME)
	@Column(name = ColumnNamePrefix + timeValue_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + timeValue_FIELDNAME)
	private Date timeValue;

	@Column(name = ColumnNamePrefix + booleanValue_FIELDNAME)
	private Boolean booleanValue;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("所属应用ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	@Override
	public String getPath0() {
		return path0;
	}

	@Override
	public void setPath0(String path0) {
		this.path0 = path0;
	}

	@Override
	public String getPath1() {
		return path1;
	}

	@Override
	public void setPath1(String path1) {
		this.path1 = path1;
	}

	@Override
	public String getPath2() {
		return path2;
	}

	@Override
	public void setPath2(String path2) {
		this.path2 = path2;
	}

	@Override
	public String getPath3() {
		return path3;
	}

	@Override
	public void setPath3(String path3) {
		this.path3 = path3;
	}

	@Override
	public String getPath4() {
		return path4;
	}

	@Override
	public void setPath4(String path4) {
		this.path4 = path4;
	}

	@Override
	public String getPath5() {
		return path5;
	}

	@Override
	public void setPath5(String path5) {
		this.path5 = path5;
	}

	@Override
	public String getPath6() {
		return path6;
	}

	@Override
	public void setPath6(String path6) {
		this.path6 = path6;
	}

	@Override
	public String getPath7() {
		return path7;
	}

	@Override
	public void setPath7(String path7) {
		this.path7 = path7;
	}

	@Override
	public Integer getPath0Location() {
		return path0Location;
	}

	@Override
	public void setPath0Location(Integer path0Location) {
		this.path0Location = path0Location;
	}

	@Override
	public Integer getPath1Location() {
		return path1Location;
	}

	@Override
	public void setPath1Location(Integer path1Location) {
		this.path1Location = path1Location;
	}

	@Override
	public Integer getPath2Location() {
		return path2Location;
	}

	@Override
	public void setPath2Location(Integer path2Location) {
		this.path2Location = path2Location;
	}

	@Override
	public Integer getPath3Location() {
		return path3Location;
	}

	@Override
	public void setPath3Location(Integer path3Location) {
		this.path3Location = path3Location;
	}

	@Override
	public Integer getPath4Location() {
		return path4Location;
	}

	@Override
	public void setPath4Location(Integer path4Location) {
		this.path4Location = path4Location;
	}

	@Override
	public Integer getPath5Location() {
		return path5Location;
	}

	@Override
	public void setPath5Location(Integer path5Location) {
		this.path5Location = path5Location;
	}

	@Override
	public Integer getPath6Location() {
		return path6Location;
	}

	@Override
	public void setPath6Location(Integer path6Location) {
		this.path6Location = path6Location;
	}

	@Override
	public Integer getPath7Location() {
		return path7Location;
	}

	@Override
	public void setPath7Location(Integer path7Location) {
		this.path7Location = path7Location;
	}

	@Override
	public ItemType getItemType() {
		return itemType;
	}

	@Override
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	@Override
	public ItemPrimitiveType getItemPrimitiveType() {
		return itemPrimitiveType;
	}

	@Override
	public void setItemPrimitiveType(ItemPrimitiveType itemPrimitiveType) {
		this.itemPrimitiveType = itemPrimitiveType;
	}

	@Override
	public ItemStringValueType getItemStringValueType() {
		return itemStringValueType;
	}

	@Override
	public void setItemStringValueType(ItemStringValueType itemStringValueType) {
		this.itemStringValueType = itemStringValueType;
	}

	@Override
	public Double getNumberValue() {
		return numberValue;
	}

	@Override
	public void setNumberValue(Double numberValue) {
		this.numberValue = numberValue;
	}

	@Override
	public Date getDateTimeValue() {
		return dateTimeValue;
	}

	@Override
	public void setDateTimeValue(Date dateTimeValue) {
		this.dateTimeValue = dateTimeValue;
	}

	@Override
	public Date getDateValue() {
		return dateValue;
	}

	@Override
	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	@Override
	public Date getTimeValue() {
		return timeValue;
	}

	@Override
	public void setTimeValue(Date timeValue) {
		this.timeValue = timeValue;
	}

	@Override
	public Boolean getBooleanValue() {
		return booleanValue;
	}

	@Override
	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	@Override
	public ItemCategory getItemCategory() {
		return itemCategory;
	}

	@Override
	public void setItemCategory(ItemCategory itemCategory) {
		this.itemCategory = itemCategory;
	}

	@Override
	public String getBundle() {
		return bundle;
	}

	@Override
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	@Override
	public String getStringShortValue() {
		return stringShortValue;
	}

	@Override
	public void setStringShortValue(String stringShortValue) {
		this.stringShortValue = stringShortValue;
	}

	@Override
	public String getStringLongValue() {
		return stringLongValue;
	}

	@Override
	public void setStringLongValue(String stringLongValue) {
		this.stringLongValue = stringLongValue;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

}
