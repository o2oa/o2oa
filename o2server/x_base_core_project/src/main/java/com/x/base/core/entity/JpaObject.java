package com.x.base.core.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.RestrictFlag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

@MappedSuperclass
public abstract class JpaObject extends GsonPropertyObject implements Serializable {

	private static final long serialVersionUID = 2809501197843500002L;

	public static String createId() {
		return UUID.randomUUID().toString();
	}

	abstract public void onPersist() throws Exception;

	public static final String default_schema = "X";

	public static final String ColumnNamePrefix = "x";

	public static final String IndexNameMiddle = "_";

	public static final String JoinIndexNameSuffix = "_join";

	public static final String KeyIndexNameSuffix = "_key";

	public static final String ElementIndexNameSuffix = "_element";

	public static final String ContainerTableNameMiddle = "_";

	public static final String DefaultUniqueConstraintSuffix = "DUC";

	public abstract String getId();

	public abstract void setId(String id);

	public static final String id_FIELDNAME = "id";

	public static final String key_FIELDNAME = "key";

	public static final String createTime_FIELDNAME = "createTime";

	public static final String updateTime_FIELDNAME = "updateTime";

	public static final String sequence_FIELDNAME = "sequence";

	public static final String distributeFactor_FIELDNAME = "distributeFactor";

	public static final String password_FIELDNAME = "password";

	public static final String scratchString_FIELDNAME = "scratchString";

	public static final String scratchBoolean_FIELDNAME = "scratchBoolean";

	public static final String scratchDate_FIELDNAME = "scratchDate";

	public static final String scratchInteger_FIELDNAME = "scratchInteger";

	public static final String PROPERTIES_FIELDNAME = "properties";

	public static final String JsonPropertiesValueHandler = "com.x.base.core.entity.annotation.JsonPropertiesValueHandler";

	public static final List<String> FieldsUnmodify = ListUtils.unmodifiableList(Arrays.asList(id_FIELDNAME,
			distributeFactor_FIELDNAME, createTime_FIELDNAME, updateTime_FIELDNAME, sequence_FIELDNAME,
			scratchString_FIELDNAME, scratchBoolean_FIELDNAME, scratchDate_FIELDNAME, scratchInteger_FIELDNAME));

	public static final List<String> FieldsUnmodifyIncludePorperties = ListUtils
			.unmodifiableList(Arrays.asList(id_FIELDNAME, distributeFactor_FIELDNAME, createTime_FIELDNAME,
					updateTime_FIELDNAME, sequence_FIELDNAME, scratchString_FIELDNAME, scratchBoolean_FIELDNAME,
					scratchDate_FIELDNAME, scratchInteger_FIELDNAME, PROPERTIES_FIELDNAME));

	public static final List<String> FieldsUnmodifyExcludeId = ListUtils.unmodifiableList(Arrays.asList(
			distributeFactor_FIELDNAME, createTime_FIELDNAME, updateTime_FIELDNAME, sequence_FIELDNAME,
			scratchString_FIELDNAME, scratchBoolean_FIELDNAME, scratchDate_FIELDNAME, scratchInteger_FIELDNAME));

	public static final List<String> FieldsUnmodifyIncludePorpertiesExcludeId = ListUtils
			.unmodifiableList(Arrays.asList(distributeFactor_FIELDNAME, createTime_FIELDNAME, updateTime_FIELDNAME,
					sequence_FIELDNAME, scratchString_FIELDNAME, scratchBoolean_FIELDNAME, scratchDate_FIELDNAME,
					scratchInteger_FIELDNAME, PROPERTIES_FIELDNAME));

	public static final List<String> FieldsInvisible = ListUtils.unmodifiableList(
			Arrays.asList(distributeFactor_FIELDNAME, sequence_FIELDNAME, password_FIELDNAME, scratchString_FIELDNAME,
					scratchBoolean_FIELDNAME, scratchDate_FIELDNAME, scratchInteger_FIELDNAME));

	public static final List<String> FieldsInvisibleIncludeProperites = ListUtils.unmodifiableList(
			Arrays.asList(distributeFactor_FIELDNAME, sequence_FIELDNAME, password_FIELDNAME, scratchString_FIELDNAME,
					scratchBoolean_FIELDNAME, scratchDate_FIELDNAME, scratchInteger_FIELDNAME, PROPERTIES_FIELDNAME));

