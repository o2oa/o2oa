package com.x.base.core.entity.dynamic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.gson.GsonPropertyObject;

public class DynamicEntity extends GsonPropertyObject {

	private static final long serialVersionUID = -533163575298575696L;

	public static final String TABLE_PREFIX = "QRY_DYN_";
	public static final String CLASS_PACKAGE = "com.x.query.dynamic.entity";
	public static final String FIELDNAME_SUFFIX = "_FIELDNAME";
	public static final String JAR_NAME = "x_query_dynamic_entity";
	public static final String JAR_PREFIX = "dynamic_";
	public static final String BUNDLE_FIELD = "bundle";

	private String name;

	private List<Field> fieldList = new ArrayList<>();

	public String tableName() {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalStateException("table name is empty.");
		}
		return TABLE_PREFIX + StringUtils.upperCase(name);
	}

	public String classSimpleName() {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalStateException("class simple name is empty.");
		}
		return name;
	}

	public String className() {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalStateException("class name is empty.");
		}
		return CLASS_PACKAGE + "." + name;
	}

	public DynamicEntity() {
		this.fieldList = new ArrayList<>();
	}

	public DynamicEntity(String name) {
		this();
		this.name = name;
	}

	public List<Field> stringFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_STRING))
				.collect(Collectors.toList());
	}

	public List<Field> integerFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_INTEGER))
				.collect(Collectors.toList());
	}

	public List<Field> longFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_LONG))
				.collect(Collectors.toList());
	}

	public List<Field> doubleFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_DOUBLE))
				.collect(Collectors.toList());
	}

	public List<Field> booleanFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_BOOLEAN))
				.collect(Collectors.toList());
	}

	public List<Field> dateFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_DATE))
				.collect(Collectors.toList());
	}

	public List<Field> timeFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_TIME))
				.collect(Collectors.toList());
	}

	public List<Field> dateTimeFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_DATETIME))
				.collect(Collectors.toList());
	}

	public List<Field> stringListFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_STRINGLIST))
				.collect(Collectors.toList());
	}

	public List<Field> integerListFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_INTEGERLIST))
				.collect(Collectors.toList());
	}

	public List<Field> longListFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_LONGLIST))
				.collect(Collectors.toList());
	}

	public List<Field> doubleListFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_DOUBLELIST))
				.collect(Collectors.toList());
	}

	public List<Field> booleanListFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_BOOLEANLIST))
				.collect(Collectors.toList());
	}

	public List<Field> stringLobFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_STRINGLOB))
				.collect(Collectors.toList());
	}

	public List<Field> stringMapFields() {
		return this.fieldList.stream().filter(o -> StringUtils.equals(o.getType(), JpaObject.TYPE_STRINGMAP))
				.collect(Collectors.toList());
	}

	public static class Field {
		private String name;
		private String description;
		private String type;

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

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Field> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<Field> fieldList) {
		this.fieldList = fieldList;
	}

}
