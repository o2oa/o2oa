package com.x.query.assemble.designer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class DynamicEntity extends GsonPropertyObject {

	public static final String TABLE_PREFIX = "QRY_DYN_";
	public static final String CLASS_PACKAGE = "com.x.query.dynamic.entity";
	public static final String FIELDNAME_SUFFIX = "_FIELDNAME";

	private String name;

	private List<StringField> stringFields;
	private List<IntegerField> integerFields;
	private List<LongField> longFields;
	private List<DoubleField> doubleFields;
	private List<BooleanField> booleanFields;
	private List<DateField> dateFields;
	private List<TimeField> timeFields;
	private List<DateTimeField> dateTimeFields;

	private List<StringField> listStringFields;
	private List<IntegerField> listIntegerFields;
	private List<LongField> listLongFields;
	private List<DoubleField> listDoubleFields;
	private List<BooleanField> listBooleanFields;
	private List<DateField> listDateFields;
	private List<TimeField> listTimeFields;
	private List<DateTimeField> listDateTimeFields;

	private List<StringLobField> stringLobFields;
	private List<StringMapField> stringMapFields;

	public void addStringField(String name, String description) {
		StringField field = new StringField();
		field.setName(name);
		field.setDescription(description);
		this.stringFields.add(field);
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
		return StringUtils.capitalize(name);
	}

	public String className() throws Exception {
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name is empty.");
		}
		return CLASS_PACKAGE + StringUtils.lowerCase(name);
	}

	public DynamicEntity() {
		this.stringFields = new ArrayList<>();
		this.integerFields = new ArrayList<>();
		this.longFields = new ArrayList<>();
		this.doubleFields = new ArrayList<>();
		this.booleanFields = new ArrayList<>();
		this.dateFields = new ArrayList<>();
		this.timeFields = new ArrayList<>();
		this.dateTimeFields = new ArrayList<>();

		this.listStringFields = new ArrayList<>();
		this.listIntegerFields = new ArrayList<>();
		this.listLongFields = new ArrayList<>();
		this.listDoubleFields = new ArrayList<>();
		this.listBooleanFields = new ArrayList<>();
		this.listDateFields = new ArrayList<>();
		this.listTimeFields = new ArrayList<>();
		this.listDateTimeFields = new ArrayList<>();

		this.stringLobFields = new ArrayList<>();
		this.stringMapFields = new ArrayList<>();
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

	public List<StringField> getStringFields() {
		return stringFields;
	}

	public void setStringFields(List<StringField> stringFields) {
		this.stringFields = stringFields;
	}

	public List<IntegerField> getIntegerFields() {
		return integerFields;
	}

	public void setIntegerFields(List<IntegerField> integerFields) {
		this.integerFields = integerFields;
	}

	public List<LongField> getLongFields() {
		return longFields;
	}

	public void setLongFields(List<LongField> longFields) {
		this.longFields = longFields;
	}

	public List<DoubleField> getDoubleFields() {
		return doubleFields;
	}

	public void setDoubleFields(List<DoubleField> doubleFields) {
		this.doubleFields = doubleFields;
	}

	public List<BooleanField> getBooleanFields() {
		return booleanFields;
	}

	public void setBooleanFields(List<BooleanField> booleanFields) {
		this.booleanFields = booleanFields;
	}

	public List<DateField> getDateFields() {
		return dateFields;
	}

	public void setDateFields(List<DateField> dateFields) {
		this.dateFields = dateFields;
	}

	public List<TimeField> getTimeFields() {
		return timeFields;
	}

	public void setTimeFields(List<TimeField> timeFields) {
		this.timeFields = timeFields;
	}

	public List<DateTimeField> getDateTimeFields() {
		return dateTimeFields;
	}

	public void setDateTimeFields(List<DateTimeField> dateTimeFields) {
		this.dateTimeFields = dateTimeFields;
	}

	public List<StringField> getListStringFields() {
		return listStringFields;
	}

	public void setListStringFields(List<StringField> listStringFields) {
		this.listStringFields = listStringFields;
	}

	public List<IntegerField> getListIntegerFields() {
		return listIntegerFields;
	}

	public void setListIntegerFields(List<IntegerField> listIntegerFields) {
		this.listIntegerFields = listIntegerFields;
	}

	public List<LongField> getListLongFields() {
		return listLongFields;
	}

	public void setListLongFields(List<LongField> listLongFields) {
		this.listLongFields = listLongFields;
	}

	public List<DoubleField> getListDoubleFields() {
		return listDoubleFields;
	}

	public void setListDoubleFields(List<DoubleField> listDoubleFields) {
		this.listDoubleFields = listDoubleFields;
	}

	public List<BooleanField> getListBooleanFields() {
		return listBooleanFields;
	}

	public void setListBooleanFields(List<BooleanField> listBooleanFields) {
		this.listBooleanFields = listBooleanFields;
	}

	public List<DateField> getListDateFields() {
		return listDateFields;
	}

	public void setListDateFields(List<DateField> listDateFields) {
		this.listDateFields = listDateFields;
	}

	public List<TimeField> getListTimeFields() {
		return listTimeFields;
	}

	public void setListTimeFields(List<TimeField> listTimeFields) {
		this.listTimeFields = listTimeFields;
	}

	public List<DateTimeField> getListDateTimeFields() {
		return listDateTimeFields;
	}

	public void setListDateTimeFields(List<DateTimeField> listDateTimeFields) {
		this.listDateTimeFields = listDateTimeFields;
	}

	public List<StringLobField> getStringLobFields() {
		return stringLobFields;
	}

	public void setStringLobFields(List<StringLobField> stringLobFields) {
		this.stringLobFields = stringLobFields;
	}

	public List<StringMapField> getStringMapFields() {
		return stringMapFields;
	}

	public void setStringMapFields(List<StringMapField> stringMapFields) {
		this.stringMapFields = stringMapFields;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