	public static final List<String> FieldsDefault = ListUtils
			.unmodifiableList(Arrays.asList(id_FIELDNAME, key_FIELDNAME, createTime_FIELDNAME, updateTime_FIELDNAME,
					sequence_FIELDNAME, distributeFactor_FIELDNAME, password_FIELDNAME, scratchString_FIELDNAME,
					scratchBoolean_FIELDNAME, scratchDate_FIELDNAME, scratchInteger_FIELDNAME));

	@FieldDescribe("创建时间,自动生成,索引创建在约束中.")
	@Column(name = ColumnNamePrefix + createTime_FIELDNAME)
	private Date createTime;

	@FieldDescribe("修改时间,自动生成,索引创建在约束中.")
	@Column(name = ColumnNamePrefix + updateTime_FIELDNAME)
	private Date updateTime;

	@FieldDescribe("列表序号,由创建时间以及ID组成.在保存时自动生成,索引创建在约束中.")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + sequence_FIELDNAME)
	private String sequence;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public static final int length_1B = 1;

	public static final int length_2B = 2;

	public static final int length_4B = 4;

	public static final int length_8B = 8;

	public static final int length_16B = 16;

	public static final int length_32B = 32;

	public static final int length_64B = 64;

	public static final int length_96B = 96;

	public static final int length_128B = 128;

	public static final int length_255B = 255;

	public static final int length_1K = 1024;

	public static final int length_2K = 1024 * 2;

	public static final int length_4K = 1024 * 4;

	public static final int length_8K = 1024 * 8;

	public static final int length_16K = 1024 * 16;

	public static final int length_32K = 1024 * 32;

	public static final int length_64K = 1024 * 64;

	public static final int length_128K = 1024 * 128;

	public static final int length_1M = 1048576;

	public static final int length_10M = 1048576 * 10;

	public static final int length_20M = 1048576 * 20;

	public static final int length_50M = 1048576 * 50;

	public static final int length_100M = length_10M * 10;

	public static final int length_1G = 1073741824;

	public static final int length_id = length_64B;

	public static final String IDCOLUMN = "xid";
	public static final String CREATETIMECOLUMN = "xcreateTime";
	public static final String UPDATETIMECOLUMN = "xupdateTime";
	public static final String SEQUENCECOLUMN = "xsequence";
	public static final String ORDERCOLUMNCOLUMN = "xorderColumn";

	public static final String DISTINGUISHEDNAME = "distinguishedName";

	public static final String TYPE_STRING = "string";
	public static final String TYPE_INTEGER = "integer";
	public static final String TYPE_LONG = "long";
	public static final String TYPE_FLOAT = "float";
	public static final String TYPE_DOUBLE = "double";
	public static final String TYPE_DATETIME = "dateTime";
	public static final String TYPE_DATE = "date";
	public static final String TYPE_TIME = "time";
	public static final String TYPE_BOOLEAN = "boolean";
	public static final String TYPE_JSONPROPERTIES = "jsonProperties";
	public static final String TYPE_BYTEARRAY = "byteArray";

	public static final String TYPE_STRINGLIST = "stringList";
	public static final String TYPE_INTEGERLIST = "integerList";
	public static final String TYPE_LONGLIST = "longList";
	public static final String TYPE_FLOATLIST = "floatList";
	public static final String TYPE_DOUBLELIST = "doubleList";
	public static final String TYPE_DATETIMELIST = "dateTimeList";
	public static final String TYPE_BOOLEANLIST = "booleanList";

	public static final String TYPE_STRINGLOB = "stringLob";

	public static final String TYPE_STRINGMAP = "stringMap";
	public static final String TYPE_INTEGERMAP = "integerMap";
	public static final String TYPE_LONGMAP = "longMap";
	public static final String TYPE_FLOATMAP = "floatMap";
	public static final String TYPE_DOUBLEMAP = "doubleMap";
	public static final String TYPE_DATETIMEMAP = "dateTimeMap";
	public static final String TYPE_BOOLEANMAP = "booleanMap";

	public static final String[] ID_DISTRIBUTEFACTOR = new String[] { id_FIELDNAME, distributeFactor_FIELDNAME };

	public List<String> flagValues() throws Exception {
		List<String> values = new ArrayList<>();
		values.add(this.getId());
		for (Field f : FieldUtils.getFieldsListWithAnnotation(this.getClass(), Flag.class)) {
			values.add(PropertyUtils.getProperty(this, f.getName()).toString());
		}
		return ListTools.trim(values, true, true);
	}

	public List<String> restrictFlagValues() throws Exception {
		List<String> values = this.flagValues();
		for (Field f : FieldUtils.getFieldsListWithAnnotation(this.getClass(), RestrictFlag.class)) {
			values.add(PropertyUtils.getProperty(this, f.getName()).toString());
		}
		return ListTools.trim(values, true, true);
	}

	public int hashCode() {
		return 31 + ((this.getId() == null) ? 0 : this.getId().hashCode());
	}

	public boolean equals(Object o) {
		if (null == o) {
			return false;
		}
		if (!(o instanceof JpaObject)) {
			return false;
		} else {
			return this.getId().equalsIgnoreCase(((JpaObject) o).getId());
		}
	}

	public static <T extends JpaObject> List<String> singularAttributeField(Class<T> clz, Boolean excludeInvisible,
			Boolean excludeLob) {
		List<String> names = new ArrayList<>();
		for (Field field : FieldUtils.getFieldsListWithAnnotation(clz, Column.class)) {
			singularAttributeField(field, excludeInvisible, excludeLob, names);
		}
		if (names.contains(JpaObject.PROPERTIES_FIELDNAME)) {
			names = Stream.concat(
					names.stream().filter(o -> !StringUtils.equalsIgnoreCase(o, JpaObject.PROPERTIES_FIELDNAME)),
					Stream.<String>of(JpaObject.PROPERTIES_FIELDNAME)).collect(Collectors.toList());
		}
		return names;
	}

	private static void singularAttributeField(Field field, Boolean excludeInvisible, Boolean excludeLob,
			List<String> names) {
		if (null == field.getAnnotation(ContainerTable.class)) {
			if (BooleanUtils.isTrue(excludeInvisible) && FieldsInvisible.contains(field.getName())) {
				return;
			}
			if (BooleanUtils.isTrue(excludeLob)) {
				if (null != field.getAnnotation(Lob.class)) {
					return;
				} else {
					Strategy strategy = field.getAnnotation(Strategy.class);
					if ((null != strategy) && StringUtils.equals(JsonPropertiesValueHandler, strategy.value())) {
						return;
					}
				}
			}
			names.add(field.getName());
		}
	}

	public String nameOfEntity() {
		String name = this.getClass().getSimpleName();
		return name;
	}

	@PrePersist
	public void prePersist() throws Exception {
		if (StringUtils.isEmpty(this.getId())) {
			throw new Exception("basePrePersist error, id is empty, entity class:" + this.getClass().getName()
					+ ", entity content:" + XGsonBuilder.toJson(this) + ".");
		}
		Date date = new Date();
		if (null == this.getCreateTime()) {
			this.setCreateTime(date);
		}
		this.setUpdateTime(date);
		if (StringUtils.isEmpty(this.getSequence())) {
			this.setSequence(StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId()));
		}
		this.onPersist();
	}

	@PreUpdate
	public void preUpdate() throws Exception {
		if (StringUtils.isEmpty(this.getId())) {
			throw new Exception("basePreUpdate error, id is empty, entity class:" + this.getClass().getName()
					+ ", entity content:" + XGsonBuilder.toJson(this) + ".");
		}
		this.setUpdateTime(new Date());
		if (StringUtils.isEmpty(this.getSequence())) {
			this.setSequence(StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId()));
		}
		this.onPersist();
	}

	public static <T extends JpaObject> T cast(Class<T> cls, List<String> fields, Object[] objects) throws Exception {
		T t = cls.getDeclaredConstructor().newInstance();
		for (int i = 0; i < fields.size(); i++) {
			PropertyUtils.setProperty(t, fields.get(i), objects[i]);
		}
		return t;
	}

//	public abstract static class EntityProperties {
//	}

}