package com.x.query.service.processing.jaxrs.table;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.processplatform.core.entity.content.Data;
import com.x.query.core.entity.schema.Table;
import com.x.query.service.processing.Business;

class ActionUpdate extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdate.class);

	private PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, String bundle, JsonElement jsonElement)
			throws Exception {
		ClassLoader classLoader = Business.getDynamicEntityClassLoader();
		Thread.currentThread().setContextClassLoader(classLoader);
		ActionResult<Wo> result = new ActionResult<>();

		if (StringUtils.isEmpty(bundle)) {
			throw new ExceptionBundleEmpty();
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);
			Table table = emc.flag(flag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(flag, Table.class);
			}
			DynamicEntity dynamicEntity = new DynamicEntity(table.getName());
			@SuppressWarnings("unchecked")
			Class<? extends JpaObject> cls = (Class<? extends JpaObject>) classLoader
					.loadClass(dynamicEntity.className());
			JpaObject o = update(jsonElement, cls);
			JpaObject obj = emc.find(bundle, cls);
			emc.beginTransaction(cls);
			if (null != obj) {
				o.copyTo(obj, JpaObject.FieldsUnmodify);
				emc.check(obj, CheckPersistType.all);
			} else {
				emc.persist(o);
				emc.check(o, CheckPersistType.all);
			}
			emc.commit();
			Wo wo = new Wo();
			result.setData(wo);
		}
		return result;
	}

	private <T extends JpaObject> T update(JsonElement jsonElement, Class<T> cls)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException,
			IllegalArgumentException, SecurityException {
		List<Field> fields = FieldUtils.getFieldsListWithAnnotation(cls, Column.class);
		T t = cls.getConstructor().newInstance();
		JsonObject data = null;
		JsonObject work = null;
		if ((jsonElement != null) && jsonElement.isJsonObject()) {
			data = jsonElement.getAsJsonObject();
			if (data.has(Data.WORK_PROPERTY)) {
				JsonElement jsonElementWork = data.get(Data.WORK_PROPERTY);
				if ((null != jsonElementWork) && jsonElementWork.isJsonObject()) {
					work = jsonElementWork.getAsJsonObject();
				}
			}
		}
		for (Field field : fields) {
			switch (JpaObjectTools.type(field)) {
			case JpaObject.TYPE_STRING:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractStringOrDistinguishedName(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_INTEGER:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractInteger(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_LONG:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractLong(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_FLOAT:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractFloat(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_DOUBLE:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractDouble(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_DATETIME:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractDateTime(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_DATE:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractDate(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_BOOLEAN:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractBoolean(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_STRINGLIST:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractStringOrDistinguishedNameList(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_INTEGERLIST:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractIntegerList(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_LONGLIST:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractLongList(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_FLOATLIST:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractFloatList(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_DOUBLELIST:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractDoubleList(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_DATETIMELIST:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractDateTimeList(processPlatformGetJsonElement(data, work, field)));
				break;
			case JpaObject.TYPE_BOOLEANLIST:
				propertyUtilsBean.setProperty(t, field.getName(),
						extractBooleanList(processPlatformGetJsonElement(data, work, field)));
				break;
			default:
				break;
			}
		}
		return t;
	}

	private JsonElement processPlatformGetJsonElement(JsonObject data, JsonObject work, Field field) {
		JsonElement element = null;
		if ((null != work) && work.has(field.getName())) {
			element = work.get(field.getName());
		} else if ((null != data) && data.has(field.getName())) {
			element = data.get(field.getName());
		}
		return element;
	}

	private String extractStringOrDistinguishedName(JsonElement jsonElement) {
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
					value = extractStringOrDistinguishedName(os.get(0));
				}
			}
		}
		return value;
	}

	private List<String> extractStringOrDistinguishedNameList(JsonElement jsonElement) {
		List<String> values = new ArrayList<>();
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				values.add(jsonElement.getAsString());
			} else if (jsonElement.isJsonObject()) {
				JsonObject o = jsonElement.getAsJsonObject();
				if (o.has(JpaObject.DISTINGUISHEDNAME)) {
					values.add(o.get(JpaObject.DISTINGUISHEDNAME).getAsString());
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray os = jsonElement.getAsJsonArray();
				for (JsonElement o : os) {
					values.add(extractStringOrDistinguishedName(o));
				}
			}
		}
		return values;
	}

	private Integer extractInteger(JsonElement jsonElement) {
		Integer value = null;
		if (null != jsonElement) {
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					value = jsonPrimitive.getAsInt();
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
				if (jsonPrimitive.isNumber()) {
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

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 7570720614789228246L;

	}

}