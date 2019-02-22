package com.x.base.core.entity.dynamic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.gson.GsonPropertyObject;

public class DynamicEntity extends GsonPropertyObject {

	public static final String TABLE_PREFIX = "QRY_DYN_";
	public static final String CLASS_PACKAGE = "com.x.query.dynamic.entity";
	public static final String FIELDNAME_SUFFIX = "_FIELDNAME";
	public static final String JAR_NAME = "x_query_dynamic_entity";

	private String name;

	private List<StringField> stringFieldList;
	private List<IntegerField> integerFieldList;
	private List<LongField> longFieldList;
	private List<DoubleField> doubleFieldList;
	private List<BooleanField> booleanFieldList;
	private List<DateField> dateFieldList;
	private List<TimeField> timeFieldList;
	private List<DateTimeField> dateTimeFieldList;

	private List<StringField> listStringFieldList;
	private List<IntegerField> listIntegerFieldList;
	private List<LongField> listLongFieldList;
	private List<DoubleField> listDoubleFieldList;
	private List<BooleanField> listBooleanFieldList;
	private List<DateTimeField> listDateTimeFieldList;

	private List<StringLobField> stringLobFieldList;
	private List<StringMapField> stringMapFieldList;

	public void addStringField(String name, String description) {
		StringField field = new StringField();
		field.setName(name);
		field.setDescription(description);
		this.stringFieldList.add(field);
	}

	public String tableName() throws Exception {
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name is empty.");
		}
		return TABLE_PREFIX + StringUtils.upperCase(name);
	}

	public String classSimpleName() throws Exception {
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name is empty.");
		}
		return name;
	}

	public String className() throws Exception {
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name is empty.");
		}
		return CLASS_PACKAGE + "." + name;
	}

	@SuppressWarnings("unchecked")
	public Class<? extends JpaObject> getObjectClass() throws Exception {
		return (Class<? extends JpaObject>) Class.forName(this.className());
	}

	public DynamicEntity() {
		this.stringFieldList = new ArrayList<>();
		this.integerFieldList = new ArrayList<>();
		this.longFieldList = new ArrayList<>();
		this.doubleFieldList = new ArrayList<>();
		this.booleanFieldList = new ArrayList<>();
		this.dateFieldList = new ArrayList<>();
		this.timeFieldList = new ArrayList<>();
		this.dateTimeFieldList = new ArrayList<>();

		this.listStringFieldList = new ArrayList<>();
		this.listIntegerFieldList = new ArrayList<>();
		this.listLongFieldList = new ArrayList<>();
		this.listDoubleFieldList = new ArrayList<>();
		this.listBooleanFieldList = new ArrayList<>();
		this.listDateTimeFieldList = new ArrayList<>();

		this.stringLobFieldList = new ArrayList<>();
		this.stringMapFieldList = new ArrayList<>();
	}

	public DynamicEntity(String name) {
		this();
		this.name = name;
	}

	public static abstract class Field {
		private String name;
		private String description;

		public String fieldName() {
			return this.name + FIELDNAME_SUFFIX;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

	public static class StringField extends Field {
	}

	public static class IntegerField extends Field {
	}

	public static class LongField extends Field {
	}

	public static class DoubleField extends Field {
	}

	public static class BooleanField extends Field {
	}

	public static class DateField extends Field {
	}

	public static class TimeField extends Field {
	}

	public static class DateTimeField extends Field {
	}

	public static class StringLobField extends Field {
	}

	public static class StringMapField extends Field {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<StringField> getStringFieldList() {
		return stringFieldList;
	}

	public void setStringFieldList(List<StringField> stringFieldList) {
		this.stringFieldList = stringFieldList;
	}

	public List<IntegerField> getIntegerFieldList() {
		return integerFieldList;
	}

	public void setIntegerFieldList(List<IntegerField> integerFieldList) {
		this.integerFieldList = integerFieldList;
	}

	public List<LongField> getLongFieldList() {
		return longFieldList;
	}

	public void setLongFieldList(List<LongField> longFieldList) {
		this.longFieldList = longFieldList;
	}

	public List<DoubleField> getDoubleFieldList() {
		return doubleFieldList;
	}

	public void setDoubleFieldList(List<DoubleField> doubleFieldList) {
		this.doubleFieldList = doubleFieldList;
	}

	public List<BooleanField> getBooleanFieldList() {
		return booleanFieldList;
	}

	public void setBooleanFieldList(List<BooleanField> booleanFieldList) {
		this.booleanFieldList = booleanFieldList;
	}

	public List<DateField> getDateFieldList() {
		return dateFieldList;
	}

	public void setDateFieldList(List<DateField> dateFieldList) {
		this.dateFieldList = dateFieldList;
	}

	public List<TimeField> getTimeFieldList() {
		return timeFieldList;
	}

	public void setTimeFieldList(List<TimeField> timeFieldList) {
		this.timeFieldList = timeFieldList;
	}

	public List<DateTimeField> getDateTimeFieldList() {
		return dateTimeFieldList;
	}

	public void setDateTimeFieldList(List<DateTimeField> dateTimeFieldList) {
		this.dateTimeFieldList = dateTimeFieldList;
	}

	public List<StringField> getListStringFieldList() {
		return listStringFieldList;
	}

	public void setListStringFieldList(List<StringField> listStringFieldList) {
		this.listStringFieldList = listStringFieldList;
	}

	public List<IntegerField> getListIntegerFieldList() {
		return listIntegerFieldList;
	}

	public void setListIntegerFieldList(List<IntegerField> listIntegerFieldList) {
		this.listIntegerFieldList = listIntegerFieldList;
	}

	public List<LongField> getListLongFieldList() {
		return listLongFieldList;
	}

	public void setListLongFieldList(List<LongField> listLongFieldList) {
		this.listLongFieldList = listLongFieldList;
	}

	public List<DoubleField> getListDoubleFieldList() {
		return listDoubleFieldList;
	}

	public void setListDoubleFieldList(List<DoubleField> listDoubleFieldList) {
		this.listDoubleFieldList = listDoubleFieldList;
	}

	public List<BooleanField> getListBooleanFieldList() {
		return listBooleanFieldList;
	}

	public void setListBooleanFieldList(List<BooleanField> listBooleanFieldList) {
		this.listBooleanFieldList = listBooleanFieldList;
	}

	public List<DateTimeField> getListDateTimeFieldList() {
		return listDateTimeFieldList;
	}

	public void setListDateTimeFieldList(List<DateTimeField> listDateTimeFieldList) {
		this.listDateTimeFieldList = listDateTimeFieldList;
	}

	public List<StringLobField> getStringLobFieldList() {
		return stringLobFieldList;
	}

	public void setStringLobFieldList(List<StringLobField> stringLobFieldList) {
		this.stringLobFieldList = stringLobFieldList;
	}

	public List<StringMapField> getStringMapFieldList() {
		return stringMapFieldList;
	}

	public void setStringMapFieldList(List<StringMapField> stringMapFieldList) {
		this.stringMapFieldList = stringMapFieldList;
	}

}
