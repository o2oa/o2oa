package com.x.query.service.processing.jaxrs.table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.persistence.Column;

import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.project.jaxrs.WrapString;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.persistence.jdbc.ElementColumn;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;

abstract class BaseAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

	private PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();

	protected <T extends JpaObject> List<T> update(JsonElement jsonElement, Class<T> cls, String bundle)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException,
			IllegalArgumentException, SecurityException {
		List<T> list = new ArrayList<>();
		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(cls, Column.class);
		List<Field> fields2 = FieldUtils.getFieldsListWithAnnotation(cls, ElementColumn.class);
		fields.addAll(fields2);
		final List<JsonObject> jsonObjectList = new ArrayList<>();
		if (jsonElement.isJsonObject()) {
			jsonObjectList.add(jsonElement.getAsJsonObject());
		}else if(jsonElement.isJsonArray()){
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			jsonArray.forEach(je -> {
				if (je.isJsonObject()) {
					jsonObjectList.add(je.getAsJsonObject());
				}
			});
		}
		for(JsonObject jsonObject : jsonObjectList) {
			T t = cls.getConstructor().newInstance();
			for (Field field : fields) {
				try {
					switch (JpaObjectTools.type(field)) {
						case JpaObject.TYPE_STRING:
							propertyUtilsBean.setProperty(t, field.getName(), extractStringOrDistinguishedName(
									getJsonElement(jsonObject, field), getColumnDefinedStringLength(field)));
							break;
						case JpaObject.TYPE_STRINGLOB:
							propertyUtilsBean.setProperty(t, field.getName(), extractString(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_INTEGER:
							propertyUtilsBean.setProperty(t, field.getName(), extractInteger(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_LONG:
							propertyUtilsBean.setProperty(t, field.getName(), extractLong(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_FLOAT:
							propertyUtilsBean.setProperty(t, field.getName(), extractFloat(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_DOUBLE:
							propertyUtilsBean.setProperty(t, field.getName(), extractDouble(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_DATETIME:
							propertyUtilsBean.setProperty(t, field.getName(), extractDateTime(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_DATE:
							propertyUtilsBean.setProperty(t, field.getName(), extractDate(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_BOOLEAN:
							propertyUtilsBean.setProperty(t, field.getName(), extractBoolean(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_STRINGLIST:
							propertyUtilsBean.setProperty(t, field.getName(), extractStringOrDistinguishedNameList(
									getJsonElement(jsonObject, field), getElementColumnDefinedStringLength(field)));
							break;
						case JpaObject.TYPE_INTEGERLIST:
							propertyUtilsBean.setProperty(t, field.getName(),
									extractIntegerList(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_LONGLIST:
							propertyUtilsBean.setProperty(t, field.getName(), extractLongList(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_FLOATLIST:
							propertyUtilsBean.setProperty(t, field.getName(), extractFloatList(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_DOUBLELIST:
							propertyUtilsBean.setProperty(t, field.getName(), extractDoubleList(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_DATETIMELIST:
							propertyUtilsBean.setProperty(t, field.getName(),
									extractDateTimeList(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_BOOLEANLIST:
							propertyUtilsBean.setProperty(t, field.getName(),
									extractBooleanList(getJsonElement(jsonObject, field)));
							break;
						case JpaObject.TYPE_STRINGMAP:
							propertyUtilsBean.setProperty(t, field.getName(),
									extractMap(getJsonElement(jsonObject, field), getElementColumnDefinedStringLength(field)));
							break;
						default:
							break;
					}
					if(StringUtils.isNotBlank(bundle) && field.getName().equals(DynamicEntity.BUNDLE_FIELD)){
						propertyUtilsBean.setProperty(t, field.getName(), bundle);
					}
				} catch (Exception e) {
					LOGGER.warn("设置对象:{} 的属性:{} 值异常：{}", cls.getSimpleName(), field.getName(), e.getMessage());
					e.printStackTrace();
				}
			}
			list.add(t);
		}
		return list;
	}

	private JsonElement getJsonElement(JsonObject jsonObject, Field field) {
		JsonElement element = null;
		if ((null != jsonObject) && jsonObject.has(field.getName())) {
			element = jsonObject.get(field.getName());
		}
		return element;
	}

	private int getColumnDefinedStringLength(Field field) {
		Column column = field.getAnnotation(Column.class);
		return (null != column) ? column.length() : JpaObject.length_255B;
	}

	private int getElementColumnDefinedStringLength(Field field) {
		ElementColumn elementColumn = field.getAnnotation(ElementColumn.class);
		return (null != elementColumn) ? elementColumn.length() : JpaObject.length_255B;
	}

	private String extractString(JsonElement jsonElement) {
		String value = null;
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				value = jsonElement.getAsString();
			}else{
				value = jsonElement.toString();
			}
		}
		return value;
	}

	private String extractStringOrDistinguishedName(JsonElement jsonElement, int maxLength) {
		String value = null;
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				value = jsonElement.getAsString();
			} else if (jsonElement.isJsonObject()) {
				JsonObject o = jsonElement.getAsJsonObject();
				if (o.has(JpaObject.DISTINGUISHEDNAME)) {
					value = o.get(JpaObject.DISTINGUISHEDNAME).getAsString();
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				if (os.size() > 0) {
					value = extractStringOrDistinguishedName(os.get(0), maxLength);
				}
			}
		}
		return StringTools.utf8SubString(value, maxLength);
	}

	private List<String> extractStringOrDistinguishedNameList(JsonElement jsonElement, int maxLength) {
		List<String> values = new ArrayList<>();
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				values.add(StringTools.utf8SubString(jsonElement.getAsString(), maxLength));
			} else if (jsonElement.isJsonObject()) {
				JsonObject o = jsonElement.getAsJsonObject();
				if (o.has(JpaObject.DISTINGUISHEDNAME)) {
					values.add(StringTools.utf8SubString(o.get(JpaObject.DISTINGUISHEDNAME).getAsString(), maxLength));
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				for (JsonElement o : os) {
					values.add(extractStringOrDistinguishedName(o, maxLength));
				}
			}
		}
		LOGGER.info(gson.toJson(values)+jsonElement.toString());
		return values;
	}

	private Integer extractInteger(JsonElement jsonElement) {
		Integer value = null;
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					value = jsonPrimitive.getAsInt();
				}else{
					try {
						value = Integer.valueOf(jsonPrimitive.getAsString());
					} catch (NumberFormatException e) {
						LOGGER.debug(e.getMessage());
					}
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				if (os.size() > 0) {
					value = extractInteger(os.get(0));
				}
			}
		}
		return value;
	}

	private List<Integer> extractIntegerList(JsonElement jsonElement) {
		List<Integer> values = new ArrayList<>();
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					values.add(jsonPrimitive.getAsInt());
				}else {
					try {
						values.add(Integer.valueOf(jsonPrimitive.getAsString()));
					} catch (NumberFormatException e) {
						LOGGER.debug(e.getMessage());
					}
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				for (JsonElement o : os) {
					values.add(extractInteger(o));
				}
			}
		}
		return values;
	}

	private Long extractLong(JsonElement jsonElement) {
		Long value = null;
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					value = jsonPrimitive.getAsLong();
				}else{
					try {
						value = Long.valueOf(jsonPrimitive.getAsString());
					} catch (NumberFormatException e) {
						LOGGER.debug(e.getMessage());
					}
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				if (os.size() > 0) {
					value = extractLong(os.get(0));
				}
			}
		}
		return value;
	}

	private List<Long> extractLongList(JsonElement jsonElement) {
		List<Long> values = new ArrayList<>();
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					values.add(jsonPrimitive.getAsLong());
				}else{
					try {
						values.add(Long.valueOf(jsonPrimitive.getAsString()));
					} catch (NumberFormatException e) {
						LOGGER.debug(e.getMessage());
					}
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				for (JsonElement o : os) {
					values.add(extractLong(o));
				}
			}
		}
		return values;
	}

	private Float extractFloat(JsonElement jsonElement) {
		Float value = null;
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					value = jsonPrimitive.getAsFloat();
				}else{
					try {
						value = Float.valueOf(jsonPrimitive.getAsString());
					} catch (NumberFormatException e) {
						LOGGER.debug(e.getMessage());
					}
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				if (os.size() > 0) {
					value = extractFloat(os.get(0));
				}
			}
		}
		return value;
	}

	private List<Float> extractFloatList(JsonElement jsonElement) {
		List<Float> values = new ArrayList<>();
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					values.add(jsonPrimitive.getAsFloat());
				}else{
					try {
						values.add(Float.valueOf(jsonPrimitive.getAsString()));
					} catch (NumberFormatException e) {
						LOGGER.debug(e.getMessage());
					}
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				for (JsonElement o : os) {
					values.add(extractFloat(o));
				}
			}
		}
		return values;
	}

	private Double extractDouble(JsonElement jsonElement) {
		Double value = null;
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					value = jsonPrimitive.getAsDouble();
				}else{
					try {
						value = Double.valueOf(jsonPrimitive.getAsString());
					} catch (NumberFormatException e) {
						LOGGER.debug(e.getMessage());
					}
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				if (os.size() > 0) {
					value = extractDouble(os.get(0));
				}
			}
		}
		return value;
	}

	private List<Double> extractDoubleList(JsonElement jsonElement) {
		List<Double> values = new ArrayList<>();
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					values.add(jsonPrimitive.getAsDouble());
				}else{
					try {
						values.add(Double.valueOf(jsonPrimitive.getAsString()));
					} catch (NumberFormatException e) {
						LOGGER.debug(e.getMessage());
					}
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				for (JsonElement o : os) {
					values.add(extractDouble(o));
				}
			}
		}
		return values;
	}

	private Date extractDateTime(JsonElement jsonElement) {
		Date value = null;
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isString()) {
					try {
						value = DateTools.parseDateTime(jsonPrimitive.getAsString());
					} catch (Exception e) {
						LOGGER.error(e);
					}
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				if (os.size() > 0) {
					value = extractDateTime(os.get(0));
				}
			}
		}
		return value;
	}

	private List<Date> extractDateTimeList(JsonElement jsonElement) {
		List<Date> values = new ArrayList<>();
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isString()) {
					try {
						values.add(DateTools.parseDateTime(jsonPrimitive.getAsString()));
					} catch (Exception e) {
						LOGGER.error(e);
					}
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				for (JsonElement o : os) {
					values.add(extractDateTime(o));
				}
			}
		}
		return values;
	}

	private Date extractDate(JsonElement jsonElement) {
		Date value = null;
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isString()) {
					try {
						value = DateTools.parseDate(jsonPrimitive.getAsString());
					} catch (Exception e) {
						LOGGER.error(e);
					}
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				if (os.size() > 0) {
					value = extractDate(os.get(0));
				}
			}
		}
		return value;
	}

	private Boolean extractBoolean(JsonElement jsonElement) {
		Boolean value = null;
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isBoolean()) {
					value = jsonPrimitive.getAsBoolean();
				}else if (jsonPrimitive.isNumber()) {
					value = jsonPrimitive.getAsBoolean();
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				if (os.size() > 0) {
					value = extractBoolean(os.get(0));
				}
			}
		}
		return value;
	}

	private List<Boolean> extractBooleanList(JsonElement jsonElement) {
		List<Boolean> values = new ArrayList<>();
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					values.add(jsonPrimitive.getAsBoolean());
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				for (JsonElement o : os) {
					values.add(extractBoolean(o));
				}
			}
		}
		return values;
	}

	private Map<String, String> extractMap(JsonElement jsonElement, int maxLength) {
		Map<String, Object> dataTable = new LinkedHashMap<>();
		Map<String, String> map = new LinkedHashMap<>();
		if (null != jsonElement) {
			if (jsonElement.isJsonObject()) {
				dataTable = gson.fromJson(jsonElement, LinkedHashMap.class);
			}
		}
		for(String key : dataTable.keySet()){
			map.put(key, StringTools.utf8SubString(Objects.toString(dataTable.get(key), ""), maxLength));
		}
		return map;
	}

}
